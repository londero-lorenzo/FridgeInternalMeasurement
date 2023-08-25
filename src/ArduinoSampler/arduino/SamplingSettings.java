package ArduinoSampler.arduino;

/**
 * <p>Classe SamplingSettings utilizzata per mantenere le informazioni sul campionamento.</p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #setPeriod(float)}
 *     </li>
 *     <li>
 *         {@link #setFrequency(float)}
 *     </li>
 *     <li>
 *         {@link #getPeriod_ms()}
 *     </li>
 *     <li>
 *         {@link #getFrequency()}
 *     </li>
 * </ul>
 */
public class SamplingSettings {

    /**
     * <p>Variabile float che indica il periodo di campionamento espresso in millisecondi.</p>
     * Il valore di questa variabile è necessario che sia in millisecondi, in quanto verrà utilizzata nel metodo {@link TimerController#start()}
     */
    private float period_ms = 1000;

    /**
     * <p>
     * Variabile float utilizzata per trasformare i millisecondi nell'unità di misura più appropriata per una semplice lettura all'interno del grafico del {@link interfaccia.IndexController#PRIMARY_CONTROLLER}.
     * </p>
     */
    private float multiplier = 1;

    /**
     * <p>Array di Stringhe contenenti le varie unità di misura del tempo.</p>
     */
    private final String[] timeIdentifiers = new String[]{"h", "min", "sec", "ms"};

    /**
     * Variabile di tipo stringa che identifica l'unità di misura del tempo.
     */
    private String timeIdentifier = "ms";

    public SamplingSettings()
    {
        this.setMultiplier();
    }

    /**
     * Metodo utilizzato per impostare {@link #period_ms}.
     *
     * @param period_s variabile float che indica il periodo di campionamento in millisecondi
     * @see #setMultiplier()
     */
    public void setPeriod(float period_s) {
        this.period_ms = period_s * 1000;
        this.setMultiplier();
    }

    /**
     * Metodo utilizzato per impostare {@link #period_ms} come frequenza.
     *
     * @param frequency float che indica la frequenza di campionamento
     * @see #setMultiplier()
     */
    public void setFrequency(float frequency) {
        this.period_ms = 1000 / frequency;
        this.setMultiplier();
    }


    /**
     * Metodo utilizzato per impostare il {@link #multiplier} e il {@link #timeIdentifier}.
     */
    private void setMultiplier() {
        if (this.period_ms >= 1000 * 60 * 60) //ore
        {
            this.multiplier = 1f / (1000 * 60 * 60);
            this.timeIdentifier = this.timeIdentifiers[0];
        } else if (this.period_ms >= 1000 * 60) //min
        {
            this.multiplier = 1f / (1000 * 60);
            this.timeIdentifier = this.timeIdentifiers[1];
        } else if (this.period_ms >= 1000) //sec
        {
            this.multiplier = 1f / 1000;
            this.timeIdentifier = this.timeIdentifiers[2];
        } else {
            this.multiplier = 1;
            this.timeIdentifier = this.timeIdentifiers[3];
        }
    }

    /**
     * Metodo utilizzato per ottenere l'intervallo di campionamento.
     *
     * @return {@link #period_ms}
     */
    public float getPeriod_ms() {
        return this.period_ms;
    }

    /**
     * Metodo utilizzato per ottenere la frequenza di campionamento.
     *
     * @return (1 / { @ link#period_ms }) {@link Float}
     */
    //viene utilizzata la Classe Float che abilita l'utilizzo del metodo "toString()"
    public Float getFrequency() {
        return 1000 / this.period_ms;
    }

    /**
     * Metodo utilizzato per ottenere {@link #multiplier}.
     *
     * @return {@link #multiplier}
     */
    public float getMultiplier() {
        return this.multiplier;
    }

    /**
     * Metodo utilizzato per ottenere {@link #timeIdentifier}.
     *
     * @return {@link #timeIdentifier}
     */
    public String getTimeIdentifier() {
        return this.timeIdentifier;
    }

}
