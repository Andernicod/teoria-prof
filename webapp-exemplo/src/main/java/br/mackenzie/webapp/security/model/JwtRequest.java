package br.mackenzie.webapp.security.model;

import java.io.Serializable;

public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String nome;
    private String sobrenome;
    private String email;
    private String username;
    private String password;

    // Construtor padrão necessário para o JSON Parsing
    public JwtRequest() {
    }

    // Construtor com todos os campos
    public JwtRequest(String nome, String sobrenome, String email, String username, String password) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getters e Setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}