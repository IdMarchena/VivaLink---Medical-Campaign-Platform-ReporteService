/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Usuario
 */
public class JsonResponse<T>{
    private boolean succes;
    private String message;
    private T data;
    private int status;
    private String timestamp;

    @Override
    public String toString() {
        return "JsonResponse{" + "succes=" + isSucces() + ", message=" + getMessage() + ", data=" + getData() + ", status=" + getStatus() + ", timestamp=" + getTimestamp() + '}';
    }

    public JsonResponse(boolean succes, String message, T data, int status) {
        this.succes = succes;
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * @return the succes
     */
    public boolean isSucces() {
        return succes;
    }

    /**
     * @param succes the succes to set
     */
    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
