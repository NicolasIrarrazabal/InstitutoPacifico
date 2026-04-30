package com.proyecto.ms_docente.repository;

import com.proyecto.ms_docente.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DocenteRepository  extends JpaRepository<Docente, UUID> {
    boolean existeByEmail(String email);
}
