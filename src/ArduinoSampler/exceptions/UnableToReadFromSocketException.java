package ArduinoSampler.exceptions;

public class UnableToReadFromSocketException extends SocketException {

    public UnableToReadFromSocketException() {
        super("Errore durante la lettura dei dati in ricezione.");
        this.setExceptionType(ExceptionTypes.UnableToReadFromSocket);
    }
}
