package backend.datos;

import logic.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {
    private final ConexionMySql conexion;

    public CategoriaDao(ConexionMySql conexion) { this.conexion = conexion; }

    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id, nombre, descripcion FROM categorias");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Categoria buscarPorId(String id) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id, nombre, descripcion FROM categorias WHERE id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public void insertar(Categoria cat) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO categorias (id, nombre, descripcion) VALUES (?,?,?)")) {
            ps.setString(1, cat.getId());
            ps.setString(2, cat.getNombre());
            ps.setString(3, cat.getDescripcion());
            ps.executeUpdate();
        }
    }

    public void actualizar(Categoria cat) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE categorias SET nombre=?, descripcion=? WHERE id=?")) {
            ps.setString(1, cat.getNombre());
            ps.setString(2, cat.getDescripcion());
            ps.setString(3, cat.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(String id) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("DELETE FROM categorias WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getString("id"), rs.getString("nombre"), rs.getString("descripcion"));
    }
}
