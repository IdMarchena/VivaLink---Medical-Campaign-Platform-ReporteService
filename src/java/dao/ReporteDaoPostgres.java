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
        String sql = """
                    SELECT  r.id AS reporteId, r.titulo AS rTitulo,r.descripcion AS rDescripcion, r.fecha_creacion AS rFecha, r.campaña_id AS rCampañaId,
                            r.usuario_id AS rUsuarioId, r.tipo_reporte AS rTipoReporte ,r.estado AS rEstado,r.comentario AS rComentario,
                            c.id AS cId,c.nombre AS cNombre,
                            u.id AS uId, u.nombre AS uNombre
                    FROM reportes r 
                    LEFT JOIN campañas c ON r.campaña_id=c.id
                    LEFT JOIN usuarios u ON r.usuario_id= u.id
                    WHERE reporteId=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("rCampañaId"), rs.getString("cNombre")), // Relación con campaña
                    new Usuario(rs.getInt("rUsuarioId"), rs.getString("uNombre")), // Relación con usuario
                    rs.getString("rTipoReporte"), // Se obtiene el tipo de reporte
                    rs.getString("rEstado"),
                    rs.getString("rComentario") // Relacionar los comentarios
                );
            }
        } catch (SQLException e) {
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
        }
    }

    // Listar todos los reportes
    @Override
    public List<Reporte> listarTodosLosReportes() {
        String sql = """
                    SELECT  r.id AS reporteId, r.titulo AS rTitulo,r.descripcion AS rDescripcion, r.fecha_creacion AS rFecha, r.campaña_id AS rCampañaId,
                            r.usuario_id AS rUsuarioId, r.tipo_reporte AS rTipoReporte ,r.estado AS rEstado,r.comentario AS rComentario,
                            c.id AS cId,c.nombre AS cNombre,
                            u.id AS uId, u.nombre AS uNombre
                    FROM reportes r 
                    LEFT JOIN campañas c ON r.campaña_id=c.id
                    LEFT JOIN usuarios u ON r.usuario_id= u.id
                    WHERE r.id=?
            """;
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("rCampañaId"), rs.getString("cNombre")), // Relación con campaña
                    new Usuario(rs.getInt("rUsuarioId"), rs.getString("uNombre")), // Relación con usuario
                    rs.getString("rTipoReporte"), // Se obtiene el tipo de reporte
                    rs.getString("rEstado"),
                    rs.getString("rComentario") // Relacionar los comentarios
                ));
            }
        } catch (SQLException e) {
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
        }
    }

    // Eliminar un reporte
    @Override
    public void eliminarReporte(int id) {
        String sql = "DELETE FROM reportes r WHERE r.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // Buscar reportes por fecha
    @Override
    public List<Reporte> buscarReportesPorFecha(LocalDate fecha) {
                String sql = """
                    SELECT  r.id AS reporteId, r.titulo AS rTitulo,r.descripcion AS rDescripcion, r.fecha_creacion AS rFecha, r.campaña_id AS rCampañaId,
                            r.usuario_id AS rUsuarioId, r.tipo_reporte AS rTipoReporte ,r.estado AS rEstado,r.comentario AS rComentario,
                            c.id AS cId,c.nombre AS cNombre,
                            u.id AS uId, u.nombre AS uNombre
                    FROM reportes r 
                    LEFT JOIN campañas c ON r.campaña_id=c.id
                    LEFT JOIN usuarios u ON r.usuario_id= u.id
                    WHERE rFecha=?
            """;
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("rCampañaId"), rs.getString("cNombre")), // Relación con campaña
                    new Usuario(rs.getInt("rUsuarioId"), rs.getString("uNombre")), // Relación con usuario
                    rs.getString("rTipoReporte"), // Se obtiene el tipo de reporte
                    rs.getString("rEstado"),
                    rs.getString("rComentario") // Relacionar los comentarios
                ));
            }
        } catch (SQLException e) {
        }
        return reportes;
    }

    // Buscar reportes por estado
    @Override
    public List<Reporte> buscarReportesPorEstado(String estado) {
        String sql = """
                    SELECT  r.id AS reporteId, r.titulo AS rTitulo,r.descripcion AS rDescripcion, r.fecha_creacion AS rFecha, r.campaña_id AS rCampañaId,
                            r.usuario_id AS rUsuarioId, r.tipo_reporte AS rTipoReporte ,r.estado AS rEstado,r.comentario AS rComentario,
                            c.id AS cId,c.nombre AS cNombre,
                            u.id AS uId, u.nombre AS uNombre
                    FROM reportes r 
                    LEFT JOIN campañas c ON r.campaña_id=c.id
                    LEFT JOIN usuarios u ON r.usuario_id= u.id
                    WHERE rEstado=?
        """;
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("rCampañaId"), rs.getString("cNombre")), // Relación con campaña
                    new Usuario(rs.getInt("rUsuarioId"), rs.getString("uNombre")), // Relación con usuario
                    rs.getString("rTipoReporte"), // Se obtiene el tipo de reporte
                    rs.getString("rEstado"),
                    rs.getString("rComen    tario") // Relacionar los comentarios
                ));
            }
        } catch (SQLException e) {
        }
        return reportes;
    }

    // Buscar reportes por campaña
    @Override
    public List<Reporte> buscarReportesPorCampaña(Campaña campaña) {
                String sql = """
                    SELECT  r.id AS reporteId, r.titulo AS rTitulo,r.descripcion AS rDescripcion, r.fecha_creacion AS rFecha, r.campaña_id AS rCampañaId,
                            r.usuario_id AS rUsuarioId, r.tipo_reporte AS rTipoReporte ,r.estado AS rEstado,r.comentario AS rComentario,
                            c.id AS cId,c.nombre AS cNombre,
                            u.id AS uId, u.nombre AS uNombre
                    FROM reportes r 
                    LEFT JOIN campañas c ON r.campaña_id=c.id
                    LEFT JOIN usuarios u ON r.usuario_id= u.id
                    WHERE rCampañaId=?
        """;
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campaña.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("rCampañaId"), rs.getString("cNombre")), // Relación con campaña
                    new Usuario(rs.getInt("rUsuarioId"), rs.getString("uNombre")), // Relación con usuario
                    rs.getString("rTipoReporte"), // Se obtiene el tipo de reporte
                    rs.getString("rEstado"),
                    rs.getString("rComentario") // Relacionar los comentarios
                ));
            }
        } catch (SQLException e) {
        }
        return reportes;
    }

    // Buscar reportes por usuario
    @Override
    public List<Reporte> buscarReportesPorUsuario(Usuario usuario) {
        String sql = """
                SELECT  r.id AS reporteId, r.titulo AS rTitulo,r.descripcion AS rDescripcion, r.fecha_creacion AS rFecha, r.campaña_id AS rCampañaId,
                        r.usuario_id AS rUsuarioId, r.tipo_reporte AS rTipoReporte ,r.estado AS rEstado,r.comentario AS rComentario,
                        c.id AS cId,c.nombre AS cNombre,
                        u.id AS uId, u.nombre AS uNombre
                FROM reportes r 
                LEFT JOIN campañas c ON r.campaña_id=c.id
                LEFT JOIN usuarios u ON r.usuario_id= u.id
                WHERE uId=?
        """;
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("rCampañaId"), rs.getString("cNombre")), // Relación con campaña
                    new Usuario(rs.getInt("rUsuarioId"), rs.getString("uNombre")), // Relación con usuario
                    rs.getString("rTipoReporte"), // Se obtiene el tipo de reporte
                    rs.getString("rEstado"),
                    rs.getString("rComentario") // Relacionar los comentarios
                ));
            }
        } catch (SQLException e) {
        }
        return reportes;
    }

    // Buscar reportes por tipo
    @Override
    public List<Reporte> buscarReportesPorTipo(String tipoReporte) {
        String sql = """
                SELECT  r.id AS reporteId, r.titulo AS rTitulo,r.descripcion AS rDescripcion, r.fecha_creacion AS rFecha, r.campaña_id AS rCampañaId,
                        r.usuario_id AS rUsuarioId, r.tipo_reporte AS rTipoReporte ,r.estado AS rEstado,r.comentario AS rComentario,
                        c.id AS cId,c.nombre AS cNombre,
                        u.id AS uId, u.nombre AS uNombre
                FROM reportes r 
                LEFT JOIN campañas c ON r.campaña_id=c.id
                LEFT JOIN usuarios u ON r.usuario_id= u.id
                WHERE rTipoReporte=?
        """;
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipoReporte);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportes.add(new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    new Campaña(rs.getInt("rCampañaId"), rs.getString("cNombre")), // Relación con campaña
                    new Usuario(rs.getInt("rUsuarioId"), rs.getString("uNombre")), // Relación con usuario
                    rs.getString("rTipoReporte"), // Se obtiene el tipo de reporte
                    rs.getString("rEstado"),
                    rs.getString("rComentario") // Relacionar los comentarios
                ));
            }
        } catch (SQLException e) {
        }
        return reportes;
    }
}
