/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.config;

import java.util.*;
import java.io.*;
import java.sql.*;

/**
 * @author Case Roole, cjr@dds.nl
 * @version $Id: DatabaseReport.java,v 1.1 2000-09-11 20:26:53 case Exp $
 *
 * $Log: not supported by cvs2svn $
 */
public class DatabaseReport extends AbstractReport {
    // Cache read property values
    Hashtable jdbcProperties;
    Hashtable mmbaserootProperties;

    long lastModifiedJDBCProperties;
    long lastModifiedMMBASEROOTProperties;
    
    // --- public methods ---------------------------------------
    public String label() {
	return "Database";
    }

    /**
     * @return String with database configuration
     */
    public String report() {
	String res = "";
	String eol = (String)specialChars.get("eol");
	String amp = (String)specialChars.get("amp");

	if (jdbcProperties == null) {
	    readJDBCProperties();
	}
	if (jdbcProperties.isEmpty()) {
	    res = res + "*** JDBC properties in "+configpath+File.separator+"modules"+File.separator+"jdbc.xml not or incorrectly set."+eol+
		"!!! Please check /mmadmin/config/configdetail.shtml?annotate+modules+jdbc for possible XML errors"+eol;
	    return res;
	}

	// Active database server
	res = "DBMS = "+getMMBASEROOTProperty("DATABASE")+eol;
	
	// Connection information
	res = res + "Connection url = "+getJDBCProperty("url")+eol;
	String convertedUrl = convertJDBCUrl(getJDBCProperty("url"),getJDBCProperty("host"),getJDBCProperty("port"),getJDBCProperty("database"));
	res = res + "Converted connection url = "+convertedUrl+eol;

	// Check connection
	res = res + "JDBC Driver = "+getJDBCProperty("driver")+eol;
	try {
	    Class.forName(getJDBCProperty("driver"));
	    Connection con = DriverManager.getConnection(convertedUrl);

	    res = res + "Connection checked ok" + eol;
	} catch (ClassNotFoundException e) {
	    res = res + "*** Error loading JDBC Driver: "+e.getMessage() + eol;
	} catch (SQLException e) {
	    res = res + "*** SQL Error: "+e.getMessage() + eol;
	    if (convertedUrl.indexOf("&amp;")<0) {
		// Try to replace ';' with '&amp;' in connection string
		try {
		    Connection con = DriverManager.getConnection(stringReplace(convertedUrl,";","&"));
		    res = res + "!!! Problem: You are constructing your url with ';' between name and password, rather than '&'"+eol;
		    res = res + "!!! Solution: replace ';' with '"+amp+"' (XML entity!) in your jdbc url!!!" + eol;
		    return res;
		} catch (SQLException ignore) {
		    res = res + ignore.getMessage()+eol;

		}
	    }
	    res = res + "* $HOST = "+getJDBCProperty("host") + eol;
	    res = res + "* $PORT = "+getJDBCProperty("port") + eol;
	    res = res + "* $DBM = "+getJDBCProperty("database") + eol;
	} 
	return res;
    }


    // --- private methods --------------------------------------
    private String getJDBCProperty(String key) {
	String pathToXMLProperties = configpath+File.separator+"modules"+File.separator+"jdbc.xml";
	File jdbcFile = new File(pathToXMLProperties);
	if (jdbcProperties == null || lastModifiedJDBCProperties < jdbcFile.lastModified()) {
	    jdbcProperties = getPropertiesFromXML("modules"+File.separator+"jdbc.xml");
	}
	String val = (String)jdbcProperties.get(key);
	if (val == null) {
	    return "";
	} else {
	    return val;
	}
    }

    private void readMMBASEROOTProperties() {
	mmbaserootProperties = getPropertiesFromXML("modules"+File.separator+"mmbaseroot.xml");
    }

    private void readJDBCProperties() {
	jdbcProperties = getPropertiesFromXML("modules"+File.separator+"jdbc.xml");
    }
	
    private String getMMBASEROOTProperty(String key) {
	String pathToXMLProperties = configpath+File.separator+"modules"+File.separator+"mmbaseroot.xml";
	File mmbaserootFile = new File(pathToXMLProperties);
	if (mmbaserootProperties == null || lastModifiedMMBASEROOTProperties < mmbaserootFile.lastModified()) {
	    mmbaserootProperties = getPropertiesFromXML("modules"+File.separator+"mmbaseroot.xml");
	}
	String val = (String)mmbaserootProperties.get(key);
	if (val == null) {
	    return "";
	} else {
	    return val;
	}
    }

    /**
     *
     */ 
    private String convertJDBCUrl(String url, String host, String port, String dbms) {
	String res = "";
	res = stringReplace(url,"$HOST",host);
	res = stringReplace(res,"$PORT",port);
	res = stringReplace(res,"$DBM",dbms);
	return res;
    }

    /**
     * @param path Relative path to database mapping file
     *
     * @return Whether a database mapping file is for the active DBMS
     */
    private boolean databaseIsActive(String path) {	
        return path.indexOf(getMMBASEROOTProperty("DATABASE")) > 0;
    }

    private boolean checkDriverLoadable(String driver) {
	try {
	    Class c = Class.forName(driver);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }
}
