package ArduinoSampler.arduino;

import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * <p>Classe TimerController, utilizzata per effettuare i campionamenti.</p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #start()}
 *     </li>
 *     <li>
 *         {@link #stop()}
 *     </li>
 * </ul>
 */
public class TimerController {
    /**
     * Oggetto {@link Timer}
     */
    private Timer timer;

    /**
     * Metodo utilizzato per avviare il campionamento
     *
     * @see Controller
     * @see Server#getSamplingSettings()
     */
    public void start() {
        long startTime = System.currentTimeMillis();
        TimerTask refreshTimePassedIndicatorTask = new TimerTask() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                long timeDifference = time - startTime;
                String hms = String.format("%02d:%02d:%02d.%03d", TimeUnit.MILLISECONDS.toHours(timeDifference),
                        TimeUnit.MILLISECONDS.toMinutes(timeDifference) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDifference)),
                        TimeUnit.MILLISECONDS.toSeconds(timeDifference) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDifference)),
                        timeDifference - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timeDifference))
                );
                Platform.runLater(() -> {
                    PRIMARY_CONTROLLER.refreshTimePassedIndicator(hms);
                });
            }
        };
        this.timer = new Timer();
        Controller timerController = new Controller();
        this.timer.schedule(refreshTimePassedIndicatorTask, 0, 100);
        this.timer.schedule(timerController, 0, (long) PRIMARY_CONTROLLER.getSerialSelected().getSamplingSettings().getPeriod_ms());
        PRIMARY_CONTROLLER.getLogger().writeWithTime("Campionamento avviato...");
    }

    /**
     * Metodo utilizzato per arrestare il campionamento
     */
    public void stop() {
        if (this.timer != null) {
            this.timer.cancel();
            PRIMARY_CONTROLLER.getLogger().writeWithTime("Campionamento fermato...");
            PRIMARY_CONTROLLER.getLogger().write(String.format("Numero campionamenti effettuati: %d", PRIMARY_CONTROLLER.getSerialSelected().getDataCollectorCount()));
        }
    }

}

/**
 * Classe Controller, estende la classe {@link TimerTask}.
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #run()}
 *     </li>
 * </ul>
 */
class Controller extends TimerTask {

    /**
     * Override al metodo {@link TimerTask#run()}.
     * <p>
     * Questo metodo viene richiamato a ogni intervallo di campionamento.
     *
     * @see SamplingSettings
     * </p>
     */
    @Override
    public void run() {
        Platform.runLater(() -> {
            if (PRIMARY_CONTROLLER.getSerialSelected() != null)
                PRIMARY_CONTROLLER.getSerialSelected().readAndCollect();
            PRIMARY_CONTROLLER.refreshChart();
        });
    }
}
