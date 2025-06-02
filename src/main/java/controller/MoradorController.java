package controller;

import dao.DAOFactory;
import dao.MoradorDAO;
import dao.PessoaDAO;
import dao.ProprietarioDAO;
import model.Morador;
import model.Pessoa;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MoradorController {
    private final MoradorDAO moradorDAO;
    private final PessoaDAO pessoaDAO;
    private final ProprietarioDAO proprietarioDAO;

    public MoradorController() {
        this.moradorDAO = DAOFactory.criarMoradorDAO();
        this.pessoaDAO = DAOFactory.criarPessoaDAO();
        this.proprietarioDAO = DAOFactory.criarProprietarioDAO();
    }

    public int cadastrarMorador(Pessoa pessoa) throws SQLException {
        return moradorDAO.cadastrarMorador(pessoa);
    }


    public List<Morador> listarTodosMoradores() throws SQLException {
        return moradorDAO.listarTodos();
    }
    
    public List<Map<String, Object>> listarMoradoresComDetalhes() throws SQLException {
        return moradorDAO.listarMoradoresComDetalhes();
    }
    
    public void vincularMoradorResidencia(int idMorador, int idResidencia, boolean responsavel) throws SQLException {
        moradorDAO.vincularMoradorResidencia(idMorador, idResidencia, responsavel);
    }
    
    public boolean pessoaJaEhMorador(int idPessoa) throws SQLException {
        return moradorDAO.pessoaJaEhMorador(idPessoa);
    }
    
    public boolean pessoaJaEhProprietario(int idPessoa) throws SQLException {
        return moradorDAO.pessoaJaEhProprietario(idPessoa);
    }
    
    public void desvincularResidencia(int idVinculo) throws SQLException {
        moradorDAO.deletarVinculoResidenciaPorId(idVinculo);
    }

    public void deletarMorador(int idMorador) throws SQLException {
        // Buscar o ID da pessoa associada ao morador
        int idPessoa = moradorDAO.buscarIdPessoaPorIdMorador(idMorador);

        // Verificar se essa pessoa é também proprietária
        boolean ehProprietario = proprietarioDAO.pessoaJaEhProprietario(idPessoa);
        System.out.println(idPessoa);
        // Deletar todos os vínculos desse morador com residências
        moradorDAO.deletarTodosVinculosResidencia(idMorador);

        // Deletar o morador
        moradorDAO.deletarMorador(idMorador);

        // Se não for proprietário, deleta os dados da pessoa
        if (!ehProprietario) {
            pessoaDAO.deletarPessoa(idPessoa);
        }
    }
        
    public int buscarIdPessoaPorIdMorador(int idMorador) throws SQLException {
        return moradorDAO.buscarIdPessoaPorIdMorador(idMorador);
    }
}