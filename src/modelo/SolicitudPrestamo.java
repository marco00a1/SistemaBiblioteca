package modelo;

import java.util.Date;

public class SolicitudPrestamo {
    private int id;
    private int usuarioId;
    private int libroId;
    private Date fechaSolicitud;
    private String estado; // PENDIENTE, APROBADA, RECHAZADA
    private String comentario;

  
    private String usuarioNombre;
    private String libroTitulo;

    public SolicitudPrestamo() {}

    public SolicitudPrestamo(int id, int usuarioId, int libroId, Date fechaSolicitud, String estado, String comentario) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
        this.comentario = comentario;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getLibroTitulo() { return libroTitulo; }
    public void setLibroTitulo(String libroTitulo) { this.libroTitulo = libroTitulo; }
}