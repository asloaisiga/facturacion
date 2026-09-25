module ni.edu.uam.facturacion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens ni.edu.uam.facturacion to javafx.fxml;
    exports ni.edu.uam.facturacion;
}