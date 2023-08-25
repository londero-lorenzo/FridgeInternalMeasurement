package ArduinoSampler.interfaccia;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Classe DialogController, utilizzata per gestire i dialoghi di errore tramite interfaccia grafica.
 * <p>Estende {@link DefaultController}</p>
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #setStage(Stage)}
 *     </li>
 *     <li>
 *         {@link #setWarningLabel(String)}
 *     </li>
 *     <li>
 *         {@link #setProceedButtonName(String)}
 *     </li>
 *     <li>
 *         {@link #setProceedButtonAction(EventHandler)}
 *     </li>
 * </ul>
 */
public class DialogController extends DefaultController {


    /**
     * Variabile {@link Label} su cui vengono scritte le avvertenze/errori.
     */
    @FXML
    private Label warningLabel;
    /**
     * Variabile {@link Button} che si riferisce al bottone che permette di procedere.
     */
    @FXML
    private Button proceedButton;
    /**
     * Variabile {@link Button} che si riferisce al bottone che permette di tornare indietro.
     */
    @FXML
    private Button goBackButton;
    /**
     * Variabile {@link GridPane} che si riferisce alla griglia in cui sono inseriti:
     * <ul style="margin-top: 0px">
     *     <li>
     *         {@link #proceedButton}
     *     </li>
     *     <li>
     *         {@link #goBackButton}
     *     </li>
     * </ul>
     */
    @FXML
    private GridPane gridButton;

    /**
     * Override al {@link DefaultController#setStage(Stage)} utilizzato per impostare il nuovo {@link Stage} a questo Controller.
     *
     * @param stage nuovo Stage
     */
    public void setStage(Stage stage) {
        super.setStage(stage);
        //imposto il metodo che verrà richiamato alla chiusura dello stage
        this.stage.resizableProperty().setValue(Boolean.FALSE);
    }

    /**
     * Metodo utilizzato per scrivere il messaggio su {@link #warningLabel}.
     *
     * @param warning stringa contenente il messaggio d'avviso/errore
     */
    public void setWarningLabel(String warning) {
        this.warningLabel.setText(warning);
    }

    /**
     * Metodo utilizzato per impostare il nome al {@link #proceedButton}.
     *
     * @param name stringa contenente il nome del bottone
     */
    public void setProceedButtonName(String name) {
        this.proceedButton.setText(name);
    }

    /**
     * Metodo utilizzato per impostare l'azione da far avvenire nel momento della pressione del {@link #proceedButton}.
     *
     * @param action {@link EventHandler<ActionEvent>}
     */
    public void setProceedButtonAction(EventHandler<ActionEvent> action) {
        this.proceedButton.setOnAction(action);
    }

    /**
     * Metodo utilizzato per lasciare visualizzato solo il {@link #proceedButton}.
     * <p>Utile se si vuole impedire di ritornare sulla finestra precedente.</p>
     */
    public void leaveOnlyProceedButton() {
        this.removeGoBackButton();
        this.gridButton.getColumnConstraints().remove(1);
    }

    /**
     * Metodo utilizzato per rimuovere il {@link #goBackButton}.
     */
    public void removeGoBackButton() {
        this.gridButton.getChildren().remove(this.goBackButton);
    }

    /**
     * Metodo utilizzato per nascondere il {@link #goBackButton}.
     */
    public void hideGoBackButton() {
        this.goBackButton.setVisible(false);
    }

}
