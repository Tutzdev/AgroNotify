package com.agronotify.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByStatusEnvioAndDataEnvioLessThanEqual(
            StatusEnvio statusEnvio,
            LocalDate dataEnvio
    );

    List<Cliente> findByNomeContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrProdutoContainingIgnoreCase(
            String nome,
            String telefone,
            String produto
    );

    long countByStatusEnvio(StatusEnvio statusEnvio);
}