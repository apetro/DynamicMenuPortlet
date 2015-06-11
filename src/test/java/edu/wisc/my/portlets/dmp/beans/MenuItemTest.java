/*******************************************************************************
* Copyright 2004, The Board of Regents of the University of Wisconsin System.
* All rights reserved.
*
* A non-exclusive worldwide royalty-free license is granted for this Software.
* Permission to use, copy, modify, and distribute this Software and its
* documentation, with or without modification, for any purpose is granted
* provided that such redistribution and use in source and binary forms, with or
* without modification meets the following conditions:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* 3. Redistributions of any form whatsoever must retain the following
* acknowledgement:
*
* "This product includes software developed by The Board of Regents of
* the University of Wisconsin System."
*
*THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
*WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
*BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
*PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
*THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
*INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
*PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
*LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
*OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
*ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/
package edu.wisc.my.portlets.dmp.beans;

import edu.wisc.my.portlets.dmp.dao.filter.FilteringMenuItem;
import junit.framework.TestCase;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @since 1.0
 */
public class MenuItemTest extends TestCase {
    
    public void testLoopDetection() {
        final MenuItem item1 = new MenuItem();
        item1.setName("Item1");
        
        final MenuItem item2 = new MenuItem();
        item2.setName("Item2");
        
        final MenuItem item3 = new MenuItem();
        item3.setName("Item3");
        
        item1.setChildren(new MenuItem[] {item2});
        item2.setChildren(new MenuItem[] {item3});
       
        
        try {
            item3.setChildren(new MenuItem[] {item1});
            fail("Setting children would result in a loop, should be detected");
        }
        catch (IllegalArgumentException iae) {
            //This is what we want!
        }
    }

    /**
     * Test that hasMatchingGroup() correctly returns true when presented groups have a match.
     */
    public void testHasMatchingGroup() {

        final MenuItem menuItem = new MenuItem();
        menuItem.setGroups( new String[] {"privilegedFew", "huddledMasses"});

        final String[] userGroups = new String[] { "privilegedFew", "someIrrelevantGroup"};

        assertTrue(menuItem.hasMatchingGroup(userGroups));

    }

    /**
     * Test that hasMatchingGroup() correctly returns false when presented groups do not match.
     */
    public void testDoesNotHaveMatchingGroup() {

        final MenuItem menuItem = new MenuItem();
        menuItem.setGroups( new String[] {"privilegedFew", "huddledMasses"});

        final String[] userGroups = new String[] { "unprivileged"};

        assertFalse(menuItem.hasMatchingGroup(userGroups));

    }

    /**
     * Test that returns false when the menu item is not granted to any groups.
     */
    public void testDoesNotHaveMatchingGroupWhenItemGroupsAreNull() {

        final MenuItem menuItem = new MenuItem();

        final String[] userGroups = new String[] { "privilegedFew"};

        assertFalse(menuItem.hasMatchingGroup(userGroups));

    }

    /**
     * Test that returns false when the user has null groups.
     */
    public void testDoesNotHaveMatchingGroupWhenUserGroupsAreNull() {

        final MenuItem menuItem = new MenuItem();
        menuItem.setGroups( new String[] {"privilegedFew", "huddledMasses"});

        assertFalse(menuItem.hasMatchingGroup(null));

    }

    /**
     * Test that a semantically equal subclass is evaluated as equal.
     */
    public void testEqualHandlingSubclasses() {

        MenuItem menuItem = new MenuItem();

        FilteringMenuItem filteringMenuItem = new FilteringMenuItem(menuItem);

        menuItem.setName("A menu item");

        assertEquals(menuItem , filteringMenuItem);
        assertEquals(filteringMenuItem, menuItem);

    }
    
    
}
