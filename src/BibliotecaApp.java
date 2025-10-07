import vista.LoginFrame;

/* Prueba de commit. para el video v2 , video mas breve.*/

public class BibliotecaApp {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
