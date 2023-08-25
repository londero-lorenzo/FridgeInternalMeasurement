package ArduinoSampler.arduino;

import ArduinoSampler.exceptions.ExceptionHandler;
import ArduinoSampler.exceptions.SocketException;
import ArduinoSampler.exceptions.InvalidIpv4AddressProvided;
import ArduinoSampler.exceptions.InvalidPortProvided;

/**
 * <h3>Address class</h3>
 * <br>
 * This class is used to generate ip addresses useful to indicate an opened socket.
 * <br>
 * These addresses use the Ipv4 standard, where a socket is identified by an IP address followed by the associated port.
 */
public class Address {

    private String ipv4;

    private int port;

    private ExceptionHandler exceptionHandler;

    private boolean usable = true;

    /**
     * This is the regular constructor in which an {@link Address} is created passing Ipv4 address and the relative port.
     *
     * @param ipv4 String including the Ipv4 address
     * @param port integer that indicate the port where a socket is opened
     */
    public Address(String ipv4, int port) {
        this.ipv4 = ipv4;
        this.port = port;
    }

    /**
     * This is a secondary constructor through which a {@link Address} is created after a failure in the process of creating the Ipv4 address.
     * <br>
     * The reasons for failure can be:
     * <ul style="margin-left:12px; padding:0; margin-top:0">
     *     <li>
     *         {@link InvalidIpv4AddressProvided Invalid Ipv4 Address Provided}
     *     </li>
     *     <li>
     *         {@link InvalidPortProvided Invalid Port Provided}
     *     </li>
     * </ul>
     *
     * @param socketException exception that led to failure.
     */
    public Address(SocketException socketException) {
        exceptionHandler = new ExceptionHandler();
        exceptionHandler.setException(socketException);
        this.usable = false;
    }

    /**
     * This is a static method to get an {@link Address} object using a string including the ipv4 and the relative port.
     *
     * @param addressAsString string including the ipv4 address and the port. <br>(e.g. {@code "192.168.0.1:25565"})
     * @return {@link Address} using the provided information. <br>
     * <b>Depending on the passed parameter, the Address object returned can be usable or unusable</b>
     */
    public static Address getAddressFromString(String addressAsString) {
        String[] addressParts = addressAsString.split(":");
        Ipv4 ipv4 = Ipv4.createIpv4FromString(addressParts[0]);
        if (ipv4 == null)
            return new Address(new InvalidIpv4AddressProvided());
        int port;
        try {
            port = Integer.parseInt(addressParts[1]);
        } catch (NumberFormatException ignored) {
            return new Address(new InvalidPortProvided());
        }
        return new Address(ipv4.toString(), port);
    }

    public boolean isUsable() {
        return this.usable;
    }

    public ExceptionHandler getExceptionHandler() {
        return this.exceptionHandler;
    }

    public String getIpv4() {
        return ipv4;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return this.ipv4 + ":" + this.port;
    }
}

/**
 * <h3>Ipv4 Address class</h3>
 * <br>
 * This class is used to simplify the Ipv4 string validation process provided.
 */
class Ipv4 {
    private final int xxx;
    private final int yyy;
    private final int zzz;
    private final int kkk;

    Ipv4(int xxx, int yyy, int zzz, int kkk) {
        this.xxx = xxx;
        this.yyy = yyy;
        this.zzz = zzz;
        this.kkk = kkk;
    }

    static Ipv4 createIpv4FromString(String ipv4AsString) {
        String[] ipv4Parts = ipv4AsString.split("\\.");
        if (ipv4Parts.length == 4) {
            for (String ipv4Part : ipv4Parts)
                if (ipv4Part.length() > 3)
                    return null;
            int[] ipv4 = new int[4];
            for (int i = 0; i < 4; i++)
                try {
                    ipv4[i] = Integer.parseInt(ipv4Parts[i]);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            return new Ipv4(ipv4[0], ipv4[1], ipv4[2], ipv4[3]);
        }
        return null;
    }

    @Override
    public String toString() {
        return Integer.toString(xxx) + '.' + yyy + '.' + zzz + '.' + kkk;
    }
}
