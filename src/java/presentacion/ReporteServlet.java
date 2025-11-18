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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet(name = "ReporteServlet", urlPatterns = {"/ReporteServlet"})
public class ReporteServlet extends HttpServlet {

    private final ReporteService reporteService;
    private final Gson gson;

    // Constructor
    public ReporteServlet() throws SQLException  {
        this.reporteService = new ReporteServiceImpl();
        // Configurar Gson con el adaptador para LocalDate
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setDateFormat("yyyy-MM-dd")
            .create();
    }

    // Método para procesar las peticiones
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        System.out.println("Acción recibida: " + action);

        // Configurar respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
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
                // Acción no reconocida
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson("Acción no reconocida: " + action));
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error interno del servidor: " + e.getMessage()));
        }
    }

    // Acción para crear un reporte
    private void crearReporte(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");
        String campañaNombre = request.getParameter("campaña");
        String usuarioNombre = request.getParameter("usuario");
        String tipoReporte = request.getParameter("tipoReporte");
        String estado = request.getParameter("estado");
        String comentario = request.getParameter("comentario");

        try {
            // Validar parámetros requeridos
            if (titulo == null || titulo.isEmpty() || descripcion == null || descripcion.isEmpty() ||
                fechaCreacion == null || fechaCreacion.isEmpty() || campañaNombre == null || campañaNombre.isEmpty() ||
                usuarioNombre == null || usuarioNombre.isEmpty() || tipoReporte == null || tipoReporte.isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson("Todos los campos son requeridos"));
                return;
            }

            // Convertir la fecha de String a LocalDate
            LocalDate fechaCreacionDate = LocalDate.parse(fechaCreacion);
            
            // Verificar si la campaña existe
            if (!reporteService.verificarCampañaExistente(campañaNombre)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson("Campaña no encontrada"));
                return;
            }

            // Verificar si el usuario tiene el rol adecuado
            if (!reporteService.verificarUsuarioPorRol(usuarioNombre, "analista")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(gson.toJson("El usuario no tiene el rol adecuado"));
                return;
            }

            // Crear los DTOs de campaña y usuario
            CampañaDto campañaDto = new CampañaDto(0, campañaNombre);
            UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre);

            // Crear el ReporteDto con los valores obtenidos
            ReporteDto reporteDto = new ReporteDto(0, titulo, descripcion, fechaCreacionDate, campañaDto, usuarioDto, tipoReporte, estado, comentario);

            // Llamar al servicio para guardar el reporte
            reporteService.guardarReporte(reporteDto);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson("Reporte creado con éxito"));
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al crear reporte: " + e.getMessage()));
        }
    }

    // Acción para listar todos los reportes
    private void listarReportes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<ReporteDto> reportes = reporteService.listarTodosLosReportes();
            response.getWriter().write(gson.toJson(reportes));
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al listar reportes: " + e.getMessage()));
        }
    }

    // Acción para buscar un reporte por ID
    private void buscarReportePorId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("El parámetro id es requerido"));
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            ReporteDto reporte = reporteService.buscarReportePorId(id);
            
            if (reporte != null) {
                response.getWriter().write(gson.toJson(reporte));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson("Reporte no encontrado"));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("ID de reporte inválido"));
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al buscar reporte: " + e.getMessage()));
        }
    }

    // Acción para actualizar un reporte
    private void actualizarReporte(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");
        String campañaNombre = request.getParameter("campaña");
        String usuarioNombre = request.getParameter("usuario");
        String tipoReporte = request.getParameter("tipoReporte");
        String estado = request.getParameter("estado");
        String comentario = request.getParameter("comentario");

        try {
            int id = Integer.parseInt(idStr);
            
            // Validar parámetros requeridos
            if (titulo == null || titulo.isEmpty() || descripcion == null || descripcion.isEmpty() ||
                fechaCreacion == null || fechaCreacion.isEmpty() || campañaNombre == null || campañaNombre.isEmpty() ||
                usuarioNombre == null || usuarioNombre.isEmpty() || tipoReporte == null || tipoReporte.isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson("Todos los campos son requeridos"));
                return;
            }

            // Convertir la fecha de String a LocalDate
            LocalDate fechaCreacionDate = LocalDate.parse(fechaCreacion);
            
            // Verificar si la campaña existe
            if (!reporteService.verificarCampañaExistente(campañaNombre)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson("Campaña no encontrada"));
                return;
            }

            // Verificar si el usuario tiene el rol adecuado
            if (!reporteService.verificarUsuarioPorRol(usuarioNombre, "analista")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(gson.toJson("El usuario no tiene el rol adecuado"));
                return;
            }

            // Crear los DTOs de campaña y usuario
            CampañaDto campañaDto = new CampañaDto(0, campañaNombre);
            UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre);

            // Crear el ReporteDto con los valores obtenidos
            ReporteDto reporteDto = new ReporteDto(id, titulo, descripcion, fechaCreacionDate, campañaDto, usuarioDto, tipoReporte, estado, comentario);

            // Llamar al servicio para actualizar el reporte
            reporteService.actualizarReporte(id, reporteDto);

            response.getWriter().write(gson.toJson("Reporte actualizado con éxito"));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("ID de reporte inválido"));
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al actualizar reporte: " + e.getMessage()));
        }
    }

    // Acción para eliminar un reporte
    private void eliminarReporte(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            reporteService.eliminarReporte(id);

            response.getWriter().write(gson.toJson("Reporte eliminado con éxito"));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("ID de reporte inválido"));
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al eliminar reporte: " + e.getMessage()));
        }
    }

    // Acción para listar reportes por fecha
    private void listarReportesPorFecha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fechaParam = request.getParameter("fecha");
        
        if (fechaParam == null || fechaParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("El parámetro fecha es requerido"));
            return;
        }
        
        try {
            LocalDate fecha = LocalDate.parse(fechaParam);
            List<ReporteDto> reportes = reporteService.buscarReportesPorFecha(fecha);
            response.getWriter().write(gson.toJson(reportes));
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("Error al procesar la fecha: " + e.getMessage()));
        }
    }

    // Acción para listar reportes por estado
    private void listarReportesPorEstado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String estado = request.getParameter("estado");
        
        if (estado == null || estado.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("El parámetro estado es requerido"));
            return;
        }
        
        try {
            List<ReporteDto> reportes = reporteService.buscarReportesPorEstado(estado);
            response.getWriter().write(gson.toJson(reportes));
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al listar reportes por estado: " + e.getMessage()));
        }
    }

    // Acción para listar reportes por campaña
    private void listarReportesPorCampaña(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String campañaNombre = request.getParameter("campaña");
        
        if (campañaNombre == null || campañaNombre.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("El parámetro campaña es requerido"));
            return;
        }
        
        try {
            CampañaDto campañaDto = new CampañaDto(0, campañaNombre);
            List<ReporteDto> reportes = reporteService.buscarReportesPorCampaña(campañaDto);
            response.getWriter().write(gson.toJson(reportes));
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al listar reportes por campaña: " + e.getMessage()));
        }
    }

    // Acción para listar reportes por usuario
    private void listarReportesPorUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usuarioNombre = request.getParameter("usuario");
        
        if (usuarioNombre == null || usuarioNombre.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("El parámetro usuario es requerido"));
            return;
        }
        
        try {
            UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre);
            List<ReporteDto> reportes = reporteService.buscarReportesPorUsuario(usuarioDto);
            response.getWriter().write(gson.toJson(reportes));
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al listar reportes por usuario: " + e.getMessage()));
        }
    }

    // Acción para listar reportes por tipo
    private void listarReportesPorTipo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tipoReporte = request.getParameter("tipoReporte");
        
        if (tipoReporte == null || tipoReporte.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("El parámetro tipoReporte es requerido"));
            return;
        }
        
        try {
            List<ReporteDto> reportes = reporteService.buscarReportesPorTipo(tipoReporte);
            response.getWriter().write(gson.toJson(reportes));
            
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error al listar reportes por tipo: " + e.getMessage()));
        }
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