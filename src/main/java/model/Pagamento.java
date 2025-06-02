package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Pagamento {
    private int id;
    private Residencia residencia;
    private int mes;
    private int ano;
    private double valor;
    private LocalDate dataPagamento;
    private boolean pago;
    private LocalDateTime dataRegistro;

    // Construtor
    public Pagamento(int id, Residencia residencia, int mes, int ano, double valor, LocalDate dataPagamento, boolean pago, LocalDateTime dataRegistro) {
        this.id = id;
        this.residencia = residencia;
        this.mes = mes;
        this.ano = ano;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.pago = pago;
        this.dataRegistro = dataRegistro;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Residencia getResidencia() {
        return residencia;
    }

    public void setResidencia(Residencia residencia) {
        this.residencia = residencia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

}