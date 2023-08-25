package ArduinoSampler.interfaccia;

import javafx.scene.control.TextArea;

import java.sql.Timestamp;

/**
 * Classe Logger utilizzato per stampare su una {@link TextArea} dei messaggi.
 */
public class Logger {

    /**
     * Variabile {@link TextArea} sulla quale verranno stampati i messaggi.
     */
    private final TextArea textArea;

    /**
     * Costruttore della Classe {@link Logger}.
     * @param textArea {@link TextArea} da utilizzare
     */
    public Logger(TextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Metodo utilizzato per scrivere un messaggio e mandare immediatamente dopo a capo.
     * @param s stringa contenente il messaggio
     */
    public void write(String s) {
        this.textArea.appendText(s + "\n");
    }

    /**
     * Metodo utilizzato per scrivere un messaggio senza mandare a capo.
     * @param s stringa contenente il messaggio
     */
    public void writeInline(String s) {
        this.textArea.appendText(s);
    }


    public void writeOnConsole(String s){
        System.out.println(s);
    }
    /**
     * Metodo utilizzato per scrivere ottenere l'orario corrente sottoforma di stringa.
     */
    private String getTime() {
        return "[" + new Timestamp(System.currentTimeMillis()) + "]";
    }

    /**
     * Metodo utilizzato per scrivere l'orario e mandare immediatamente dopo a capo.
     */
    private void writeTime() {
        this.textArea.appendText(this.getTime() + "\n");
    }

    /**
     * Metodo utilizzato per scrivere un messaggio preceduto da orario.
     */
    public void writeWithTime(String s) {
        this.writeTime();
        this.write(s);
    }
}
