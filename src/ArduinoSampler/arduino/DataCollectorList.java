package ArduinoSampler.arduino;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Classe DataCollectorList, utilizzata per gestire l'insieme delle classi {@link DataCollector}.</p>
 * Funziona prevalentemente come un Array:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #add(DataCollector)}
 *     </li>
 *     <li>
 *         {@link #remove(DataCollector)}
 *     </li>
 *     <li>
 *         {@link #getDataAverages()}
 *     </li>
 *     <li>
 *         {@link #getDataCollectors()}
 *     </li>
 *     <li>
 *         {@link #getDataCollector(int)}
 *     </li>
 *     <li>
 *         {@link #getDataCollectorFromEnd(int)}
 *     </li>
 *     <li>
 *         {@link #getLastDataCollector()}
 *     </li>
 * </ul>
 */

public class DataCollectorList {

    /**
     * Variabile {@link Map} utilizzata per mantenere i vari valori medi
     */
    private final Map<String, Float> avgs = new HashMap<>();

    /**
     * Array di oggetti {@link DataCollector}
     */
    private DataCollector[] dataCollectors = new DataCollector[0];


    /**
     * Metodo utilizzato per aggiungere {@link DataCollector} all'array {@link #dataCollectors}.
     *
     * @param collector {@link DataCollector}
     */
    public void add(DataCollector collector) {
        int no_collectors = this.dataCollectors.length;
        DataCollector[] new_data_collectors = new DataCollector[no_collectors + 1];
        System.arraycopy(this.dataCollectors, 0, new_data_collectors, 0, no_collectors);
        new_data_collectors[no_collectors] = collector;
        this.dataCollectors = new_data_collectors;
        this.calculate_avgs(collector);
    }

    /**
     * Metodo utilizzato per calcolare le varie per ogni chiave contenuta nel {@link DataCollector}
     *
     * @param collector {@link DataCollector}
     */
    private void calculate_avgs(DataCollector collector) {
        for (String key : collector.getData().keySet()) {
            float average = 0;
            if (avgs.containsKey(key))
                average = avgs.get(key);
            average *= this.dataCollectors.length - 1;
            average += collector.getData(key);
            average /= this.dataCollectors.length;
            avgs.put(key, average);
        }
    }

    /**
     * Metodo utilizzato per rimuovere {@link DataCollector} dall'array {@link #dataCollectors}.
     *
     * @param collector {@link DataCollector}
     */
    public void remove(DataCollector collector) {
        DataCollector[] new_data_collectors = new DataCollector[this.dataCollectors.length];
        int counter = 0;
        for (DataCollector current_data_collector : this.dataCollectors) {
            if (current_data_collector != collector) {
                new_data_collectors[counter] = current_data_collector;
                counter++;
            }
        }
        DataCollector[] new_collectors_resized = new DataCollector[counter];
        System.arraycopy(new_data_collectors, 0, new_collectors_resized, 0, counter);
        this.dataCollectors = new_collectors_resized;
    }

    /**
     * Metodo utilizzato per ottenere le varie medie
     *
     * @return {@link Map} contenente le varie medie sotto le apposite chiavi
     */
    public Map<String, Float> getDataAverages() {
        return this.avgs;
    }

    /**
     * Metodo utilizzato per la restituzione dell'array contenente tutti i {@link DataCollector} aggiunti a questa Classe.
     *
     * @return {@link #dataCollectors} ({@code list})
     */
    public DataCollector[] getDataCollectors() {
        return dataCollectors;
    }

    /**
     * Metodo utilizzato per ottenere il {@link DataCollector} nella data posizione dell'array {@link #dataCollectors}
     *
     * @param index valore intero che indica la posizione del {@link DataCollector}
     * @return {@link DataCollector}
     */
    public DataCollector getDataCollector(int index) {
        return (index < this.dataCollectors.length) ? this.dataCollectors[index] : null;
    }

    /**
     * Metodo utilizzato per ottenere il {@link DataCollector} nella data posizione rispetto alla fine dell'array {@link #dataCollectors}
     *
     * @param index valore intero che indica la posizione del {@link DataCollector}
     * @return {@link DataCollector}
     */
    public DataCollector getDataCollectorFromEnd(int index) {
        return (index < this.dataCollectors.length) ? this.dataCollectors[(this.dataCollectors.length - 1) - index] : null;
    }

    /**
     * Metodo utilizzato per ottenere l'ultimo set di dati raccolti
     *
     * @return {@link DataCollector} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se sono stati raccolti dati
     *     </li>
     * </ul>
     * {@code null} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se non sono presenti dati raccolti
     *     </li>
     * </ul>
     */
    public DataCollector getLastDataCollector() {
        if (this.dataCollectors.length != 0)
            return this.dataCollectors[this.dataCollectors.length - 1];
        return null;
    }


}