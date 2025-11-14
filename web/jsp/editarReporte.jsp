<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Reporte</title>
</head>
<body>
    <h1>Editar Reporte</h1>
    <form action="ReporteServlet" method="POST">
        <input type="hidden" name="action" value="actualizar">
        <input type="hidden" name="id" value="${reporte.id}">
        
        <label for="titulo">Título:</label>
        <input type="text" name="titulo" value="${reporte.titulo}" required><br>
        
        <label for="descripcion">Descripción:</label>
        <textarea name="descripcion" required>${reporte.descripcion}</textarea><br>
        
        <label for="fechaCreacion">Fecha de Creación:</label>
        <input type="date" name="fechaCreacion" value="${reporte.fechaCreacion}" required><br>
        
        <label for="campaña">Campaña:</label>
        <input type="text" name="campaña" value="${reporte.campaña.nombre}" required><br>
        
        <label for="usuario">Usuario:</label>
        <input type="text" name="usuario" value="${reporte.usuarioM.nombre}" required><br>
        
        <label for="tipoReporte">Tipo de Reporte:</label>
        <input type="text" name="tipoReporte" value="${reporte.tipoReporte}" required><br>
        
        <label for="estado">Estado:</label>
        <input type="text" name="estado" value="${reporte.estado}" required><br>
        
        <label for="comentario">Comentario:</label>
        <textarea name="comentario">${reporte.comentario}</textarea><br>
        
        <button type="submit">Actualizar Reporte</button>
    </form>
    
    <a href="ReporteServlet?action=listar">Volver a Listar Reportes</a>
</body>
</html>
