/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.BridgeCollections;
import org.mmbase.datatypes.DataType;
import org.mmbase.bridge.implementation.*;
import org.mmbase.storage.search.*;
import org.mmbase.security.*;
import org.mmbase.util.*;
import org.xml.sax.InputSource;

/**
 * Abstract implementation of CloudContext which implements a number of methods with reasonable defaults, or based on other methods.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public abstract class AbstractCloudContext implements CloudContext {

    public static final String CLOUD = "mmbase";
    protected final BasicStringList clouds        = new BasicStringList();
    private Authentication authentication = new NoAuthentication();


    static {
        org.mmbase.util.xml.AbstractBuilderReader.registerSystemIDs();
        org.mmbase.util.xml.AbstractBuilderReader.registerPublicIDs();
    }


    public AbstractCloudContext() {
        clouds.add(CLOUD);
    }


    public ModuleList getModules() {
        return BridgeCollections.EMPTY_MODULELIST;
    }

    public Module getModule(String name) throws NotFoundException {
        throw new NotFoundException();
    }

    public boolean hasModule(String name) {
        return false;
    }

    public StringList getCloudNames() {
        return BridgeCollections.unmodifiableStringList(clouds);
    }

    public String getDefaultCharacterEncoding() {
        return "UTF-8";
    }

    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    public TimeZone getDefaultTimeZone() {
        return TimeZone.getDefault();
    }


    public FieldList createFieldList() {
        return new BasicFieldList();
    }


    public Cloud getCloud(String name) {
        return getCloud(name, "anonymous", new HashMap<String, Object>());
    }

    public Cloud getCloud(String name, String authenticationType, Map<String, ?> loginInfo) throws NotFoundException {
        UserContext uc =  getAuthentication().login(authenticationType, loginInfo, new Object[] {});
        return getCloud(name, uc);
    }


    public NodeList createNodeList() {
        return getCloud(CLOUD).createNodeList();
    }

    public RelationList createRelationList() {
        return getCloud(CLOUD).createRelationList();
    }


    public NodeManagerList createNodeManagerList() {
        return getCloud(CLOUD).createNodeManagerList();
    }

    public RelationManagerList createRelationManagerList() {
        return getCloud(CLOUD).createRelationManagerList();
    }
    public ModuleList createModuleList() {
        return new BasicModuleList();
    }

    public StringList createStringList() {
        return new BasicStringList();
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public ActionRepository getActionRepository() {
        return ActionRepository.getInstance();
    }

    public boolean isUp() {
        return true;
    }

    public CloudContext assertUp() {
        return this;
    }


 }
