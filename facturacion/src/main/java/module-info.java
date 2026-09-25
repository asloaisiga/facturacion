module ni.edu.uam.facturacion {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.facturacion to javafx.fxml;
    exports ni.edu.uam.facturacion;
}