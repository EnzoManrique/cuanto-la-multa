package com.manrique.split_app.repository;

import com.manrique.split_app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
        java.util.Optional<Usuario> findByEmail(String email);

}