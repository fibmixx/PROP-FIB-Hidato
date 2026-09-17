package edu.upc.prop.clusterxx.presentation.paneles;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;
import edu.upc.prop.clusterxx.domain.enumerations.TipusDificultat;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * @file GeneradorPanel.java
 * @brief Definició i implementació de la classe GeneradorPanel.
 * Panel per a generar hidatos automàticament i carregar-los a un repositori.
 * Disseny de primer nivell amb dues seccions verticals centrades de manera elegant.
 */
public class GeneradorPanel extends JPanel {

    /**
     * @brief Instància del controlador de la capa de presentació.
     */
    private CtrlPresentacio ctrlPresentacio;

    /**
     * @brief Components per a la secció de generació.
     */
    private JComboBox<String> dificultatCombo;
    private JComboBox<String> adjacenciaCombo;
    private JButton generarBtn;

    /**
     * @brief Components per a la secció de pujada/guardat.
     */
    private JComboBox<String> repoComboBox;
    private JButton pujarBtn;

    /**
     * @brief Indica si s'ha generat un hidato.
     */
    private boolean hidatoGenerat = false;

    /**
     * @brief Constructor del panel generador.
     * 
     * @param ctrlPresentacio Instància del controlador de la capa de presentació.
     */
    public GeneradorPanel(CtrlPresentacio ctrlPresentacio) {
        this.ctrlPresentacio = ctrlPresentacio;
        setLayout(new BorderLayout(15, 15));
        boolean nightMode = ctrlPresentacio.getNightMode();
        setBackground(nightMode ? new Color(45, 45, 45) : Color.WHITE);

        initComponents();
        carregarRepositoris();
    }

    /**
     * @brief Inicialitza tots els components visuals.
     * 
     * @post Es genera el panell generador amb la estructura visual definida.
     */
    private void initComponents() {
        boolean nightMode = ctrlPresentacio.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : Color.WHITE;
        Color headerColor = nightMode ? new Color(90, 50, 90) : new Color(255, 105, 180);
        Color borderColor = nightMode ? new Color(80, 80, 80) : new Color(230, 230, 230);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerColor);
        JLabel welcomeLabel = new JLabel("Generador Automàtic d'Hidatos");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainCentral = new JPanel(new GridBagLayout());
        mainCentral.setBackground(bgColor);
        mainCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel controlsContainer = new JPanel();
        controlsContainer.setLayout(new BoxLayout(controlsContainer, BoxLayout.Y_AXIS));
        controlsContainer.setBackground(bgColor);
        controlsContainer.setPreferredSize(new Dimension(450, 415));
        controlsContainer.setMaximumSize(new Dimension(450, 415));

        JPanel creacioPanel = new JPanel(new GridBagLayout());
        creacioPanel.setBackground(bgColor);
        creacioPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                "1. Paràmetres de Creació",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                headerColor
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel difLabel = new JLabel("Dificultat:");
        difLabel.setFont(new Font("Arial", Font.BOLD, 13));
        if (nightMode) difLabel.setForeground(Color.WHITE);
        creacioPanel.add(difLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        dificultatCombo = new JComboBox<>(new String[]{"Fàcil", "Mitjà", "Difícil"});
        dificultatCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        creacioPanel.add(dificultatCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel adjLabel = new JLabel("Adjacència:");
        adjLabel.setFont(new Font("Arial", Font.BOLD, 13));
        if (nightMode) adjLabel.setForeground(Color.WHITE);
        creacioPanel.add(adjLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        adjacenciaCombo = new JComboBox<>(new String[]{
            "Quadrat (Aresta)", 
            "Quadrat amb Diagonals", 
            "Triangle", 
            "Hexàgon"
        });
        adjacenciaCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        creacioPanel.add(adjacenciaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        generarBtn = crearBoto("Generar Hidato Aleatori", e -> onGenerarHidato());
        generarBtn.setBackground(new Color(255, 105, 180));
        generarBtn.setForeground(Color.WHITE);
        generarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        creacioPanel.add(generarBtn, gbc);

        gbc.gridy = 3;
        JButton crearManualBtn = crearBoto("Generar Hidato Custom", e -> onCrearHidatoManual());
        crearManualBtn.setBackground(new Color(155, 89, 182));
        crearManualBtn.setForeground(Color.WHITE);
        crearManualBtn.setFont(new Font("Arial", Font.BOLD, 14));
        creacioPanel.add(crearManualBtn, gbc);

        gbc.gridy = 4;
        JButton introduirBtn = crearBoto("Introduir Hidato Manualment", e -> onIntroduirHidato());
        introduirBtn.setBackground(new Color(255, 165, 0));
        introduirBtn.setForeground(Color.WHITE);
        introduirBtn.setFont(new Font("Arial", Font.BOLD, 14));
        creacioPanel.add(introduirBtn, gbc);


        controlsContainer.add(creacioPanel);
        controlsContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel pujadaPanel = new JPanel(new GridBagLayout());
        pujadaPanel.setBackground(bgColor);
        pujadaPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                "2. Desa al Repositori",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                headerColor
        ));

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(10, 10, 10, 10);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        gbc2.gridx = 0; gbc2.gridy = 0; gbc2.weightx = 0.3;
        JLabel repoLabel = new JLabel("Repositori:");
        repoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        if (nightMode) repoLabel.setForeground(Color.WHITE);
        pujadaPanel.add(repoLabel, gbc2);

        gbc2.gridx = 1; gbc2.weightx = 0.7;
        repoComboBox = new JComboBox<>();
        repoComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        pujadaPanel.add(repoComboBox, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1; gbc2.gridwidth = 2; gbc2.weightx = 1.0;
        pujarBtn = crearBoto("Pujar al Repositori", e -> onPujarRepositori());
        pujarBtn.setBackground(new Color(46, 204, 113));
        pujarBtn.setForeground(Color.WHITE);
        pujarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        pujarBtn.setEnabled(false);
        pujadaPanel.add(pujarBtn, gbc2);

        controlsContainer.add(pujadaPanel);

        mainCentral.add(controlsContainer);
        add(mainCentral, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(bgColor);
        JButton tornarBtn = crearBoto("Menú principal", e -> ctrlPresentacio.irAlMenuPrincipal());
        footerPanel.add(tornarBtn);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 20));
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * @brief Funció per a carregar els repositoris existents.
     * 
     * @post Es generen els repositoris de l'usuari actual a la llista.
     */
    private void carregarRepositoris() {
        repoComboBox.removeAllItems();
        List<String> repos = ctrlPresentacio.llistarRepositoris();
        if (repos != null && !repos.isEmpty()) {
            for (String r : repos) {
                repoComboBox.addItem(r);
            }
            if (hidatoGenerat) {
                pujarBtn.setEnabled(true);
            }
        } else {
            repoComboBox.addItem("No tens cap repositori creat");
            pujarBtn.setEnabled(false);
        }
    }

    /**
     * @brief Funció que s'executa quan es fa clic sobre el botó de Generar Hidato.
     * 
     * @post Es genera un hidato amb la dificultat i adjacència seleccionades.
     */
    private void onGenerarHidato() {
        String difText = (String) dificultatCombo.getSelectedItem();
        TipusDificultat dificultat = TipusDificultat.FACIL;
        if ("Mitjà".equals(difText)) {
            dificultat = TipusDificultat.MITJA;
        } else if ("Difícil".equals(difText)) {
            dificultat = TipusDificultat.DIFICIL;
        }

        String adjText = (String) adjacenciaCombo.getSelectedItem();
        String adj = "quadrat";
        int num_mapa = 0;
        if (adjText.contains("Diagonals")) {
            adj = "quadrat_d";
        } else if (adjText.contains("Triangle")) {
            adj = "triangle";
            num_mapa = 2;
        } else if (adjText.contains("Hexàgon")) {
            adj = "hexagon";
            num_mapa = 1;
        }

        final TipusDificultat difFinal = dificultat;
        final String adjFinal = adj;
        final int finalNumMapa = num_mapa;

        generarBtn.setText("Generant...");
        generarBtn.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ctrlPresentacio.generarNouHidato(difFinal, adjFinal, finalNumMapa);
                return null;
            }

            @Override
            protected void done() {
                generarBtn.setText("Generar Hidato");
                generarBtn.setEnabled(true);
                try {
                    get();
                    hidatoGenerat = true;
                    carregarRepositoris();
                    JOptionPane.showMessageDialog(GeneradorPanel.this, 
                            "Hidato generat amb èxit! Selecciona un repositori a continuació per a desar-lo.", 
                            "Èxit", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GeneradorPanel.this, 
                            "Error generant l'Hidato: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * @brief Funció que s'executa quan es fa clic sobre el botó de Desar al Repositori.
     * 
     * @post Es desa l'hidato al repositori seleccionat.
     */
    private void onPujarRepositori() {
        String selRepo = (String) repoComboBox.getSelectedItem();
        if (selRepo == null || selRepo.equals("No tens cap repositori creat")) {
            JOptionPane.showMessageDialog(this, 
                    "Siusplau, crea primer un repositori a la secció de Repositoris.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int repoId = Integer.parseInt(selRepo.split(" - ", 2)[0].trim());
            ctrlPresentacio.guardarHidatoEnRepositori(repoId);
            
            JOptionPane.showMessageDialog(this, 
                    "Hidato desat correctament al repositori: " + selRepo, 
                    "Èxit", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error desant l'Hidato: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Funció que s'executa quan es fa clic sobre el botó de Crear Hidato Manual.
     * 
     * @post Obre un diàleg per a configurar les dimensions i adjacència del tauler manual.
     */
    private void onCrearHidatoManual() {
        JDialog dimDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Configurar Hidato Custom", true);
        dimDialog.setLayout(new GridBagLayout());
        dimDialog.getContentPane().setBackground(Color.WHITE);
        dimDialog.setSize(400, 320);
        dimDialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel rowsLabel = new JLabel("Files (2-20):");
        rowsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(rowsLabel, gbc);

        gbc.gridx = 1;
        JTextField rowsField = new JTextField("", 10);
        rowsField.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(rowsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel colsLabel = new JLabel("Columnes (2-20):");
        colsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(colsLabel, gbc);

        gbc.gridx = 1;
        JTextField colsField = new JTextField("", 10);
        colsField.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(colsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel maxLabel = new JLabel("Màx. Caselles:");
        maxLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(maxLabel, gbc);

        gbc.gridx = 1;
        JTextField maxField = new JTextField("", 10);
        maxField.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(maxField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel probLabel = new JLabel("Prob. Amagar (0.0-1.0):");
        probLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(probLabel, gbc);

        gbc.gridx = 1;
        JTextField probField = new JTextField("", 10);
        probField.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(probField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel adjLabel = new JLabel("Adjacència:");
        adjLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(adjLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> adjCombo = new JComboBox<>(new String[]{
            "Quadrat (Aresta)", 
            "Quadrat amb Diagonals", 
            "Triangle", 
            "Hexàgon"
        });
        adjCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(adjCombo, gbc);

        adjCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(adjCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel tipusLabel = new JLabel("Tipus Mapa:");
        tipusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(tipusLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> tipusMapaCombo = new JComboBox<>(new String[]{
            "Rectangular", 
            "Hexagonal",
            "Triangular"
        });

        tipusMapaCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(tipusMapaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        JButton cancelBtn = crearBoto("Cancel·lar", e -> dimDialog.dispose());
        JButton generateBtn = crearBoto("Generar Custom", null);
        generateBtn.setBackground(new Color(155, 89, 182));
        generateBtn.setForeground(Color.WHITE);
        btnPanel.add(cancelBtn);
        btnPanel.add(generateBtn);
        dimDialog.add(btnPanel, gbc);

        generateBtn.addActionListener(e -> {
            String rText = rowsField.getText().trim();
            String cText = colsField.getText().trim();
            String maxText = maxField.getText().trim();
            String probText = probField.getText().trim();

            if (rText.isEmpty() || cText.isEmpty() || maxText.isEmpty() || probText.isEmpty()) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "Tots els camps són obligatoris.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int rows, cols, max_tiles;
            double prob;
            try {
                rows = Integer.parseInt(rText);
                cols = Integer.parseInt(cText);
                max_tiles = Integer.parseInt(maxText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "Files, columnes i màxim de caselles han de ser números enters.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                prob = Double.parseDouble(probText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "La probabilitat d'amagar ha de ser un número decimal (ex: 0.5).", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (rows < 2 || rows > 20 || cols < 2 || cols > 20) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "Les dimensions de files i columnes han d'estar entre 2 i 20.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (max_tiles <= 0 || max_tiles > (rows * cols)) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "El màxim de caselles ha de ser major que 0 i com a màxim " + (rows * cols) + ".", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (prob < 0.0 || prob > 1.0) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "La probabilitat d'amagar ha d'estar entre 0.0 i 1.0.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String adjText = (String) adjCombo.getSelectedItem();
            String adj = "quadrat";
            if (adjText.contains("Diagonals")) {
                adj = "quadrat_d";
            } else if (adjText.contains("Triangle")) {
                adj = "triangle";
            } else if (adjText.contains("Hexàgon")) {
                adj = "hexagon";
            }

            if (adj.equals("triangle") && rows != cols) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "Si vols generar un Hidato triangular, el tauler ha de ser quadrat (files = columnes).", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int num_mapa = 0;
            String tipusMapaText = (String) tipusMapaCombo.getSelectedItem();
            if (tipusMapaText.contains("Hexagonal")) {
                num_mapa = 1;
            } else if (tipusMapaText.contains("Triangular")) {
                num_mapa = 2;
            }

            if (num_mapa == 1 && adj.equals("triangle")) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "No es pot generar un Hidato amb tiles triangulars i tauler hexagonal.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } 
            else if (num_mapa == 2 && adj.equals("hexagon")) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "No es pot generar un Hidato amb tiles hexagonal i tauler triangular.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if ((adj.equals("quadrat") || adj.equals("quadrat_d")) && num_mapa != 0) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "No es pot generar un Hidato amb tiles quadrades i tauler no rectangular.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (adj.equals("triangle") && rows != cols) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "Si vols generar un Hidato triangular, el tauler ha de ser quadrat (files = columnes).", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dimDialog.dispose();

            generarBtn.setText("Generant...");
            generarBtn.setEnabled(false);

            final int finalRows = rows;
            final int finalCols = cols;
            final int finalMax = max_tiles;
            final double finalProb = prob;
            final String finalAdj = adj;
            final int finalMapa = num_mapa;


            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        ctrlPresentacio.generarNouHidatoCustom(finalRows, finalCols, finalMax, finalProb, finalAdj, finalMapa);
                    } catch (Throwable ex) {
                        throw new Exception(ex);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    generarBtn.setText("Generar Hidato");
                    generarBtn.setEnabled(true);
                    try {
                        get();
                        hidatoGenerat = true;
                        carregarRepositoris();
                        JOptionPane.showMessageDialog(GeneradorPanel.this, 
                                "Hidato personalitzat generat amb èxit! És completable i vàlid.\n"
                                + "Selecciona un repositori a continuació per a desar-lo.", 
                                "Èxit", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(GeneradorPanel.this, 
                                "Error generant/validant l'Hidato: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });

        dimDialog.setVisible(true);
    }



    /**
     * @brief Funció que s'executa quan es fa clic sobre el botó d'Introduir Hidato.
     * 
     * @post Obre un diàleg per a configurar les dimensions i adjacència del tauler manual.
     */
    private void onIntroduirHidato() {
        JDialog dimDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Configurar Hidato Manual", true);
        dimDialog.setLayout(new GridBagLayout());
        dimDialog.getContentPane().setBackground(Color.WHITE);
        dimDialog.setSize(380, 250);
        dimDialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel rowsLabel = new JLabel("Files (2-20):");
        rowsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(rowsLabel, gbc);

        gbc.gridx = 1;
        JTextField rowsField = new JTextField("", 10);
        rowsField.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(rowsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel colsLabel = new JLabel("Columnes (2-20):");
        colsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(colsLabel, gbc);

        gbc.gridx = 1;
        JTextField colsField = new JTextField("", 10);
        colsField.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(colsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel adjLabel = new JLabel("Adjacència:");
        adjLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dimDialog.add(adjLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> adjCombo = new JComboBox<>(new String[]{
            "Quadrat (Aresta)", 
            "Quadrat amb Diagonals", 
            "Triangle", 
            "Hexàgon"
        });
        adjCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        dimDialog.add(adjCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        JButton cancelBtn = crearBoto("Cancel·lar", e -> dimDialog.dispose());
        JButton nextBtn = crearBoto("Següent", null);
        nextBtn.setBackground(new Color(255, 105, 180));
        nextBtn.setForeground(Color.WHITE);
        btnPanel.add(cancelBtn);
        btnPanel.add(nextBtn);
        dimDialog.add(btnPanel, gbc);

        nextBtn.addActionListener(e -> {
            String rText = rowsField.getText().trim();
            String cText = colsField.getText().trim();

            if (rText.isEmpty() || cText.isEmpty()) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "Els camps de files i columnes no poden estar buits.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int rows, cols;
            try {
                rows = Integer.parseInt(rText);
                cols = Integer.parseInt(cText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "Siusplau, introdueix números enters vàlids per a les files i columnes.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (rows < 2 || rows > 20 || cols < 2 || cols > 20) {
                JOptionPane.showMessageDialog(dimDialog, 
                        "Les dimensions han d'estar entre 2 i 20.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String adjText = (String) adjCombo.getSelectedItem();
            String adj = "quadrat";
            if (adjText.contains("Diagonals")) {
                adj = "quadrat_d";
            } else if (adjText.contains("Triangle")) {
                adj = "triangle";
            } else if (adjText.contains("Hexàgon")) {
                adj = "hexagon";
            }
            dimDialog.dispose();
            obrirEditorGrid(rows, cols, adj);
        });

        dimDialog.setVisible(true);
    }

    /**
     * @brief Obre el diàleg interactiu per introduir els valors del tauler cel·la per cel·la.
     * @param rows Nombre de files del tauler.
     * @param cols Nombre de columnes del tauler.
     * @param adj Adjacència del tauler.
     * @post Obre un diàleg per introduir els valors del tauler. Si l'usuari introdueix valors vàlids, el tauler es crearà i es validarà. Si hi ha un error, es mostrarà un missatge d'error.
     */
    private void obrirEditorGrid(int rows, int cols, String adj) {
        JDialog gridDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Introduir Caselles de l'Hidato (" + rows + "x" + cols + ")", true);
        gridDialog.setLayout(new BorderLayout(10, 10));
        gridDialog.getContentPane().setBackground(Color.WHITE);
        
        JLabel helpLabel = new JLabel("<html><div style='padding: 10px; text-align: center;'>"
            + "<b>Instruccions:</b> '?' casella buida | '#' fora del tauler | '*' forat bloquejat<br>"
            + "O un número (ex: 1, 2, ...)"
            + "</div></html>", SwingConstants.CENTER);
        helpLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gridDialog.add(helpLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField[][] fields = new JTextField[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JTextField tf = new JTextField("?", 3);
                tf.setHorizontalAlignment(JTextField.CENTER);
                tf.setFont(new Font("Arial", Font.BOLD, 14));
                fields[i][j] = tf;
                gridPanel.add(tf);
            }
        }

        int width = Math.max(420, cols * 60 + 40);
        int height = Math.max(320, rows * 45 + 180);
        gridDialog.setSize(width, height);
        gridDialog.setLocationRelativeTo(this);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        gridDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JButton cancelBtn = crearBoto("Cancel·lar", e -> gridDialog.dispose());
        JButton validarBtn = crearBoto("Validar i Carregar", null);
        validarBtn.setBackground(new Color(46, 204, 113));
        validarBtn.setForeground(Color.WHITE);
        
        actionsPanel.add(cancelBtn);
        actionsPanel.add(validarBtn);
        gridDialog.add(actionsPanel, BorderLayout.SOUTH);

        validarBtn.addActionListener(e -> {
            String[][] matrix = new String[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    String val = fields[i][j].getText().trim();
                    if (val.isEmpty()) {
                        val = "?";
                    }
                    matrix[i][j] = val;
                }
            }

            try {
                ctrlPresentacio.carregarIValidarTaulerManual(rows, cols, matrix, adj);
                hidatoGenerat = true;
                carregarRepositoris();
                gridDialog.dispose();
                JOptionPane.showMessageDialog(GeneradorPanel.this, 
                        "Hidato personal validat amb èxit! És completable.\n"
                        + "Selecciona un repositori a continuació per a desar-lo.", 
                        "Èxit", JOptionPane.INFORMATION_MESSAGE);
            } catch (Throwable ex) {
                JOptionPane.showMessageDialog(gridDialog, 
                        "Error de validació: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gridDialog.setVisible(true);
    }


    /**
     * @brief Funció que crea botons amb un disseny uniforme.
     * 
     * @param text Text que tindrà el botó i que indica que fa.
     * @param listener Event que espera el botó.
     *
     * @return Retorna el botó creat.
     */
    private JButton crearBoto(String text, ActionListener listener) {
        JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusable(false);
        btn.addActionListener(listener);
        return btn;
    }
}