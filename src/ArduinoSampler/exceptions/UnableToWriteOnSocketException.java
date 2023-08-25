package ArduinoSampler.exceptions;

public class UnableToWriteOnSocketException extends SocketException{

    public UnableToWriteOnSocketException() {
        super("Unable to write on connected socket.");
        this.setExceptionType(ExceptionTypes.UnableToWriteOnSocket);
    }
}
