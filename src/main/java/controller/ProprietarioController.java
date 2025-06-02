package controller;

import dao.DAOFactory;
import dao.MoradorDAO;
import dao.ProprietarioDAO;
import dao.PessoaDAO;
import dao.ResidenciaDAO;
import model.Pessoa;
import model.Proprietario;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProprietarioController {
    private final ProprietarioDAO proprietarioDAO;
    private final PessoaDAO pessoaDAO;
    private final ResidenciaDAO residenciaDAO;
    private final MoradorDAO moradorDAO;

    public ProprietarioController() {
        this.proprietarioDAO = DAOFactory.criarProprietarioDAO();
        this.pessoaDAO = DAOFactory.criarPessoaDAO();
        this.residenciaDAO = DAOFactory.criarResidenciaDAO();
        this.moradorDAO = DAOFactory.criarMoradorDAO();
    }

    public void cadastrarProprietario(Pessoa pessoa) throws SQLException {
        Proprietario proprietario = new Proprietario(0, pessoa, LocalDateTime.now());
        proprietarioDAO.salvar(proprietario);
    }

    // Exemplo: Filtrar proprietários com algum critério usando lambda (se quiser)
    public List<Proprietario> listarProprietarios() throws SQLException {
        List<Proprietario> todos = proprietarioDAO.listarTodos();
        // Por exemplo, filtrar apenas proprietários ativos (se tivesse um atributo ativo)
        // Como não tem, só retorna tudo mesmo
        return todos.stream()
                //.filter(p -> p.isAtivo()) // Exemplo
                .collect(Collectors.toList());
    }

    public Proprietario buscarPorId(int id) throws SQLException {
        return proprietarioDAO.buscarPorId(id);
    }

    public Proprietario buscarPessoaPorId(int id) throws SQLException {
        return proprietarioDAO.buscarPorId(id);
    }

    public List<Map<String, Object>> listarProprietariosComDados() throws SQLException {
        return proprietarioDAO.listarProprietariosComResidencia();
    }

    public List<Map<String, Object>> listarProprietariosComDadosDistinct() throws SQLException {
        return proprietarioDAO.listarProprietariosComDadosDistinct();
    }

    public List<Map<String, Object>> listarProprietariosComEndereco() throws SQLException {
        return proprietarioDAO.listarProprietariosComEndereco();
    }

    public boolean pessoaJaEhProprietario(int idPessoa) throws SQLException {
        return proprietarioDAO.pessoaJaEhProprietario(idPessoa);
    }

    public void deletarProprietario(int idProprietario) throws SQLException {
        int idPessoa = proprietarioDAO.buscarIdPessoaPorProprietario(idProprietario);
        boolean isMorador = moradorDAO.pessoaJaEhMorador(idPessoa);

        if (isMorador) {
            proprietarioDAO.deletarProprietarioPorId(idProprietario);
        } else {
            proprietarioDAO.deletarProprietarioPorId(idProprietario);
            pessoaDAO.deletarPessoa(idPessoa);
        }
    }

    public boolean isMorador(int idPessoa) throws SQLException {
        return moradorDAO.pessoaJaEhMorador(idPessoa);
    }

    public void excluirProprietario(int idProprietario) throws SQLException {
        proprietarioDAO.deletarProprietarioPorId(idProprietario);
    }

    public void excluirPessoa(int idPessoa) throws SQLException {
        pessoaDAO.deletarPessoa(idPessoa);
    }

    public void desvincularProprietarioDasResidencias(int idProprietario) throws SQLException {
        residenciaDAO.desvincularProprietario(idProprietario);
    }
}
