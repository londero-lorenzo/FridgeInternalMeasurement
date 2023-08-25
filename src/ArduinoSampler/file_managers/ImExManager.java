package ArduinoSampler.file_managers;

import ArduinoSampler.database.Database;
import ArduinoSampler.database.Driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe ImExManager, utilizzata per le operazioni d'importazione ed esportazione dei database.
 * <p>Metodi <b>generali</b>:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *           {@link #checkSecureFilePriv()}
 *     </li>
 *     <li>
 *         {@link #find()}
 *     </li>
 *     <li>
 *           {@link #setTarget(String)}
 *     </li>
 *     <li>
 *         {@link #getImExStatus()}
 *     </li>
 *     <li>
 *         {@link #getTargetPath()}
 *     </li>
 *     <li>
 *         {@link #getTarget()}
 *     </li>
 * </ul>
 *
 *
 * <p>Metodi principali <b>Importazione</b>:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *          {@link #importFromFile(String)}
 *     </li>
 *     <li>
 *          {@link #importFromFileWithDatabaseName(String, String)}
 *     </li>
 * </ul>
 * <p>Metodi principali <b>Esportazione</b>:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *          {@link #exportAllDatabase(String)}
 *     </li>
 *     <li>
 *          {@link #exportOnlyData(String)}
 *     </li>
 * </ul>
 */

public class ImExManager {

    /**
     * Variabile statica di tipo stringa, utilizzata per indicare che sono consentite le importazioni e le esportazioni.
     */
    final public static String IMP_EXP_ENABLED = "IMPORT_EXPORT_OK";

    /**
     * Variabile statica di tipo stringa, utilizzata per indicare che sono consentite le importazioni e le esportazioni ma sono ristrette verso una specifica cartella.
     */
    final public static String IMP_EXP_RESTRICTED = "IMPORT_EXPORT_RESCRITED";

    /**
     * Variabile statica di tipo stringa, utilizzata per indicare che le importazioni e le esportazioni sono disabilitate.
     */
    final public static String IMP_EXP_DISABLED = "IMPORT_EXPORT_DISABLED";

    /**
     * Variabile di tipo stringa contenente il nome del programma che permette le varie operazioni d'importazione/esportazione.
     */
    private String target;

    /**
     * Variabile di tipo carattere, utilizzata per indicare il tipo di separatore di colonne da utilizzare nel momento dell'esportazione.
     */
    final private char SEPARATOR = ';';

    /**
     * Variabile di tipo {@link Database} utilizzata nel processo di esportazione.
     */
    private Database database;

    /**
     * Variabile di tipo {@link Driver} utilizzata nei vari processi d'importazione/esportazione.
     */
    private final Driver driver;


    /**
     * Variabile di tipo stringa contenente il percorso assoluto relativo al {@link #target}.
     */
    private String targetPath;

    /**
     * Variabile di tipo stringa contenente il percorso assoluto relativo alla cartella in cui opera il sistema di gestione dei database relazionali.
     */
    private final String basedir;

    /**
     * Variabile di tipo stringa contenente il valore corrispondente alla variabile <b><i>secure_file_priv</i></b>.
     * Possibili valori che può assumere tale variabile:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@code ""}, le operazioni di importazione ed esportazione sono senza restrizioni.
     *     </li>
     *     <li>
     *         {@code <path>}, le operazioni di importazione ed esportazione sono limitate nella cartella di riferimento.
     *     </li>
     *     <li>
     *         {@code null}, il sistema di gestione dei database relazionali non permette importazioni ed esportazioni.
     *     </li>
     * </ul>
     */
    private final String secureFilePriv;

    /**
     * Variabile di tipo stringa che indica lo status delle esportazioni.
     * <p>
     * Può assumere tre possibili valori:
     *     <ul style="margin-top:0px">
     *         <li>
     *             {@link #IMP_EXP_ENABLED}
     *         </li>
     *         <li>
     *             {@link #IMP_EXP_RESTRICTED}
     *         </li>
     *         <li>
     *             {@link #IMP_EXP_DISABLED}
     *         </li>
     *     </ul>
     * </p>
     */
    private String ImExStatus;


    /**
     * Costruttore della Classe {@link ImExManager}, utilizzato per:
     * <ul style="margin-top:0px">
     *     <li>
     *         inizializzare il {@link #database}
     *     </li>
     *     <li>
     *         inizializzare il {@link #driver}
     *     </li>
     *     <li>
     *         inizializzare la variabile {@link #basedir}
     *     </li>
     *     <li>
     *         inizializzare la variabile {@link #secureFilePriv}
     *     </li>
     *     <li>
     *         verificare lo stato delle importazioni/esportazioni {@link #ImExStatus}
     *     </li>
     * </ul>
     *
     * @param database {@link Database} a cui farà riferimento questa Classe
     */
    public ImExManager(Database database) {
        this.database = database;
        this.driver = this.database.getDriver();
        this.basedir = this.driver.getBaseDir();
        this.secureFilePriv = this.driver.getSecureFilePriv();
        this.checkSecureFilePriv();
    }


    /**
     * Costruttore della Classe {@link ImExManager}, utilizzato per:
     * <ul style="margin-top:0px">
     *     <li>
     *         inizializzare il {@link #driver}
     *     </li>
     *     <li>
     *         inizializzare la variabile {@link #basedir}
     *     </li>
     *     <li>
     *         inizializzare la variabile {@link #secureFilePriv}
     *     </li>
     *     <li>
     *         verificare lo stato delle importazioni/esportazioni {@link #ImExStatus}
     *     </li>
     * </ul>
     *
     * @param driver {@link Driver} a cui farà riferimento questa Classe
     * @see #checkSecureFilePriv()
     */
    public ImExManager(Driver driver) {
        this.driver = driver;
        this.basedir = this.driver.getBaseDir();
        this.secureFilePriv = this.driver.getSecureFilePriv();
        this.checkSecureFilePriv();
    }

    /**
     * Metodo utilizzato per verificare lo stato delle importazioni/esportazioni.
     *
     * @see #secureFilePriv
     * @see #ImExStatus
     */
    public void checkSecureFilePriv() {
        if (this.secureFilePriv == null) {
            ImExStatus = IMP_EXP_DISABLED;
            return;
            // il database ha le impostazioni di esportazione e importazione disabilitate
        }
        if (this.secureFilePriv.equals(""))
            ImExStatus = IMP_EXP_ENABLED;
        else
            ImExStatus = IMP_EXP_RESTRICTED;
    }

    /**
     * Metodo utilizzato per trovare il file {@link #target} all'interno della {@link #basedir}.
     *
     * @return {@code true} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se {@link #target} è stato trovato
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se {@link #ImExStatus} è disabilitato
     *     </li>
     *     <li>
     *         se {@link #target} non è stato trovato
     *     </li>
     * </ul>
     */

    public boolean find() {
        if (Objects.equals(ImExStatus, IMP_EXP_DISABLED))
            return false;
        final File folder = new File(this.basedir);
        File target = searchInDirectory(folder);
        if (target == null)
            return false;
        this.targetPath = target.getAbsolutePath();
        return true;
    }

    /**
     * Metodo utilizzato per spostarsi all'interno della cartella {@link #basedir}, alla ricerca di {@link #target}.
     *
     * @param folder {@link File} che rappresenta la cartella da analizzare
     * @return {@link File}
     */
    private File searchInDirectory(File folder) {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                File file_found = this.searchInDirectory(file);
                if (file_found != null)
                    return file_found;
            } else {
                if (Objects.equals(file.getName(), this.target)) {
                    return file;
                }
            }
        }
        return null;
    }

    /**
     * Metodo utilizzato per far eseguire i comandi, passati come argomento, al {@link #target}.
     *
     * @param cmd {@code String[]}, rappresenta la lista dei comandi
     * @return {@code true} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando è stato eseguito correttamente
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando ha generato degli errori
     *     </li>
     *     <li>
     *         se durante l'esecuzione del comando è insorto {@link IOException}
     *     </li>
     * </ul>
     */
    private boolean execute(String[] cmd) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String se;
            while ((se = stdError.readLine()) != null)
                if (se.toLowerCase().contains("error"))
                    PRIMARY_CONTROLLER.getLogger().writeWithTime(se);
            return true;
        } catch (IOException e) {
            PRIMARY_CONTROLLER.getLogger().writeWithTime(e.getMessage());
            return false;
        }
    }

    /**
     * Metodo utilizzato per importare un database all'interno del {@link Driver} selezionato.
     *
     * @param pathString percorso che fa riferimento al file .sql
     * @return {@code true} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando è stato eseguito correttamente
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando ha generato degli errori
     *     </li>
     * </ul>
     * @see #execute(String[])
     */
    public boolean importFromFile(String pathString) {
        String[] cmd = new String[]{
                this.targetPath,
                "-u", this.driver.getUSER(),
                "-p" + this.driver.getPASS(),
                "-e", "source " + pathString
        };
        return this.execute(cmd);
    }

    /**
     * Metodo utilizzato per importare un database, di cui si conosce il nome, all'interno del {@link Driver} selezionato.
     *
     * @param db_name    nome del database
     * @param pathString percorso che fa riferimento al file .sql
     * @return {@code true} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando è stato eseguito correttamente
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando ha generato degli errori
     *     </li>
     * </ul>
     * @see #execute(String[])
     */
    public boolean importFromFileWithDatabaseName(String db_name, String pathString) {
        String[] cmd = new String[]{
                this.targetPath,
                "-u", this.driver.getUSER(),
                "-p" + this.driver.getPASS(),
                "--execute=" + "use " + db_name + ";source \"" + pathString.replace("\\", "/") + "\";"
                //TODO: Eseguire esportazione di prova e utilizzarla per il template (use import_from)
                //"-e", "use " + db_name + ";" + "source " + pathString+";"

        };
        return this.execute(cmd);
    }

    /**
     * Metodo utilizzato per esportare tutto il database in una determinata posizione.
     *
     * @param location percorso del file .sql
     * @return {@code true} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando è stato eseguito correttamente
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando ha generato degli errori
     *     </li>
     * </ul>
     * @see #execute(String[])
     */
    public boolean exportAllDatabase(String location) {
        String[] cmd = new String[]{this.targetPath,
                "-u", this.driver.getUSER(),
                "-p" + this.driver.getPASS(),
                "--databases",
                this.database.getName(), "-r" + location};
        return this.execute(cmd);
    }

    /**
     * Metodo utilizzato per esportare solo i dati contenuti all'interno del database.
     *
     * <p>
     * Questo comando andrà a creare tanti file .csv quanti sono le tabelle all'interno del database da voler esportare.
     * </p>
     *
     * @param location percorso della cartella in cui esportare tutti i file
     * @return {@code true} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando è stato eseguito correttamente
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0px">
     *     <li>
     *         se il comando ha generato degli errori
     *     </li>
     *     <li>
     *         se non sono state eliminate le cartelle temporanee
     *     </li>
     *     <li>
     *         se non sono stati spostati tutti i file .csv
     *     </li>
     * </ul>
     * @see #execute(String[])
     */
    public boolean exportOnlyData(String location) {
        Path tempLocation;
        if (Objects.equals(this.ImExStatus, IMP_EXP_RESTRICTED))
            tempLocation = Paths.get(this.secureFilePriv, "exp_temp/");
        else
            tempLocation = Paths.get(location, "exp_temp/");
        // vengono impostati i vari percorsi temporanei in base alla tipologia di secure_file_priv
        File output_temp_dir = tempLocation.toFile();
        File output_dir = Paths.get(location, this.database.getName()).toFile();
        // vengono inizializzate le varie cartelle
        if (!output_dir.mkdirs()) {
            PRIMARY_CONTROLLER.getLogger().write(String.format("Esista già una cartella denominata '%s' nel percorso '%s'", output_dir.getName(), output_dir.getAbsolutePath()));
            return false;
        }
        if (!output_temp_dir.mkdirs()) {
            PRIMARY_CONTROLLER.getLogger().write(String.format("Esista già una cartella temporanea denominata '%s' nel percorso '%s'", output_temp_dir.getName(), output_temp_dir.getAbsolutePath()));
            return false;
        }
        // vengono create le varie cartelle, se almeno una delle due cartelle non viene generata restituisce [false]
        String[] cmd = new String[]{
                this.targetPath,
                "-u", this.driver.getUSER(),
                "-p" + this.driver.getPASS(),
                this.database.getName(),
                "-t",
                "--tab=" + tempLocation.toAbsolutePath(),
                "--fields-terminated-by=" + SEPARATOR,
                "--fields-enclosed-by=\""
        };
        if (!this.execute(cmd)) {
            PRIMARY_CONTROLLER.getLogger().write(String.format("Si sono verificati degli errori durante l'esecuzione del comando '%s'", Arrays.toString(cmd)));
            return false;
        }
        // viene eseguita l'esportazione, se fallisce restituisce [false]
        boolean error;
        for (File file : Objects.requireNonNull(output_temp_dir.listFiles()))
            if (file.getName().contains(".txt")) {
                error = !file.renameTo(new File(Paths.get(output_dir.getAbsolutePath(), file.getName().replace(".txt", ".csv")).toString()));
                if (error)
                    break;
            } else
                file.delete();
        // vengono spostati i vari file con all'interno i dati, vengono successivamente cancellati i file temporanei
        error = output_temp_dir.delete();
        if (error)
            PRIMARY_CONTROLLER.getLogger().write("Non è stato completato lo spostamento dei file .csv");
        return error;
        // viene infine cancellata la cartella temporanea, se non è possibile farlo viene restituito [false]
    }


    /**
     * Metodo utilizzato per impostare il {@link #target}.
     *
     * @param newTarget stringa che fa riferimento al {@link #target}
     */
    public void setTarget(String newTarget) {
        this.target = newTarget;
    }

    /**
     * Metodo utilizzato per ottenere lo stato delle importazioni/esportazioni.
     *
     * @return {@link #ImExStatus}
     */
    public String getImExStatus() {
        return this.ImExStatus;
    }

    /**
     * Metodo utilizzato per ottenere lo il percorso del {@link #target}
     *
     * @return {@link #targetPath}
     */
    public String getTargetPath() {
        return this.targetPath;
    }

    /**
     * Metodo utilizzato per ottenere il {@link #target}.
     *
     * @return {@link #target}
     */
    public String getTarget() {
        return this.target;
    }
}
