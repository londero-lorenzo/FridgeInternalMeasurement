package ArduinoSampler.interfaccia;

import ArduinoSampler.database.Database;
import ArduinoSampler.database.Driver;
import ArduinoSampler.file_managers.ImExManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe RDBMSController, utilizzata per gestire i {@link Driver} e creare {@link Database}.
 * <p>Estende {@link DefaultController}</p>
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #next(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #removeDriver(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #refreshDriverTable()}
 *     </li>
 * </ul>
 */

public class RDBMSController extends DefaultController {

    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Definizioni generali per il funzionamento del controller
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * Variabile di tipo stringa contenente il messaggio d'errore da mostrare nel momento di mancata creazione.
     */
    private final String genericError = "Errore generico durante la creazione!";

    /**
     * Variabile di tipo stringa contenente il messaggio d'errore da mostrare nel momento in cui si vuole creare un {@link Database} avente nome invalido.
     */
    private final String invalidDatabaseNameError = "Nome del database invalido!";

    /**
     * Variabile di tipo stringa contenente il messaggio d'errore da mostrare nel momento in cui si vuole creare un {@link Database} con lo stesso nome di un altro presente sul sistema di gestione.
     */
    private final String sameDatabaseNameError = "Esiste già un database con questo nome!";

    /**
     * Variabile di tipo stringa contente il percorso relativo alla struttura sql di un genere {@link Database} utilizzato come template.
     */
    private final String mysqlTemplatePath = "mysql_template/template.sql";

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
     * Variabile {@link TextField} utilizzato per scrivere il nome del {@link Database} che si vuole create.
     */
    @FXML
    private TextField databaseNameField;

    /**
     * Variabile {@link Label} che avvisa che è possibile inserire il nome del {@link Database}.
     */
    @FXML
    private Label databaseLabel;

    /**
     * Variabile {@link TableView} utilizzata per immagazzinare i {@link Driver}.
     */

    @FXML
    private TableView<Driver> driverTableView;

    /**
     * Variabile di tipo {@link TableColumn} nella quale verranno inseriti i nomi relativi ai {@link Driver}.
     */
    @FXML
    private TableColumn<Driver, String> driverNameColumn;

    /**
     * Variabile di tipo {@link TableColumn} nella quale verranno inseriti gli indirizzi JDBC dei {@link Driver}.
     */
    @FXML
    private TableColumn<Driver, String> driverJDBCURLColumn;

    /**
     * Variabile di tipo {@link Button} a cui fa riferimento il metodo {@link #next(ActionEvent)}
     */
    @FXML
    private Button nextButton;

    /**
     * Variabile di tipo {@link Button} a cui fa riferimento il metodo {@link #removeDriver(ActionEvent)}
     */
    @FXML
    private Button removeDriverButton;

    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Fine definizioni generali per il funzionamento dell'interfaccia grafica
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */


    /**
     * Metodo utilizzato per la creazione della finestra che si occupa dell'aggiunta dei vari sistemi di gestione dei DataBase.
     *
     * @param event L'evento catturato dal bottone
     */
    @FXML
    public void openDriverAdderInterface(ActionEvent event) {
        this.disableObjs();
        this.createNewWindowWithPriority("Imposta credenziali", "addRDBMS_manager_interface.fxml", new WindowSize(500, 350));
    }

    /**
     * Metodo utilizzato per realizzare un {@link Database} avente lo stesso nome contenuto in {@link #databaseNameField}.
     *
     * @param event evento catturato dal bottone
     */
    public void next(ActionEvent event) {
        if (databaseNameField.getText().equals("")) {
            this.createWarningDialogWindow("Problema di creazione", this.invalidDatabaseNameError);
            return;
        }
        Driver selected_driver = driverTableView.getSelectionModel().getSelectedItem();
        Database db = new Database(selected_driver, databaseNameField.getText());
        if (db.hasValidName())
            if (db.build()) {
                ImExManager import_manager = new ImExManager(selected_driver);
                import_manager.setTarget("mysql.exe");
                String warning_error;
                WindowSize size;
                if (import_manager.find()) {
                    if (import_manager.importFromFileWithDatabaseName(db.getName(), new File(this.mysqlTemplatePath).getAbsolutePath())) {
//                        System.out.println(db.getName());
//                        System.out.println(new File(this.MySql_TEMPLATE_path).getAbsolutePath());
                        db.buildDataStructure();
                        PRIMARY_CONTROLLER.addDatabase(db);
                        this.exit();
                        return;
                    } else {
                        warning_error = "Attenzione!\nNon è stato possibile realizzare il database";
                        size = new WindowSize(300, 100);
                        PRIMARY_CONTROLLER.getLogger().write(warning_error);
                    }
                } else {
                    if (Objects.equals(import_manager.getImExStatus(), ImExManager.IMP_EXP_DISABLED)) {
                        warning_error = "Attenzione!\nIl server è impostato per impedire le importazioni ed esportazioni," +
                                "\nsi consiglia di controllare la variabile secure_file_priv nel file my.ini";
                        size = new WindowSize(375, 125);
                    } else {
                        warning_error = "Attenzione!\nNon è stato possibile trovare " + import_manager.getTarget();
                        size = new WindowSize(300, 100);
                    }
                    PRIMARY_CONTROLLER.getLogger().writeWithTime(warning_error);
                }
                this.createWarningDialogWindowToGoBack("Problema durante l'importazione", warning_error, size);

                db.delete();
            } else {
                this.createWarningDialogWindow("Problema di creazione", this.genericError);
            }
        else {
            this.createWarningDialogWindow("Problema di creazione", this.sameDatabaseNameError);
        }
    }


    /**
     * Metodo utilizzato per rimuovere il {@link Driver} da {@link IndexController#PRIMARY_CONTROLLER}
     *
     * @param event evento catturato dal bottone
     */
    @FXML
    public void removeDriver(ActionEvent event) {
        Driver driver_selected = driverTableView.getSelectionModel().getSelectedItem();
        if (PRIMARY_CONTROLLER.getDatabasesUsed().getDatabasesByJDBC_URL(driver_selected.getJDBC_URL()).getDatabases().length > 0) {
            DialogController controller = (DialogController) this.createNewWindowWithPriority("Eliminazione sistema di gestione", "dialog.fxml", new WindowSize(300, 100));
            EventHandler<ActionEvent> action = actionEvent -> {
                PRIMARY_CONTROLLER.removeDriver(driver_selected);
                PRIMARY_CONTROLLER.removeDatabasesFromJDBCURL(driver_selected.getJDBC_URL());
                this.Update();
                this.disableObjs();
                controller.exit();
            };
            controller.setWarningLabel("Attenzione!\nEliminando questo sistema di gestione\nrimuoverai anche i database associati!");
            controller.setProceedButtonAction(action);
        } else {
            PRIMARY_CONTROLLER.removeDriver(driver_selected);
            PRIMARY_CONTROLLER.removeDatabasesFromJDBCURL(driver_selected.getJDBC_URL());
            this.Update();
            this.disableObjs();
        }

    }

    /**
     * Metodo utilizzato per disabilitare:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #databaseLabel}
     *     </li>
     *     <li>
     *         {@link #databaseNameField}
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
        this.databaseLabel.setDisable(true);
        this.databaseNameField.setDisable(true);
        this.nextButton.setDisable(true);
        this.removeDriverButton.setDisable(true);
    }

    /**
     * Metodo realizzato per aggiornare i valori contenuti in {@link #driverTableView}.
     */
    public void refreshDriverTable() {
        final ObservableList<Driver> data = FXCollections.observableArrayList(
                PRIMARY_CONTROLLER.getDriversUsed().getDriverConnections()
        );
        driverTableView.getItems().clear();
        driverTableView.refresh();
        driverTableView.setItems(data);
    }

    /**
     * Metodo utilizzato per modificare il comportamento delle celle di {@link #driverTableView}.
     * <p>
     * Implementa {@link Callback}
     * </p>
     * <p>
     * Questa Classe sovrascrive il metodo {@link Callback#call(Object)} in modo tale da realizzare delle {@link ListCell} personalizzate.
     * </p>
     * <p>Evidenzia in rosso i {@link Driver} irraggiungibili</p>
     */
    public void setTableFactory() {
        driverNameColumn.setCellValueFactory(new PropertyValueFactory<>("RDBMS_NAME"));
        driverJDBCURLColumn.setCellValueFactory(new PropertyValueFactory<>("JDBC_URL"));
        driverTableView.setRowFactory(new Callback<TableView<Driver>, TableRow<Driver>>() {
            @Override
            public TableRow<Driver> call(TableView<Driver> databaseTableView) {
                return new TableRow<>() {
                    @Override
                    public void updateItem(Driver driver, boolean empty) {
                        super.updateItem(driver, empty);
                        if (driver != null) {
                            if (!driver.testConnection())
                                this.setStyle("-fx-background: rgba(255,0,0,0.67)");
                        }
                    }
                };
            }
        });
    }

    /**
     * Metodo richiamato alla creazione del Controller, imposta un ascoltatore sul {@link #driverTableView} per abilitare dei bottoni solo dopo aver verificato la connessione del {@link Driver} selezionato.
     * <p>
     * I bottoni che vengono abilitati sono:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #databaseLabel}
     *     </li>
     *     <li>
     *         {@link #databaseNameField}
     *     </li>
     *     <li>
     *         {@link #nextButton}
     *     </li>
     * </ul>
     *
     * <p>Richiama infine il metodo {@link #setTableFactory()}</p>
     */
    public void initialize() {
//        this.refresh_RDBMS_table();

        // ascoltatore impostato sulla selezione degli elementi contenuti all'interno della tabella
        this.driverTableView.getSelectionModel().selectedItemProperty().addListener(((observableValue, old_driverConnection, driverConnection_selected) ->
        {
            if (driverConnection_selected != null) {
                if (driverConnection_selected.testConnection()) {
                    this.databaseLabel.setDisable(false);
                    this.databaseNameField.setDisable(false);
                    this.nextButton.setDisable(false);
                }
                this.removeDriverButton.setDisable(false);
            }
            //vengono abilitati gli oggetti per poter proseguire nella creazione del DataBase
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
        this.setMinSize(new WindowSize(610, 440));
    }

    /**
     * Metodo utilizzato per far eseguire {@link #refreshDriverTable()} ogni qualvolta che viene invocato.
     */
    public void Update() {
        this.refreshDriverTable();
    }


}
