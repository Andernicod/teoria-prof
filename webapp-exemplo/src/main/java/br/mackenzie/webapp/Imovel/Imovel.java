package br.mackenzie.webapp.Imovel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import br.mackenzie.webapp.security.model.DAOUser;

import javax.persistence.JoinColumn;

@Entity
public class Imovel {
    @Id 
    @GeneratedValue
    private Long id;
    private String titulo;

    @Lob
    private String descricao;
    private double preco;

    @Lob
    private String foto;
    private String tipo;

    private String rua;
    private String numero;
    private String cidade;
    private String estado;
    private String cep;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private DAOUser usuario;

    public Imovel() {}

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public String getFoto() { return foto; }
    public String getTipo() { return tipo; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCep() { return cep; }
    public DAOUser getUsuario() { return usuario; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setFoto(String foto) { this.foto = foto; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setRua(String rua) { this.rua = rua; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCep(String cep) { this.cep = cep; }
    public void setUsuario(DAOUser usuario) { this.usuario = usuario; }
}