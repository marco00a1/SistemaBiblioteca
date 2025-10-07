package vista;

import dao.LibroDAO;
import dao.SolicitudDAO;
import modelo.Libro;
import modelo.Usuario;
import modelo.SolicitudPrestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class PanelLibros extends JPanel {
    private LibroDAO libroDAO;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JCheckBox chkDisponible;

    private Usuario usuario;
    private Consumer<String> mostrarMensaje; 

  
    private SolicitudDAO solicitudDAO = new SolicitudDAO();

   
    public PanelLibros(Usuario usuario, Consumer<String> mostrarMensaje) {
        this.usuario = usuario;
        this.mostrarMensaje = mostrarMensaje;
        libroDAO = new LibroDAO();
        setLayout(new BorderLayout());

        // Tabla
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Titulo", "Autor", "Disponible"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

     
        if (usuario.getRol().equalsIgnoreCase("admin")) {
            JPanel panelForm = new JPanel(new GridLayout(3, 2, 5, 5));
            txtTitulo = new JTextField();
            txtAutor = new JTextField();
            chkDisponible = new JCheckBox("Disponible");

            panelForm.add(new JLabel("Titulo:"));
            panelForm.add(txtTitulo);
            panelForm.add(new JLabel("Autor:"));
            panelForm.add(txtAutor);
            panelForm.add(new JLabel("Estado:"));
            panelForm.add(chkDisponible);

            add(panelForm, BorderLayout.NORTH);

            JPanel panelBotones = new JPanel();
            JButton btnAgregar = new JButton("Agregar");
            JButton btnEditar = new JButton("Editar");
            JButton btnEliminar = new JButton("Eliminar");

            panelBotones.add(btnAgregar);
            panelBotones.add(btnEditar);
            panelBotones.add(btnEliminar);

            add(panelBotones, BorderLayout.SOUTH);

           
            btnAgregar.addActionListener(e -> {
                String titulo = txtTitulo.getText().trim();
                String autor = txtAutor.getText().trim();
                boolean disponible = chkDisponible.isSelected();

                if (!titulo.isEmpty() && !autor.isEmpty()) {
                    Libro nuevo = new Libro(titulo, autor, disponible);
                    libroDAO.insertarLibro(nuevo);
                    cargarLibros();
                    limpiarCampos();
                    mostrarMensaje.accept("Libro agregado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Debe ingresar titulo y autor");
                }
            });

            btnEditar.addActionListener(e -> {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    int id = (int) modeloTabla.getValueAt(fila, 0);
                    String titulo = txtTitulo.getText().trim();
                    String autor = txtAutor.getText().trim();
                    boolean disponible = chkDisponible.isSelected();

                    Libro editado = new Libro(id, titulo, autor, disponible);
                    libroDAO.actualizarLibro(editado);
                    cargarLibros();
                    limpiarCampos();
                    mostrarMensaje.accept("Libro editado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Seleccione un libro para editar");
                }
            });

            btnEliminar.addActionListener(e -> {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    int id = (int) modeloTabla.getValueAt(fila, 0);
                    libroDAO.eliminarLibro(id);
                    cargarLibros();
                    mostrarMensaje.accept("Libro eliminado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Seleccione un libro para eliminar");
                }
            });

           
            tabla.getSelectionModel().addListSelectionListener(e -> {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    txtTitulo.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtAutor.setText(modeloTabla.getValueAt(fila, 2).toString());
                    chkDisponible.setSelected((boolean) modeloTabla.getValueAt(fila, 3));
                }
            });
        } else {
            
            JPanel panelSolicitud = new JPanel();
            JButton btnSolicitar = new JButton("Solicitar libro seleccionado");
            panelSolicitud.add(btnSolicitar);
            add(panelSolicitud, BorderLayout.SOUTH);

            btnSolicitar.addActionListener(e -> solicitarLibro());
        }

        // Cargar datos
        cargarLibros();
    }

    private void cargarLibros() {
        modeloTabla.setRowCount(0);
        List<Libro> lista = libroDAO.listarLibros();
        for (Libro l : lista) {
          
            modeloTabla.addRow(new Object[]{
                    l.getId(),
                    l.getTitulo(),
                    l.getAutor(),
                    l.isDisponible()
            });
        }
    }

    private void limpiarCampos() {
        if(txtTitulo!=null) txtTitulo.setText("");
        if(txtAutor!=null) txtAutor.setText("");
        if(chkDisponible!=null) chkDisponible.setSelected(false);
    }

    // Nuevo: solicitar libro (para usuarios no socio)
    private void solicitarLibro() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para solicitar");
            return;
        }
        int idLibro = (int) modeloTabla.getValueAt(fila, 0);
        boolean disponible = (boolean) modeloTabla.getValueAt(fila, 3);

       
        if (!disponible) {
            JOptionPane.showMessageDialog(this, "El libro no está disponible actualmente");
            return;
        }

     
        if (usuario.isSocio()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Eres socio. ¿Deseas crear una solicitud para que el admin la apruebe igual? (si prefieres crear préstamo directo usa el módulo de préstamos)", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

     
        String comentario = JOptionPane.showInputDialog(this, "Comentario:");
        SolicitudPrestamo s = new SolicitudPrestamo();
        s.setUsuarioId(usuario.getId());
        s.setLibroId(idLibro);
        s.setFechaSolicitud(new Date());
        s.setEstado("PENDIENTE");
        s.setComentario(comentario);

        boolean ok = solicitudDAO.insertarSolicitud(s);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Solicitud enviada correctamente. El administrador la revisara.");
        } else {
            JOptionPane.showMessageDialog(this, "Error al enviar la solicitud");
        }
    }
}