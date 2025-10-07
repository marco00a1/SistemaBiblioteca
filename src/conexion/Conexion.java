package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
	/*
    private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=SistemaBiblioteca;encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456$";
    */
    
    private static final String URL =
            "jdbc:sqlserver://TI-03-2:1433;databaseName=SistemaBiblioteca;encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    public static Connection getConnection() {
        Connection cn = null;
        try {
            cn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("conectado a SQL Server correctamente");
        } catch (SQLException e) {
            System.err.println("Error de conexion: " + e.getMessage());
        }
        return cn;
    }
}