package edu.upc.prop.clusterxx.presentation.windows;

import javax.swing.*;
import java.awt.*;

/**
 * @file MainWindow.java
 *
 * @brief Classe que definieix la estructura visual i comportament de la finestra principal de l'aplicació, on es mostraran els diferents panels segons la navegació de l'usuari.
 */
public class MainWindow extends JFrame{
    /**
     * @brief Panel de continguts.
     */
    private JPanel contentPanel;

    /**
     * @brief Funció per a definir la estructura visual de la finestra principal de l'aplicació.
     *
     * @post Es crea la finestra principal del projecte amb l'estructura visual definida.
     */
    public MainWindow() {
        setTitle("Hidato Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Panel principal que cambiará dinámicamente
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        setContentPane(contentPanel);

        setVisible(true);
    }

    /**
     * @brief Funció per a canviar el panel a mostrar en la pantalla principal.
     *
     * @post Es canvia el panel a mostrar en la pantalla principal.
     */
    public void setPanel(JPanel newPanel) {
        contentPanel.removeAll();
        contentPanel.add(newPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
