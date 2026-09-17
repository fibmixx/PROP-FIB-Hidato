package edu.upc.prop.clusterxx.presentation.paneles;

import javax.swing.*;
import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;
import edu.upc.prop.clusterxx.presentation.windows.UserSelectionWindow;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @file RankingPanel.java
 * @brief Classe que representa el panel per gestionar rànkings.
 * @author TBD
 */
public class RankingPanel extends JPanel {

    /**
     * @brief Instància del controlador de la capa de presentació.
     */
    private CtrlPresentacio ctrlPresentacio;

    /**
     * @brief Components per a la llista de rànkings personalitzats.
     */
    private DefaultListModel<String> rankingListModel;
    private JList<String> rankingList;

    /**
     * @brief Pestanyes per a rànkings propios i global.
     */
    private JTabbedPane tabbedPane;

    /**
     * @brief Constructor del panel de rànkings.
     * @param ctrlPresentacio Instància del controlador de la capa de presentació.
     */
    public RankingPanel(CtrlPresentacio ctrlPresentacio) {
        this.ctrlPresentacio = ctrlPresentacio;
        setLayout(new BorderLayout(10, 10));
        boolean nightMode = ctrlPresentacio.getNightMode();
        setBackground(nightMode ? new Color(45, 45, 45) : Color.WHITE);

        initComponents();
        carregarRankings();
    }

    /**
     * @brief Inicialitza tots els components del panel.
     * 
     * @post S'han inicialitzat tots els components del panel.
     */
    private void initComponents() {
        boolean nightMode = ctrlPresentacio.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : Color.WHITE;
        Color headerColor = nightMode ? new Color(90, 50, 90) : new Color(255, 105, 180);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerColor);
        JLabel welcomeLabel = new JLabel("Gestió de Rànkings");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        JPanel rankingsPanel = crearPanelRankingsPersonalitzats();
        tabbedPane.addTab("Els Meus Rànkings", rankingsPanel);

        JPanel globalPanel = crearPanelRankingGlobal();
        tabbedPane.addTab("Rànking Global", globalPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(bgColor);
        JButton tornarBtn = crearBoto("Menú principal", e -> ctrlPresentacio.irAlMenuPrincipal());
        footerPanel.add(tornarBtn);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * @brief Crea el panel per a rànkings personalitzats (no global).
     * 
     * @post S'ha creat un panel que mostra els rànkings personalitzats.
     */
    private JPanel crearPanelRankingsPersonalitzats() {
        boolean nightMode = ctrlPresentacio.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : Color.WHITE;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rankingListModel = new DefaultListModel<>();
        rankingList = new JList<>(rankingListModel);
        rankingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        rankingList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = rankingList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    if (rankingList.getCellBounds(index, index).contains(e.getPoint())) {
                        obrirTabRankingSeleccionat();
                    }
                }
            }
        });

        JScrollPane scrollRankings = new JScrollPane(rankingList);
        scrollRankings.setBorder(BorderFactory.createTitledBorder("Els Meus Rànkings"));
        panel.add(scrollRankings, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(bgColor);

        JButton veureBtn = crearBoto("Veure Puntuacions", e -> obrirTabRankingSeleccionat());
        JButton crearBtn = crearBoto("Crear Rànking", e -> onCrearRanking());
        JButton eliminarBtn = crearBoto("Eliminar Rànking", e -> onEliminarRanking());

        controlPanel.add(veureBtn);
        controlPanel.add(crearBtn);
        controlPanel.add(eliminarBtn);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * @brief Crea el panel per al rànking global (només lectura).
     * 
     * @post S'ha creat un panel que mostra les puntuacions del rànking global.
     */
    private JPanel crearPanelRankingGlobal() {
        boolean nightMode = ctrlPresentacio.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : Color.WHITE;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> modelGlobal = new DefaultListModel<>();
        JList<String> listGlobal = new JList<>(modelGlobal);
        JScrollPane scrollGlobal = new JScrollPane(listGlobal);
        scrollGlobal.setBorder(BorderFactory.createTitledBorder("Puntuacions Globals"));

        List<String> puntuacionsGlobal = ctrlPresentacio.obtenirPuntuacionsRankingGlobal();
        if (puntuacionsGlobal != null) {
            for (String s : puntuacionsGlobal) {
                modelGlobal.addElement(s);
            }
        }

        panel.add(scrollGlobal, BorderLayout.CENTER);

        JButton refreshBtn = crearBoto("Actualitzar", e -> {
            modelGlobal.clear();
            List<String> puntuacions = ctrlPresentacio.obtenirPuntuacionsRankingGlobal();
            if (puntuacions != null) {
                for (String s : puntuacions) {
                    modelGlobal.addElement(s);
                }
            }
        });
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(refreshBtn);
        controlPanel.setBackground(bgColor);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * @brief Carrega la llista de rànkings personalitzats en background.
     * 
     * @post S'ha carregat la llista de rànkings personalitzats en background.
     */
    private void carregarRankings() {
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() {
                return ctrlPresentacio.llistarRankings();
            }

            @Override
            protected void done() {
                rankingListModel.clear();
                try {
                    List<String> rankingss = get();
                    if (rankingss != null) {
                        for (String s : rankingss) {
                            rankingListModel.addElement(s);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RankingPanel.this, "Error carregant els rànkings.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * @brief Obre una nova pestanya o selecciona la ja existent amb les puntuacions del rànking.
     * 
     * @post S'ha obert una nova pestanya o s'ha seleccionat la ja existent amb les puntuacions del rànking.
     */
    private void obrirTabRankingSeleccionat() {
        String sel = rankingList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un rànking per veure les seves puntuacions.", "Informació", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(sel.split(" - ", 2)[0].trim());
            String tabTitle = "Rànking " + id;

            int index = -1;
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getTitleAt(i).equals(tabTitle)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                tabbedPane.setSelectedIndex(index);
            } else {
                JPanel detailPanel = crearPanelRankingPersonalitzatDetall(id, tabTitle);
                tabbedPane.addTab(tabTitle, detailPanel);
                
                int newIndex = tabbedPane.indexOfComponent(detailPanel);
                if (newIndex != -1) {
                    tabbedPane.setTabComponentAt(newIndex, new TabHeaderWithCloseButton(tabTitle, detailPanel));
                }
                tabbedPane.setSelectedComponent(detailPanel);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error obrint el rànking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Tanca la pestanya corresponent al rànking amb el format "Rànking id".
     * 
     * @param id L'ID del rànking a tancar.
     * 
     * @post S'ha tancat la pestanya corresponent al rànking amb el format "Rànking id".
     */
    private void tancarTabRanking(int id) {
        String tabTitle = "Rànking " + id;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(tabTitle)) {
                tabbedPane.removeTabAt(i);
                break;
            }
        }
    }

    /**
     * @brief Crea un panel que imita l'estil del global amb el llistat de puntuacions del rànking personalitzat.
     * 
     * @post S'ha creat un panel que mostra les puntuacions del rànking amb l'ID proporcionat.
     */
    private JPanel crearPanelRankingPersonalitzatDetall(int id, String tabTitle) {
        boolean nightMode = ctrlPresentacio.getNightMode();
        Color bgColor = nightMode ? new Color(45, 45, 45) : Color.WHITE;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createTitledBorder("Puntuacions de Rànking " + id));

        List<String> puntuacions = ctrlPresentacio.obtenirPuntuacionsRanking(id);
        if (puntuacions != null) {
            for (String s : puntuacions) {
                model.addElement(s);
            }
        }

        panel.add(scroll, BorderLayout.CENTER);

        JButton refreshBtn = crearBoto("Actualitzar", e -> {
            model.clear();
            List<String> p = ctrlPresentacio.obtenirPuntuacionsRanking(id);
            if (p != null) {
                for (String s : p) {
                    model.addElement(s);
                }
            }
        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(refreshBtn);
        controlPanel.setBackground(bgColor);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * @brief Accio per crear un nou rànking en background.
     * @post S'ha creat un nou rànking amb els usuaris seleccionats, s'ha afegit a la llista i s'ha obert una pestanya amb el seu contingut.
     */
    private void onCrearRanking() {
        String creador = ctrlPresentacio.getCurrentUser().getUsername();
        
        List<String> usuariosDisponibles = ctrlPresentacio.obtenirLlistaUsuaris();

        if (usuariosDisponibles == null || usuariosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hi ha usuaris disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UserSelectionWindow selectionWindow = new UserSelectionWindow(
            SwingUtilities.getWindowAncestor(this),
            creador,
            usuariosDisponibles
        );

        if (selectionWindow.isConfirmed()) {
            List<String> usuariosSeleccionats = selectionWindow.getSelectedUsers();

            if (usuariosSeleccionats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No vas seleccionar cap usuari. El rànking no es va crear.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ctrlPresentacio.crearRanking(creador, usuariosSeleccionats);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); 
                        carregarRankings();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(RankingPanel.this,
                                "Error creant el rànking: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    /**
     * @brief Accio per eliminar un rànking seleccionat en background.
     */
    private void onEliminarRanking() {
        String sel = rankingList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un rànking per esborrar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Estàs segur que vols eliminar: " + sel + " ?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id;
        try {
            id = Integer.parseInt(sel.split(" - ", 2)[0].trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No s'ha pogut extreure l'ID del rànking.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                ctrlPresentacio.eliminarRanking(id);
                return null;
            }

            @Override
            protected void done() {
                carregarRankings();
                tancarTabRanking(id);
            }
        };
        worker.execute();
    }

    /**
     * @brief Funció auxiliar per crear botons amb estil.
     */
    private JButton crearBoto(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.addActionListener(listener);
        return btn;
    }

    /**
     * @brief Component per a la capçalera de la pestanya amb un botó de tancar (x).
     */
    private class TabHeaderWithCloseButton extends JPanel {
        public TabHeaderWithCloseButton(String title, Component pane) {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setOpaque(false);
            
            JLabel label = new JLabel(title);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            
            JButton closeBtn = new JButton("x");
            closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
            closeBtn.setForeground(Color.GRAY);
            closeBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            closeBtn.setContentAreaFilled(false);
            closeBtn.setFocusable(false);
            closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            closeBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    closeBtn.setForeground(Color.RED);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    closeBtn.setForeground(Color.GRAY);
                }
            });
            
            closeBtn.addActionListener(e -> {
                tabbedPane.remove(pane);
            });
            
            add(label);
            add(closeBtn);
        }
    }
}
