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
/*
 * Created on Apr 19, 2005
 */
package edu.wisc.my.portlets.dmp.dao;

import edu.wisc.my.portlets.dmp.beans.MenuItem;
 
/**
 * This interface defines the general methods for storing and
 * retrieving menus.
 * 
 * @author sschwartz
 */
public interface MenuDao {

    /**
     * Gets an array of named menus that have been published.
     */
    public String[] getPublishedMenuNames();

    /**
     * Gets the root MenuItem for a named menu. Will return null if no menu exists for
     * the name.
     * 
     * @param menuName the String object
     */
    public MenuItem getMenu(String menuName);

    /**
     * Gets the root MenuItem for a named menu and set of groups. Will return null if no
     * menu exists for the name or if the root item cannot be displayed for any of the
     * passed groups
     *      
     * @param menuName the String object
     * @param userGroups the String [] object
     */
    public MenuItem getMenu(String menuName, String[] userGroups);
    
    /**
     * Stores a menu tree with the specified name.
     * 
     * @param menuName the String object
     * @param rootItem the MenuItem object
     */
    public void storeMenu(String menuName, MenuItem rootItem);

    /**
     * Deletes the named menu.
     * 
     * @param menuName the String object
     */
    public void deleteMenu(String menuName);
}
