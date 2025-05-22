package com.example.springboot.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Nullable
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    // BCrypt encoder estático para usar en eventos JPA
    @Transient
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PrePersist
    @PreUpdate
    private void encriptarPassword() {
        if (this.password != null && !this.password.startsWith("$2a$")) {
            this.password = encoder.encode(this.password);
        }
    }
}
