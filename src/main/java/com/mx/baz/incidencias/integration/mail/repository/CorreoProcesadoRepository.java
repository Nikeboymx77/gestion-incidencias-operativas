package com.mx.baz.incidencias.integration.mail.repository;

import com.mx.baz.incidencias.integration.mail.entity.CorreoProcesado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorreoProcesadoRepository extends JpaRepository<CorreoProcesado, Long> {

    boolean existsByIdCorreo(String idCorreo);
}
