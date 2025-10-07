package dao;

import conexion.Conexion;
import modelo.SolicitudPrestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

    // Insertar solicitud
    public boolean insertarSolicitud(SolicitudPrestamo s) {
        String sql = "INSERT INTO SolicitudPrestamo (usuario_id, libro_id, fecha_solicitud, estado, comentario) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, s.getUsuarioId());
            ps.setInt(2, s.getLibroId());
            ps.setDate(3, new java.sql.Date(s.getFechaSolicitud().getTime()));
            ps.setString(4, s.getEstado() == null ? "PENDIENTE" : s.getEstado());
            ps.setString(5, s.getComentario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Listar solicitudes (todas)
    public List<SolicitudPrestamo> listarSolicitudes() {
        List<SolicitudPrestamo> lista = new ArrayList<>();
        String sql = "SELECT sp.id, sp.usuario_id, sp.libro_id, sp.fecha_solicitud, sp.estado, sp.comentario, u.nombre AS usuario, l.titulo AS libro " +
                     "FROM SolicitudPrestamo sp " +
                     "INNER JOIN Usuario u ON sp.usuario_id = u.id " +
                     "INNER JOIN Libro l ON sp.libro_id = l.id " +
                     "ORDER BY sp.id ASC";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SolicitudPrestamo s = new SolicitudPrestamo();
                s.setId(rs.getInt("id"));
                s.setUsuarioId(rs.getInt("usuario_id"));
                s.setLibroId(rs.getInt("libro_id"));
                s.setFechaSolicitud(rs.getDate("fecha_solicitud"));
                s.setEstado(rs.getString("estado"));
                s.setComentario(rs.getString("comentario"));
                s.setUsuarioNombre(rs.getString("usuario"));
                s.setLibroTitulo(rs.getString("libro"));
                lista.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Listar solo pendientes
    public List<SolicitudPrestamo> listarPendientes() {
        List<SolicitudPrestamo> lista = new ArrayList<>();
        String sql = "SELECT sp.id, sp.usuario_id, sp.libro_id, sp.fecha_solicitud, sp.estado, sp.comentario, u.nombre AS usuario, l.titulo AS libro " +
                     "FROM SolicitudPrestamo sp " +
                     "INNER JOIN Usuario u ON sp.usuario_id = u.id " +
                     "INNER JOIN Libro l ON sp.libro_id = l.id " +
                     "WHERE sp.estado = 'PENDIENTE' " +
                     "ORDER BY sp.id ASC";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SolicitudPrestamo s = new SolicitudPrestamo();
                s.setId(rs.getInt("id"));
                s.setUsuarioId(rs.getInt("usuario_id"));
                s.setLibroId(rs.getInt("libro_id"));
                s.setFechaSolicitud(rs.getDate("fecha_solicitud"));
                s.setEstado(rs.getString("estado"));
                s.setComentario(rs.getString("comentario"));
                s.setUsuarioNombre(rs.getString("usuario"));
                s.setLibroTitulo(rs.getString("libro"));
                lista.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Actualizar estado (APROBADA / RECHAZADA / PENDIENTE)
    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE SolicitudPrestamo SET estado=? WHERE id=?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Rechazar con comentario
    public boolean rechazarConComentario(int id, String comentario) {
        String sql = "UPDATE SolicitudPrestamo SET estado='RECHAZADA', comentario=? WHERE id=?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, comentario);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}