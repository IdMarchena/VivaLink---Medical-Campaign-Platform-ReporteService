package external;

import dto.UsuarioDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UsuarioServiceClient {

    // URL base del servicio UsuarioService
    private static final String BASE_URL = "http://localhost:8080/UsuarioService/UsuarioServlet";

    // Método para verificar si el usuario existe y tiene el rol adecuado
    public boolean verificarUsuarioPorRol(String nombre, String rol) {
        try {
            // Preparar los parámetros para la consulta
            String params = String.format("nombre=%s&rol=%s", 
                URLEncoder.encode(nombre, "UTF-8"), 
                URLEncoder.encode(rol, "UTF-8")
            );

            // URL del endpoint para verificar usuario por nombre y rol
            URL url = new URL(BASE_URL + "?action=verificarUsuarioPorNombreYrol&" + params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Leer la respuesta del servidor
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    String response = content.toString();

                    // Procesar la respuesta JSON, por ejemplo, '{"valido":true}'
                    return response.contains("\"valido\":true");
                }
            } else {
                System.out.println("Error al verificar usuario. Código: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;  // Si hay algún error o el usuario no existe
    }
}
