package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL = "jdbc:mysql://localhost:3306/condominio";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static Connection conexao = null;

    private ConexaoBD() {} // Construtor privado para Singleton

    public static Connection getConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conexao;
    }

    public static void fecharConexao() throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            conexao.close();
        }
    }
}