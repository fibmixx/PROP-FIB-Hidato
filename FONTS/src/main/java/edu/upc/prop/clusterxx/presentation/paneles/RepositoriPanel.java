package edu.upc.prop.clusterxx.presentation.paneles;

import javax.swing.*;
import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * @file RepositoriPanel.java
 *
 * @brief Panel per gestionar repositoris: llistar, crear i esborrar.
 */
public class RepositoriPanel extends JPanel {

    /**
     * @brief Instància del controlador de Presentació
     */
    private CtrlPresentacio ctrlPresentacio;

    /**
     * @brief Llista per a encapsular els repositoris.
     */
    private DefaultListModel<String> listModel;

    /**
     * @brief Llista per encapsular els repositoris.
     */
    private JList<String> repoList;

    /**
     * @brief Camp de text per a introduir el nom del nou repositori.
     */
    private JTextField nomField;

    /**
     * @brief Funció que defineix la estructura visual del panel de repositoris de hidatos.
     *
     * @post Es genera el panel de repositoris amb la estructura visual definida.
     */
    public RepositoriPanel(CtrlPresentacio ctrlPresentacio) {
        this.ctrlPresentacio = ctrlPresentacio;
        setLayout(new BorderLayout(10, 10));
        boolean nightMode = ctrlPresentacio.getNightMode();
        setBackground(nightMode ? new Color(45, 45, 45) : Color.WHITE);

        initComponents();
        carregarRepositoris();
    }

    /**
     * @brief Funció que defineix la estructura visual del panel de repositoris.
     *
     * @post Es genera el panel de rànkings amb la estructura visual definida.
     */
    private void initComponents() {
        boolean nightMode = ctrlPresentacio.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : Color.WHITE;
        Color headerColor = nightMode ? new Color(90, 50, 90) : new Color(255, 105, 180);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerColor);
        JLabel welcomeLabel = new JLabel("Repositori de Hidatos");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        repoList = new JList<>(listModel);
        repoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(repoList);
        scroll.setBorder(BorderFactory.createTitledBorder("Repositoris"));
        add(scroll, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nomField = new JTextField(20);
        JButton crearBtn = new JButton("Crear repositori");
        crearBtn.addActionListener(e -> onCrear());
        JButton borrarBtn = new JButton("Eliminar seleccionat");
        borrarBtn.addActionListener(e -> onEliminar());
        JButton verHidatosBtn = new JButton("Ver Hidatos");
        verHidatosBtn.addActionListener(e -> onVerHidatos());
        JButton tornarBtn = createButton("Menú principal", e -> ctrlPresentacio.irAlMenuPrincipal());


        controls.add(new JLabel("Nom:"));
        controls.add(nomField);
        controls.add(crearBtn);
        controls.add(borrarBtn);
        controls.add(verHidatosBtn);

        JButton compartirBtn = new JButton("Compartir");
        compartirBtn.addActionListener(e -> onCompartir());
        controls.add(compartirBtn);

        southPanel.add(controls, BorderLayout.CENTER);
        southPanel.add(tornarBtn, BorderLayout.EAST);
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * @brief Funció per a carregar la llista de repositoris de l'usuari.
     *
     * @post Deixa carregada la llista de repositoris de l'usuari dins de la pestanya de Repositoris.
     */
    private void carregarRepositoris() {
        //Carregar en background per no bloquejar UI
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                // carregar del disc (pot ser lent)
                try { ctrlPresentacio.carregarRepositorisUsuari(); } catch (Exception ex) { /* ignore */ }
                // obtenim la llista amb detall (inclou el comptador d'hidatos)
                return ctrlPresentacio.llistarRepositorisDetallats();
            }

            @Override
            protected void done() {
                listModel.clear();
                try {
                    List<String> noms = get();
                    if (noms != null) {
                        for (String s : noms) listModel.addElement(s);
                    }
                } catch (Exception e) {
                    // en cas d'error, intentem la llista bàsica
                    List<String> fallback = ctrlPresentacio.llistarRepositoris();
                    if (fallback != null) for (String s : fallback) listModel.addElement(s);
                }
            }
        };
        worker.execute();
    }

    /**
     * @brief Funció per afegir la llògica del botó de crear repositoris.
     *
     * @post Es crea el botó o dona error i el missatge d'error corresponent. En cas de crear, es recarrega la llista de repositoris.
     */
    private void onCrear() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introdueix un nom vàlid per al repositori.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Crear en background per no bloquejar UI
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ctrlPresentacio.crearRepositori(nom);
                return null;
            }

            @Override
            protected void done() {
                nomField.setText("");
                carregarRepositoris();
            }
        };
        worker.execute();
    }

    /**
     * @brief Funció per afegir la llògica del botó de eliminar repositoris.
     *
     * @post S'elimina el botó o dona error i el missatge d'error corresponent. En cas d'eliminar, es recarrega la llista de repositoris.
     */
    private void onEliminar() {
        String sel = repoList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un repositori per esborrar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Estàs segur que vols eliminar: " + sel + " ?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id;
        try {
            String[] parts = sel.split(" - ", 2);
            id = Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No s'ha pogut extreure l'ID del repositori seleccionat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ctrlPresentacio.eliminarRepositori(id);
                return null;
            }

            @Override
            protected void done() {
                carregarRepositoris();
            }
        };
        worker.execute();
    }

    /**
     * @brief Funció per afegir la llògica del botó de veure els hidatos d'un repositori.
     *
     * @post Es mostra la llista de hidatos del repositori seleccionat o dona error si no n'hi ha cap seleccionat.
     */
    private void onVerHidatos() {
        String sel = repoList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un repositori per veure els seus hidatos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id;
        try {
            String[] parts = sel.split(" - ", 2);
            id = Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No s'ha pogut extreure l'ID del repositori seleccionat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ctrlPresentacio.irAlLlistaHidatos(id);
    }

    /**
     * @brief Lògica per a demanar un usuari i compartir el repositori seleccionat de manera seqüencial.
     */
    private void onCompartir() {
        String sel = repoList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un repositori per compartir.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idRepo = extraerIdSeleccionado(sel);
        if (idRepo == -1) return;

        String nomUser = JOptionPane.showInputDialog(this, "Introdueix el nom de l'usuari amb qui vols compartir:", "Compartir Repositori", JOptionPane.QUESTION_MESSAGE);
        if (nomUser == null) return;
        nomUser = nomUser.trim();

        if (nomUser.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nom d'usuari no pot estar buit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            ctrlPresentacio.compartirRepositori(idRepo, nomUser);
            JOptionPane.showMessageDialog(this, "Repositori compartit correctament amb '" + nomUser + "'!", "Èxit", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al compartir: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Auxiliar para extraer de forma segura el ID numérico del string de la lista.
     */
    private int extraerIdSeleccionado(String sel) {
        try {
            String[] parts = sel.split(" - ", 2);
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No s'ha pogut extreure l'ID del repositori seleccionat.", "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    /**
     * @brief Funció per a crear un botó interactuable al panel.

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
}
