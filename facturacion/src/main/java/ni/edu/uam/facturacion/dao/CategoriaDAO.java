package ni.edu.uam.facturacion.dao;

import ni.edu.uam.facturacion.database.ConexionDB;
import ni.edu.uam.facturacion.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public boolean guardar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, activa) VALUES (?, ?)";

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, categoria.getNombre());
            ps.setBoolean(2, categoria.isActiva());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al guardar categoría: " + e.getMessage());
            return false;
        }
    }

    public List<Categoria> listar() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre, activa FROM categoria ORDER BY id";

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("nombre"));
                categoria.setActiva(rs.getBoolean("activa"));
                categorias.add(categoria);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }

        return categorias;
    }

    public List<Categoria> listarActivas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre, activa FROM categoria WHERE activa = TRUE ORDER BY nombre";

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("nombre"));
                categoria.setActiva(rs.getBoolean("activa"));
                categorias.add(categoria);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }

        return categorias;
    }

    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nombre = ?, activa = ? WHERE id = ?";

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, categoria.getNombre());
            ps.setBoolean(2, categoria.isActiva());
            ps.setInt(3, categoria.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM categoria WHERE id = ?";

        try (Connection connection = ConexionDB.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}