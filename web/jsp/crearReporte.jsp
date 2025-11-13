<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crear Reporte</title>
</head>
<body>
    <h1>Crear Reporte</h1>
    <form action="ReporteServlet" method="POST">
        <input type="hidden" name="action" value="crear">
        
        <label for="titulo">Título:</label>
        <input type="text" name="titulo" required><br>
        
        <label for="descripcion">Descripción:</label>
        <textarea name="descripcion" required></textarea><br>
        
        <label for="fechaCreacion">Fecha de Creación:</label>
        <input type="date" name="fechaCreacion" required><br>
        
        <label for="campaña">Campaña:</label>
        <input type="text" name="campaña" required><br>
        
        <label for="usuario">Usuario:</label>
        <input type="text" name="usuario" required><br>
        
        <label for="tipoReporte">Tipo de Reporte:</label>
        <input type="text" name="tipoReporte" required><br>
        
        <label for="estado">Estado:</label>
        <input type="text" name="estado" required><br>
        
        <label for="comentario">Comentario:</label>
        <textarea name="comentario"></textarea><br>
        
        <button type="submit">Crear Reporte</button>
    </form>
    
    <c:if test="${not empty mensaje}">
        <p>${mensaje}</p>
    </c:if>
    
    <a href="ReporteServlet?action=listar">Volver a Listar Reportes</a>
</body>
</html>
