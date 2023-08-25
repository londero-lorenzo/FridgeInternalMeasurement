package ArduinoSampler.interfaccia;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe DefaultController, utilizzato come Controller Generico.
 * <p>Si occupa di fornire dei metodi di default alle altre classi Controller</p>
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #setStage(Stage)}
 *     </li>
 *     <li>
 *         {@link #setPreviousController(DefaultController)}
 *     </li>
 *     <li>
 *         {@link #createNewWindowWithPriority(String, String, WindowSize)}
 *     </li>
 *     <li>
 *         {@link #createWarningDialogWindow(String, String)}
 *     </li>
 *     <li>
 *         {@link #createWarningDialogWindowToGoBack(String, String, WindowSize)}
 *     </li>
 * </ul>
 */
public class DefaultController {

    /**
     * Variabile {@link Stage} utilizzata per avere un riferimento alla finestra.
     */
    protected Stage stage;

    /**
     * Variabile {@link DefaultController} utilizzato per aver un riferimento al controller precedente rispetto alla pagina creata.
     */
    protected DefaultController previous_controller;

    /**
     * Metodo utilizzato per impostare lo Stage corrente a questo controller. <br>
     * Viene impostato il comportamento di default della finestra nel momento in cui viene chiusa,
     * il metodo utilizzato richiamerà la funzione <i>{@link #Update()}</i> nel precedente Controller per fare in modo che sappia quando far eseguire le istruzioni
     *
     * @param stage nuovo Stage
     */
    protected void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnHidden(e -> {
            if (this.previous_controller != null)
                this.previous_controller.Update();
        });
        //imposto il metodo che verrà richiamato alla chiusura dello stage
    }

    /**
     * Metodo utilizzato per impostare il {@link DefaultController} della finestra precedente.
     *
     * @param controller {@link DefaultController} della precedente finestra
     */
    protected void setPreviousController(DefaultController controller) {
        this.previous_controller = controller;
        this.Update();
    }

    /**
     * Metodo utilizzato per realizzare una finestra con la priorità su quella già aperta.
     *
     * @param title    stringa contenente il titolo della nuova finestra
     * @param xml_file stringa contente il percorso del file {@code .xml} riferito alla finestra da voler creare
     * @param size     variabile {@link WindowSize} che fa rifermento alle grandezze della finestra
     * @return {@link DefaultController} di questa finestra
     * @see WindowBuilder
     * @see WindowSize
     */
    DefaultController createNewWindowWithPriority(String title, String xml_file, WindowSize size) {
        WindowBuilder window_builder = new WindowBuilder(title, xml_file, size, this).build();
        window_builder.setOwner(this.stage);
        window_builder.show();
        return window_builder.getController();
    }

    /**
     * Metodo utilizzato per la realizzazione di una nuova finestra che andrà a sostituire in termini di priorità la finestra precedente
     * per fare questo restituisce il Controller che gestisce la nuova finestra e la imposta come genitore della finestra precedente alla
     * finestra che andrà a richiamare questa funzione.
     *
     * @param title    titolo della nuova finestra
     * @param xml_file nome del file xml a cui fa riferimento l'interfaccia grafica della nuova finestra
     * @param size     oggetto Window_size contenente la grandezza della finestra
     * @return {@link DefaultController}
     * @see WindowBuilder
     * @see WindowSize
     */
    public DefaultController createNewWindowForReplace(String title, String xml_file, WindowSize size) {
        WindowBuilder window_builder = new WindowBuilder(title, xml_file, size, this).build();
        window_builder.setOwner(this.previous_controller.stage);
        window_builder.show();
        return window_builder.getController();
    }

    /**
     * Metodo utilizzato per realizzare una finestra di dialogo che avvisi di eventuali errori insorti.
     *
     * @param title         stringa contenente la tipologia d'errore
     * @param warning_error stringa contente l'errore
     * @param size          variabile WindowSize contenente le dimensioni della finestra
     * @see WindowBuilder
     * @see WindowSize
     */
    public void createWarningDialogWindow(String title, String warning_error, WindowSize size) {
        DialogController controller = (DialogController) this.createNewWindowWithPriority(title, "dialog.fxml", size);
        EventHandler<ActionEvent> action = actionEvent -> {
            controller.exit();
        };
        controller.setProceedButtonName("OK");
        controller.leaveOnlyProceedButton();
        controller.setWarningLabel(warning_error);
        controller.setProceedButtonAction(action);
        controller.stage.setOnCloseRequest(e -> {
            if (controller.previous_controller != null) {
                controller.exit();
            }
        });
    }

    /**
     * Metodo utilizzato per realizzare una finestra di dialogo che avvisi di eventuali errori insorti.
     *
     * @param title         stringa contenente la tipologia d'errore
     * @param warning_error stringa contente l'errore
     * @see WindowBuilder
     * @see WindowSize
     */
    public void createWarningDialogWindow(String title, String warning_error) {
        WindowSize size = new WindowSize(300, 75);
        this.createWarningDialogWindow(title, warning_error, size);
    }

    /**
     * Metodo utilizzato per realizzare una finestra di dialogo che avvisi di eventuali errori insorti.
     * <p>
     * La finestra presenterà un bottone che se cliccato terminerà non solo la finestra di dialogo ma anche quella precedente.
     * </p>
     *
     * @param title         stringa contenente la tipologia d'errore
     * @param warning_error stringa contente l'errore
     * @see WindowBuilder
     * @see WindowSize
     */
    public void createWarningDialogWindowToGoBack(String title, String warning_error, WindowSize size) {
        DialogController controller = (DialogController) this.createNewWindowWithPriority(title, "dialog.fxml", size);
        EventHandler<ActionEvent> action = actionEvent -> {
            controller.exit();
            controller.previous_controller.exit();
        };
        controller.setProceedButtonName("OK");
        controller.leaveOnlyProceedButton();
        controller.setWarningLabel(warning_error);
        controller.setProceedButtonAction(action);
        controller.stage.setOnCloseRequest(e -> {
            if (controller.previous_controller != null) {
                controller.exit();
                controller.previous_controller.exit();
//                    this.previous_controller.Update();
            }
        });
    }

    /**
     * Metodo utilizzato per chiudere la finestra corrente e ritornare quindi allo stage precedente.
     */
    public void exit() {
        this.stage.close();
    }

    /**
     * Metodo utilizzato per impostare la dimensione minima della finestra.
     * @param minSize variabile {@link WindowSize} contenente la dimensione minima
     */

    public void setMinSize(WindowSize minSize) {
        this.stage.setMinWidth(minSize.width());
        this.stage.setMinHeight(minSize.height());
    }


    /**
     * Metodo utilizzato per essere richiamato ogni qualvolta una finestra viene chiusa.
     * <p>
     * Ogni Controller ha la possibilità di sovrascrivere questo metodo per poterlo utilizzare al suo interno.
     * </p>
     */
    public void Update() {
    }

    /**
     * Metodo utilizzato per la chiusura della finestra corrente tramite la pressione del bottone "<i>Torna indietro</i>"
     *
     * @param event L'evento catturato dal bottone
     */
    @FXML
    public void goBack(ActionEvent event) {
        this.exit();
    }
}

/**
 * Record WindowSize, utilizzato per definire le grandezze della finestra da voler creare.
 *
 * @param width  variabile di tipo intero che rappresenta la larghezza della finestra
 * @param height variabile di tipo intero che rappresenta la lunghezza della finestra
 */
record WindowSize(int width, int height) {
}

/**
 * Classe WindowBuilder, utilizzata per realizzare le varie finestra di gestione.
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #build()}
 *     </li>
 *     <li>
 *         {@link #setOwner(Stage)}
 *     </li>
 *     <li>
 *         {@link #show()}
 *     </li>
 *     <li>
 *         {@link #getController()}
 *     </li>
 * </ul>
 */

class WindowBuilder {

    /**
     * Variabile di tipo stringa contenente il titolo della finestra da realizzare.
     */
    private final String title;

    /**
     * Variabile di tipo stringa contenente il percorso del file {@code .xml} riferito alla finestra da realizzare.
     */
    private final String xml_file;

    /**
     * Variabile {@link WindowSize} contenente le informazioni sulla grandezza della finestra da realizzare.
     */
    private final WindowSize windowsize;

    /**
     * Variabile {@link DefaultController} che fa riferimento al Controller che andrà a gestire la finestra.
     */
    private DefaultController controller;

    /**
     * Variabile {@link DefaultController} che fa riferimento al Controller che gestisce la precedente finestra.
     */
    private final DefaultController previous_controller;

    /**
     * Variabile {@link Stage} che fa riferimento alla finestra da realizzare.
     */
    private Stage stage;

    /**
     * Costruttore della Classe {@link WindowBuilder}, si occupa d'inizializzare le seguenti variabili:
     * <ul style="margin-top: 0px">
     *     <li>
     *         {@link #title}
     *     </li>
     *     <li>
     *         {@link #xml_file}
     *     </li>
     *     <li>
     *         {@link #windowsize}
     *     </li>
     *     <li>
     *         {@link #previous_controller}
     *     </li>
     * </ul>
     *
     * @param title             stringa contenente il titolo della nuova finestra
     * @param xml_file          stringa contenente il percorso del file {@code .xml } riferito alla finestra da realizzare
     * @param size              variabile {@link WindowSize} al cui interno sono contenute le informazioni sulle dimensioni della nuova finestra
     * @param defaultController {@link DefaultController} che richiama questo costruttore
     */
    public WindowBuilder(String title, String xml_file, WindowSize size, DefaultController defaultController) {
        this.title = title;
        this.xml_file = xml_file;
        this.windowsize = size;
        this.previous_controller = defaultController;
    }

    /**
     * Metodo utilizzato per costruire la finestra.
     * <p>Viene restituito {@link WindowBuilder} così da permettere l'utilizzo in linea di questo metodo.</p>
     *
     * @return {@link WindowBuilder}
     */
    public WindowBuilder build() {
        try {
            this.stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(xml_file)));
            Parent root = loader.load();
            this.controller = loader.getController();
            this.controller.setStage(this.stage);
            this.controller.setPreviousController(this.previous_controller);
            this.stage.setTitle(this.title);
            this.stage.setScene(new Scene(root, this.windowsize.width(), this.windowsize.height()));
            this.stage.initModality(Modality.WINDOW_MODAL);
            return this;
        } catch (IOException e) {
            PRIMARY_CONTROLLER.getLogger().write(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo utilizzato per impostare il proprietario di questa finestra.
     * <p>Una volta impostato il proprietario, l'utente non sarà più in grado di svolgere operazioni su di esso fino a quando non viene chiusa questa finestra.</p>
     *
     * @param stage {@link Stage} del proprietario
     */
    public void setOwner(Stage stage) {
        this.stage.initOwner(stage);
    }

    /**
     * Metodo utilizzato per mostrare la finestra creata.
     */
    public void show() {
        this.stage.show();
    }

    /**
     * Metodo utilizzato per ottenere il {@link DefaultController} dalla finestra creata.
     *
     * @return {@link DefaultController}
     */
    public DefaultController getController() {
        return this.controller;
    }
}