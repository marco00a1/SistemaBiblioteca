package vista;

import modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private Usuario usuarioLogueado;
    private JDesktopPane desktopPane;

    private JInternalFrame internalLibros;
    private JInternalFrame internalPrestamos;
    private JInternalFrame internalUsuarios;

    //Guardamos referencias directas a los paneles
    private PanelPrestamos panelPrestamos;
    private PanelLibros panelLibros;
    private PanelUsuarios panelUsuarios;

    public MainFrame(Usuario usuario) {
        this.usuarioLogueado = usuario;

        //Titulo con nombre y correo
        setTitle("Menú Biblioteca - " + usuario.getNombre() + " (" + usuario.getEmail() + ")");
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.WHITE);
        setContentPane(desktopPane);

        //Fondo
        JLabel fondo = new JLabel(new ImageIcon(MainFrame.class.getResource("/imagenes/fondo.jpg")));
        fondo.setBounds(0, 0, 1366, 768);
        desktopPane.add(fondo);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // ---- MENU LIBROS ----
        JMenu menuLibros = new JMenu("Libros");
        menuLibros.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-archivos.png")));
        JMenuItem verLibros = new JMenuItem("Ver Libros");
        verLibros.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-archivos.png")));
        verLibros.addActionListener(e -> abrirInternalFrame("Libros"));
        menuLibros.add(verLibros);
        menuBar.add(menuLibros);

        // ---- MENU PRESTAMOS ----
        if ("admin".equalsIgnoreCase(usuario.getRol()) || usuario.isSocio()) {
            JMenu menuPrestamos = new JMenu("Préstamos");
            menuPrestamos.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-lista.png")));
            JMenuItem gestionPrestamos = new JMenuItem("Gestión de Préstamos");
            gestionPrestamos.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-lista.png")));
            gestionPrestamos.addActionListener(e -> abrirInternalFrame("Prestamos"));
            menuPrestamos.add(gestionPrestamos);
            menuBar.add(menuPrestamos);
        }

        // ---- MENU USUARIOS ----
        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            JMenu menuUsuarios = new JMenu("Usuarios");
            menuUsuarios.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-archivos.png")));
            JMenuItem gestionUsuarios = new JMenuItem("Gestión de Usuarios");
            gestionUsuarios.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-archivos.png")));
            gestionUsuarios.addActionListener(e -> abrirInternalFrame("Usuarios"));
            menuUsuarios.add(gestionUsuarios);
            menuBar.add(menuUsuarios);
        }

        // ---- MENU SALIR ----
        JMenu menuSalir = new JMenu("Salir");
        menuSalir.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-buscar.png")));
        JMenuItem cerrarSesion = new JMenuItem("Cerrar Sesión");
        cerrarSesion.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-buscar.png")));
        cerrarSesion.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        JMenuItem salirApp = new JMenuItem("Salir del Sistema");
        salirApp.setIcon(new ImageIcon(MainFrame.class.getResource("/imagenes/x48-buscar.png")));
        salirApp.addActionListener(e -> System.exit(0));
        menuSalir.add(cerrarSesion);
        menuSalir.add(salirApp);
        menuBar.add(menuSalir);
    }

    //Metodo para crear internal frames
    private JInternalFrame crearInternalFrame(JPanel panel, String titulo, int width, int height) {
        JInternalFrame internal = new JInternalFrame(titulo, true, true, true, true);
        internal.setSize(width, height);
        internal.setContentPane(panel);
        internal.setVisible(true);
        desktopPane.add(internal);
        try { internal.setSelected(true); } catch (Exception e) { e.printStackTrace(); }
        return internal;
    }

    //Metodo para abrir internal frames centrados y recargar datos
    private void abrirInternalFrame(String nombre) {
        JInternalFrame frame = null;

        switch (nombre) {
            case "Libros":
                if (internalLibros == null || internalLibros.isClosed()) {
                    panelLibros = new PanelLibros(usuarioLogueado, this::mostrarMensaje);
                    internalLibros = crearInternalFrame(panelLibros, "Libros", 700, 400);
                }
                frame = internalLibros;
                break;

            case "Prestamos":
                if (internalPrestamos == null || internalPrestamos.isClosed()) {
                    panelPrestamos = new PanelPrestamos(usuarioLogueado, this::mostrarMensaje);
                    internalPrestamos = crearInternalFrame(panelPrestamos, "Préstamos", 700, 400);
                }
                frame = internalPrestamos;
                //Recargar siempre que se abra
                panelPrestamos.cargarPrestamos();
                break;

            case "Usuarios":
                if (internalUsuarios == null || internalUsuarios.isClosed()) {
                    panelUsuarios = new PanelUsuarios(this::mostrarMensaje);
                    internalUsuarios = crearInternalFrame(panelUsuarios, "Usuarios", 700, 400);
                }
                frame = internalUsuarios;
                //Recargar siempre que se abra
                panelUsuarios.cargarUsuarios();
                break;
        }

        if (frame != null) {
            frame.setLocation((desktopPane.getWidth() - frame.getWidth()) / 2,
                              (desktopPane.getHeight() - frame.getHeight()) / 2);
            frame.setVisible(true);
            try { frame.setSelected(true); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // Mostrar mensaje emergente desde los paneles
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public static void main(String[] args) {
        try {
            // Activar LookAndFeel de JTattoo
            UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            Usuario u = new Usuario();
            u.setNombre("Admin");
            u.setEmail("admin@correo.com");
            u.setRol("admin");
            u.setSocio(true);
            new MainFrame(u).setVisible(true);
        });
    }
}
