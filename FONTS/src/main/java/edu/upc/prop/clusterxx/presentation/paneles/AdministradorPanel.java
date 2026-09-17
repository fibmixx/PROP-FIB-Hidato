package edu.upc.prop.clusterxx.presentation.paneles;

import javax.swing.*;
import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;
import edu.upc.prop.clusterxx.presentation.windows.UserSelectionWindow;
import java.awt.*;
import java.util.List;

/**
 * @file AdministradorPanel.java
 *
 * @brief Classe per a definir la estructura visual i comportament del panel d'administrador.
 */
public class AdministradorPanel extends JPanel{

    /**
     * @brief Instància del controlador de la capa de presentació.
     */
    private CtrlPresentacio ctrlPresentacio;

    /**
     * @brief Funció que defineix la estructura visual del panel d'administrador.
     *
     * @post Es genera el panel d'administrador amb la estructura visual definida.
     */
    public AdministradorPanel(CtrlPresentacio ctrlPresentacio) {
        this.ctrlPresentacio = ctrlPresentacio;

        setLayout(new BorderLayout(10, 10));
        boolean nightMode = ctrlPresentacio.getNightMode();
        setBackground(nightMode ? new Color(45, 45, 45) : Color.WHITE);

        initCompontents();
    }

    /**
     * @brief Funció que defineix la estructura visual del panel d'administrador.
     *
     * @post Es genera el panel d'administrador amb la estructura visual definida.
     */
    private void initCompontents() {
        boolean nightMode = ctrlPresentacio.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : Color.WHITE;
        Color headerColor = nightMode ? new Color(90, 50, 90) : new Color(255, 105, 180);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerColor);
        JLabel welcomeLabel = new JLabel(
                "Administrador"
        );
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        optionsPanel.setBackground(bgColor);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton esborraUsuaris = createButton("Esborra Usuaris", e -> eliminaUsuaris());
        optionsPanel.add(esborraUsuaris);

        JButton netejaRankingGlob = createButton("Esborra Rànking Global", e -> ctrlPresentacio.netejarRankingGlobal());
        optionsPanel.add(netejaRankingGlob);

       add(optionsPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton tornarBtn = createButton("Menú principal", e -> ctrlPresentacio.irAlMenuPrincipal());
        buttonPanel.add(tornarBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * @brief Funció per a crear un botó interactuable al panel.
     *
     * @param text Text que tindrà el botó i que indica que fa.
     * @param listener Event que espera el botó.
     *
     * @return Retorna el botó creat.
     */
    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.addActionListener(listener);
        return btn;
    }

    /**
     * @brief Funció per a esborrar usuaris existents
     * 
     * @post Si a la finestra que s'obra s'escull algun usuari, s'esborra.
     */
    private void eliminaUsuaris() {
        List<String> usuariosDisponibles = ctrlPresentacio.obtenirLlistaUsuaris();

        if (usuariosDisponibles == null || usuariosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hi ha usuaris disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserSelectionWindow selectionWindow = new UserSelectionWindow(
            SwingUtilities.getWindowAncestor(this),
            ctrlPresentacio.getCurrentUser().getUsername(),
            usuariosDisponibles
        );

        if (selectionWindow.isConfirmed()) {
            List<String> usuariosSeleccionats = selectionWindow.getSelectedUsers();

            if (usuariosSeleccionats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No vas seleccionar cap usuari. El rànking no es va crear.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            //Eliminar usuaris
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    for (String user: usuariosSeleccionats)
                        ctrlPresentacio.esborrarUsuari(user);
                    return null;
                }

                @Override
                protected void done() {
                }
            };
            worker.execute();
        }
    }
}
