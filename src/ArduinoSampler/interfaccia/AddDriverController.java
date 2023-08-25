package ArduinoSampler.interfaccia;

import ArduinoSampler.database.Driver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ArduinoSampler.database.Database;
import javafx.stage.Stage;

import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe AddDriverController, utilizzata per aggiungere i {@link Driver} all' {@link IndexController#PRIMARY_CONTROLLER}.
 * <p>Estende {@link DefaultController}</p>
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #add(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #initialize()}
 *     </li>
 * </ul>
 */
public class AddDriverController extends DefaultController {

    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Definizioni generali per il funzionamento del controller
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */

    // valori di default utilizzati per la connessione al RDBMS

    /**
     * Variabile di tipo stringa contenente il nome di default del Sistema di Gestione dei Database.
     */
    private final String namePlaceholder = "MySQL";
    /**
     * Variabile di tipo stringa contenente l'indirizzo di default del Sistema di Gestione dei Database.
     */
    private final String URLPlaceholder = "localhost";

    /**
     * Variabile di tipo stringa contenente la porta di default sulla quale opera il Sistema di Gestione dei Database.
     */
    private final String portPlaceholder = "3306";

    /**
     * Variabile di tipo stringa contenente il nome utente di default del Sistema di Gestione dei Database.
     */
    private final String userPlaceholder = "root";

    /**
     * Variabile di tipo stringa contenente la password di default del Sistema di Gestione dei Database.
     */
    private final String passwordPlaceholder = "root";


    // messaggi d'errore

    /**
     * Variabile di tipo stringa contenente il messaggio d'errore da mostrare nel momento di connessione fallita.
     */
    private final String connectionError = "Connessione fallita!";

    /**
     * Variabile di tipo stringa contenente il messaggio d'errore da mostrare nel momento in cui si vuole creare un Sistema di Gestione dei Database che ha un nome identico a uno di quelli già presenti.
     */
    private final String sameRdbmsNameError = "Esiste già un sistema di\ngestione con questo nome!";

    /**
     * Variabile di tipo stringa contenente il messaggio d'errore da mostrare nel momento in cui si vuole creare un Sistema di Gestione dei Database che ha un indirizzo identico a uno di quelli già presenti.
     */
    private final String sameJDBCurlError = "Esiste già un sistema di\ngestione con questo indirizzo!";


    /*
   --------------------------------------------------------------------------------------------------------------------------------------------------
   Fine definizioni generali per il funzionamento del controller
   --------------------------------------------------------------------------------------------------------------------------------------------------
    */


    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Definizioni generali per il funzionamento dell'interfaccia grafica
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * Variabile {@link TextField} che fa riferimento all'oggetto in cui viene scritto il nome del Sistema di Gestione da voler aggiungere
     */
    @FXML
    private TextField NameTextfield;

    /**
     * Variabile {@link TextField} che fa riferimento all'oggetto in cui viene scritto l'indirizzo del Sistema di Gestione da voler aggiungere
     */
    @FXML
    private TextField URLTextfield;

    /**
     * Variabile {@link TextField} che fa riferimento all'oggetto in cui viene scritta la porta per collegarsi al Sistema di Gestione da voler aggiungere
     */
    @FXML
    private TextField PortTextfield;

    /**
     * Variabile {@link TextField} che fa riferimento all'oggetto in cui viene scritto il nome utente per accedere al Sistema di Gestione da voler aggiungere
     */
    @FXML
    private TextField UserTextfield;

    /**
     * Variabile {@link TextField} che fa riferimento all'oggetto in cui viene scritta la password per accedere al Sistema di Gestione da voler aggiungere
     */
    @FXML
    private TextField PasswordTextfield;



    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Fine definizioni generali per il funzionamento dell'interfaccia grafica
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */


    /**
     * Metodo utilizzato per la verifica della connessione con il RDBMS.
     * <p>
     * In caso di esito positivo, viene passato il {@link Driver} di connessione al controller della finestra per la gestione dei {@link Database}.
     * </p>
     * <p>
     * In caso di esito negativo, viene mostrato a schermo una scritta di mancata connessione.
     * </p>
     *
     * @param event evento catturato dal bottone
     */
    @FXML
    void add(ActionEvent event) {
        String RDBMS_NAME = NameTextfield.getText().equals("") ? this.namePlaceholder : NameTextfield.getText();
        String RDBMS_URL = URLTextfield.getText().equals("") ? this.URLPlaceholder : URLTextfield.getText();
        String RDBMS_port = PortTextfield.getText().equals("") ? this.portPlaceholder : PortTextfield.getText();
        String RDBMS_user = UserTextfield.getText().equals("") ? this.userPlaceholder : UserTextfield.getText();
        String RDBMS_password = PasswordTextfield.getText().equals("") ? this.passwordPlaceholder : PasswordTextfield.getText();
        // vengono inizializzate le variable utili per effettuare la connessione
        Driver driver = new Driver(RDBMS_URL + ":" + RDBMS_port, RDBMS_user, RDBMS_password, RDBMS_NAME);

        // viene verificato il funzionamento della connessione tramite un test
        if (!driver.testConnection()) {
            this.createWarningDialogWindow("Errore", connectionError, new WindowSize(300, 85));
        } else {
            // se il test da esito positivo verifico che non sia presente un RDBMS simile
            for (Driver driver_ : PRIMARY_CONTROLLER.getDriversUsed().getDriverConnections()) {
                if (Objects.equals(driver_.getRDBMS_NAME(), RDBMS_NAME)) {
                    this.createWarningDialogWindow("Errore", sameRdbmsNameError, new WindowSize(300, 85));
                    return;
                } else if (Objects.equals(driver_.getJDBC_URL(), driver.getJDBC_URL())) {
                    this.createWarningDialogWindow("Errore", sameJDBCurlError, new WindowSize(300, 85));
                    return;
                }
            }
            // una volta verificato il tutto aggiungo il driver alla lista
            PRIMARY_CONTROLLER.addDriver(driver);
            this.exit();
        }
    }

    /**
     * Metodo richiamato alla creazione del Controller, imposta i valori di accesso di default come placeholder
     */
    public void initialize() {
        this.NameTextfield.setPromptText(this.namePlaceholder);
        this.URLTextfield.setPromptText(this.URLPlaceholder);
        this.PortTextfield.setPromptText(this.portPlaceholder);
        this.UserTextfield.setPromptText(this.userPlaceholder);
        this.PasswordTextfield.setPromptText(this.passwordPlaceholder);
    }

    /**
     * Metodo utilizzato per impostare lo Stage passato come parametro a questo controller.
     * Imposta anche i valori minimi di dimensione per questa finestra.
     *
     * @param stage nuovo Stage
     */
    public void setStage(Stage stage) {
        super.setStage(stage);
        this.setMinSize(new WindowSize(465, 380));
    }

}
