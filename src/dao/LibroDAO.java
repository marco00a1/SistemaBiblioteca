package dao;

import conexion.Conexion;
import modelo.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class LibroDAO {

    // Insertar libro
    public boolean insertarLibro(Libro libro) {
        String sql = "INSERT INTO Libro (titulo, autor, disponible) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setBoolean(3, libro.isDisponible());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar libro (alias de actualizarLibro para tu panel)
    public boolean actualizar(Libro libro) {
        return actualizarLibro(libro);
    }

    // Actualizar libro (método interno)
    public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE Libro SET titulo=?, autor=?, disponible=? WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setBoolean(3, libro.isDisponible());
            ps.setInt(4, libro.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener libro por ID
    public Libro obtenerPorId(int id) {
        String sql = "SELECT id, titulo, autor, disponible FROM Libro WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getBoolean("disponible")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Eliminar libro con validación de integridad
    public boolean eliminarLibro(int id) {
        String sql = "DELETE FROM Libro WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Si hay un error de referencia (libro con solicitudes/prestamos asociados)
            if (e.getMessage().contains("REFERENCE")) {
                JOptionPane.showMessageDialog(null,
                        "No se puede eliminar este libro porque tiene solicitudes o préstamos asociados.\n" +
                        "En su lugar, puede marcarlo como no disponible.",
                        "Error de integridad referencial",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // Listar libros
    public List<Libro> listarLibros() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, disponible FROM Libro";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro l = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getBoolean("disponible")
                );
                lista.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}