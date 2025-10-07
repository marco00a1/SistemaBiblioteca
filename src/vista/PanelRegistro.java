package vista;

import dao.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PanelRegistro extends JDialog {

    private JTextField txtNombre, txtEmail;
    private JPasswordField txtPassword;
    private JButton btnRegistrar, btnCancelar;
    private JPanel contentPane;

    public PanelRegistro(JFrame parent) {
        super(parent, "Registro Usuario", true);
        setSize(400, 450);
        setLocationRelativeTo(parent);

        contentPane = new JPanel(null);
        setContentPane(contentPane);

        // Nombre
        txtNombre = new JTextField();
        txtNombre.setBounds(65, 100, 273, 40);
        txtNombre.setBorder(null);
        contentPane.add(txtNombre);
        JLabel lblNombre = createImageLabel("/imagenes/login/txt_user.png", 17, 100, 332, 40);
        contentPane.add(lblNombre);

        // Email
        txtEmail = new JTextField();
        txtEmail.setBounds(65, 160, 273, 40);
        txtEmail.setBorder(null);
        contentPane.add(txtEmail);
        JLabel lblEmail = createImageLabel("/imagenes/login/txt_user.png", 17, 160, 332, 40);
        contentPane.add(lblEmail);

        // Password
        txtPassword = new JPasswordField();
        txtPassword.setBounds(65, 220, 273, 40);
        txtPassword.setBorder(null);
        contentPane.add(txtPassword);
        JLabel lblPassword = createImageLabel("/imagenes/login/txt_pass.png", 17, 220, 332, 40);
        contentPane.add(lblPassword);

        // Agregar placeholders
        agregarPlaceholder(txtNombre, "Ingrese nombre");
        agregarPlaceholder(txtEmail, "Ingrese correo");
        agregarPlaceholder(txtPassword, "Ingrese contraseña");

     
        this.setFocusable(true);
        this.requestFocusInWindow();

        // Botones
        btnRegistrar = new JButton();
        btnRegistrar.setBounds(50, 320, 120, 50);
        btnRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setBorderPainted(false);
        setButtonIcon(btnRegistrar, "/imagenes/login/btn_iniciar_2.png");
        contentPane.add(btnRegistrar);

        btnCancelar = new JButton();
        btnCancelar.setBounds(200, 320, 120, 50);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setBorderPainted(false);
        setButtonIcon(btnCancelar, "/imagenes/login/btn_cancel_2.png");
        btnCancelar.addActionListener(e -> dispose());
        contentPane.add(btnCancelar);

        // Fondo
        JLabel lblFondo = createImageLabel("/imagenes/login/frame_3.png", 0, 0, 400, 450);
        contentPane.add(lblFondo);

        UsuarioDAO dao = new UsuarioDAO();

        btnRegistrar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();

            // Evitar registrar placeholders como datos
            if(nombre.isEmpty() || nombre.equals("Ingrese nombre") ||
               email.isEmpty() || email.equals("Ingrese correo") ||
               pass.isEmpty() || pass.equals("Ingrese contraseña")){
                JOptionPane.showMessageDialog(this, "Complete todos los campos");
                return;
            }

            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setEmail(email);
            u.setPassword(pass);
            u.setActivo(true);
            u.setDeuda(0);
            u.setSocio(false);
            u.setRol("usuario"); // rol por defecto

            boolean ok = dao.registrar(u);
            if(ok){
                JOptionPane.showMessageDialog(this,"Usuario registrado correctamente. Inicie sesión.");
                dispose(); // cierra el registro
            } else {
                JOptionPane.showMessageDialog(this,"Error al registrar. Email duplicado?");
            }
        });
    }

    // Placeholder para JTextField
    private void agregarPlaceholder(JTextField campo, String textoGuia) {
        campo.setText(textoGuia);
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if(campo.getText().equals(textoGuia)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if(campo.getText().isEmpty()) {
                    campo.setText(textoGuia);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Placeholder para JPasswordField
    private void agregarPlaceholder(JPasswordField campo, String textoGuia) {
        campo.setEchoChar((char)0); // mostrar texto inicialmente
        campo.setText(textoGuia);
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                String password = new String(campo.getPassword());
                if(password.equals(textoGuia)) {
                    campo.setText("");
                    campo.setEchoChar('•'); // ocultar contraseña
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if(campo.getPassword().length == 0) {
                    campo.setText(textoGuia);
                    campo.setEchoChar((char)0);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    private JLabel createImageLabel(String path, int x, int y, int w, int h){
        JLabel lbl = new JLabel();
        URL url = getClass().getResource(path);
        if(url != null){
            lbl.setIcon(new ImageIcon(url));
        } else {
            lbl.setText("Imagen no encontrada: " + path);
            lbl.setForeground(Color.RED);
        }
        lbl.setBounds(x, y, w, h);
        return lbl;
    }

    private void setButtonIcon(JButton btn, String path){
        URL url = getClass().getResource(path);
        if(url != null) btn.setIcon(new ImageIcon(url));
    }
}