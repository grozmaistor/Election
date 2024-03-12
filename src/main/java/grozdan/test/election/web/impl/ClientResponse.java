package grozdan.test.election.web.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ClientResponse() {
    public static String toJSONString(int code, String message) {
        return new StringBuilder()
                .append("{")
                .append("  \"code\": ").append(code).append(",\n")
                .append("  \"message\": \"").append(message).append("\"\n")
                .append("}")
                .toString();
    }

    public static String toBadRequestJSON(String message) {
        return ClientResponse.toJSONString(HttpStatus.BAD_REQUEST.value(), message);
    }

    public static ResponseEntity<String> toBadRequest(String message) {
        return ResponseEntity.badRequest().body(toBadRequestJSON(message));
    }

    public static ResponseEntity<String> toOK(String message) {
        return ResponseEntity.badRequest().body(toJSONString(HttpStatus.OK.value(), message));
    }
}