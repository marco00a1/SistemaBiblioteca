package vista;

import dao.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GestionLibros extends JFrame {
    private JTable tablaLibros;
    private JButton btnSolicitarPrestamo;
    private Usuario usuario; // Usuario logueado
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public GestionLibros(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Gestión de Libros");
        setSize(600, 400);
        setLayout(new BorderLayout());

      
        tablaLibros = new JTable();
        add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

        
        btnSolicitarPrestamo = new JButton("Solicitar préstamo");

      
        if (usuario != null && "usuario".equalsIgnoreCase(usuario.getRol())) {
            add(btnSolicitarPrestamo, BorderLayout.SOUTH);
        }

        // Accion del boton
        btnSolicitarPrestamo.addActionListener((ActionEvent e) -> {
            if (usuario != null) {
                new VentanaSolicitarLibro(usuario.getId()); // abre ventana emergente
            } else {
                JOptionPane.showMessageDialog(this, "Debe iniciar sesión para solicitar un préstamo.");
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}