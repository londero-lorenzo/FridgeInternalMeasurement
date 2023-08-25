package ArduinoSampler.exceptions;

public class InvalidIpv4AddressProvided extends SocketException {

    public InvalidIpv4AddressProvided() {
        super("The Ipv4 address provided is invalid.");
        this.setExceptionType(ExceptionTypes.InvalidIpv4AddressProvided);
    }
}
