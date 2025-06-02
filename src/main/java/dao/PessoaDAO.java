package dao;

import model.Pessoa;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {
    public void salvar(Pessoa pessoa) throws SQLException {
        String sql = "INSERT INTO pessoa (nome, data_nasc, rg, cpf, data_cadastro) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pessoa.getNome());
            stmt.setDate(2, Date.valueOf(pessoa.getDataNasc())); // Mudança aqui
            stmt.setString(3, pessoa.getRg());
            stmt.setString(4, pessoa.getCpf());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pessoa.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Pessoa> listarTodos() throws SQLException {
        List<Pessoa> pessoas = new ArrayList<>();
        String sql = """
            SELECT id, nome, data_nasc, rg, cpf, data_cadastro
            FROM pessoa
            WHERE id NOT IN (SELECT id_pessoa FROM morador)
              AND id NOT IN (SELECT id_pessoa FROM proprietario)
        """;
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pessoa p = new Pessoa(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDate("data_nasc").toLocalDate(),
                    rs.getString("rg"),
                    rs.getString("cpf"),
                    rs.getTimestamp("data_cadastro").toLocalDateTime()
                );
                pessoas.add(p);
            }
        }
        return pessoas;
    }

    public Pessoa buscarPorId(int id) throws SQLException {
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
    
    public Pessoa buscarPessoaPorIdMorador(int idMorador) throws SQLException {
        String sql = """
                     SELECT p.id, p.nome, p.data_nasc, p.rg, p.cpf, p.data_cadastro
                     FROM pessoa p
                     INNER JOIN morador m ON p.id = m.id_pessoa
                     WHERE m.id = ?
                     """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMorador);

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

    public void atualizarPessoa(int id, String nome, LocalDate dataNasc, String rg, String cpf) throws SQLException {
        String sql = "UPDATE pessoa SET nome = ?, data_nasc = ?, rg = ?, cpf = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            // Converte LocalDate para java.sql.Date para o MySQL
            stmt.setDate(2, java.sql.Date.valueOf(dataNasc));

            stmt.setString(3, rg);
            stmt.setString(4, cpf);
            stmt.setInt(5, id);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Erro: nenhuma linha foi atualizada.");
            }
        }
    }
    
    // Deletar pessoa
    public void deletarPessoa(int idPessoa) throws SQLException {
        String sql = "DELETE FROM pessoa WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPessoa);
            stmt.executeUpdate();
        }
    }
    
    public Pessoa buscarUltimaPessoa() throws SQLException {
        String sql = "SELECT * FROM pessoa ORDER BY id DESC LIMIT 1";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new Pessoa(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDate("data_nasc").toLocalDate(),
                    rs.getString("cpf"),
                    rs.getString("rg")
                );
            }
        }
        return null;
    }



}