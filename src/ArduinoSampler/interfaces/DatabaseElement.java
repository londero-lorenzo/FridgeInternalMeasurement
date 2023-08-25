package ArduinoSampler.interfaces;

import java.util.Map;

/**
 * Interfaccia di un oggetto che riguarda i Database.
 *
 * <p>
 * Implementazioni:
 * </p>
 *     <ul style="margin-top:0px">
 *         <li>
 *             {@link database.Database}
 *         </li>
 *         <li>
 *             {@link database.Driver}
 *         </li>
 *     </ul>
 */
public interface DatabaseElement {

    /**
     * Metodo utilizzato per ottenere una mappa riguardante i dettagli dell'oggetto.
     *
     * @return {@link Map} con le informazioni
     */
    Map<String, String> getInformation();
}
