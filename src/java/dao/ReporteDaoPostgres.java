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

    public ReporteDaoPostgres(DataBaseConnection connection) throws SQLException {
        DataBaseConnection db = DataBaseConnectionFactory.connection("postgres");
        this.conn = db.getConnection();
    }

    // Método para buscar un reporte por ID - CORREGIDO
    @Override
    public Reporte buscarReportePorId(int id) {
        String sql = """
            SELECT  
                r.id AS reporteId, 
                r.titulo AS rTitulo,
                r.descripcion AS rDescripcion, 
                r.fecha_creacion AS rFecha, 
                r.campaña_id AS rCampañaId,
                r.usuario_id AS rUsuarioId, 
                r.tipo_reporte AS rTipoReporte,
                r.estado AS rEstado,
                r.comentario AS rComentario,
                c.id AS cId,
                c.nombre AS cNombre,
                u.id AS uId, 
                u.nombre AS uNombre
            FROM reportes r 
            LEFT JOIN campañas c ON r.campaña_id = c.id
            LEFT JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.id = ?
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Campaña campaña = new Campaña(
                    rs.getInt("cId"), 
                    rs.getString("cNombre")
                );
                
                Usuario usuario = new Usuario(
                    rs.getInt("uId"), 
                    rs.getString("uNombre")
                );
                
                return new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getDate("rFecha") != null ? rs.getDate("rFecha").toLocalDate() : null,
                    campaña,
                    usuario,
                    rs.getString("rTipoReporte"),
                    rs.getString("rEstado"),
                    rs.getString("rComentario")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando reporte por ID: " + e.getMessage(), e);
        }
        return null;
    }

    // Verificar si un reporte existe por ID - CORREGIDO
    @Override
    public boolean verificarSiElReporteExiste(int id) {
        String sql = "SELECT COUNT(*) FROM reportes WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error verificando existencia del reporte: " + e.getMessage(), e);
        }
        return false;
    }

    // Guardar un reporte - CORREGIDO
    @Override
    public void guardar(Reporte reporte) {
        String sql = """
            INSERT INTO reportes (titulo, descripcion, fecha_creacion, estado, campaña_id, usuario_id, tipo_reporte, comentario) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reporte.getTitulo());
            ps.setString(2, reporte.getDescripcion());
            ps.setDate(3, Date.valueOf(reporte.getFechaCreacion()));
            ps.setString(4, reporte.getEstado());
            ps.setInt(5, reporte.getCampaña().getId());
            ps.setInt(6, reporte.getUsuarioM().getId());
            ps.setString(7, reporte.getTipoReporte());
            ps.setString(8, reporte.getComentario());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error guardando reporte: " + e.getMessage(), e);
        }
    }

    // Listar todos los reportes - CORREGIDO (eliminé el WHERE que estaba mal)
    @Override
    public List<Reporte> listarTodosLosReportes() {
        String sql = """
            SELECT  
                r.id AS reporteId, 
                r.titulo AS rTitulo,
                r.descripcion AS rDescripcion, 
                r.fecha_creacion AS rFecha, 
                r.campaña_id AS rCampañaId,
                r.usuario_id AS rUsuarioId, 
                r.tipo_reporte AS rTipoReporte,
                r.estado AS rEstado,
                r.comentario AS rComentario,
                c.id AS cId,
                c.nombre AS cNombre,
                u.id AS uId, 
                u.nombre AS uNombre
            FROM reportes r 
            LEFT JOIN campañas c ON r.campaña_id = c.id
            LEFT JOIN usuarios u ON r.usuario_id = u.id
            ORDER BY r.id
        """;
        
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Campaña campaña = new Campaña(
                    rs.getInt("cId"), 
                    rs.getString("cNombre")
                );
                
                Usuario usuario = new Usuario(
                    rs.getInt("uId"), 
                    rs.getString("uNombre")
                );
                
                Reporte reporte = new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getDate("rFecha") != null ? rs.getDate("rFecha").toLocalDate() : null,
                    campaña,
                    usuario,
                    rs.getString("rTipoReporte"),
                    rs.getString("rEstado"),
                    rs.getString("rComentario")
                );
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando reportes: " + e.getMessage(), e);
        }
        return reportes;
    }

    // Actualizar un reporte - CORREGIDO
    @Override
    public void actualizarReporte(int id, Reporte reporte) {
        String sql = """
            UPDATE reportes 
            SET titulo = ?, descripcion = ?, fecha_creacion = ?, estado = ?, 
                campaña_id = ?, usuario_id = ?, tipo_reporte = ?, comentario = ? 
            WHERE id = ?
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reporte.getTitulo());
            ps.setString(2, reporte.getDescripcion());
            ps.setDate(3, Date.valueOf(reporte.getFechaCreacion()));
            ps.setString(4, reporte.getEstado());
            ps.setInt(5, reporte.getCampaña().getId());
            ps.setInt(6, reporte.getUsuarioM().getId());
            ps.setString(7, reporte.getTipoReporte());
            ps.setString(8, reporte.getComentario());
            ps.setInt(9, id);
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error actualizando reporte: " + e.getMessage(), e);
        }
    }

    // Eliminar un reporte - CORREGIDO
    @Override
    public void eliminarReporte(int id) {
        String sql = "DELETE FROM reportes WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error eliminando reporte: " + e.getMessage(), e);
        }
    }

    // Buscar reportes por fecha - CORREGIDO
    @Override
    public List<Reporte> buscarReportesPorFecha(LocalDate fecha) {
        String sql = """
            SELECT  
                r.id AS reporteId, 
                r.titulo AS rTitulo,
                r.descripcion AS rDescripcion, 
                r.fecha_creacion AS rFecha, 
                r.campaña_id AS rCampañaId,
                r.usuario_id AS rUsuarioId, 
                r.tipo_reporte AS rTipoReporte,
                r.estado AS rEstado,
                r.comentario AS rComentario,
                c.id AS cId,
                c.nombre AS cNombre,
                u.id AS uId, 
                u.nombre AS uNombre
            FROM reportes r 
            LEFT JOIN campañas c ON r.campaña_id = c.id
            LEFT JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.fecha_creacion = ?
            ORDER BY r.id
        """;
        
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Campaña campaña = new Campaña(
                    rs.getInt("cId"), 
                    rs.getString("cNombre")
                );
                
                Usuario usuario = new Usuario(
                    rs.getInt("uId"), 
                    rs.getString("uNombre")
                );
                
                Reporte reporte = new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getDate("rFecha") != null ? rs.getDate("rFecha").toLocalDate() : null,
                    campaña,
                    usuario,
                    rs.getString("rTipoReporte"),
                    rs.getString("rEstado"),
                    rs.getString("rComentario")
                );
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando reportes por fecha: " + e.getMessage(), e);
        }
        return reportes;
    }

    // Buscar reportes por estado - CORREGIDO
    @Override
    public List<Reporte> buscarReportesPorEstado(String estado) {
        String sql = """
            SELECT  
                r.id AS reporteId, 
                r.titulo AS rTitulo,
                r.descripcion AS rDescripcion, 
                r.fecha_creacion AS rFecha, 
                r.campaña_id AS rCampañaId,
                r.usuario_id AS rUsuarioId, 
                r.tipo_reporte AS rTipoReporte,
                r.estado AS rEstado,
                r.comentario AS rComentario,
                c.id AS cId,
                c.nombre AS cNombre,
                u.id AS uId, 
                u.nombre AS uNombre
            FROM reportes r 
            LEFT JOIN campañas c ON r.campaña_id = c.id
            LEFT JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.estado = ?
            ORDER BY r.id
        """;
        
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Campaña campaña = new Campaña(
                    rs.getInt("cId"), 
                    rs.getString("cNombre")
                );
                
                Usuario usuario = new Usuario(
                    rs.getInt("uId"), 
                    rs.getString("uNombre")
                );
                
                Reporte reporte = new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getDate("rFecha") != null ? rs.getDate("rFecha").toLocalDate() : null,
                    campaña,
                    usuario,
                    rs.getString("rTipoReporte"),
                    rs.getString("rEstado"),
                    rs.getString("rComentario")
                );
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando reportes por estado: " + e.getMessage(), e);
        }
        return reportes;
    }

    // Buscar reportes por campaña - CORREGIDO
    @Override
    public List<Reporte> buscarReportesPorCampaña(Campaña campaña) {
        String sql = """
            SELECT  
                r.id AS reporteId, 
                r.titulo AS rTitulo,
                r.descripcion AS rDescripcion, 
                r.fecha_creacion AS rFecha, 
                r.campaña_id AS rCampañaId,
                r.usuario_id AS rUsuarioId, 
                r.tipo_reporte AS rTipoReporte,
                r.estado AS rEstado,
                r.comentario AS rComentario,
                c.id AS cId,
                c.nombre AS cNombre,
                u.id AS uId, 
                u.nombre AS uNombre
            FROM reportes r 
            LEFT JOIN campañas c ON r.campaña_id = c.id
            LEFT JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.campaña_id = ?
            ORDER BY r.id
        """;
        
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campaña.getId());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Campaña camp = new Campaña(
                    rs.getInt("cId"), 
                    rs.getString("cNombre")
                );
                
                Usuario usuario = new Usuario(
                    rs.getInt("uId"), 
                    rs.getString("uNombre")
                );
                
                Reporte reporte = new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getDate("rFecha") != null ? rs.getDate("rFecha").toLocalDate() : null,
                    camp,
                    usuario,
                    rs.getString("rTipoReporte"),
                    rs.getString("rEstado"),
                    rs.getString("rComentario")
                );
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando reportes por campaña: " + e.getMessage(), e);
        }
        return reportes;
    }

    // Buscar reportes por usuario - CORREGIDO
    @Override
    public List<Reporte> buscarReportesPorUsuario(Usuario usuario) {
        String sql = """
            SELECT  
                r.id AS reporteId, 
                r.titulo AS rTitulo,
                r.descripcion AS rDescripcion, 
                r.fecha_creacion AS rFecha, 
                r.campaña_id AS rCampañaId,
                r.usuario_id AS rUsuarioId, 
                r.tipo_reporte AS rTipoReporte,
                r.estado AS rEstado,
                r.comentario AS rComentario,
                c.id AS cId,
                c.nombre AS cNombre,
                u.id AS uId, 
                u.nombre AS uNombre
            FROM reportes r 
            LEFT JOIN campañas c ON r.campaña_id = c.id
            LEFT JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.usuario_id = ?
            ORDER BY r.id
        """;
        
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Campaña campaña = new Campaña(
                    rs.getInt("cId"), 
                    rs.getString("cNombre")
                );
                
                Usuario user = new Usuario(
                    rs.getInt("uId"), 
                    rs.getString("uNombre")
                );
                
                Reporte reporte = new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getDate("rFecha") != null ? rs.getDate("rFecha").toLocalDate() : null,
                    campaña,
                    user,
                    rs.getString("rTipoReporte"),
                    rs.getString("rEstado"),
                    rs.getString("rComentario")
                );
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando reportes por usuario: " + e.getMessage(), e);
        }
        return reportes;
    }

    // Buscar reportes por tipo - CORREGIDO
    @Override
    public List<Reporte> buscarReportesPorTipo(String tipoReporte) {
        String sql = """
            SELECT  
                r.id AS reporteId, 
                r.titulo AS rTitulo,
                r.descripcion AS rDescripcion, 
                r.fecha_creacion AS rFecha, 
                r.campaña_id AS rCampañaId,
                r.usuario_id AS rUsuarioId, 
                r.tipo_reporte AS rTipoReporte,
                r.estado AS rEstado,
                r.comentario AS rComentario,
                c.id AS cId,
                c.nombre AS cNombre,
                u.id AS uId, 
                u.nombre AS uNombre
            FROM reportes r 
            LEFT JOIN campañas c ON r.campaña_id = c.id
            LEFT JOIN usuarios u ON r.usuario_id = u.id
            WHERE r.tipo_reporte = ?
            ORDER BY r.id
        """;
        
        List<Reporte> reportes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipoReporte);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Campaña campaña = new Campaña(
                    rs.getInt("cId"), 
                    rs.getString("cNombre")
                );
                
                Usuario usuario = new Usuario(
                    rs.getInt("uId"), 
                    rs.getString("uNombre")
                );
                
                Reporte reporte = new Reporte(
                    rs.getInt("reporteId"),
                    rs.getString("rTitulo"),
                    rs.getString("rDescripcion"),
                    rs.getDate("rFecha") != null ? rs.getDate("rFecha").toLocalDate() : null,
                    campaña,
                    usuario,
                    rs.getString("rTipoReporte"),
                    rs.getString("rEstado"),
                    rs.getString("rComentario")
                );
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando reportes por tipo: " + e.getMessage(), e);
        }
        return reportes;
    }
}