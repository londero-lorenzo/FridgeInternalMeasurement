package ArduinoSampler.file_managers;

import ArduinoSampler.database.Database;
import ArduinoSampler.database.Driver;
import ArduinoSampler.interfaces.ListHandler;
import ArduinoSampler.interfaces.DatabaseElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Classe DataArchiver, utilizzata per effettuare tutte le operazioni di salvataggio su file delle informazioni relative ai {@link Database} e {@link Driver}.
 * <p>Metodi principali:</p>
 * <ul style="margin-top: 0px">
 *     <li>
 *         {@link #save(DatabaseElement, boolean)}
 *     </li>
 *     <li>
 *         {@link #loadData()}
 *     </li>
 *     <li>
 *         {@link #overrideFile()}
 *     </li>
 *     <li>
 *         {@link #getElementsSaved()}
 *     </li>
 * </ul>
 */
public class DataArchiver {
    /**
     * Variabile di tipo stringa contenente il percorso della cartella in cui effettuare il salvataggio.
     */
    private String folderPath;

    /**
     * Variabile di tipo stringa contenente il nome del file di salvataggio.
     */
    private String fileName;

    /**
     * Variabile di tipo {@link Path} contenente l'intero percorso della cartella in cui effettuare il salvataggio.
     */
    private Path fullPath;


    /**
     * Variabile che fa riferimento alla tipologia d'interfaccia {@link ListHandler}.
     */
    private ListHandler listHandlerType;


    /**
     * Metodo utilizzato per inizializzare:
     * <ul style="margin-top: 0px">
     *     <li>
     *         il file su cui salvare i dati
     *     </li>
     *     <li>
     *         il percorso della cartella dov'è presente il file di salvataggio {@link #folderPath}
     *     </li>
     *     <li>
     *         il nome del file di salvataggio {@link #fileName}
     *     </li>
     *     <li>
     *         il percorso del file su cui salvare {@link #fullPath}
     *     </li>
     *     <li>
     *         viene inizializzata l'interfaccia {@link #listHandlerType} tramite quella passata come parametro
     *     </li>
     * </ul>
     *
     * @param path        cartella in cui effettuare il salvataggio
     * @param fileName    nome del file di salvataggio
     * @param listHandler interfaccia che fa riferimento a {@link ListHandler}
     */
    public DataArchiver(String path, String fileName, ListHandler listHandler) {
        Path folderDataPath = Paths.get(path);
        Path data_path = Paths.get(path, fileName);
        File data_file = data_path.toFile();
        try {
            folderDataPath.toFile().mkdirs();
            data_file.createNewFile();
            this.folderPath = path;
            this.fileName = fileName;
            this.fullPath = data_path;
            this.listHandlerType = listHandler;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo utilizzato per effettuare la scrittura su file.
     *
     * @param element variabile che implementa l'interfaccia {@link DatabaseElement}
     * @param append  variabile booleana utilizzata per indicare se aggiungere in fondo al file il seguente elemento
     * @throws RuntimeException se si verificano problemi di scrittura
     */
    private void write(DatabaseElement element, boolean append) {
        String information = element.getInformation().toString();
        try {
            FileWriter data_writer = new FileWriter(this.fullPath.toFile(), append);
            data_writer.write(information + "\n");
            data_writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.listHandlerType.add(element);
    }


    /**
     * Metodo utilizzato per richiamare la funzione di scrittura {@link #write(DatabaseElement, boolean)}.
     *
     * @param element variabile che implementa l'interfaccia {@link DatabaseElement}
     * @param append  variabile booleana utilizzata per indicare se aggiungere in fondo al file il seguente elemento
     */
    public void save(DatabaseElement element, boolean append) {
        this.write(element, append);
    }


    /**
     * Metodo utilizzato per richiamare la funzione di scrittura {@link #write(DatabaseElement, boolean)}.
     * <p>Ad ogni richiamo viene sovrascritto il file di salvataggio</p>.
     *
     * @param element variabile che implementa l'interfaccia {@link DatabaseElement}
     */
    public void save(DatabaseElement element) {
        this.write(element, false);
    }


    /**
     * Metodo utilizzato:
     * <ul style="margin-top: 0px">
     *     <li>
     *         leggere le informazioni salvate relative al {@link DatabaseElement}.
     *     </li>
     *     <li>
     *         aggiungere il {@link DatabaseElement} all'interno della variabile {@link #listHandlerType}.
     *     </li>
     * </ul>
     *
     * @throws RuntimeException se {@link #fullPath} fa riferimento a un file inesistente
     * @see ListHandler#addFromRawInformation(String)
     */
    public void loadData() {
        try {
            Scanner data_reader = new Scanner(this.fullPath.toFile());
            while (data_reader.hasNext()) {
                String information = data_reader.nextLine();
                this.listHandlerType.addFromRawInformation(information);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo utilizzato per sovrascrivere il file di salvataggio.
     *
     * @throws RuntimeException se si verificano problemi di scrittura
     */
    public void overrideFile() {
        try {
            FileWriter data_writer = new FileWriter(this.fullPath.toFile());
            data_writer.write("");
            data_writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo utilizzato per impostare la variabile {@link #folderPath}.
     *
     * @param new_path variabile di tipo stringa che fa riferimento al nuovo percorso
     */
    public void setFolderPath(String new_path) {
        this.folderPath = new_path;
    }


    /**
     * Metodo utilizzato per ottenere l'interfaccia {@link ListHandler}.
     *
     * @return {@link ListHandler}
     */
    public ListHandler getElementsSaved() {
        return this.listHandlerType;
    }

    /**
     * Metodo utilizzato per ottenere il percorso della cartella in cui è presente il file di salvataggio.
     *
     * @return {@link #folderPath}
     */
    public String getFolderPath() {
        return this.folderPath;
    }

    /**
     * Metodo utilizzato per ottenere il nome del file di salvataggio.
     *
     * @return {@link #fileName}
     */
    public String getFileName() {
        return this.fileName;
    }
}