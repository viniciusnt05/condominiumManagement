package model;

import java.time.LocalDateTime;

public class Proprietario {

    private int id;
    private Pessoa pessoa;
    private LocalDateTime dataRegistro;

    public Proprietario() {
        
    }
    
    public Proprietario(int id, Pessoa pessoa, LocalDateTime dataRegistro) {
        this.id = id;
        this.pessoa = pessoa;
        this.dataRegistro = dataRegistro;
    }

    // Getter e Setter do id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter e Setter da pessoa
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    // Getter e Setter da dataRegistro
    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
}
