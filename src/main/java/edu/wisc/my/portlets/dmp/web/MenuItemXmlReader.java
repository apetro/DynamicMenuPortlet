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

import java.io.IOException;
import java.util.Set;
import java.util.Stack;

import javax.portlet.WindowState;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import edu.wisc.my.portlets.dmp.beans.MenuItem;

/**
 * Takes a MenuItemInputSource containing a MenuItem structure and generates
 * well formed XML representing the structure using SAX events.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 */
public class MenuItemXmlReader implements XMLReader, Locator {
    private static final String ELEMENT_MENUS = "menus";
    private static final String ELEMENT_MENU = "menu";
    private static final String ELEMENT_MENU_NAME = "name";
    private static final String ELEMENT_MENU_ITEM = "menu_item";
    private static final String ELEMENT_MENU_ITEM_NAME = "name";
    private static final String ELEMENT_MENU_ITEM_DESCRIPTION = "description";
    private static final String ELEMENT_MENU_ITEM_TARGET = "target";
    private static final String ELEMENT_MENU_ITEM_URL = "url";
    private static final String ELEMENT_MENU_ITEM_GROUPS = "groups";
    private static final String ELEMENT_MENU_ITEM_GROUP = "group";
    private static final String ELEMENT_MENU_ITEM_WINDOW_STATES = "states";
    private static final String ELEMENT_MENU_ITEM_WINDOW_STATE = "state";
    private static final String ELEMENT_MENU_ITEM_CHILDREN = "children";

    private static final Attributes EMPTY_ATTR = new AttributesImpl();

    private final Stack<String> elementStack = new Stack<String>();
    private String lastElement = "";

    private ContentHandler contentHandler = null;
    private EntityResolver entityResolver = null;
    private DTDHandler dtdHandler = null;
    private ErrorHandler errorHandler = null;

    
    /**
     * @see org.xml.sax.Locator#getColumnNumber()
     */
    public int getColumnNumber() {
        return 0;
    }

    /**
     * @see org.xml.sax.Locator#getLineNumber()
     */
    public int getLineNumber() {
        return 0;
    }

    /**
     * @see org.xml.sax.Locator#getPublicId()
     */
    public String getPublicId() {
        return null;
    }

    /**
     * @see org.xml.sax.Locator#getSystemId()
     */
    public String getSystemId() {
        return null;
    }
    

    /**
     * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
     */
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name) || "http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            throw new SAXNotSupportedException("The feature '" + name + "' is not supported by this XMLReader");
        }

        throw new SAXNotRecognizedException("The feature '" + name + "' is not recognized by this XMLReader");
    }

    /**
     * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
     */
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name) || "http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            throw new SAXNotSupportedException("The feature '" + name + "' is not supported by this XMLReader");
        }

        throw new SAXNotRecognizedException("The feature '" + name + "' is not recognized by this XMLReader");
    }

    /**
     * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
     */
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("The property '" + name + "' is not recognized by this XMLReader");
    }

    /**
     * @see org.xml.sax.XMLReader#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("The property '" + name + "' is not recognized by this XMLReader");
    }

    /**
     * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
     */
    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    /**
     * @see org.xml.sax.XMLReader#getEntityResolver()
     */
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    /**
     * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
     */
    public void setDTDHandler(DTDHandler handler) {
        this.dtdHandler = handler;
    }

    /**
     * @see org.xml.sax.XMLReader#getDTDHandler()
     */
    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    /**
     * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    /**
     * @see org.xml.sax.XMLReader#getContentHandler()
     */
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    /**
     * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
     */
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    /**
     * @see org.xml.sax.XMLReader#getErrorHandler()
     */
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    /**
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public void parse(InputSource input) throws IOException, SAXException {
        if (!(input instanceof MenuItemInputSource))
            throw new IllegalArgumentException("Must pass an instance of MenuItemInputSource to parse(InputSource) input.class='" + input.getClass() + "'");
        if (this.contentHandler == null)
            throw new IllegalStateException("ContentHandler must be set");

        this.elementStack.clear();
        this.contentHandler.setDocumentLocator(this);
        this.contentHandler.startDocument();
        
        final MenuItemInputSource menuSource = (MenuItemInputSource)input;
        
        //Note, the use of { } blocks in this method is simply for
        //readability. Looking at the event calls can make a bit
        //more sence when indentation is included.
        this.startElement(ELEMENT_MENUS);
        {
            final Set<String> menuNames = menuSource.getMenuNames();
            for (final String menuName : menuNames) {
                final MenuItem rootItem = menuSource.getRootMenuItem(menuName);
            
                this.startElement(ELEMENT_MENU);
                {
                    this.startElement(ELEMENT_MENU_NAME);
                    {
                        this.characters(menuName);
                    }
                    this.endElement();
                    
                    this.renderItem(rootItem);
                }
                this.endElement();
            }
        }
        this.endElement();

        this.contentHandler.endDocument();
    }

    /**
     * @see org.xml.sax.XMLReader#parse(java.lang.String)
     */
    public void parse(String argsystemId) throws IOException, SAXException {
        throw new UnsupportedOperationException("Use parse(InputSource) instead");
    }


    /**
     * Generates SAX events for a MenuItem object. Recurses on child items.
     * 
     * @param item The MenuItem object to generate events for.
     * @throws SAXException If an error occurs while generating the events.
     */
    private void renderItem(MenuItem item) throws SAXException {
        //Note, the use of { } blocks in this method is simply for
        //readability. Looking at the event calls can make a bit
        //more sence when indentation is included.
        
        this.startElement(ELEMENT_MENU_ITEM);
        {
            this.startElement(ELEMENT_MENU_ITEM_NAME);
            {
                this.characters(item.getName());
            }
            this.endElement();

            this.startElement(ELEMENT_MENU_ITEM_DESCRIPTION);
            {
                this.characters(item.getDescription());
            }
            this.endElement();

            this.startElement(ELEMENT_MENU_ITEM_TARGET);
            {
                this.characters(item.getTarget());
            }
            this.endElement();

            this.startElement(ELEMENT_MENU_ITEM_URL);
            {
                this.characters(item.getUrl());
            }
            this.endElement();

            this.startElement(ELEMENT_MENU_ITEM_GROUPS);
            {
                final String[] groups = item.getGroups();
                for (int index = 0; index < groups.length; index++) {
                    this.startElement(ELEMENT_MENU_ITEM_GROUP);
                    {
                        this.characters(groups[index]);
                    }
                    this.endElement();
                }
            }
            this.endElement();

            this.startElement(ELEMENT_MENU_ITEM_WINDOW_STATES);
            {
                final WindowState[] states = item.getDisplayStates();
                for (int index = 0; index < states.length; index++) {
                    this.startElement(ELEMENT_MENU_ITEM_WINDOW_STATE);
                    {
                        this.characters(states[index].toString().toUpperCase());
                    }
                    this.endElement();
                }
            }
            this.endElement();

            this.startElement(ELEMENT_MENU_ITEM_CHILDREN);
            {
                final MenuItem[] children = item.getChildren();
                for (int index = 0; index < children.length; index++) {
                    this.renderItem(children[index]);
                }
            }
            this.endElement();
        }

        this.endElement();
    }

    /**
     * Generates a new line and appropriate indentation for output formating.
     * 
     * @throws SAXException
     */
    private void indent() throws SAXException {
        this.characters("\n");
        
        final int depth = this.elementStack.size();
        for (int index = 0; index < depth; index++) {
            this.characters("    ");
        }
    }

    /**
     * Generates a start element event, adds the element to a stack so a corresponding end element
     * call can be made.
     * 
     * @param elementName The element to start.
     * @throws SAXException
     */
    private void startElement(String elementName) throws SAXException {
        if (this.lastElement != null)
            this.indent();

        this.contentHandler.startElement(null, elementName, elementName, EMPTY_ATTR);
        this.elementStack.push(elementName);
        this.lastElement = elementName;
    }

    /**
     * Generates a characaters event if chars is not null.
     * 
     * @param chars The characters to print.
     * @throws SAXException
     */
    private void characters(String chars) throws SAXException {
        if (chars != null) {
            final char[] charArray = chars.toCharArray();
            this.contentHandler.characters(charArray, 0, charArray.length);
        }
        this.lastElement = null;
    }

    /**
     * Uses the element stack to end the last start element event. 
     * 
     * @throws SAXException
     */
    private void endElement() throws SAXException {
        final String elementName = this.elementStack.pop();
        
        if (this.lastElement != null && !this.lastElement.equals(elementName))
            this.indent();

        this.contentHandler.endElement(null, elementName, elementName);
        this.lastElement = "";
    }
}