package dao;

import model.Morador;
import model.Pessoa;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoradorDAO {
    public void salvar(Morador morador) throws SQLException {
        String sql = "INSERT INTO morador (id_pessoa, data_registro) VALUES (?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, morador.getPessoa().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    morador.setId(rs.getInt(1));
                }
            }
        }
    }
    
    public int cadastrarMorador(Pessoa pessoa) throws SQLException {
        String sql = "INSERT INTO morador (id_pessoa) VALUES (?)";
        int idGerado = -1;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pessoa.getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idGerado = rs.getInt(1);
            }
        }

        return idGerado;
    }


    public List<Morador> listarTodos() throws SQLException {
        List<Morador> moradores = new ArrayList<>();
        String sql = "SELECT m.id, m.data_registro, p.* FROM morador m " +
                   "JOIN pessoa p ON m.id_pessoa = p.id";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pessoa pessoa = new Pessoa(
                    rs.getInt("p.id"),
                    rs.getString("p.nome"),
                    rs.getDate("p.data_nasc").toLocalDate(), // Correção aqui
                    rs.getString("p.rg"),
                    rs.getString("p.cpf"),
                    rs.getTimestamp("p.data_cadastro").toLocalDateTime()
                );
                Morador m = new Morador(
                    rs.getInt("m.id"),
                    pessoa,
                    rs.getTimestamp("m.data_registro").toLocalDateTime()
                );
                moradores.add(m);
            }
        }
        return moradores;
    }
    
    public List<Map<String, Object>> listarMoradoresComDetalhes() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = """
            SELECT 
                m.id AS id_morador,
                p.id AS id_pessoa,
                mr.id AS id_vinculo,
                p.nome,
                p.cpf,
                p.rg,
                p.data_nasc,
                r.rua,
                r.numero,
                mr.data_vinculo,
                mr.responsavel
            FROM 
                morador m
            INNER JOIN 
                pessoa p ON m.id_pessoa = p.id
            LEFT JOIN 
                morador_residencia mr ON m.id = mr.id_morador
            LEFT JOIN 
                residencia r ON mr.id_residencia = r.id
            ORDER BY 
                r.rua, r.numero, p.nome;
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id_morador", rs.getInt("id_morador"));
                map.put("id_pessoa", rs.getInt("id_pessoa"));
                map.put("id_vinculo", rs.getObject("id_vinculo")); // pode ser null
                map.put("nome", rs.getString("nome"));
                map.put("cpf", rs.getString("cpf"));
                map.put("rg", rs.getString("rg"));
                map.put("data_nasc", rs.getDate("data_nasc") != null 
                        ? rs.getDate("data_nasc").toLocalDate() : null);
                map.put("rua", rs.getString("rua"));
                map.put("numero", rs.getString("numero"));
                map.put("data_vinculo", rs.getTimestamp("data_vinculo") != null 
                        ? rs.getTimestamp("data_vinculo").toLocalDateTime().toLocalDate() : null);
                map.put("responsavel", rs.getObject("responsavel") != null 
                        ? rs.getBoolean("responsavel") : false);

                lista.add(map);
            }
        }
        return lista;
    }

    public void vincularMoradorResidencia(int idMorador, int idResidencia, boolean responsavel) throws SQLException {
        String sql = """
            INSERT INTO morador_residencia (id_morador, id_residencia, data_vinculo, responsavel)
            VALUES (?, ?, CURRENT_DATE, ?)
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMorador);
            stmt.setInt(2, idResidencia);
            stmt.setBoolean(3, responsavel);

            stmt.executeUpdate();
        }
    }
    
    public boolean pessoaJaEhMorador(int idPessoa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM morador WHERE id_pessoa = ?";
            try (Connection conn = ConexaoBD.getConexao();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idPessoa);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
            return false;
    }
    
    public boolean pessoaJaEhProprietario(int idPessoa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM proprietario WHERE id_pessoa = ?";
            try (Connection conn = ConexaoBD.getConexao();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idPessoa);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
            return false;
    }
    
    public void deletarVinculoResidenciaPorId(int idVinculo) throws SQLException {
        String sql = "DELETE FROM morador_residencia WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVinculo);
            stmt.executeUpdate();
        }
    }

    // Deletar todos os vínculos de um morador
    public void deletarTodosVinculosResidencia(int idMorador) throws SQLException {
        String sql = "DELETE FROM morador_residencia WHERE id_morador = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMorador);
            stmt.executeUpdate();
        }
    }

    // Deletar o morador da tabela morador
    public void deletarMorador(int idMorador) throws SQLException {
        String sql = "DELETE FROM morador WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMorador);
            stmt.executeUpdate();
        }
    }

    // Buscar o id_pessoa a partir do id_morador
    public int buscarIdPessoaPorIdMorador(int idMorador) throws SQLException {
        String sql = "SELECT id_pessoa FROM morador WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMorador);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_pessoa");
                } else {
                    throw new SQLException("Pessoa não encontrada para este morador.");
                }
            }
        }
    }


    


}