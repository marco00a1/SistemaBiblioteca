package vista;

import dao.PrestamoDAO;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaHistorialPrestamos extends JFrame {
    private JTable tabla;
    private DefaultTableModel modelo;
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    private int usuarioId;

    public VentanaHistorialPrestamos(int usuarioId) {
        this.usuarioId = usuarioId;

        setTitle("Historial de Préstamos");
        setSize(600, 400);
        setLayout(new BorderLayout());

        modelo = new DefaultTableModel(new String[]{
                "ID", "Libro", "Fecha Préstamo", "Fecha Devolución", "Devuelto"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Los socios no pueden editar
            }
        };

        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        cargarHistorial();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void cargarHistorial() {
        modelo.setRowCount(0); // limpiar

        List<Prestamo> lista = prestamoDAO.listarPrestamosPorUsuario(usuarioId);

        for (Prestamo p : lista) {
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getLibroTitulo(),
                    p.getFechaPrestamo(),
                    p.getFechaDevolucion(),
                    p.isDevuelto() ? "Sí" : "No"
            });
        }
    }
}