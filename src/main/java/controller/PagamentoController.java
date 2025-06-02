package controller;

import dao.DAOFactory;
import dao.PagamentoDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PagamentoController {
    private PagamentoDAO dao;

    public PagamentoController() {
        this.dao = DAOFactory.criarPagamentoDAO();
    }

    public List<Map<String, Object>> listarPagamentosPorResidencia(int idResidencia) throws SQLException {
        return dao.listarPagamentosPorResidencia(idResidencia);
    }

    public void registrarPagamento(int idResidencia, int mes, int ano, BigDecimal valor) throws SQLException {
        dao.registrarPagamento(idResidencia, mes, ano, valor);
    }

    public void pagar(int idPagamento) throws SQLException {
        dao.pagar(idPagamento);
    }
}
