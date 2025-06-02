package dao;

import model.MoradorResidencia;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MoradorResidenciaDAO {
    public void vincularMorador(MoradorResidencia mr) throws SQLException {
        String sql = "INSERT INTO morador_residencia (id_morador, id_residencia, responsavel, data_vinculo) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mr.getMorador().getId());
            stmt.setInt(2, mr.getResidencia().getId());
            stmt.setBoolean(3, mr.isResponsavel());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    public List<MoradorResidencia> listarMoradoresDaResidencia(int idResidencia) throws SQLException {
        List<MoradorResidencia> vinculos = new ArrayList<>();
        String sql = "SELECT * FROM morador_residencia WHERE id_residencia = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idResidencia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Nota: Morador e Residencia precisam ser carregados separadamente
                    MoradorResidencia mr = new MoradorResidencia(
                        rs.getInt("id"),
                        null, // Morador (carregar depois)
                        null, // Residencia (carregar depois)
                        rs.getBoolean("responsavel"),
                        rs.getTimestamp("data_vinculo").toLocalDateTime()
                    );
                    vinculos.add(mr);
                }
            }
        }
        return vinculos;
    }
}