package com.example.springboot.psswd;

import org.springframework.context.annotation.Bean;

import java.util.regex.Pattern;

public class ValidarFormatoPassword {
    //Contraseña de 4 - 8 caracteres que requiere números y letras
    private static final String PASSWORD_REGEX =  "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}$";

    //Contraseña de 4 a 32 caracteres qye requiere al menos 3 de 4: (mayúsculas
    //y letras minúsculas, números y caracteres especiales) y como máximo
    //2 caracteres consecutivos iguales.
    private static final String COMPLEX_PASSWORD_REGEX =
            "^(?:(?=.*\\d)|(?=.*[a-z])|(?=.*[A-Z])|" +
                    "(?=.*[\\W]))(?:(?=.*\\d)(?=.*[a-z])|" +
                    "(?=.*[\\d])(?=.*[A-Z])|(?=.*[\\d])(?=.*[\\W])|" +
                    "(?=.*[a-z])(?=.*[A-Z])|(?=.*[a-z])(?=.*[\\W])|" +
                    "(?=.*[A-Z])(?=.*[\\W]))(?!.*(.)\\1{2,})[\\da-zA-Z\\W]{4,32}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(COMPLEX_PASSWORD_REGEX);

    @Bean
    public static boolean validarFormato(String password) {
        //Validar la contraseña
        if (PASSWORD_PATTERN.matcher(password).matches()) {
            return true;
        } else {
            return false;
        }
    }
}
