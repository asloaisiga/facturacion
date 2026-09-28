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
    private void abrirCategorias() {
        abrirVentana(
                "/ni/edu/uam/facturacion/fxml/categoria-view.fxml",
                "Categorías"
        );
    }

    @FXML
    private void abrirProductos() {
        abrirVentana(
                "/ni/edu/uam/facturacion/fxml/producto-view.fxml",
                "Productos"
        );
    }

    private void abrirVentana(String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FacturacionApplication.class.getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println(
                    "Error al abrir ventana: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    @FXML
    private void salir() {
        Platform.exit();
    }
}