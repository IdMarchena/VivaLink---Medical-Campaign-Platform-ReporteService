/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;

/**
 *
 * @author Usuario
 */
public class Reporte {
    private int id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private Campaña campaña;
    private Usuario usuarioM;
    private String tipoReporte;
    private String estado;
    private String comentario;

    @Override
    public String toString() {
        return "Reporte{" + "id=" + getId() + ", titulo=" + getTitulo() + ", descripcion=" + getDescripcion() + ", fechaCreacion=" + getFechaCreacion() + ", campa\u00f1a=" + getCampaña() + ", usuarioM=" + getUsuarioM() + ", tipoReporte=" + getTipoReporte() + ", estado=" + getEstado() +",comentario="+getComentario() +'}';
    }

    public Reporte(int id, String titulo, String descripcion, LocalDate fechaCreacion, Campaña campaña, Usuario usuarioM, String tipoReporte, String estado,String comentario) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.campaña = campaña;
        this.usuarioM = usuarioM;
        this.tipoReporte = tipoReporte;
        this.estado = estado;
        this.comentario=comentario;
        
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the fechaCreacion
     */
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * @param fechaCreacion the fechaCreacion to set
     */
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * @return the campaña
     */
    public Campaña getCampaña() {
        return campaña;
    }

    /**
     * @param campaña the campaña to set
     */
    public void setCampaña(Campaña campaña) {
        this.campaña = campaña;
    }

    /**
     * @return the usuarioM
     */
    public Usuario getUsuarioM() {
        return usuarioM;
    }
    public String getComentario() {
        return comentario;
    }

    /**
     * @param usuarioM the usuarioM to set
     */
    public void setUsuarioM(Usuario usuarioM) {
        this.usuarioM = usuarioM;
    }

    /**
     * @return the tipoReporte
     */
    public String getTipoReporte() {
        return tipoReporte;
    }

    /**
     * @param tipoReporte the tipoReporte to set
     */
    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
