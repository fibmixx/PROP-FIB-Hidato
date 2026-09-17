package edu.upc.prop.clusterxx.presentation.paneles;

import javax.swing.*;

import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;
import java.awt.*;

/**
 * @file MenuPrincipalPanel.java
 *
 * @brief Classe per a definir la estructura visual i comportament del panel del menú principal.
 */
public class MenuPrincipalPanel extends JPanel{
    /**
     * @brief Instància del controlador de presentació.
     */
    private CtrlPresentacio ctrlPresentacio;

    /**
     * @brief Booleà per saber si el programa està en mode oscur.
     */
    private boolean nightMode;

    /**
     * @brief Funció que defineix la estructura visual del panel del menú principal.
     *
     * @post Es genera el panel del menú principal amb la estructura visual definida.
     */
    public MenuPrincipalPanel() {
        this.ctrlPresentacio = CtrlPresentacio.getInstance();
        this.nightMode = ctrlPresentacio.getNightMode();

        setLayout(new BorderLayout(10, 10));
        setBackground(nightMode ? new Color(45, 45, 45) : Color.WHITE);

        initComponents();
    }

    /**
     * @brief Funció que inicialitza els components visuals del panel del menú principal.
     *
     * @post Incialitza els components visuals del panel del menú principal, incloent el header de benvinguda i els botons d'opcions.
     */
    private void initComponents() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(nightMode ? new Color(90, 50, 90) : new Color(255, 105, 180));
        JLabel welcomeLabel = new JLabel(
                "Benvingut " + ctrlPresentacio.getCurrentUser().getUsername() + "!"
        );
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);

        JButton modeBtn = createButton(
                nightMode ? "Mode Diürn" : "Mode Fosc",
                e -> ctrlPresentacio.toggleNightMode()
        );
        modeBtn.setBackground(nightMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
        modeBtn.setForeground(nightMode ? Color.WHITE : Color.DARK_GRAY);
        headerPanel.add(modeBtn);

        add(headerPanel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        optionsPanel.setBackground(nightMode ? new Color(45, 45, 45) : Color.WHITE);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton playBtn = createButton("Jugar", e -> ctrlPresentacio.irAlHidatoGame(10, 10));
        optionsPanel.add(playBtn);

        JButton genBtn = createButton("Generació d'Hidatos", e -> ctrlPresentacio.irAlGenerador());
        optionsPanel.add(genBtn);

        JButton rankingBtn = createButton("Rankings", e -> ctrlPresentacio.irAlRanking());
        optionsPanel.add(rankingBtn);

        JButton repoBtn = createButton("Repositoris", e -> ctrlPresentacio.irAlRepositori());
        optionsPanel.add(repoBtn);
        
        if (ctrlPresentacio.getCurrentUser().isAdmin()) {
            JButton adminBtn = createButton("Administrador", e -> ctrlPresentacio.irAlAdministrador());
            optionsPanel.add(adminBtn);
        }

        JButton logoutBtn = createButton("Tancar Sessió", e -> ctrlPresentacio.logout());
        optionsPanel.add(logoutBtn);

        add(optionsPanel, BorderLayout.CENTER);
    }

    /**
     * @brief Funció per a crear un botó interactuable al menú principal.
     *
     * @param text Text que tindrà el botó i que indica que fa.
     * @param listener Event que espera el botó.
     *
     * @return Retorna el botó creat.
     */
    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        if (nightMode) {
            btn.setBackground(new Color(70, 70, 70));
            btn.setForeground(Color.WHITE);
        }
        btn.addActionListener(listener);
        return btn;
    }
}
