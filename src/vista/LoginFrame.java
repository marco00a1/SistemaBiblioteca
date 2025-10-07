package vista;

import dao.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegistrar;
    public static JProgressBar pbCargar;
    private Usuario usuarioLogueado;

    public LoginFrame() {
        setTitle("Acceso Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 388, 487);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Email
        txtEmail = new JTextField();
        txtEmail.setBounds(65, 187, 273, 48);
        txtEmail.setBorder(null);
        contentPane.add(txtEmail);

        JLabel lblEmail = new JLabel();
        lblEmail.setIcon(new ImageIcon(LoginFrame.class.getResource("/imagenes/login/txt_user.png")));
        lblEmail.setBounds(17, 187, 332, 48);
        contentPane.add(lblEmail);

        // Password
        txtPassword = new JPasswordField();
        txtPassword.setBounds(65, 269, 273, 48);
        txtPassword.setBorder(null);
        contentPane.add(txtPassword);

        JLabel lblPassword = new JLabel();
        lblPassword.setIcon(new ImageIcon(LoginFrame.class.getResource("/imagenes/login/txt_pass.png")));
        lblPassword.setBounds(17, 269, 332, 48);
        contentPane.add(lblPassword);

        // Botones
        btnLogin = new JButton();
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setIcon(new ImageIcon(LoginFrame.class.getResource("/imagenes/login/btn_iniciar_2.png")));
        btnLogin.setBounds(47, 379, 118, 58);
        contentPane.add(btnLogin);

        btnRegistrar = new JButton();
        btnRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setIcon(new ImageIcon(LoginFrame.class.getResource("/imagenes/login/btn_regist_2.png")));
        btnRegistrar.setBounds(194, 379, 118, 58);
        contentPane.add(btnRegistrar);

        // Barra de carga
        pbCargar = new JProgressBar();
        pbCargar.setBounds(85, 342, 202, 14);
        pbCargar.setStringPainted(true);
        contentPane.add(pbCargar);

        // Fondo
        JLabel lblFondo = new JLabel();
        lblFondo.setIcon(new ImageIcon(LoginFrame.class.getResource("/imagenes/login/frame_2.png")));
        lblFondo.setBounds(0, 0, 375, 447);
        contentPane.add(lblFondo);

        UsuarioDAO dao = new UsuarioDAO();

        // Placeholders para email y contraseña
        setupPlaceholders();

        // Activar boton login solo si hay texto real
        txtEmail.getDocument().addDocumentListener(new SimpleDocumentListener() { public void update() { activarBoton(); } });
        txtPassword.getDocument().addDocumentListener(new SimpleDocumentListener() { public void update() { activarBoton(); } });

        // Botones
        btnLogin.addActionListener(e -> verificarUsuario(dao));
        btnRegistrar.addActionListener(e -> {
            PanelRegistro registro = new PanelRegistro(this);
            registro.setVisible(true);
        });

        // Evitar foco automatico en los campos al abrir
        contentPane.setFocusable(true);
        contentPane.requestFocusInWindow();
    }

    private void setupPlaceholders() {
        // Email
        txtEmail.setText("Correo electrónico");
        txtEmail.setForeground(Color.GRAY);
        txtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if(txtEmail.getText().equals("Correo electrónico")) {
                    txtEmail.setText("");
                    txtEmail.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if(txtEmail.getText().isEmpty()) {
                    txtEmail.setText("Correo electrónico");
                    txtEmail.setForeground(Color.GRAY);
                }
            }
        });

        // Password
        txtPassword.setText("Contraseña");
        txtPassword.setForeground(Color.GRAY);
        txtPassword.setEchoChar((char)0);
        txtPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if(String.valueOf(txtPassword.getPassword()).equals("Contraseña")) {
                    txtPassword.setText("");
                    txtPassword.setForeground(Color.BLACK);
                    txtPassword.setEchoChar('•');
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if(txtPassword.getPassword().length == 0) {
                    txtPassword.setText("Contraseña");
                    txtPassword.setForeground(Color.GRAY);
                    txtPassword.setEchoChar((char)0);
                }
            }
        });
    }

    private void activarBoton() {
        btnLogin.setEnabled(!txtEmail.getText().isEmpty() && !txtEmail.getText().equals("Correo electrónico") &&
                            txtPassword.getPassword().length > 0 && !String.valueOf(txtPassword.getPassword()).equals("Contraseña"));
    }

    private void verificarUsuario(UsuarioDAO dao) {
        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if(email.isEmpty() || email.equals("Correo electrónico") || pass.isEmpty() || pass.equals("Contraseña")) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }

        Usuario u = dao.login(email, pass);
        if(u != null){
            if(!u.isActivo()){
                JOptionPane.showMessageDialog(this, "Usuario inactivo");
                return;
            }
            usuarioLogueado = u;
            new HiloBarraLogin(usuarioLogueado).start();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales inválidas");
        }
    }

    class HiloBarraLogin extends Thread {
        private Usuario usuario;
        public HiloBarraLogin(Usuario u) { this.usuario = u; }

        public void run() {
            for(int i=0; i<=100; i++) {
                final int valor = i;
                SwingUtilities.invokeLater(() -> pbCargar.setValue(valor));
                try { Thread.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }
            }
            SwingUtilities.invokeLater(() -> new MainFrame(usuario).setVisible(true));
            dispose();
        }
    }

    interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update();
        default void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        default void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        default void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        EventQueue.invokeLater(() -> {
            try {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}