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
package edu.wisc.my.portlets.dmp.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.xml.sax.InputSource;

import edu.wisc.my.portlets.dmp.beans.MenuItem;


/**
 * Doesn't really do any InputSource methods, just used to pass a
 * MenuItem structure to the MenuItemXmlReader
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Id: MenuItemInputSource.java,v 1.1 2008/09/29 00:53:51 dalquist Exp $
 */
public class MenuItemInputSource extends InputSource {
    private final Map<String, MenuItem> menus;
    
    /**
     * Convience to specify a single menu.
     * 
     * @param menuName The name of the menu.
     * @param rootItem The root MenuItem for the menu.
     */
    public MenuItemInputSource(String menuName, MenuItem rootItem) {
        if (menuName == null)
            throw new IllegalArgumentException("menuName cannot be null");
        if (rootItem == null)
            throw new IllegalArgumentException("rootItem cannot be null");
        
        final Map<String, MenuItem> menusBuilder = new HashMap<String, MenuItem>();
        menusBuilder.put(menuName, rootItem);
        
        this.menus = Collections.unmodifiableMap(menusBuilder);
    }
    
    /**
     * Specify a large number of menus. Keys must be String and values
     * must be MenuItem.
     * 
     * @param menus A Map of menu names to root MenuItems
     */
    public MenuItemInputSource(Map<String, MenuItem> menus) {
        if (menus == null)
            throw new IllegalArgumentException("menus cannot be null");
        
        this.menus = Collections.unmodifiableMap(new HashMap<String, MenuItem>(menus));
    }
    
    /**
     * Get a Set of named menus in this source. Will never
     * be null.
     * 
     * @return A Set of named menus in this source. 
     */
    public Set<String> getMenuNames() {
        return this.menus.keySet();
    }
    
    /**
     * Get the root MenuItem for the named menu. null if no
     * menu exists for the item.
     * 
     * @param menuName The name of the menu to get.
     * @return The root MenuItem for the menu.
     */
    public MenuItem getRootMenuItem(String menuName) {
        return this.menus.get(menuName);
    }
}
