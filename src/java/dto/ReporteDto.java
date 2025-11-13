/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package dto;

import java.time.LocalDate;
/**
 *
 * @author Usuario
 */
public record ReporteDto(int id,
                        String titulo,
                        String descripcion,
                        LocalDate fechaCreacion,
                        CampañaDto campaña,
                        UsuarioDto usuarioM,
                        String tipoReporte,
                        String estado,
                        String comentario) {

}
