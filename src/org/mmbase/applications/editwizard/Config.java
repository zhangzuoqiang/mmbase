/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import java.util.Stack;
import java.io.File;
import org.mmbase.util.xml.URIResolver;
import org.mmbase.applications.editwizard.SecurityException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.*;
/**
 * This struct contains configuration information for the jsps. This
 * thing is put in the session. A subclass 'Configurator' can be used
 * to fill this struct.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: Config.java,v 1.6 2002-05-15 12:10:36 michiel Exp $
 */

public class Config {

    // protocol string to test referrer pages
    private final static String PROTOCOL = "http://";

    public String      sessionKey        = null;
    public URIResolver uriResolver       = null;
    public int         list_pagelength   = 50;
    public int         list_maxpagecount = 10;
    public int         maxupload = 4 * 1024 * 1024; // 1 MByte max uploadsize
    public Stack       subObjects = new Stack(); // stores the Lists and Wizards.
    public String      wizard;
    public String      sessionId;   // necessary if client doesn't accept cookies to store sessionid (this is appended to urls)
    public String      backPage;
    
    static public abstract class SubConfig {
        public String page;
    }

    static public class WizardConfig extends SubConfig {
        public Wizard wiz;
        public String objectNumber;
    }
    static public class ListConfig extends SubConfig {
        public File   template;
        public String fields;
        public String startNodes;
        public String nodePath;
        public String constraints;
        public String orderBy;
        public String directions;        
        public int    age = -1;
        public int    start = 0;
        public boolean distinct;
    }

    /**
     * To fill the Config struct, this 'Configurator' exists. You
     * could extend it to change wich query parameters must be used,
     * and what are the defaults and so on.
     */
    public abstract static class Configurator {
        private static Logger log = Logging.getLoggerInstance(Config.class.getName());

        private HttpServletRequest request;
        private HttpServletResponse response;
        private Config config;
        public Configurator(HttpServletRequest req, HttpServletResponse res, Config c) {
            request = req;
            response = res;
            config  = c;
            config.sessionId = res.encodeURL("");
            // The editwizard need to know the 'backpage' (for 'index' and 'logout' links).
            // It can be specified by a 'referrer' parameter. If this is missing the
            // 'Referer' http header is tried.
            if (config.backPage == null) {
                log.debug("No backpage. Getting from parameters");
                config.backPage = getParam("referrer", "");
                if (config.backPage.equals("")) {
                    log.debug("No backpage getting from header");
                    config.backPage = request.getHeader("Referer");
                }
                if (config.backPage == null) {
                    log.debug("No backpage setting to ''");
                    config.backPage = "";
                }
            }

            // if no 'uriResolver' is configured yet, then there is one created right now:
            // the uriResolver is used to find xml's and xsl's.
            if (config.uriResolver == null) {                
                if (log.isDebugEnabled()) {
                    log.trace("creating uriresolver (backpage = " + config.backPage + ")");
                }
                URIResolver.EntryList extraDirs = new URIResolver.EntryList();
                File refFile;
                // capture direct reference of http:// and shttp:// referers
                int protocolPos= config.backPage.indexOf(PROTOCOL);
                if (protocolPos >=0 ) { // given absolutely
                    String path =  config.backPage.substring(config.backPage.indexOf('/', protocolPos + PROTOCOL.length()));
                    // Using URL.getPath() would be nicer, but is not availeble in java 1.2
                    // suppose it is from the same server, we can find back the directory then:
                    refFile = new File(request.getRealPath(path.substring(request.getContextPath().length()))).getParentFile();

                    // TODO: What if it happened to be not from the same server?
                } else {
                    // Was given relatively, that's easy:
                    refFile = new File(request.getRealPath(config.backPage)).getParentFile();
                }
                if (refFile != null && refFile.exists()) {
                    extraDirs.add("ref:", refFile);
                }
                File jspFileDir = new File(request.getRealPath(request.getServletPath())).getParentFile(); // the directory of this jsp (list, wizard)
                File basedir    = new java.io.File(jspFileDir.getParentFile().getAbsolutePath(), "data"); // ew default data/xsls is in ../data then
                extraDirs.add("ew:", basedir);
                config.uriResolver = new URIResolver(jspFileDir, extraDirs);
            }
        }
        protected String getParam(String paramName) { 
            if (request.getParameter(paramName) == null) return null;
            return org.mmbase.util.Encode.decode("ESCAPE_URL_PARAM", request.getParameter(paramName)).replace('\\', '/'); 
        }
        
        protected String getParam(String paramName, String def) {
            if (request.getParameter(paramName) == null) {
                if (def == null) return null;
                return def.toString(); 
            }
            return getParam(paramName);
        }
        
        protected Integer getParam(String paramName, Integer def) {
            String i = request.getParameter(paramName);
            if (i == null || i.equals("")) return def;
            return new Integer(i);
        }
        protected Boolean getParam(String paramName, Boolean def) {
            if (request.getParameter(paramName) == null) return def;
            return new Boolean(request.getParameter(paramName));            
        }
        public String getBackPage(){
            if(config.subObjects.size() == 0) {
                return config.backPage;
            } else {
                return ((SubConfig) config.subObjects.peek()).page;
            }
        }

        public  ListConfig createList() {
            ListConfig l = new ListConfig();
            l.page = response.encodeURL(request.getServletPath() + "?proceed=yes");
            return l;
        }
        public Config.WizardConfig createWizard(Cloud cloud) throws SecurityException, WizardException {
            WizardConfig wizard = new WizardConfig();
            wizard.page = response.encodeURL(request.getServletPath() + "?proceed=yes");
            config(wizard); // determin the objectnumber.            
            wizard.wiz = new Wizard(request.getContextPath(), config.uriResolver, config.wizard, wizard.objectNumber, cloud);
            wizard.wiz.setSessionId(config.sessionId);
            return wizard;
        }
        public abstract void config(Config.ListConfig c);
        public abstract void config(Config.WizardConfig c);

    }

}
