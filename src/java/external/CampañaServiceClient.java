package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CampañaServiceClient {

    // URL base del servicio CampañaService
    private static final String BASE_URL = "http://localhost:8080/CampañaService/CampañaServlet";

    // Método para verificar si la campaña existe
    public boolean verificarCampañaExistente(String nombreCampaña) {
        try {
            // Preparar los parámetros para la consulta
            String params = "nombre=" + URLEncoder.encode(nombreCampaña, "UTF-8");

            // URL del endpoint para verificar la campaña por nombre
            URL url = new URL(BASE_URL + "?action=verificarCampañaPorNombre&" + params);
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
                System.out.println("Error al verificar campaña. Código: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;  // Si hay algún error o la campaña no existe
    }
}
