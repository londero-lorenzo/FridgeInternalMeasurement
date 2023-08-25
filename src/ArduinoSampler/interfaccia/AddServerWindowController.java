package ArduinoSampler.interfaccia;

import ArduinoSampler.arduino.Server;
import ArduinoSampler.arduino.ServerSocket;
import ArduinoSampler.exceptions.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import ArduinoSampler.arduino.Address;

import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

public class AddServerWindowController extends DefaultController {

    @FXML
    private TextField addressField;

    @FXML
    private TextField portField;

    @FXML
    void addNewServer(ActionEvent event) {
        String serverIp = addressField.getText();
        String serverPort = portField.getText();
        if (Objects.equals(serverIp, ""))
            serverIp = " ";
        if (Objects.equals(serverPort, ""))
            serverPort = " ";
        Address newServerAddress = Address.getAddressFromString(serverIp + ':' + serverPort);
        if (newServerAddress.isUsable()){
            ServerSocket newServerSocket = new ServerSocket(newServerAddress);
            try{
                if (newServerSocket.checkForConnection()) {
                    if (newServerSocket.requiresWatchword())
                    {
                        ServerWatchwordInterfaceController serverWatchwordInterfaceController = (ServerWatchwordInterfaceController) this.createNewWindowWithPriority("Parola d'ordine", "ServerWatchword_interface.fxml", new WindowSize(200, 150));
                        serverWatchwordInterfaceController.stage.setResizable(false);
                        serverWatchwordInterfaceController.setSocketToVerify(newServerSocket);
                    }else
                        PRIMARY_CONTROLLER.addServer(new Server(newServerSocket));
                }
            }catch (SocketException socketException) {
                this.createWarningDialogWindow("Errore", socketException.getMessage());
            }
        }else{
            SocketException lastException = newServerAddress.getExceptionHandler().getLastException();
            this.createWarningDialogWindow("Errore", lastException.getMessage());
        }
    }

    public void initialize() {

    }


    public void setStage(Stage stage) {
        super.setStage(stage);
        //imposto il metodo che verrà richiamato alla chiusura dello stage
        this.stage.resizableProperty().setValue(Boolean.FALSE);
    }


}
