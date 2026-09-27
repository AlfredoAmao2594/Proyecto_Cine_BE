package com.cine.complete.util;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class CardUtils {

     private static final DateTimeFormatter FORMATO_EXPIRACION = DateTimeFormatter.ofPattern("yyyy/MM");

    private CardUtils() {
    }

    /**
     * Algoritmo de Luhn: de derecha a izquierda, duplica uno sí y uno no;
     * si el doble pasa de 9 réstale 9; la suma total debe ser múltiplo de 10.
     */
    public static boolean esLuhnValido(String numero) {
        if (numero == null || !numero.matches("\\d{12,19}")) {
            return false;
        }
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }

    /** Franquicia según el primer dígito (suficiente para el sandbox). */
    public static String franquicia(String numero) {
        switch (numero.charAt(0)) {
            case '4':
                return "VISA";
            case '5':
                return "MASTERCARD";
            case '3':
                return "AMEX";
            default:
                return "DINERS";
        }
    }

    /** "4097440000000004" -> "************0004" */
    public static String enmascarar(String numero) {
        if (numero == null || numero.length() < 4) {
            return "****";
        }
        return "*".repeat(numero.length() - 4) + numero.substring(numero.length() - 4);
    }

    /** true si la fecha YYYY/MM ya pasó (una tarjeta vence al terminar su mes). */
    public static boolean estaVencida(String expiracion, YearMonth mesActual) {
        try {
            return YearMonth.parse(expiracion, FORMATO_EXPIRACION).isBefore(mesActual);
        } catch (DateTimeParseException e) {
            return true;
        }
    }
}
