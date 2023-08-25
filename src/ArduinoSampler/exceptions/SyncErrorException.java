package ArduinoSampler.exceptions;

public class SyncErrorException extends SocketException{
    public SyncErrorException() {
        super("Errore di sincronia durante l'utilizzo del protocollo di comunicazione. ");
    }
}
