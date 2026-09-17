package edu.upc.prop.clusterxx.presentation.paneles;

import javax.swing.*;
import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;
import edu.upc.prop.clusterxx.domain.model.Hidato;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * @file LlistaHidatosRepoPanel.java
 *
 * @brief Panel para mostrar la lista de hidatos de un repositorio y crear juegos.
 */
public class LlistaHidatosRepoPanel extends JPanel {

    /**
     * @brief Referencia al controlador de presentación para interactuar con la lógica de la aplicación.
     */
    private CtrlPresentacio ctrlPresentacio;
    /**
     * @brief Identificador del repositorio cuyos hidatos se mostrarán en el panel.
     */
    private int repositorioId;
    /**
     * @brief Modelo de lista para mostrar la información de los hidatos en la interfaz gráfica.
     */
    private DefaultListModel<String> listModel;
    /**
     * @brief Componente gráfico de lista para mostrar los hidatos disponibles en el repositorio.
     */
    private JList<String> hidatoList;
    /**
     * @brief Lista de entradas que asocia el ID del hidato con su objeto Hidato, utilizada para gestionar las acciones sobre los hidatos seleccionados.
     */
    private List<Map.Entry<Integer, Hidato>> hidatos;

    /**
     * @brief Creadora del panel, inicializa los componentes gráficos y carga los hidatos del repositorio.
     *
     * @param ctrlPresentacio Referencia al controlador de presentación para interactuar con la lógica de la aplicación.
     * @param repositorioId Identificador del repositorio cuyos hidatos se mostrarán en el panel.
     */
    public LlistaHidatosRepoPanel(CtrlPresentacio ctrlPresentacio, int repositorioId) {
        this.ctrlPresentacio = ctrlPresentacio;
        this.repositorioId = repositorioId;

        setLayout(new BorderLayout(10, 10));
        boolean nightMode = ctrlPresentacio.getNightMode();
        setBackground(nightMode ? new Color(45, 45, 45) : Color.WHITE);

        initComponents();
        carregarHidatos();
    }

    /**
     * @brief Inicializa los componentes gráficos del panel, incluyendo el header, la lista de hidatos y los botones de control.
     */
    private void initComponents() {
        boolean nightMode = ctrlPresentacio.getNightMode();
        Color headerColor = nightMode ? new Color(50, 60, 100) : new Color(100, 149, 237);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerColor);
        JLabel titleLabel = new JLabel("Hidatos del Repositori");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Lista de hidatos
        listModel = new DefaultListModel<>();
        hidatoList = new JList<>(listModel);
        hidatoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(hidatoList);
        scroll.setBorder(BorderFactory.createTitledBorder("Hidatos disponibles"));
        add(scroll, BorderLayout.CENTER);

        // Panel de botones inferior
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton crearJuegoBtn = new JButton("Crear Joc");
        crearJuegoBtn.addActionListener(e -> onCrearJuego());

        JButton eliminarBtn = new JButton("Eliminar Hidato");
        eliminarBtn.addActionListener(e -> {
            try {
                onBorrarHidato();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton volveroBtn = new JButton("Tornar");
        volveroBtn.addActionListener(e -> ctrlPresentacio.irAlRepositori());

        controls.add(crearJuegoBtn);
        controls.add(eliminarBtn); // Añadido al flujo de botones
        controls.add(volveroBtn);

        southPanel.add(controls, BorderLayout.CENTER);
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * @brief Carga los hidatos del repositorio y los muestra en la lista. Si no hay hidatos, muestra un mensaje indicándolo.
     */
    private void carregarHidatos() {
        listModel.clear();
        hidatos = ctrlPresentacio.getHidatosDelRepositorio(repositorioId);

        if (hidatos.isEmpty()) {
            listModel.addElement("No hi ha hidatos en aquest repositori");
        } else {
            for (Map.Entry<Integer, Hidato> entry : hidatos) {
                Hidato h = entry.getValue();
                String info = "ID: " + h.getId() +
                        " (" + h.getNum_files() + "x" + h.getNum_cols() + ") " +
                        "- " + h.getDificultat();
                listModel.addElement(info);
            }
        }
    }

    /**
     * @brief Maneja el borrado del hidato seleccionado del repositorio.
     */
    private void onBorrarHidato() throws Exception {
        int selectedIndex = hidatoList.getSelectedIndex();
        // Evitamos que intente borrar si no hay selección o si la lista contiene el mensaje de "vacío"
        if (selectedIndex == -1 || hidatos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Si us plau, selecciona un hidato per esborrar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hidato hidatoSeleccionado = hidatos.get(selectedIndex).getValue();

        // Confirmación de seguridad
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "Estàs segur que vols esborrar l'Hidato amb ID " + hidatoSeleccionado.getId() + "?",
                "Confirmar esborrat",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Invocamos al controlador pasándole el Hidato entero
            ctrlPresentacio.borrarHidato(hidatoSeleccionado.getId(), repositorioId);

            // Informamos al usuario y refrescamos la lista para que desaparezca visualmente
            JOptionPane.showMessageDialog(this, "Hidato esborrat correctament.", "Èxit", JOptionPane.INFORMATION_MESSAGE);
            carregarHidatos();
        }
    }

    /**
     * @brief Maneja la creación de un juego a partir del hidato seleccionado, permitiendo elegir el tipo de juego y, en caso de competitivo, el usuario a desafiar.
     */
    private void onCrearJuego() {
        int selectedIndex = hidatoList.getSelectedIndex();
        if (selectedIndex == -1 || hidatos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Si us plau, selecciona un hidato.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hidato hidatoSeleccionado = hidatos.get(selectedIndex).getValue();

        String[] opciones = {"Clàssic", "Cooperatiu", "Competitiu"};
        int seleccion = JOptionPane.showOptionDialog(
                this,
                "Selecciona el tipus de joc:",
                "Tipus de Joc",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == -1) return; // Cancelado

        String tipo;
        if (seleccion == 0) tipo = "classic";
        else if (seleccion == 1) tipo = "coop";
        else tipo = "competitive";

        if ("competitive".equals(tipo)) {
            String usuariDesafiat = JOptionPane.showInputDialog(this, "Introdueix el nom d'usuari del jugador a desafiar:", "");
            if (usuariDesafiat == null || usuariDesafiat.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nom d'usuari no vàlid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ctrlPresentacio.crearGameDelTipo(hidatoSeleccionado, tipo, usuariDesafiat.trim());
        } else {
            ctrlPresentacio.crearGameDelTipo(hidatoSeleccionado, tipo);
        }

        ctrlPresentacio.irAlRepositori();
    }
}