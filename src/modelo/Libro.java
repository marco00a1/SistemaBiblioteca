package modelo;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private boolean disponible;

    public Libro() {}

    public Libro(int id, String titulo, String autor, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = disponible;
    }

    public Libro(String titulo, String autor, boolean disponible) {
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = disponible;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponible() { return disponible; }

    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }



    @Override
    public String toString() {
        return titulo + (disponible ? " (Disponible)" : " (No disponible)");
    }
}