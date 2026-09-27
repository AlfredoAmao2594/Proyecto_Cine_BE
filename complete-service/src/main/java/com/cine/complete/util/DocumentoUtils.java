package com.cine.complete.util;

public final class DocumentoUtils {

    private DocumentoUtils() {
    }

    public static boolean esValido(String tipo, String numero) {
        if (tipo == null || numero == null) {
            return false;
        }
        switch (tipo) {
            case "DNI":
                return numero.matches("\\d{8}");
            case "CE":
                return numero.matches("[A-Za-z0-9]{8,12}");
            case "PASAPORTE":
                return numero.matches("[A-Za-z0-9]{6,12}");
            default:
                return false;
        }
    }
}