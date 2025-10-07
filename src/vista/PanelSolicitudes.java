	package vista;
	
	import dao.SolicitudDAO;
	import modelo.SolicitudPrestamo;
	
	import javax.swing.*;
	import javax.swing.table.DefaultTableModel;
	import java.awt.*;
	import java.util.List;
	
	public class PanelSolicitudes extends JPanel {
	    private JTable tabla;
	    private DefaultTableModel modelo;
	    private SolicitudDAO solicitudDAO = new SolicitudDAO();
	
	    public PanelSolicitudes() {
	        setLayout(new BorderLayout());
	
	      
	        modelo = new DefaultTableModel(new String[]{
	                "ID", "Usuario", "Libro", "Fecha Solicitud", "Estado", "Comentario"
	        }, 0);
	        tabla = new JTable(modelo);
	
	        JScrollPane scroll = new JScrollPane(tabla);
	        add(scroll, BorderLayout.CENTER);
	
	      
	        JPanel panelBotones = new JPanel();
	        JButton btnAprobar = new JButton("Aprobar");
	        JButton btnRechazar = new JButton("Rechazar");
	        JButton btnPendiente = new JButton("Pendiente");
	        JButton btnRefrescar = new JButton("Refrescar");
	
	        panelBotones.add(btnAprobar);
	        panelBotones.add(btnRechazar);
	        panelBotones.add(btnPendiente);
	        panelBotones.add(btnRefrescar);
	        add(panelBotones, BorderLayout.SOUTH);
	
	   
	        btnRefrescar.addActionListener(e -> cargarSolicitudes());
	
	        btnAprobar.addActionListener(e -> cambiarEstado("APROBADA"));
	        btnRechazar.addActionListener(e -> {
	            int fila = tabla.getSelectedRow();
	            if (fila >= 0) {
	                String comentario = JOptionPane.showInputDialog(this, "Motivo de rechazo:");
	                if (comentario != null) {
	                    int id = (int) modelo.getValueAt(fila, 0);
	                    solicitudDAO.actualizarEstado(id, "RECHAZADA");
	                    modelo.setValueAt("RECHAZADA", fila, 4);
	                    modelo.setValueAt(comentario, fila, 5);
	                }
	            } else {
	                JOptionPane.showMessageDialog(this, "Selecciona una solicitud");
	            }
	        });
	        btnPendiente.addActionListener(e -> cambiarEstado("PENDIENTE"));
	
	      
	        cargarSolicitudes();
	    }
	
	    private void cargarSolicitudes() {
	        modelo.setRowCount(0); // limpiar
	        List<SolicitudPrestamo> lista = solicitudDAO.listarSolicitudes();
	        for (SolicitudPrestamo s : lista) {
	            modelo.addRow(new Object[]{
	                    s.getId(),
	                    s.getUsuarioNombre(),
	                    s.getLibroTitulo(),
	                    s.getFechaSolicitud(),
	                    s.getEstado(),
	                    s.getComentario()
	            });
	        }
	    }
	
	    private void cambiarEstado(String nuevoEstado) {
	        int fila = tabla.getSelectedRow();
	        if (fila >= 0) {
	            int id = (int) modelo.getValueAt(fila, 0);
	            if (solicitudDAO.actualizarEstado(id, nuevoEstado)) {
	                modelo.setValueAt(nuevoEstado, fila, 4);
	                if (nuevoEstado.equals("APROBADA")) {
	                    modelo.setValueAt("✔️ Aprobado", fila, 5);
	                } else if (nuevoEstado.equals("PENDIENTE")) {
	                    modelo.setValueAt("", fila, 5);
	                }
	                JOptionPane.showMessageDialog(this, "Solicitud actualizada");
	            }
	        } else {
	            JOptionPane.showMessageDialog(this, "Selecciona una solicitud");
	        }
	    }
	}