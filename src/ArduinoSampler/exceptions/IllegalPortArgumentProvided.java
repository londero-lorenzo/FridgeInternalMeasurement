package ArduinoSampler.exceptions;

public class IllegalPortArgumentProvided extends SocketException{

    public IllegalPortArgumentProvided() {
        super("The port argument provided is outside the specified range of valid port values, which is between 0 and 65535, inclusive..");
        this.setExceptionType(ExceptionTypes.IllegalPortArgumentProvided);
    }
}
