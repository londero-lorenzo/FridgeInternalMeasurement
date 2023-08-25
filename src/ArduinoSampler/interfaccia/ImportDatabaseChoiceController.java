package ArduinoSampler.interfaccia;

import ArduinoSampler.database.Database;
import ArduinoSampler.database.Driver;
import ArduinoSampler.file_managers.ImExManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe ImportDatabaseChoiceController, utilizzata per gestire la scelta d'esportazione dei {@link Database} tramite interfaccia grafica.
 * <p>Estende {@link DefaultController}, {@link RDBMSController}</p>
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #next(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #getSelectedDriver()}
 *     </li>
 * </ul>
 */
public class ImportDatabaseChoiceController extends RDBMSController {

    /**
     * Variabile {@link BorderPane} utilizzata per ottenere lo {@link Stage} riferito a questa finestra.
     */
    @FXML
    private BorderPane mainPanel;

    /**
     * Variabile {@link TableView}, utilizzata per immagazzinare le informazioni principali dei {@link Driver} aggiunti.
     */
    @FXML
    private TableView<Driver> driverTableView;

    /**
     * Variabile {@link Label} che interrogare l'utente sulla scelta d'esportazione.
     */
    @FXML
    private Label choiceLabel;

    /**
     * Variabile {@link ToggleGroup} utilizzata per ottenere quale {@link RadioButton} è stato selezionato.
     */
    @FXML
    private ToggleGroup typeOfImport;
    /**
     * Variabile {@link RadioButton} utilizzata per selezionare l'opzione d'importare il {@link Database} dal {@link Driver}.
     */
    @FXML
    private RadioButton importFromDriver;

    /**
     * Variabile {@link RadioButton} utilizzata per selezionare l'opzione d'importare il {@link Database} da un file {@code .sql}.
     */
    @FXML
    private RadioButton importFromFile;

    /**
     * Variabile {@link Button} utilizzato per procedere con l'esportazione utilizzando il {@link Driver} selezionato.
     */
    @FXML
    private Button nextButton;

    /**
     * Variabile {@link Button} utilizzata per rimuovere il {@link Driver} selezionato.
     */
    @FXML
    private Button removeDriverButton;


    /**
     * {@link Driver} selezionato dalla {@link #driverTableView}.
     */
    private Driver selectedDriver;


    /**
     * Metodo utilizzato per:
     * <ul style="margin-top:0px">
     *     <li>
     *         aprire una nuova finestra per importare i vari {@link Database} dal {@link Driver}, se è stata selezionata tale opzione.
     *     </li>
     *     <li>
     *         indicare un unico file {@code .sql}, se è stata selezionata l'opzione di importare il {@link Database} da file.
     *     </li>
     * </ul>
     * <p>
     *     Una volta identificato il percorso e trovato il programma per l'importazione, verrà importato il {@link Database}.
     * </p>
     *
     * @param event L'evento catturato dal bottone
     * @see DefaultController
     * @see #createNewWindowForReplace(String, String, WindowSize)
     * @see FileChooser
     * @see ImExManager
     */
    @FXML
    public void next(ActionEvent event) {
        this.selectedDriver = this.driverTableView.getSelectionModel().getSelectedItem();
        if (typeOfImport.getSelectedToggle() == importFromDriver) {
            DefaultController newWindowController = this.createNewWindowForReplace("Importazione da Driver", "import_database_manager.fxml", new WindowSize(450, 400));

            //Esegue un override sul metodo di richiamo al momento della chiusura della finestra facendo riferimento all'Update della finestra precedente che caricherà i database
            newWindowController.stage.setOnHidden(e -> {
                if (this.previous_controller != null)
                    this.previous_controller.Update();
            });
            this.exit();
        } else if (typeOfImport.getSelectedToggle() == importFromFile) {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importa da file");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL", "*.sql"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tutti i file", "*.*"));
            Stage stage = (Stage) this.mainPanel.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                ImExManager importManager = new ImExManager(selectedDriver);
                importManager.setTarget("mysql.exe");
                String warningError;
                if (importManager.find()) {
                    if (importManager.importFromFile(file.getAbsolutePath())) {
                        typeOfImport.selectToggle(importFromDriver);
                        PRIMARY_CONTROLLER.getLogger().writeWithTime("Importazione verso il Driver " + this.selectedDriver.getJDBC_URL() + " completata...");
                        return;
                    }
                    else {
                        warningError = "Non è stato possibile importare il database";
                        PRIMARY_CONTROLLER.getLogger().write("Importazione verso il Driver " + this.selectedDriver.getJDBC_URL() + " fallita...");
                        PRIMARY_CONTROLLER.getLogger().write(warningError);
                    }

                } else {
                    if (Objects.equals(importManager.getImExStatus(), ImExManager.IMP_EXP_DISABLED))
                        warningError = "Attenzione!\nIl server è impostato per impedire le importazioni ed esportazioni," +
                                "\nsi consiglia di controllare la variabile secure_file_priv nel file my.ini";
                    else
                        warningError = "Attenzione!\nNon è stato possibile trovare " + importManager.getTarget();
                    PRIMARY_CONTROLLER.getLogger().writeWithTime("Importazione verso il Driver " + this.selectedDriver.getJDBC_URL() + " fallita...");
                    PRIMARY_CONTROLLER.getLogger().write(warningError);
                }
                this.createWarningDialogWindowToGoBack("Problema durante l'esportazione", warningError, new WindowSize(375, 125));
            }
        }
    }
    /**
     * Metodo utilizzato per disabilitare:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #choiceLabel}
     *     </li>
     *     <li>
     *         {@link #importFromDriver}
     *     </li>
     *     <li>
     *         {@link #importFromFile}
     *     </li>
     *     <li>
     *         {@link #nextButton}
     *     </li>
     *     <li>
     *         {@link #removeDriverButton}
     *     </li>
     * </ul>
     */
    public void disableObjs() {
        this.choiceLabel.setDisable(true);
        this.importFromDriver.setDisable(true);
        this.importFromFile.setDisable(true);
        this.nextButton.setDisable(true);
        this.removeDriverButton.setDisable(true);
    }
    /**
     * Metodo richiamato alla creazione del Controller, imposta un ascoltatore sul {@link #driverTableView} per abilitare dei bottoni solo dopo aver verificato la connessione del {@link Driver} selezionato.
     * <p>
     *     I bottoni che vengono abilitati sono:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #choiceLabel}
     *     </li>
     *     <li>
     *         {@link #importFromDriver}
     *     </li>
     *     <li>
     *         {@link #importFromFile}
     *     </li>
     *     <li>
     *         {@link #nextButton}
     *     </li>
     *     <li>
     *         {@link #removeDriverButton}
     *     </li>
     * </ul>
     *
     * <p>Richiama infine il metodo {@link #setTableFactory()}</p>
     */
    public void initialize() {
        this.driverTableView.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldDriverConnection, driverConnectionSelected) ->
        {
            if (driverConnectionSelected != null) { // condizione necessaria quando vengono eliminati tutti i driver
                if (observableValue.getValue().testConnection()) {
                    this.choiceLabel.setDisable(false);
                    this.importFromDriver.setDisable(false);
                    this.importFromFile.setDisable(false);
                    this.nextButton.setDisable(false);
                }
            }
            this.removeDriverButton.setDisable(false);
        }));
        this.setTableFactory();
    }

    /**
     * Metodo utilizzato per impostare lo Stage passato come parametro a questo controller.
     * Imposta anche i valori minimi di dimensione per questa finestra.
     * @param stage nuovo Stage
     */
    public void setStage(Stage stage)
    {
        super.setStage(stage);
        this.setMinSize(new WindowSize(660, 440));
    }

    /**
     * Metodo utilizzato per ottenere il {@link Driver} selezionato.
     * @return {@link Driver}
     */
    public Driver getSelectedDriver() {
        return this.selectedDriver;
    }

}
