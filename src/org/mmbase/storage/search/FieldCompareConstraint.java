/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A constraint that compares a stepfield value with another value.
 * <p>
 * This corresponds with comparison operators <, =, > and LIKE in SQL SELECT-syntax. 
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 * @since MMBase-1.7
 */
public interface FieldCompareConstraint extends FieldConstraint {
    /**
     * Gets the operator used to compare values. This must be either <code>LESS</code>, <code>EQUAL</code> or <code>GREATER</code>. <code>LIKE</code> is allowed as well when the associated field is of string type.
     */
    int getOperator();

    int LESS = 1;
    int EQUAL = 2;
    int GREATER = 3;
    int LIKE = 4;
}
