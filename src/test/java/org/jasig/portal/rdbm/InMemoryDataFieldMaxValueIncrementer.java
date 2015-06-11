/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.rdbm;

import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 */
public class InMemoryDataFieldMaxValueIncrementer extends AbstractDataFieldMaxValueIncrementer {
    private long key;

    /**
     * @see org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer#getNextKey()
     */
    protected synchronized long getNextKey() {
        return this.key++;
    }
}
