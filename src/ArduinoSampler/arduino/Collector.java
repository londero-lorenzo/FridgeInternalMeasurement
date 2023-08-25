package ArduinoSampler.arduino;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;


/**
 * <p>Classe Collector utilizzata per la raccolta e l'elaborazione dei dati grezzi inviati da Arduino tramite l'apposita porta seriale.</p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #addData(int)}
 *     </li>
 *     <li>
 *         {@link #isCollecting}
 *     </li>
 *     <li>
 *         {@link #getIdentifier}
 *     </li>
 *     <li>
 *         {@link #getLastDataAsFloat}
 *     </li>
 *     <li>
 *         {@link #getLastDataAsDouble}
 *     </li>
 * </ul>
 */
public class Collector {
    /**
     * Variabile di tipo intero che assumerà il valore corrispondente al numero di byte che costituiscono il pacchetto da inviare contenente i dati.
     * <p>
     * es. <br>
     * ->   float = 4 byte; <br>
     * ->   double = 8 byte;
     */
    private final int receive_bytes = 4;

    /**
     * Variabile di tipo carattere utilizzata per capire quando è stata avviata la trasmissione dei dati.
     */
    private final char DataDelimiterOpen = '[';

    /**
     * Variabile di tipo carattere utilizzata per capire quando è stata terminata la trasmissione dei dati.
     */
    private final char DataDelimiterClose = ']';

    /**
     * Array di byte al cui interno verranno inseriti gli ottetti di bit che costituiscono il dato trasmesso.
     */
    private final byte[] last_raw_data_storage = new byte[receive_bytes];


    /**
     * Variabile di tipo intero utilizzata per inserire il byte ricevuto all'interno dell'array degli altri byte registrati.
     * <p>
     * Il valore di questa variabile va da zero fino al numero corrispondente ai pacchetti da ricevere (vedi: {@link #receive_bytes}).
     * </p>
     */
    private int index;

    /**
     * Variabile di tipo booleana utilizzata per indicare se sta avvenendo la raccolta dei dati.
     */
    private boolean is_collecting = false;

    /**
     * Variabile di tipo carattere che ha lo scopo d'identificare il raccoglitore.
     */
    private final char identifier;

    /**
     * Variabile di tipo intero utilizzata per immagazzinare il valore grezzo del dato ricevuto.
     */
    private int raw_int_data;


    /**
     * Costruttore per la classe di raccolta e conversione dei dati provenienti da Arduino.
     *
     * @param Identifier carattere identificativo della classe (vedi: {@link #identifier})
     */
    public Collector(char Identifier) {
        this.identifier = Identifier;
    }

    /**
     * Metodo utilizzato per richiamare l'identificativo della classe di raccolta.
     *
     * @return carattere identificativo della classe (vedi: {@link #identifier})
     */
    public char getIdentifier() {
        return this.identifier;
    }


    /**
     * Metodo per la raccolta dei dati, si differenzia in tre fasi:
     * <ul>
     *     <li>
     *         <b>Avvio della raccolta dei dati</b>:
     *         <p>
     *             Quando il valore in ingresso corrisponde al carattere di avvio trasmissione (vedi: {@link #DataDelimiterOpen})
     *         </p>
     *     </li>
     *     <li>
     *         <b>Raccolta dei dati</b>
     *         <p>
     *             Quando è stata avviata la raccolta, i dati successivamente ricevuti verranno immagazzinati nell'apposito array (vedi: {@link #last_raw_data_storage})
     *         </p>
     *     </li>
     *     <li>
     *         <b>Fine della raccolta dei dati</b>
     *         <p>
     *             Quando il valor in ingresso corrisponde al carattere di fine trasmissione (vedi: {@link #DataDelimiterClose})
     *         </p>
     *     </li>
     * </ul>
     * <br>
     *
     * @param data valore intero che contiene i dati ricevuti da Arduino
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui l'elaborazione del dato è avvenuta con successo
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se la raccolta dei dati è gia stata avviata ed è stato ricevuto nuovamente il carattere di avvio trasmissione (vedi: {@link #DataDelimiterOpen})
     *     </li>
     *     <li>
     *         se sono stati ricevuti dei dati fuori dalla seconda fase
     *     </li>
     * </ul>
     */
    public boolean addData(int data) { // add_data è in un loop while. data assume valori: {"[", byte, byte, byte, byte, "]"}
        if (data == this.DataDelimiterOpen && is_collecting) {
            PRIMARY_CONTROLLER.getLogger().write("Attenzione, è gia stata avviato un processo d'immagazzinamento dati");
            return false;
        }
        // è gia stata avviato il processo d'immagazzinamento dati

        if (data == this.DataDelimiterOpen) {
            this.is_collecting = true;
            this.index = 0;
            return true;
            // viene avviato il processo d'immagazzinamento dati
        }
        if (data == this.DataDelimiterClose) {
            this.is_collecting = false;
            this.storage();
            return true;
            // finisce il processo d'immagazzinamento dati e viene avviato il processo di elaborazione
        }
        if (this.is_collecting) {
            this.last_raw_data_storage[this.index] = (byte) data;
            this.index++;
            return true;
        }
        PRIMARY_CONTROLLER.getLogger().write("Attenzione, errore durante la raccolta dati");
        return false;
        // viene caricato il dato sotto forma di byte e restituito true per indicare che non sono sorte complicanze
    }

    /**
     * Metodo utilizzato per convertire i byte immagazzinati in un intero e salvare il risultato nella variabile {@link #raw_int_data}.
     * <p>
     * Il metodo utilizzato per la conversione utilizza l'operatore {@code or} e {@code left-shift (<<)} a livello di bit.
     * </p><br><br>
     *
     * <h2 style="margin-bottom:0">
     * Dimostrazione conversione
     * </h2>
     * <b><br>
     * Test eseguito con:
     *     <ul>
     *         <li>
     *             valore = 35.35241
     *         </li>
     *         <li>
     *             parametro {@link #receive_bytes} = 4 (float)
     *         </li>
     *     </ul>
     * </b>
     * <p>
     *         <p>valore binario inviato da arduino:<br>
     *         {@code 0100 0010 - 0000 1101 - 0110 1000 - 1101 1110}
     *         </p><br>
     *
     *         <p>valore binario ricevuto dal programma java:<br>
     *         {@code 1101 1110 - 0110 1000 - 0000 1101 - 0100 0010}
     *         </p><br>
     * <p>
     *         Si può notare che la sequenza ricevuta ha come valore di bit più significato pari a uno,
     *
     *         <p>java reinterpreta i valori tenendo conto del segno (<b>c2</b>), quindi:<br>
     *         <pre>&nbsp{@code (-34)       (104)        (13)       (66)}</pre>
     *         </p>
     * <p>
     *         Da qui è possibile comprendere che ci sono stati degli errori visto che il dato inviato da Arduino e quello ricevuto da Java hanno segno opposto. <br>
     * </p>
     * <p>
     * Osservando le varie sequenze è necessario invertire le posizioni dei vari byte
     * <p>
     * <pre style= "margin-top:10px">
     *                                   1101 1110  (0)
     * |                       0110 1000             (1 <<  8)
     * |            0000 1101                        (2 << 16)
     * | 0100 0010                                   (3 << 24)
     * ----------------------------------------------------------
     *  0100 0010  0000 1101  0110 1000  1101 1110 -> è possibile ora convertire il valore binario in variabile float
     * <p>
     * {@code IEEE 754 (0100 0010  0000 1101  0110 1000  1101 1110) = 35.35241 dec}
     * </pre>
     */
    private void storage() {
        this.raw_int_data = 0;
        for (int i = receive_bytes - 1; i >= 0; i--) {
            this.raw_int_data |= (this.last_raw_data_storage[i] & 0xFF) << 8 * i;
        }

        /*
         24 = 8 * 3
        int raw_int_data = ((this.last_raw_data_storage[3] & 0xFF) << 24) | ((this.last_raw_data_storage[2] & 0xFF) << 16) | ((this.last_raw_data_storage[1] & 0xFF) << 8) | (this.last_raw_data_storage[0] & 0xFF);
         */
    }

    /**
     * Metodo utilizzato per sapere se questo Collettore sta ancora raccogliendo i dati.
     *
     * @return {@link #is_collecting}
     */
    public boolean isCollecting() {
        return this.is_collecting;
    }

    /**
     * Metodo utilizzato per ottenere il valore raccolto sotto forma di Float.
     *
     * @return {@link #raw_int_data} {@code (Float)}
     */
    public Float getLastDataAsFloat() {
        return Float.intBitsToFloat(this.raw_int_data);
    }

    /**
     * Metodo utilizzato per ottenere il valore raccolto sotto forma di Double.
     *
     * @return {@link #raw_int_data} {@code (Double)}
     */
    public Double getLastDataAsDouble() {
        return Double.longBitsToDouble(this.raw_int_data);
    }
}
