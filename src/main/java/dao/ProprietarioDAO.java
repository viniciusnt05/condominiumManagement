package dao;

import model.Proprietario;
import model.Pessoa;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProprietarioDAO {
    public void salvar(Proprietario proprietario) throws SQLException {
        String sql = "INSERT INTO proprietario (id_pessoa, data_registro) VALUES (?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, proprietario.getPessoa().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(proprietario.getDataRegistro()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    proprietario.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Proprietario> listarTodos() throws SQLException {
        List<Proprietario> proprietarios = new ArrayList<>();
        String sql = "SELECT pr.id AS proprietario_id, pr.data_registro AS proprietario_data_registro, " +
                     "p.* FROM proprietario pr JOIN pessoa p ON pr.id_pessoa = p.id";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pessoa pessoa = new Pessoa(
                        rs.getInt("p.id"),
                        rs.getString("p.nome"),
                        rs.getDate("p.data_nasc").toLocalDate(),
                        rs.getString("p.rg"),
                        rs.getString("p.cpf"),
                        rs.getTimestamp("p.data_cadastro").toLocalDateTime()
                );

                Proprietario proprietario = new Proprietario(
                        rs.getInt("proprietario_id"),
                        pessoa,
                        rs.getTimestamp("proprietario_data_registro").toLocalDateTime()
                );

                proprietarios.add(proprietario);
            }
        }
        return proprietarios;
    }
    
    public Proprietario buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id, p.nome, p.data_nasc, p.rg, p.cpf, pr.data_registro " +
                     "FROM proprietario pr " +
                     "JOIN pessoa p ON pr.id_pessoa = p.id " +
                     "WHERE pr.id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Converte data_nasc de java.sql.Date para java.time.LocalDate
                    java.sql.Date sqlDateNasc = rs.getDate("data_nasc");
                    LocalDate dataNasc = sqlDateNasc != null ? sqlDateNasc.toLocalDate() : null;

                    // Cria o objeto Pessoa usando o construtor completo sem dataCadastro
                    Pessoa pessoa = new Pessoa(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        dataNasc,
                        rs.getString("rg"),
                        rs.getString("cpf")
                    );

                    // Converte Timestamp para LocalDateTime
                    Timestamp ts = rs.getTimestamp("data_registro");
                    LocalDateTime dataRegistro = ts != null ? ts.toLocalDateTime() : null;

                    // Cria o Proprietario com Pessoa e dataRegistro
                    Proprietario proprietario = new Proprietario(id, pessoa, dataRegistro);

                    return proprietario;
                }
            }
        }

        return null;
    }
    
    public Pessoa buscarPessoaPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, data_nasc, rg, cpf, data_cadastro FROM pessoa WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Pessoa(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDate("data_nasc").toLocalDate(),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getTimestamp("data_cadastro").toLocalDateTime()
                    );
                }
            }
        }
        return null;
    }

    
    public List<Map<String, Object>> listarProprietariosComEndereco() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = """
            SELECT 
                p.id,
                p.nome,
                p.cpf,
                p.rg,
                p.data_nasc,
                p.data_cadastro,
                r.rua,
                r.numero
            FROM 
                proprietario pr
            JOIN 
                pessoa p ON pr.id_pessoa = p.id
            LEFT JOIN 
                residencia r ON r.id_proprietario = pr.id;
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("nome", rs.getString("nome"));
                map.put("cpf", rs.getString("cpf"));
                map.put("rg", rs.getString("rg"));
                map.put("data_nasc", rs.getDate("data_nasc").toLocalDate());
                map.put("data_cadastro", rs.getTimestamp("data_cadastro").toLocalDateTime());
                String endereco = rs.getString("rua") != null ? rs.getString("rua") + ", " + rs.getString("numero") : "Sem residência";
                map.put("endereco", endereco);

                lista.add(map);
            }
        }
        return lista;
    }
    
    public List<Map<String, Object>> listarProprietariosComResidencia() throws SQLException {
        String sql = "SELECT p.id AS id_pessoa, pr.id AS id_proprietario, p.nome, p.cpf, p.rg, p.data_nasc, pr.data_registro, " +
                     "r.rua, r.numero " +
                     "FROM proprietario pr " +
                     "JOIN pessoa p ON pr.id_pessoa = p.id " +
                     "JOIN residencia r ON pr.id = r.id_proprietario";

        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();

                map.put("id_pessoa", rs.getInt("id_pessoa"));           // id da pessoa
                map.put("id_proprietario", rs.getInt("id_proprietario")); // id do proprietário
                map.put("nome", rs.getString("nome"));
                map.put("cpf", rs.getString("cpf"));
                map.put("rg", rs.getString("rg"));

                Date dataNasc = rs.getDate("data_nasc");
                map.put("data_nasc", dataNasc != null ? dataNasc.toLocalDate() : null);

                Timestamp dataCadastro = rs.getTimestamp("data_registro");
                map.put("data_cadastro", dataCadastro != null ? dataCadastro.toLocalDateTime() : null);

                String endereco = rs.getString("rua") + ", " + rs.getString("numero");
                map.put("endereco", endereco);

                lista.add(map);
            }
        }

        return lista;
    }

    
    public List<Map<String, Object>> listarProprietariosComDadosDistinct() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = """
            SELECT DISTINCT p.id AS id_proprietario, pe.nome, pe.cpf
            FROM proprietario p
            JOIN pessoa pe ON p.id_pessoa = pe.id
            ORDER BY pe.nome
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id_proprietario"));  // id do proprietario
                item.put("nome", rs.getString("nome"));
                item.put("cpf", rs.getString("cpf"));
                lista.add(item);
            }
        }

        return lista;
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
    
    // Buscar idPessoa pelo idProprietario
    public int buscarIdPessoaPorProprietario(int idProprietario) throws SQLException {
        String sql = "SELECT id_pessoa FROM proprietario WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProprietario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_pessoa");
            }
            throw new SQLException("Proprietário não encontrado.");
        }
    }

    // Verificar se existe morador com essa pessoa
    public boolean existeMoradorPorPessoa(int idPessoa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM morador WHERE id_pessoa = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPessoa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    // Deletar proprietário pelo id
    public void deletarProprietarioPorId(int idProprietario) throws SQLException {
        String sql = "DELETE FROM proprietario WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProprietario);
            stmt.executeUpdate();
        }
    }

    // Deletar pessoa pelo id
    public void deletarPessoaPorId(int idPessoa) throws SQLException {
        String sql = "DELETE FROM pessoa WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPessoa);
            stmt.executeUpdate();
        }
    }
}
