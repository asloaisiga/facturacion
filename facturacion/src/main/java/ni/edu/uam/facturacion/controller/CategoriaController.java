package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.model.Categoria;

import java.util.Optional;

public class CategoriaController {

    @FXML
    private TextField txtNombre;

    @FXML
    private CheckBox chkActiva;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<String> cmbFiltroEstado;

    @FXML
    private TableView<Categoria> tblCategorias;

    @FXML
    private TableColumn<Categoria, Integer> colId;

    @FXML
    private TableColumn<Categoria, String> colNombre;

    @FXML
    private TableColumn<Categoria, Boolean> colActiva;

    @FXML
    private TableColumn<Categoria, Void> colAcciones;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();

    private FilteredList<Categoria> categoriasFiltradas;

    private Categoria categoriaSeleccionada;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarBotonEliminar();
        configurarFiltros();
        configurarSeleccionTabla();
        cargarCategorias();

        chkActiva.setSelected(true);
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getId()
                ).asObject()
        );

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNombre()
                )
        );

        colActiva.setCellValueFactory(data ->
                new javafx.beans.property.SimpleBooleanProperty(
                        data.getValue().isActiva()
                )
        );
    }

    private void cargarCategorias() {
        categorias.setAll(categoriaDAO.listar());
    }

    private void configurarFiltros() {
        cmbFiltroEstado.setItems(
                FXCollections.observableArrayList(
                        "Todas",
                        "Activas",
                        "Inactivas"
                )
        );

        cmbFiltroEstado.setValue("Todas");

        categoriasFiltradas =
                new FilteredList<>(
                        categorias,
                        categoria -> true
                );

        tblCategorias.setItems(categoriasFiltradas);

        txtBuscar.textProperty().addListener(
                (observable, anterior, nuevo) ->
                        aplicarFiltros()
        );

        cmbFiltroEstado.valueProperty().addListener(
                (observable, anterior, nuevo) ->
                        aplicarFiltros()
        );
    }

    private void aplicarFiltros() {
        categoriasFiltradas.setPredicate(categoria -> {

            String texto = txtBuscar.getText();

            if (texto == null) {
                texto = "";
            }

            texto = texto.toLowerCase().trim();

            boolean coincideBusqueda =
                    texto.isEmpty()
                            || categoria.getNombre()
                            .toLowerCase()
                            .contains(texto);

            String estado =
                    cmbFiltroEstado.getValue();

            boolean coincideEstado = true;

            if ("Activas".equals(estado)) {
                coincideEstado =
                        categoria.isActiva();

            } else if ("Inactivas".equals(estado)) {
                coincideEstado =
                        !categoria.isActiva();
            }

            return coincideBusqueda
                    && coincideEstado;
        });
    }

    private void configurarSeleccionTabla() {
        tblCategorias.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, anterior, seleccionada) -> {

                            if (seleccionada != null) {
                                categoriaSeleccionada =
                                        seleccionada;

                                cargarCategoriaFormulario(
                                        seleccionada
                                );
                            }
                        }
                );
    }

    private void cargarCategoriaFormulario(
            Categoria categoria
    ) {
        txtNombre.setText(
                categoria.getNombre()
        );

        chkActiva.setSelected(
                categoria.isActiva()
        );
    }

    @FXML
    private void guardarCategoria() {
        String nombre =
                txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta(
                    "Ingrese el nombre de la categoría."
            );
            return;
        }

        if (categoriaDAO.existeNombre(nombre)) {
            mostrarAlerta(
                    "Ya existe una categoría con ese nombre."
            );
            return;
        }

        Categoria categoria =
                new Categoria();

        categoria.setNombre(nombre);

        categoria.setActiva(
                chkActiva.isSelected()
        );

        if (categoriaDAO.guardar(categoria)) {

            mostrarInformacion(
                    "Categoría guardada correctamente."
            );

            cargarCategorias();
            limpiar();

        } else {
            mostrarAlerta(
                    "No se pudo guardar la categoría."
            );
        }
    }

    @FXML
    private void actualizarCategoria() {

        if (categoriaSeleccionada == null) {
            mostrarAlerta(
                    "Seleccione una categoría para actualizar."
            );
            return;
        }

        String nombre =
                txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta(
                    "Ingrese el nombre de la categoría."
            );
            return;
        }

        if (categoriaDAO.existeNombre(
                nombre,
                categoriaSeleccionada.getId()
        )) {
            mostrarAlerta(
                    "Ya existe otra categoría con ese nombre."
            );
            return;
        }

        categoriaSeleccionada.setNombre(
                nombre
        );

        categoriaSeleccionada.setActiva(
                chkActiva.isSelected()
        );

        if (categoriaDAO.actualizar(
                categoriaSeleccionada
        )) {

            mostrarInformacion(
                    "Categoría actualizada correctamente."
            );

            cargarCategorias();
            limpiar();

        } else {
            mostrarAlerta(
                    "No se pudo actualizar la categoría."
            );
        }
    }

    private void configurarBotonEliminar() {

        colAcciones.setCellFactory(
                columna -> new TableCell<>() {

                    private final Button btnEliminar =
                            new Button("Eliminar");

                    {
                        btnEliminar.setOnAction(event -> {

                            Categoria categoria =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            eliminarCategoria(
                                    categoria
                            );
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {
                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnEliminar);
                        }
                    }
                }
        );
    }

    private void eliminarCategoria(
            Categoria categoria
    ) {

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle(
                "Eliminar categoría"
        );

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Desea eliminar la categoría "
                        + categoria.getNombre()
                        + "?"
        );

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get()
                == ButtonType.OK) {

            if (categoriaDAO.eliminar(
                    categoria.getId()
            )) {

                mostrarInformacion(
                        "Categoría eliminada correctamente."
                );

                cargarCategorias();
                limpiar();

            } else {
                mostrarAlerta(
                        "No se pudo eliminar la categoría. "
                                + "Puede estar siendo utilizada por un producto."
                );
            }
        }
    }

    @FXML
    private void limpiar() {
        categoriaSeleccionada = null;

        txtNombre.clear();

        chkActiva.setSelected(true);

        tblCategorias
                .getSelectionModel()
                .clearSelection();
    }

    private void mostrarAlerta(
            String mensaje
    ) {
        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle("Categorías");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(
            String mensaje
    ) {
        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle("Categorías");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}