package com.mycompany.condominio;

import javax.swing.SwingUtilities;
import view.main.MainView;

public class Condominio {

    public static void main(String[] args) {
        // Para garantir que a GUI seja criada na EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}
