# AgroPecuaria

Sistema para agropecuárias gerenciarem clientes e notificações automáticas de recompra.

## Tecnologias

- Java
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- JPA/Hibernate
- HTML, CSS e JavaScript puro

## Funcionalidades

- Login com JWT
- Senha criptografada com BCrypt
- CRUD de clientes(ADMIN)
- Login ADMIN
- Busca de clientes
- Dashboard
- Scheduler para simular envio automático após 7 dias
- Frontend simples para rodar o sistema

## Como rodar

Configure o PostgreSQL e ajuste o `application.properties`.

Depois execute:

```bash
./mvnw spring-boot:run