package ArduinoSampler.arduino;

//import com.fazecast.jSerialComm.SerialPort;

/**
 * <p>Classe SerialList, utilizzata per gestire l'insieme delle classi {@link Server}.</p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #add(Server)}
 *     </li>
 *     <li>
 *         {@link #remove(Server)}
 *     </li>
 *     <li>
 *         {@link #autoInitialize()}
 *     </li>
 *     <li>
 *         {@link #close_all()}
 *     </li>
 *     <li>
 *         {@link #remove_all()}
 *     </li>
 *     <li>
 *         {@link #getSerials()}
 *     </li>
 *     <li>
 *         {@link #getSerialPortFromPort(String)}
 *     </li>
 * </ul>
 */
public class ServerList {

    /**
     * Array di oggetti {@link Server}
     */
    private Server[] serialPorts = new Server[0];

    /**
     * Metodo utilizzato per aggiungere {@link DataCollector} all'array {@link #serialPorts}.
     *
     * @param serialPort {@link Server}
     */

    public void add(Server serialPort) {
        int numbersOfServer = this.serialPorts.length;
        Server[] new_serialPorts = new Server[numbersOfServer + 1];
        System.arraycopy(this.serialPorts, 0, new_serialPorts, 0, numbersOfServer);
        new_serialPorts[numbersOfServer] = serialPort;
        this.serialPorts = new_serialPorts;
    }

    /**
     * Metodo utilizzato per rimuovere {@link DataCollector} dall'array {@link #serialPorts}.
     *
     * @param serial {@link DataCollector}
     */
    public void remove(Server serial) {
        /*
        Esp32_Server[] newSerialPorts = new Esp32_Server[this.serialPorts.length];
        int counter = 0;
        for (Esp32_Server current_serialport : this.serialPorts) {
            if (!Objects.equals(current_serialport.getPortLocation(), serial.getPortLocation())) {
                newSerialPorts[counter] = current_serialport;
                counter++;
            }
        }
        Esp32_Server[] new_collectors_resized = new Esp32_Server[counter];
        System.arraycopy(newSerialPorts, 0, new_collectors_resized, 0, counter);
        this.serialPorts = new_collectors_resized;

         */
    }

    /**
     * Metodo utilizzato per chiudere la comunicazione con tutte le porte seriali registrate.
     */
    public void close_all() {
        for (Server serial : this.serialPorts) {
            serial.close();
        }
    }

    /**
     * Metodo utilizzato per ripristinare l'array {@link #serialPorts}.
     */
    public void remove_all() {
        this.serialPorts = new Server[0];
    }


    /**
     * Metodo utilizzato per ottenere la lista contenente i {@link Server}.
     *
     * @return {@link #serialPorts} ({@code list})
     */
    public Server[] getSerials() {
        return this.serialPorts;
    }

    /**
     * Metodo utilizzato per ottenere la seriale dal nome della porta.
     *
     * @param port Stringa contenente il nome della porta seriale
     * @return {@link Server} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se {@link #serialPorts} contiene una porta seriale con tale nome
     *     </li>
     * </ul>
     * {@code null} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se non c'è stato nessun riscontro
     *     </li>
     * </ul>
     */
    public Server getSerialPortFromPort(String port) {
        /*
        for (Esp32_Server serial : this.serialPorts) {
            if (Objects.equals(serial.getPortLocation(), port))
                return serial;
        }
         */
        return null;
    }
}
