package com.example.springboot.Repositorios;
import com.example.springboot.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select count(id) from Usuario where email= ?1 and password = ?2")
    Integer repValidarPassword(String email, String password);

    Usuario findByUsername(String username);

/*    Usuario findByEmail(String email);

    Optional<Usuario> findByEmailAndTokenAndActiveTrue(String email, String token);

    Usuario findNombreUsuarioAndActiveTrue(String nombre);

    Usuario findUsuarioByEmailAndActiveTrue(String email);

    Usuario findUsuarioByEmailAndPassword(String email, String password); */

    //Posibles Más métodos
}
