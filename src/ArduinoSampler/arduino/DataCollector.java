package ArduinoSampler.arduino;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Classe DataCollector utilizzata per l'immagazzinamento dei dati ricevuti ed elaborati.</p>
 * Metodi principali:
 * <ul>
 *     <li>
 *         {@link #setCollectionTime(long)}
 *     </li>
 *     <li>
 *         {@link #add(String, float)}
 *     </li>
 *     <li>
 *         {@link #getData()}
 *     </li>
 *     <li>
 *         {@link #getCollectionTime()}
 *     </li>
 *     <li>
 *         {@link #getCollectionDateTime()}
 *     </li>
 * </ul>
 */
public class DataCollector {

    /**
     * Variabile di tipo long utilizzata per registrare l'istante di tempo in cui è avvenuto il campionamento.
     */
    private long collectionTime;
    /**
     * Variabile {@link Map} che utilizza come chiave una variabile {@link String} e come valore una variabile {@link Float}.
     * <p>Viene utilizzata per immagazzinare i dati ricevuti da Arduino, a ogni valore corrisponde una chiave.</p>
     * <p>
     * es.
     * <ul style="margin-top: 0px">
     *     <li>
     *         "Temperature" => [value] ({@link Float})
     *     </li>
     *     <li>
     *         "Humidity" => [value] ({@link Float})
     *     </li>
     * </ul>
     */
    private final Map<String, Float> data;

    /**
     * Costruttore per la Classe d'immagazzinamento dati.
     */
    public DataCollector() {
        this.data = new HashMap<>();
    }

    /**
     * Metodo utilizzato per impostare la variabile {@link #collectionTime}.
     *
     * @param collectionTime valore di tipo long che rappresenta l'istante di tempo in cui è avvenuto il campionamento
     */
    public void setCollectionTime(long collectionTime) {
        this.collectionTime = collectionTime;
    }

    /**
     * Metodo utilizzato per inserire il dato raccolto all'interno della variabile {@link #data}.
     *
     * @param key  variabile {@link String} che rappresenta la chiave del dato da inserire
     * @param data dato da inserire (float)
     */
    public void add(String key, float data) {
        this.data.put(key, data);
    }

    /**
     * Metodo utilizzato per ottenere i dati memorizzati dalla classe.
     *
     * @return {@link #data}
     */
    public Map<String, Float> getData() {
        return this.data;
    }

    /**
     * Metodo utilizzato per ottenere solamente uno specifico dato memorizzato.
     *
     * @param key chiave ({@link String}) del dato da ottenere
     * @return {@link Float} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se la chiave è presente all'interno della variabile {@link #data}
     *     </li>
     * </ul>
     * {@code null} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se la chiave non è presente all'interno della variabile {@link #data}
     *     </li>
     * </ul>
     */
    public Float getData(String key) {
        if (this.data.containsKey(key))
            return this.data.get(key);
        return null;
    }


    /**
     * Metodo utilizzato per ottenere l'istante di tempo in cui è avvenuto il campionamento
     *
     * @return {@link #collectionTime}
     */
    public long getCollectionTime() {
        return this.collectionTime;
    }

    /**
     * Metodo utilizzato per ottenere l'istante di tempo in cui è avvenuto il campionamento in un formato utilizzabile da MySql
     *
     * @return {@link Timestamp}
     */
    public Timestamp getCollectionDateTime() {
        return new Timestamp(this.collectionTime);
    }

}
