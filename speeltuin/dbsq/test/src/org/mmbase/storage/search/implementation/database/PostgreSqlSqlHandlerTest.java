package org.mmbase.storage.search.implementation.database;

import junit.textui.TestRunner;
import org.mmbase.storage.search.*;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.implementation.database.MySqlSqlHandler;
import junit.framework.*;
import org.mmbase.module.corebuilders.FieldDefs;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.3 $
 */
public class PostgreSqlSqlHandlerTest extends TestCase {
    
    /** Test instance. */
    private PostgreSqlSqlHandler instance;
    
    /** Disallowed values map. */
    private Map disallowedValues = null;
    
    /** Prefix applied to buildernames to create tablenames. */
    private String prefix = null;
    
    /** MMBase query. */
    private MMBase mmbase = null;
    
    /** Images builder, used as builder example. */
    private MMObjectBuilder images = null;
    
    /** Test query. */
    private BasicSearchQuery query = null;
    
    public PostgreSqlSqlHandlerTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        MMBaseContext.init();
        mmbase = MMBase.getMMBase();
        images = mmbase.getBuilder("images");
        
        // Disallowed fields map.
        disallowedValues = new HashMap();
        disallowedValues.put("number", "m_number");
        instance = new PostgreSqlSqlHandler(disallowedValues);
        
        prefix = mmbase.getBaseName() + "_";
        
        query = new BasicSearchQuery();
        BasicStep imageStep = query.addStep(images);
        FieldDefs imageNumber = images.getField("number");
        BasicStepField imageNumberField = query.addField(imageStep, imageNumber);
        Constraint constraint = new BasicFieldNullConstraint(imageNumberField);
        query.setConstraint(constraint);
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of getSupportLevel method, of class org.mmbase.storage.search.implementation.database.PostgreSqlSqlHandler. */
    public void testGetSupportLevel() throws Exception{
        // Support max number.
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(-1);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        // Support offset.
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setOffset(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setOffset(0);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    /** Test of getSupportLevel2(Constraint,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.PostgreSqlSqlHandler. */
    public void testGetSupportLevel2() throws Exception {
        // Should return basic support level of constraint.
        SearchQuery query = new BasicSearchQuery();
        Constraint constraint = new TestConstraint(SearchQueryHandler.SUPPORT_NONE);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_NONE);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_WEAK);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_WEAK);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_NORMAL);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_NORMAL);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_OPTIMAL);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    /** Test of toSql method, of class org.mmbase.storage.search.implementation.database.PostgreSqlSqlHandler. */
    public void testToSql() throws Exception {
        // Test use of "LIMIT/OFFSET" construct.
        assertTrue(instance.toSql(query, instance), 
        instance.toSql(query, instance).equals(
        "SELECT m_number FROM " 
        + prefix + "images WHERE m_number IS NULL"));
        
        query.setMaxNumber(100);
        assertTrue(instance.toSql(query, instance), 
        instance.toSql(query, instance).equals(
        "SELECT m_number FROM " 
        + prefix + "images WHERE m_number IS NULL LIMIT 100"));
        
        query.setOffset(50);
        assertTrue(instance.toSql(query, instance), 
        instance.toSql(query, instance).equals(
        "SELECT m_number FROM " 
        + prefix 
        + "images WHERE m_number IS NULL LIMIT 100 OFFSET 50"));

        query.setMaxNumber(-1);
        assertTrue(instance.toSql(query, instance), 
        instance.toSql(query, instance).equals(
        "SELECT m_number FROM " 
        + prefix + "images WHERE m_number IS NULL LIMIT "
        + Integer.MAX_VALUE + " OFFSET 50"));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(PostgreSqlSqlHandlerTest.class);
        
        return suite;
    }
    
}
