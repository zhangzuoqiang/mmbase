/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.cache.MultilevelCache;
import org.mmbase.cache.AggregatedResultCache;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.security.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Basic implementation of Cloud. It wraps a.o. the core's ClusterBuilder and Typedef functionalities.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: BasicCloud.java,v 1.98 2003-08-08 12:08:43 michiel Exp $
 */
public class BasicCloud implements Cloud, Cloneable, Comparable, SizeMeasurable {
    private static Logger log = Logging.getLoggerInstance(BasicCloud.class);

    // lastRequestId
    // used to generate a temporary ID number
    // The Id starts at - 2 and is decremented each time a new id is asked
    // until Integer.MIN_VALUE is reached (after which counting starts again at -2).
    private static int lastRequestId = -2;

    // link to cloud context
    private BasicCloudContext cloudContext = null;

    // link to typedef object for retrieving type info (builders, etc)
    private TypeDef typedef = null;

    // name of the cloud
    protected String name = null;

    // account of the current user (unique)
    // This is a unique number, unrelated to the user context
    // It is meant to uniquely identify this session to MMBase
    // It is NOT used for authorisation!
    protected String account = null;

    // description
    // note: in future, this is dependend on language settings!
    protected String description = null;

    // transactions
    protected Map transactions = new HashMap();

    // node managers cache
    protected Map nodeManagerCache = new HashMap();

    // parent Cloud, if appropriate
    protected BasicCloud parentCloud = null;

    // MMBaseCop
    MMBaseCop mmbaseCop = null;

    // User context
    protected BasicUser userContext = null;

    private MultilevelCache multilevelCache; // should be static?

    private Locale locale;

    public int getByteSize() {
        return getByteSize(new SizeOf());
    }
    public int getByteSize(SizeOf sizeof) {
        return sizeof.sizeof(transactions) + sizeof.sizeof(nodeManagerCache) + sizeof.sizeof(multilevelCache);
    }

    /**
     *  basic constructor for descendant clouds (i.e. Transaction)
     */
    BasicCloud(String cloudName, BasicCloud cloud) {
        cloudContext = cloud.cloudContext;
        parentCloud = cloud;
        typedef = cloud.typedef;
        locale = cloud.locale;
        name = cloudName;
        description = cloud.description;
        mmbaseCop = cloud.mmbaseCop;

        userContext = cloud.userContext;
        account = cloud.account;

        // start multilevel cache
        multilevelCache = MultilevelCache.getCache();
    }

    /**
     */
    BasicCloud(String name, String application, Map loginInfo, CloudContext cloudContext) {
        // get the cloudcontext and mmbase root...
        this.cloudContext = (BasicCloudContext)cloudContext;
        MMBase mmb = BasicCloudContext.mmb;

        // do authentication.....
        mmbaseCop = mmb.getMMBaseCop();

        if (mmbaseCop == null) {
            String message = "Couldn't find the MMBaseCop.";
            log.error(message);
            throw new BridgeException(message);
        }
        org.mmbase.security.UserContext uc = mmbaseCop.getAuthentication().login(application, loginInfo, null);
        if (uc == null) {
            throw new BridgeException("Login invalid (login-module: " + application + ")");
        }
        userContext = new BasicUser(mmbaseCop, uc);
        // end authentication...

        // other settings of the cloud...
        typedef = mmb.getTypeDef();
        locale = new Locale(mmb.getLanguage(), "");

        // normally, we want the cloud to read it's context from an xml file.
        // the current system does not support multiple clouds yet,
        // so as a temporary hack we set default values
        this.name = name;
        description = name;

        // generate an unique id for this instance...
        account = "U" + uniqueId();

        // start multilevel cache
        multilevelCache = MultilevelCache.getCache();
    }

    // Makes a node or Relation object based on an MMObjectNode
    Node makeNode(MMObjectNode node, String nodenumber) {
        NodeManager nm = getNodeManager(node.parent.getTableName());
        int nodenr = node.getNumber();
        if (nodenr == -1) {
            int nodeid = Integer.parseInt(nodenumber);
            if (node.parent instanceof TypeDef) {
                return new BasicNodeManager(node, this, nodeid);
            } else if (node.parent instanceof RelDef || node.parent instanceof TypeRel) {
                return new BasicRelationManager(node, this, nodeid);
            } else if (node.parent instanceof InsRel) {
                return new BasicRelation(node, this, nodeid);
            } else {
                return new BasicNode(node, this, nodeid);
            }
        } else {
            this.verify(Operation.READ, nodenr);
            if (node.parent instanceof TypeDef) {
                return new BasicNodeManager(node, this);
            } else if (node.parent instanceof RelDef || node.parent instanceof TypeRel) {
                return new BasicRelationManager(node, this);
            } else if (node.parent instanceof InsRel) {
                return new BasicRelation(node, this);
            } else {
                return new BasicNode(node, this);
            }
        }
    }

    public Node getNode(String nodenumber) throws NotFoundException {
        MMObjectNode node;
        try {
            node = BasicCloudContext.tmpObjectManager.getNode(account, nodenumber);
        } catch (RuntimeException e) {
            throw new NotFoundException("Something went wrong while getting node with number " + nodenumber, e);
        }
        if (node == null) {
            throw new NotFoundException("Node with number '" + nodenumber + "' does not exist.");
        } else {
            return makeNode(node, nodenumber);
        }
    }

    public Node getNode(int nodenumber) throws NotFoundException {
        return getNode("" + nodenumber);
    }

    public Node getNodeByAlias(String aliasname) throws NotFoundException {
        return getNode(aliasname);
    }

    public Relation getRelation(int nodenumber) throws NotFoundException {
        return getRelation("" + nodenumber);
    }

    public Relation getRelation(String nodenumber) throws NotFoundException {
        return (Relation)getNode(nodenumber);
    }

    public boolean hasNode(int nodenumber) {
        return hasNode("" + nodenumber, false);
    }

    public boolean hasNode(String nodenumber) {
        return hasNode(nodenumber, false);
    }

    // check if anode exists.
    // if isrelation is true, the method returns false if the node is not a relation
    private boolean hasNode(String nodenumber, boolean isrelation) {
        MMObjectNode node;
        try {
            node = BasicCloudContext.tmpObjectManager.getNode(account, nodenumber);
        } catch (RuntimeException e) {
            return false; // error - node inaccessible or does not exist
        }
        if (node == null) {
            return false; // node does not exist
        } else {
            if (isrelation && !(node.parent instanceof InsRel)) {
                return false;
            }
            return true;
        }
    }

    public boolean hasRelation(int nodenumber) {
        return hasNode("" + nodenumber, true);
    }

    public boolean hasRelation(String nodenumber) {
        return hasNode(nodenumber, true);
    }

    public NodeManagerList getNodeManagers() {
        Vector nodeManagers = new Vector();
        for (Enumeration builders = BasicCloudContext.mmb.getMMObjects(); builders.hasMoreElements();) {
            MMObjectBuilder bul = (MMObjectBuilder)builders.nextElement();
            if (!bul.isVirtual() && check(Operation.READ, bul.oType)) {
                nodeManagers.add(bul.getTableName());
            }
        }
        return new BasicNodeManagerList(nodeManagers, this);
    }

    public NodeManager getNodeManager(String nodeManagerName) throws NotFoundException {
        MMObjectBuilder bul = BasicCloudContext.mmb.getMMObject(nodeManagerName);
        // always look if builder exists, since otherwise
        if (bul == null) {
            throw new NotFoundException("Node manager with name " + nodeManagerName + " does not exist.");
        }
        // cache quicker, and you don't get 2000 nodetypes when you do a search....
        BasicNodeManager nodeManager = (BasicNodeManager)nodeManagerCache.get(nodeManagerName);
        if (nodeManager == null) {
            // not found in cache
            nodeManager = new BasicNodeManager(bul, this);
            nodeManagerCache.put(nodeManagerName, nodeManager);
        } else if (nodeManager.getMMObjectBuilder() != bul) {
            // cache differs
            nodeManagerCache.remove(nodeManagerName);
            nodeManager = new BasicNodeManager(bul, this);
            nodeManagerCache.put(nodeManagerName, nodeManager);
        }
        return nodeManager;
    }

    public boolean hasNodeManager(String nodeManagerName) {
        return BasicCloudContext.mmb.getMMObject(nodeManagerName) != null;
    }

    /**
     * Retrieves a node manager
     * @param nodeManagerId ID of the NodeManager to retrieve
     * @return the requested <code>NodeManager</code> if the manager exists, <code>null</code> otherwise
     */
    public NodeManager getNodeManager(int nodeManagerId) throws NotFoundException {
        return getNodeManager(typedef.getValue(nodeManagerId));
    }

    /**
     * Retrieves a RelationManager.
     * Note that you can retrieve a manager with source and destination reversed.
     * @param sourceManagerID number of the NodeManager of the source node
     * @param destinationManagerID number of the NodeManager of the destination node
     * @param roleID number of the role
     * @return the requested RelationManager
     */
    RelationManager getRelationManager(int sourceManagerId, int destinationManagerId, int roleId) {
        Set set = BasicCloudContext.mmb.getTypeRel().getAllowedRelations(sourceManagerId, destinationManagerId, roleId);
        if (set.size() > 0) {
            return new BasicRelationManager((MMObjectNode)set.iterator().next(), this);
        } else {
            log.error("Relation " + sourceManagerId + "/" + destinationManagerId + "/" + roleId + " does not exist");
            return null; // calling method throws exception
        }
    }

    public RelationManager getRelationManager(int number) throws NotFoundException {
        MMObjectNode n = BasicCloudContext.mmb.getTypeDef().getNode(number);
        if (n == null) {
            throw new NotFoundException("Relation manager with number " + number + " does not exist.");
        }
        if ((n.getBuilder() instanceof RelDef) || (n.getBuilder() instanceof TypeRel)) {
            return new BasicRelationManager(n, this);
        } else {
            throw new NotFoundException("Node with number " + number + " is not a relation manager.");
        }
    }

    public RelationManagerList getRelationManagers() {
        Vector v = BasicCloudContext.mmb.getTypeRel().searchVector("");
        return new BasicRelationManagerList(v, this);
    }

    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName) throws NotFoundException {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1) {
            throw new NotFoundException("Role '" + roleName + "' does not exist.");
        }
        int n1 = typedef.getIntValue(sourceManagerName);
        if (n1 == -1) {
            throw new NotFoundException("Source type '" + sourceManagerName + "' does not exist.");
        }
        int n2 = typedef.getIntValue(destinationManagerName);
        if (n2 == -1) {
            throw new NotFoundException("Destination type '" + destinationManagerName + "' does not exist.");
        }
        RelationManager rm = getRelationManager(n1, n2, r);
        if (rm == null) {
            throw new NotFoundException("Relation manager from '" + sourceManagerName + "' to '" + destinationManagerName + "' as '" + roleName + "' does not exist.");
        } else {
            return rm;
        }
    }

    public RelationManager getRelationManager(NodeManager source, NodeManager destination, String roleName) throws NotFoundException {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1) {
            throw new NotFoundException("Role '" + roleName + "' does not exist.");
        }
        RelationManager rm = getRelationManager(source.getNumber(), destination.getNumber(), r);
        if (rm == null) {
            throw new NotFoundException("Relation manager from '" + source + "' to '" + destination + "' as '" + roleName + "' does not exist.");
        } else {
            return rm;
        }
        
    }

    public boolean hasRelationManager(String sourceManagerName, String destinationManagerName, String roleName) {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1)  return false;
        int n1 = typedef.getIntValue(sourceManagerName);
        if (n1 == -1) return false;
        int n2 = typedef.getIntValue(destinationManagerName);
        if (n2 == -1) return false;
        return getRelationManager(n1, n2, r) != null;
    }

    public boolean  hasRelationManager(NodeManager source, NodeManager destination, String roleName) {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1) return false;
        return getRelationManager(source.getNumber(), destination.getNumber(), r) != null;
    }

    public RelationManager getRelationManager(String roleName) throws NotFoundException {
        int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
        if (r == -1) {
            throw new NotFoundException("Role '" + roleName + "' does not exist.");
        }
        return getRelationManager(r);
    }

    public RelationManagerList getRelationManagers(String sourceManagerName, String destinationManagerName, String roleName) throws NotFoundException {
        NodeManager n1 = null;
        if (sourceManagerName != null)
            n1 = getNodeManager(sourceManagerName);
        NodeManager n2 = null;
        if (destinationManagerName != null)
            n2 = getNodeManager(destinationManagerName);
        return getRelationManagers(n1, n2, roleName);
    }

    public RelationManagerList getRelationManagers(NodeManager sourceManager, NodeManager destinationManager, String roleName) throws NotFoundException {
        if (sourceManager != null) {
            return sourceManager.getAllowedRelations(destinationManager, roleName, null);
        } else if (destinationManager != null) {
            return destinationManager.getAllowedRelations(sourceManager, roleName, null);
        } else if (roleName != null) {
            int r = BasicCloudContext.mmb.getRelDef().getNumberByName(roleName);
            if (r == -1) {
                throw new NotFoundException("Role '" + roleName + "' does not exist.");
            }
            Vector v = BasicCloudContext.mmb.getTypeRel().searchVector("rnumber==" + r);
            return new BasicRelationManagerList(v, this);
        } else {
            return getRelationManagers();
        }
    }

    public boolean hasRelationManager(String roleName) {
        return BasicCloudContext.mmb.getRelDef().getNumberByName(roleName) != -1;
    }

    /**
     * Create unique temporary node number.
     * The Id starts at - 2 and is decremented each time a new id is asked
     * until Integer.MINVALUE is reached (after which counting starts again at -2).
     * @todo This may be a temporary solution. It may be desirable to immediately reserve a
     * number at the database layer, so resolving (by the transaction) will not be needed.
     * However, this needs some changes in the TemporaryNodeManager and the classes that make use of this.
     *
     * @return the temporary id as an integer
     */
    static synchronized int uniqueId() {
        if (lastRequestId > Integer.MIN_VALUE) {
            lastRequestId--;
        } else {
            lastRequestId = -2;
        }
        return lastRequestId;
    }

    /**
     * Test if a node id is a temporay id.
     * @param id the id (node numebr) to test
     * @return true if the id is a temporary id
     * @since MMBase-1.5
     */
    static boolean isTemporaryId(int id) {
        return id < -1;
    }

    public Transaction createTransaction() {
        return createTransaction(null, false);
    }

    public Transaction createTransaction(String name) throws AlreadyExistsException {
        return createTransaction(name, false);
    }

    public Transaction createTransaction(String name, boolean overwrite) throws AlreadyExistsException {
        if (name == null) {
            name = "Tran" + uniqueId();
        } else {
            Transaction oldtransaction = (Transaction)transactions.get(name);
            if (oldtransaction != null) {
                if (overwrite) {
                    oldtransaction.cancel();
                } else {
                    throw new AlreadyExistsException("Transaction with name " + name + "already exists.");
                }
            }
        }
        Transaction transaction = new BasicTransaction(name, this);
        transactions.put(name, transaction);
        return transaction;
    }

    public Transaction getTransaction(String name) {
        Transaction tran = (Transaction)transactions.get(name);
        if (tran == null) {
            tran = createTransaction(name, false);
        }
        return tran;
    }

    public CloudContext getCloudContext() {
        return cloudContext;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return userContext;
    }

    /**
     * Retrieves the current user accountname (unique)
     * @return the account name
     */
    String getAccount() {
        return account;
    }

    /**
    * Checks access rights.
    * @param operation the operation to check (READ, WRITE, CREATE, OWN)
    * @param nodeID the node on which to check the operation
    * @return <code>true</code> if access is granted, <code>false</code> otherwise
    */
    boolean check(Operation operation, int nodeID) {
        return mmbaseCop.getAuthorization().check(userContext.getUserContext(), nodeID, operation);
    }

    /**
    * Asserts access rights. throws an exception if an operation is not allowed.
    * @param operation the operation to check (READ, WRITE, CREATE, OWN)
    * @param nodeID the node on which to check the operation
    */
    void verify(Operation operation, int nodeID) {
        mmbaseCop.getAuthorization().verify(userContext.getUserContext(), nodeID, operation);
    }

    /**
    * Checks access rights.
    * @param operation the operation to check (CREATE, CHANGE_RELATION)
    * @param nodeID the node on which to check the operation
    * @param srcNodeID the source node for this relation
    * @param dstNodeID the destination node for this relation
    * @return <code>true</code> if access is granted, <code>false</code> otherwise
    */
    boolean check(Operation operation, int nodeID, int srcNodeID, int dstNodeID) {
        return mmbaseCop.getAuthorization().check(userContext.getUserContext(), nodeID, srcNodeID, dstNodeID, operation);
    }

    /**
    * Asserts access rights. throws an exception if an operation is not allowed.
    * @param operation the operation to check (CREATE, CHANGE_RELATION)
    * @param nodeID the node on which to check the operation
    * @param srcNodeID the source node for this relation
    * @param dstNodeID the destination node for this relation
    */
    void verify(Operation operation, int nodeID, int srcNodeID, int dstNodeID) {
        mmbaseCop.getAuthorization().verify(userContext.getUserContext(), nodeID, srcNodeID, dstNodeID, operation);
    }

    /**
    * initializes access rights for a newly created node
    * @param nodeID the node to init
    */
    void createSecurityInfo(int nodeID) {
        mmbaseCop.getAuthorization().create(userContext.getUserContext(), nodeID);
    }

    /**
    * removes access rights for a removed node
    * @param nodeID the node to init
    */
    void removeSecurityInfo(int nodeID) {
        mmbaseCop.getAuthorization().remove(userContext.getUserContext(), nodeID);
    }

    /**
    * updates access rights for a changed node
    * @param nodeID the node to init
    */
    void updateSecurityInfo(int nodeID) {
        mmbaseCop.getAuthorization().update(userContext.getUserContext(), nodeID);
    }

    /**
     * Converts a constraint by turning all 'quoted' fields into
     * database supported fields.
     * XXX: todo: escape characters for '[' and ']'.
     */
    private String convertClausePartToDBS(String constraints) {
        // obtain dbs for fieldname checks
        MMJdbc2NodeInterface dbs = BasicCloudContext.mmb.getDatabase();
        StringBuffer result = new StringBuffer();
        int posa = constraints.indexOf('[');
        while (posa > -1) {
            int posb = constraints.indexOf(']', posa);
            if (posb == -1) {
                posa = -1;
            } else {
                String fieldName = constraints.substring(posa + 1, posb);
                int posc = fieldName.indexOf('.', posa);
                if (posc == -1) {
                    fieldName = dbs.getAllowedField(fieldName);
                } else {
                    fieldName = fieldName.substring(0, posc + 1) + dbs.getAllowedField(fieldName.substring(posc + 1));
                }
                result.append(constraints.substring(0, posa)).append(fieldName);
                constraints = constraints.substring(posb + 1);
                posa = constraints.indexOf('[');
            }
        }
        result.append(constraints);
        return result.toString();
    }

    /**
     * Converts a constraint by turning all 'quoted' fields into
     * database supported fields.
     * XXX: todo: escape characters for '[' and ']'.
     */
    String convertClauseToDBS(String constraints) {
        if (constraints.startsWith("MMNODE")) {
            return constraints;
        } else if (constraints.startsWith("ALTA")) {
            return constraints.substring(5);
        }
        StringBuffer result = new StringBuffer();
        int posa = constraints.indexOf('\'');
        while (posa > -1) {
            int posb = constraints.indexOf('\'', 1);
            if (posb == -1) {
                posa = -1;
            } else {
                String part = constraints.substring(0, posa);
                result.append(convertClausePartToDBS(part)).append(constraints.substring(posa, posb + 1));
                constraints = constraints.substring(posb + 1);
                posa = constraints.indexOf('\'');
            }
        }
        result.append(convertClausePartToDBS(constraints));
        if (!constraints.startsWith("WHERE ")) {
            result.insert(0, "WHERE ");
        }
        return result.toString();
    }

    // javadoc inherited
    public NodeList getList(Query query) {
        if (query.isAggregating()) { // should this perhaps be a seperate method? --> Then also 'isAggregating' not needed any more
            return getResultNodeList(query);
        } else {
            return getSecureClusterNodes(query);
        }
    }


    /**
     * Aggregating query result.
     * @since MMBase-1.7
     */
    protected NodeList getResultNodeList(Query query) {
        try {
            Authorization auth = mmbaseCop.getAuthorization();
            boolean checked = false; // query should alway be 'BasicQuery' but if not, for some on-fore-seen reason..
            
            if (query instanceof BasicQuery) {
                BasicQuery bquery = (BasicQuery) query;
                if (bquery.isSecure()) {
                    checked = true;
                } else {
                    Authorization.QueryCheck check = auth.check(userContext.getUserContext(), query, Operation.READ);
                    bquery.setSecurityConstraint(check);
                    checked = bquery.isSecure();
                }
            }
        
            if (! checked) {
                log.warn("Query could not be completely modified by security: Aggregated result might be wrong");
            }
            AggregatedResultCache cache = AggregatedResultCache.getCache();

            List resultList = (List) cache.get(query);
            if (resultList == null) {            
                ResultBuilder resultBuilder = new ResultBuilder(BasicCloudContext.mmb, query);
                resultList = BasicCloudContext.mmb.getDatabase().getNodes(query, resultBuilder);
                cache.put(query, resultList);
            }
            query.markUsed();
            NodeManager tempNodeManager;
            if (resultList.size() > 0) {
                tempNodeManager = new VirtualNodeManager((MMObjectNode)resultList.get(0), this);
            } else {
                tempNodeManager = new VirtualNodeManager(this);
            }
            NodeList resultNodeList = new BasicNodeList(resultList, tempNodeManager);
            resultNodeList.setProperty("query", query);
            return resultNodeList;
        } catch (SearchQueryException sqe) {
            throw new BridgeException(sqe);
        }
    }

    /**
     * Result with all Cluster - MMObjectNodes (without security, but with cache)
     * @since MMBase-1.7
     */

    protected List    getClusterNodes(Query query) {
        
        
        ClusterBuilder clusterBuilder = BasicCloudContext.mmb.getClusterBuilder();
        // check multilevel cache if needed
        List resultList = (List)multilevelCache.get(query);
        
        // if unavailable, obtain from database
        if (resultList == null) {
            log.debug("result list is null, getting from database");
            try {
                resultList = clusterBuilder.getClusterNodes(query);
            } catch (SearchQueryException sqe) {
                throw new BridgeException(sqe);
            }
            multilevelCache.put(query, resultList);
        }
    
        query.markUsed();

        return resultList;
    }

    /**
     * Result with Cluster Nodes (checked security)
     * @since MMBase-1.7
     */
    protected NodeList getSecureClusterNodes(Query query) {
        Authorization auth = mmbaseCop.getAuthorization();
        boolean checked = false; // query should alway be 'BasicQuery' but if not, for some on-fore-seen reason..

        if (query instanceof BasicQuery) {
            BasicQuery bquery = (BasicQuery) query;
            if (bquery.isSecure()) {
                checked = true;
            } else {
                Authorization.QueryCheck check = auth.check(userContext.getUserContext(), query, Operation.READ);
                bquery.setSecurityConstraint(check);
                checked = bquery.isSecure();
            }
        }
        
        //assert(resultList != null);
        List resultList = getClusterNodes(query);

        if (log.isDebugEnabled()) {
            log.debug("Creating NodeList of size " + resultList.size());
        }

        BasicNodeList resultNodeList; // this will be the result NodeList

        { // create resultNodeList

            NodeManager tempNodeManager = null;
            if (resultList.size() > 0) {
                tempNodeManager = new VirtualNodeManager((MMObjectNode)resultList.get(0), this);
            } else {
                tempNodeManager = new VirtualNodeManager(this);
            }
            resultNodeList = new BasicNodeList(resultList, tempNodeManager);
            resultNodeList.setProperty("query", query);
        }


        if (! checked) {
            log.debug("Starting read-check");
            // resultNodeList is now a BasicNodeList; read restriction should only be applied now
            // assumed it though, that it contain _only_ MMObjectNodes..

            // get authorization for this call only
            
            UserContext user = userContext.getUserContext();
            List steps = query.getSteps();
            
            log.debug("Creating iterator");
            ListIterator li = resultNodeList.listIterator();
            resultNodeList.autoConvert = false; // make sure no conversion to Node happen
            while (li.hasNext()) {
                log.debug("next");
                Object o = li.next();
                if (log.isDebugEnabled()) {
                    log.debug(o.getClass().getName());
                }
                MMObjectNode node = (MMObjectNode)o;
                boolean mayRead = true;
                for (int j = 0; mayRead && (j < steps.size()); ++j) {
                    int nodenr = node.getIntValue(((Step)steps.get(j)).getTableName() + ".number");
                    if (nodenr != -1) {
                        mayRead = auth.check(user, nodenr, Operation.READ);
                    }
                }
                if (!mayRead)
                    li.remove();
            }
            resultNodeList.autoConvert = true;
        }
        
        return resultNodeList;
    }

    //javadoc inherited
    public NodeList getList(
        String startNodes,
        String nodePath,
        String fields,
        String constraints,
        String orderby,
        String directions,
        String searchDir,
        boolean distinct) {

        {
            // the bridge test case say that you may also specifiy empty string (why?)
            if ("".equals(startNodes))
                startNodes = null;
            if ("".equals(fields))
                fields = null;
            if ("".equals(constraints))
                constraints = null;
            if ("".equals(searchDir))
                searchDir = null;
            // check invalid search command
            org.mmbase.util.Encode encoder = new org.mmbase.util.Encode("ESCAPE_SINGLE_QUOTE");
            // if(startNodes != null) startNodes = encoder.encode(startNodes);
            // if(nodePath != null) nodePath = encoder.encode(nodePath);
            // if(fields != null) fields = encoder.encode(fields);
            if (orderby != null)
                orderby = encoder.encode(orderby);
            if (directions != null)
                directions = encoder.encode(directions);
            if (searchDir != null)
                searchDir = encoder.encode(searchDir);
            if (constraints != null && !validConstraints(constraints)) {
                throw new BridgeException("invalid constraints:" + constraints);
            }
        }

        // create query object
        Query query;
        {
            ClusterBuilder clusterBuilder = BasicCloudContext.mmb.getClusterBuilder();
            int search = -1;
            if (searchDir != null) {
                search = ClusterBuilder.getSearchDir(searchDir);
            }

            List snodes = StringSplitter.split(startNodes);
            List tables = StringSplitter.split(nodePath);
            List f = StringSplitter.split(fields);
            List orderVec = StringSplitter.split(orderby);
            List d = StringSplitter.split(directions);
            try {
                query = new BasicQuery(this, clusterBuilder.getMultiLevelSearchQuery(snodes, f, distinct ? "YES" : "NO", tables, constraints, orderVec, d, search));
            } catch (IllegalArgumentException iae) {
                throw new BridgeException(iae);
            }
        }

        return getList(query);
    }

    /**
     * set the Context of the current Node
     */
    void setContext(int nodeNumber, String context) {
        mmbaseCop.getAuthorization().setContext(userContext.getUserContext(), nodeNumber, context);
    }

    /**
     * get the Context of the current Node
     */
    String getContext(int nodeNumber) {
        return mmbaseCop.getAuthorization().getContext(userContext.getUserContext(), nodeNumber);
    }

    /**
     * get the Contextes which can be set to this specific node
     */
    StringList getPossibleContexts(int nodeNumber) {
        return new BasicStringList(mmbaseCop.getAuthorization().getPossibleContexts(userContext.getUserContext(), nodeNumber));
    }

    public void setLocale(Locale l) {
        if (l == null) {
            locale = new Locale(BasicCloudContext.mmb.getLanguage(), "");
        } else {
            locale = l;
        }
    }
    public Locale getLocale() {
        return locale;
    }

    /** returns false, when escaping wasnt closed, or when a ";" was found outside a escaped part (to prefent spoofing) */
    boolean validConstraints(String constraints) {
        // first remove all the escaped "'" ('' occurences) chars...
        String remaining = constraints;
        while (remaining.indexOf("''") != -1) {
            int start = remaining.indexOf("''");
            int stop = start + 2;
            if (stop < remaining.length()) {
                String begin = remaining.substring(0, start);
                String end = remaining.substring(stop);
                remaining = begin + end;
            } else {
                remaining = remaining.substring(0, start);
            }
        }
        // assume we are not escaping... and search the string..
        // Keep in mind that at this point, the remaining string could contain different information
        // than the original string. This doesnt matter for the next sequence...
        // but it is important to realize!
        while (remaining.length() > 0) {
            if (remaining.indexOf('\'') != -1) {
                // we still contain a "'"
                int start = remaining.indexOf('\'');

                // escaping started, but no stop
                if (start == remaining.length()) {
                    log.warn("reached end, but we are still escaping(you should sql-escape the search query inside the jsp-page?)\noriginal:" + constraints);
                    return false;
                }

                String notEscaped = remaining.substring(0, start);
                if (notEscaped.indexOf(';') != -1) {
                    log.warn(
                        "found a ';' outside the constraints(you should sql-escape the search query inside the jsp-page?)\noriginal:"
                            + constraints
                            + "\nnot excaped:"
                            + notEscaped);
                    return false;
                }

                int stop = remaining.substring(start + 1).indexOf('\'');
                if (stop < 0) {
                    log.warn(
                        "reached end, but we are still escaping(you should sql-escape the search query inside the jsp-page?)\noriginal:"
                            + constraints
                            + "\nlast escaping:"
                            + remaining.substring(start + 1));
                    return false;
                }
                // we added one to to start, thus also add this one to stop...
                stop = start + stop + 1;

                // when the last character was the stop of our escaping
                if (stop == remaining.length()) {
                    return true;
                }

                // cut the escaped part from the string, and continue with resting sting...
                remaining = remaining.substring(stop + 1);
            } else {
                if (remaining.indexOf(';') != -1) {
                    log.warn("found a ';' inside our constrain:" + constraints);
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    public boolean mayRead(int nodenumber) {
        return mayRead(nodenumber + "");
    }

    public boolean mayRead(String nodenumber) {
        MMObjectNode node;
        try {
            node = BasicCloudContext.tmpObjectManager.getNode(account, nodenumber);
        } catch (RuntimeException e) {
            throw new NotFoundException("Something went wrong while getting node with number " + nodenumber, e);
        }
        if (node == null) {
            throw new NotFoundException("Node with number '" + nodenumber + "' does not exist.");
        } else {
            int nodenr = node.getNumber();
            if (nodenr == -1) {
                return true; // temporary node
            } else {
                return check(Operation.READ, node.getNumber()); // check read access
            }
        }
    }

    // javadoc inherited
    public Query createQuery() {
        return new BasicQuery(this);
    }


    public Query createAggregatedQuery() {
        return new BasicQuery(this, true);
    }

    /**
     * Compares this cloud to the passed object.
     * Returns 0 if they are equal, -1 if the object passed is a Cloud and larger than this cloud,
     * and +1 if the object passed is a Cloud and smaller than this cloud.
     * @todo There is no specific order in which clouds are ordered at this moment.
     *       Currently, all clouds within one CloudContext are treated as being equal.
     * @param o the object to compare it with
     */
    public int compareTo(Object o) {
        int h1 = ((Cloud)o).getCloudContext().hashCode();
        int h2 = cloudContext.hashCode();
        if (h1 > h2) {
            return -1;
        } else if (h1 < h2) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares this cloud to the passed object, and returns true if they are equal.
     * @todo Add checks for multiple clouds in the same cloudcontext
     *       Currently, all clouds within one CloudContext are treated as being equal.
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        // XXX: Currently, all clouds (i.e. transactions/user clouds) within a CloudContext
        // are treated as the 'same' cloud. This may change in future implementations
        return (o instanceof Cloud) && cloudContext.equals(((Cloud)o).getCloudContext());
    }

}
