package nl.didactor.taglib;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.ResourceWatcher;
import javax.servlet.jsp.PageContext;
import javax.servlet.Servlet;
import java.util.*;
import java.io.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class implements a translation table. This table contains
 * translations for a namespace with different locales.
 * It reads the files in the translation path, and parses them. Based on the 
 * filenames, the namespace and locale are interpreted. For example:
 * <ul>
 *  <li> core.properties, contains the base translations for the 'core' namespace.
 *  <li> core.nl.properties, contains the translations for the 'core' namespace in the dutch locale.
 *  <li> core.nl_eo.properties, contains the translations for the 'core' namespace in the dutch locale with 'eo' specialization.
 * </ul>
 * If a translation cannot be found in a specialization, it's parent will
 * be queried instead, going to the root.
 * <p>
 * The translationtable will walk the current directory and
 * read all files found in it. 
 * @version $Id: TranslateTable.java,v 1.11 2007-05-10 15:14:00 michiel Exp $
 */
public class TranslateTable {
    private static final Logger log = Logging.getLoggerInstance(TranslateTable.class);
    private static final Map<String, String>  translationTable = Collections.synchronizedMap(new HashMap<String, String>());
    private static boolean initialized = false;
    private static TranslationFileWatcher watcher;
    private String translationlocale;

    /**
     * Inner class that watches the translation files and 
     * reloads them into the translation table in case they are
     * changed 
     */
    static class TranslationFileWatcher extends ResourceWatcher { 
        public TranslationFileWatcher(ResourceLoader rl) { 
            super(rl); 
        } 
        
        /**
         * Change event. Read the file and process it.
         */
        public void onChange(String resource) { 
            readResource(resourceLoader, resource);
        } 
    } 

    /**
     * Initialize the entire Translation Table. This may only be done
     * once. The method is synchronized to prevent concurrent thread
     * to accessing it simultaniously.
     */
    public static synchronized void init() {
        if (initialized) {
            return;
        }
        ResourceLoader loader =  ResourceLoader.getConfigurationRoot().getChildResourceLoader("translations");
        watcher = new TranslationFileWatcher(loader);
        watcher.setDelay(10 * 1000);
        addResources(watcher);

        watcher.start();
        initialized = true;
    }

    /**
     * Read a given directory and add all files to the filewatcher
     */
    private static void addResources(TranslationFileWatcher watcher) {
        Set<String> subs =  watcher.getResourceLoader().getResourcePaths(java.util.regex.Pattern.compile(".*\\.properties"), false);
        for (String sub : subs) {
            readResource(watcher.getResourceLoader(), sub);
            watcher.add(sub);
        }
    }

    /**
     * Read a file into the inner data structures.
     * @param file the file to read
     */
    protected static synchronized void readResource(ResourceLoader loader, String resource) {
        // filename has the form: namespace.locale.properties
        StringTokenizer st = new StringTokenizer(resource, ".");
        String namespace = st.nextToken();

        // If there is no '.' in the filename then it's not a valid translation file
        if (!st.hasMoreTokens()) {
            return;
        }
        String locale = st.nextToken();
        String properties = "";
        if (st.hasMoreTokens()) {
            properties = st.nextToken();
        } else {
            properties = locale;
            locale = "";
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(resource), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }

                int equalsign = line.indexOf("=");
                if (equalsign == -1) {
                    continue;
                }

                String key = line.substring(0, equalsign).trim();
                String value = line.substring(equalsign + 1, line.length()).trim();

                String fkey = namespace;
                if (!"".equals(locale)) {
                    fkey += "." + locale;
                }
                fkey += "." + key;

                if (translationTable.containsKey(fkey)) {
                    translationTable.remove(fkey);
                    log.debug("removed previous definition for '" + fkey + "'");
                }
                translationTable.put(fkey, value);
                log.debug("added translation value for '" + fkey + "': '" + value + "'");
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
    }

    /**
     * Sync the entire translation tables to disk. This is done by iterating
     * over the (sorted) set of translation keys, and parsing them into component,
     * locale and messagekey. The file for this component+locale is emptied, the new
     * keys are saved to that file. During this process the file is unsubscribed from 
     * the filewatcher, to make sure that there are no re-reads of the translation table
     * when we are writing it to disk.
     */
    public static void save() {
        Map<String, PrintWriter> seenFiles = new HashMap<String, PrintWriter>();
        synchronized(translationTable) {
            TreeSet<String> ts = new TreeSet<String>(translationTable.keySet());
            String previousFilename = "";
            PrintWriter out = null;
            for (String key : ts) {
                StringTokenizer st = new StringTokenizer(key, ".");
                String component = st.nextToken();
                String locale = st.nextToken();
                String filename = "";
                String completekey = key;

                // There are two options: component.keyname and component.locale.keyname
                if (st.hasMoreTokens()) {
                    key = st.nextToken();
                    filename = component + "." + locale + ".properties";
                } else {
                    key = locale;
                    locale = "";
                    filename = component + ".properties";
                }

                // If we were not already writing to this filename, we have to open up
                // the new file (and close the previous one)
                if (!filename.equals(previousFilename)) {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    try {

                        // If we have written to this file before, we must append to it!
                        out = seenFiles.get(filename);
                        if (out == null) {
                            // New file, remove it from the filewatcher (so we are sure that there is nobody
                            // reading the file when we are writing it.
                            watcher.remove(filename);
                            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(watcher.getResourceLoader().createResourceAsStream(filename), "UTF-8")));
                            seenFiles.put(filename, out);
                        }
                    } catch (IOException e) {
                        log.error("Exception while trying to write resource '" +  watcher.getResourceLoader() + " " + filename + "': " + e, e);
                    }
                }
                previousFilename = filename;
                out.println(key + "=" + translationTable.get(completekey));
            }
            for (PrintWriter o : seenFiles.values()) {
                o.flush();
                o.close();
            }
        }

        // Final step: add the files to the filewatcher again
        for (String fn : seenFiles.keySet()) {
            watcher.add(fn);
        }
    }
    
    /**
     * Public constructor, it initializes the internal data structures.
     */
    public TranslateTable(String translationlocale) {
        this.translationlocale = translationlocale;
    }
   
    /**
     * Fetch a translation from the translation tables.
     */
    public String translate(String tkey) {
        log.debug("translate('" + tkey + "')");
        String locale = translationlocale;
        StringTokenizer st = new StringTokenizer(tkey, ".");
        String namespace = st.nextToken();
        if (!st.hasMoreTokens()) {
            log.error("Cannot translate key with no namespace: '" + tkey + "'");
            return null;
        }
        String key = st.nextToken();

        while (true) {
            String gkey = namespace;
            if (locale != null && !"".equals(locale)) {
                gkey += "." + locale;
            }
            gkey += "." + key;
            log.debug("Looking for translation for [" + gkey + "]");
            if (translationTable.containsKey(gkey)) {
                return (String)translationTable.get(gkey);
            } else {
                if (locale == null || "".equals(locale)) {
                    return null;
                }
            }
            if (locale.lastIndexOf("_") > -1) {
                locale = locale.substring(0, locale.lastIndexOf("_"));
            } else {
                locale = "";
            }
        }
    }

    /**
     * Set a new translation value
     */
    public static void changeTranslation(String tkey, String locale, String newvalue) {
        synchronized(translationTable) {
            StringTokenizer st = new StringTokenizer(tkey, ".");
            String namespace = st.nextToken();
            String key = st.nextToken();

            String gkey = namespace;
            if (locale != null && !"".equals(locale)) {
                gkey += "." + locale;
            }
            gkey += "." + key;
            if (translationTable.containsKey(gkey)) {
                translationTable.remove(gkey);
            }
            translationTable.put(gkey, newvalue);
        }
    }
}
