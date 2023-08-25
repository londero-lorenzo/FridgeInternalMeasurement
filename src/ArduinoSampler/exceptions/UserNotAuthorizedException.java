package ArduinoSampler.exceptions;

public class UserNotAuthorizedException extends SocketException{
    public UserNotAuthorizedException() {
        super("Non è stato inserita una parola d'ordine corretta.\nAccesso negato...");
    }
}
