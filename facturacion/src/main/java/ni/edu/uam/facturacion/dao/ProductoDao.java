package ni.edu.uam.facturacion.dao;

import ni.edu.uam.facturacion.database.ConexionDB;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {

    public boolean guardar(Producto producto) {
        String sql = """
                INSERT INTO producto
                (
                    codigo,
                    nombre,
                    categoria_id,
                    precio_venta,
                    existencia,
                    ruta_imagen,
                    activo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCategoria().getId());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setString(6, producto.getRutaImagen());
            ps.setBoolean(7, producto.isActivo());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT p.id, p.codigo, p.nombre, p.precio_venta, p.existencia,
                       p.ruta_imagen, p.activo, c.id AS categoria_id,
                       c.nombre AS categoria_nombre, c.activa AS categoria_activa
                FROM producto p
                INNER JOIN categoria c ON p.categoria_id = c.id
                ORDER BY p.id
                """;

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("categoria_id"));
                categoria.setNombre(rs.getString("categoria_nombre"));
                categoria.setActiva(rs.getBoolean("categoria_activa"));

                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCategoria(categoria);
                producto.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                producto.setExistencia(rs.getInt("existencia"));
                producto.setRutaImagen(rs.getString("ruta_imagen"));
                producto.setActivo(rs.getBoolean("activo"));

                productos.add(producto);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }

        return productos;
    }

    public boolean actualizar(Producto producto) {
        String sql = """
                UPDATE producto
                SET codigo = ?, nombre = ?, categoria_id = ?, precio_venta = ?,
                    existencia = ?, ruta_imagen = ?, activo = ?
                WHERE id = ?
                """;

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCategoria().getId());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setString(6, producto.getRutaImagen());
            ps.setBoolean(7, producto.isActivo());
            ps.setInt(8, producto.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM producto WHERE id = ?";

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}