package dohi.weather.error;


public class InvalidDate extends RuntimeException {
    private static final String MESSAGE = "미래의 일기는 등록할 수 없습니다.";

    public InvalidDate() {
        super(MESSAGE);
    }
    public InvalidDate(String message) {
        super(message);
    }
}