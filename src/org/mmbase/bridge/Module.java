/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Map;
import javax.servlet.*;

/**
 * Modules are pieces of functionality that are not MMBase objects.
 * e.g. Session, Mail, Upload and other functionality
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: Module.java,v 1.14 2004-10-09 09:40:09 nico Exp $
 */
public interface Module {

    /**
     * Retrieves the CloudContext to which this module belongs
     * @return CloudContext
     */
    public CloudContext getCloudContext();

    /**
     * Retrieve the name of the module (in the default language defined in mmbaseroot.xml).
     * @return name of the module
     */
    public String getName();

    /**
     * Retrieve the description of the module.
     * @return description of the module
     */
    public String getDescription();

    /**
     * Retrieve a property of the module.
     * @param name the name of the property
     * @return the property value (null if not given)
     * @since  MMBase-1.7
     */
    public String getProperty(String name);

    /**
     * Retrieve a copy of the module's properties
     * @return a map of module properties
     * @since  MMBase-1.7
     */
    public Map getProperties();

    /**
     * Runs the command with the given parameter(s).
     * @param command the command to run, i.e. "MESSAGE-UPDATE".
     * @param parameter the main parameter for the command. Depends on the command issued. Not all
     *      commands make use of this parameter.
     */
    public void process(String command, Object parameter);

    /**
     * Runs the command with the given parameter(s).
     * @param command the command to run, i.e. "MESSAGE-UPDATE".
     * @param parameter the main parameter for the command. Depends on the command issued. Not all
     *      commands make use of this parameter.
     * @param auxparameters additional parameters for this command.
     */
    public void process(String command, Object parameter, Map auxparameters);

    /**
     * Runs the command with the given parameter(s).
     * @param command the command to run, i.e. "MESSAGE-UPDATE".
         * @param parameter the main parameter for the command. Depends on the command issued. Not all
         *      commands make use of this parameter.
         * @param auxparameters additional parameters for this command.
     * @param req the Request item to use for obtaining user information. For backward compatibility.
     * @param resp the Response item to use for redirecting users. For backward compatibility.
     */
    public void process(String command, Object parameter, Map auxparameters, ServletRequest req,  ServletResponse resp);

    /**
     * Retrieve info from a module based on a command string.
     * Similar to the $MOD command in SCAN.
     * @param command the info to obtain, i.e. "USER-OS".
     * @return info from a module
     */
    public String getInfo(String command);

    /**
     * Retrieve info from a module based on a command string
     * Similar to the $MOD command in SCAN.
     * @param command the info to obtain, i.e. "USER-OS".
     * @param req the Request item to use for obtaining user information. For backward compatibility.
     * @param resp the Response item to use for redirecting users. For backward compatibility.
     * @return info from a module
     */
    public String getInfo(String command, ServletRequest req, ServletResponse resp);

    /**
     * Retrieve info (as a list of virtual nodes) from a module based on a command string.
     * Similar to the LIST command in SCAN.
     * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
     * @param command the info to obtain, i.e. "USER-OS".
     * @param parameters a hashtable containing the named parameters of the list.
     * @return info from a module (as a list of virtual nodes)
     */
    public NodeList getList(String command, Map parameters);

    /**
     * Retrieve info from a module based on a command string
     * Similar to the LIST command in SCAN.
     * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
     * @param command the info to obtain, i.e. "USER-OS".
     * @param parameters a hashtable containing the named parameters of the list.
     * @param req the Request item to use for obtaining user information. For backward compatibility.
     * @param resp the Response item to use for redirecting users. For backward compatibility.
     * @return info from a module (as a list of virtual nodes)
     */
    public NodeList getList(String command, Map parameters, ServletRequest req, ServletResponse resp);


}
