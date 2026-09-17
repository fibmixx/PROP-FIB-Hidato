package edu.upc.prop.clusterxx.presentation.paneles;
import edu.upc.prop.clusterxx.domain.model.CoopGame;
import edu.upc.prop.clusterxx.domain.model.Hidato;
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
public class JugarCoopPanel extends JPanel {

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

    /**
     * @brief Timer de Swing per actualitzar el temps cada mig segon. Diferent al timer del Game
     */
    private Timer swingTimer;

    /**
     * @brief Boolean para controlar de quién es el turno en el juego cooperativo.
     */
    private boolean playerOneTurn = true;

    /**
     * @brief Nombre de files i columnes del tauler, i el valor màxim que pot tenir una casella.
     */
    private int numFiles, numCols, numMax;

    /**
     * @brief Creadora de la classe JugarPanel.
     *
     * S'inicialitza el panel amb el tauler del joc seleccionat, es configuren els botons per modificar les caselles i es comença a comptar el temps.
     */
    public JugarCoopPanel() {
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
        // Mostramos los dos timers a la vez: jugador 1 (timer principal) y jugador 2 (timer company)
        JPanel timersPanel = new JPanel(new GridLayout(1,2));
        if (nightMode) timersPanel.setBackground(bgColor);
        JLabel timeLabel1 = new JLabel("Jugador 1 - Temps: 0 s", SwingConstants.LEFT);
        JLabel timeLabel2 = new JLabel("Jugador 2 - Temps: 0 s", SwingConstants.RIGHT);
        if (nightMode) { timeLabel1.setForeground(Color.WHITE); timeLabel2.setForeground(Color.WHITE); }
        timersPanel.add(timeLabel1);
        timersPanel.add(timeLabel2);
        top.add(timersPanel, BorderLayout.WEST);
        JButton resolverBtn = new JButton("Resoldre");
        top.add(resolverBtn, BorderLayout.EAST);
        
        // Panel de botones centrales (Pista y Menú Principal)
        JPanel centerButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton pistaBtn = new JButton("Pista");
        pistaBtn.addActionListener(e -> {
            List<int[]> pistas = cp.aplicarPista();
            if (pistas == null || pistas.isEmpty()) {
                JOptionPane.showMessageDialog(JugarCoopPanel.this, "No es possible donar pista.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Actualizar los botones con las pistas
            for (int[] coord : pistas) {
                int fila = coord[0];
                int col = coord[1];
                String nouValor = cp.getTaulerSelectedGame()[fila][col];
                cellButtons[fila][col].setText(nouValor);
            }
            JOptionPane.showMessageDialog(JugarCoopPanel.this, "Pista donada: " + pistas.size() + " cela(es) revelada(es).", "Pista", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(JugarCoopPanel.this, "Tauler reiniciat.", "Info", JOptionPane.INFORMATION_MESSAGE);
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
                            String input = JOptionPane.showInputDialog(JugarCoopPanel.this, "Introdueix valor (número entre 1 i " + numMax + " o ?):", tauler[fi][fj]);
                            if (input == null) return; // cancelar
                            input = input.trim();
                            if (!validateInput(input)) {
                                JOptionPane.showMessageDialog(JugarCoopPanel.this, "Valor invàlid.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (!cp.validarValor(input, numMax)) {
                                JOptionPane.showMessageDialog(JugarCoopPanel.this, "Valor fora de rang.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            cp.setTaulerValue(fi, fj, input);
                            b.setText(input);
                            if (playerOneTurn) {
                                // pasar a jugador 2
                                cp.stopTimer();
                                cp.startTimerCompany();
                                playerOneTurn = false;
                                JOptionPane.showMessageDialog(JugarCoopPanel.this, "Torn de Jugador 2.");
                            } else {
                                // pasar a jugador 1
                                cp.stopTimerCompany();
                                cp.startTimer();
                                playerOneTurn = true;
                                JOptionPane.showMessageDialog(JugarCoopPanel.this, "Torn de Jugador 1.");
                            }
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
        try {
            cp.stopTimerCompany();
        } catch (Exception ignored) {}
        swingTimer = new Timer(500, e -> updateTime(timeLabel1, timeLabel2));
        swingTimer.start();

        resolverBtn.addActionListener(e -> {
            String adj = cp.detectAdjacencia(cp.getHidatoSelectedGame());

            boolean correcto = cp.comprovarSolucio(numFiles, numCols, adj);

            if (correcto) {
                swingTimer.stop();
                cp.stopTimer();
                cp.stopTimerCompany();
                long t1 = cp.getTemps();
                long t2 = cp.getTempsCompany();
                long punts = Math.max(0, 100000 - (int) (t1 + t2));
                cp.acabarGameSeleccionat((int) punts);
                JOptionPane.showMessageDialog(JugarCoopPanel.this, "¡Felicidades! Hidato resolt. Puntuación: " + punts + " (Temps: " + ((int)(t1+t2) / 1000) + " s)");
                cp.irAlHidatoGame(0,0);
            } else {
                if (playerOneTurn) {
                    cp.stopTimer();
                    cp.startTimerCompany();
                    playerOneTurn = false;
                    JOptionPane.showMessageDialog(JugarCoopPanel.this, "Valor incorrecte. Torn de Jugador 2.");
                } else {
                    cp.stopTimerCompany();
                    cp.startTimer();
                    playerOneTurn = true;
                    JOptionPane.showMessageDialog(JugarCoopPanel.this, "Valor incorrecte. Torn de Jugador 1.");
                }
                return;
            }
        });
    }

    /**
     * @brief Funció per actualitzar el temps mostrat a la etiqueta.
     *
     * Es calcula el temps transcuregut en segons a partir del timer del Game y s'actualitza el text de la etiqueta.
     */
    private void updateTime(JLabel timeLabel1, JLabel timeLabel2) {
        long t1 = 0;
        long t2 = 0;
        try { t1 = cp.getTemps() / 1000; } catch (Exception ignored) {}
        try { t2 = cp.getTempsCompany() / 1000; } catch (Exception ignored) {}
        timeLabel1.setText("Jugador 1 - Temps: " + t1 + " s");
        timeLabel2.setText("Jugador 2 - Temps: " + t2 + " s");
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
