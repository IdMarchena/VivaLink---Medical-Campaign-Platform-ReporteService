package servicio.serviceImpl;

import dto.ReporteDto;
import dto.CampañaDto;
import dto.UsuarioDto;
import mapper.ReporteMapper;
import repositorio.ReporteRepository;
import servicio.service.ReporteService;
import external.CampañaServiceClient;
import external.UsuarioServiceClient;
import java.sql.SQLException;
import modelo.Reporte;
import modelo.Campaña;
import modelo.Usuario;

import java.time.LocalDate;
import java.util.List;

public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final CampañaServiceClient campañaServiceClient;
    private final UsuarioServiceClient usuarioServiceClient;

    // Constructor
    public ReporteServiceImpl(String tipoDb) throws SQLException {
        this.reporteRepository = new ReporteRepository(tipoDb);
        this.campañaServiceClient = new CampañaServiceClient();
        this.usuarioServiceClient = new UsuarioServiceClient();
    }

    @Override
    public ReporteDto buscarReportePorId(int id) {
        Reporte reporte = reporteRepository.buscarReportePorId(id);
        return ReporteMapper.reporteToDto(reporte);
    }

    @Override
    public boolean verificarSiElReporteExiste(int id) {
        return reporteRepository.verificarSiElReporteExiste(id);
    }

    @Override
    public void guardarReporte(ReporteDto reporteDto) {
        // Verificar que la campaña existe
        if (!verificarCampañaExistente(reporteDto.campaña().nombre())) {
            throw new IllegalArgumentException("La campaña no existe.");
        }

        // Verificar que el usuario tiene el rol adecuado
        if (!verificarUsuarioPorRol(reporteDto.usuarioM().nombre(), "ADMIN")) {
            throw new IllegalArgumentException("El usuario no tiene el rol adecuado.");
        }

        // Convertir ReporteDto a Reporte
        Reporte reporte = ReporteMapper.dtoToReporte(reporteDto);

        // Guardar el reporte
        reporteRepository.guardarReporte(reporte);
    }

    @Override
    public List<ReporteDto> listarTodosLosReportes() {
        List<Reporte> reportes = reporteRepository.listarTodosLosReportes();
        return ReporteMapper.entityListToDtoList(reportes);
    }

    @Override
    public void actualizarReporte(int id, ReporteDto reporteDto) {
        // Verificar que la campaña existe
        if (!verificarCampañaExistente(reporteDto.campaña().nombre())) {
            throw new IllegalArgumentException("La campaña no existe.");
        }

        // Verificar que el usuario tiene el rol adecuado
        if (!verificarUsuarioPorRol(reporteDto.usuarioM().nombre(), "ADMIN")) {
            throw new IllegalArgumentException("El usuario no tiene el rol adecuado.");
        }

        // Convertir ReporteDto a Reporte
        Reporte reporte = ReporteMapper.dtoToReporte(reporteDto);

        // Actualizar el reporte
        reporteRepository.actualizarReporte(id, reporte);
    }

    @Override
    public void eliminarReporte(int id) {
        reporteRepository.eliminarReporte(id);
    }

    @Override
    public List<ReporteDto> buscarReportesPorFecha(LocalDate fecha) {
        List<Reporte> reportes = reporteRepository.buscarReportesPorFecha(fecha);
        return ReporteMapper.entityListToDtoList(reportes);
    }

    @Override
    public List<ReporteDto> buscarReportesPorEstado(String estado) {
        List<Reporte> reportes = reporteRepository.buscarReportesPorEstado(estado);
        return ReporteMapper.entityListToDtoList(reportes);
    }

    @Override
    public List<ReporteDto> buscarReportesPorCampaña(CampañaDto campañaDto) {
        Campaña campaña = ReporteMapper.dtoToCampaña(campañaDto);
        List<Reporte> reportes = reporteRepository.buscarReportesPorCampaña(campaña);
        return ReporteMapper.entityListToDtoList(reportes);
    }

    @Override
    public List<ReporteDto> buscarReportesPorUsuario(UsuarioDto usuarioDto) {
        Usuario usuario = ReporteMapper.dtoToUsuario(usuarioDto);
        List<Reporte> reportes = reporteRepository.buscarReportesPorUsuario(usuario);
        return ReporteMapper.entityListToDtoList(reportes);
    }

    @Override
    public List<ReporteDto> buscarReportesPorTipo(String tipoReporte) {
        List<Reporte> reportes = reporteRepository.buscarReportesPorTipo(tipoReporte);
        return ReporteMapper.entityListToDtoList(reportes);
    }

    @Override
    public boolean verificarCampañaExistente(String nombreCampaña) {
        return campañaServiceClient.verificarCampañaExistente(nombreCampaña);
    }

    @Override
    public boolean verificarUsuarioPorRol(String nombreUsuario, String rol) {
        return usuarioServiceClient.verificarUsuarioPorRol(nombreUsuario, rol);
    }
}
