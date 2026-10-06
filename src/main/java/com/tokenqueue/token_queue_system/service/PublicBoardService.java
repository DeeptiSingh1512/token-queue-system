package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.BoardCounterResponse;
import com.tokenqueue.token_queue_system.dto.BoardResponse;
import com.tokenqueue.token_queue_system.dto.BoardServiceResponse;
import com.tokenqueue.token_queue_system.entity.Counter;
import com.tokenqueue.token_queue_system.entity.Office;
import com.tokenqueue.token_queue_system.entity.Token;
import com.tokenqueue.token_queue_system.enums.TokenStatus;
import com.tokenqueue.token_queue_system.repository.CounterRepository;
import com.tokenqueue.token_queue_system.repository.OfficeRepository;
import com.tokenqueue.token_queue_system.repository.ServiceTypeRepository;
import com.tokenqueue.token_queue_system.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicBoardService {

    private static final List<TokenStatus> BEING_SERVED =
            List.of(TokenStatus.CALLED, TokenStatus.IN_SERVICE);

    private final OfficeRepository officeRepository;
    private final CounterRepository counterRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final TokenRepository tokenRepository;

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long officeId) {
        Office office = officeRepository.findById(officeId)
                .filter(Office::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Office not found"));

        LocalDate today = LocalDate.now(TokenService.ZONE);

        List<BoardCounterResponse> counters = counterRepository.findByOfficeId(officeId)
                .stream()
                .filter(Counter::isActive)
                .map(c -> {
                    Optional<Token> current = tokenRepository
                            .findTopByCounterIdAndTokenDateAndStatusInOrderByCalledAtDesc(
                                    c.getId(), today, BEING_SERVED);
                    return new BoardCounterResponse(
                            c.getId(),
                            c.getName(),
                            current.map(Token::getTokenNumber).orElse(null),
                            current.map(Token::getStatus).orElse(null));
                })
                .toList();

        List<BoardServiceResponse> services = serviceTypeRepository
                .findByOfficeIdAndActiveTrue(officeId)
                .stream()
                .map(s -> {
                    long waiting = tokenRepository
                            .countByServiceTypeIdAndTokenDateAndStatus(
                                    s.getId(), today, TokenStatus.WAITING);
                    return new BoardServiceResponse(
                            s.getId(),
                            s.getName(),
                            s.getPrefix(),
                            waiting,
                            (int) waiting * s.getAvgServiceMinutes());
                })
                .toList();

        return new BoardResponse(
                office.getId(),
                office.getName(),
                LocalDateTime.now(TokenService.ZONE),
                counters,
                services);
    }
}