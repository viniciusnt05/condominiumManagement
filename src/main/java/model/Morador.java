package model;

import java.time.LocalDateTime;

public class Morador {
    private int id;
    private Pessoa pessoa;
    private LocalDateTime dataRegistro;

    // Construtor
    public Morador(int id, Pessoa pessoa, LocalDateTime dataRegistro) {
        this.id = id;
        this.pessoa = pessoa;
        this.dataRegistro = dataRegistro;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

}