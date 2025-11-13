
package servicio.service;
import java.util.List;
import java.time.LocalDate;
import dto.CampañaDto;
import dto.ReporteDto;
import dto.UsuarioDto;

public interface ReporteService {

    // Buscar un reporte por ID
    ReporteDto buscarReportePorId(int id);

    // Verificar si el reporte existe por ID
    boolean verificarSiElReporteExiste(int id);

    // Guardar un reporte tras verificar campaña y usuario
    void guardarReporte(ReporteDto reporte);

    // Listar todos los reportes
    List<ReporteDto> listarTodosLosReportes();

    // Actualizar un reporte tras verificar campaña y usuario
    void actualizarReporte(int id, ReporteDto reporte);

    // Eliminar un reporte
    void eliminarReporte(int id);

    // Buscar reportes por fecha
    List<ReporteDto> buscarReportesPorFecha(LocalDate fecha);

    // Buscar reportes por estado
    List<ReporteDto> buscarReportesPorEstado(String estado);

    // Buscar reportes por campaña
    List<ReporteDto> buscarReportesPorCampaña(CampañaDto campaña);

    // Buscar reportes por usuario
    List<ReporteDto> buscarReportesPorUsuario(UsuarioDto usuario);

    // Buscar reportes por tipo
    List<ReporteDto> buscarReportesPorTipo(String tipoReporte);

    // Verificar si la campaña existe
    boolean verificarCampañaExistente(String nombreCampaña);

    // Verificar si el usuario tiene el rol adecuado
    boolean verificarUsuarioPorRol(String nombreUsuario, String rol);
}
