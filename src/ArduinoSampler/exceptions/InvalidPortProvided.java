package ArduinoSampler.exceptions;

public class InvalidPortProvided extends SocketException {
    public InvalidPortProvided() {
        super("The port of the address provided is invalid.");
        this.setExceptionType(ExceptionTypes.InvalidPortProvided);
    }
}
