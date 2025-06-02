package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagamentoDAO {
    public List<Map<String, Object>> listarPagamentosPorResidencia(int idResidencia) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = """
            SELECT 
                pc.id,
                pc.mes,
                pc.ano,
                pc.valor,
                pc.pago,
                pc.data_pagamento,
                CONCAT(p.nome, ' - ', p.cpf) AS responsavel
            FROM 
                pagamento_condominio pc
            LEFT JOIN 
                residencia r ON pc.id_residencia = r.id
            LEFT JOIN 
                morador_residencia mr ON mr.id_residencia = r.id AND mr.responsavel = true
            LEFT JOIN 
                pessoa p ON mr.id_morador = p.id
            WHERE 
                pc.id_residencia = ?
            ORDER BY pc.ano DESC, pc.mes DESC;
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResidencia);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("mes", rs.getInt("mes"));
                map.put("ano", rs.getInt("ano"));
                map.put("valor", rs.getBigDecimal("valor"));
                map.put("pago", rs.getBoolean("pago"));
                map.put("data_pagamento", rs.getDate("data_pagamento") != null ? rs.getDate("data_pagamento").toLocalDate() : null);
                map.put("responsavel", rs.getString("responsavel") != null ? rs.getString("responsavel") : "Sem responsável");
                lista.add(map);
            }
        }

        return lista;
    }

    public void registrarPagamento(int idResidencia, int mes, int ano, java.math.BigDecimal valor) throws SQLException {
        String sql = """
            INSERT INTO pagamento_condominio (id_residencia, mes, ano, valor, pago)
            VALUES (?, ?, ?, ?, false);
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResidencia);
            stmt.setInt(2, mes);
            stmt.setInt(3, ano);
            stmt.setBigDecimal(4, valor);
            stmt.executeUpdate();
        }
    }

    public void pagar(int idPagamento) throws SQLException {
        String sql = """
            UPDATE pagamento_condominio
            SET pago = true, data_pagamento = CURRENT_DATE()
            WHERE id = ?;
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPagamento);
            stmt.executeUpdate();
        }
    }

}