-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS condominio;
USE condominio;

-- Tabela base de Pessoas
CREATE TABLE IF NOT EXISTS pessoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_nasc DATE NOT NULL,
    rg VARCHAR(20) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Proprietários
CREATE TABLE IF NOT EXISTS proprietario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pessoa INT NOT NULL UNIQUE,
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pessoa) REFERENCES pessoa(id)
);

-- Tabela de Moradores
CREATE TABLE IF NOT EXISTS morador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pessoa INT NOT NULL UNIQUE,
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pessoa) REFERENCES pessoa(id)
);

-- Tabela de Residências
CREATE TABLE IF NOT EXISTS residencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rua VARCHAR(100) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    cep VARCHAR(9) NOT NULL,
    id_proprietario INT,
    em_dia BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_proprietario) REFERENCES proprietario(id)
);

-- Tabela de relação Morador-Residência
CREATE TABLE IF NOT EXISTS morador_residencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_morador INT NOT NULL,
    id_residencia INT NOT NULL,
    responsavel BOOLEAN DEFAULT FALSE,
    data_vinculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_morador) REFERENCES morador(id),
    FOREIGN KEY (id_residencia) REFERENCES residencia(id)
);

-- Tabela de Pagamentos do Condomínio
CREATE TABLE IF NOT EXISTS pagamento_condominio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_residencia INT NOT NULL,
    mes INT NOT NULL,
    ano INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_pagamento DATE,
    pago BOOLEAN DEFAULT FALSE,
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_residencia) REFERENCES residencia(id)
);