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

public class ProductoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private TextField txtRutaImagen;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ProductoDao productoDao = new ProductoDao();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private FilteredList<Producto> productosFiltrados;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());

        colCodigo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCodigo()));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        colCategoria.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria().getNombre()));

        colPrecio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrecioVenta()));

        colExistencia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getExistencia()).asObject());

        colActivo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleBooleanProperty(data.getValue().isActivo()));

        chkActivo.setSelected(true);
        cargarCategorias();
        cargarProductos();
        configurarFiltro();
    }

    private void cargarCategorias() {
        cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDAO.listarActivas()));
    }

    private void cargarProductos() {
        listaProductos.setAll(productoDao.listar());
    }

    private void configurarFiltro() {
        productosFiltrados = new FilteredList<>(listaProductos, producto -> true);
        tblProductos.setItems(productosFiltrados);

        txtBuscar.textProperty().addListener((observable, anterior, nuevo) -> {
            productosFiltrados.setPredicate(producto -> {
                if (nuevo == null || nuevo.trim().isEmpty()) {
                    return true;
                }

                String filtro = nuevo.toLowerCase().trim();

                return producto.getCodigo().toLowerCase().contains(filtro)
                        || producto.getNombre().toLowerCase().contains(filtro)
                        || producto.getCategoria().getNombre().toLowerCase().contains(filtro);
            });
        });
    }

    @FXML
    private void guardarProducto() {
        if (!validarCampos()) {
            return;
        }

        try {
            Producto producto = new Producto();
            producto.setCodigo(txtCodigo.getText().trim());
            producto.setNombre(txtNombre.getText().trim());
            producto.setCategoria(cmbCategoria.getValue());
            producto.setPrecioVenta(new BigDecimal(txtPrecio.getText().trim()));
            producto.setExistencia(Integer.parseInt(txtExistencia.getText().trim()));
            producto.setRutaImagen(txtRutaImagen.getText().trim());
            producto.setActivo(chkActivo.isSelected());

            if (productoDao.guardar(producto)) {
                limpiar();
                cargarProductos();
            } else {
                mostrarAlerta("No se pudo guardar el producto.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("El precio y la existencia deben ser valores válidos.");
        }
    }

    @FXML
    private void eliminarProducto() {
        Producto producto = tblProductos.getSelectionModel().getSelectedItem();

        if (producto == null) {
            mostrarAlerta("Seleccione un producto.");
            return;
        }

        if (productoDao.eliminar(producto.getId())) {
            limpiar();
            cargarProductos();
        } else {
            mostrarAlerta("No se pudo eliminar el producto.");
        }
    }

    @FXML
    private void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtExistencia.clear();
        txtRutaImagen.clear();
        chkActivo.setSelected(true);
        tblProductos.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarAlerta("Ingrese el código del producto.");
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Ingrese el nombre del producto.");
            return false;
        }

        if (cmbCategoria.getValue() == null) {
            mostrarAlerta("Seleccione una categoría.");
            return false;
        }

        if (txtPrecio.getText().trim().isEmpty()) {
            mostrarAlerta("Ingrese el precio.");
            return false;
        }

        if (txtExistencia.getText().trim().isEmpty()) {
            mostrarAlerta("Ingrese la existencia.");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Productos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}