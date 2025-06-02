package dao;

public class DAOFactory {

    public static MoradorDAO criarMoradorDAO() {
        return new MoradorDAO();
    }

    public static MoradorResidenciaDAO criarMoradorResidenciaDAO() {
        return new MoradorResidenciaDAO();
    }

    public static PagamentoDAO criarPagamentoDAO() {
        return new PagamentoDAO();
    }

    public static PessoaDAO criarPessoaDAO() {
        return new PessoaDAO();
    }

    public static ProprietarioDAO criarProprietarioDAO() {
        return new ProprietarioDAO();
    }

    public static ResidenciaDAO criarResidenciaDAO() {
        return new ResidenciaDAO();
    }

}
