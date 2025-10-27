package blog.exceptions;

public class DuplicateReportException extends RuntimeException {
    public DuplicateReportException(String message) {
        super(message);
    }

    public DuplicateReportException() {
        super("You have already reported this content. Please wait for admin review.");
    }
}
