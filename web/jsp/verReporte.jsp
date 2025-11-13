<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ver Reporte</title>
</head>
<body>
    <h1>Ver Reporte</h1>
    
    <c:if test="${not empty reporte}">
        <p><strong>ID:</strong> ${reporte.id}</p>
        <p><strong>Título:</strong> ${reporte.titulo}</p>
        <p><strong>Descripción:</strong> ${reporte.descripcion}</p>
        <p><strong>Fecha de Creación:</strong> ${reporte.fechaCreacion}</p>
        <p><strong>Campaña:</strong> ${reporte.campaña.nombre}</p>
        <p><strong>Usuario:</strong> ${reporte.usuarioM.nombre}</p>
        <p><strong>Tipo de Reporte:</strong> ${reporte.tipoReporte}</p>
        <p><strong>Estado:</strong> ${reporte.estado}</p>
        <p><strong>Comentario:</strong> ${reporte.comentario}</p>
    </c:if>
    
    <a href="ReporteServlet?action=listar">Volver a Listar Reportes</a>
</body>
</html>
