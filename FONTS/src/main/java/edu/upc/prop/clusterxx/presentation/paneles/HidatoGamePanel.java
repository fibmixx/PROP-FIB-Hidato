package edu.upc.prop.clusterxx.presentation.paneles;


import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;
import edu.upc.prop.clusterxx.domain.model.Usuari;
import edu.upc.prop.clusterxx.domain.model.Game;
import edu.upc.prop.clusterxx.domain.model.CompetitiveGame;
import edu.upc.prop.clusterxx.domain.model.CoopGame;
import java.util.List;


/**
 * @file HidatoGamePanel.java
 * @brief Definició i implementació de la classe HidatoGamePanel.
 * @author Esteve Coma
 * @brief Mostra la llista de jocs de l'usuari i permet obrir el panel de joc.
 */
public class HidatoGamePanel extends JPanel {


    /**
     * @brief Crea un nou panel amb la llista de jocs de l'usuari.
     * @post S'ha creat un nou panel amb la llista de jocs de l'usuari.
     */
    public HidatoGamePanel() {
        setLayout(new BorderLayout(10,10));


        CtrlPresentacio cp = CtrlPresentacio.getInstance();
        boolean nightMode = cp.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : null;
        Usuari u = cp.getCurrentUser();

        if (nightMode) setBackground(bgColor);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBorder(new EmptyBorder(10,10,0,10));
        if (nightMode) header.setBackground(bgColor);
        JLabel headerLabel = new JLabel("Jocs de l'usuari: ");
        if (nightMode) headerLabel.setForeground(Color.WHITE);
        header.add(headerLabel);

        JButton menuBtn = new JButton("Menu Principal");
        menuBtn.addActionListener(e -> cp.irAlMenuPrincipal());
        header.add(menuBtn);
        add(header, BorderLayout.NORTH);


        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(10,10,10,10));
        if (nightMode) center.setBackground(bgColor);


        if (u == null) {
            center.add(new JLabel("No hi ha cap usuari en sessió."));
        } else {
            final List<Game> gamesList = cp.getGamesActuals();


            if (gamesList.isEmpty()) {
                center.add(new JLabel("No tens cap joc creat."));
            } else {
                JPanel listPanel = new JPanel();
                listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
                for (int i = 0; i < gamesList.size(); ++i) {
                    Game g = gamesList.get(i);
                    String tipus = "Clàssic";
                    if (g instanceof CompetitiveGame) tipus = "Competitiu";
                    else if (g instanceof CoopGame) tipus = "Cooperatiu";
                    String text = "Joc " + i + " - Tipus: " + tipus + " - Hidato ID: " + g.getHidato().getId() + " - Puntuació: " + g.getPuntuation();
                    if (g.getFinished()) {
                        JLabel lab = new JLabel(text + " (Acabat)");
                        lab.setForeground(Color.GRAY);
                        listPanel.add(lab);
                    } else {
                        JButton btn = new JButton(text + " (Jugar)");
                        final int idx = i;
                        btn.addActionListener(e -> {
                            Game selected = gamesList.get(idx);
                            cp.setSelectedGame(selected);
                            if (selected instanceof CoopGame) cp.irAlJugarPanelCoop();
                            else if (selected instanceof CompetitiveGame) cp.irAlJugarPanelComp();
                            else cp.irAlJugarPanel();
                        });
                        listPanel.add(btn);
                    }
                }
                JScrollPane sp = new JScrollPane(listPanel);
                sp.setPreferredSize(new Dimension(600, 300));
                center.add(sp);
            }
        }


        add(center, BorderLayout.CENTER);
    }
}
