package dohi.weather.config;

import dohi.weather.error.InvalidDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // InvalidDate 예외를 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request 응답
    @ExceptionHandler(InvalidDate.class)
    public String handleInvalidDate(InvalidDate ex) {
        // 예외 메시지를 출력
        System.out.println("Invalid date error: " + ex.getMessage());
        return ex.getMessage(); // 클라이언트에게 메시지 반환
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public Exception handleAllException() {
        System.out.println("error from GlobalExceptionHandler");
        return new Exception();
    }
}
