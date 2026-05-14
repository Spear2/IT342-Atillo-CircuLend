package edu.cit.atillo.circulend.features.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ErrorBody error;
    private String timestamp;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.setSuccess(true);
        r.setData(data);
        r.setError(null);
        r.setTimestamp(Instant.now().toString());
        return r;
    }

    public static <T> ApiResponse<T> failure(String code, String message, Object details) {
        ApiResponse<T> r = new ApiResponse<>();
        r.setSuccess(false);
        r.setData(null);
        r.setError(new ErrorBody(code, message, details));
        r.setTimestamp(Instant.now().toString());
        return r;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorBody {
        private String code;
        private String message;
        private Object details;
    }
}
