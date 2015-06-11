/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.portlets.dmp.dao;

/**
 * Provides a method for getting group memebership information for a user.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 */
public interface GroupsDao {
    /**
     * @param userName The user to get the group array for, may be null which impies an anonymous user.
     * @return The groups the user is a member of, null implies the user is not a member of any groups.
     */
    public String[] getContainingGroups(String userName);
}
