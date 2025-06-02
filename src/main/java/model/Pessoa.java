package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Pessoa {
    private int id;
    private String nome;
    private LocalDate dataNasc;
    private String rg;
    private String cpf;
    private LocalDateTime dataCadastro;

    public Pessoa() {
    
    }
    
    // Construtor para cadastro (sem ID)
    public Pessoa(String nome, LocalDate dataNasc, String rg, String cpf) {
        this.nome = nome;
        this.dataNasc = dataNasc;
        this.rg = rg;
        this.cpf = cpf;
    }

    // Construtor para edição (com ID)
    public Pessoa(int id, String nome, LocalDate dataNasc, String rg, String cpf) {
        this.id = id;
        this.nome = nome;
        this.dataNasc = dataNasc;
        this.rg = rg;
        this.cpf = cpf;
    }

    // Construtor completo (para busca no banco)
    public Pessoa(int id, String nome, LocalDate dataNasc, String rg, String cpf, LocalDateTime dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.dataNasc = dataNasc;
        this.rg = rg;
        this.cpf = cpf;
        this.dataCadastro = dataCadastro;
    }
    

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
}
