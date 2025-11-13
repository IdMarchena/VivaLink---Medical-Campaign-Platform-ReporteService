<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Listar Reportes</title>
</head>
<body>
    <h1>Listado de Reportes</h1>
    
    <c:if test="${not empty reportes}">
        <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Título</th>
                    <th>Descripción</th>
                    <th>Fecha de Creación</th>
                    <th>Campaña</th>
                    <th>Usuario</th>
                    <th>Tipo de Reporte</th>
                    <th>Estado</th>
                    <th>Comentario</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="reporte" items="${reportes}">
                    <tr>
                        <td>${reporte.id}</td>
                        <td>${reporte.titulo}</td>
                        <td>${reporte.descripcion}</td>
                        <td>${reporte.fechaCreacion}</td>
                        <td>${reporte.campaña.nombre}</td>
                        <td>${reporte.usuarioM.nombre}</td>
                        <td>${reporte.tipoReporte}</td>
                        <td>${reporte.estado}</td>
                        <td>${reporte.comentario}</td>
                        <td>
                            <a href="ReporteServlet?action=buscar&id=${reporte.id}">Ver</a> | 
                            <a href="ReporteServlet?action=actualizar&id=${reporte.id}">Actualizar</a> | 
                            <a href="ReporteServlet?action=eliminar&id=${reporte.id}">Eliminar</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    
    <c:if test="${empty reportes}">
        <p>No hay reportes disponibles.</p>
    </c:if>
    
    <a href="ReporteServlet?action=crear">Crear Nuevo Reporte</a>
</body>
</html>
