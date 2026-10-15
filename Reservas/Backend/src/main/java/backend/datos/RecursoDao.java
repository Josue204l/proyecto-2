package backend.datos;

import logic.Categoria;
import logic.Recurso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDao {
    private final Database conexion;
    private final CategoriaDao categoriaDao;

    public RecursoDao(Database conexion, CategoriaDao categoriaDao) {
        this.conexion = conexion;
        this.categoriaDao = categoriaDao;
    }

    public List<Recurso> listar() throws SQLException {
        List<Recurso> lista = new ArrayList<>();
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id, descripcion, fk_categoria FROM recursos");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Recurso buscarPorId(String id) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id, descripcion, fk_categoria FROM recursos WHERE id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public void insertar(Recurso r) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO recursos (id, descripcion, fk_categoria) VALUES (?,?,?)")) {
            ps.setString(1, r.getId());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getCategoria() != null ? r.getCategoria().getId() : null);
            ps.executeUpdate();
        }
    }

    public void actualizar(Recurso r) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE recursos SET descripcion=?, fk_categoria=? WHERE id=?")) {
            ps.setString(1, r.getDescripcion());
            ps.setString(2, r.getCategoria() != null ? r.getCategoria().getId() : null);
            ps.setString(3, r.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(String id) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("DELETE FROM recursos WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    private Recurso mapear(ResultSet rs) throws SQLException {
        Categoria cat = categoriaDao.buscarPorId(rs.getString("fk_categoria"));
        return new Recurso(rs.getString("id"), rs.getString("descripcion"), cat);
    }
}
