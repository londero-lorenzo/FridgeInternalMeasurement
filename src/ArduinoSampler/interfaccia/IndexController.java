package ArduinoSampler.interfaccia;

import ArduinoSampler.arduino.DataCollector;
import ArduinoSampler.arduino.Server;
import ArduinoSampler.arduino.ServerList;
import ArduinoSampler.arduino.TimerController;
import ArduinoSampler.database.Database;
import ArduinoSampler.database.DatabaseList;
import ArduinoSampler.database.Driver;
import ArduinoSampler.database.DriverList;
import ArduinoSampler.file_managers.DataArchiver;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Objects;

/**
 * Classe IndexController, classe principale dell'intero progetto.
 * <p>
 * Questo controller ha il compito di gestire tutte le relazioni tra i vari sotto Controller.
 * </p>
 * <p>
 * Variabili principali:
 * </p>
 *     <ul style="margin-top: 0px">
 *         <li>
 *             {@link DataArchiver}
 *         </li>
 *         <li>
 *             {@link DatabaseList}
 *         </li>
 *         <li>
 *             {@link DriverList}
 *         </li>
 *         <li>
 *             {@link ServerList}
 *         </li>
 *         <li>
 *             {@link TimerController}
 *         </li>
 *         <li>
 *             {@link Logger}
 *         </li>
 *    </ul>
 *
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #setStage(Stage)}
 *     </li>
 *     <li>
 *         {@link #loadInterface()}
 *     </li>
 *     <li>
 *         {@link #addDatabase(Database)}
 *     </li>
 *     <li>
 *         {@link #removeDatabase(Database)}
 *     </li>
 *     <li>
 *         {@link #addDriver(Driver)}
 *     </li>
 *     <li>
 *         {@link #removeDriver(Driver)}
 *     </li>
 *     <li>
 *         {@link #removeSerialSelected()}
 *     </li>
 *     <li>
 *         {@link #setDriverDataArchiver(DataArchiver)}
 *     </li>
 *     <li>
 *         {@link #setDatabaseDataArchiver(DataArchiver)}
 *     </li>
 *     <li>
 *         {@link #refreshChart()}
 *     </li>
 *     <li>
 *         {@link #getDatabasesUsed()}
 *     </li>
 *     <li>
 *         {@link #getDriversUsed()}
 *     </li>
 *     <li>
 *         {@link #getDatabaseSelected()}
 *     </li>
 *     <li>
 *         {@link #getSerialSelected()}
 *     </li>
 *     <li>
 *         {@link #getLogger()}
 *     </li>
 *     <li>
 *         {@link #start()}
 *     </li>
 *     <li>
 *         {@link #stop()}
 *     </li>
 * </ul>
 */

public class IndexController {

    /**
     * Variabile statica che fa riferimento alla Classe {@link IndexController}, viene inizializzata facendo riferimento a questa classe.
     * <p>Viene richiamata ogni qual'volta si debbano modificare eventuali variabili all'interno della Classe {@link IndexController}.</p>
     */

    public static IndexController PRIMARY_CONTROLLER;

    /**
     * Variabile {@link DatabaseList}, utilizzata per memorizzare i {@link Database} utilizzabili.
     */
    private DatabaseList databasesUsed;

    /**
     * Variabile {@link DriverList}, utilizzata per memorizzare i {@link Driver} utilizzabili da parte dei {@link Database} immagazzinati all'interno di {@link #databasesUsed}.
     */
    private DriverList driversUsed;

    /**
     * Variabile {@link ServerList}, utilizzata per memorizzare i {@link Server} utilizzabili.
     */
    private ServerList serverList;

    /**
     * Variabile {@link TimerController} utilizzata per svolgere l'operazione di campionamento.
     */
    private TimerController timerController;

    /**
     * Variabile {@link Logger}, utilizzata per stampare sulla console i vari messaggi che necessitano di essere letti dall'utente.
     */
    private Logger logger;

    /**
     * Variabile {@link ChartFeature}, utilizzata per memorizzare le caratteristiche e il comportamento del grafico principale {@link #chart}.
     */
    private ChartFeature chartFeature;

    /**
     * Variabile {@link Stage} che identifica la finestra della finestra corrente.
     */
    private Stage stage;


    // variabile per interrompere il thread per determinare le interfaccie
    /**
     * Variabile booleana utilizzata per interrompere, in caso di chiusura della finestra, il thread contenuto nel metodo {@link #loadInterface()}
     *
     * @see #setStage(Stage)
     * @see #exit()
     */
    private boolean isOpened = false;

    /**
     * Variabile booleana utilizzata per verificare se il programma sta svolgendo l'operazione di campionamento.
     */
    private boolean isSampling = false;

    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Definizioni generali per il funzionamento dell'interfaccia grafica
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * Variabile {@link Button} che identifica il bottone per aprire l'interfaccia di gestione dei {@link Database}.
     */
    @FXML
    private Button databaseManagerButton;

    /**
     * Variabile {@link Button} che identifica il bottone necessario per aggiornare le {@link Server}.
     */
    @FXML
    private Button addNewServerButton;

    /**
     * Variabile {@link Button} che identifica il bottone per aprire l'interfaccia {@link SamplingSettingsController}.
     */
    @FXML
    private Button samplingSettings;

    /**
     * Variabile {@link ComboBox<Database>}, utilizzata per immagazzinare e selezionare i {@link Database} da utilizzare nel campionamento.
     */
    @FXML
    private ComboBox<Database> databaseSelector;

    /**
     * Variabile {@link ComboBox<   Server   >}, utilizzata per immagazzinare e selezionare i {@link Server} da utilizzare nel campionamento.
     */
    @FXML
    private ComboBox<Server> interfaceSelector;

    /**
     * Variabile {@link TextField} utilizzata per indicare la frequenza di campionamento.
     */
    @FXML
    private TextField samplingIndicator;

    /**
     * Variabile {@link Button} che identifica il bottone necessario ad avviare il campionamento.
     */
    @FXML
    private Button startButton;


    /**
     * Variabile {@link Button} che identifica il bottone necessaria a fermare il campionamento.
     */
    @FXML
    private Button stopButton;

    /**
     * Variabile {@link TextField} utilizzata per indicare il tempo trascorso.
     */
    @FXML
    private TextField timePassedIndicator;

    /**
     * Variabile {@link LineChart} che identifica il grafico su cui inserire i dati appena raccolti.
     */
    @FXML
    private LineChart<String, Float> chart;


    @FXML
    private Axis<String> time_axis;

    /**
     * Variabile {@link TextField} utilizzata per indicare la temperatura dell'ultima misurazione.
     */
    @FXML
    private TextField temperatureIndicator;

    /**
     * Variabile {@link TextField} utilizzata per indicare la temperatura media rispetto a tutti i campioni raccolti.
     */
    @FXML
    private TextField averageTemperatureIndicator;

    @FXML
    private TextField humidityIndicator;

    @FXML
    private TextField averageHumidityIndicator;

    /**
     * Variabile {@link TextArea} utilizzata per stampare degli avvisi durante l'utilizzo del programma.
     */
    @FXML
    private TextArea consoleArea;



    /*
    --------------------------------------------------------------------------------------------------------------------------------------------------
    Fine definizioni generali per il funzionamento dell'interfaccia grafica
    --------------------------------------------------------------------------------------------------------------------------------------------------
     */


    /**
     * Metodo richiamato alla creazione del Controller, si occupa d'inizializzare:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #PRIMARY_CONTROLLER}
     *     </li>
     *     <li>
     *         {@link #databasesUsed}
     *     </li>
     *     <li>
     *         {@link #driversUsed}
     *     </li>
     *     <li>
     *         {@link #serverList}
     *     </li>
     *     <li>
     *         {@link #timerController}
     *     </li>
     *     <li>
     *         {@link #logger}
     *     </li>
     *     <li>
     *         {@link #chartFeature}
     *     </li>
     *     <li>
     *         il comportamento delle celle del ComboBox {@link #databaseSelector}
     *     </li>
     * </ul>
     *
     * @see DatabaseList
     * @see DriverList
     * @see ServerList
     * @see TimerController
     * @see Logger
     * @see ChartFeature
     * @see #setDatabaseSelectorFactory()
     */
    public void initialize() {
        if (PRIMARY_CONTROLLER == null)
            PRIMARY_CONTROLLER = this;
        else
            System.exit(1);
        this.databasesUsed = new DatabaseList();
        this.driversUsed = new DriverList();
        this.serverList = new ServerList();
        this.timerController = new TimerController();
        this.logger = new Logger(this.consoleArea);
        this.chartFeature = new ChartFeature(900, 10, 50, 2);
        this.setDatabaseSelectorFactory();
    }

    /**
     * Metodo utilizzato per inizializzare lo {@link Stage} della finestra.
     * <p>Si occupa di:</p>
     * <ul style="margin-top:0px">
     *     <li>
     *         impostare l'azione alla chiusura della finestra
     *     </li>
     *     <li>
     *         di richiamare il metodo {@link #loadInterface()}
     *     </li>
     *     <li>
     *         di inizializzare la variabile {@link #isOpened} con valore {@code true}
     *     </li>
     * </ul>
     *
     * @param stage {@link Stage} della finestra corrente
     * @see #exit()
     * @see #loadInterface()
     */
    public void setStage(Stage stage) {
        this.isOpened = true;
        this.stage = stage;
        this.stage.setOnHidden(e -> this.exit());
        this.loadInterface();
        ChangeListener<Number> stageSizeListener = (observableValue, oldValue, newValue) -> {
            if (!this.isSampling)
                this.refreshChart();
        };
        this.stage.widthProperty().addListener(stageSizeListener);
        this.stage.heightProperty().addListener(stageSizeListener);
        this.stage.setMinWidth(750);
        this.stage.setMinHeight(450);
    }

    /**
     * Metodo utilizzato per realizzare un {@link Thread} che si occupi della scansione delle {@link Server} disponibili.
     * <p>
     * Ad ogni scansione vengono rimosse tutte le {@link Server} all'interno di {@link #serverList}, per poi tornare ad aggiungere tutti quelle {@link Server} identificate.
     * </p>
     * <p>
     * Alla fine dell'identificazione vengono richiamati i seguenti metodi:
     * </p>
     *     <ul style="margin-top:0px">
     *         <li>
     *             {@link #refreshSerialPortsSelector()}
     *         </li>
     *         <li>
     *             {@link #refreshSamplingIndicator()}
     *         </li>
     *     </ul>
     * <p>
     *     Subito dopo l'avvio del {@link Thread}, viene richiamato il metodo {@link #setSerialPortsSelectorFactory()}
     * </p>
     *
     * @see ServerList
     * @see Server
     * @see #refreshSerialPortsSelector()
     * @see #refreshSamplingIndicator()
     * @see #setSerialPortsSelectorFactory()
     */

    public void loadInterface() {
        /*
        Runnable scanInterfaceThread = () -> {
            this.startButton.setDisable(true);
            this.addNewServerButton.setDisable(true);
            this.logger.write("-------------------Identificazione interfacce--------------------------");
            this.serialUsed.close_all();
            this.serialUsed.remove_all();
            for (SerialPort port : SerialPort.getCommPorts()) {
                Esp32_Server serialPort = new Esp32_Server(port.getSystemPortName(), (port.getPortDescription().contains(" (COM")) ? port.getPortDescription().substring(0, port.getPortDescription().indexOf(" (COM")) : port.getPortDescription());
                if (!this.isOpened)
                    return;
                if (serialPort.open()) {
                    this.serialUsed.add(serialPort);
                }
            }
            this.logger.write("-----------------Fine identificazione interfacce-----------------------");
            this.interfaceSelector.getSelectionModel().selectedItemProperty().addListener((options, old_serial, new_serial) -> {
                this.refreshSamplingIndicator();
                if (old_serial != null && new_serial != null)
                    old_serial.close();
                if (new_serial != null) {
                    new_serial.open(false);
                    Runtime.getRuntime().addShutdownHook(new Thread(new_serial::close));
                }
            });
            Platform.runLater(() -> {
                this.refreshSerialPortsSelector();
                this.refreshSamplingIndicator();
                if (this.interfaceSelector.getValue() != null) // se non è selezionato nessun'interfaccia seriale
                    this.interfaceSelector.getValue().open(false);

                if (this.serialUsed.getSerials().length > 0)
                    this.startButton.setDisable(false);
                this.addNewServerButton.setDisable(false);
            });
        };
        Thread run = new Thread(scanInterfaceThread);
        run.start();
        this.setSerialPortsSelectorFactory();
         */
    }


    /**
     * Metodo utilizzato per richiamare il metodo {@link #loadInterface()} tramite pressione del {@link #addNewServerButton}.
     */
    @FXML
    public void openServerCommunicationInterface(ActionEvent event) {
        //this.loadInterface();


        Stage current_stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Stage database_manager_stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("addServer_interface.fxml")));
        Parent serverInterfaceAdder_StageRoot = null;
        try {
            serverInterfaceAdder_StageRoot = loader.load();
        } catch (IOException e) {
            this.logger.writeOnConsole(e.getMessage());
            Runtime.getRuntime().exit(1);
        }
        AddServerWindowController controller = loader.getController();
        controller.setStage(database_manager_stage);
        database_manager_stage.setTitle("Inserimento nuovo server");
        database_manager_stage.setScene(new Scene(serverInterfaceAdder_StageRoot, 400, 200));
        database_manager_stage.initModality(Modality.WINDOW_MODAL);
        database_manager_stage.initOwner(current_stage);
        database_manager_stage.show();
    }

    /**
     * Metodo utilizzato per avviare l'interfaccia grafica di gestione dei database.
     *
     * @param event evento catturato dal bottone
     */
    @FXML
    public void openDatabaseManagerInterface(ActionEvent event) {
        Stage current_stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        // Memorizzo nella variabile 'current_stage' la finestra nella quale si trova il bottone che è stato cliccato

        Stage database_manager_stage = new Stage();
        // Creo un nuovo stage

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("database_manager_interface.fxml")));
        // Definisco la classe FXMLLoader, la quale ha il compito di caricare il documento XML

        Parent database_manager_stage_root = null;
        try {
            database_manager_stage_root = loader.load();
        } catch (IOException e) {
            this.logger.write(e.getMessage());
            Runtime.getRuntime().exit(1);
        }
        // Carico l'interfaccia di gestione dei database nella variabile 'database_manager_stage_root'

        DatabaseManagerController controller = loader.getController();
        // Estrapolo il controller per avere verso di lui un riferimento

        controller.setStage(database_manager_stage);
        // Assegno al controller lo Stage

        database_manager_stage.setTitle("Gestione database");
        // Assegno il titolo alla nuova scheda di dialogo

        database_manager_stage.setScene(new Scene(database_manager_stage_root, 600, 400));
        // Imposto una nuova scena con le dimensione adeguate

        database_manager_stage.initModality(Modality.WINDOW_MODAL);
        // Imposto la finestra della gestione dei database in modalità modale, così facendo blocco l'input alle altre finestre aperte

        database_manager_stage.initOwner(current_stage);
        // Imposto come genitore tale finestra, così facendo avrà la priorità

        database_manager_stage.show();
        // Mostro la finestra di gestione dei database
    }

    /**
     * Metodo utilizzato per avviare l'interfaccia grafica di gestione del campionamento.
     *
     * @param event evento catturato dal bottone
     */
    @FXML
    public void openSamplingSettingsInterface(ActionEvent event) {
        Stage current_stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Stage database_manager_stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("sampling_settings_interface.fxml")));
        Parent database_manager_stage_root = null;
        try {
            database_manager_stage_root = loader.load();
        } catch (IOException e) {
            this.logger.write(e.getMessage());
            Runtime.getRuntime().exit(1);
        }
        SamplingSettingsController controller = loader.getController();
        controller.setStage(database_manager_stage);
        database_manager_stage.setTitle("Settaggio velocità di campionamento");
        database_manager_stage.setScene(new Scene(database_manager_stage_root, 400, 200));
        database_manager_stage.initModality(Modality.WINDOW_MODAL);
        database_manager_stage.initOwner(current_stage);
        database_manager_stage.show();
    }

    /**
     * Metodo utilizzato per aggiungere {@link Database} all'interno di {@link #databasesUsed}.
     * <p>
     * Dopo aver aggiunto il {@link Database} vengono richiamati i metodi:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link DatabaseList#save()}
     *     </li>
     *     <li>
     *         {@link #refreshDatabaseSelector()}
     *     </li>
     * </ul>
     *
     * @param database {@link Database} da aggiungere
     */
    public void addDatabase(Database database) {
        this.databasesUsed.add(database);
        this.databasesUsed.save();
        this.refreshDatabaseSelector();
    }

    public void addServer(Server server){
        this.serverList.add(server);
    }


    /**
     * Metodo utilizzato per aggiungere {@link Database} all'interno di {@link #databasesUsed} tramite {@link DatabaseList}.
     * <p>
     * Dopo aver aggiunto i {@link Database} vengono richiamati i metodi:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link DatabaseList#save()}
     *     </li>
     *     <li>
     *         {@link #refreshDatabaseSelector()}
     *     </li>
     * </ul>
     *
     * @param databaseList {@link DatabaseList} da cui prelevare i {@link Database}
     */
    public void addDatabaseFromAnotherDatabaseList(DatabaseList databaseList) {
        for (Database db : databaseList.getDatabases())
            this.databasesUsed.add(db);
        this.databasesUsed.save();
        this.refreshDatabaseSelector();
    }

    /**
     * Metodo utilizzato per rimuovere {@link Database} da {@link #databasesUsed}.
     * <p>
     * Dopo aver rimosso il {@link Database} vengono richiamati i metodi:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link DatabaseList#save()}
     *     </li>
     *     <li>
     *         {@link #refreshDatabaseSelector()}
     *     </li>
     * </ul>
     *
     * @param database {@link Database} da rimuovere
     */
    public void removeDatabase(Database database) {
        this.databasesUsed.remove(database);
        this.databasesUsed.save();
        this.refreshDatabaseSelector();
    }


    /**
     * Metodo utilizzato per rimuovere {@link Database} da {@link #databasesUsed} tramite l'utilizzo del metodo {@link DatabaseList#removeFromJDBC_URL(String)}.
     * <p>
     * Dopo aver rimosso il {@link Database} vengono richiamati i metodi:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link DatabaseList#save()}
     *     </li>
     *     <li>
     *         {@link #refreshDatabaseSelector()}
     *     </li>
     * </ul>
     *
     * @param JDBC_URL stringa contenente l'indirizzo JDBC del {@link Database} da rimuovere
     */
    public void removeDatabasesFromJDBCURL(String JDBC_URL) {
        this.databasesUsed.removeFromJDBC_URL(JDBC_URL);
        this.databasesUsed.save();
        this.refreshDatabaseSelector();
        //TODO: controllare se ci sono driver inutilizzati (unused_drivers)
    }


    /**
     * Metodo utilizzato per aggiungere {@link Driver} all'interno di {@link #driversUsed}.
     * <p>
     * Dopo aver aggiunto il {@link Driver} viene richiamato il metodo:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link DriverList#save()}
     *     </li>
     * </ul>
     *
     * @param driver {@link Driver} da aggiungere
     */
    public void addDriver(Driver driver) {
        this.driversUsed.add(driver);
        this.driversUsed.save();
    }

    /**
     * Metodo utilizzato per rimuovere {@link Driver} da {@link #driversUsed}.
     * <p>
     * Dopo aver rimosso il {@link Driver} viene richiamato il metodo:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link DriverList#save()}
     *     </li>
     * </ul>
     *
     * @param driver {@link Driver} da rimuovere
     */
    public void removeDriver(Driver driver) {
        this.driversUsed.remove(driver);
        this.driversUsed.save();
    }


    /**
     * Metodo utilizzato per rimuovere {@link Server} corrente da {@link #serverList}.
     * <p>
     * Dopo aver rimosso il {@link Driver} viene richiamato il metodo:
     * </p>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #refreshSerialPortsSelector()}
     *     </li>
     * </ul>
     */
    public void removeSerialSelected() {
        this.serverList.remove(this.getSerialSelected());
        this.refreshSerialPortsSelector();
    }

    /**
     * Metodo utilizzato per impostare il {@link DataArchiver} a {@link #driversUsed} e successivamente per ottenere i {@link Driver} dal file di salvataggio.
     *
     * @param data_archiver {@link DataArchiver} che fa riferimento al file di salvataggio riferito ai {@link Driver}
     */
    public void setDriverDataArchiver(DataArchiver data_archiver) {
        this.driversUsed.setDriverArchiver(data_archiver); //non è inseribile nel initialize() in quanto il metodo
        // setDriver_data_archiver viene richiamato subito dopo e di conseguenza farei riferimento a un oggetto null
        this.driversUsed.getDriversFromDataArchiver();
    }

    /**
     * Metodo utilizzato per impostare il {@link DataArchiver} a {@link #databasesUsed} e successivamente per ottenere i {@link Database} dal file di salvataggio.
     *
     * @param data_archiver {@link DataArchiver} che fa riferimento al file di salvataggio riferito ai {@link Driver}
     */
    public void setDatabaseDataArchiver(DataArchiver data_archiver) {
        this.databasesUsed.setDataArchiver(data_archiver); //non è inseribile nel initialize() in quanto il metodo
        // setDriver_data_archiver viene richiamato subito dopo e di conseguenza farei riferimento a un oggetto null
        this.databasesUsed.getDatabasesFromDataArchiver();
        //viene assegnato il driver a ogni database corrispondente
        for (Database db : this.databasesUsed.getDatabases()) {
            db.setDriver(this.driversUsed.getDriverByJDBC_URL(db.getJDBC_URL()));
        }
        this.refreshDatabaseSelector();
//        System.out.println(this.databases_used);
    }

    /**
     * Metodo utilizzato per modificare il comportamento delle celle di {@link #interfaceSelector}.
     * <p>
     * Questa Classe sovrascrive il metodo {@link Callback#call(Object)} in modo tale da realizzare delle {@link ListCell} personalizzate.
     * </p>
     *
     * <p>Comportamento celle se sono stati trovati dispositivi:</p>
     *     <ul style="margin-top:0px">
     *         <li>
     *             far apparire il nome del dispositivo hardware nel menù a tendina
     *         </li>
     *     </ul>
     * <p>Comportamento celle se non sono stati trovati dispositivi:</p>
     *     <ul style="margin-top:0px">
     *         <li>
     *             mostrare il testo "Nessun'interfaccia disponibile" nel bottone del {@link ComboBox}
     *         </li>
     *         <li>
     *             impedire l'avvio del campionamento disabilitando {@link #startButton}
     *         </li>
     *     </ul>
     */
    public void setSerialPortsSelectorFactory() {

        // gestisce le celle di selezione
        this.interfaceSelector.setCellFactory(new Callback<ListView<Server>, ListCell<Server>>() {
            @Override
            public ListCell<Server> call(ListView<Server> SerialListView) {
                return new ListCell<Server>() {
                    @Override
                    public void updateItem(Server db, boolean empty) {
                        super.updateItem(db, empty);
                        if (db != null)
                            setText(db.getDeviceName());
                    }
                };
            }
        });

        // gestisce il PromptText (il testo nella cella per l'apertura del ComboBox)
        this.interfaceSelector.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(Server serial, boolean empty) {
                super.updateItem(serial, empty);
                if (serial == null || empty) {
                    setText("Nessun server disponibile");
                    startButton.setDisable(true);
                    samplingSettings.setDisable(true);
                } else {
                    setText(serial.getDeviceName());
                    samplingSettings.setDisable(false);
                    if (getDatabaseSelected() != null)
                        startButton.setDisable(false);
                }
            }
        });
    }

    /**
     * Metodo utilizzato per modificare il comportamento delle celle di {@link #databaseSelector}.
     * <p>
     * Questa Classe sovrascrive il metodo {@link Callback#call(Object)} in modo tale da realizzare delle {@link ListCell} personalizzate.
     * </p>
     *
     * <p>Comportamento celle se sono stati trovati {@link Database} utilizzabili:</p>
     *     <ul style="margin-top:0px">
     *         <li>
     *             mostrare il nome del {@link Database} con specificazione del sistema di gestione se necessario
     *         </li>
     *     </ul>
     * <p>Comportamento celle se non sono stati trovati dispositivi:</p>
     *     <ul style="margin-top:0px">
     *         <li>
     *             mostrare il testo "Nessun {@link Database} disponibile" nel bottone del {@link ComboBox}
     *         </li>
     *         <li>
     *             impedire l'avvio del campionamento disabilitando {@link #startButton}
     *         </li>
     *     </ul>
     */
    public void setDatabaseSelectorFactory() {
        // gestisce le celle di selezione
        this.databaseSelector.setCellFactory(new Callback<ListView<Database>, ListCell<Database>>() {
            @Override
            public ListCell<Database> call(ListView<Database> databaseListView) {
                return new ListCell<Database>() {
                    @Override
                    public void updateItem(Database db, boolean empty) {
                        super.updateItem(db, empty);
                        if (db != null) {
                            int same_name_database = 0;

                            // vengono contati i database selezionati con lo stesso nome
                            for (String name : databasesUsed.getDatabasesNames()) {
                                if (Objects.equals(name, db.getName()))
                                    same_name_database++;
                            }
                            // se {same_name_database == 1} allora esiste solo un database, altrimenti esistono altri database in altri RDBMS
                            setText((same_name_database == 1) ? db.getName() : db.getName() + " [" + db.getRDBMS() + "]");
                        }
                    }
                };
            }
        });

        // gestisce il PromptText (il testo nella cella per l'apertura del ComboBox)
        this.databaseSelector.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(Database db, boolean empty) {
                super.updateItem(db, empty);
                if (db == null || empty) {
                    setText("Nessun database disponibile");
                    startButton.setDisable(true);
                } else {
//                    System.out.println(db.getName());
                    setText(db.getName());
                    if (getSerialSelected() != null)
                        startButton.setDisable(false);
                }

            }
        });
    }

    /**
     * Metodo utilizzato per aggiornare i dati all'interno di {@link #databaseSelector}.
     */
    public void refreshDatabaseSelector() {
        final ObservableList<Database> data = FXCollections.observableArrayList(
                this.getDatabasesUsed().getReachableDatabases()
        );

        this.databaseSelector.setItems(data);
        if (this.databaseSelector.getValue() == null)
            this.databaseSelector.getSelectionModel().select(0);
    }

    /**
     * Metodo utilizzato per aggiornare i dati all'interno di {@link #serverList}.
     */
    public void refreshSerialPortsSelector() {
        final ObservableList<Server> data = FXCollections.observableArrayList(
                this.serverList.getSerials()
        );

        this.interfaceSelector.setItems(data);
        if (this.interfaceSelector.getValue() == null)
            this.interfaceSelector.getSelectionModel().select(0);
    }

    /**
     * Metodo utilizzato per aggiornare la frequenza nel campo {@link #samplingIndicator}.
     */
    public void refreshSamplingIndicator() {
        if (this.getSerialSelected() != null) // se non è selezionato nessun'interfaccia seriale
            this.samplingIndicator.setText(this.getSerialSelected().getSamplingSettings().getFrequency().toString());
    }


    public void refreshTimePassedIndicator(String time) {
        this.timePassedIndicator.setText(time);
    }

    /**
     * Metodo utilizzato per mostrare i dati raccolti sul grafico.
     * <p>
     * Il numero di punti mostrati varia in base alla larghezza del grafico, in funzione dei parametri inseriti in {@link #chartFeature}.
     * </p>
     *
     * <p>es.</p>
     * <pre>
     * Larghezza di default della finestra: 900px
     * Numero di punti presenti in 900px: 10
     * Ogni quanti pixel bisogna aggiungere nuovi punti: 50px
     * Numero di punti punti da aggiungere: 2
     *
     * <i>(...aumenta la dimensione del grafico di 50 pixel...)</i>
     *
     * differenza delle larghezze dei grafici (diff): 950 - 900 = 50px
     * moltiplicatore_punti (k) = (diff)/50px = 50px/50px = 1
     * punti extra (en)= k * 2 = 2
     * nuovi punti = 10 + en = 12
     * </pre>
     */
    public void refreshChart() {
        if (this.getSerialSelected() == null)
            return;
        this.chart.getData().clear();
        String temperature_column_name = this.getDatabaseSelected().getDataStructure().getTemperatureColumnName();
        String humidity_column_name = this.getDatabaseSelected().getDataStructure().getHumidityColumnName();
        DataCollector last_collector = this.getSerialSelected().getDataCollectorList().getLastDataCollector();

        if (last_collector == null)
            return;
        long last_time = last_collector.getCollectionTime();
        this.temperatureIndicator.setText(last_collector.getData(temperature_column_name).toString());
        this.humidityIndicator.setText(last_collector.getData(temperature_column_name).toString());

        XYChart.Series<String, Float> seriesTemperature = new XYChart.Series<>();
        seriesTemperature.setName("Temperatura");

        XYChart.Series<String, Float> seriesHumidity = new XYChart.Series<>();
        seriesHumidity.setName("Umidità");

        this.time_axis.setLabel("Tempo [" + this.getSerialSelected().getSamplingSettings().getTimeIdentifier() + "]");
        /*
        900px  -> 10 punti
        950px  -> 12 punti diff(50)
        1000px -> 14 punti diff(100)
        1050px -> 16 punti diff(150)

         */
        float diff = (float) this.stage.getWidth() - this.chartFeature.widthInPixelsOfChart();  // 950 - 900 = 50;
        float div = diff / this.chartFeature.howManyPixelsYouNeedToAddNewPoints();     // 50/50 = 1;
        int k = (int) Math.ceil(div); // ceil(1) = 1;
        int noExtraPoints = k * this.chartFeature.numberOfPointsToAdd(); //1 * 2 = 2;

        for (int i = 0; i < this.chartFeature.numberOfPoints_for_widthInPixelsOfChart() + noExtraPoints; i++) {
            DataCollector collector = this.getSerialSelected().getDataCollectorList().getDataCollectorFromEnd(i);
            if (collector != null) {
                float temperature = collector.getData(temperature_column_name);
                float humidity = collector.getData(humidity_column_name);
                double time = -Math.ceil((last_time - collector.getCollectionTime()) * this.getSerialSelected().getSamplingSettings().getMultiplier() * 100) / 100;
                seriesTemperature.getData().add(new XYChart.Data<String, Float>(String.valueOf(time), temperature));
                seriesHumidity.getData().add(new XYChart.Data<String, Float>(String.valueOf(time), humidity));
            }
        }
        this.chart.getData().add(seriesTemperature);
        this.chart.getData().add(seriesHumidity);
        this.averageTemperatureIndicator.setText(this.getSerialSelected().getDataCollectorList().getDataAverages().get(temperature_column_name).toString());
        this.averageHumidityIndicator.setText(this.getSerialSelected().getDataCollectorList().getDataAverages().get(humidity_column_name).toString());
        this.getDatabaseSelected().insert();
    }


    /**
     * Metodo utilizzato per ottenere {@link #databasesUsed}.
     *
     * @return {@link #databasesUsed}
     */
    public DatabaseList getDatabasesUsed() {
        return this.databasesUsed;
    }

    /**
     * Metodo utilizzato per ottenere {@link #driversUsed}.
     *
     * @return {@link #driversUsed}
     */
    public DriverList getDriversUsed() {
        return this.driversUsed;
    }


    /**
     * Metodo utilizzato per ottenere il {@link Database} selezionato.
     *
     * @return {@link Database} selezionato
     */
    public Database getDatabaseSelected() {
        return this.databaseSelector.getValue();
    }

    /**
     * Metodo utilizzato per ottenere il {@link Server} selezionato.
     *
     * @return {@link Server} selezionato
     */
    public Server getSerialSelected() {
        return this.interfaceSelector.getValue();
    }


    /**
     * Metodo utilizzato per ottenere {@link #logger}.
     *
     * @return {@link #logger}
     */
    public Logger getLogger() {
        return this.logger;
    }


    /**
     * Metodo utilizzato per abilitare i pulsanti che permettono il campionamento:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #timerController}
     *     </li>
     *     <li>
     *         {@link #startButton}
     *     </li>
     *     <li>
     *         {@link #stopButton}
     *     </li>
     *     <li>
     *         {@link #addNewServerButton}
     *     </li>
     *     <li>
     *         {@link #databaseManagerButton}
     *     </li>
     *     <li>
     *         {@link #interfaceSelector}
     *     </li>
     *     <li>
     *         {@link #databaseSelector}
     *     </li>
     *     <li>
     *         {@link #samplingSettings}
     *     </li>
     * </ul>
     */
    @FXML
    public void start() {
        this.isSampling = true;
        this.timerController.start();
        this.startButton.setDisable(true);
        this.stopButton.setDisable(false);
        this.addNewServerButton.setDisable(true);
        this.databaseManagerButton.setDisable(true);
        this.interfaceSelector.setDisable(true);
        this.databaseSelector.setDisable(true);
        this.samplingSettings.setDisable(true);
    }

    /**
     * Metodo utilizzato per disabilitare i pulsanti che permettono il campionamento:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link #timerController}
     *     </li>
     *     <li>
     *         {@link #startButton}
     *     </li>
     *     <li>
     *         {@link #stopButton}
     *     </li>
     *     <li>
     *         {@link #addNewServerButton}
     *     </li>
     *     <li>
     *         {@link #databaseManagerButton}
     *     </li>
     *     <li>
     *         {@link #interfaceSelector}
     *     </li>
     *     <li>
     *         {@link #databaseSelector}
     *     </li>
     *     <li>
     *         {@link #samplingSettings}
     *     </li>
     * </ul>
     */
    public void stopSampling() {
        this.isSampling = false;
        this.timerController.stop();
        this.stopButton.setDisable(true);
        this.startButton.setDisable(false);
        this.addNewServerButton.setDisable(false);
        this.databaseManagerButton.setDisable(false);
        this.interfaceSelector.setDisable(false);
        this.databaseSelector.setDisable(false);
        this.samplingSettings.setDisable(false);
    }

    /**
     * Metodo utilizzato per fermare il campionamento.
     */
    @FXML
    public void stop() {
        this.stopSampling();
    }

    /**
     * Metodo utilizzato per chiudere la finestra corrente, quindi per arrestare il programma.
     */

    public void exit() {
        this.isOpened = false;
        this.timerController.stop();
    }
}

/**
 * Record ChartFeature, utilizzato per definire alcuni parametri del grafico.
 *
 * @param widthInPixelsOfChart                    larghezza (px) di default della finestra in cui si trova il grafico
 * @param numberOfPoints_for_widthInPixelsOfChart numero di punti presenti in {@link #widthInPixelsOfChart}
 * @param howManyPixelsYouNeedToAddNewPoints      ogni quanti pixel bisogna aggiungere nuovi punti
 * @param numberOfPointsToAdd                     numero di punti da aggiungere
 */
record ChartFeature(int widthInPixelsOfChart, int numberOfPoints_for_widthInPixelsOfChart,
                    int howManyPixelsYouNeedToAddNewPoints, int numberOfPointsToAdd) {
}
