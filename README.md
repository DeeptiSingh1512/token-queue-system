\# Digital Token \& Queue Management System



A backend-focused Digital Token and Queue Management System built with \*\*Java, Spring Boot, Spring Security, JWT, JPA and Microsoft SQL Server\*\*.



The system digitizes token booking and queue management for service offices, allowing citizens to book tokens while staff manage queues and administrators manage offices, services, counters and operational statistics.



\## Features



\### Authentication \& Authorization

\- JWT-based authentication

\- Role-based access control

\- Admin, Staff and Citizen roles

\- Secure protected REST APIs



\### Citizen

\- User registration and login

\- View available services

\- Book digital tokens

\- Track token status and queue position

\- Cancel waiting tokens

\- View token history



\### Staff

\- View assigned queue

\- Call the next waiting token

\- Start service

\- Complete service

\- Skip no-show tokens

\- Manage active queue at assigned counter



\### Admin

\- Manage offices

\- Manage services

\- Manage counters

\- Assign staff to counters

\- Manage staff accounts

\- View office statistics

\- View service-wise statistics



\### Public Queue Board

\- View current queue status without authentication

\- Display currently serving token

\- Display waiting tokens

\- Display service information



\### Automated Queue Management

\- Automatic no-show handling

\- Scheduled token cleanup

\- Configurable called-token timeout

\- Daily expired-token cleanup



\### API \& Validation

\- RESTful API architecture

\- Swagger/OpenAPI documentation

\- Request validation

\- Centralized exception handling

\- Proper HTTP status codes

\- Duplicate active-token protection

\- Authentication and authorization handling



\## Technology Stack



| Technology | Usage |

|---|---|

| Java 21 | Backend development |

| Spring Boot | REST API development |

| Spring Security | Authentication \& authorization |

| JWT | Token-based authentication |

| Spring Data JPA | Database access |

| Hibernate | ORM |

| Microsoft SQL Server | Database |

| Maven | Build \& dependency management |

| Swagger / OpenAPI | API documentation |

| Lombok | Boilerplate reduction |



\## Project Structure



```text

token-queue-system/

├── src/

│   ├── main/

│   │   ├── java/

│   │   │   └── com/tokenqueue/token\_queue\_system/

│   │   └── resources/

│   └── test/

├── pom.xml

├── mvnw

├── mvnw.cmd

├── README.md

└── .gitignore

