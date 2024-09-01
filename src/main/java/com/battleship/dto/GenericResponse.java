package com.battleship.dto;

public class GenericResponse<T> {
    private String status;
    private String message;
    private T data;

    public GenericResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> GenericResponse<T> success(String message, T data) {
        return new GenericResponse<>("success", message, data);
    }

    public static <T> GenericResponse<T> error(String message, T data) {
        return new GenericResponse<>("error", message, data);
    }
}
