package backend.datos;

import logic.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDao {
    private final Database conexion;

    public FuncionarioDao(Database conexion) { this.conexion = conexion; }

    public List<Funcionario> listar() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id, clave, rol, nombre, telefono, puesto FROM funcionarios");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Funcionario buscarPorId(String id) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id, clave, rol, nombre, telefono, puesto FROM funcionarios WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public void insertar(Funcionario f) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO funcionarios (id, clave, rol, nombre, telefono, puesto) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, f.getId());
            ps.setString(2, f.getClave());
            ps.setString(3, f.getRol());
            ps.setString(4, f.getNombre());
            ps.setString(5, f.getTelefono());
            ps.setString(6, f.getPuesto());
            ps.executeUpdate();
        }
    }

    public void actualizar(Funcionario f) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE funcionarios SET clave=?, rol=?, nombre=?, telefono=?, puesto=? WHERE id=?")) {
            ps.setString(1, f.getClave());
            ps.setString(2, f.getRol());
            ps.setString(3, f.getNombre());
            ps.setString(4, f.getTelefono());
            ps.setString(5, f.getPuesto());
            ps.setString(6, f.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(String id) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("DELETE FROM funcionarios WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    private Funcionario mapear(ResultSet rs) throws SQLException {
        return new Funcionario(
                rs.getString("id"), rs.getString("clave"), rs.getString("rol"),
                rs.getString("nombre"), rs.getString("telefono"), rs.getString("puesto"));
    }
}
