package com.example.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductoDTO {

    private long id;

    private String nombre;

    private double precio;

    private int cantidad;

    private Set<CategoriaDTO> categorias;
}
