module FridgeInternalMeasurement {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.junit.jupiter.api;

    opens ArduinoSampler;

    opens ArduinoSampler.interfaccia to javafx.graphics, javafx.fxml;

}