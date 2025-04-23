package com.example.springboot.pokemon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PokemonResponse {
    private PokemonDTO pokemon; // Este campo debe coincidir con la estructura de la respuesta JSON
}