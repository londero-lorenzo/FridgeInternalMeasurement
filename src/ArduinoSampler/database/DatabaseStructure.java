package ArduinoSampler.database;

import java.sql.*;
import java.util.*;


/**
 * <p>Classe DataStructure, utilizzata per ottenere le informazioni riguardanti la struttura SQL del Database.</p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #findTable(ResearchParameters...)}
 *     </li>
 *     <li>
 *         {@link #getTableName()}
 *     </li>
 *     <li>
 *         {@link #getTemperatureColumnName()}
 *     </li>
 *     <li>
 *         {@link #getTupleValuesName()}
 *     </li>
 * </ul>
 */

public class DatabaseStructure {

    /**
     * Variabile di tipo stringa che assumerà il nome della colonna relativa al tempo.
     */
    private String timeColumnName;

    /**
     * Variabile di tipo stringa che assumerà il nome della colonna relativa alla temperatura.
     */
    private String temperatureColumnName;


    private String humidityColumnName;

    /**
     * Variabile di tipo stringa che assumerà il nome della tabella in cui verranno salvati i dati.
     */
    private String tableName;

    /**
     * {@link Database} a cui fa riferimento questa struttura dati.
     */
    private final Database database;


    /**
     * Costruttore della Classe {@link DatabaseStructure}, si occupa d'inizializzare il {@link #database}.
     *
     * @param database {@link Database} a cui fa riferimento
     */
    public DatabaseStructure(Database database) {
        this.database = database;
    }

    /**
     * Metodo utilizzato per trovare la tabella in cui salvare i dati.
     *
     * @param parameters lista di {@link ResearchParameters} utilizzati per filtrare la tabella voluta
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se viene trovata la tabella che corrisponde ai parametri di ricerca
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se non viene trovata alcuna tabella
     *     </li>
     * </ul>
     * @see ResearchParameters
     * @see DatabaseTableFinder
     */
    public boolean findTable(ResearchParameters... parameters) {
        DatabaseTableFinder databaseTableFinder = new DatabaseTableFinder(this.database);
        this.tableName = databaseTableFinder.findTableName(parameters);
        this.timeColumnName = databaseTableFinder.getTimeColumnName();
        return tableName != null;
    }


    /**
     * Metodo utilizzato per ottenere il nome della tabella in cui salvare i dati.
     *
     * @return {@link #tableName}
     */
    public String getTableName() {
        return this.tableName;
    }


    /**
     * Metodo utilizzato per inizializzare la variabile {@link #temperatureColumnName}
     *
     * @param temperatureColumnName variabile String contenente il nome della colonna
     */
    public void setTemperatureColumnName(String temperatureColumnName) {
        this.temperatureColumnName = temperatureColumnName;
    }


    public void setHumidityColumnName(String humidityColumnName)
    {
        this.humidityColumnName = humidityColumnName;
    }
    /**
     * Metodo utilizzato per ottenere il nome della colonna riferita alla temperatura
     *
     * @return {@link #temperatureColumnName}
     */
    public String getTemperatureColumnName() {
        return this.temperatureColumnName;
    }

    public String getHumidityColumnName()
    {
        return this.humidityColumnName;
    }


    /**
     * Metodo utilizzato per ottenere i nomi relativi alle tabelle.
     * <p>viene utilizzato nel metodo {@link Database#insert()}.</p>
     *
     * @return Stringa, struttura:
     * <code>({@link #timeColumnName}, {@link #temperatureColumnName}</code>
     */
    public String getTupleValuesName() {
        return " (" + this.timeColumnName + ", " + this.temperatureColumnName + ") ";
    }
}


/**
 * <p>Classe DatabaseTableFinder, utilizzata per cercare e trovare una tabella specifica, attraverso {@link ResearchParameters}, all'interno di un {@link Database}.</p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #findTableName(ResearchParameters...)}
 *     </li>
 *     <li>
 *         {@link #getTimeColumnName()}
 *     </li>
 * </ul>
 */
class DatabaseTableFinder {

    /**
     * {@link Database} in cui effettuare la ricerca.
     */
    private final Database database;

    /**
     * Stringa contenente il nome della tabella trovata.
     */
    private String tableName;

    /**
     * Stringa contenente il nome della colonna relativa al tempo all'interno della tabella trovata.
     */
    private String timeColumnName;

    /**
     * Costruttore della Classe {@link DatabaseTableFinder}, inizializza il {@link #database}.
     *
     * @param database {@link Database} in cui effettuare la ricerca
     */
    public DatabaseTableFinder(Database database) {
        this.database = database;
    }

    /**
     * Metodo utilizzato per verificare che ogni parametro di ricerca è stato soddisfatto.
     *
     * @param researchParameters {@link ResearchParameters}, parametro di ricerca
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se ogni parametro di ricerca è stato rispettato
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se almeno un parametro di ricerca non è stato rispettato
     *     </li>
     * </ul>
     */
    private boolean checkResults(ResearchParameters... researchParameters) {
        boolean output = true;
        for (ResearchParameters parameters : researchParameters) {
            output &= parameters.hasResult();
        }
        return output;
    }

    /**
     * Metodo utilizzato per ottenere tutte le informazioni riguardanti la struttura SQL del {@link Database}.
     * <p>Il funzionamento si divide in:</p>
     * <br>
     * <h2 style="margin:0">
     * Ricezione Tabelle
     * </h2>
     * <p>
     * Tramite il metodo {@link Database#getStatement()} viene ottenuto lo {@link java.sql.Statement} relativo al {@link Database} in questione, grazie al quale viene interrogato il sistema di gestione con la query:<br>
     * {@code SHOW TABLES;}
     * </p>
     * <h2 style="margin:0">
     * Elaborazione Tabelle
     * </h2>
     * <p>
     * La precedente query farà in modo che il sistema di gestione risponda con una tabella contenente tutti i nomi delle varie tabelle contenute all'interno del {@link Database}.<br>
     * Per ogni riga della tabella di risposta, viene preparata un altra query:<br>
     * <code>DESCRIBE {@link Database#getName()}.[nome_tabella];></code>
     * </p>
     * <h2 style="margin:0">
     * Controllo Parametri
     * </h2>
     *
     * <p>Grazie alla seconda query, invece è invece possibile ottenere:</p>
     * <ul style="margin-top:0px">
     *     <li>
     *         Nome della variabile
     *     </li>
     *     <li>
     *         Tipologia della variabile
     *     </li>
     * </ul>
     * Dalle informazioni ottenute è possible verificare se la tabella selezionata presenta le caratteristiche per poter ospitare i dati che si intendono salvare (tempo, temperatura).
     *
     * @see #isSimilar(String, String)
     */
    public String findTableName(ResearchParameters... parameters) {

        try (PreparedStatement preparedStatementTable = this.database.getStatement().getConnection().prepareStatement("SHOW TABLES;")) {
            ResultSet tables = preparedStatementTable.executeQuery();
            while (tables.next()) {
                checkTableUsability(tables, parameters);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this.tableName;
    }


    /**
     * Sottometodo di {@link #findTableName(ResearchParameters...)}, si occupa di determinare se una tabella può essere utilizzata.
     *
     * @param tables     {@link ResultSet} contenente le informazioni relative a una tabella
     * @param parameters {@link ResearchParameters}, parametri di ricerca
     * @throws SQLException <ul>
     *                      <li>
     *                      se insorgono degli errori durante la comunicazione con il RDMBS
     *                      </li>
     *                      </ul>
     */
    private void checkTableUsability(ResultSet tables, ResearchParameters... parameters) throws SQLException {
        String table_name = tables.getString(1);
        PreparedStatement preparedStatementDescribe = this.database.getStatement().getConnection().prepareStatement("DESCRIBE " + this.database.getName() + "." + table_name + " ;");
        ResultSet describe = preparedStatementDescribe.executeQuery();
        boolean tableUsable = true;
        while (describe.next()) {
            String field = describe.getString(1);
            String type = describe.getString(2);
            String key = describe.getString(4);
            String extra = describe.getString(6);
            tableUsable &= isVariableMatched(table_name, field, type, key, extra, parameters);
        }
        tableUsable &= checkResults(parameters);
        this.timeColumnName = (tableUsable) ? this.timeColumnName : null;
        this.tableName = (tableUsable) ? this.tableName : null;
    }

    /**
     * Sottometodo di {@link #checkTableUsability(ResultSet, ResearchParameters...)}, si occupa di verificare se una variabile all'interno della tabella rispetta il parametro di ricerca definito da {@link ResearchParameters}.
     *
     * @param table_name Stringa contenente il nome della tabella
     * @param field      Stringa contenente il nome della variabile
     * @param type       Stringa contenente il tipo della variabile
     * @param key        Stringa contenente le informazioni riguardanti le chiavi primarie
     * @param extra      Stringa contenente parametri extra della variabile
     * @param parameters {@link ResearchParameters}, parametri di ricerca
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se la variabile selezionata rispetta un parametro di ricerca
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se la variabile selezionata non rispetta nessun parametro di ricerca
     *     </li>
     * </ul>
     */
    private boolean isVariableMatched(String table_name, String field, String type, String key, String extra, ResearchParameters... parameters) {
        if (Objects.equals(type, "int") && Objects.equals(key, "PRI") && Objects.equals(extra, "auto_increment"))
            return true;
        else if (Objects.equals(type, "float") || Objects.equals(type, "double")) {
            for (ResearchParameters researchParameters : parameters) {
                for (String default_field : researchParameters.getParameters()) {
                    if (this.isSimilar(field, default_field)) {
                        researchParameters.setColumnName(field);
                        return true;
                    }
                }
            }
        } else if (Objects.equals(type, "datetime")) {
            this.timeColumnName = field;
            this.tableName = table_name;
            return true;
        }
        return false;
    }

    /**
     * Metodo realizzato per generare una {@link Map} contenente:
     *     <table>
     *         <tr>
     *             <th>
     *                 Chiave
     *             </th>
     *             <th>
     *                 Valore
     *             </th>
     *         </tr>
     *         <tr>
     *             <td>
     *                  [{@code carattere}]:
     *             </td>
     *             <td>
     *                  [{@code numero_ripetizioni}]
     *             </td>
     *         </tr>
     *     </table><br>
     * es. <br>
     * Stringa presa in esame: <br>{@code Hello world!}
     *     <table>
     *         <tr>
     *             <th>
     *                 Chiave
     *             </th>
     *             <th>
     *                 Valore
     *             </th>
     *         </tr>
     *         <tr>
     *             <td>
     *                  [{@code h}]:
     *             </td>
     *             <td>
     *                  [{@code 1}]
     *             </td>
     *         </tr>
     *         <tr>
     *             <td>
     *                  [{@code e}]:
     *             </td>
     *             <td>
     *                  [{@code 1}]
     *             </td>
     *         </tr>
     *         <tr>
     *             <td>
     *                  [{@code l}]:
     *             </td>
     *             <td>
     *                  [{@code 3}]
     *             </td>
     *         </tr>
     *         <tr>
     *             <td>
     *                  [{@code o}]:
     *             </td>
     *             <td>
     *                  [{@code 2}]
     *             </td>
     *         </tr>
     *         <tr>
     *             <td>
     *                  [{@code w}]:
     *             </td>
     *             <td>
     *                  [{@code 1}]
     *             </td>
     *         </tr>
     *         <tr>
     *             <td>
     *                  [{@code r}]:
     *             </td>
     *             <td>
     *                  [{@code 1}]
     *             </td>
     *         </tr>
     *         <tr>
     *             <td>
     *                  [{@code d}]:
     *             </td>
     *             <td>
     *                  [{@code 1}]
     *             </td>
     *         </tr>
     *     </table>
     *
     * @param str Stringa con la quale generare la {@link Map} (verrà convertita tutta in minuscolo)
     * @return {@link Map}
     */
    private Map<Character, Integer> generateCharMap(String str) {
        Map<Character, Integer> map = new HashMap<>();
        Integer currentChar;
        for (char c : str.toCharArray()) {
            currentChar = map.get(c);
            if (currentChar == null) {
                map.put(c, 1);
            } else {
                map.put(c, currentChar + 1);
            }
        }
        return map;
    }

    /**
     * Metodo realizzato per comparare due stringhe prese in esame.
     *
     * @param str        prima stringa da prendere in esame
     * @param compareStr seconda stringa da prendere in esame
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui il numero di caratteri simili supera il parametro <b>{@code THRESHOLD}</b> espresso in percentuale riferito alla stringa più lunga
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         nel caso in cui il numero di caratteri simili non supera il parametro <b>{@code THRESHOLD}</b>
     *     </li>
     * </ul>
     */
    private boolean isSimilar(String str, String compareStr) {
        float THRESHOLD = 0.60f; //soglia che identifica il numero di caratteri per determinare se la stringa è simile o meno (60%)
        Map<Character, Integer> strMap = this.generateCharMap(str.toLowerCase());
        Map<Character, Integer> compareStrMap = this.generateCharMap(compareStr.toLowerCase());
        Set<Character> charSet = compareStrMap.keySet();
        int similar_chars = 0;
        int total_strChars = str.length();
        if (total_strChars < compareStrMap.size())
            total_strChars = compareStr.length();
        float thisThreshold;
        Iterator<Character> it = charSet.iterator();
        char currentChar;
        Integer currentCountStrMap;
        Integer currentCountCompareStrMap;
        while (it.hasNext()) {
            currentChar = it.next();
            currentCountStrMap = strMap.get(currentChar);
            if (currentCountStrMap != null) {
                currentCountCompareStrMap = compareStrMap.get(currentChar);
                if (currentCountCompareStrMap >= currentCountStrMap) {
                    similar_chars += currentCountStrMap;
                } else {
                    similar_chars += currentCountCompareStrMap;
                }
            }
        }
        thisThreshold = ((float) similar_chars) / ((float) total_strChars);
        return thisThreshold > THRESHOLD;
    }

    /**
     * Metodo utilizzato per ottenere il nome della colonna relativo al tempo
     *
     * @return {@link #timeColumnName}
     */
    public String getTimeColumnName() {
        return this.timeColumnName;
    }
}

/**
 * <p>Classe ResearchParameters, utilizzata per creare i parametri di ricerca per trovare una specifica variabile all'interno di una database tabella situata in un {@link Database}.
 * Lo scopo di questa Classe è quello d'identificare la posizione nella quale salvare i dati ricevuti su un {@link Database} nella colonna corretta.
 *
 * </p>
 * Metodi principali:
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #add(String)}
 *     </li>
 *     <li>
 *         {@link #hasResult()}
 *     </li>
 *     <li>
 *         {@link #setColumnName(String)}
 *     </li>
 *     <li>
 *         {@link #getColumnName()}
 *     </li>
 *     <li>
 *         {@link #getParameters()}
 *     </li>
 * </ul>
 */
class ResearchParameters {
    /**
     * Lista di stringhe che identificano i parametri di ricerca.
     * <p>
     * I parametri sono delle stringhe che vengono confrontate con il nome delle colonne contenute nelle varie tabelle, per ogni stringa viene verificato se è simile al nome della colonna selezionata.
     * </p>
     * <p>
     * Es:
     * String[] parameters = new String[]{"temperature", "temp", "t"};
     * <pre>
     *
     * _______ _____________ __________
     * | index | temperature | humidity |
     * |-------|-------------|----------|
     * |  ...  |    ...      |   ...    |
     *
     * -----------------------------------------
     * isSimilar("index","temperature") -> false
     * isSimilar("index","temp") -> false
     * isSimilar("index","t") -> false
     * -----------------------------------------
     * isSimilar("temperature","temperature") -> true <b>MATCHED</b>
     * -----------------------------------------
     * </pre>
     */
    private String[] parameters = new String[0];

    /**
     * Stringa contenente il nome della colonna trovata tramite la ricerca.
     */
    private String columnName;

    /**
     * Variabile booleana che indica lo stato di ricerca.
     */
    private boolean found = false;

    /**
     * Costruttore della Classe {@link ResearchParameters}, aggiunge le stringhe passate come parametro ai parametri di ricerca.
     *
     * @param parameters Lista di stringhe identificabili come parametri di ricerca
     */
    public ResearchParameters(String... parameters) {
        for (String parameter : parameters)
            this.add(parameter);
    }

    /**
     * Metodo utilizzato per aggiungere i parametri di ricerca all'interno di {@link #parameters}.
     *
     * @param parameter Stringa contenete il valore corrispondente al parametro di ricerca
     */
    public void add(String parameter) {
        int noParameters = this.parameters.length;
        String[] newParameters = new String[noParameters + 1];
        System.arraycopy(this.parameters, 0, newParameters, 0, noParameters);
        newParameters[noParameters] = parameter;
        this.parameters = newParameters;
    }

    /**
     * Metodo utilizzato per ottenere lo stato della ricerca.
     *
     * @return {@code true} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se è stata trovata la variabile che rispetta i parametri di ricerca
     *     </li>
     * </ul>
     * {@code false} <br>
     * <ul style="margin-top:0">
     *     <li>
     *         se non è ancora stata trovata la variabile che rispetta i parametri di ricerca
     *     </li>
     * </ul>
     */
    public boolean hasResult() {
        return this.found;
    }

    /**
     * Metodo utilizzato per inizializzare la variabile {@link #columnName}, viene quindi trovata la variabile cercata.
     *
     * @param columnName Stringa contenente il nome della colonna ricercata
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
        this.found = true;
    }

    /**
     * Metodo utilizzato per ottenere il nome della colonna nella quale salvare i dati.
     *
     * @return {@link #columnName}
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Metodo utilizzato per ottenere i parametri di ricerca.
     *
     * @return {@link #parameters}
     */
    public String[] getParameters() {
        return parameters;
    }
}
