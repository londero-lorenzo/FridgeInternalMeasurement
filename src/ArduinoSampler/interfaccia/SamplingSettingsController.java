package ArduinoSampler.interfaccia;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe SamplingSettingsController, utilizzata per gestire le velocità di campionamento.
 * <p>Estende {@link DefaultController}</p>
 */
public class SamplingSettingsController extends DefaultController {


    /**
     * Variabile {@link Label}, utilizzata per avvisare l'utente del tipo di campionamento fare (frequenza, periodi).
     */
    @FXML
    private Label notifyLabel;

    /**
     * Variabile {@link RadioButton} utilizzata per selezionare l'opzione di effettuare il campionamento con la frequenza.
     */
    @FXML
    private RadioButton frequencyRadio;

    /**
     * Variabile {@link RadioButton} utilizzata per selezionare l'opzione di effettuare il campionamento con il periodo.
     */
    @FXML
    private RadioButton periodRadio;

    /**
     * Variabile {@link ToggleGroup} utilizzata per ottenere quale {@link RadioButton} è stato selezionato.
     */
    @FXML
    private ToggleGroup typeOfSampling;

    /**
     * Variabile {@link TextField} nel quale verrà inserito il valore di campionamento.
     */
    @FXML
    private TextField valueField;

    /**
     * Variabile {@link Label}, utilizzata per indicare quali metodologie di campionamento sono possibili selezionare.
     */
    @FXML
    private Label labelToChange;

    /**
     * Variabile {@link Label}, utilizzata in caso di errato inserimento del valore.
     */
    @FXML
    private Label errorLabel;

    /**
     * Metodo utilizzato per impostare il campionamento in base alla frequenza o in base al periodo.
     *
     * @param event evento catturato dal bottone
     */
    @FXML
    void done(ActionEvent event) {
        String raw_value = valueField.getText();
        try {
            float value = Float.parseFloat(raw_value);
            if (typeOfSampling.getSelectedToggle() == frequencyRadio)
                PRIMARY_CONTROLLER.getSerialSelected().getSamplingSettings().setFrequency(value);
            else
                PRIMARY_CONTROLLER.getSerialSelected().getSamplingSettings().setPeriod(value);
            PRIMARY_CONTROLLER.refreshSamplingIndicator();
            this.exit();
        } catch (NumberFormatException e) {
            errorLabel.setVisible(true);
        }
    }

    /**
     * Metodo richiamato alla creazione del Controller, imposta un ascoltatore su {@link #periodRadio} in modo tale cambiare la scritta in {@link #notifyLabel} in baso all'opzione selezionata.
     */

    public void initialize() {
        this.periodRadio.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old_value, Boolean new_value) {
                notifyLabel.setText((!new_value) ? "Indica la frequenza di campionamento:" : "Indica il periodo di campionamento:");
            }
        });
        labelToChange.setText("Indicare se si vuole rappresentare la velocità di campionamento\nin base al numero di campioni al secondo o in base al periodo:");
    }

    /**
     * Metodo utilizzato per impostare lo Stage passato come parametro a questo controller.
     * Imposta anche i valori minimi di dimensione per questa finestra.
     *
     * @param stage nuovo Stage
     */
    public void setStage(Stage stage) {
        super.setStage(stage);
        this.setMinSize(new WindowSize(390, 190));
    }
}
