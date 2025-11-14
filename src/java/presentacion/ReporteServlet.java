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

@WebServlet(name = "ReporteServlet", urlPatterns = {"/ReporteServlet"})
public class ReporteServlet extends HttpServlet {

    private final ReporteService reporteService;
    private final Gson gson;

    // Constructor
    public ReporteServlet() throws SQLException  {
        this.reporteService = new ReporteServiceImpl("TipoDb");
        this.gson = new Gson();
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
                JsonResponse<Object> jsonResponse = new JsonResponse<>(
                    false, 
                    "Acción no reconocida: " + action, 
                    null, 
                    HttpServletResponse.SC_BAD_REQUEST
                );
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(jsonResponse));
            }
        } catch (Exception e) {
            JsonResponse<Object> errorResponse = new JsonResponse<>(
                false, 
                "Error interno del servidor: " + e.getMessage(), 
                null, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
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
                
                JsonResponse<Object> jsonResponse = new JsonResponse<>(
                    false, 
                    "Todos los campos son requeridos", 
                    null, 
                    HttpServletResponse.SC_BAD_REQUEST
                );
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Convertir la fecha de String a LocalDate
            LocalDate fechaCreacionDate = LocalDate.parse(fechaCreacion);
            
            // Verificar si la campaña existe
            if (!reporteService.verificarCampañaExistente(campañaNombre)) {
                JsonResponse<Object> jsonResponse = new JsonResponse<>(
                    false, 
                    "Campaña no encontrada", 
                    null, 
                    HttpServletResponse.SC_NOT_FOUND
                );
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Verificar si el usuario tiene el rol adecuado
            if (!reporteService.verificarUsuarioPorRol(usuarioNombre, "analista")) {
                JsonResponse<Object> jsonResponse = new JsonResponse<>(
                    false, 
                    "El usuario no tiene el rol adecuado", 
                    null, 
                    HttpServletResponse.SC_FORBIDDEN
                );
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Crear los DTOs de campaña y usuario
            CampañaDto campañaDto = new CampañaDto(0, campañaNombre);
            UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre);

            // Crear el ReporteDto con los valores obtenidos
            ReporteDto reporteDto = new ReporteDto(0, titulo, descripcion, fechaCreacionDate, campañaDto, usuarioDto, tipoReporte, estado, comentario);

            // Llamar al servicio para guardar el reporte
            reporteService.guardarReporte(reporteDto);

            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                true, 
                "Reporte creado con éxito", 
                null, 
                HttpServletResponse.SC_CREATED
            );
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(jsonResponse));
        } catch (Exception e) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "Hubo un error al crear el reporte: " + e.getMessage(), 
                null, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }

    // Acción para listar todos los reportes
    private void listarReportes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ReporteDto> reportes = reporteService.listarTodosLosReportes();
        
        JsonResponse<List<ReporteDto>> jsonResponse = new JsonResponse<>(
            true, 
            "Reportes obtenidos exitosamente", 
            reportes, 
            HttpServletResponse.SC_OK
        );
        
        response.getWriter().write(gson.toJson(jsonResponse));
    }

    // Acción para buscar un reporte por ID
    private void buscarReportePorId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idd = request.getParameter("id");
        
        if (idd == null || idd.isEmpty()) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "El parámetro id es requerido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }
        
        try {
            int id = Integer.parseInt(idd);
            ReporteDto reporte = reporteService.buscarReportePorId(id);
            
            if (reporte != null) {
                JsonResponse<ReporteDto> jsonResponse = new JsonResponse<>(
                    true, 
                    "Reporte encontrado", 
                    reporte, 
                    HttpServletResponse.SC_OK
                );
                response.getWriter().write(gson.toJson(jsonResponse));
            } else {
                JsonResponse<Object> jsonResponse = new JsonResponse<>(
                    false, 
                    "Reporte no encontrado", 
                    null, 
                    HttpServletResponse.SC_NOT_FOUND
                );
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(jsonResponse));
            }
        } catch (NumberFormatException e) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "ID de reporte inválido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
        } catch (Exception e) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "Error al buscar el reporte: " + e.getMessage(), 
                null, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }

    // Acción para actualizar un reporte
    private void actualizarReporte(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idd = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");
        String campañaNombre = request.getParameter("campaña");
        String usuarioNombre = request.getParameter("usuario");
        String tipoReporte = request.getParameter("tipoReporte");
        String estado = request.getParameter("estado");
        String comentario = request.getParameter("comentario");

        try {
            int id = Integer.parseInt(idd);
            
            // Validar parámetros requeridos
            if (titulo == null || titulo.isEmpty() || descripcion == null || descripcion.isEmpty() ||
                fechaCreacion == null || fechaCreacion.isEmpty() || campañaNombre == null || campañaNombre.isEmpty() ||
                usuarioNombre == null || usuarioNombre.isEmpty() || tipoReporte == null || tipoReporte.isEmpty()) {
                
                JsonResponse<Object> jsonResponse = new JsonResponse<>(
                    false, 
                    "Todos los campos son requeridos", 
                    null, 
                    HttpServletResponse.SC_BAD_REQUEST
                );
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Convertir la fecha de String a LocalDate
            LocalDate fechaCreacionDate = LocalDate.parse(fechaCreacion);
            
            // Verificar si la campaña existe
            if (!reporteService.verificarCampañaExistente(campañaNombre)) {
                JsonResponse<Object> jsonResponse = new JsonResponse<>(
                    false, 
                    "Campaña no encontrada", 
                    null, 
                    HttpServletResponse.SC_NOT_FOUND
                );
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Verificar si el usuario tiene el rol adecuado
            if (!reporteService.verificarUsuarioPorRol(usuarioNombre, "analista")) {
                JsonResponse<Object> jsonResponse = new JsonResponse<>(
                    false, 
                    "El usuario no tiene el rol adecuado", 
                    null, 
                    HttpServletResponse.SC_FORBIDDEN
                );
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Crear los DTOs de campaña y usuario
            CampañaDto campañaDto = new CampañaDto(0, campañaNombre);
            UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre);

            // Crear el ReporteDto con los valores obtenidos
            ReporteDto reporteDto = new ReporteDto(id, titulo, descripcion, fechaCreacionDate, campañaDto, usuarioDto, tipoReporte, estado, comentario);

            // Llamar al servicio para actualizar el reporte
            reporteService.actualizarReporte(id, reporteDto);

            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                true, 
                "Reporte actualizado con éxito", 
                null, 
                HttpServletResponse.SC_OK
            );
            response.getWriter().write(gson.toJson(jsonResponse));
        } catch (NumberFormatException e) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "ID de reporte inválido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
        } catch (Exception e) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "Hubo un error al actualizar el reporte: " + e.getMessage(), 
                null, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }

    // Acción para eliminar un reporte
    private void eliminarReporte(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idd = request.getParameter("id");

        try {
            int id = Integer.parseInt(idd);
            reporteService.eliminarReporte(id);

            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                true, 
                "Reporte eliminado con éxito", 
                null, 
                HttpServletResponse.SC_OK
            );
            response.getWriter().write(gson.toJson(jsonResponse));
        } catch (NumberFormatException e) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "ID de reporte inválido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
        } catch (Exception e) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "Hubo un error al eliminar el reporte: " + e.getMessage(), 
                null, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }

    // Acción para listar reportes por fecha
    private void listarReportesPorFecha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fechaParam = request.getParameter("fecha");
        
        if (fechaParam == null || fechaParam.isEmpty()) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "El parámetro fecha es requerido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }
        
        try {
            LocalDate fecha = LocalDate.parse(fechaParam);
            List<ReporteDto> reportes = reporteService.buscarReportesPorFecha(fecha);
            
            JsonResponse<List<ReporteDto>> jsonResponse = new JsonResponse<>(
                true, 
                "Reportes por fecha obtenidos exitosamente", 
                reportes, 
                HttpServletResponse.SC_OK
            );
            
            response.getWriter().write(gson.toJson(jsonResponse));
        } catch (Exception e) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "Error al procesar la fecha: " + e.getMessage(), 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }

    // Acción para listar reportes por estado
    private void listarReportesPorEstado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String estado = request.getParameter("estado");
        
        if (estado == null || estado.isEmpty()) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "El parámetro estado es requerido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }
        
        List<ReporteDto> reportes = reporteService.buscarReportesPorEstado(estado);
        
        JsonResponse<List<ReporteDto>> jsonResponse = new JsonResponse<>(
            true, 
            "Reportes por estado obtenidos exitosamente", 
            reportes, 
            HttpServletResponse.SC_OK
        );
        
        response.getWriter().write(gson.toJson(jsonResponse));
    }

    // Acción para listar reportes por campaña
    private void listarReportesPorCampaña(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String campañaNombre = request.getParameter("campaña");
        
        if (campañaNombre == null || campañaNombre.isEmpty()) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "El parámetro campaña es requerido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }
        
        CampañaDto campañaDto = new CampañaDto(0, campañaNombre);
        List<ReporteDto> reportes = reporteService.buscarReportesPorCampaña(campañaDto);
        
        JsonResponse<List<ReporteDto>> jsonResponse = new JsonResponse<>(
            true, 
            "Reportes por campaña obtenidos exitosamente", 
            reportes, 
            HttpServletResponse.SC_OK
        );
        
        response.getWriter().write(gson.toJson(jsonResponse));
    }

    // Acción para listar reportes por usuario
    private void listarReportesPorUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usuarioNombre = request.getParameter("usuario");
        
        if (usuarioNombre == null || usuarioNombre.isEmpty()) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "El parámetro usuario es requerido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }
        
        UsuarioDto usuarioDto = new UsuarioDto(0, usuarioNombre);
        List<ReporteDto> reportes = reporteService.buscarReportesPorUsuario(usuarioDto);
        
        JsonResponse<List<ReporteDto>> jsonResponse = new JsonResponse<>(
            true, 
            "Reportes por usuario obtenidos exitosamente", 
            reportes, 
            HttpServletResponse.SC_OK
        );
        
        response.getWriter().write(gson.toJson(jsonResponse));
    }

    // Acción para listar reportes por tipo
    private void listarReportesPorTipo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tipoReporte = request.getParameter("tipoReporte");
        
        if (tipoReporte == null || tipoReporte.isEmpty()) {
            JsonResponse<Object> jsonResponse = new JsonResponse<>(
                false, 
                "El parámetro tipoReporte es requerido", 
                null, 
                HttpServletResponse.SC_BAD_REQUEST
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }
        
        List<ReporteDto> reportes = reporteService.buscarReportesPorTipo(tipoReporte);
        
        JsonResponse<List<ReporteDto>> jsonResponse = new JsonResponse<>(
            true, 
            "Reportes por tipo obtenidos exitosamente", 
            reportes, 
            HttpServletResponse.SC_OK
        );
        
        response.getWriter().write(gson.toJson(jsonResponse));
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