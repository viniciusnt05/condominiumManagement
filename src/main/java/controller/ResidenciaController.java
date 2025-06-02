package controller;

import dao.DAOFactory;
import dao.ConexaoBD;
import dao.ResidenciaDAO;
import dao.ProprietarioDAO;
import dao.PessoaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Pessoa;
import model.Proprietario;
import model.Residencia;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResidenciaController {
    private final ResidenciaDAO residenciaDAO;
    private final PessoaDAO pessoaDAO;
    private final ProprietarioDAO proprietarioDAO;
    private final ProprietarioController proprietarioController;

    public ResidenciaController() {
        this.residenciaDAO = DAOFactory.criarResidenciaDAO();
        this.pessoaDAO = DAOFactory.criarPessoaDAO();
        this.proprietarioDAO = DAOFactory.criarProprietarioDAO();
        this.proprietarioController = new ProprietarioController();
    }

    // Cadastrar residência
    public String cadastrarResidencia(String rua, String numero, String cep, Integer idProprietario, boolean emDia) {
        try {
            Proprietario proprietario = null;
            if (idProprietario != null && idProprietario != 0) {
                proprietario = proprietarioController.buscarPorId(idProprietario);
            }

            Residencia residencia = new Residencia(
                    0,
                    rua,
                    numero,
                    cep,
                    proprietario,
                    emDia
            );

            residenciaDAO.salvar(residencia);
            return "Residência cadastrada com sucesso!";
        } catch (SQLException e) {
            return "Erro ao cadastrar residência: " + e.getMessage();
        }
    }

    // Listar residências usando stream + lambda para montar a lista de mapas
    public List<Map<String, Object>> listarResidencias() throws SQLException {
        List<Residencia> residencias = residenciaDAO.listarTodos();

        return residencias.stream()
            .map(r -> {
                Map<String, Object> map = new HashMap<>();

                // Monta o endereço concatenando rua + número
                String endereco = r.getRua() + ", " + r.getNumero();

                // Verifica se o proprietário e pessoa estão preenchidos
                String proprietario = (r.getProprietario() != null && r.getProprietario().getPessoa() != null)
                        ? r.getProprietario().getPessoa().getNome()
                        : "Sem proprietário";

                map.put("id", r.getId());
                map.put("endereco", endereco);
                map.put("proprietario", proprietario);
                map.put("status", r.isEmDia() ? "Em dia" : "Pendente");

                return map;
            })
            .collect(Collectors.toList());
    }

    // Atualizar status de pagamento da residência
    public String atualizarStatusPagamento(int idResidencia, boolean emDia) {
        try {
            residenciaDAO.atualizarStatusPagamento(idResidencia, emDia);
            return "Status de pagamento atualizado com sucesso!";
        } catch (SQLException e) {
            return "Erro ao atualizar status: " + e.getMessage();
        }
    }

    public List<Map<String, Object>> listarResidenciasComDetalhes() throws SQLException {
        // Aqui não faz sentido usar stream porque é só repassar resultado do DAO
        return residenciaDAO.listarResidenciasComDetalhes();
    }

    public boolean verificarPagamentosEmDia(int idResidencia) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM pagamento_condominio WHERE id_residencia = ? AND pago = false) AS existe_pendente";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResidencia);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean existePendente = rs.getBoolean("existe_pendente");
                    boolean emDia = !existePendente;

                    // Atualiza o campo em_dia na tabela residencia
                    String updateSql = "UPDATE residencia SET em_dia = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setBoolean(1, emDia);
                        updateStmt.setInt(2, idResidencia);
                        updateStmt.executeUpdate();
                    }

                    return emDia;
                }
            }
        }

        // Se der algum problema, considera que não está em dia
        return false;
    }

    public Map<String, Object> buscarResidenciaPorId(int idResidencia) throws SQLException {
        // Método simples, só delega para DAO
        return residenciaDAO.buscarPorId(idResidencia);
    }

    public void editarResidencia(int idResidencia, String rua, int numero, String cep, Integer idProprietario, boolean emDia) throws SQLException {
        // Apenas chama o DAO
        residenciaDAO.atualizar(idResidencia, rua, numero, cep, idProprietario, emDia);
    }
    
    public void deletarResidencia(int idResidencia) throws SQLException {
        residenciaDAO.deletarResidencia(idResidencia);
    }

}
