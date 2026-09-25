package ni.edu.uam.facturacion.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.FacturacionApplication;

import java.io.IOException;

public class MenuPrincipalController {
    @FXML
    private void abrirProductos() {
        try {
            FXMLLoader loader = new FXMLLoader(FacturacionApplication.class.getResource("/ni/edu/uam/facturacion/fxml/producto-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Productos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al abrir productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void salir() {
        Platform.exit();
    }
}