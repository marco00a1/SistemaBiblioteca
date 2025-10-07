package dao;

import conexion.Conexion;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Registrar nuevo usuario
    public boolean registrarUsuario(Usuario u) {
        String sql = "INSERT INTO Usuario (nombre, email, password, rol, socio, activo, deuda) VALUES (?,?,?,?,0,1,0)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            // Si el rol es nulo, lo guardamos como "usuario"
            ps.setString(4, (u.getRol() == null || u.getRol().isEmpty()) ? "usuario" : u.getRol());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    // Login
    public Usuario login(String email, String password) {
        String sql = "SELECT * FROM Usuario WHERE email=? AND password=? AND activo=1";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        safeRol(rs.getString("rol")),
                        rs.getBoolean("activo"),
                        rs.getDouble("deuda"),
                        rs.getBoolean("socio")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en login: " + e.getMessage());
        }
        return null;
    }

    // Listar usuarios
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        safeRol(rs.getString("rol")),
                        rs.getBoolean("activo"),
                        rs.getDouble("deuda"),
                        rs.getBoolean("socio")
                );
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // Actualizar usuario
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE Usuario SET nombre=?, email=?, password=?, rol=?, activo=?, socio=? WHERE id=?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, safeRol(u.getRol()));
            ps.setBoolean(5, u.isActivo());
            ps.setBoolean(6, u.isSocio());
            ps.setInt(7, u.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // Eliminar usuario
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Usuario WHERE id=?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    // Solicitar libro
    public boolean solicitarLibro(int usuarioId, int libroId) {
        String sql = "INSERT INTO SolicitudPrestamo (usuario_id, libro_id, estado, fecha_solicitud) VALUES (?,?, 'Pendiente', GETDATE())";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al solicitar libro: " + e.getMessage());
            return false;
        }
    }

    // Listar solicitudes (ordenadas por ID)
    public List<String> listarSolicitudesPendientes() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT s.id, u.nombre AS usuario, l.titulo AS libro, s.fecha_solicitud, s.estado, s.comentario " +
                     "FROM SolicitudPrestamo s " +
                     "JOIN Usuario u ON s.usuario_id = u.id " +
                     "JOIN Libro l ON s.libro_id = l.id " +
                     "ORDER BY s.id ASC";

        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String info = "Solicitud #" + rs.getInt("id") +
                              " | Usuario: " + rs.getString("usuario") +
                              " | Libro: " + rs.getString("libro") +
                              " | Fecha: " + rs.getDate("fecha_solicitud") +
                              " | Estado: " + rs.getString("estado") +
                              " | Comentario: " + (rs.getString("comentario") != null ? rs.getString("comentario") : "");
                lista.add(info);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar solicitudes: " + e.getMessage());
        }
        return lista;
    }

    // Aprobar solicitud
    public boolean aprobarSolicitud(int solicitudId) {
        String sql = "UPDATE SolicitudPrestamo SET estado='Aprobada' WHERE id=?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, solicitudId);
            if (ps.executeUpdate() > 0) {
                String sql2 = "UPDATE Usuario SET socio=1 WHERE id = (SELECT usuario_id FROM SolicitudPrestamo WHERE id=?)";
                try (PreparedStatement ps2 = cn.prepareStatement(sql2)) {
                    ps2.setInt(1, solicitudId);
                    ps2.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al aprobar solicitud: " + e.getMessage());
        }
        return false;
    }

    // Rechazar solicitud
    public boolean rechazarSolicitud(int solicitudId) {
        String sql = "UPDATE SolicitudPrestamo SET estado='Rechazada' WHERE id=?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, solicitudId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al rechazar solicitud: " + e.getMessage());
            return false;
        }
    }

    // Registrar (alias de registrarUsuario)
    public boolean registrar(Usuario u) {
        return registrarUsuario(u);
    }

    // Buscar usuario por id
    public Usuario loginById(int id) {
        String sql = "SELECT * FROM Usuario WHERE id=?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        safeRol(rs.getString("rol")),
                        rs.getBoolean("activo"),
                        rs.getDouble("deuda"),
                        rs.getBoolean("socio")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en loginById: " + e.getMessage());
        }
        return null;
    }

    // --- Método privado para evitar nulls en rol ---
    private String safeRol(String rol) {
        return (rol == null || rol.trim().isEmpty()) ? "usuario" : rol;
    }
}