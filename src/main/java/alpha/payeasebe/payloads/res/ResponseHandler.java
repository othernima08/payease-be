package alpha.payeasebe.payloads.res;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static ResponseEntity<?> responseData(Integer statusCode, String message, Object data){
        Map<String, Object> body = new HashMap<>();
        body.put("status", statusCode);
        body.put("success", true);
        body.put("message", message);
        body.put("data", data);

        return ResponseEntity.status(statusCode).body(body);
    }

    public static ResponseEntity<?> responseMessage(Integer statusCode, String message, Boolean isSuccess){
        Map<String, Object> body = new HashMap<>();
        body.put("status", statusCode);
        body.put("success", isSuccess);
        body.put("message", message);

        return ResponseEntity.status(statusCode).body(body);
    }

    public static ResponseEntity<?> responseError(Integer statusCode, String message, Object error) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", statusCode);
        body.put("success", false);
        body.put("message", message);
        body.put("error", error);
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(statusCode).body(body);
    }
}
