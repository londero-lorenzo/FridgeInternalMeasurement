package ArduinoSampler.interfaces;

import java.util.HashMap;
import java.util.Map;

/**
 * Interfaccia di un oggetto che riguarda le Liste.
 *
 * <p>
 * Implementazioni:
 * </p>
 *     <ul style="margin-top:0px">
 *         <li>
 *             {@link database.DatabaseList}
 *         </li>
 *         <li>
 *             {@link database.DriverList}
 *         </li>
 *     </ul>
 */

public interface ListHandler {

    /**
     * Metodo utilizzato per aggiungere {@link DatabaseElement} alla lista.
     *
     * @param d {@link DatabaseElement}
     */
    void add(DatabaseElement d);

    /**
     * Metodo utilizzato per rimuovere {@link DatabaseElement} dalla lista.
     *
     * @param d {@link DatabaseElement}
     */
    void remove(DatabaseElement d);

    default void addFromRawInformation(String d) {
    }

    /**
     * Metodo utilizzato per ottenere una {@link Map} da una stringa, utilizzato in fase di lettura da file di salvataggio.
     * @return {@link Map} con le informazioni mappate
     */
    default Map<String, String> fromStringToMap(String map_string) {
        String information = map_string.substring(1, map_string.length() - 1);
        Map<String, String> map_information = new HashMap<>();
        String[] pairs = information.split(", ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            map_information.put(keyValue[0], keyValue[1]);
        }
        return map_information;
    }
}

