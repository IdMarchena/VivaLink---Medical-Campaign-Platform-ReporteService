package mapper;

import modelo.Reporte;
import modelo.Campaña;
import modelo.Usuario;
import dto.ReporteDto;
import dto.CampañaDto;
import dto.UsuarioDto;

import java.util.List;
import java.util.stream.Collectors;

public class ReporteMapper {

    // Mapper para convertir de Reporte a ReporteDto
    public static ReporteDto reporteToDto(Reporte reporte) {
        CampañaDto campañaDto = campañaToDto(reporte.getCampaña());
        UsuarioDto usuarioDto = usuarioToDto(reporte.getUsuarioM());

        return new ReporteDto(
            reporte.getId(),
            reporte.getTitulo(),
            reporte.getDescripcion(),
            reporte.getFechaCreacion(),
            campañaDto,
            usuarioDto,
            reporte.getTipoReporte(),
            reporte.getEstado(),
            reporte.getComentario()
        );
    }

    // Mapper para convertir de ReporteDto a Reporte (Entidad)
    public static Reporte dtoToReporte(ReporteDto reporteDto) {
        Campaña campaña = dtoToCampaña(reporteDto.campaña());
        Usuario usuario = dtoToUsuario(reporteDto.usuarioM());

        return new Reporte(
            reporteDto.id(),
            reporteDto.titulo(),
            reporteDto.descripcion(),
            reporteDto.fechaCreacion(),
            campaña,
            usuario,
            reporteDto.tipoReporte(),
            reporteDto.estado(),
            reporteDto.comentario()
        );
    }

    // Mapper para convertir de Campaña a CampañaDto
    public static CampañaDto campañaToDto(Campaña campaña) {
        return new CampañaDto(
            campaña.getId(),
            campaña.getNombre()
        );
    }

    // Mapper para convertir de CampañaDto a Campaña (Entidad)
    public static Campaña dtoToCampaña(CampañaDto campañaDto) {
        return new Campaña(
            campañaDto.id(),
            campañaDto.nombre()
        );
    }

    // Mapper para convertir de Usuario a UsuarioDto
    public static UsuarioDto usuarioToDto(Usuario usuario) {
        return new UsuarioDto(
            usuario.getId(),
            usuario.getNombre()
        );
    }

    // Mapper para convertir de UsuarioDto a Usuario (Entidad)
    public static Usuario dtoToUsuario(UsuarioDto usuarioDto) {
        return new Usuario(
            usuarioDto.id(),
            usuarioDto.nombre()
        );
    }

    // Mapper para convertir de una lista de Reporte a una lista de ReporteDto
    public static List<ReporteDto> entityListToDtoList(List<Reporte> entityList) {
        return entityList.stream()
            .map(ReporteMapper::reporteToDto)
            .collect(Collectors.toList());
    }

    // Mapper para convertir de una lista de ReporteDto a una lista de Reporte
    public static List<Reporte> dtoListToEntityList(List<ReporteDto> dtoList) {
        return dtoList.stream()
            .map(ReporteMapper::dtoToReporte)
            .collect(Collectors.toList());
    }
}
