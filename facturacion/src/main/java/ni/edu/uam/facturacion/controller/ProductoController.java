package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.dao.ProductoDao;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;

import java.math.BigDecimal;
import java.util.Optional;

public class ProductoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtExistencia;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<String> cmbFiltroEstado;

    @FXML
    private ComboBox<Categoria> cmbFiltroCategoria;

    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colExistencia;

    @FXML
    private TableColumn<Producto, Boolean> colActivo;

    private final ProductoDao productoDao = new ProductoDao();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private FilteredList<Producto> productosFiltrados;

    private Producto productoSeleccionado;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarCategorias();
        configurarFiltros();
        cargarProductos();
        configurarSeleccionTabla();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCodigo()
                )
        );

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNombre()
                )
        );

        colCategoria.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategoria().getNombre()
                )
        );

        colPrecio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getPrecioVenta()
                )
        );

        colExistencia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getExistencia()
                ).asObject()
        );

        colActivo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleBooleanProperty(
                        data.getValue().isActivo()
                )
        );
    }

    private void cargarCategorias() {
        ObservableList<Categoria> categorias =
                FXCollections.observableArrayList(
                        categoriaDAO.listarActivas()
                );

        cmbCategoria.setItems(categorias);
        cmbFiltroCategoria.setItems(
                FXCollections.observableArrayList(categorias)
        );
    }

    private void cargarProductos() {
        productos.setAll(productoDao.listar());
    }

    private void configurarFiltros() {
        cmbFiltroEstado.setItems(
                FXCollections.observableArrayList(
                        "Todos",
                        "Activos",
                        "Inactivos"
                )
        );

        cmbFiltroEstado.setValue("Todos");

        productosFiltrados =
                new FilteredList<>(productos, producto -> true);

        tblProductos.setItems(productosFiltrados);

        txtBuscar.textProperty().addListener(
                (observable, anterior, nuevo) -> aplicarFiltros()
        );

        cmbFiltroEstado.valueProperty().addListener(
                (observable, anterior, nuevo) -> aplicarFiltros()
        );

        cmbFiltroCategoria.valueProperty().addListener(
                (observable, anterior, nuevo) -> aplicarFiltros()
        );
    }

    private void aplicarFiltros() {
        productosFiltrados.setPredicate(producto -> {

            String texto = txtBuscar.getText();

            if (texto == null) {
                texto = "";
            }

            texto = texto.toLowerCase().trim();

            boolean coincideBusqueda =
                    texto.isEmpty()
                            || producto.getCodigo()
                            .toLowerCase()
                            .contains(texto)
                            || producto.getNombre()
                            .toLowerCase()
                            .contains(texto)
                            || producto.getCategoria()
                            .getNombre()
                            .toLowerCase()
                            .contains(texto);

            String estado = cmbFiltroEstado.getValue();

            boolean coincideEstado = true;

            if ("Activos".equals(estado)) {
                coincideEstado = producto.isActivo();
            } else if ("Inactivos".equals(estado)) {
                coincideEstado = !producto.isActivo();
            }

            Categoria categoriaFiltro =
                    cmbFiltroCategoria.getValue();

            boolean coincideCategoria =
                    categoriaFiltro == null
                            || producto.getCategoria()
                            .getId()
                            .equals(categoriaFiltro.getId());

            return coincideBusqueda
                    && coincideEstado
                    && coincideCategoria;
        });
    }

    private void configurarSeleccionTabla() {
        tblProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, anterior, seleccionado) -> {

                            if (seleccionado != null) {
                                productoSeleccionado = seleccionado;
                                cargarProductoFormulario(seleccionado);
                            }
                        }
                );
    }

    private void cargarProductoFormulario(Producto producto) {
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        cmbCategoria.setValue(producto.getCategoria());

        txtPrecio.setText(
                producto.getPrecioVenta().toString()
        );

        txtExistencia.setText(
                String.valueOf(producto.getExistencia())
        );

        chkActivo.setSelected(producto.isActivo());
    }

    @FXML
    private void guardarProducto() {
        Producto producto = obtenerProductoFormulario();

        if (producto == null) {
            return;
        }

        if (productoDao.existeCodigo(producto.getCodigo())) {
            mostrarAlerta(
                    "Ya existe un producto con ese código."
            );
            return;
        }

        if (productoDao.guardar(producto)) {
            mostrarInformacion(
                    "Producto guardado correctamente."
            );

            cargarProductos();
            limpiar();
        } else {
            mostrarAlerta(
                    "No se pudo guardar el producto."
            );
        }
    }

    @FXML
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            mostrarAlerta(
                    "Seleccione un producto para actualizar."
            );
            return;
        }

        Producto producto = obtenerProductoFormulario();

        if (producto == null) {
            return;
        }

        producto.setId(productoSeleccionado.getId());

        if (productoDao.existeCodigo(
                producto.getCodigo(),
                producto.getId()
        )) {
            mostrarAlerta(
                    "Ya existe otro producto con ese código."
            );
            return;
        }

        if (productoDao.actualizar(producto)) {
            mostrarInformacion(
                    "Producto actualizado correctamente."
            );

            cargarProductos();
            limpiar();
        } else {
            mostrarAlerta(
                    "No se pudo actualizar el producto."
            );
        }
    }

    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarAlerta(
                    "Seleccione un producto para eliminar."
            );
            return;
        }

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Eliminar producto");
        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Desea eliminar el producto "
                        + productoSeleccionado.getNombre()
                        + "?"
        );

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            if (productoDao.eliminar(
                    productoSeleccionado.getId()
            )) {
                mostrarInformacion(
                        "Producto eliminado correctamente."
                );

                cargarProductos();
                limpiar();

            } else {
                mostrarAlerta(
                        "No se pudo eliminar el producto."
                );
            }
        }
    }

    private Producto obtenerProductoFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty()) {
            mostrarAlerta("El código es obligatorio.");
            return null;
        }

        if (nombre.isEmpty()) {
            mostrarAlerta("El nombre es obligatorio.");
            return null;
        }

        Categoria categoria = cmbCategoria.getValue();

        if (categoria == null) {
            mostrarAlerta(
                    "Debe seleccionar una categoría."
            );
            return null;
        }

        BigDecimal precio;

        try {
            precio = new BigDecimal(
                    txtPrecio.getText().trim()
            );
        } catch (NumberFormatException e) {
            mostrarAlerta(
                    "El precio debe ser numérico."
            );
            return null;
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarAlerta(
                    "El precio debe ser mayor que cero."
            );
            return null;
        }

        int existencia;

        try {
            existencia = Integer.parseInt(
                    txtExistencia.getText().trim()
            );
        } catch (NumberFormatException e) {
            mostrarAlerta(
                    "La existencia debe ser un número entero."
            );
            return null;
        }

        if (existencia < 0) {
            mostrarAlerta(
                    "La existencia no puede ser negativa."
            );
            return null;
        }

        Producto producto = new Producto();

        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecioVenta(precio);
        producto.setExistencia(existencia);
        producto.setActivo(chkActivo.isSelected());

        return producto;
    }

    @FXML
    private void limpiar() {
        productoSeleccionado = null;

        txtCodigo.clear();
        txtNombre.clear();

        cmbCategoria
                .getSelectionModel()
                .clearSelection();

        txtPrecio.clear();
        txtExistencia.clear();

        chkActivo.setSelected(true);

        tblProductos
                .getSelectionModel()
                .clearSelection();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert =
                new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Productos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {
        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Productos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}