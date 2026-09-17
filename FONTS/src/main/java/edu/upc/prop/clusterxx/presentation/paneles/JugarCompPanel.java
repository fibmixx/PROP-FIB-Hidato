package edu.upc.prop.clusterxx.presentation.paneles;
import edu.upc.prop.clusterxx.domain.model.CompetitiveGame;
import edu.upc.prop.clusterxx.domain.model.Game;
import edu.upc.prop.clusterxx.domain.model.Hidato;
import edu.upc.prop.clusterxx.domain.model.Usuari;
import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;
import java.util.List;

/**
 * @file JugarPanel.java
 *
 * @brief Definició i implementació de la classe JugarPanel.
 *
 * Serveix per proporcionar un panel on el jugador pot jugar a un hidato seleccionat, interactuant amb el tauler, comprovant la solució i veient el temps transcorregut.
 */
public class JugarCompPanel extends JPanel {

    /**
     * @brief Panel principal de joc, on es mostra el tauler i es pot interactuar amb ell.
     *
     * Permet al jugador modificar les caselles del tauler, comprovar la solució i veure el temps transcorregut.
     */
    private CtrlPresentacio cp;

    /**
     * @brief Matriu de botons que representen les caselles del tauler.
     */
    private JButton[][] cellButtons;

    /**
     * @brief Etiqueta que mostra el temps transcorregut.
     */
    private JLabel timeLabel;
    /**
     * @brief Timer de Swing per actualitzar el temps cada mig segon. Diferent al timer del Game
     */
    private Timer swingTimer;

    /**
     * @brief Nombre de files i columnes del tauler, i el valor màxim que pot tenir una casella.
     */
    private int numFiles, numCols, numMax;

    /**
     * @brief Creadora de la classe JugarPanel.
     *
     * S'inicialitza el panel amb el tauler del joc seleccionat, es configuren els botons per modificar les caselles i es comença a comptar el temps.
     */
    public JugarCompPanel() {
        this.cp = CtrlPresentacio.getInstance();

        if (cp.getSelectedGame() == null) {
            // CAS HIPOTÈTIC: no hi ha cap joc seleccionat, tornem al menú principal
            setLayout(new BorderLayout());
            add(new JLabel("No hi ha cap joc seleccionat.", SwingConstants.CENTER), BorderLayout.CENTER);
            JButton menuBtnNoGame = new JButton("Menu Principal");
            menuBtnNoGame.addActionListener(e -> cp.irAlMenuPrincipal());
            JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
            south.add(menuBtnNoGame);
            add(south, BorderLayout.SOUTH);
            return;
        }

        this.numFiles = cp.getNumFilesSelectedGame();
        this.numCols = cp.getNumColsSelectedGame();;
        this.numMax = cp.calcularNumMax();

        boolean nightMode = cp.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : null;

        setLayout(new BorderLayout(10,10));
        if (nightMode) setBackground(bgColor);

        JPanel top = new JPanel(new BorderLayout());
        if (nightMode) top.setBackground(bgColor);
        timeLabel = new JLabel("Temps: 0 s");
        if (nightMode) timeLabel.setForeground(Color.WHITE);
        top.add(timeLabel, BorderLayout.WEST);
        JButton resolverBtn = new JButton("Resoldre");
        top.add(resolverBtn, BorderLayout.EAST);
        
        // Panel de botones centrales (Pista y Menú Principal)
        JPanel centerButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton pistaBtn = new JButton("Pista");
        pistaBtn.addActionListener(e -> {
            List<int[]> pistas = cp.aplicarPista();
            if (pistas == null || pistas.isEmpty()) {
                JOptionPane.showMessageDialog(JugarCompPanel.this, "No es possible donar pista.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Actualizar los botones con las pistas
            for (int[] coord : pistas) {
                int fila = coord[0];
                int col = coord[1];
                String nouValor = cp.getTaulerSelectedGame()[fila][col];
                cellButtons[fila][col].setText(nouValor);
            }
            JOptionPane.showMessageDialog(JugarCompPanel.this, "Pista donada: " + pistas.size() + " cela(es) revelada(es).", "Pista", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton menuBtn = new JButton("Menú Principal");
        menuBtn.addActionListener(e -> {
            if (swingTimer != null && swingTimer.isRunning()) swingTimer.stop();
            cp.stopTimer();
            cp.guardarPartidaActual();
            cp.irAlMenuPrincipal();
        });
        JButton resetBtn = new JButton("Reiniciar Tauler");
        resetBtn.addActionListener(e -> {
            cp.resetTaulerSelectedGame();
            String[][] taulerReseteado = cp.getTaulerSelectedGame();

            //actualizar nueva interficie gràfica con el tauler reiniciado
            for (int i = 0; i < numFiles; ++i) {
                for (int j = 0; j < numCols; ++j) {
                    String nuevoValor = taulerReseteado[i][j];

                    // Solo actualizamos las casillas que no sean paredes ('#')
                    if (!nuevoValor.equals("#")) {
                        cellButtons[i][j].setText(nuevoValor);
                    }
                }
            }
            JOptionPane.showMessageDialog(JugarCompPanel.this, "Tauler reiniciat.", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        centerButtons.add(resetBtn);
        centerButtons.add(pistaBtn);
        centerButtons.add(menuBtn);
        top.add(centerButtons, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(numFiles, numCols));
        cellButtons = new TileButton[numFiles][numCols];

        String adjShape = cp.detectAdjacencia(cp.getHidatoSelectedGame());
        String[][] tauler = cp.getTaulerSelectedGame();
        for (int i = 0; i < numFiles; ++i) {
            for (int j = 0; j < numCols; ++j) {
                String val = tauler[i][j];
                TileButton b = new TileButton(val, adjShape, i, j);
                b.setFocusable(true);
                b.setRequestFocusEnabled(true);
                b.setMargin(new Insets(0,0,0,0));
                b.setPreferredSize(new Dimension(72,72));
                if (val.equals("#")) {
                    b.setEnabled(false);
                    b.setBackground(Color.BLACK);
                    b.setForeground(Color.WHITE);
                } else {
                    boolean modificable = cp.esModificable(cp.getHidatoSelectedGame(), i, j);
                    if (!modificable) {
                        b.setEnabled(false);
                    } else {
                        final int fi = i;
                        final int fj = j;
                        b.addActionListener(e -> {
                            String input = JOptionPane.showInputDialog(JugarCompPanel.this, "Introdueix valor (número entre 1 i " + numMax + " o ?):", tauler[fi][fj]);
                            if (input == null) return; // cancelar
                            input = input.trim();
                            if (!validateInput(input)) {
                                JOptionPane.showMessageDialog(JugarCompPanel.this, "Valor invàlid.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (!cp.validarValor(input, numMax)) {
                                JOptionPane.showMessageDialog(JugarCompPanel.this, "Valor fora de rang.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            cp.setTaulerValue(fi, fj, input);
                            b.setText(input);
                        });
                    }
                }
                cellButtons[i][j] = b;
                gridPanel.add(b);
            }
        }

        JScrollPane sp = new JScrollPane(gridPanel);
        add(sp, BorderLayout.CENTER);
        gridPanel.setFocusable(true);
        gridPanel.requestFocusInWindow();

        cp.startTimer();
        swingTimer = new Timer(500, e -> updateTime());
        swingTimer.start();

        resolverBtn.addActionListener(e -> {
            String adj = cp.detectAdjacencia(cp.getHidatoSelectedGame());
            boolean correcto = cp.comprovarSolucio(numFiles, numCols, adj);
            if (correcto) {
                swingTimer.stop();
                cp.stopTimer();
                long temps = cp.getTemps();
                long punts = Math.max(0, 100000 - (int) temps);
                // marcar puntuació del desafíador en el CompetitiveGame i crear el game per al desafiat (NO VA)
                try {
                    CompetitiveGame cg = (CompetitiveGame) cp.getSelectedGame();
                    cg.setPuntuacioDesafiador((int)punts);
                    // crear un nou Game per al usuari desafiat amb el mateix hidato
                    Usuari desafiat = cg.getUsuariDesafiat();
                    if (desafiat != null) {
                        cp.acabarGameSeleccionat((int) punts);
                        cp.createGame(desafiat, cg.getHidatoOriginal());
                        JOptionPane.showMessageDialog(JugarCompPanel.this, "¡Felicidades! Hidato resolt. Puntuació: " + punts + " (Temps: " + (temps / 1000) + " s)" +  "\nS'ha creat el repte per al jugador desafiat: " + desafiat.getUsername());
                    } else {
                        cp.acabarGameSeleccionat((int) punts);
                        JOptionPane.showMessageDialog(JugarCompPanel.this, "¡Felicidades! Hidato resolt. Puntuació: " + punts + " (Temps: " + (temps / 1000) + " s)" + "\n(No hi ha jugador desafiat)." );
                    }
                } catch (Exception ignored) {
                    cp.acabarGameSeleccionat((int) punts);
                    JOptionPane.showMessageDialog(JugarCompPanel.this, "¡Felicidades! Hidato resolt. Puntuació: " + punts + " (Temps: " + (temps / 1000) + " s)");
                }
                // volver a la lista de juegos
                cp.irAlHidatoGame(0,0);
            } else {
                String[][] sol = cp.obtenerSolucio(numMax, numFiles, numCols, adj);
                if (sol != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String[] row : sol) {
                        for (String c : row) sb.append(c).append(" ");
                        sb.append('\n');
                    }
                    JOptionPane.showMessageDialog(JugarCompPanel.this, "No està correcte. Solució proposada:\n" + sb.toString());
                } else {
                    JOptionPane.showMessageDialog(JugarCompPanel.this, "No està correcte i no s'ha pogut generar solució.");
                }
            }
        });
    }

    /**
     * @brief Funció per actualitzar el temps mostrat a la etiqueta.
     *
     * Es calcula el temps transcuregut en segons a partir del timer del Game y s'actualitza el text de la etiqueta.
     */
    private void updateTime() {
        long t = cp.getTemps() / 1000;
        timeLabel.setText("Temps: " + t + " s");
    }

    /**
     * @brief Funció per validar l'input introduït por el jugador al modificar una casella.
     *
     * L'input és vàlid si és un número enter positiu o el caràcter '?' (que representa una casella buida).
     *
     * @param in És l'input introduït por el jugador.
     *
     * @return Retorna true si l'input és vàlid, false en caso contrario.
     */
    private boolean validateInput(String in) {
        if (in.equals("?")) return true;
        return Pattern.matches("\\d+", in);
    }
}
