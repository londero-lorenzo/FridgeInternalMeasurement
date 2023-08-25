package ArduinoSampler.arduino;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * <p>Classe CollectorList, utilizzata per gestire l'insieme delle classi {@link Collector}.</p>
 * Funziona prevalentemente come un Array:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #add(Collector)}
 *     </li>
 *     <li>
 *         {@link #remove(Collector)}
 *     </li>
 *     <li>
 *         {@link #getCollectors()}
 *     </li>
 *     <li>
 *         {@link #getCollectorsFromIdentifier(char)}
 *     </li>
 *     <li>
 *         {@link #addDataToCollector(int, char)}
 *     </li>
 * </ul>
 */
public class CollectorList {
    /**
     * Array di oggetti {@link Collector}
     */
    private Collector[] collectors = new Collector[0];


    /**
     * Metodo utilizzato per aggiungere {@link Collector} all'array {@link #collectors}.
     * <p>
     *     I collettori vengono aggiunti solamente se non sono già presenti all'interno di {@link #collectors}.
     * </p>
     * @param collector {@link Collector}
     */
    public void add(Collector collector) {
        if (!hasAlreadyBeenAdded(collector.getIdentifier())) {
            int no_collectors = this.collectors.length;
            Collector[] new_collectors = new Collector[no_collectors + 1];
            System.arraycopy(this.collectors, 0, new_collectors, 0, no_collectors);
            new_collectors[no_collectors] = collector;
            this.collectors = new_collectors;
        }else
            PRIMARY_CONTROLLER.getLogger().write(String.format("L'oggetto, con identificativo '%s', per raccolta dati è già stato aggiunto", collector.getIdentifier()));
    }

    /**
     * Metodo utilizzato per rimuovere {@link Collector} dall'array {@link #collectors}.
     * <p>
     *     La rimozione viene effettuata tramite il confronto degli identificatori tramite {@link Collector#getIdentifier()}
     * </p>
     * @param collector {@link Collector}
     */
    public void remove(Collector collector) {
        Collector[] new_collectors = new Collector[this.collectors.length];
        int counter = 0;
        for (Collector current_collector : this.collectors) {
            if (current_collector.getIdentifier() != collector.getIdentifier()) {
                new_collectors[counter] = current_collector;
                counter++;
            }
        }
        Collector[] new_collectors_resized = new Collector[counter];
        System.arraycopy(new_collectors, 0, new_collectors_resized, 0, counter);
        this.collectors = new_collectors_resized;
    }

    /**
     * Metodo utilizzato per verificare che siano presenti altri {@link Collector}.
     * @param identifier Identificatore del raccoglitore (vedi: {@link Collector#getIdentifier()})
     * @return
     * {@code true} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se è gia presente almeno un {@link Collector} con lo stesso identificatore
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se non è presente nessun {@link Collector} con lo stesso identificatore
     *     </li>
     * </ul>
     */
    private boolean hasAlreadyBeenAdded(char identifier) {
        for (Collector collector : this.collectors)
            if (collector.getIdentifier() == identifier)
                return true;
        return false;
    }

    /**
     * Metodo utilizzato per la restituzione dell'array contenente tutti i {@link Collector} aggiunti a questa Classe.
     * @return {@link #collectors} ({@code list})
     */

    public Collector[] getCollectors() {
        return this.collectors;
    }

    /**
     * Metodo utilizzato per ottenere il {@link Collector} in base al identificativo.
     * @param identifier Identificatore del raccoglitore (vedi: {@link Collector#getIdentifier()})
     * @return
     * {@link Collector} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se è stato trovato un {@link Collector} che abbia lo stesso identificatore
     *     </li>
     * </ul>
     * {@code null} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se non è presente nessun {@link Collector} con lo stesso identificatore
     *     </li>
     * </ul>
     */
    public Collector getCollectorsFromIdentifier(char identifier) {
        for (Collector collector : this.collectors)
            if (collector.getIdentifier() == identifier)
                return collector;
        return null;
    }


    /**
     * Metodo utilizzato per indirizzare i dati ricevuti da Arduino al {@link Collector} corrispondente in base all'identificatore.
     * @param data valore intero che contiene i dati ricevuti da Arduino
     * @param identifier identifier Identificatore del raccoglitore (vedi: {@link Collector#getIdentifier()})
     * @return valore booleano restituito dal metodo {@link Collector#addData(int)}
     */
    public boolean addDataToCollector(int data, char identifier) {
        return this.getCollectorsFromIdentifier(identifier).addData(data);
    }
}
