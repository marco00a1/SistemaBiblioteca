package vista;

import dao.UsuarioDAO;
import dao.SolicitudDAO;
import dao.PrestamoDAO;
import dao.LibroDAO;
import modelo.Usuario;
import modelo.SolicitudPrestamo;
import modelo.Prestamo;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Date;
import java.util.function.Consumer;

public class PanelUsuarios extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private UsuarioDAO dao = new UsuarioDAO();

    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtPassword;
    private JCheckBox chkActivo;
    private JCheckBox chkSocio;

    private JButton btnAgregar, btnEditar, btnEliminar;

    private int idSeleccionado = -1;

    private Consumer<String> mostrarMensaje;

    private SolicitudDAO solicitudDAO = new SolicitudDAO();
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    private LibroDAO libroDAO = new LibroDAO();

    private JTable tablaSolicitudes;
    private DefaultTableModel modeloSolicitudes;

    public PanelUsuarios(Consumer<String> mostrarMensaje) {
        this.mostrarMensaje = mostrarMensaje;
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

       
        JPanel panelUsuarios = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        txtNombre = new JTextField();
        txtEmail = new JTextField();
        txtPassword = new JTextField();
        chkActivo = new JCheckBox("Activo");
        chkSocio = new JCheckBox("Socio");

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Activo:"));
        formPanel.add(chkActivo);
        formPanel.add(new JLabel("Socio:"));
        formPanel.add(chkSocio);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        panelUsuarios.add(formPanel, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Password", "Activo", "Socio"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        cargarUsuarios();

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                idSeleccionado = (int) tabla.getValueAt(fila, 0);
                txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                txtEmail.setText(tabla.getValueAt(fila, 2).toString());
                txtPassword.setText(tabla.getValueAt(fila, 3).toString());
                chkActivo.setSelected((boolean) tabla.getValueAt(fila, 4));
                chkSocio.setSelected((boolean) tabla.getValueAt(fila, 5));
            }
        });

        panelUsuarios.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        btnAgregar = new JButton("Agregar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        btnAgregar.addActionListener(e -> agregarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());

        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        panelUsuarios.add(buttonPanel, BorderLayout.SOUTH);

        tabs.add("Usuarios", panelUsuarios);

        
        JPanel panelSolicitudes = new JPanel(new BorderLayout());
        modeloSolicitudes = new DefaultTableModel(new String[]{"ID", "Usuario", "Libro", "Fecha", "Estado", "Comentario"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaSolicitudes = new JTable(modeloSolicitudes);
        cargarSolicitudes();

        panelSolicitudes.add(new JScrollPane(tablaSolicitudes), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel();
        JButton btnAprobar = new JButton("Aprobar");
        JButton btnRechazar = new JButton("Rechazar");
        panelAcciones.add(btnAprobar);
        panelAcciones.add(btnRechazar);
        panelSolicitudes.add(panelAcciones, BorderLayout.SOUTH);

        btnAprobar.addActionListener(e -> aprobarSolicitud());
        btnRechazar.addActionListener(e -> rechazarSolicitud());

        tabs.add("Solicitudes", panelSolicitudes);
        add(tabs, BorderLayout.CENTER);
    }

    

    public void cargarUsuarios() {
        modelo.setRowCount(0);
        List<Usuario> usuarios = dao.listarUsuarios();
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[]{
                    u.getId(),
                    u.getNombre(),
                    u.getEmail(),
                    u.getPassword(),
                    u.isActivo(),
                    u.isSocio()
            });
        }
    }

    public void agregarUsuario() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        boolean activo = chkActivo.isSelected();
        boolean socio = chkSocio.isSelected();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos");
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(password);
        u.setActivo(activo);
        u.setSocio(socio);

        boolean ok = dao.registrar(u);
        if (ok) {
            cargarUsuarios();
            limpiarFormulario();
            mostrarMensaje.accept("Usuario agregado correctamente");
        } else {
            JOptionPane.showMessageDialog(this, "El email ya existe");
        }
    }

    public void editarUsuario() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        boolean activo = chkActivo.isSelected();
        boolean socio = chkSocio.isSelected();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos");
            return;
        }

        Usuario u = new Usuario();
        u.setId(idSeleccionado);
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(password);
        u.setActivo(activo);   
        u.setSocio(socio);     

        dao.actualizar(u);
        cargarUsuarios();
        limpiarFormulario();
        mostrarMensaje.accept("Usuario editado correctamente");
    }

    public void eliminarUsuario() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.eliminar(idSeleccionado);
            cargarUsuarios();
            limpiarFormulario();
            mostrarMensaje.accept("Usuario eliminado correctamente");
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        chkActivo.setSelected(false);
        chkSocio.setSelected(false);
    }

    private void cargarSolicitudes() {
        modeloSolicitudes.setRowCount(0);
        List<SolicitudPrestamo> lista = solicitudDAO.listarSolicitudes();
        for (SolicitudPrestamo s : lista) {
            modeloSolicitudes.addRow(new Object[]{
                    s.getId(),
                    s.getUsuarioNombre(),
                    s.getLibroTitulo(),
                    s.getFechaSolicitud(),
                    s.getEstado(),
                    s.getComentario()
            });
        }
    }

    private SolicitudPrestamo obtenerSolicitudDesdeTabla(int fila) {
        try {
            int id = (int) modeloSolicitudes.getValueAt(fila, 0);
            List<SolicitudPrestamo> todas = solicitudDAO.listarSolicitudes();
            for (SolicitudPrestamo sp : todas) {
                if (sp.getId() == id) return sp;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void aprobarSolicitud() {
        int fila = tablaSolicitudes.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Seleccione una solicitud"); return; }
        SolicitudPrestamo s = obtenerSolicitudDesdeTabla(fila);
        if (s == null) return;

        if (!solicitudDAO.actualizarEstado(s.getId(), "APROBADA")) {
            JOptionPane.showMessageDialog(this, "Error al actualizar solicitud");
            return;
        }

        UsuarioDAO udao = new UsuarioDAO();
        Usuario usuarioBD = udao.loginById(s.getUsuarioId());
        if (usuarioBD != null) {
            usuarioBD.setSocio(true);
            udao.actualizar(usuarioBD);
        }

        Prestamo p = new Prestamo();
        p.setUsuarioId(s.getUsuarioId());
        p.setLibroId(s.getLibroId());
        p.setFechaPrestamo(new Date());
        p.setFechaDevolucion(null);
        p.setDevuelto(false);
        prestamoDAO.insertar(p);

        List<Libro> listaLib = libroDAO.listarLibros();
        for (Libro lb : listaLib) {
            if (lb.getId() == s.getLibroId()) {
                lb.setDisponible(false);
                libroDAO.actualizarLibro(lb);
                break;
            }
        }

        cargarSolicitudes();
        cargarUsuarios();
        JOptionPane.showMessageDialog(this, "Solicitud aprobada y préstamo creado. Usuario hecho socio.");
    }

    private void rechazarSolicitud() {
        int fila = tablaSolicitudes.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Seleccione una solicitud"); return; }
        int idSolicitud = (int) modeloSolicitudes.getValueAt(fila, 0);
        if (solicitudDAO.actualizarEstado(idSolicitud, "RECHAZADA")) {
            cargarSolicitudes();
            JOptionPane.showMessageDialog(this, "Solicitud rechazada");
        } else {
            JOptionPane.showMessageDialog(this, "Error al rechazar solicitud");
        }
    }
}