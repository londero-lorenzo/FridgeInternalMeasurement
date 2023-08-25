package ArduinoSampler.database;

import ArduinoSampler.interfaccia.IndexController;
import ArduinoSampler.interfaces.DatabaseElement;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ArduinoSampler.interfaccia.IndexController.PRIMARY_CONTROLLER;

/**
 * Classe Driver, questa classe serve per interconnettersi con un sistema di gestione dei database, come in questo caso MySql.
 * <i>Per funzionare necessita che il servizio per la gestione dei database sia avviato e funzionante</i>.
 * <p>
 * Implementa l'interfaccia {@link DatabaseElement}.
 * </p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #createConnection(String, String, String)}
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
 *         {@link #checkIfDatabaseExists(String)}
 *     </li>
 *     <li>
 *         {@link #createDatabase(String)}
 *     </li>
 *     <li>
 *         {@link #removeDatabase(String)}
 *     </li>
 *     <li>
 *         {@link #closeConnection()}
 *     </li>
 *     <li>
 *         {@link #setRDBMS_NAME(String)}
 *     </li>
 *     <li>
 *         {@link #getRDBMS_NAME()}
 *     </li>
 *     <li>
 *         {@link #getUSER()}
 *     </li>
 *     <li>
 *         {@link #getPASS()}
 *     </li>
 *     <li>
 *         {@link #getJDBC_URL()}
 *     </li>
 *     <li>
 *         {@link #getAllDatabases()}
 *     </li>
 *     <li>
 *         {@link #getBaseDir()}
 *     </li>
 *     <li>
 *         {@link #getSecureFilePriv()}
 *     </li>
 *     <li>
 *         {@link #getInformation()}
 *     </li>
 * </ul>
 */

public class Driver implements DatabaseElement {
    /**
     * Variabile di tipo stringa contenente il tipo di protocollo utilizzato per la comunicazione.
     */
    private String SUBPROTOCOL = "jdbc:mysql";

    /**
     * Variabile di tipo stringa contenente il nome del sistema di gestione.
     */
    private String RDBMS_NAME = null;

    /**
     * Variabile di tipo stringa contenente l'URL che fa riferimento al sistema di gestione dei database relazionali (Relational database management system).
     * <p>
     * es.<br>
     * {@code //localhost:3306}
     * </p>
     */
    private String RDBMS_URL;

    /**
     * Variabile di tipo stringa contenente l'intero indirizzo utilizzabile per la connessione al RDBMS.
     * <p>
     * Formato: <br>
     * <code>{@link #SUBPROTOCOL}:{@link #RDBMS_URL}</code>
     * </p>
     * <p>
     * es.<br>
     * {@code jdbc:mysql://localhost:3306}
     * </p>
     */
    private String JDBC_URL;

    /**
     * Variabile di tipo stringa contenente il nome utente utilizzato per l'accesso.
     */
    private String USER;
    /**
     * Variabile di tipo stringa contenente la password utilizzata per l'accesso.
     */
    private String PASS;

    /**
     * Variabile {@link java.sql.Statement}, utilizzata per eseguire le query sull'RDBMS connesso.
     */
    private Statement statement;

    /**
     * <p>Costruttore della Classe {@link Driver}.</p>
     * Si occupa di:
     * <ul style="margin-top:0">
     *     <li>
     *         impostare le credenziali di accesso ({@link #USER}, {@link #PASS})
     *     </li>
     *     <li>
     *         impostare i vari indirizzi per effettuare la connessione
     *     </li>
     * </ul>
     *
     * @param RDBMS_URL variabile di tipo stringa che fa riferimento all'indirizzo URL su cui opera il RDBMS con la relativa porta in formato: [indirizzo:porta]
     * @param USER      nome utente necessario per accedere al RDBMS
     * @param PASS      password necessaria per accedere al RDBMS
     * @see #createConnection(String, String, String)
     */
    public Driver(String RDBMS_URL, String USER, String PASS) {
        this.createConnection(RDBMS_URL, USER, PASS);
    }


    /**
     * Costruttore della Classe {@link Driver}.
     * <p>
     * Si occupa di:
     * <ul style="margin-top:0">
     *     <li>
     *         impostare le credenziali di accesso ({@link #USER}, {@link #PASS})
     *     </li>
     *     <li>
     *         impostare i vari indirizzi per effettuare la connessione
     *     </li>
     *     <li>
     *         impostare il nome al sistema di gestione {@link #RDBMS_NAME}
     *     </li>
     * </ul>
     *
     * @param RDBMS_URL  variabile di tipo stringa che fa riferimento all'indirizzo URL su cui opera il RDBMS con la relativa porta in formato: [indirizzo:porta]
     * @param USER       nome utente necessario per accedere al RDBMS
     * @param PASS       password necessaria per accedere al RDBMS
     * @param RDBMS_NAME nome del gestore di DataBase
     */
    public Driver(String RDBMS_URL, String USER, String PASS, String RDBMS_NAME) {
        this.createConnection(RDBMS_URL, USER, PASS);
        this.setRDBMS_NAME(RDBMS_NAME);
    }

    /**
     * <p>Costruttore della Classe {@link Driver}.</p>
     * <p>
     * Si occupa di:
     * <ul style="margin-top:0">
     *     <li>
     *         impostare le credenziali di accesso ({@link #USER}, {@link #PASS})
     *     </li>
     *     <li>
     *         impostare i vari indirizzi per effettuare la connessione
     *     </li>
     *     <li>
     *         impostare il nome al sistema di gestione {@link #RDBMS_NAME}
     *     </li>
     * </ul>
     *
     * <p>
     * <b>NOTE</b><br>
     * Questo costruttore non imposta automaticamente il {@link #statement}.
     * </p>
     *
     * @param mapInformation Variabile di tipo {@link Map} contenente le informazioni del {@link Driver}
     */
    public Driver(Map<String, String> mapInformation) {
        this.USER = mapInformation.get("USER");
        this.PASS = mapInformation.get("PASS");
        this.SUBPROTOCOL = mapInformation.get("SUB_PROTOCOL");
        this.RDBMS_URL = mapInformation.get("RDBMS_URL");
        this.JDBC_URL = mapInformation.get("JDBC_URL");
        this.RDBMS_NAME = mapInformation.get("RDBMS_NAME");
    }
    /**
     * Metodo per verificare l'uguaglianza con il driver passato come parametro
     * @param driver_to_compare {@link Driver} da confrontare
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se entrambi i {@link Driver} possiedono lo stesso {@link #JDBC_URL}
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se i {@link Driver} non possiedono lo stesso {@link #JDBC_URL}
     *     </li>
     * </ul>
     */
    public boolean equals(Driver driver_to_compare)
    {
        return Objects.equals(this.JDBC_URL, driver_to_compare.getJDBC_URL());
    }

    /**
     * Metodo utilizzato per impostare i vari indirizzi per la comunicazione con il sistema di gestione dei database relazionali.
     * Imposta:
     * <ul style="margin-top:0">
     *     <li>
     *         le credenziali di accesso ({@link #USER}, {@link #PASS})
     *     </li>
     *     <li>
     *         {@link #RDBMS_URL}
     *     </li>
     *     <li>
     *         {@link #JDBC_URL}
     *     </li>
     * </ul>
     *
     * @param RDBMS_URL Stringa contenente l'indirizzo dell'RDBMS
     * @param USER      Stringa contenente il nome utente per effettuare l'accesso
     * @param PASS      Stringa contenente la password per effettuare l'accesso
     */
    public void createConnection(String RDBMS_URL, String USER, String PASS) {
        this.USER = USER;
        this.PASS = PASS;

        //la variabile RDBMS_URL necessita della presenza dei "//" all'inizio dell'URL
        if (RDBMS_URL.charAt(0) == '/' && RDBMS_URL.charAt(1) == '/')     //vengono controllati i primi due caratteri per accertarsi che siano presenti o meno
            this.RDBMS_URL = RDBMS_URL;                                   //se sono presenti allora non sono necessarie ulteriori azioni
        else
            this.RDBMS_URL = "//" + RDBMS_URL;                            //se non sono presenti vengono aggiunti

        this.JDBC_URL = SUBPROTOCOL + ":" + this.RDBMS_URL; //infine viene inizializzato l'URL necessario per la comunicazione
    }

    /**
     * Metodo utilizzato per verificare che il {@link Driver} sia raggiungibile.
     * <p>L'indirizzo che viene utilizzato è contenuto nella variabile {@link #JDBC_URL}.</p>
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
        try (
                Connection conn = DriverManager.getConnection(this.JDBC_URL, this.USER, this.PASS) //tento la connessione con le credenziali che sono state passate
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

    private void createStatement() {
        try {
            Connection conn = DriverManager.getConnection(this.JDBC_URL, this.USER, this.PASS);
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
     *         la connessione all'indirizzo {@link #JDBC_URL} è chiusa.
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
     * Metodo utilizzato per verificare se un {@link Database} è già presente all'interno dell'sistema di gestione dei database relazionali.
     *
     * @param database_name Stringa contenente il nome del {@link Database}
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui esiste già un {@link Database} con lo stesso nome.
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui non esiste un {@link Database} con lo stesso nome.
     *     </li>
     * </ul>
     */
    public boolean checkIfDatabaseExists(String database_name) {
        this.checkStatement();
        try {
            ResultSet rs = this.statement.executeQuery("SHOW DATABASES LIKE '" + database_name + "'");
            return rs.next(); //ritorna true se ha dei dati all'interno altrimenti restituisce false
        } catch (SQLException e) {
            e.printStackTrace();
            return false; //per lasciare che il programma prosegua e dia errore nella riga successiva
        }
    }

    /**
     * Metodo utilizzato per creare il {@link Database}.
     *
     * @param name Stringa contenente il nome del {@link Database}
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
    public boolean createDatabase(String name) {
        this.checkStatement();
        try {
            this.statement.executeUpdate("CREATE DATABASE " + name);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Metodo utilizzato per la rimozione del {@link Database} dal sistema di gestione dei database relazionali.
     *
     * @param name Stringa contenente il nome del {@link Database} da rimuovere
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui la rimozione ha esito positivo
     *         <p><i>(in questo caso la connessione viene chiusa)</i></p>
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui la rimozione ha esito negativo.
     *     </li>
     * </ul>
     */
    public boolean removeDatabase(String name) {
        this.checkStatement();
        try {
            this.statement.executeUpdate("DROP DATABASE " + name);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Metodo utilizzato per chiudere la connessione con il sistema di gestione dei database relazionali.
     */
    public void closeConnection() {
        if (this.statement == null)
            return;
        try {
            this.statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Metodo utilizzato per impostare il nome del sistema di gestione dei database relazionali.
     *
     * @param name Stringa contenente il nome del {@link Database}
     * @see #RDBMS_NAME
     */
    public void setRDBMS_NAME(String name) {
        this.RDBMS_NAME = name;
    }

    // Metodi get per il ritorno delle variabili utilizzate

    /**
     * Metodo utilizzato per ottenere il nome del sistema di gestione dei database relazionali.
     *
     * @return {@link #RDBMS_NAME}
     */
    public String getRDBMS_NAME() {
        return this.RDBMS_NAME;
    }

    /**
     * Metodo utilizzato per ottenere il nome utente utilizzato per la connessione al sistema di gestione dei database relazionali.
     *
     * @return {@link #USER}
     */
    public String getUSER() {
        return this.USER;
    }

    /**
     * Metodo utilizzato per ottenere la password utilizzata per la connessione al sistema di gestione dei database relazionali.
     *
     * @return {@link #PASS}
     */
    public String getPASS() {
        return this.PASS;
    }

    /**
     * Metodo utilizzato per ottenere il protocollo utilizzato per la connessione al sistema di gestione dei database relazionali.
     *
     * @return {@link #SUBPROTOCOL}
     */
    public String getSUBPROTOCOL() {
        return SUBPROTOCOL;
    }

    /**
     * Metodo utilizzato per ottenere l'indirizzo del sistema di gestione dei database relazionali utilizzato.
     *
     * @return {@link #RDBMS_URL}
     */
    public String getRDBMS_URL() {
        return RDBMS_URL;
    }

    /**
     * Metodo utilizzato per ottenere l'indirizzo che viene usato nel metodo {@link DriverManager#getConnection(String)}.
     *
     * @return {@link #JDBC_URL}
     */
    public String getJDBC_URL() {
        return JDBC_URL;
    }

    /**
     * Metodo utilizzato per ottenere tutti i {@link Database} presenti sul sistema di gestione dei database relazionali.
     *
     * @return {@link DatabaseList} contenente i {@link Database}
     */
    public DatabaseList getAllDatabases() {
        this.checkStatement();
        ResultSet rs;
        DatabaseList db_list = new DatabaseList();
        try {
            rs = this.statement.executeQuery("SHOW DATABASES");

            while (rs.next()) {
                Database db = new Database(this, rs.getString(1));
                if (db.isUsable())
                    db_list.add(db);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return db_list;
    }


    /**
     * <p>Metodo utilizzato per ottenere il valore corrispondente al percorso assoluto della cartella relativa relativo al sistema di gestione dei database relazionali.</p>
     * <p>
     * Il percorso in questione riporta agli applicativi:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@code mysql.exe}, utile per eseguire il source dei file *.sql
     *     </li>
     *     <li>
     *         {@code mysqldump.exe}, utile per eseguire le esportazioni
     *     </li>
     *     <li>
     *         {@code mysqlimport.exe}, utile per le importazioni
     *     </li>
     * </ul>
     *
     * @return Stringa contenente il percorso assoluto del sistema di gestione dei database relazionali
     */
    public String getBaseDir() {
        this.checkStatement();
        String basedir;
        try {
            ResultSet rs = this.statement.executeQuery("SHOW VARIABLES WHERE Variable_Name like 'basedir';");
            rs.next();
            basedir = rs.getString(2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return basedir;
    }

    /**
     * <p>Metodo utilizzato per ottenere il valore corrispondente alla variabile <b><i>secure_file_priv</i></b>, utilizzata nelle operazioni d'importazioni ed esportazioni.</p>
     * <p>
     * Possibili valori che può assumere tale variabile:
     * <ul style="margin-top:0px">
     *     <li>
     *         {@code ""}, le operazioni di importazione ed esportazione sono senza restrizioni
     *     </li>
     *     <li>
     *         {@code <path>}, le operazioni di importazione ed esportazione sono limitate nella cartella di riferimento
     *     </li>
     *     <li>
     *         {@code null}, il sistema di gestione dei database relazionali non permette importazioni ed esportazioni
     *     </li>
     * </ul>
     *
     * @return Stringa contenente il valore della variabile <b><i>secure_file_priv</i></b>
     */
    public String getSecureFilePriv() {
        this.checkStatement();
        String secure_file_priv;
        try {
            ResultSet rs = this.statement.executeQuery("SHOW VARIABLES WHERE Variable_Name like 'secure_file_priv';");
            rs.next();
            secure_file_priv = rs.getString(2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return secure_file_priv;
    }

    /**
     * <p>Metodo utilizzato per ottenere le informazioni del {@link Driver} sotto forma di {@link Map}.</p>
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
     *              USER:
     *         </td>
     *         <td>
     *              {@link #USER}
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>
     *              PASS:
     *         </td>
     *         <td>
     *             {@link #PASS}
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>
     *              USER:
     *         </td>
     *         <td>
     *              {@link #SUBPROTOCOL}
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>
     *              PASS:
     *         </td>
     *         <td>
     *             {@link #RDBMS_URL}
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>
     *              USER:
     *         </td>
     *         <td>
     *              {@link #JDBC_URL}
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>
     *              PASS:
     *         </td>
     *         <td>
     *             {@link #RDBMS_NAME}
     *         </td>
     *     </tr>
     * </table>
     *
     * @return {@link Map}
     */
    public Map<String, String> getInformation() {
        Map<String, String> info = new HashMap<>();
        info.put("USER", this.USER);
        info.put("PASS", this.PASS);
        info.put("SUB_PROTOCOL", this.SUBPROTOCOL);
        info.put("RDBMS_URL", this.RDBMS_URL);
        info.put("JDBC_URL", this.JDBC_URL);
        info.put("RDBMS_NAME", this.RDBMS_NAME);
        return info;
    }

}
