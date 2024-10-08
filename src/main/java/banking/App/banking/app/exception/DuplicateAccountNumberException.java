package banking.App.banking.app.exception;

public class DuplicateAccountNumberException extends RuntimeException{

    public DuplicateAccountNumberException(String message) {
        super(message);
    }
}
