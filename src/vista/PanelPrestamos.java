package vista;

import dao.PrestamoDAO;
import dao.UsuarioDAO;
import dao.LibroDAO;
import modelo.Prestamo;
import modelo.Usuario;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class PanelPrestamos extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private PrestamoDAO dao = new PrestamoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private LibroDAO libroDAO = new LibroDAO();

    private Usuario usuario; 
    private Consumer<String> mostrarMensaje;

    private JComboBox<Usuario> cbUsuario;
    private JComboBox<Libro> cbLibro;
    private JTextField txtFechaPrestamo;
    private JTextField txtFechaDevolucion;
    private JCheckBox chkDevuelto;

    private JButton btnAgregar, btnEditar, btnEliminar;

    private int idSeleccionado = -1;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public PanelPrestamos(Usuario usuario, Consumer<String> mostrarMensaje) {
        this.usuario = usuario;
        this.mostrarMensaje = mostrarMensaje;
        sdf.setLenient(false);
        setLayout(new BorderLayout());

        // ---------- PANEL FORMULARIO SOLO PARA ADMIN ----------
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));

            cbUsuario = new JComboBox<>();
            cbLibro = new JComboBox<>();
            txtFechaPrestamo = new JTextField();
            txtFechaDevolucion = new JTextField();
            chkDevuelto = new JCheckBox("Devuelto");

            cargarUsuarios();
            cargarLibros();

            formPanel.add(new JLabel("Usuario:"));
            formPanel.add(cbUsuario);
            formPanel.add(new JLabel("Libro:"));
            formPanel.add(cbLibro);

            formPanel.add(new JLabel("Fecha Préstamo:"));
            formPanel.add(txtFechaPrestamo);
            formPanel.add(new JLabel("Fecha Devolución:"));
            formPanel.add(txtFechaDevolucion);

            formPanel.add(new JLabel(""));
            formPanel.add(chkDevuelto);
            formPanel.add(new JLabel(""));
            formPanel.add(new JLabel(""));

            add(formPanel, BorderLayout.NORTH);
        }

        // ---------- TABLA ----------
        modelo = new DefaultTableModel(new String[]{"ID", "Usuario", "Libro", "Préstamo", "Devolución", "Devuelto"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //socios no pueden editar celdas
                return "admin".equalsIgnoreCase(usuario.getRol());
            }
        };
        tabla = new JTable(modelo);
        cargarPrestamos();
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ---------- BOTONES SOLO PARA ADMIN ----------
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            JPanel buttonPanel = new JPanel();
            btnAgregar = new JButton("Agregar");
            btnEditar = new JButton("Editar");
            btnEliminar = new JButton("Eliminar");

            btnAgregar.addActionListener(e -> agregarPrestamo());
            btnEditar.addActionListener(e -> editarPrestamo());
            btnEliminar.addActionListener(e -> eliminarPrestamo());

            buttonPanel.add(btnAgregar);
            buttonPanel.add(btnEditar);
            buttonPanel.add(btnEliminar);

            add(buttonPanel, BorderLayout.SOUTH);
        }

        // ---------- SELECCION TABLA SOLO PARA ADMIN ----------
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            tabla.getSelectionModel().addListSelectionListener(e -> {
                if(!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                    int fila = tabla.getSelectedRow();
                    idSeleccionado = (int) tabla.getValueAt(fila, 0);

                    String nombreUsuario = tabla.getValueAt(fila, 1).toString();
                    String tituloLibro = tabla.getValueAt(fila, 2).toString();

                    for(int i=0; i<cbUsuario.getItemCount(); i++) {
                        if(cbUsuario.getItemAt(i).getNombre().equals(nombreUsuario)) {
                            cbUsuario.setSelectedIndex(i);
                            break;
                        }
                    }
                    for(int i=0; i<cbLibro.getItemCount(); i++) {
                        if(cbLibro.getItemAt(i).getTitulo().equals(tituloLibro)) {
                            cbLibro.setSelectedIndex(i);
                            break;
                        }
                    }

                    txtFechaPrestamo.setText(tabla.getValueAt(fila, 3).toString());
                    txtFechaDevolucion.setText(tabla.getValueAt(fila, 4).toString());
                    chkDevuelto.setSelected(tabla.getValueAt(fila, 5).toString().equalsIgnoreCase("Si"));
                }
            });
        }
    }

    private void cargarUsuarios() {
        cbUsuario.removeAllItems();
        List<Usuario> usuarios = usuarioDAO.listarUsuarios();
        for (Usuario u : usuarios) {
            if (u.isActivo() && "usuario".equalsIgnoreCase(u.getRol())) {
                cbUsuario.addItem(u);
            }
        }
    }

    private void cargarLibros() {
        cbLibro.removeAllItems();
        List<Libro> libros = libroDAO.listarLibros();
        for(Libro l : libros) cbLibro.addItem(l);
    }

    public void cargarPrestamos() {
        modelo.setRowCount(0);
        List<Prestamo> prestamos;

        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            prestamos = dao.listarPrestamos();
        } else {
            //Socio → solo sus prestamos
            prestamos = dao.listarPrestamosPorUsuario(usuario.getId());
        }

        for(Prestamo p : prestamos) {
            String fp = p.getFechaPrestamo()!=null ? sdf.format(p.getFechaPrestamo()) : "";
            String fd = p.getFechaDevolucion()!=null ? sdf.format(p.getFechaDevolucion()) : "";
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getUsuarioNombre(),
                    p.getLibroTitulo(),
                    fp,
                    fd,
                    p.isDevuelto()?"Si":"No"
            });
        }
    }

    private void agregarPrestamo() {
        try {
            Usuario u = (Usuario) cbUsuario.getSelectedItem();
            Libro l = (Libro) cbLibro.getSelectedItem();
            if(u==null || l==null) return;

            if(u.getDeuda() > 0) {
                JOptionPane.showMessageDialog(this, "El usuario tiene deuda pendiente. No se puede registrar el préstamo.");
                return;
            }

            Date fp = parseFecha(txtFechaPrestamo.getText());
            if(fp == null) fp = new Date();
            Date fd = parseFecha(txtFechaDevolucion.getText());

            Prestamo p = new Prestamo();
            p.setUsuarioId(u.getId());
            p.setLibroId(l.getId());
            p.setFechaPrestamo(fp);
            p.setFechaDevolucion(fd);
            p.setDevuelto(chkDevuelto.isSelected());

            dao.insertar(p);

            if (!chkDevuelto.isSelected()) {
                l.setDisponible(false);
                libroDAO.actualizar(l);
            }

            cargarPrestamos();
            mostrarMensaje.accept("Préstamo agregado correctamente");
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editarPrestamo() {
        if(idSeleccionado<0){
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo para editar");
            return;
        }
        try {
            Usuario u = (Usuario) cbUsuario.getSelectedItem();
            Libro l = (Libro) cbLibro.getSelectedItem();
            if(u==null || l==null) return;

            if(u.getDeuda() > 0) {
                JOptionPane.showMessageDialog(this, "El usuario tiene deuda pendiente. No se puede editar el préstamo.");
                return;
            }

            Date fp = parseFecha(txtFechaPrestamo.getText());
            if(fp == null) fp = new Date();
            Date fd = parseFecha(txtFechaDevolucion.getText());

            Prestamo p = new Prestamo();
            p.setId(idSeleccionado);
            p.setUsuarioId(u.getId());
            p.setLibroId(l.getId());
            p.setFechaPrestamo(fp);
            p.setFechaDevolucion(fd);
            p.setDevuelto(chkDevuelto.isSelected());

            dao.actualizar(p);

            if (chkDevuelto.isSelected()) {
                l.setDisponible(true);
            } else {
                l.setDisponible(false);
            }
            libroDAO.actualizar(l);

            cargarPrestamos();
            mostrarMensaje.accept("Préstamo editado correctamente");
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminarPrestamo() {
        if(idSeleccionado<0){
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo para eliminar");
            return;
        }
        try {
            Prestamo p = dao.obtenerPorId(idSeleccionado);
            if (p != null) {
                Libro l = libroDAO.obtenerPorId(p.getLibroId());
                if (l != null) {
                    l.setDisponible(true);
                    libroDAO.actualizar(l);
                }
            }

            dao.eliminar(idSeleccionado);
            cargarPrestamos();
            mostrarMensaje.accept("Préstamo eliminado correctamente");
            idSeleccionado = -1;
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private Date parseFecha(String fechaStr) throws ParseException {
        if(fechaStr==null || fechaStr.trim().isEmpty()) return null;
        return sdf.parse(fechaStr.trim());
    }
}