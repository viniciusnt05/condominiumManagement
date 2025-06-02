package model;

import java.time.LocalDateTime;

public class MoradorResidencia {
    private int id;
    private Morador morador;
    private Residencia residencia;
    private boolean responsavel;
    private LocalDateTime dataVinculo;

    // Construtor
    public MoradorResidencia(int id, Morador morador, Residencia residencia, boolean responsavel, LocalDateTime dataVinculo) {
        this.id = id;
        this.morador = morador;
        this.residencia = residencia;
        this.responsavel = responsavel;
        this.dataVinculo = dataVinculo;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }

    public Residencia getResidencia() {
        return residencia;
    }

    public void setResidencia(Residencia residencia) {
        this.residencia = residencia;
    }

    public boolean isResponsavel() {
        return responsavel;
    }

    public void setResponsavel(boolean responsavel) {
        this.responsavel = responsavel;
    }

    public LocalDateTime getDataVinculo() {
        return dataVinculo;
    }

    public void setDataVinculo(LocalDateTime dataVinculo) {
        this.dataVinculo = dataVinculo;
    }
    
}