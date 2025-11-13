package dao;

import dao.connection.DataBaseConnection;
import factory.DataBaseConnectionFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Campaña;
import modelo.Reporte;
import modelo.Usuario;

public class ReporteDaoPostgres implements ReporteDao {
    private final Connection conn;

    // Constructor que recibe el tipo de base de datos
    public ReporteDaoPostgres(String tipoDb) throws SQLException {
        DataBaseConnection dbConnection = DataBaseConnectionFactory.connection(tipoDb);
        this.conn = dbConnection.getConnection();
    }

    // Método para buscar un reporte por ID
    @Override
    public Reporte buscarReportePorId(int id) {
        String sql = "SELECT * FROM reportes WHERE id_reporte = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Reporte(
                    rs.getInt("id_reporte"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("id_campaña"), rs.getString("campaña_nombre")), // Relación con campaña
                    new Usuario(rs.getInt("id_usuario"), rs.getString("nombre")), // Relación con usuario
                    rs.getString("tipo_reporte"), // Se obtiene el tipo de reporte
                    rs.getString("estado"),
                    rs.getString("comentarios") // Relacionar los comentarios
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retorna null si no se encuentra el reporte
    }

    // Verificar si un reporte existe por ID
    @Override
    public boolean verificarSiElReporteExiste(int id) {
        String sql = "SELECT COUNT(*) FROM reportes WHERE id_reporte = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Guardar un reporte
    @Override
    public void guardar(Reporte reporte) {
        String sql = "INSERT INTO reportes (titulo, descripcion, fecha_creacion, estado, id_campaña, id_usuario, tipo_reporte, comentarios) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reporte.getTitulo());
            ps.setString(2, reporte.getDescripcion());
            ps.setDate(3, Date.valueOf(LocalDate.now())); // Fecha de creación, asumiendo que es hoy
            ps.setString(4, reporte.getEstado());
            ps.setInt(5, reporte.getCampaña().getId());
            ps.setInt(6, reporte.getUsuarioM().getId());
            ps.setString(7, reporte.getTipoReporte()); // Agregar tipo de reporte
            ps.setString(8, reporte.getComentario()); // Agregar comentarios
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Listar todos los reportes
    @Override
    public List<Reporte> listarTodosLosReportes() {
        String sql = "SELECT * FROM reportes";
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("id_reporte"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("id_campaña"), rs.getString("campaña_nombre")), // Relación con campaña
                    new Usuario(rs.getInt("id_usuario"), rs.getString("nombre")), // Relación con usuario
                    rs.getString("tipo_reporte"), // Obtener tipo de reporte
                    rs.getString("estado"),
                    rs.getString("comentarios") // Obtener comentarios
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }

    // Actualizar un reporte
    @Override
    public void actualizarReporte(int id, Reporte reporte) {
        String sql = "UPDATE reportes SET titulo = ?, descripcion = ?, fecha_creacion = ?, estado = ?, id_campaña = ?, id_usuario = ?, tipo_reporte = ?, comentarios = ? WHERE id_reporte = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reporte.getTitulo());
            ps.setString(2, reporte.getDescripcion());
            ps.setDate(3, Date.valueOf(reporte.getFechaCreacion()));
            ps.setString(4, reporte.getEstado());
            ps.setInt(5, reporte.getCampaña().getId());
            ps.setInt(6, reporte.getUsuarioM().getId());
            ps.setString(7, reporte.getTipoReporte()); // Actualizar tipo de reporte
            ps.setString(8, reporte.getComentario()); // Actualizar comentarios
            ps.setInt(9, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar un reporte
    @Override
    public void eliminarReporte(int id) {
        String sql = "DELETE FROM reportes WHERE id_reporte = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Buscar reportes por fecha
    @Override
    public List<Reporte> buscarReportesPorFecha(LocalDate fecha) {
        String sql = "SELECT * FROM reportes WHERE fecha_creacion = ?";
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("id_reporte"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("id_campaña"), rs.getString("campaña_nombre")),
                    new Usuario(rs.getInt("id_usuario"), rs.getString("nombre")),
                    rs.getString("tipo_reporte"),
                    rs.getString("estado"),
                    rs.getString("comentarios")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }

    // Buscar reportes por estado
    @Override
    public List<Reporte> buscarReportesPorEstado(String estado) {
        String sql = "SELECT * FROM reportes WHERE estado = ?";
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("id_reporte"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("id_campaña"), rs.getString("campaña_nombre")),
                    new Usuario(rs.getInt("id_usuario"), rs.getString("nombre")),
                    rs.getString("tipo_reporte"),
                    rs.getString("estado"),
                    rs.getString("comentarios")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }

    // Buscar reportes por campaña
    @Override
    public List<Reporte> buscarReportesPorCampaña(Campaña campaña) {
        String sql = "SELECT * FROM reportes WHERE id_campaña = ?";
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campaña.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("id_reporte"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("id_campaña"), rs.getString("campaña_nombre")),
                    new Usuario(rs.getInt("id_usuario"), rs.getString("nombre")),
                    rs.getString("tipo_reporte"),
                    rs.getString("estado"),
                    rs.getString("comentarios")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }

    // Buscar reportes por usuario
    @Override
    public List<Reporte> buscarReportesPorUsuario(Usuario usuario) {
        String sql = "SELECT * FROM reportes WHERE id_usuario = ?";
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("id_reporte"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("id_campaña"), rs.getString("campaña_nombre")),
                    new Usuario(rs.getInt("id_usuario"), rs.getString("nombre")),
                    rs.getString("tipo_reporte"),
                    rs.getString("estado"),
                    rs.getString("comentarios")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }

    // Buscar reportes por tipo
    @Override
    public List<Reporte> buscarReportesPorTipo(String tipoReporte) {
        String sql = "SELECT * FROM reportes WHERE tipo_reporte = ?";
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipoReporte);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("id_reporte"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("id_campaña"), rs.getString("campaña_nombre")),
                    new Usuario(rs.getInt("id_usuario"), rs.getString("nombre")),
                    rs.getString("tipo_reporte"),
                    rs.getString("estado"),
                    rs.getString("comentarios")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }
}
