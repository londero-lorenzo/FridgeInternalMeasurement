package ArduinoSampler.interfaccia;

import ArduinoSampler.database.Database;
import ArduinoSampler.file_managers.ImExManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;


/**
 * Classe ExportDatabaseController, utilizzata per gestire le operazioni d'esportazione dei {@link Database} tramite interfaccia grafica.
 * <p>Estende {@link DefaultController}</p>
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #setDatabase(Database)}
 *     </li>
 *     <li>
 *         {@link #browse(ActionEvent)}
 *     </li>
 * </ul>
 */
public class ExportDatabaseController extends DefaultController {

    /**
     * Variabile {@link BorderPane} utilizzata per ottenere lo {@link Stage} riferito a questa finestra.
     */
    @FXML
    private BorderPane mainPanel;

    /**
     * Variabile {@link ImExManager} utilizzata per l'operazione d'esportazione.
     */
    private ImExManager exportManager;

    /**
     * Variabile {@link ToggleGroup} utilizzata per ottenere quale {@link RadioButton} è stato selezionato.
     */
    @FXML
    private ToggleGroup typeOfExport;
    /**
     * Variabile {@link RadioButton} utilizzata per selezionare l'opzione che esporta tutto il {@link Database}
     */
    @FXML
    private RadioButton exportAllDatabaseRadio;

    /**
     * Variabile {@link RadioButton} utilizzata per selezionare l'opzione che esporta solo i dati dal {@link Database}
     */
    @FXML
    private RadioButton exportOnlyDataRadio;

    /**
     * Variabile {@link Label} utilizzata per avvisare quale posizione selezionare per esportare i solo i dati.
     */
    @FXML
    private Label exportOnlyDataAdvice;


    /**
     * Metodo utilizzato per:
     * <ul style="margin-top:0px">
     *     <li>
     *         inizializzare {@link #exportManager} con il {@link Database} passato come argomento
     *     </li>
     *     <li>
     *         impostare e cercare l'applicazione che si occupa dell'esportazione
     *     </li>
     * </ul>
     *
     * @param database_to_export {@link Database} da esportare
     * @see ImExManager
     * @see #createWarningDialogWindowToGoBack(String, String, WindowSize)
     */
    public void setDatabase(Database database_to_export) {
        this.exportManager = new ImExManager(database_to_export);
        this.exportManager.setTarget("mysqldump.exe");
        if (!this.exportManager.find()) {
            String warning_error;
            if (Objects.equals(this.exportManager.getImExStatus(), ImExManager.IMP_EXP_DISABLED))
                warning_error = "Attenzione!\nIl server è impostato per impedire le importazioni ed esportazioni," +
                        "\nsi consiglia di controllare la variabile secure_file_priv nel file my.ini";
            else
                warning_error = "Attenzione!\nNon è stato possibile trovare " + this.exportManager.getTarget();
            this.createWarningDialogWindowToGoBack("Problema durante l'esportazione", warning_error, new WindowSize(375, 125));
            PRIMARY_CONTROLLER.getLogger().writeWithTime(warning_error);
        }
    }

    /**
     * Metodo utilizzato per selezionare:
     * <ul style="margin-top:0px">
     *     <li>
     *         un unico file {@code .sql}, se è stata selezionata l'opzione di esportare tutto il {@link Database}
     *     </li>
     *     <li>
     *         un'intera cartella, se è stata selezionata l'opzione di esportare solo i dati dal {@link Database}
     *     </li>
     * </ul>
     * <p>
     *     Una volta identificato il percorso viene eseguita l'esportazione.
     * </p>
     *
     * @param event L'evento catturato dal bottone
     * @see DialogController
     * @see FileChooser
     * @see DirectoryChooser
     * @see ImExManager
     * @see #createNewWindowWithPriority(String, String, WindowSize)
     */
    @FXML
    public void browse(ActionEvent event) {
        File file;
        if (this.typeOfExport.getSelectedToggle() == this.exportAllDatabaseRadio) {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva su file");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL", "*.sql"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tutti i file", "*.*"));
            Stage stage = (Stage) this.mainPanel.getScene().getWindow();
            file = fileChooser.showSaveDialog(stage);
        } else {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Salva in una cartella");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            Stage stage = (Stage) this.mainPanel.getScene().getWindow();
            file = directoryChooser.showDialog(stage);
        }

        if (file != null) {
            if (this.typeOfExport.getSelectedToggle() == this.exportAllDatabaseRadio) {
                if (this.exportManager.exportAllDatabase(file.getAbsolutePath())) {
                    this.exit();
                    PRIMARY_CONTROLLER.getLogger().writeWithTime("Esportazione completata...");
                    return;
                }
            } else {
                if (this.exportManager.exportOnlyData(file.getAbsolutePath())) {
                    this.exit();
                    PRIMARY_CONTROLLER.getLogger().writeWithTime("Esportazione completata...");
                    return;
                }
            }
            DialogController controller = (DialogController) this.createNewWindowWithPriority("Problema Importazione, espostazione", "dialog.fxml", new WindowSize(375, 125));
            EventHandler<ActionEvent> action = actionEvent -> {
                controller.exit();
            };
            controller.setProceedButtonName("OK");
            controller.leaveOnlyProceedButton();
            controller.setWarningLabel("Attenzione!\nNon è stato possibile completare l'operazione di esportazione");
            controller.setProceedButtonAction(action);
            PRIMARY_CONTROLLER.getLogger().write("Esportazione fallita...");
        }

    }

    /**
     * Metodo richiamato alla creazione del Controller, imposta un ascoltatore sul {@link #exportOnlyDataRadio} per mostrare {@link #exportOnlyDataAdvice} quando viene selezionato.
     */
    public void initialize() {
        this.exportOnlyDataRadio.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old_value, Boolean new_value) {
                exportOnlyDataAdvice.setVisible(new_value);
            }
        });
    }


}
