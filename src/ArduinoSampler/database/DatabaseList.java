package ArduinoSampler.database;

import java.util.Objects;

import ArduinoSampler.file_managers.DataArchiver;
import ArduinoSampler.interfaces.ListHandler;
import ArduinoSampler.interfaces.DatabaseElement;

/**
 * <p>Classe CollectorList utilizzata per gestire l'insieme delle classi {@link Database}.</p>
 * <p>
 * Implementa l'interfaccia {@link ListHandler}.
 * </p>
 * Funziona prevalentemente come un Array:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #add(DatabaseElement)}
 *     </li>
 *     <li>
 *         {@link #addFromRawInformation(String)}
 *     </li>
 *     <li>
 *         {@link #remove(DatabaseElement)}
 *     </li>
 *     <li>
 *         {@link #removeFromJDBC_URL(String)}
 *     </li>
 *     <li>
 *         {@link #setDataArchiver(DataArchiver)}
 *     </li>
 *     <li>
 *         {@link #save()}
 *     </li>
 *     <li>
 *         {@link #isEmpty()}
 *     </li>
 *     <li>
 *         {@link #getDatabases()}
 *     </li>
 *     <li>
 *         {@link #getDatabase(int)}
 *     </li>
 *     <li>
 *         {@link #getDatabasesNames()}
 *     </li>
 *     <li>
 *         {@link #getDatabaseByName(String)}
 *     </li>
 *     <li>
 *         {@link #getDatabasesByJDBC_URL(String)}
 *     </li>
 *     <li>
 *         {@link #getDatabasesFromDataArchiver()}
 *     </li>
 * </ul>
 */
public class DatabaseList implements ListHandler {

    /**
     * Array di oggetti {@link Database}
     */
    private Database[] databases = new Database[0];

    /**
     * Oggetto {@link DataArchiver}, utilizzato per effettuare il salvataggio del {@link Database}
     */
    private DataArchiver databaseArchiver;

    /**
     * Metodo utilizzato per aggiungere {@link Database} all'array {@link #databases}.
     *
     * @param element {@link Database}
     */
    public void add(DatabaseElement element) {
        Database database = (Database) element;
        int noDatabases = this.databases.length;
        Database[] newDatabases = new Database[noDatabases + 1];
        System.arraycopy(this.databases, 0, newDatabases, 0, noDatabases);
        newDatabases[noDatabases] = database;
        this.databases = newDatabases;
    }


    /**
     * Metodo utilizzato per aggiungere un {@link Database} realizzato dalle informazioni salvate.
     *
     * @param rawInformation Stringa contenente le informazioni grezze lette dal file di salvataggio
     * @see #fromStringToMap(String)
     */
    public void addFromRawInformation(String rawInformation) {
        this.add(new Database(this.fromStringToMap(rawInformation)));
    }

    /**
     * Metodo utilizzato per rimuovere {@link Database} dall'array {@link #databases}.
     *
     * @param element {@link Database}
     */
    public void remove(DatabaseElement element) {
        Database databaseToRemove = (Database) element;
        Database[] newDatabases = new Database[this.databases.length];
        int counter = 0;
        for (Database currentDatabase : this.databases) {
            if (!currentDatabase.equals(databaseToRemove)) {
                newDatabases[counter] = currentDatabase;
                counter++;
            }
        }
        Database[] newDatabasesResized = new Database[counter];
        System.arraycopy(newDatabases, 0, newDatabasesResized, 0, counter);
        this.databases = newDatabasesResized;
    }

    /**
     * Metodo utilizzato per rimuovere {@link Database} dall'array {@link #databases} tramite JDBC_URL.
     *
     * @param JDBC_URL indirizzo principale del {@link Driver}
     * @see Database#getJDBC_URL()
     */
    public void removeFromJDBC_URL(String JDBC_URL) {
        if (this.databases.length == 0)
            return;
        Database[] newDatabases = new Database[this.databases.length - this.getDatabasesByJDBC_URL(JDBC_URL).getDatabases().length];
        int counter = 0;
        for (Database currentDatabase : this.databases) {
            if (!Objects.equals(currentDatabase.getJDBC_URL(), JDBC_URL)) {
                newDatabases[counter] = currentDatabase;
                counter++;
            }
        }
        Database[] newDatabasesResized = new Database[counter];
        System.arraycopy(newDatabases, 0, newDatabasesResized, 0, counter);
        this.databases = newDatabasesResized;
    }


    /**
     * Metodo utilizzato per impostare il {@link DataArchiver} al {@link Database}.
     *
     * @param dataArchiver {@link DataArchiver} da utilizzare
     */
    public void setDataArchiver(DataArchiver dataArchiver) {
        this.databaseArchiver = dataArchiver;
    }

    /**
     * Metodo utilizzato per salvare ogni {@link Database} contenuto in {@link #databases}.
     * <p>
     * <b>Nota</b><br>
     * Ad ogni richiamo di questa funzione viene sovrascritto il file di salvataggio con le informazioni dei database.<br>
     * Ogni {@link Database} viene salvato su una riga.
     * </p>
     *
     * @see DataArchiver#overrideFile()
     * @see DataArchiver#save(DatabaseElement)
     */

    public void save() {
        this.databaseArchiver.overrideFile();
        for (Database database : this.databases) {
            this.databaseArchiver.save(database, true);
        }
    }

    /**
     * Metodo utilizzato per verificare se {@link #databases} risulta vuoto.
     *
     * @return {@code true} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se non sono presenti {@link Database}
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se sono presenti {@link Database}
     *     </li>
     * </ul>
     */
    public boolean isEmpty() {
        return this.databases.length == 0;
    }

    /**
     * <p>Metodo utilizzato per verificare se {@link #databases} contiene già un {@link Database} che presenta le stesse informazioni di quello passato come parametro.</p>
     * <p>
     * Il confronto tra Database utilizza i seguenti metodi:<br>
     * <ul style="margin-top:0px">
     *     <li>
     *         {@link Database#getName()}
     *     </li>
     *     <li>
     *         {@link Database#getJDBC_URL()}
     *     </li>
     * </ul>
     *
     * @param database {@link Database} da confrontare
     * @return <ul style="margin-top: 0px">
     *     <li>
     *         se è già presente un {@link Database} con le stesse informazioni
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top: 0px">
     *     <li>
     *         se non è presente un {@link Database} con le stesse informazioni
     *     </li>
     * </ul>
     */
    public boolean has(Database database) {
        for (Database currentDatabase : this.databases)
            if (currentDatabase.equals(database))
                return true;
        return false;
    }

    /**
     * Metodo utilizzato per ottenere i vari {@link Database} contenuti in {@link #databases}.
     *
     * @return {@link Database} ({@code list})
     */
    public Database[] getDatabases() {
        return this.databases;
    }

    /**
     * Metodo utilizzato per ottenere tutti i {@link Database} raggiungibili contenuti in {@link #databases}.
     *
     * @return {@link Database} ({@code list})
     */

    public Database[] getReachableDatabases() {
        DatabaseList dbList = new DatabaseList();
        for (Database database : this.databases)
            if (database.testConnection() && database.isUsable())
                dbList.add(database);
        return dbList.getDatabases();
    }

    /**
     * Metodo utilizzato per ottenere il {@link Database}, situato nella posizione corrispondente del parametro passato, contenuto in {@link #databases}.
     *
     * @param index posizione del {@link Database}
     * @return {@link Database}
     */
    public Database getDatabase(int index) {
        return this.databases[index];
    }

    /**
     * Metodo utilizzato per ottenere il nome di tutti i {@link Database} contenuti in {@link #databases}.
     *
     * @return Nomi dei {@link Database} ({@code list})
     */
    public String[] getDatabasesNames() {
        String[] names = new String[this.databases.length];
        for (int i = 0; i < this.databases.length; i++)
            names[i] = this.getDatabase(i).getName();
        return names;
    }

    /**
     * Metodo utilizzato per ottenere il {@link Database} in base al nome passato come parametro
     *
     * @param name Stringa contenente il nome del {@link Database}
     * @return {@link Database}
     * @see Database#getName()
     */
    public Database getDatabaseByName(String name) {
        for (Database database : this.databases)
            if (Objects.equals(database.getName(), name))
                return database;
        return null;
    }

    /**
     * Metodo utilizzato per ottenere i {@link Database} in base al sistema di gestione passato come parametro.
     *
     * @param RDBMS Stringa contenente il nome del sistema di gestione
     * @return Variabile {@link DatabaseList} contenente i database filtrati
     * @see Driver#getRDBMS_NAME()
     */
    public DatabaseList getDatabasesByRDBMS(String RDBMS) {
        DatabaseList dbList = new DatabaseList();
        for (Database database : this.databases)
            if (Objects.equals(database.getDriver().getRDBMS_NAME(), RDBMS))
                dbList.add(database);
        return dbList;
    }

    /**
     * Metodo utilizzato per ottenere i {@link Database} in base all'indirizzo principale del {@link Driver}.
     *
     * @param driverJDBC_URL Stringa contenente l'indirizzo principale del {@link Driver}
     * @return Variabile {@link DatabaseList} contenente i database filtrati
     * @see Driver#getJDBC_URL()
     */
    public DatabaseList getDatabasesByJDBC_URL(String driverJDBC_URL) {
        DatabaseList dbList = new DatabaseList();
        for (Database database : this.databases)
            if (Objects.equals(database.getDriver().getJDBC_URL(), driverJDBC_URL))
                dbList.add(database);
        return dbList;
    }

    /**
     * Metodo utilizzato per inizializzare la variabile {@link #databases} dalle informazioni dei {@link Database} salvati.
     *
     * @see DataArchiver#getElementsSaved()
     */
    public void getDatabasesFromDataArchiver() {
        this.databaseArchiver.loadData();
        this.databases = ((DatabaseList) this.databaseArchiver.getElementsSaved()).getDatabases();
    }

    /**
     * Metodo utilizzato per stampare i nomi dei vari {@link Database} contenuti in {@link #databases}.
     * <p>
     * <b>NOTA</b><br>
     * Utilizzato solo nei test
     * </p>
     */
    public void print_names() {
        StringBuilder output = new StringBuilder();
        for (Database database : this.databases) {
            output.append(database.getName()).append(" ,");
        }
        output.append(";");
        System.out.println(output);
    }

}
