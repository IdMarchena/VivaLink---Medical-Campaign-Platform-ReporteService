package presentacion;

import servicio.service.ReporteService;
import servicio.serviceImpl.ReporteServiceImpl;
import dto.ReporteDto;
import dto.CampañaDto;
import dto.UsuarioDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "ReporteServlet", urlPatterns = {"/ReporteServlet"})
public class ReporteServlet extends HttpServlet {

    private final ReporteService reporteService;

    // Constructor
    public ReporteServlet() throws SQLException  {
        this.reporteService = new ReporteServiceImpl("TipoDb"); // Se inicializa con el tipo de base de datos
    }

    // Método para procesar las peticiones
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        System.out.println("Acción recibida: " + action);

        // Filtra las acciones según el parámetro "action"
        if ("crear".equals(action)) {
            crearReporte(request, response);
        } else if ("listar".equals(action)) {
            listarReportes(request, response);
        } else if ("buscar".equals(action)) {
            buscarReportePorId(request, response);
        } else if ("actualizar".equals(action)) {
            actualizarReporte(request, response);
        } else if ("eliminar".equals(action)) {
            eliminarReporte(request, response);
        } else if ("listarPorFecha".equals(action)) {
            listarReportesPorFecha(request, response);
        } else if ("listarPorEstado".equals(action)) {
            listarReportesPorEstado(request, response);
        } else if ("listarPorCampaña".equals(action)) {
            listarReportesPorCampaña(request, response);
        } else if ("listarPorUsuario".equals(action)) {
            listarReportesPorUsuario(request, response);
        } else if ("listarPorTipo".equals(action)) {
            listarReportesPorTipo(request, response);
        } else {
            // Otras acciones o por defecto
        }
    }

    // Acción para crear un reporte
    private void crearReporte(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");
        String campañaNombre = request.getParameter("campaña");
        String usuarioNombre = request.getParameter("usuario");
        String tipoReporte = request.getParameter("tipoReporte");
        String estado = request.getParameter("estado");
        String comentario = request.getParameter("comentario");

        try {
            // Convertir la fecha de String a LocalDate
            LocalDate fechaCreacionDate = LocalDate.parse(fechaCreacion);
            
            // Verificar si la campaña existe
            if (!reporteService.verificarCampañaExistente(campañaNombre)) {
                request.setAttribute("mensaje", "Campaña no encontrada.");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Verificar si el usuario tiene el rol adecuado
            if (!reporteService.verificarUsuarioPorRol(usuarioNombre, "analista")) {
                request.setAttribute("mensaje", "El usuario no tiene el rol adecuado.");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Crear los DTOs de campaña y usuario
            CampañaDto campañaDto = new CampañaDto(0, campañaNombre); // Puedes ajustarlo para tomar los datos correctos si se requiere más información
            UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre); // Lo mismo aquí, ajustando según sea necesario

            // Crear el ReporteDto con los valores obtenidos
            ReporteDto reporteDto = new ReporteDto(0, titulo, descripcion, fechaCreacionDate, campañaDto, usuarioDto, tipoReporte, estado, comentario);

            // Llamar al servicio para guardar el reporte
            reporteService.guardarReporte(reporteDto);

            request.setAttribute("mensaje", "Reporte creado con éxito.");
            listarReportes(request, response);
        } catch (Exception e) {
            request.setAttribute("mensaje", "Hubo un error al crear el reporte.");
            request.getRequestDispatcher("/jsp/crearReporte.jsp").forward(request, response);
        }
    }

    // Acción para listar todos los reportes
    private void listarReportes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ReporteDto> reportes = reporteService.listarTodosLosReportes();
        request.setAttribute("reportes", reportes);
        request.getRequestDispatcher("/jsp/listarReportes.jsp").forward(request, response);
    }

    // Acción para buscar un reporte por ID
    private void buscarReportePorId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idd = request.getParameter("id");
        int id = Integer.parseInt(idd);
        try {
            ReporteDto reporte = reporteService.buscarReportePorId(id);
            if (reporte != null) {
                request.setAttribute("reporte", reporte);
                request.getRequestDispatcher("/jsp/verReporte.jsp").forward(request, response);
            } else {
                response.sendRedirect("/jsp/reporteNoEncontrado.jsp");
            }
        } catch (Exception e) {
            response.sendRedirect("/jsp/reporteNoEncontrado.jsp");
        }
    }

    // Acción para actualizar un reporte
    private void actualizarReporte(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idd = request.getParameter("id");
        int id = Integer.parseInt(idd);
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");
        String campañaNombre = request.getParameter("campaña");
        String usuarioNombre = request.getParameter("usuario");
        String tipoReporte = request.getParameter("tipoReporte");
        String estado = request.getParameter("estado");
        String comentario = request.getParameter("comentario");

        try {
            // Convertir la fecha de String a LocalDate
            LocalDate fechaCreacionDate = LocalDate.parse(fechaCreacion);
            
            // Verificar si la campaña existe
            if (!reporteService.verificarCampañaExistente(campañaNombre)) {
                request.setAttribute("mensaje", "Campaña no encontrada.");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Verificar si el usuario tiene el rol adecuado
            if (!reporteService.verificarUsuarioPorRol(usuarioNombre, "analista")) {
                request.setAttribute("mensaje", "El usuario no tiene el rol adecuado.");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Crear los DTOs de campaña y usuario
            CampañaDto campañaDto = new CampañaDto(0, campañaNombre); // Ajustar con los datos correctos
            UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre); // Ajustar con los datos correctos

            // Crear el ReporteDto con los valores obtenidos
            ReporteDto reporteDto = new ReporteDto(id, titulo, descripcion, fechaCreacionDate, campañaDto, usuarioDto, tipoReporte, estado, comentario);

            // Llamar al servicio para actualizar el reporte
            reporteService.actualizarReporte(id, reporteDto);

            request.setAttribute("mensaje", "Reporte actualizado con éxito.");
            listarReportes(request, response);
        } catch (Exception e) {
            request.setAttribute("mensaje", "Hubo un error al actualizar el reporte.");
            request.getRequestDispatcher("/jsp/editarReporte.jsp").forward(request, response);
        }
    }

    // Acción para eliminar un reporte
    private void eliminarReporte(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idd = request.getParameter("id");
        int id = Integer.parseInt(idd);

        try {
            reporteService.eliminarReporte(id);
            request.setAttribute("mensaje", "Reporte eliminado con éxito.");
            listarReportes(request, response);
        } catch (Exception e) {
            request.setAttribute("mensaje", "Hubo un error al eliminar el reporte.");
            request.getRequestDispatcher("/jsp/listarReportes.jsp").forward(request, response);
        }
    }

    // Acción para listar reportes por fecha
    private void listarReportesPorFecha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fechaParam = request.getParameter("fecha");
        LocalDate fecha = LocalDate.parse(fechaParam);
        List<ReporteDto> reportes = reporteService.buscarReportesPorFecha(fecha);
        request.setAttribute("reportes", reportes);
        request.getRequestDispatcher("/jsp/listarReportesPorFecha.jsp").forward(request, response);
    }

    // Acción para listar reportes por estado
    private void listarReportesPorEstado(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String estado = request.getParameter("estado");
        List<ReporteDto> reportes = reporteService.buscarReportesPorEstado(estado);
        request.setAttribute("reportes", reportes);
        request.getRequestDispatcher("/jsp/listarReportesPorEstado.jsp").forward(request, response);
    }

    // Acción para listar reportes por campaña
    private void listarReportesPorCampaña(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String campañaNombre = request.getParameter("campaña");
        CampañaDto campañaDto = new CampañaDto(0, campañaNombre);
        List<ReporteDto> reportes = reporteService.buscarReportesPorCampaña(campañaDto);
        request.setAttribute("reportes", reportes);
        request.getRequestDispatcher("/jsp/listarReportesPorCampaña.jsp").forward(request, response);
    }

    // Acción para listar reportes por usuario
    private void listarReportesPorUsuario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usuarioNombre = request.getParameter("usuario");
        UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre);
        List<ReporteDto> reportes = reporteService.buscarReportesPorUsuario(usuarioDto);
        request.setAttribute("reportes", reportes);
        request.getRequestDispatcher("/jsp/listarReportesPorUsuario.jsp").forward(request, response);
    }

    // Acción para listar reportes por tipo
    private void listarReportesPorTipo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tipoReporte = request.getParameter("tipoReporte");
        List<ReporteDto> reportes = reporteService.buscarReportesPorTipo(tipoReporte);
        request.setAttribute("reportes", reportes);
        request.getRequestDispatcher("/jsp/listarReportesPorTipo.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
