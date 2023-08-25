package ArduinoSampler.interfaccia;

import ArduinoSampler.database.Database;
import ArduinoSampler.database.DatabaseList;
import ArduinoSampler.database.Driver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe ExportDatabaseController, utilizzata per gestire le operazioni d'importazione dei {@link Database} tramite interfaccia grafica.
 * <p>Estende {@link DefaultController}</p>
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #add(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #remove(ActionEvent)}
 *     </li>
 *     <li>
 *         {@link #done(ActionEvent)}
 *     </li>
 * </ul>
 */
public class ImportDatabaseFromDriverController extends DefaultController {

    /**
     * Variabile {@link DatabaseList}, contiene tutti i {@link Database} selezionati per l'importazione.
     */
    private DatabaseList databasesSelectedList;

    /**
     * Variabile {@link Button}, identifica il bottone per aggiungere il {@link Database} selezionato al {@link #databasesSelectedList}.
     */
    @FXML
    private Button addButton;

    /**
     * Variabile {@link Button}, identifica il bottone per rimuovere il {@link Database} selezionata dal {@link #databasesSelectedList}.
     */
    @FXML
    private Button removeButton;

    /**
     * Variabile {@link Button}, identifica il bottone per aggiungere i {@link Database}, contenuti all'interno del {@link #databasesSelectedList}, al {@link IndexController#PRIMARY_CONTROLLER}
     */
    @FXML
    private Button doneButton;

    /**
     * Variabile {@link ListView}, identifica la lista grafica nella quale sono situati tutti i {@link Database} utilizzabili.
     */
    @FXML
    private ListView<Database> databaseListView;


    /**
     * Metodo utilizzato per aggiungere il {@link Database} selezionato all'interno della variabile {@link #databasesSelectedList}.
     *
     * @param event evento catturato dal bottone
     */
    @FXML
    public void add(ActionEvent event) {
        this.databasesSelectedList.add(this.databaseListView.getSelectionModel().getSelectedItem());
        this.refreshDatabaseList();
        this.doneButton.setDisable(false);
    }

    /**
     * Metodo utilizzato per rimuovere il {@link Database} selezionato della variabile {@link #databasesSelectedList}.
     *
     * @param event evento catturato dal bottone
     */
    @FXML
    public void remove(ActionEvent event) {
        this.databasesSelectedList.remove(this.databaseListView.getSelectionModel().getSelectedItem());
        this.refreshDatabaseList();
        if (this.databasesSelectedList.isEmpty())
            this.doneButton.setDisable(true);
    }

    /**
     * Metodo utilizzato aggiungere i {@link Database} selezionati all'interno del {@link IndexController#PRIMARY_CONTROLLER}.
     *
     * @param event evento catturato dal bottone
     */
    @FXML
    public void done(ActionEvent event) {
        PRIMARY_CONTROLLER.addDatabaseFromAnotherDatabaseList(this.databasesSelectedList);
        this.exit();
    }

    /**
     * Metodo utilizzato per aggiornare i valori contenuti all'interno della variabile {@link #databaseListView}.
     */
    private void refreshDatabaseList() {
        ImportDatabaseChoiceController controller = (ImportDatabaseChoiceController) this.previous_controller;
        Driver driver = controller.getSelectedDriver();
        DatabaseList databaseList = driver.getAllDatabases();
        final ObservableList<Database> data = FXCollections.observableArrayList(
                databaseList.getDatabases()
        );
        databaseListView.setItems(data);
    }

    /**
     * Metodo utilizzato per far eseguire {@link #refreshDatabaseList()} ogni qualvolta che viene invocato.
     */
    public void Update() {
        this.refreshDatabaseList();
    }

    /**
     * Metodo richiamato alla creazione del Controller, viene utilizzato per:
     * <ul style="margin-top:0px">
     *     <li>
     *         inizializzare la variabile {@link #databasesSelectedList}
     *     </li>
     *     <li>
     *         impostare un ascoltatore su {@link #databaseListView} in modo tale da abilitare o meno i corretti bottoni.
     *     </li>
     *     <li>
     *         impostare il comportamento delle celle, contenute all'interno del {@link #databaseListView}, grazie alla Classe {@link CallBack}
     *     </li>
     * </ul>
     */
    public void initialize() {
        databasesSelectedList = new DatabaseList();
        this.databaseListView.getSelectionModel().selectedItemProperty().addListener(((observableValue, last_database, database_selected) ->
        {
            if (database_selected == null) {
                this.addButton.setDisable(true);
                this.removeButton.setDisable(true);
                return;
            }
            this.addButton.setDisable(PRIMARY_CONTROLLER.getDatabasesUsed().has(database_selected));
            if (this.databasesSelectedList.has(database_selected)) {
                this.addButton.setDisable(true);
                this.removeButton.setDisable(false);
            } else {
                this.removeButton.setDisable(true);
            }
        }));

        // viene impostato il funzionamento delle varie celle
        CallBack callBack = new CallBack();
        callBack.setDatabaseSelected(this.databasesSelectedList);
        this.databaseListView.setCellFactory(callBack);

    }

    /**
     * Metodo utilizzato per impostare lo Stage passato come parametro a questo controller.
     * Imposta anche i valori minimi di dimensione per questa finestra.
     *
     * @param stage nuovo Stage
     */
    public void setStage(Stage stage) {
        super.setStage(stage);
        this.setMinSize(new WindowSize(450, 440));
    }

    /**
     * Metodo utilizzato per la chiusura della finestra corrente tramite la pressione del bottone "<i>Torna indietro</i>".
     * Riapre la finestra delle importazioni.
     * @param event L'evento catturato dal bottone
     */
    @FXML
    public void goBack(ActionEvent event) {
        this.exit();
        this.createNewWindowWithPriority("Importa database", "import_database_choice_manager.fxml", new WindowSize(650, 400));
    }

}


/**
 * Classe CallBack, utilizzata per modificare il comportamento delle celle.
 * <p>
 * Implementa {@link Callback}
 * </p>
 * <p>
 * Questa Classe sovrascrive il metodo {@link Callback#call(Object)} in modo tale da realizzare delle {@link ListCell} personalizzate.
 * </p>
 */
class CallBack implements Callback<ListView<Database>, ListCell<Database>> {

    /**
     * Variabile {@link DatabaseList} al cui interno sono contenuti i {@link Database} precedentemente selezionati nella Classe {@link ImportDatabaseFromDriverController}.
     */
    private DatabaseList databaseListSelected;


    /**
     * Override del metodo {@link Callback#call(Object)}
     *
     * @param databaseListView parametro {@link ListView<Database>}
     * @return {@link ListCell<Database>} utilizzabile da {@link ListView<Database>}
     */
    @Override
    public ListCell<Database> call(ListView<Database> databaseListView) {
        return new listCellDatabase();
    }

    /**
     * Metodo utilizzato per inizializzare {@link #databaseListSelected}.
     *
     * @param databaseList {@link DatabaseList} utilizzato da {@link ImportDatabaseFromDriverController}
     */
    public void setDatabaseSelected(DatabaseList databaseList) {
        this.databaseListSelected = databaseList;
    }

    /**
     * Classe listCellDatabase, utilizzata per gestire il comportamento di ogni singola cella contenuta all'interno di {@link ListCell}.
     * <p>
     * Estende {@link ListCell<Database>}
     * </p>
     */
    class listCellDatabase extends ListCell<Database> {
        /**
         * Override del metodo {@link ListCell#updateItem(Object, boolean)}, esso richiamato ogni volta che è necessario aggiornare la cella.
         *
         * <p>Questo metodo permette la modifica del colore di sfondo appartenente alla cella contenente i {@link Database}.</p>
         *
         * <ul style="margin-top:0px">
         *     <li>
         *         se il {@link Database} è già contenuto all'interno del {@link IndexController#PRIMARY_CONTROLLER}, il colore dello sfondo sarà rosso.
         *     </li>
         *     <li>
         *         se il {@link Database} è gia contenuto all'interno del {@link #databaseListSelected}, il colore dello sfondo sarà verde.
         *     </li>
         * </ul>
         *
         * @param db    database a cui fa riferimento la cella
         * @param empty valore booleano che indica se la cella fa riferimento a un database o meno
         */
        @Override
        public void updateItem(Database db, boolean empty) {
            super.updateItem(db, empty);
            if (db != null) {
                setText(db.getName());
                if (PRIMARY_CONTROLLER.getDatabasesUsed().has(db)) {
                    this.setStyle("-fx-background: rgba(255,0,0,0.67)");
                    return;
                }
                if (databaseListSelected.has(db))
                    this.setStyle("-fx-background: rgba(0,255,0,0.40)");
                else
                    this.setStyle("");
            }
        }
    }

}

