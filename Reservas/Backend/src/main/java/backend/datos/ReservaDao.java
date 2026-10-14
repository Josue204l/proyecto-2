package backend.datos;

import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDao {
    private final ConexionMySql conexion;
    private final FuncionarioDao funcionarioDao;
    private final RecursoDao recursoDao;
    private final CategoriaDao categoriaDao;

    public ReservaDao(ConexionMySql conexion, FuncionarioDao funcionarioDao,
                      RecursoDao recursoDao, CategoriaDao categoriaDao) {
        this.conexion = conexion;
        this.funcionarioDao = funcionarioDao;
        this.recursoDao = recursoDao;
        this.categoriaDao = categoriaDao;
    }

    public List<Reserva> listar() throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, titulo, fecha, hora_inicio, hora_fin, fk_funcionario, estado FROM reservas");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(c, rs));
        }
        return lista;
    }

    public Reserva buscarPorId(String id) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, titulo, fecha, hora_inicio, hora_fin, fk_funcionario, estado FROM reservas WHERE id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(c, rs) : null;
            }
        }
    }

    public void insertar(Reserva r) throws SQLException {
        try (Connection c = conexion.abrir()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO reservas (id, titulo, fecha, hora_inicio, hora_fin, fk_funcionario, estado) VALUES (?,?,?,?,?,?,?)")) {
                    ps.setString(1, r.getId());
                    ps.setString(2, r.getTitulo());
                    ps.setDate(3, Date.valueOf(r.getFecha()));
                    ps.setTime(4, Time.valueOf(r.getHoraInicio()));
                    ps.setTime(5, Time.valueOf(r.getHoraFin()));
                    ps.setString(6, r.getSolicitante() != null ? r.getSolicitante().getId() : null);
                    ps.setString(7, r.getEstado());
                    ps.executeUpdate();
                }
                insertarRelaciones(c, r);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public void actualizar(Reserva r) throws SQLException {
        try (Connection c = conexion.abrir()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE reservas SET titulo=?, fecha=?, hora_inicio=?, hora_fin=?, fk_funcionario=?, estado=? WHERE id=?")) {
                    ps.setString(1, r.getTitulo());
                    ps.setDate(2, Date.valueOf(r.getFecha()));
                    ps.setTime(3, Time.valueOf(r.getHoraInicio()));
                    ps.setTime(4, Time.valueOf(r.getHoraFin()));
                    ps.setString(5, r.getSolicitante() != null ? r.getSolicitante().getId() : null);
                    ps.setString(6, r.getEstado());
                    ps.setString(7, r.getId());
                    ps.executeUpdate();
                }
                eliminarRelaciones(c, r.getId());
                insertarRelaciones(c, r);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public void eliminar(String id) throws SQLException {
        try (Connection c = conexion.abrir()) {
            c.setAutoCommit(false);
            try {
                eliminarRelaciones(c, id);
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM reservas WHERE id=?")) {
                    ps.setString(1, id);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    // ---------------------------------------------------------------
    // Helpers privados
    // ---------------------------------------------------------------

    private void insertarRelaciones(Connection c, Reserva r) throws SQLException {
        if (r.getRecursosAsignados() != null) {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO reserva_recursos (fk_reserva, fk_recurso) VALUES (?,?)")) {
                for (Recurso rec : r.getRecursosAsignados()) {
                    ps.setString(1, r.getId());
                    ps.setString(2, rec.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
        if (r.getCategoriasRequeridas() != null) {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO reserva_categorias (fk_reserva, fk_categoria) VALUES (?,?)")) {
                for (Categoria cat : r.getCategoriasRequeridas()) {
                    ps.setString(1, r.getId());
                    ps.setString(2, cat.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    private void eliminarRelaciones(Connection c, String reservaId) throws SQLException {
        for (String tabla : new String[]{"reserva_recursos", "reserva_categorias"}) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM " + tabla + " WHERE fk_reserva=?")) {
                ps.setString(1, reservaId);
                ps.executeUpdate();
            }
        }
    }

    private Reserva mapear(Connection c, ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        Funcionario solicitante = funcionarioDao.buscarPorId(rs.getString("fk_funcionario"));
        List<Recurso> recursos = listarRecursosDeReserva(c, id);
        List<Categoria> categorias = listarCategoriasDeReserva(c, id);

        Reserva r = new Reserva(id, rs.getString("titulo"),
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora_inicio").toLocalTime(),
                rs.getTime("hora_fin").toLocalTime(),
                recursos, categorias, solicitante);
        r.setEstado(rs.getString("estado"));
        return r;
    }

    private List<Recurso> listarRecursosDeReserva(Connection c, String reservaId) throws SQLException {
        List<Recurso> lista = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT fk_recurso FROM reserva_recursos WHERE fk_reserva=?")) {
            ps.setString(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recurso rec = recursoDao.buscarPorId(rs.getString("fk_recurso"));
                    if (rec != null) lista.add(rec);
                }
            }
        }
        return lista;
    }

    private List<Categoria> listarCategoriasDeReserva(Connection c, String reservaId) throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT fk_categoria FROM reserva_categorias WHERE fk_reserva=?")) {
            ps.setString(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria cat = categoriaDao.buscarPorId(rs.getString("fk_categoria"));
                    if (cat != null) lista.add(cat);
                }
            }
        }
        return lista;
    }
}
