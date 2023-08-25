package ArduinoSampler.exceptions;

public class NotRecognizedCommandException extends SocketException{
    public NotRecognizedCommandException() {
        super("Il server utilizza un protocollo di comunicazione diverso.\nComando ricevuto non riconosciuto...");
    }
}
