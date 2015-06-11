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
package edu.wisc.my.portlets.dmp.tools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.portlet.WindowState;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import edu.wisc.my.portlets.dmp.beans.MenuItem;


/**
 * Monitors SAX events and generates DMP menus.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 */
public class MenuItemGeneratingHandler implements ContentHandler {
    //Locations
    private static final String MENU = "/menus/menu";
    private static final String MENU_ITEM = "/menu_item";
    private static final String MENU_ITEM_GROUPS = "/menu_item/groups";
    private static final String MENU_ITEM_STATES = "/menu_item/states";
    private static final String MENU_ITEM_CHILDREN = "/menu_item/children";
    
    //Text
    private static final String MENU_NAME = "/menu/name";
    private static final String MENU_ITEM_NAME = "/menu_item/name";
    private static final String MENU_ITEM_DESCRIPTION = "/menu_item/description";
    private static final String MENU_ITEM_TARGET = "/menu_item/target";
    private static final String MENU_ITEM_URL = "/menu_item/url";
    private static final String MENU_ITEM_GROUP = "/menu_item/groups/group";
    private static final String MENU_ITEM_STATE = "/menu_item/states/state";

    private final XmlLocationStack location = new XmlLocationStack();
    private final Map menus = new HashMap();
    
    private String menuName = null;
    private MenuItem rootItem = null;
    private Stack menuItemStack = new Stack();
    private StringBuffer chars = null; 
    
    /**
     * After processing a map of all the menus this handler created
     * are available as a Map. The key is the menu name string the
     * value is the root MenuItem object.
     * 
     * @return Returns the menus.
     */
    public Map getMenus() {
        return this.menus;
    }
    
    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        final String locationPath = this.location.startElement(qName);
        
        if (locationPath.endsWith(MENU_NAME) ||
                locationPath.endsWith(MENU_ITEM_NAME) ||
                locationPath.endsWith(MENU_ITEM_DESCRIPTION) ||
                locationPath.endsWith(MENU_ITEM_TARGET) ||
                locationPath.endsWith(MENU_ITEM_URL) || 
                locationPath.endsWith(MENU_ITEM_GROUP) ||
                locationPath.endsWith(MENU_ITEM_STATE)) {
            
            this.chars = new StringBuffer(512);
        }
        else if (locationPath.endsWith(MENU_ITEM)) {
            this.menuItemStack.push(new MenuItem());
        }
        else if (locationPath.endsWith(MENU_ITEM_GROUPS) ||
                locationPath.endsWith(MENU_ITEM_STATES) ||
                locationPath.endsWith(MENU_ITEM_CHILDREN)) {

            this.menuItemStack.push(new LinkedList());
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.chars != null)
            this.chars.append(ch, start, length);
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        final String locationPath = this.location.endElement(qName);
        
        if (locationPath.equals(MENU)) {
            this.menus.put(this.menuName, this.rootItem);
            this.menuName = null;
            this.rootItem = null;
            this.menuItemStack.clear();
        }
        else if (locationPath.endsWith(MENU_ITEM)) {
            final MenuItem item = (MenuItem)this.menuItemStack.pop();
            
            if (this.location.toString().endsWith(MENU_ITEM_CHILDREN)) {
                final List children = (List)this.menuItemStack.peek();
                children.add(item);
            }
            else {
                this.rootItem = item;
            }
        }
        else if (locationPath.endsWith(MENU_ITEM_GROUPS)) {
            final List groups = (List)this.menuItemStack.pop();
            final MenuItem currentItem = (MenuItem)this.menuItemStack.peek();
            currentItem.setGroups((String[])groups.toArray(new String[groups.size()]));
        }
        else if (locationPath.endsWith(MENU_ITEM_STATES)) {
            final List states = (List)this.menuItemStack.pop();
            final MenuItem currentItem = (MenuItem)this.menuItemStack.peek();
            currentItem.setDisplayStates((WindowState[])states.toArray(new WindowState[states.size()]));
        }
        else if (locationPath.endsWith(MENU_ITEM_CHILDREN)) {
            final List children = (List)this.menuItemStack.pop();
            final MenuItem currentItem = (MenuItem)this.menuItemStack.peek();
            currentItem.setChildren((MenuItem[])children.toArray(new MenuItem[children.size()]));
        }
        else if (locationPath.endsWith(MENU_NAME)) {
            this.menuName = this.chars.toString();
        }
        else if (locationPath.endsWith(MENU_ITEM_NAME)) {
            final MenuItem currentItem = (MenuItem)this.menuItemStack.peek();
            currentItem.setName(this.chars.toString());
        }
        else if (locationPath.endsWith(MENU_ITEM_DESCRIPTION)) {
            final MenuItem currentItem = (MenuItem)this.menuItemStack.peek();
            currentItem.setDescription(this.chars.toString());
        }
        else if (locationPath.endsWith(MENU_ITEM_TARGET)) {
            final MenuItem currentItem = (MenuItem)this.menuItemStack.peek();
            currentItem.setTarget(this.chars.toString());
        }
        else if (locationPath.endsWith(MENU_ITEM_URL)) {
            final MenuItem currentItem = (MenuItem)this.menuItemStack.peek();
            currentItem.setUrl(this.chars.toString());
        }
        else if (locationPath.endsWith(MENU_ITEM_GROUP)) {
            final List groups = (List)this.menuItemStack.peek();
            groups.add(this.chars.toString());
        }
        else if (locationPath.endsWith(MENU_ITEM_STATE)) {
            final List states = (List)this.menuItemStack.peek();
            states.add(new WindowState(this.chars.toString()));
        }
            
        if (this.chars != null) {
            this.chars = null;
        }
    }
    
    
    public void setDocumentLocator(Locator locator) {
    }
    public void startDocument() throws SAXException {
    }
    public void endDocument() throws SAXException {
    }
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }
    public void endPrefixMapping(String prefix) throws SAXException {
    }
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }
    public void processingInstruction(String target, String data) throws SAXException {
    }
    public void skippedEntity(String name) throws SAXException {
    }
}
