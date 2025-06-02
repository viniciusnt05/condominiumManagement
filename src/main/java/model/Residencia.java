package model;

public class Residencia {
    private int id;
    private String rua;
    private String numero;
    private String cep;
    private Proprietario proprietario;
    private boolean emDia;

    public Residencia() {
        
    }
    
    // Construtor
    public Residencia(int id, String rua, String numero, String cep, Proprietario proprietario, boolean emDia) {
        this.id = id;
        this.rua = rua;
        this.numero = numero;
        this.cep = cep;
        this.proprietario = proprietario;
        this.emDia = emDia;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Proprietario getProprietario() {
        return proprietario;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

    public boolean isEmDia() {
        return emDia;
    }

    public void setEmDia(boolean emDia) {
        this.emDia = emDia;
    }
    
}