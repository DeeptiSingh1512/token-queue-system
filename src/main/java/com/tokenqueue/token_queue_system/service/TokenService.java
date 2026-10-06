package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.TokenResponse;
import com.tokenqueue.token_queue_system.dto.TokenStatusResponse;
import com.tokenqueue.token_queue_system.entity.ServiceType;
import com.tokenqueue.token_queue_system.entity.Token;
import com.tokenqueue.token_queue_system.entity.User;
import com.tokenqueue.token_queue_system.enums.TokenStatus;
import com.tokenqueue.token_queue_system.repository.ServiceTypeRepository;
import com.tokenqueue.token_queue_system.repository.TokenRepository;
import com.tokenqueue.token_queue_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    public static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final List<TokenStatus> ACTIVE_STATUSES =
            List.of(TokenStatus.WAITING, TokenStatus.CALLED, TokenStatus.IN_SERVICE);
    private static final List<TokenStatus> BEING_SERVED =
            List.of(TokenStatus.CALLED, TokenStatus.IN_SERVICE);

    private final TokenRepository tokenRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserRepository userRepository;

    @Transactional
    public TokenResponse book(String citizenEmail, Long serviceTypeId) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        // Locks this service row so two bookings can't take the same number
        ServiceType service = serviceTypeRepository.findByIdForUpdate(serviceTypeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Service not found"));

        if (!service.isActive() || !service.getOffice().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "This service is not available");
        }

        LocalDate today = LocalDate.now(ZONE);

        if (tokenRepository.existsByCitizenIdAndServiceTypeIdAndTokenDateAndStatusIn(
                citizen.getId(), serviceTypeId, today, ACTIVE_STATUSES)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "You already have an active token for this service");
        }

        int next = tokenRepository
                .findTopByServiceTypeIdAndTokenDateOrderBySequenceNumberDesc(serviceTypeId, today)
                .map(t -> t.getSequenceNumber() + 1)
                .orElse(1);

        Token token = Token.builder()
                .tokenNumber(service.getPrefix() + "-" + String.format("%03d", next))
                .sequenceNumber(next)
                .tokenDate(today)
                .citizen(citizen)
                .serviceType(service)
                .build();

        return TokenResponse.from(tokenRepository.save(token));
    }

    @Transactional(readOnly = true)
    public TokenStatusResponse getStatus(String citizenEmail, Long tokenId) {
        Token token = findOwnedToken(citizenEmail, tokenId);
        ServiceType service = token.getServiceType();

        long ahead = 0;
        Integer position = null;
        int waitMinutes = 0;

        if (token.getStatus() == TokenStatus.WAITING) {
            ahead = tokenRepository
                    .countByServiceTypeIdAndTokenDateAndStatusAndSequenceNumberLessThan(
                            service.getId(), token.getTokenDate(),
                            TokenStatus.WAITING, token.getSequenceNumber());
            position = (int) ahead + 1;
            waitMinutes = (int) ahead * service.getAvgServiceMinutes();
        }

        String nowServing = tokenRepository
                .findTopByServiceTypeIdAndTokenDateAndStatusInOrderByCalledAtDesc(
                        service.getId(), token.getTokenDate(), BEING_SERVED)
                .map(Token::getTokenNumber)
                .orElse(null);

        return new TokenStatusResponse(
                token.getId(),
                token.getTokenNumber(),
                token.getStatus(),
                service.getName(),
                service.getOffice().getName(),
                ahead,
                position,
                waitMinutes,
                nowServing);
    }

    @Transactional
    public TokenResponse cancel(String citizenEmail, Long tokenId) {
        Token token = findOwnedToken(citizenEmail, tokenId);

        if (token.getStatus() != TokenStatus.WAITING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Only a waiting token can be cancelled");
        }

        token.setStatus(TokenStatus.CANCELLED);
        return TokenResponse.from(tokenRepository.save(token));
    }

    @Transactional(readOnly = true)
    public List<TokenResponse> getMyTokens(String citizenEmail) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        return tokenRepository.findByCitizenIdOrderByCreatedAtDesc(citizen.getId())
                .stream().map(TokenResponse::from).toList();
    }

    // Another citizen's token looks the same as a missing one (404)
    private Token findOwnedToken(String citizenEmail, Long tokenId) {
        return tokenRepository.findById(tokenId)
                .filter(t -> t.getCitizen().getEmail().equals(citizenEmail))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Token not found"));
    }
}