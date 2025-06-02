package controller;

import dao.DAOFactory;
import dao.PessoaDAO;
import dao.ConexaoBD; // pode continuar usando direto para listagem simples, se quiser
import java.sql.Connection;
import java.sql.ResultSet;
import model.Pessoa;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PessoaController {
    private final PessoaDAO pessoaDAO;

    public PessoaController() {
        this.pessoaDAO = DAOFactory.criarPessoaDAO(); // <- Aqui a mudança
    }

    public void cadastrarPessoa(String nome, LocalDate dataNasc, String rg, String cpf) throws SQLException {
        Pessoa pessoa = new Pessoa(nome, dataNasc, rg, cpf);
        pessoaDAO.salvar(pessoa);
    }

    public List<Map<String, Object>> listarPessoas() throws SQLException {
        List<Map<String, Object>> pessoas = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, data_nasc FROM pessoa";

        try (Connection conn = ConexaoBD.getConexao();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> pessoa = new HashMap<>();
                pessoa.put("id", rs.getInt("id"));
                pessoa.put("nome", rs.getString("nome"));
                pessoa.put("cpf", rs.getString("cpf"));
                pessoa.put("data_nasc", rs.getDate("data_nasc").toLocalDate());
                pessoas.add(pessoa);
            }
        }
        return pessoas;
    }

    public List<String> listarNomesDePessoas() throws SQLException {
        return pessoaDAO.listarTodos().stream()
            .map(Pessoa::getNome)
            .collect(Collectors.toList());
    }

    public Pessoa buscarPorId(int id) throws SQLException {
        return pessoaDAO.buscarPorId(id);
    }

    public Pessoa buscarPorNome(String nome) throws SQLException {
        List<Pessoa> pessoas = pessoaDAO.listarTodos();
        return pessoas.stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    public List<Pessoa> listarTodos() throws SQLException {
        return pessoaDAO.listarTodos();
    }

    public Pessoa buscarPessoaPorId(int idPessoa) throws SQLException {
        return pessoaDAO.buscarPorId(idPessoa);
    }
    
    public Pessoa buscarPessoaPorIdMorador(int idMorador) throws SQLException {
        return pessoaDAO.buscarPessoaPorIdMorador(idMorador);
    }

    public void editarPessoa(int idPessoa, String nome, LocalDate dataNasc, String rg, String cpf) throws SQLException {
        pessoaDAO.atualizarPessoa(idPessoa, nome, dataNasc, rg, cpf);
    }

    public Pessoa buscarUltimaPessoa() throws SQLException {
        return pessoaDAO.buscarUltimaPessoa();
    }
}
