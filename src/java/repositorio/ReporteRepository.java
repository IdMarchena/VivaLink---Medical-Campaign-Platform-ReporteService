package repositorio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import dao.ReporteDao;
import factory.ReporteDaoFactory;
import modelo.Reporte;
import modelo.Campaña;
import modelo.Usuario;

/**
 *
 * @author Usuario
 */
public class ReporteRepository {
    private final ReporteDao reporteDao;

    // Constructor que inicializa el Dao dependiendo del tipo de base de datos
    public ReporteRepository() throws SQLException {
        reporteDao = ReporteDaoFactory.dao("postgres");
    }

    // Buscar un reporte por ID
    public Reporte buscarReportePorId(int id) {
        return reporteDao.buscarReportePorId(id);
    }

    // Verificar si un reporte existe por ID
    public boolean verificarSiElReporteExiste(int id) {
        return reporteDao.verificarSiElReporteExiste(id);
    }

    // Guardar un reporte en la base de datos
    public void guardarReporte(Reporte reporte) {
        reporteDao.guardar(reporte);
    }

    // Listar todos los reportes
    public List<Reporte> listarTodosLosReportes() {
        return reporteDao.listarTodosLosReportes();
    }

    // Actualizar un reporte en la base de datos
    public void actualizarReporte(int id, Reporte reporte) {
        reporteDao.actualizarReporte(id, reporte);
    }

    // Eliminar un reporte de la base de datos
    public void eliminarReporte(int id) {
        reporteDao.eliminarReporte(id);
    }

    // Buscar reportes por fecha
    public List<Reporte> buscarReportesPorFecha(LocalDate fecha) {
        return reporteDao.buscarReportesPorFecha(fecha);
    }

    // Buscar reportes por estado
    public List<Reporte> buscarReportesPorEstado(String estado) {
        return reporteDao.buscarReportesPorEstado(estado);
    }

    // Buscar reportes por campaña
    public List<Reporte> buscarReportesPorCampaña(Campaña campaña) {
        return reporteDao.buscarReportesPorCampaña(campaña);
    }

    // Buscar reportes por usuario
    public List<Reporte> buscarReportesPorUsuario(Usuario usuario) {
        return reporteDao.buscarReportesPorUsuario(usuario);
    }

    // Buscar reportes por tipo
    public List<Reporte> buscarReportesPorTipo(String tipoReporte) {
        return reporteDao.buscarReportesPorTipo(tipoReporte);
    }
}
