/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 */
public class Forum {

    // logger
    static private Logger log = Logging.getLoggerInstance(Forum.class);

    private String name;
    private String description;
    private String administratorsline;
    private int id, totalusers, totalusersnew;
    private Node node;

    private int viewcount;
    private int postcount;
    private int postthreadcount = -1;

    private int lastposttime;
    private String lastposter;
    private String lastpostsubject;

    private Hashtable administrators = new Hashtable();

    private Hashtable postareas = new Hashtable();

    private Hashtable posters = new Hashtable();
    private Hashtable posternames = new Hashtable();
    private Vector onlineposters = new Vector();
    private Vector newposters = new Vector();

    /**
     * Constructor
     *
     * @param node forum node
     */
    public Forum(Node node) {
        this.node = node;
        this.name = node.getStringValue("name");
        this.description = node.getStringValue("description");
        this.id = node.getNumber();

        this.viewcount = node.getIntValue("viewcount");
        if (viewcount == -1) viewcount = 0;
        this.postcount = node.getIntValue("postcount");
        if (postcount == -1) postcount = 0;
        this.postthreadcount = node.getIntValue("postthreadcount");
        if (postthreadcount == -1) postthreadcount = 0;

        this.lastpostsubject = node.getStringValue("lastpostsubject");
        this.lastposter = node.getStringValue("lastposter");
        this.lastposttime = node.getIntValue("lastposttime");

        // read postareas
        preCachePosters();
        readAreas();
        readRoles();
    }

    /**
     * Set the MMBase objectnumber of the forum
     *
     * @param id MMase objectnumber
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set forum to node
     *
     * @param node forumnode
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Set name of the forum
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
        node.setValue("name", name);
    }

    /**
     * Set the language of the forum
     *
     * @param language
     */
    public void setLanguage(String language) {
        node.setValue("language", language);
    }

    /**
     * Get the language of the forum
     *
     * @return the language
     */
    public String getLanguage() {
        return node.getStringValue("language");
    }

    /**
     * Set the description of the forum
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
        node.setValue("description", description);
    }

    /**
     * get the name of the forum
     *
     * @return name of the forum
     */
    public String getName() {
        return name;
    }

    /**
     * get the description of the forum
     *
     * @return description of the forum
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the MMBase objectnumber of the forum
     *
     * @return MMBase objectnumber of the forum
     */
    public int getId() {
        return id;
    }

    /**
     * get the number of posts on the forum
     *
     * @return number of posts on the furum
     */
    public int getPostCount() {
        return postcount;
    }

    /**
     * get the number of views on the forum
     *
     * @return number of views
     */
    public int getViewCount() {
        return viewcount;
    }

    /**
     * get the accountname / nick of the last poster on the forum
     *
     * @return accountname / nick of the last poster on the forum
     */
    public String getLastPoster() {
        return lastposter;
    }

    /**
     * get the date/time (Epoch) of the last post on the forum
     *
     * @return date/time (Epoch) of the last post on the forum
     */
    public int getLastPostTime() {
        return lastposttime;
    }

    /**
     * get the subject of the last post on the forum
     *
     * @return subject of the last post on the forum
     */
    public String getLastSubject() {
        return lastpostsubject;
    }

    /**
     * "Save" the forum (add it to the syncQueue)
     *
     * @return <code>true</code>
     */
    public boolean save() {
        syncNode(ForumManager.FASTSYNC);
        return true;
    }

    /**
     * add the poster-node to the given syncQueue
     *
     * @param queue syncQueue that must be used
     */
    private void syncNode(int queue) {
        node.setIntValue("postcount", postcount);
        node.setIntValue("postthreadcount", postthreadcount);
        node.setIntValue("viewcount", viewcount);
        node.setIntValue("lastposttime", lastposttime);
        node.setStringValue("lastposter", lastposter);
        node.setStringValue("lastpostsubject", lastpostsubject);
        ForumManager.syncNode(node, queue);
    }


    /**
     * get the administrators of the forum
     *
     * @return administrators
     */
    public Enumeration getAdministrators() {
        return administrators.elements();
    }

    /**
     * get the posters of the forum
     *
     * @return posters
     */
    public Enumeration getPosters() {
        return posters.elements();
    }

    /**
     * get the posters that are online
     *
     * @return online posters
     */
    public Enumeration getPostersOnline() {
        return onlineposters.elements();
    }

    /**
     * determine if the given account is an administrator
     *
     * @param account
     * @return <code>true</code> if the account is an administrator
     */
    public boolean isAdministrator(String account) {
        return administrators.containsKey(account);
    }

    /**
     * get the online administrators (comma-seperated)
     *
     * @param baseurl
     * @return comma-seperated list of administrators-accounts for this forum. If the passed baseurl isn't empty it will make html-links for you
     */
    public String getAdministratorsLine(String baseurl) {
        if (administratorsline != null) return administratorsline;
        administratorsline = "";
        Enumeration e = administrators.elements();
        while (e.hasMoreElements()) {
            Poster p = (Poster) e.nextElement();
            if (!administratorsline.equals("")) administratorsline += ",";
            if (baseurl.equals("")) {
                administratorsline += p.getAccount();
            } else {
                administratorsline += "<a href=\"" + baseurl + "?forumid=" + getId() + "&posterid=" + p.getId() + "\">" + p.getAccount() + "</a>";
            }
        }
        return administratorsline;
    }

    /**
     * get a postarea of this forum  by it's MMbase objectnumber
     *
     * @param id MMbase objectnumber of the postarea
     * @return
     */
    public PostArea getPostArea(String id) {
        Object o = postareas.get(id);
        if (o != null) {
            return (PostArea) o;
        }
        return null;
    }

    /**
     * remove a postarea of this forum  by it's MMbase objectnumber
     *
     * @param id MMbase objectnumber of the postarea
     * @return Feedback. <code>true</code> if the action was successful, <code>false</code> if it wasn't
     */
    public boolean removePostArea(String id) {
        PostArea a = (PostArea) postareas.get(id);
        if (a != null) {
            if (a.remove()) {
                postareas.remove(id);
                return true;
            }
        } else {
            log.error("trying to delete a unknown postarea");
        }
        return false;
    }


    /**
     * remove a folder(mailbox) for a poster
     *
     * @param posterid
     * @param foldername
     * @return <code>true</code> if the action was successful
     */
    public boolean removeFolder(int posterid, String foldername) {
        Poster poster = getPoster(posterid);
        if (poster != null) {
            return poster.removeMailbox(foldername);
        }
        return false;
    }


    /**
     * remove a poster from the onlineposters-Vector
     *
     * @param p posternode
     */
    public void removeOnlinePoster(Poster p) {
        onlineposters.remove(p);
    }

    /**
     * get the number of postareas for this forum
     *
     * @return number of postareas for this forum
     */
    public int getPostAreaCount() {
        return postareas.size();
    }

    /**
     * get all the postareas of this forum
     *
     * @return postareas
     */
    public Enumeration getPostAreas() {
        return postareas.elements();
    }

    /**
     * add a poster to the onlineposters-Vector
     *
     * @param p new online poster
     */
    public void newPosterOnline(Poster p) {
        if (!onlineposters.contains(p)) {
            onlineposters.add(p);
        }
    }

    /**
     * add a poster to the newposters-Vector
     *
     * @param p new poster
     */
    public void newPoster(Poster p) {
        if (!newposters.contains(p)) {
            newposters.add(p);
        }
    }

    /**
     * create a new postarea for this forum
     *
     * @param name        name of the new postarea
     * @param description description of the new postarea
     * @return MMBase objectnumber for the newly created postarea
     */
    public int newPostArea(String name, String description) {
        NodeManager nm = ForumManager.getCloud().getNodeManager("postareas");
        if (nm != null) {
            Node anode = nm.createNode();
            anode.setStringValue("name", name);
            anode.setStringValue("description", description);
            anode.commit();

            RelationManager rm = ForumManager.getCloud().getRelationManager("forums", "postareas", "related");
            if (rm != null) {
                Node rel = rm.createRelation(node, anode);
                rel.commit();
                PostArea area = new PostArea(this, anode);
                postareas.put("" + anode.getNumber(), area);
                return anode.getNumber();
            } else {
                log.error("Forum can't load relation nodemanager forums/postareas/related");
            }
        } else {
            log.error("Forum can't load postareas nodemanager");
        }
        return -1;
    }

    /**
     * Called on construction
     * Fill the postareas-Hashtable
     */
    private void readAreas() {
        long start = System.currentTimeMillis();
        if (node != null) {
            NodeIterator i = node.getRelatedNodes("postareas").nodeIterator();
            while (i.hasNext()) {
                Node node2 = i.nextNode();
                PostArea area = new PostArea(this, node2);
                postareas.put("" + node2.getNumber(), area);
            }
        }
        long end = System.currentTimeMillis();
    }

    /**
     * get the total number of postthreads in this forum
     *
     * @return number of postthreads
     */
    public int getPostThreadCount() {
        //if (postthreadcount==-1) recalcPostThreadCount();
        return postthreadcount;
    }

    /**
     * Recalculates the private variabele "postcount" which contains
     * the total number of posts in the forum
     * <p/>
     * *** This method is not used ***
     */
    private void recalcPostCount() {
        int count = 0;
        Enumeration e = postareas.elements();
        while (e.hasMoreElements()) {
            PostArea a = (PostArea) e.nextElement();
            count += a.getPostCount();
        }
        postcount = count;
    }

    /**
     * Recalculates the private variabele "postthreadcount" which contains
     * the total number of postthreads in the forum
     * <p/>
     * *** This method is not used ***
     */
    private void recalcPostThreadCount() {
        int count = 0;
        Enumeration e = postareas.elements();
        while (e.hasMoreElements()) {
            PostArea a = (PostArea) e.nextElement();
            count += a.getPostThreadCount();
        }
        postthreadcount = count;
    }

    /**
     * Well, what does this do, then?
     */
    public void leafsChanged() {
//	recalcPostCount();
//	recalcPostThreadCount();
    }


    /**
     * signal the forum that there is a new reply
     * updates the postcount, lastposttime, lastposter, lastpostsubject of ths forum, and places it in the syncQueue
     *
     * @param child PostArea
     */
    public void signalNewReply(PostArea child) {
        postcount++;
        lastposttime = child.getLastPostTime();
        lastposter = child.getLastPoster();
        lastpostsubject = child.getLastSubject();
        syncNode(ForumManager.FASTSYNC);
    }

    /**
     * signal the forum that there is a new postthread
     * updates the postthreadcount , and places it in the syncQueue
     *
     * @param child PostArea
     */
    public void signalNewPost(PostArea child) {
        postthreadcount++;
        syncNode(ForumManager.FASTSYNC);
    }

    /**
     * signal the forum that there's a new view
     *
     * @param child PostArea
     */
    public void signalViewsChanged(PostArea child) {
        viewcount++;
        syncNode(ForumManager.SLOWSYNC);
    }

    /**
     * get a poster of this forum by it's accountname/nick (???)
     *
     * @param posterid accountname/nick
     * @return Poster <code>null</code> if the account was not found
     */
    public Poster getPoster(String posterid) {
        Poster p = (Poster) posternames.get(posterid);
        if (p != null) {
            return p;
        }
        return null;
    }

    /**
     * get a poster of this forum by it's MMBase Objectnumber
     *
     * @param posterid MMBase Objectnumber of the poster
     * @return Poster  <code>null</code> if the poster was not found
     */
    public Poster getPoster(int posterid) {
        Poster p = (Poster) posters.get(new Integer(posterid));
        if (p != null) {
            return p;
        } else {
            /*
            if (node!=null) {
                p=new Poster(node);
                posters.put(new Integer(posterid),p);
                posternames.put(p.getAccount(),p);
                return p;
            }
            */
        }
        return null;
    }

    /**
     * get the total number of posters in the forum
     *
     * @return number of posters in the forum
     */
    public int getPostersTotalCount() {
        return totalusers;
    }

    /**
     * get the number of online posters for the forum
     *
     * @return number of online posters
     */
    public int getPostersOnlineCount() {
        return onlineposters.size();
    }

    /**
     * get the number of new posters for the forum
     *
     * @return number of new posters
     */
    public int getPostersNewCount() {
        return newposters.size();
    }

    /**
     * Called on construction
     * Fill the posters, posternames, totalusers and onlineposters etc ...
     * <p/>
     * this is all wrong should be replaced way to much mem to read
     * them all.
     */
    private void preCachePosters() {
        long start = System.currentTimeMillis();
        if (node != null) {
            totalusers = 0;
            totalusersnew = 0;
            int onlinetime = ((int) (System.currentTimeMillis() / 1000)) - (getPosterExpireTime());
            int newtime = ((int) (System.currentTimeMillis() / 1000)) - (24 * 60 * 60 * 7);

            NodeIterator i = node.getRelatedNodes("posters", "related", "both").nodeIterator();
            while (i.hasNext()) {
                Node node = i.nextNode();
                Poster p = new Poster(node, this);
                posters.put(new Integer(p.getId()), p);
                posternames.put(p.getAccount(), p);
                totalusers++;
                if (p.getLastSeen() > onlinetime) {
                    onlineposters.add(p);
                }
                if (p.getFirstLogin() == -1 || p.getFirstLogin() > newtime) {
                    newPoster(p);
                }
            }
        }
        long end = System.currentTimeMillis();
    }

    /**
     * create a new poster for the forum
     *
     * @param account  accountname to register
     * @param password password to register
     * @return newly created Poster-object <code>null</code> if creation failed
     */
    public Poster createPoster(String account, String password) {
        NodeManager nm = ForumManager.getCloud().getNodeManager("posters");
        if (nm != null) {
            Node pnode = nm.createNode();
            pnode.setStringValue("account", account);
            pnode.setStringValue("password", password);
            pnode.setIntValue("postcount", 0);
            pnode.setIntValue("firstlogin", ((int) (System.currentTimeMillis() / 1000)));
            pnode.setIntValue("lastseen", ((int) (System.currentTimeMillis() / 1000)));
            pnode.commit();

            RelationManager rm = ForumManager.getCloud().getRelationManager("forums", "posters", "related");
            if (rm != null) {

                Node rel = rm.createRelation(node, pnode);
                rel.commit();

                Poster p = new Poster(pnode, this);
                posters.put(new Integer(p.getId()), p);
                onlineposters.add(p);
                posternames.put(p.getAccount(), p);

                totalusers++;
                totalusersnew++;
                return p;
            } else {
                log.error("Forum can't load relation nodemanager forums/posters/related");
                return null;
            }
        } else {
            log.error("Forum can't load posters nodemanager");
            return null;
        }
    }

    /**
     * add administrator to forum
     *
     * @param ap Poster
     * @return always <code>false</code> (ToDo ??)
     */
    public boolean addAdministrator(Poster ap) {
        if (!isAdministrator(ap.getAccount())) {
            RelationManager rm = ForumManager.getCloud().getRelationManager("forums", "posters", "rolerel");
            if (rm != null) {
                Node rel = rm.createRelation(node, ap.getNode());
                rel.setStringValue("role", "administrator");
                rel.commit();
                administrators.put(ap.getAccount(), ap);
            } else {
                log.error("Forum can't load relation nodemanager forums/posters/rolerel");
            }
        }
        return false;
    }

    /**
     * remove a poster from the posters
     *
     * @param p poster
     */
    public void childRemoved(Poster p) {
        posters.remove(p);
    }

    /**
     * remove all posters and postareas from hash
     *
     * @return <code>true</code> if it succeeds, <code>false</code> if it doesn't
     */
    public boolean remove() {
        Enumeration e = posters.elements();
        while (e.hasMoreElements()) {
            Poster p = (Poster) e.nextElement();
            if (!p.remove()) {
                log.error("Can't remove Poster : " + p.getId());
                return false;
            }
            posters.remove(new Integer(p.getId()));
        }

        e = postareas.elements();
        while (e.hasMoreElements()) {
            PostArea a = (PostArea) e.nextElement();
            if (!a.remove()) {
                log.error("Can't remove Area : " + a.getId());
                return false;
            }
            postareas.remove("" + a.getId());
        }

        node.delete(true);
        return true;
    }

    /**
     * Called on construction
     * gather the administrators
     */
    private void readRoles() {
        if (node != null) {
            RelationIterator i = node.getRelations("rolerel", "posters").relationIterator();
            while (i.hasNext()) {
                Relation rel = i.nextRelation();
                Node p = null;
                if (rel.getSource().getNumber() == node.getNumber()) {
                    p = rel.getDestination();
                } else {
                    p = rel.getSource();
                }
                String role = rel.getStringValue("role");
                // check limited to 12 chars to counter mmbase 12
                // chars in role bug in some installs
                if (role.substring(0, 12).equals("administrato")) {
                    Poster po = getPoster(p.getNumber());
                    administrators.put(po.getAccount(), po);
                }
            }
        }
    }

    /**
     * get the expiretime for the posters in seconds
     *
     * @return expiretime in seconds
     */
    public int getPosterExpireTime() {
        return (5 * 60);
    }

    /**
     * create a new private message
     *
     * @param poster  accountname/nick of the sending poster
     * @param to      accountname/nick of the recepient poster
     * @param subject subject of the private message
     * @param body    body of the private message
     * @return always -1 (ToDo ?)
     */
    public int newPrivateMessage(String poster, String to, String subject, String body) {
        Poster toposter = getPoster(to);
        Poster fromposter = getPoster(poster);
        if (toposter != null) {
            Mailbox mailbox = toposter.getMailbox("Inbox");
            if (mailbox == null) {
                mailbox = toposter.addMailbox("Inbox", "inbox for user " + toposter.getAccount(), 1, 25, -1, 1, 1);
            }
            NodeManager nm = ForumManager.getCloud().getNodeManager("forumprivatemessage");
            if (nm != null) {
                Node mnode = nm.createNode();
                mnode.setStringValue("subject", subject);
                mnode.setStringValue("body", body);
                mnode.setStringValue("poster", fromposter.getAccount());
                mnode.setIntValue("createtime", (int) (System.currentTimeMillis() / 1000));
                mnode.setIntValue("viewstate", 0);
                mnode.setStringValue("fullname", fromposter.getFirstName() + " " + fromposter.getLastName());
                mnode.commit();

                RelationManager rm = ForumManager.getCloud().getRelationManager("forummessagebox", "forumprivatemessage", "related");
                if (rm != null) {
                    Node rel = rm.createRelation(mailbox.getNode(), mnode);
                    rel.commit();
                } else {
                    log.error("Forum can't load relation nodemanager forummessagebox/forumprivatemessage/related");
                }
            } else {
                log.error("Forum can't load forumprivatemessage nodemanager");
            }
        }
        return -1;
    }

    /**
     * create a new folder (messagebox) for the given poster
     *
     * @param posterid  MMBase objectnumber of the poster
     * @param newfolder name of the new folder
     * @return always -1 (ToDo?)
     */
    public int newFolder(int posterid, String newfolder) {
        Poster poster = getPoster(posterid);
        if (poster != null) {
            Mailbox mailbox = poster.getMailbox(newfolder);
            if (mailbox == null) {
                mailbox = poster.addMailbox(newfolder, "mailbox " + newfolder + " for user " + poster.getAccount(), 1, 25, -1, 1, 1);
            }
        }
        return -1;
    }

    /**
     * called to maintain the memorycaches
     */
    public void maintainMemoryCaches() {
        Enumeration e = postareas.elements();
        while (e.hasMoreElements()) {
            // for now all postareas nodes are loaded so
            // we just call them all for a maintain
            PostArea a = (PostArea) e.nextElement();
            a.maintainMemoryCaches();
        }
    }

}
