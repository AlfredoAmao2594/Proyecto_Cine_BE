package com.cine.auth.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioAutenticado {
    private String sub;
    private String nombre;
    private String rol;
}