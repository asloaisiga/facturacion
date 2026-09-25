package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.model.Categoria;

public class MenuPrincipalController {

    @FXML
    private TextField txtNombre;

    @FXML
    private CheckBox chkActiva;

    @FXML
    private TableView<Categoria> tblCategorias;

    @FXML
    private TableColumn<Categoria, Integer> colId;

    @FXML
    private TableColumn<Categoria, String> colNombre;

    @FXML
    private TableColumn<Categoria, Boolean> colActiva;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getId()).asObject());

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNombre()));

        colActiva.setCellValueFactory(data ->
                new javafx.beans.property.SimpleBooleanProperty(
                        data.getValue().isActiva()));

        chkActiva.setSelected(true);
        cargarCategorias();
    }

    @FXML
    private void guardarCategoria() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Debe ingresar el nombre de la categoría.");
            return;
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setActiva(chkActiva.isSelected());

        if (categoriaDAO.guardar(categoria)) {
            limpiar();
            cargarCategorias();
        } else {
            mostrarAlerta("No se pudo guardar la categoría.");
        }
    }

    @FXML
    private void eliminarCategoria() {
        Categoria categoria = tblCategorias.getSelectionModel().getSelectedItem();

        if (categoria == null) {
            mostrarAlerta("Seleccione una categoría.");
            return;
        }

        if (categoriaDAO.eliminar(categoria.getId())) {
            limpiar();
            cargarCategorias();
        } else {
            mostrarAlerta("No se pudo eliminar la categoría.");
        }
    }

    @FXML
    private void limpiar() {
        txtNombre.clear();
        chkActiva.setSelected(true);
        tblCategorias.getSelectionModel().clearSelection();
    }

    private void cargarCategorias() {
        tblCategorias.setItems(
                FXCollections.observableArrayList(categoriaDAO.listar())
        );
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Categorías");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}