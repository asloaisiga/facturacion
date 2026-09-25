module ni.edu.uam.facturacion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires lombok;


    opens ni.edu.uam.facturacion to javafx.fxml;
    exports ni.edu.uam.facturacion;
}