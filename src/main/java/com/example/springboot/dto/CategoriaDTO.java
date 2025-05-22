package com.example.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTO {

    private long id;
    private String nombre;
    private String descripcion;
    private Set<ProductoDTO> productos;
}
