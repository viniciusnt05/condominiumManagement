package controller;

import dao.DAOFactory;
import dao.MoradorResidenciaDAO;
import model.MoradorResidencia;
import java.sql.SQLException;

public class MoradorResidenciaController {
    private final MoradorResidenciaDAO moradorResidenciaDAO;

    public MoradorResidenciaController() {
        this.moradorResidenciaDAO = DAOFactory.criarMoradorResidenciaDAO();
    }

    public void vincularMoradorAResidencia(int idMorador, int idResidencia, boolean responsavel) throws SQLException {
        MoradorResidencia mr = new MoradorResidencia(0, null, null, responsavel, null);
        moradorResidenciaDAO.vincularMorador(mr);
    }
}
