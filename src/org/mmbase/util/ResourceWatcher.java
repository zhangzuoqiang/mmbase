/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.File;
import java.net.URL;
import java.util.*;


import org.mmbase.core.event.*;
import org.mmbase.util.logging.*;
import org.mmbase.bridge.Node;

/**
 *  Like {@link org.mmbase.util.FileWatcher} but for Resources. If (one of the) file(s) to which the resource resolves
 *  to is added or changed, it's onChange will be triggered, if not a 'more important' wil was
 *  existing already. If a file is removed, and was the most important one, it will be removed from the filewatcher.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: ResourceWatcher.java,v 1.7 2005-11-02 19:15:40 ernst Exp $
 * @see    org.mmbase.util.FileWatcher
 * @see    org.mmbase.util.ResourceLoader
 */
public abstract class ResourceWatcher implements NodeEventListener  {
    private static final Logger log = Logging.getLoggerInstance(ResourceWatcher.class);

    /**
     * All instantiated ResourceWatchers. Only used until setResourceBuilder is called. Then it
     * is set to null, and not used any more (also used in ResourceLoader).
     *
     */
    static  Set resourceWatchers = new HashSet();

    /**
     * Considers all resource-watchers. Perhaps onChange must be called, because there is a node for this resource available now.
     */
    static void setResourceBuilder() {
        synchronized(resourceWatchers) {
            Iterator i = resourceWatchers.iterator();
            while (i.hasNext()) {
                ResourceWatcher rw = (ResourceWatcher) i.next();
                if (rw.running) {
                    EventManager.getInstance().addEventListener(rw);
                }
                Iterator j = rw.resources.iterator();
                while (j.hasNext()) {
                    String resource = (String) j.next();
                    if (rw.mapNodeNumber(resource)) {
                        log.service("ResourceBuilder is available now. Resource " + resource + " must be reloaded.");
                        rw.onChange(resource);

                    }
                }
            }
        }
        resourceWatchers = null; // no need to store those any more.
    }

    /**
     * Delay setting used for the filewatchers.
     */
   private long delay = -1;

    /**
     * All resources watched by this ResourceWatcher. A Set of Strings. Often, a ResourceWatcher would watch only one resource.
     */
    protected SortedSet resources = new TreeSet();

    /**
     * When a resource is loaded from a Node, we must know which Nodes correspond to which
     * resource. You could ask the node itself, but if the node happens to be deleted, then you
     * can't know that any more. Used in {@link #nodeChanged}
     */
    protected Map       nodeNumberToResourceName = new HashMap();

    /**
     * Whether this ResourceWatcher has been started (see {@link #start})
     */
    private boolean running = false;

    /**
     * Wrapped FileWatcher for watching the file-resources. ResourceName -> FileWatcher.
     */
    protected Map fileWatchers = new HashMap();

    /**
     * The resource-loader associated with this ResourceWatcher.
     */
    protected ResourceLoader resourceLoader;


    /**
     * Constructor.
     */
    protected ResourceWatcher(ResourceLoader rl) {
        resourceLoader = rl;
        if (resourceWatchers != null) {
            synchronized(resourceWatchers) {
                resourceWatchers.add(this);
            }
        }
    }
    /**
     * Constructor, defaulting to the Root ResourceLoader (see {@link ResourceLoader#getConfigurationRoot}).
     */
    protected ResourceWatcher() {
        this(ResourceLoader.getConfigurationRoot());
    }



    /**
     * @return Unmodifiable set of String of watched resources
     */
    public Set getResources() {
        return Collections.unmodifiableSortedSet(resources);
    }

    /**
     * The associated ResourceLoader
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     *
     * @param resourceName The resource to be monitored.
     */
    public synchronized void add(String resourceName) {
        if (resourceName == null || resourceName.equals("")) {
            log.warn("Cannot watch resource '" + resourceName + "' " + Logging.stackTrace());
            return;
        }
        resources.add(resourceName);
        log.service("Started watching '" + resourceName + "' for resource loader " + resourceLoader + "(now watching " + resources + ")");
        if (running) {
            createFileWatcher(resourceName);
            mapNodeNumber(resourceName);
        }
    }

    /**
     * If you resolved a resource already to an URL, you can still add it for watching.
     */
    public synchronized void add(URL url) {
        if (url.getProtocol().equals(ResourceLoader.PROTOCOL)) {
            String path = url.getPath();
            add(path.substring(resourceLoader.getContext().getPath().length()));
        } else {
            throw new UnsupportedOperationException("Don't know how to watch " + url + " (Only URLs produced by ResourceLoader are supported)");
        }
    }

    /**
     * When a resource is added to this ResourceWatcher, this method is called to create a
     * {@link FileWatcher}, and add all files associated with the resource to it.
     */
    protected synchronized void createFileWatcher(String resource) {
        FileWatcher fileWatcher = new ResourceFileWatcher(resource);
        if (delay != -1) {
            fileWatcher.setDelay(delay);
        }
        fileWatcher.getFiles().addAll(resourceLoader.getFiles(resource));
        fileWatcher.start(); // filewatchers are only created on start, so must always be started themselves.
        fileWatchers.put(resource, fileWatcher);
    }

    /**
     * When a resource is added to this ResourceWatcher, this method is called to check wether a
     * ResourceBuilder node is associated with this resource. If so, this methods maps the number of
     * the node to the resource name. This is needed in {@link #nodeChanged} in case of a
     * node-deletion.
     * @return Whether a Node as found to map.
     */
    protected synchronized boolean mapNodeNumber(String resource) {
        Node node = resourceLoader.getResourceNode(resource);
        if (node != null) {
            nodeNumberToResourceName.put("" + node.getNumber(), resource);
            return true;
        } else {
            return false;
        }

    }




    /**
     * If a node (of the type 'resourceBuilder') changes, checks if it is a node belonging to one of the resource of this resource-watcher.
     * If so, {@link #onChange} is called.
     */
    public void notify(NodeEvent event) {
        String number = "" + event.getNodeNumber();
        switch(event.getType()) {
        case NodeEvent.EVENT_TYPE_DELETE: {
            // hard..
            String name = (String) nodeNumberToResourceName.get(number);
            if (name != null && resources.contains(name)) {
                nodeNumberToResourceName.remove(number);
                log.service("Resource " + name + " changed (node removed)");
                onChange(name);
            }
            break;
        }
        default: {            
            Node node = ResourceLoader.resourceBuilder.getCloud().getNode(number);
            int contextPrefix = resourceLoader.getContext().getPath().length() - 1;
            String name = node.getStringValue(ResourceLoader.RESOURCENAME_FIELD);
            if (name.length() > contextPrefix && resources.contains(name.substring(contextPrefix))) {
                log.service("Resource " + name + " changed (node added)");
                nodeNumberToResourceName.put(number, name);
                onChange(name);
            }
        }
        }
    }
    public Properties getConstraintsForEvent(Event event) {
        return null;
    }


    public synchronized void start() {
        // create and start all filewatchers.
        Iterator i = resources.iterator();
        while (i.hasNext()) {
            String resource = (String) i.next();
            resourceLoader.checkShadowedNewerResources(resource);
            mapNodeNumber(resource);
            createFileWatcher(resource);

        }
        if (resourceWatchers == null) {
            EventManager.getInstance().addEventListener(this);
        }
        running = true;
    }


    /**
     * Put here the stuff that has to be executed, when a file has been changed.
     * @param resourceName The resource that was changed.
     */
    abstract public void onChange(String resourceName);


    /**
     * Set the delay to observe between each check of the file changes.
     */
    public synchronized void setDelay(long delay) {
        this.delay = delay;
        Iterator i = fileWatchers.values().iterator();
        while (i.hasNext()) {
            FileWatcher fw = (FileWatcher) i.next();
            fw.setDelay(delay);
        }
    }


    /**
     */
    public synchronized void remove(String resourceName) {
        boolean wasRunning = running;
        if (running) { // it's simplest like this.
            exit();
        }
        resources.remove(resourceName);
        if (wasRunning) {
            start();
        }
    }

    /**
     * Removes all resources.
     */
    public synchronized  void clear() {
        if (running) {
            exit();
            resources.clear();
            start();
        } else {
            resources.clear();
        }
    }

    /**
     * Stops watching. Stops all filewatchers, removes observers.
     */
    public synchronized void exit() {
        Iterator i = fileWatchers.values().iterator();
        while (i.hasNext()) {
            FileWatcher fw = (FileWatcher) i.next();
            fw.exit();
            i.remove();
        }
        if (ResourceLoader.resourceBuilder != null) {
            EventManager.getInstance().removeEventListener(this);
        }
        running = false;
    }


    /**
     * Shows the 'contents' of the filewatcher. It shows a list of files/last modified timestamps.
     */
    public String toString() {
        return "" + resources + " " + fileWatchers;
    }


    /**
     * A FileWatcher associated with a certain resource of this ResourceWatcher.
     */

    protected class ResourceFileWatcher extends FileWatcher {
        private String resource;
        ResourceFileWatcher(String resource) {
            this.resource = resource;
        }
        public void onChange(File f) {
            URL shadower = resourceLoader.shadowed(f, resource);
            if (shadower == null) {
                ResourceWatcher.this.onChange(resource);
            } else {
                log.warn("File " + f + " changed, but it is shadowed by " + shadower);
            }
        }
    }


}
