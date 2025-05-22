package com.example.springboot.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nombre;

    @Column (nullable = false)
    @DecimalMin(value = "0.0")
    private double precio;

    @Column (nullable = false)
    @Min(value = 0)
    private int cantidad;

    @Nullable
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Categoria> categorias;
}
