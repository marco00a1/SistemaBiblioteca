package dao;

import conexion.Conexion;
import modelo.Prestamo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    private Connection conn;

    public PrestamoDAO() {
        conn = Conexion.getConnection();
    }

    public List<Prestamo> listarPrestamos() {
        List<Prestamo> lista = new ArrayList<>();
        try {
            String sql = "SELECT p.*, u.nombre as usuarioNombre, l.titulo as libroTitulo " +
                         "FROM prestamo p " +
                         "JOIN usuario u ON p.usuario_id=u.id " +
                         "JOIN libro l ON p.libro_id=l.id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setUsuarioId(rs.getInt("usuario_id"));
                p.setLibroId(rs.getInt("libro_id"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                p.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                p.setDevuelto(rs.getBoolean("devuelto"));
                p.setUsuarioNombre(rs.getString("usuarioNombre"));
                p.setLibroTitulo(rs.getString("libroTitulo"));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    //Listar prestamos de un usuario
    public List<Prestamo> listarPrestamosPorUsuario(int usuarioId) {
        List<Prestamo> lista = new ArrayList<>();
        try {
            String sql = "SELECT p.*, u.nombre as usuarioNombre, l.titulo as libroTitulo " +
                         "FROM prestamo p " +
                         "JOIN usuario u ON p.usuario_id=u.id " +
                         "JOIN libro l ON p.libro_id=l.id " +
                         "WHERE p.usuario_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setUsuarioId(rs.getInt("usuario_id"));
                p.setLibroId(rs.getInt("libro_id"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                p.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                p.setDevuelto(rs.getBoolean("devuelto"));
                p.setUsuarioNombre(rs.getString("usuarioNombre"));
                p.setLibroTitulo(rs.getString("libroTitulo"));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    //Nuevo metodo: listar prestamos de un usuario que estén Aprobados
    public List<Prestamo> listarPrestamosPorUsuarioYAprobados(int usuarioId) {
        List<Prestamo> lista = new ArrayList<>();
        try {
            String sql = "SELECT p.*, u.nombre as usuarioNombre, l.titulo as libroTitulo " +
                         "FROM prestamo p " +
                         "JOIN usuario u ON p.usuario_id=u.id " +
                         "JOIN libro l ON p.libro_id=l.id " +
                         "WHERE p.usuario_id=? AND p.estado='Aprobado'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setUsuarioId(rs.getInt("usuario_id"));
                p.setLibroId(rs.getInt("libro_id"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                p.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                p.setDevuelto(rs.getBoolean("devuelto"));
                p.setUsuarioNombre(rs.getString("usuarioNombre"));
                p.setLibroTitulo(rs.getString("libroTitulo"));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Prestamo obtenerPorId(int id) {
        try {
            String sql = "SELECT * FROM prestamo WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setUsuarioId(rs.getInt("usuario_id"));
                p.setLibroId(rs.getInt("libro_id"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                p.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                p.setDevuelto(rs.getBoolean("devuelto"));
                return p;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertar(Prestamo p) {
        try {
            String sql = "INSERT INTO prestamo (usuario_id, libro_id, fecha_prestamo, fecha_devolucion, devuelto) VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getUsuarioId());
            ps.setInt(2, p.getLibroId());
            ps.setDate(3, p.getFechaPrestamo()!=null ? new java.sql.Date(p.getFechaPrestamo().getTime()): null);
            ps.setDate(4, p.getFechaDevolucion()!=null ? new java.sql.Date(p.getFechaDevolucion().getTime()): null);
            ps.setBoolean(5, p.isDevuelto());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizar(Prestamo p) {
        try {
            String sql = "UPDATE prestamo SET usuario_id=?, libro_id=?, fecha_prestamo=?, fecha_devolucion=?, devuelto=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getUsuarioId());
            ps.setInt(2, p.getLibroId());
            ps.setDate(3, p.getFechaPrestamo()!=null ? new java.sql.Date(p.getFechaPrestamo().getTime()): null);
            ps.setDate(4, p.getFechaDevolucion()!=null ? new java.sql.Date(p.getFechaDevolucion().getTime()): null);
            ps.setBoolean(5, p.isDevuelto());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM prestamo WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}