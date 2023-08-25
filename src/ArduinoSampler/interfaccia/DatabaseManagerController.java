package ArduinoSampler.interfaccia;

import ArduinoSampler.database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe DatabaseManagerController, utilizzata per gestire le operazioni dei {@link Database} tramite interfaccia grafica.
 * <p>Estende {@link DefaultController}</p>
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #addDatabase(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #importDatabase(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #exportDatabase(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #removeDatabase(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #deleteDatabase(ActionEvent)}
 *     </li>
 * </ul>
 */
public class DatabaseManagerController extends DefaultController {



    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Definizioni generali per il funzionamento dell'interfaccia grafica
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * Variabile {@link TableColumn}, utilizzata per immagazzinare il nome di ogni {@link Database} aggiunto.
     */
    @FXML
    private TableColumn<Database, String> databaseNameColumn;

    /**
     * Variabile {@link TableColumn}, utilizzata per immagazzinare l'indirizzo di ogni {@link Database} aggiunto.
     */
    @FXML
    private TableColumn<Database, String> databaseUrlColumn;
    /**
     * Variabile {@link TableColumn}, utilizzata per immagazzinare il nome dei vari sistemi di gestione dei database.
     */
    @FXML
    private TableColumn<Database, String> RDBMSColumn;

    /**
     * Variabile {@link TableView}, utilizzata per immagazzinare le informazioni principali dei {@link Database} aggiunti.
     */
    @FXML
    private TableView<Database> databaseTableView;

    /**
     * Variabile {@link Button}, identifica il bottone corrispondente all'esportazione del {@link Database}.
     */
    @FXML
    private Button exportDatabaseBtn;
    /**
     * Variabile {@link Button}, identifica il bottone corrispondente alla rimozione del {@link Database}.
     */
    @FXML
    private Button removeDatabaseBtn;
    /**
     * Variabile {@link Button}, identifica il bottone corrispondente all'eliminazione del {@link Database}.
     */
    @FXML
    private Button deleteDatabaseBtn;


    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Fine definizioni generali per il funzionamento dell'interfaccia grafica
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * Metodo utilizzato per la creazione della finestra che si occupa dell'aggiunta dei vari {@link Database}.
     *
     * @param event L'evento catturato dal bottone
     */

    @FXML
    public void addDatabase(ActionEvent event) {
        this.createNewWindowWithPriority("Seleziona sistema di gestione", "RDBMS_manager_interface.fxml", new WindowSize(600, 400));
    }

    /**
     * Metodo utilizzato per la creazione della finestra che si occupa dell'importazione dei {@link Database}.
     *
     * @param event L'evento catturato dal bottone
     */
    @FXML
    public void importDatabase(ActionEvent event) {
        this.createNewWindowWithPriority("Importa database", "import_database_choice_manager.fxml", new WindowSize(650, 400));
    }

    /**
     * Metodo utilizzato per la creazione della finestra che si occupa dell'esportazione' dei vari {@link Database}.
     *
     * @param event L'evento catturato dal bottone
     */
    @FXML
    public void exportDatabase(ActionEvent event) {
        ExportDatabaseController controller = (ExportDatabaseController) this.createNewWindowWithPriority("Esporta database", "export_database_manager.fxml", new WindowSize(450, 250));
        controller.setDatabase(this.databaseTableView.getSelectionModel().getSelectedItem());
    }

    /**
     * Metodo utilizzato per la creazione della finestra che si occupa della rimozione dei vari {@link Database} selezionati.
     *
     * @param event L'evento catturato dal bottone
     */
    @FXML
    public void removeDatabase(ActionEvent event) {
        PRIMARY_CONTROLLER.removeDatabase(this.databaseTableView.getSelectionModel().getSelectedItem());
        this.Update();
    }

    /**
     * Metodo utilizzato per la creazione della finestra che si occupa della cancellazione dei {@link Database} selezionati.
     * <p>
     * <i>Chiede la conferma di cancellazione</i>
     * </p>
     *
     * @param event L'evento catturato dal bottone
     */
    @FXML
    public void deleteDatabase(ActionEvent event) {
        DialogController controller = (DialogController) this.createNewWindowWithPriority("Eliminazione database", "dialog.fxml", new WindowSize(300, 100));
        EventHandler<ActionEvent> action = actionEvent -> {
            Database database = databaseTableView.getSelectionModel().getSelectedItem();
            if (database.delete()) {
                PRIMARY_CONTROLLER.removeDatabase(database);
                Update();
                controller.exit();
            }
        };
        controller.setWarningLabel("Attenzione!\nStai per eliminare definitivamente questo database!");
        controller.setProceedButtonAction(action);
    }


    /**
     * Metodo utilizzato per ricaricare la {@link #databaseTableView} con i dati riguardanti i {@link Database} utilizzati dal {@link IndexController#PRIMARY_CONTROLLER}
     */
    private void refreshDatabaseList() {
        final ObservableList<Database> data = FXCollections.observableArrayList(
                PRIMARY_CONTROLLER.getDatabasesUsed().getDatabases()
        );
        databaseTableView.setItems(data);
    }

    /**
     * Metodo utilizzato per impostare il comportamento delle varie celle contenute nel {@link #databaseTableView}.<br>
     * Fa riferimento alle colonne:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #databaseNameColumn}
     *     </li>
     *     <li>
     *         {@link #databaseUrlColumn}
     *     </li>
     *     <li>
     *         {@link #RDBMSColumn}
     *     </li>
     * </ul>
     */
    private void setTableFactory() {
        databaseNameColumn.setCellValueFactory(new PropertyValueFactory<Database, String>("name")); //fa riferimento al getname()
        /*
        Per usare il metodo 'setCellValueFactory' è necessario che la classe a cui fa riferimento il
        'PropertyValueFactory' (Database) possieda un getter visto che le sue variabili interne sono di tipo privato
         */
        databaseUrlColumn.setCellValueFactory(new PropertyValueFactory<Database, String>("DATABASE_URL")); //fa riferimento al getDATABASE_URL()
        RDBMSColumn.setCellValueFactory(new PropertyValueFactory<Database, String>("RDBMS")); //fa riferimento al getRDBMS()
        // viene impostato il funzionamento di ogni riga all'interno della tabella
        databaseTableView.setRowFactory(new Callback<TableView<Database>, TableRow<Database>>() {
            @Override
            public TableRow<Database> call(TableView<Database> databaseTableView) {
                return new TableRow<>() {
                    @Override
                    public void updateItem(Database db, boolean empty) {
                        super.updateItem(db, empty);
                        if (db != null && !empty)
                            if (!db.testConnection())
                                this.setStyle("-fx-background: rgba(255,0,0,0.67)");
                    }
                };
            }
        });
    }

    /**
     * Metodo richiamato alla creazione del Controller, imposta un ascoltatore su {@link #databaseTableView} in modo tale da abilitare o meno i corretti bottoni.
     */
    public void initialize() {
        this.databaseTableView.getSelectionModel().selectedItemProperty().addListener(((observableValue, last_database, database_selected) ->
        {
            if (database_selected != null) { // condizione necessaria quando vengono eliminati tutti i driver
                if (observableValue.getValue().testConnection()) {
                    this.exportDatabaseBtn.setDisable(false);
                    this.deleteDatabaseBtn.setDisable(false);
                }
                // consento l'utilizzo solo dei comandi utilizzabili senza interrogazione al RDBMS
                this.removeDatabaseBtn.setDisable(false);
            } else {
                this.exportDatabaseBtn.setDisable(true);
                this.removeDatabaseBtn.setDisable(true);
                this.deleteDatabaseBtn.setDisable(true);
            }
        }));
        // vengono impostate le caratteristiche delle varie colonne all'interno della tabella
        this.setTableFactory();
        this.refreshDatabaseList();
    }

    /**
     * Metodo utilizzato per impostare lo Stage passato come parametro a questo controller.
     * Imposta anche i valori minimi di dimensione per questa finestra.
     * @param stage nuovo Stage
     */
    public void setStage(Stage stage)
    {
        super.setStage(stage);
        this.setMinSize(new WindowSize(585, 450));
    }

    /**
     * Metodo utilizzato per far eseguire {@link #refreshDatabaseList()} ogni qualvolta che viene invocato.
     */
    public void Update() {
        this.refreshDatabaseList();
    }
}
