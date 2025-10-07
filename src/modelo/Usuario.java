package modelo;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private boolean activo;
    private double deuda;
    private boolean socio;

    public Usuario() {}

    public Usuario(int id, String nombre, String email, String password, String rol, boolean activo, double deuda, boolean socio){
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
        this.deuda = deuda;
        this.socio = socio;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public double getDeuda() { return deuda; }
    public void setDeuda(double deuda) { this.deuda = deuda; }

    public boolean isSocio() { return socio; }
    public void setSocio(boolean socio) { this.socio = socio; }

    @Override
    public String toString() {
        return nombre;
    }
}