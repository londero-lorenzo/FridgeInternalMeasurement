package ArduinoSampler.interfaccia;

import ArduinoSampler.database.DatabaseList;
import ArduinoSampler.database.DriverList;
import ArduinoSampler.file_managers.DataArchiver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String path_driver_data_folder = "data";
        String driver_data_file = "drivers.save";
        DataArchiver driver_archiver = new DataArchiver(path_driver_data_folder, driver_data_file, new DriverList());

        String path_database_data_folder = "data";
        String database_data_file = "database.save";
        DataArchiver database_archiver = new DataArchiver(path_database_data_folder, database_data_file, new DatabaseList());


        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("index_interface.fxml")));
        Parent root = loader.load();
        IndexController controller = loader.getController();

        controller.setDriverDataArchiver(driver_archiver);
        controller.setDatabaseDataArchiver(database_archiver);
        controller.setStage(primaryStage);
        // Estrapolo il controller per avere verso di lui un riferimento
//        controller.setStage(primaryStage);
        primaryStage.setTitle("Analisi dati");
        primaryStage.setScene(new Scene(root, 900, 650));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}