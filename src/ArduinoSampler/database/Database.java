package ArduinoSampler.database;

import ArduinoSampler.arduino.DataCollector;
import ArduinoSampler.interfaccia.IndexController;
import ArduinoSampler.interfaces.DatabaseElement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe Database, utilizzata per gestire un singolo Database connesso al proprio {@link Driver}.
 * <p>
 * Implementa l'interfaccia {@link DatabaseElement}.
 * </p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #setName(String)}
 *     </li>
 *     <li>
 *         {@link #setDriver(Driver)}
 *     </li>
 *     <li>
 *         {@link #hasValidName()}
 *     </li>
 *     <li>
 *         {@link #build()}
 *     </li>
 *     <li>
 *         {@link #testConnection()}
 *     </li>
 *     <li>
 *         {@link #createStatement()}
 *     </li>
 *     <li>
 *         {@link #checkStatement()}
 *     </li>
 *     <li>
 *         {@link #insert()}
 *     </li>
 *     <li>
 *         {@link #delete()}
 *     </li>
 *     <li>
 *         {@link #getName()}
 *     </li>
 *     <li>
 *         {@link #getJDBC_URL()}
 *     </li>
 *     <li>
 *         {@link #getRDBMS()}
 *     </li>
 *     <li>
 *         {@link #getDATABASE_URL()}
 *     </li>
 *     <li>
 *         {@link #getDriver()}
 *     </li>
 *     <li>
 *         {@link #getStatement()}
 *     </li>
 *     <li>
 *         {@link #getDataStructure()}
 *     </li>
 *     <li>
 *         {@link #getInformation()}
 *     </li>
 * </ul>
 */
public class Database implements DatabaseElement {

    /**
     * Stringa contenente il nome del Database
     */
    private String name;


    /**
     * Stringa contenente il nome del sistema di gestione dei database relazionali <b>(Relational Database Management System)</b>, in questo caso MySQL
     */
    private String RDBMS;

    /**
     * Stringa contenente l'indirizzo principale del Driver (<i>viene inclusa la porta</i>)
     * <p>
     * es.<br>
     * {@code jdbc:mysql://localhost:3306}
     * </p>
     */
    private String JDBC_URL;


    /**
     * Stringa contenente l'ULR del database all'interno del RDBMS
     * <p>
     * Struttura:<br>
     * <code>[{@link #JDBC_URL} + '/' + {@link #RDBMS}]</code>
     * </p>
     * <p>
     * es.<br>
     * {@code jdbc:mysql://localhost:3306/db_temperature}
     * </p>
     */
    private String DATABASE_URL;

    /**
     * Variabile {@link java.sql.Statement}, utilizzata per eseguire le query, sull'RDBMS connesso, riferite a questo database.
     */
    private Statement statement;

    /**
     * Variabile {@link DatabaseStructure}, utilizzata per immagizanre la struttura del database.
     */
    private DatabaseStructure databaseStructure;

    /**
     * Variabile {@link Driver}, utilizzata ler avere un riferimento al driver utilizzato.
     */
    private Driver driver;

    /**
     * Valore di tipo booleano che identifica se questa struttura dati è utilizzabile.
     */
    private boolean usable = false;

    /**
     * <p>Costruttore della Classe {@link Database}.</p>
     * Si occupa di:
     * <ul style="margin-top:0">
     *     <li>
     *         impostare un nome al database
     *     </li>
     *     <li>
     *         impostare un driver al database
     *     </li>
     *     <li>
     *         impostare le varie variabili necessarie per il corretto funzionamneto della classe (vedi: {@link #setDriver(Driver)})
     *     </li>
     *     <li>
     *         ottenere la struttura del database (vedi: {@link #buildDataStructure()})
     *     </li>
     * </ul>
     *
     * @param dbDriver {@link Driver} da utilizzare per questo database
     * @param db_name  nome del database
     * @see #setName(String)
     * @see #setDriver(Driver)
     */
    public Database(Driver dbDriver, String db_name) {
        this.setName(db_name);
        this.setDriver(dbDriver);
    }


    /**
     * Costruttore della Classe {@link Database}.
     * <p>
     * Si occupa di:
     * </p>
     * <ul style="margin-top:0">
     *     <li>
     *         impostare un nome al database
     *     </li>
     *     <li>
     *         impostare le varie variabili necessarie per il corretto funzionamento della classe (vedi: {@link #setDriver(Driver)})
     *     </li>
     * </ul>
     * <p>
     * <b>NOTE</b><br>
     * Questo costruttore non imposta automaticamente il {@link Driver} a cui é associato il {@link Database}, necessita successivamente dell'utilizzo del metodo {@link #setDriver(Driver)}.
     * </p>
     *
     * @param map_information Variabile di tipo {@link Map} contenente le informazioni del {@link Database}
     */
    public Database(Map<String, String> map_information) {
        this.name = map_information.get("DB_NAME");
        this.JDBC_URL = map_information.get("JDBC_URL");
        this.DATABASE_URL = this.JDBC_URL + "/" + this.name;
    }

    /**
     * Metodo per verificare l'uguaglianza con il database passato come parametro
     *
     * @param databaseToCompare {@link Database} da confrontare
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se entrambi i {@link Database} possiedono lo stesso {@link #DATABASE_URL}
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se i {@link Database} non possiedono lo stesso {@link #DATABASE_URL}
     *     </li>
     * </ul>
     */
    public boolean equals(Database databaseToCompare) {
        return Objects.equals(this.DATABASE_URL, databaseToCompare.getDATABASE_URL());
    }

    /**
     * Metodo utilizzato per impostare il nome al database
     *
     * @param dbName Nome del database
     */
    public void setName(String dbName) {
        this.name = dbName;
    }

    /**
     * Metodo utilizzato per impostare:
     * <ul>
     *     <li>
     *         il driver del database
     *     </li>
     *     <li>
     *         l'indirizzo {@link #JDBC_URL}
     *     </li>
     *     <li>
     *         il sistema di gestione {@link #RDBMS}
     *     </li>
     *     <li>
     *         l'indirizzo del database {@link #DATABASE_URL}
     *     </li>
     *     <li>
     *         la struttura del database {@link #DATABASE_URL} <i><b>[solo se il database è raggiungibile]</b></i>
     *     </li>
     * </ul>
     *
     * @param dbDriver {@link Driver} da impostare per il database
     */
    public void setDriver(Driver dbDriver) {
        if (dbDriver != null) { // se non viene trovato il driver tramite JDBC_URL
            this.driver = dbDriver;
            this.JDBC_URL = this.driver.getJDBC_URL();
            this.RDBMS = this.driver.getRDBMS_NAME();
            this.DATABASE_URL = this.JDBC_URL + "/" + this.name;
            if (this.testConnection())
                this.buildDataStructure();
        }
    }


    /**
     * Metodo utilizzato per verificare se il Database ha un nome valido.
     *
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui il nome del Database non è già presente sul {@link #RDBMS}
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui il nome del Database è già presente sul {@link #RDBMS}
     *     </li>
     * </ul>
     */
    public boolean hasValidName() {
        return !this.driver.checkIfDatabaseExists(this.name);
    }

    /**
     * Metodo utilizzato per creare il database.
     *
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se viene creato
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se non viene creato
     *     </li>
     * </ul>
     * @see Driver#createDatabase(String)
     */
    public boolean build() {
        return this.driver.createDatabase(this.name);
    }

    /**
     * Metodo utilizzato per realizzare la struttura del Database
     *
     * @see DatabaseStructure
     */
    public void buildDataStructure() {
        this.databaseStructure = new DatabaseStructure(this);
        ResearchParameters temperatureVariableNames = new ResearchParameters("temperature", "temp", "t");
        ResearchParameters humidityVariableNames = new ResearchParameters("humidity", "hum", "rh%", "um");
        this.usable = this.databaseStructure.findTable(temperatureVariableNames, humidityVariableNames);
        this.databaseStructure.setTemperatureColumnName(temperatureVariableNames.getColumnName());
        this.databaseStructure.setHumidityColumnName(humidityVariableNames.getColumnName());
    }

    /**
     * Metodo utilizzato per indicare che il Database è utilizzabile.
     *
     * @return {@code true} | {@code false}
     */
    public boolean isUsable() {
        return this.usable;
    }

    /**
     * Metodo utilizzato per verificare che il {@link Database} sia raggiungibile.
     * <p>L'indirizzo che viene utilizzato è contenuto nella variabile {@link #DATABASE_URL}.</p>
     *
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui il test restituisca un esito positivo.
     *         <p><i>(in questo caso la connessione viene chiusa)</i></p>
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui il {@link Driver} sia nullo
     *     </li>
     *     <li>
     *         se insorge un {@link SQLException}
     *     </li>
     * </ul>
     */
    public boolean testConnection() {
        if (this.driver == null) // se non possiede un driver
            return false;
        try (
                Connection conn = DriverManager.getConnection(this.DATABASE_URL, this.driver.getUSER(), this.driver.getPASS()) //tento la connessione con le credenziali che sono state passate
        ) {
            conn.close(); //in caso di esito positivo, chiudo la connessione e restituisco il valore [true]
            return true;
        } catch (SQLException e) {
            return false; //in caso di esito negativo, restituisco il valore [false]
        }

    }

    /**
     * Metodo utilizzato per inizializzare la variabile {@link #statement}.
     * <p>
     * Gli eventuali {@link SQLException} vengono scritti sulla console dell'interfaccia {@link IndexController}.
     * </p>
     */
    public void createStatement() {
        try {
            Connection conn = DriverManager.getConnection(this.DATABASE_URL, this.driver.getUSER(), this.driver.getPASS());
            this.statement = conn.createStatement();
        } catch (SQLException e) {
            PRIMARY_CONTROLLER.getLogger().write(e.getMessage());
        }
    }


    /**
     * Metodo utilizzato per verificare lo stato della connessione.
     * <p>Viene creato un nuovo {@link #statement}</p> se:
     * <ul style="margin-top:0">
     *     <li>
     *         è nullo.
     *     </li>
     *     <li>
     *         la connessione con il {@link #RDBMS} è chiusa.
     *     </li>
     *     <li>
     *         durante il controllo è insorto {@link SQLException}.
     *     </li>
     * </ul>
     *
     * @see #createStatement()
     */
    public void checkStatement() {
        try {
            if (this.statement == null || (this.statement.isClosed()))
                this.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            this.createStatement();
        }
    }

    /**
     * Metodo utilizzato per inserire i dati raccolti all'interno del Database.
     *
     * @see DataCollector
     * @see DatabaseStructure
     */
    public void insert() {
        this.checkStatement();
        DataCollector last_collector = PRIMARY_CONTROLLER.getSerialSelected().getDataCollectorList().getLastDataCollector();
        String query = "INSERT INTO " + this.databaseStructure.getTableName() + this.databaseStructure.getTupleValuesName() + " VALUES (" +
                "'" + last_collector.getCollectionDateTime() + "', " + last_collector.getData(this.databaseStructure.getTemperatureColumnName()) + ");";
//        System.out.println(query);
        try {
            this.statement.executeUpdate(query);
        } catch (SQLException e) {
            PRIMARY_CONTROLLER.getLogger().writeWithTime("Errore durante l'inserimento dei dati raccolti nel Database");
            PRIMARY_CONTROLLER.getLogger().write(e.getMessage());
        }
    }


    /**
     * Metodo utilizzato per eliminare <b>definitivamente</b> il database.
     *
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se viene eliminato
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se non viene eliminato
     *     </li>
     * </ul>
     * @see Driver#removeDatabase(String)
     */
    public boolean delete() {
        return this.driver.removeDatabase(this.name);
    }

    /**
     * Metodo utilizzato per ottenere il nome del {@link Database}.
     *
     * @return {@link #name}
     */
    public String getName() {
        return this.name;
    }

    /**
     * Metodo utilizzato per ottenere l'indirizzo principale del {@link Driver}.
     *
     * @return {@link #JDBC_URL}
     */
    public String getJDBC_URL() {
        return this.JDBC_URL;
    }

    /**
     * Metodo utilizzato per ottenere il nome del sistema di gestione.
     *
     * @return {@link #RDBMS}
     */
    public String getRDBMS() {
        if (this.RDBMS == null)
            return "Nessun RDBMS identificato";
        return this.RDBMS;
    }


    /**
     * Metodo utilizzato per ottenere l'indirizzo del Database.
     *
     * @return {@link #DATABASE_URL}
     */
    public String getDATABASE_URL() {
        return this.DATABASE_URL;
    }

    /**
     * Metodo utilizzato per ottenere il {@link Driver} che gestisce questo Database.
     *
     * @return {@link Driver}
     */
    public Driver getDriver() {
        return this.driver;
    }


    /**
     * Metodo utilizzato per ottenere {@link #statement}
     *
     * @return {@link #statement}
     */
    public Statement getStatement() {
        this.checkStatement();
        return this.statement;
    }


    /**
     * Metodo utilizzato per ottenere la struttura del Database.
     *
     * @return {@link DatabaseStructure}
     */
    public DatabaseStructure getDataStructure() {
        return this.databaseStructure;
    }


    /**
     * <p>Metodo utilizzato per ottenere le informazioni del {@link Database} sotto forma di {@link Map}.</p>
     * <p>
     * Struttura map:
     * <table>
     *     <tr>
     *         <th>
     *             Chiave
     *         </th>
     *         <th>
     *             Valore
     *         </th>
     *     </tr>
     *     <tr>
     *         <td>
     *              DB_NAME:
     *         </td>
     *         <td>
     *              {@link #name}
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>
     *              JDBC_URL:
     *         </td>
     *         <td>
     *             {@link #JDBC_URL}
     *         </td>
     *     </tr>
     * </table>
     *
     * @return {@link Map}
     */

    public Map<String, String> getInformation() {
        Map<String, String> info = new HashMap<>();
        info.put("DB_NAME", this.name);
        info.put("JDBC_URL", this.JDBC_URL);
        return info;
    }
}
