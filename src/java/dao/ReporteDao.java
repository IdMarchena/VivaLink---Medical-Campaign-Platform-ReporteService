package dao;

import modelo.Reporte;
import modelo.Campaña;
import modelo.Usuario;
import java.time.LocalDate;
import java.util.List;

public interface ReporteDao {

    // Buscar un reporte por su ID
    Reporte buscarReportePorId(int id);

    // Verificar si un reporte existe por su ID
    boolean verificarSiElReporteExiste(int id);

    // Guardar un nuevo reporte
    void guardar(Reporte reporte);

    // Listar todos los reportes
    List<Reporte> listarTodosLosReportes();

    // Actualizar un reporte existente
    void actualizarReporte(int id, Reporte reporte);

    // Eliminar un reporte por su ID
    void eliminarReporte(int id);

    // Buscar reportes por fecha de creación
    List<Reporte> buscarReportesPorFecha(LocalDate fecha);

    // Buscar reportes por estado
    List<Reporte> buscarReportesPorEstado(String estado);

    // Buscar reportes por tipo de reporte
    List<Reporte> buscarReportesPorTipo(String tipoReporte);

    // Buscar reportes relacionados con una campaña específica
    List<Reporte> buscarReportesPorCampaña(Campaña campaña);

    // Buscar reportes creados por un usuario específico
    List<Reporte> buscarReportesPorUsuario(Usuario usuario);
}
