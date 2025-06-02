package dao;

import model.Residencia;
import model.Proprietario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap; // só se precisar (mas não vamos usar aqui)
import java.util.Map;
import model.Pessoa;

public class ResidenciaDAO {

    private final ProprietarioDAO proprietarioDAO = new ProprietarioDAO();

    public void salvar(Residencia residencia) throws SQLException {
        String sql = "INSERT INTO residencia (rua, numero, cep, id_proprietario, em_dia) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, residencia.getRua());
            stmt.setString(2, residencia.getNumero());
            stmt.setString(3, residencia.getCep());

            if (residencia.getProprietario() != null) {
                stmt.setInt(4, residencia.getProprietario().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setBoolean(5, residencia.isEmDia());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    residencia.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Residencia> listarPorProprietario(int idProprietario) throws SQLException {
        List<Residencia> residencias = new ArrayList<>();
        String sql = "SELECT * FROM residencia WHERE id_proprietario = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProprietario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Proprietario proprietario = proprietarioDAO.buscarPorId(rs.getInt("id_proprietario"));

                    Residencia r = new Residencia(
                        rs.getInt("id"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("cep"),
                        proprietario,
                        rs.getBoolean("em_dia")
                    );
                    residencias.add(r);
                }
            }
        }
        return residencias;
    }

    public Map<String, Object> buscarPorId(int id) throws SQLException {
        String sql = "SELECT r.id, r.rua, r.numero, r.cep, r.id_proprietario, r.em_dia FROM residencia r WHERE r.id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("id", rs.getInt("id"));
                    mapa.put("rua", rs.getString("rua"));
                    mapa.put("numero", rs.getInt("numero"));
                    mapa.put("cep", rs.getString("cep"));
                    mapa.put("id_proprietario", rs.getInt("id_proprietario"));
                    mapa.put("em_dia", rs.getBoolean("em_dia"));
                    return mapa;
                } else {
                    return null;
                }
            }
        }
    }

    public void atualizar(int idResidencia, String rua, int numero, String cep, Integer idProprietario, boolean emDia) throws SQLException {
        String sql = "UPDATE residencia SET rua = ?, numero = ?, cep = ?, id_proprietario = ?, em_dia = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rua);
            stmt.setInt(2, numero);
            stmt.setString(3, cep);

            if (idProprietario != null && idProprietario != -1) {
                stmt.setInt(4, idProprietario);
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setBoolean(5, emDia);
            stmt.setInt(6, idResidencia);

            stmt.executeUpdate();
        }
    }

    public List<Residencia> listarTodos() throws SQLException {
        List<Residencia> lista = new ArrayList<>();

        String sql = """
            SELECT r.id, r.rua, r.numero, r.cep, r.em_dia,
                   p.id AS proprietario_id,
                   pe.id AS pessoa_id, pe.nome, pe.cpf, pe.rg
            FROM residencia r
            LEFT JOIN proprietario p ON r.id_proprietario = p.id
            LEFT JOIN pessoa pe ON p.id_pessoa = pe.id
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Residencia r = new Residencia();
                r.setId(rs.getInt("id"));
                r.setRua(rs.getString("rua"));
                r.setNumero(rs.getString("numero"));
                r.setCep(rs.getString("cep"));
                r.setEmDia(rs.getBoolean("em_dia"));

                // Montar Pessoa (proprietário)
                Pessoa pessoa = null;
                int pessoaId = rs.getInt("pessoa_id");
                if (!rs.wasNull()) {
                    pessoa = new Pessoa();
                    pessoa.setId(pessoaId);
                    pessoa.setNome(rs.getString("nome"));
                    pessoa.setCpf(rs.getString("cpf"));
                    pessoa.setRg(rs.getString("rg"));
                }

                // Montar Proprietario
                Proprietario proprietario = null;
                int proprietarioId = rs.getInt("proprietario_id");
                if (!rs.wasNull()) {
                    proprietario = new Proprietario();
                    proprietario.setId(proprietarioId);
                    proprietario.setPessoa(pessoa);
                }

                r.setProprietario(proprietario);

                lista.add(r);
            }
        }
        return lista;
    }

    public void atualizarStatusPagamento(int idResidencia, boolean emDia) throws SQLException {
        String sql = "UPDATE residencia SET em_dia = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, emDia);
            stmt.setInt(2, idResidencia);

            stmt.executeUpdate();
        }
    }
    
    public List<Map<String, Object>> listarResidenciasComDetalhes() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = """
            SELECT 
                r.id,
                r.rua,
                r.numero,
                r.cep,
                r.em_dia,
                p.nome AS nome_proprietario,
                p.cpf AS cpf_proprietario,
                COUNT(mr.id_morador) AS qtde_moradores,
                resp.nome AS nome_responsavel,
                resp.cpf AS cpf_responsavel
            FROM 
                residencia r
            LEFT JOIN 
                proprietario pr ON r.id_proprietario = pr.id
            LEFT JOIN 
                pessoa p ON pr.id_pessoa = p.id
            LEFT JOIN 
                morador_residencia mr ON r.id = mr.id_residencia
            LEFT JOIN 
                morador m ON mr.id_morador = m.id
            LEFT JOIN 
                pessoa resp ON resp.id = (
                    SELECT p2.id
                    FROM morador_residencia mr2
                    JOIN morador m2 ON mr2.id_morador = m2.id
                    JOIN pessoa p2 ON m2.id_pessoa = p2.id
                    WHERE mr2.id_residencia = r.id AND mr2.responsavel = true
                    LIMIT 1
                )
            GROUP BY 
                r.id, r.rua, r.numero, r.cep, r.em_dia, 
                p.nome, p.cpf, resp.nome, resp.cpf;
        """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("rua", rs.getString("rua"));
                map.put("numero", rs.getString("numero"));
                map.put("cep", rs.getString("cep"));
                map.put("em_dia", rs.getBoolean("em_dia"));

                // Proprietário
                String nomeProprietario = rs.getString("nome_proprietario");
                String cpfProprietario = rs.getString("cpf_proprietario");
                if (nomeProprietario != null && cpfProprietario != null) {
                    map.put("proprietario", nomeProprietario + " - " + cpfProprietario);
                } else {
                    map.put("proprietario", "Sem proprietário");
                }

                // Moradores
                map.put("qtde_moradores", rs.getInt("qtde_moradores"));

                // Responsável
                String resp = rs.getString("nome_responsavel");
                String cpfResp = rs.getString("cpf_responsavel");
                if (resp != null && cpfResp != null) {
                    map.put("responsavel", resp + " - " + cpfResp);
                } else {
                    map.put("responsavel", "Sem responsável");
                }

                lista.add(map);
            }
        }
        return lista;
    }
    
    public void desvincularProprietario(int idProprietario) throws SQLException {
        String sql = "UPDATE residencia SET id_proprietario = NULL WHERE id_proprietario = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProprietario);
            stmt.executeUpdate();
        }
    }
    
    public void deletarResidencia(int idResidencia) throws SQLException {
        String sqlDeleteVinculos = "DELETE FROM morador_residencia WHERE id_residencia = ?";
        String sqlDeleteResidencia = "DELETE FROM residencia WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao()) {
            conn.setAutoCommit(false); // Inicia transação

            try (
                PreparedStatement stmtVinculos = conn.prepareStatement(sqlDeleteVinculos);
                PreparedStatement stmtResidencia = conn.prepareStatement(sqlDeleteResidencia)
            ) {
                // Deleta os vínculos com moradores
                stmtVinculos.setInt(1, idResidencia);
                stmtVinculos.executeUpdate();

                // Deleta a residência
                stmtResidencia.setInt(1, idResidencia);
                stmtResidencia.executeUpdate();

                conn.commit(); // Confirma transação
            } catch (SQLException e) {
                conn.rollback(); // Desfaz alterações se der erro
                throw e;
            } finally {
                conn.setAutoCommit(true); // Garante que o autocommit volte ao normal
            }
        }
    }
}
