package edu.upc.prop.clusterxx.presentation.windows;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * @file UserSelectionWindow.java
 *
 * @brief Finestra modal per a la selecció d'usuaris que participaran en un Ranking Personal.
 *
 * Permet seleccionar múltiples usuaris (excepte el creador) per compartir un ranking personalitzat.
 * S'assegura que hi hagi un mínim de 2 participants (creador + almenys 1 altre usuari).
 */
public class UserSelectionWindow extends JDialog {
    /**
     * @brief Resultat de la selecció (usuaris seleccionats).
     */
    private List<String> selectedUsers;

    /**
     * @brief Flag per saber si s'ha confirmat la selecció.
     */
    private boolean confirmed;

    /**
     * @brief Model de la llista de checkboxes.
     */
    private DefaultListModel<UserCheckBox> listModel;

    /**
     * @brief Llista de checkboxes dels usuaris disponibles.
     */
    private JList<UserCheckBox> userList;

    /**
     * @brief Constructor de la finestra de selecció d'usuaris.
     *
     * @param parent Finestra pare.
     * @param creador Nom de l'usuari que crea el ranking (no podrà ser seleccionat).
     * @param usuariosDisponibles Llista de noms d'usuaris disponibles per seleccionar (excloent el creador).
     *
     * @post S'obre una finestra modal amb la llista d'usuaris disponibles per seleccionar, i es poden confirmar o cancelar la selecció.
     */
    public UserSelectionWindow(Window parent, String creador, List<String> usuariosDisponibles) {
        super((Frame) parent, "Finestra de selecció d'usuaris", JDialog.DEFAULT_MODALITY_TYPE);
        this.selectedUsers = new ArrayList<>();
        this.confirmed = false;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents(creador, usuariosDisponibles);
    }

    /**
     * @brief Inicialitza els components de la finestra.
     *
     * @param creador Nom de l'usuari creador.
     * @param usuariosDisponibles Llista d'usuaris disponibles.
     *
     * @post S'inicialitzen tots els components.
     */
    private void initComponents(String creador, List<String> usuariosDisponibles) {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Capçalera
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Selecciona els usuaris:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        add(headerPanel, BorderLayout.NORTH);

        // Llista de checkboxes
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setCellRenderer(new UserCheckBoxRenderer());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Omplir la llista amb els usuaris disponibles (excloint el creador)
        for (String usuario : usuariosDisponibles) {
            if (!usuario.equals(creador)) {
                listModel.addElement(new UserCheckBox(usuario, false));
            }
        }

        // Agregar listener per als clicks en els checkboxes
        userList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = userList.locationToIndex(evt.getPoint());
                if (index >= 0 && index < listModel.getSize()) {
                    UserCheckBox item = listModel.getElementAt(index);
                    item.setSelected(!item.isSelected());
                    listModel.set(index, item);
                    userList.repaint();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Usuaris disponibles"));
        add(scrollPane, BorderLayout.CENTER);

        // Panell de botons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton confirmBtn = new JButton("Acceptar");
        confirmBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        confirmBtn.addActionListener(e -> onConfirm());

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelBtn.addActionListener(e -> onCancel());

        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * @brief Accio per confirmar la selecció.
     *
     * @post Crea o no crea el rànking en donar a confirmar.
     */
    private void onConfirm() {
        selectedUsers.clear();
        int count = 0;
        for (int i = 0; i < listModel.getSize(); ++i) {
            UserCheckBox item = listModel.getElementAt(i);
            if (item.isSelected()) {
                selectedUsers.add(item.getUsername());
                count++;
            }
        }

        // Validar que hi hagi almenys un usuari seleccionat
        if (count == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona almenys un usuari.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        confirmed = true;
        dispose();
    }

    /**
     * @brief Accio per cancelar la selecció.
     */
    private void onCancel() {
        confirmed = false;
        selectedUsers.clear();
        dispose();
    }

    /**
     * @brief Obté la llista d'usuaris seleccionats.
     *
     * @return Llista d'usuaris seleccionats, o llista buida si es va cancelar.
     */
    public List<String> getSelectedUsers() {
        return new ArrayList<>(selectedUsers);
    }

    /**
     * @brief Comprova si la selecció va ser confirmada.
     *
     * @return true si es va fer clic en "Acceptar", false si es va fazer clic en "Cancelar" o es va tancar la finestra.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * @brief Classe interna que representa un item seleccionable (checkbox).
     */
    public static class UserCheckBox {
        private String username;
        private boolean selected;

        public UserCheckBox(String username, boolean selected) {
            this.username = username;
            this.selected = selected;
        }

        /**
         * @brief Funció per obtenir el username.
         *
         * @return Retorna el username.
         */
        public String getUsername() {
            return username;
        }

        /**
         * @brief Funció per comprovar si el checkbox està seleccionat.
         *
         * @return Retorna un booleà per veure si el checkbox està seleccionat.
         */
        public boolean isSelected() {
            return selected;
        }

        /**
         * @brief Funció per marcar o desmarcar el checkbox.
         *
         * @param selected Booleà que indica si el checkbox està selected.
         */
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }


    /**
     * @brief Renderitzador personalitzat per mostrar checkboxes en la llista.
     */
    public static class UserCheckBoxRenderer extends JCheckBox implements ListCellRenderer<UserCheckBox> {
        @Override
        public Component getListCellRendererComponent(JList<? extends UserCheckBox> list, UserCheckBox value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.getUsername());
            setSelected(value.isSelected());
            setBackground(isSelected ? UIManager.getColor("List.selectionBackground") : list.getBackground());
            setForeground(isSelected ? UIManager.getColor("List.selectionForeground") : list.getForeground());
            return this;
        }
    }
}
