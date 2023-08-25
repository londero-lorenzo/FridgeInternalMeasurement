package ArduinoSampler.interfaccia;

import ArduinoSampler.arduino.ServerSocket;
import ArduinoSampler.exceptions.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ServerWatchwordInterfaceController extends DefaultController {

    @FXML
    private TextField watchwordField;

    private ServerSocket socketToVerify;


    @FXML
    void verify(ActionEvent event) {
        if (this.socketToVerify != null) {
            String watchword = watchwordField.getText();
            try {
                socketToVerify.verifyWatchword(watchword);
            } catch (SocketException e) {
                this.createWarningDialogWindow("Errore", e.getMessage());
            }

        }
    }

    void setSocketToVerify(ServerSocket socket){
        this.socketToVerify = socket;
    }

}
