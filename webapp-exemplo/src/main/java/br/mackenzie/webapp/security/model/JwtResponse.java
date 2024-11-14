package br.mackenzie.webapp.security.model;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    // Método para obter o token JWT
    public String getToken() {
        return this.jwttoken;
    }
}