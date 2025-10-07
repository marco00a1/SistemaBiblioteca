package vista;

import dao.UsuarioDAO;
import conexion.Conexion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VentanaSolicitarLibro extends JFrame {
    private JComboBox<String> comboLibros;
    private JButton btnSolicitar;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private int usuarioId;

    public VentanaSolicitarLibro(int usuarioId) {
        this.usuarioId = usuarioId;

        setTitle("Solicitar Libro");
        setSize(400, 150);
        setLayout(new FlowLayout());

        comboLibros = new JComboBox<>();
        cargarLibrosDisponibles();
        add(comboLibros);

        btnSolicitar = new JButton("Solicitar");
        add(btnSolicitar);

        btnSolicitar.addActionListener((ActionEvent e) -> {
            String seleccionado = (String) comboLibros.getSelectedItem();
            if (seleccionado != null) {
                int libroId = Integer.parseInt(seleccionado.split(" - ")[0]); 
                if (usuarioDAO.solicitarLibro(usuarioId, libroId)) {
                    JOptionPane.showMessageDialog(this, "Solicitud enviada.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al solicitar libro.");
                }
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void cargarLibrosDisponibles() {
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT id, titulo FROM Libro WHERE disponible = 1");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comboLibros.addItem(rs.getInt("id") + " - " + rs.getString("titulo"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}