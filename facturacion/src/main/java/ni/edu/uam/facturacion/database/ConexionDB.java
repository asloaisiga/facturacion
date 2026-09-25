package ni.edu.uam.facturacion.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/tienda_javafx";

    private static final String USUARIO = "postgres";

    private static final String PASSWORD = "TU_CONTRASEÑA";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    public static void main(String[] args) {

        try (Connection connection = getConnection()) {

            System.out.println("Conexión exitosa con PostgreSQL");

        } catch (SQLException e) {

            System.out.println("Error al conectar con PostgreSQL");
            System.out.println(e.getMessage());
        }
    }
}