/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import org.mmbase.module.builders.Jumpers;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * Redirects request based on information supplied by the jumpers builder.
 *
 * @author Jaco de Groot
 * @version $Id: JumpersFilter.java,v 1.9 2004-02-24 11:53:19 michiel Exp $
 */
public class JumpersFilter implements Filter, MMBaseStarter {
    private static final Logger log = Logging.getLoggerInstance(JumpersFilter.class);
    private static MMBase mmbase;
    private static Jumpers bul;
    private static String name;

    private Thread initThread;

    /**
     * Supported for use with older versions of the servlet api, such as used by Orion 1.5.2
     * This method simply thows an exception when called.
     * @deprecated will be dropped in future versions
     */
    public void setFilterConfig(FilterConfig fc) {
        throw new UnsupportedOperationException("This method is not part of the Servlet api 2.3");
    }

    /**
     * Supported for use with older versions of the servlet api, such as used by Orion 1.5.2
     * This method simply thows an exception when called.
     * @deprecated will be dropped in future versions
     */
    public FilterConfig getFilterConfig() {
        throw new UnsupportedOperationException("This method is not part of the Servlet api 2.3");
    }

    public MMBase getMMBase() {
        return mmbase;
    }
    public void setMMBase(MMBase mmb) {
        mmbase = mmb;
    }
    public void setInitException(ServletException se) {
        // never mind, simply, ignore
    }
    /**
     * @javadoc
     */
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
        name = filterConfig.getFilterName();
        MMBaseContext.init(filterConfig.getServletContext());
        MMBaseContext.initHtmlRoot();

        // stuff that can take indefinite amount of time (database down and so on) is done in separate thread
        initThread = new MMBaseStartThread(this);
        initThread.start();

    }

    /**
     * @javadoc
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws java.io.IOException, ServletException {
        if (bul == null) {
            if (mmbase != null) {
                bul = (Jumpers)mmbase.getBuilder("jumpers");
            } 
            if (bul == null) {
                filterChain.doFilter(servletRequest, servletResponse);
                return; // nothing to be done
            }
        }
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse res = (HttpServletResponse)servletResponse;
        /**
         * getContextPath()
         * Returns the portion of the request URI that indicates the context of the request.
         * The context path always comes first in a request URI.
         * The path starts with a "/" character but does not end with a "/" character.
         * For servlets in the default (root) context, this method returns "". The container does not decode this string.
         */
        int contextPart = req.getContextPath().length();
        String reqURI = req.getRequestURI();
        String key = "";
        if (contextPart < reqURI.length()) {
            // also remove the leading "/", unless it's an empty string.
            key = req.getRequestURI().substring(req.getContextPath().length()+1);
        }

        if (key.indexOf('.') == -1 && !key.endsWith("/")) {
            String url = bul.getJump(key);
            if (url != null) {
                /*
                 * Sends a temporary redirect response to the client using the specified redirect location URL.
                 * This method can accept relative URLs; the servlet container must convert the relative URL
                 * to an absolute URL before sending the response to the client. If the location is relative without a leading '/' the
                 * container interprets it as relative to the current request URI. If the location is relative with a leading '/' the container
                 *  interprets it as relative to the servlet container root.
                 * If the response has already been committed, this method throws an IllegalStateException.
                 *  After using this method, the response should be considered to be committed and should not be written to.
                 */
                // res.sendRedirect(res.encodeRedirectURL(req.getContextPath() + url));
                res.sendRedirect(url);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * @javadoc
     */
    public void destroy() {
        log.info("Filter " + name + " destroyed.");
        initThread.interrupt();
    }


}
