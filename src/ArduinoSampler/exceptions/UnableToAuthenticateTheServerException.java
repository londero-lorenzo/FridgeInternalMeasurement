package ArduinoSampler.exceptions;

public class UnableToAuthenticateTheServerException extends SocketException{
    public UnableToAuthenticateTheServerException() {
        super("Impossibile autenticare il server.");
        this.setExceptionType(ExceptionTypes.IllegalPortArgumentProvided);
    }
}
