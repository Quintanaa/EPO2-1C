package com.example.springboot.Repositorios;
import com.example.springboot.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

/*    @Query("select count(id) from Usuario where email= ?1 and password = ?2")
    Integer repValidarPassword(String email, String password);

    Usuario finUsuariobyNombre(String nombre);
    Usuario findByEmail(String email);

    Optional<Usuario> findByEmailAndTokenAndActiveTrue(String email, String token);

    Usuario findNombreUsuarioAndActiveTrue(String nombre);

    Usuario findUsuarioByEmailAndActiveTrue(String email);

    Usuario findUsuarioByEmailAndPassword(String email, String password); */

    //Posibles Más métodos
}
