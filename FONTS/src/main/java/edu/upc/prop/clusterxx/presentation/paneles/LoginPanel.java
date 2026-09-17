package edu.upc.prop.clusterxx.presentation.paneles;

import javax.swing.*;

import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;

import java.awt.*;

/**
 * @file LoginPanel.java
 *
 * @brief Classe que defineix la estructura visual i comportament del panel de login.
 */
public class LoginPanel extends JPanel{

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JButton exitBtn;

    /**
     * @brief Funció que defineix la estructura visual del panel de login
     *
     * @post Es genera el panel de login amb la estructura visual definida.
     */
    public LoginPanel() {
        boolean nightMode = CtrlPresentacio.getInstance().getNightMode();

        setLayout(new GridBagLayout());
        setBackground(nightMode ? new Color(45, 45, 45) : new Color(240, 240, 240));

        initComponents();
    }

    /**
     * @brief Funció que inicialitza els components visuals del panel de login.
     *
     * @post Incialitza els components visuals del panel de login, incloent el títol, els camps de text per a l'usuari i la contrasenya, i els botons d'iniciar sessió, registrar-se i sortir.
     */
    private void initComponents() {
        //Part del header
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        boolean nightMode = CtrlPresentacio.getInstance().getNightMode();

        //Part del títul
        JLabel titleLabel = new JLabel("HIDATO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(nightMode ? Color.WHITE : Color.BLACK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        add(titleLabel, gbc);

        //Part de l'usuari
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel userLabel = new JLabel("Usuari:");
        if (nightMode) userLabel.setForeground(Color.WHITE);
        add(userLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        add(usernameField, gbc);

        //Part de la contrasenya
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Contrasenya:");
        if (nightMode) passLabel.setForeground(Color.WHITE);
        add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        //Part dels botons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(nightMode ? new Color(45, 45, 45) : new Color(240, 240, 240));

        loginBtn = new JButton("Iniciar Sessió");
        loginBtn.addActionListener(e -> handleLogin());
        buttonPanel.add(loginBtn);

        registerBtn = new JButton("Registrar-se");
        registerBtn.addActionListener(e -> handleRegister());
        buttonPanel.add(registerBtn);

        exitBtn = new JButton("Sortir");
        exitBtn.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(buttonPanel, gbc);
    }

    /**
     * @brief Funció per a gestionar el login d'un usuari.
     *
     * @post Si tot funciona correctament, l'usuari fa login al sistema. Si no, saltarà un missatge explicant el error.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        CtrlPresentacio cp = CtrlPresentacio.getInstance();
        cp.login(username, password);
    }

    /**
     * @brief Funció per a gestionar el registre d'un nou usuari.
     *
     * @post Si tot funciona correctament, es crea un nou usuari. Si no, saltarà un missatge explicant el error.
     */
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cap dels dos camps pot estar buit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CtrlPresentacio cp = CtrlPresentacio.getInstance();
        cp.register(username, password);
        usernameField.setText("");
        passwordField.setText("");
    }
}
