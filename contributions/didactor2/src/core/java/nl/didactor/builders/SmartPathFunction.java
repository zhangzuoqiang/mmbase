package nl.didactor.builders;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.mmbase.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * This builder implements the 'getSmartPath()' method (used by 
 * TreeInclude / Leafinclude) by using values in the fields that
 * are specified in the builder XML file.
 *
 * @author Michiel Meeuwissen
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @since Didactor-2.3
 * @version $Id: SmartPathFunction.java,v 1.3 2007-06-07 21:27:25 michiel Exp $
 */
public class SmartPathFunction extends org.mmbase.module.core.SmartPathFunction {
    protected static Logger log = Logging.getLoggerInstance(SmartPathFunction.class);

    protected final String spFieldNames[];
    protected final String spPathPrefix;

    public SmartPathFunction(MMObjectBuilder parent) {
        super(parent);
        String spFieldName = parent.getInitParameter("smartpathfield");

        if (spFieldName == null || spFieldName.equals("")) {
            throw new RuntimeException("You must specify the 'smartpathfield' property in your <properties> block for this builder " + parent);
        } 
        spFieldNames = spFieldName.split("\\s*,\\s*");

        String p = parent.getInitParameter("pathprefix");
        if (p == null) p = "";
        spPathPrefix = p;
    }

    /**
     */
    public String smartpath() {
        if (log.isDebugEnabled()) {
            log.debug("starting getSmartPath(" + webRoot + "," + path + "," + nodeNumber + "," + version + ")");
        }
        if (spFieldNames == null || spFieldNames.length == 0) {
            return super.smartpath();
        }
        if (log.isDebugEnabled()) {
            log.debug("Path is '" + path + "', smartpath-prefix is '" + spPathPrefix + "'");
        }
        if (path == null) path = "";
        if (!spPathPrefix.equals("") && (path.equals("") || path.equals("/"))) {
            path = spPathPrefix;
        }
        ResourceLoader child = webRoot.getChildResourceLoader(path);

        String n = nodeNumber;
        if (version != null) n += "\\." + version;       
        MMObjectNode node = parent.getNode(n);

        String magName = null;
        if (log.isDebugEnabled()) {
            log.debug("Going to test with fields " + spFieldNames);
        }
        if (node == null) {
            throw new RuntimeException("No node with number " + nodeNumber + " found");
        }

        for (String spFieldName : spFieldNames) {
            magName = (String) node.getValue(spFieldName);
            if (log.isDebugEnabled()) {
                log.debug(spFieldName + " = '" + magName + "'");
            }
            if (magName != null && !"".equals(magName)) {
                log.debug("Got a not-null one! ('" + magName + "')");
                break;
            }
        }

        if (magName == null) {
            return null;
        }

        Set<String> s = child.getChildContexts(Pattern.compile(".*/" + magName), false);

        if (s.size() == 0) {
            log.debug("Not found " + magName + " in " + child);
            return null;
        }

        String result = path + s.iterator().next() + "/";
        if (log.isDebugEnabled()) {
            log.debug("Matching path: " + s + " -> " + result);
        }
        return result;
    }

}
