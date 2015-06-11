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

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.wisc.my.portlets.dmp.beans.MenuItem;
import edu.wisc.my.portlets.dmp.dao.MenuDao;


/**
 * Bean that uses the publisherContext.xml spring bean config to
 * access the DAO instance used to publish menus. Reads a menu
 * XML document, generates MenuItem objects and stores them in
 * the database.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 */
public class XmlMenuPublisher {
    private static final Log LOG = LogFactory.getLog(XmlMenuPublisher.class);
    
    private BeanFactory factory;
    
    /**
     * Get the bean factory for the publisher.
     */
    private synchronized BeanFactory getFactory() {
        if (this.factory == null) {
            final GenericApplicationContext ctx = new GenericApplicationContext();
            final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
            xmlReader.loadBeanDefinitions(new ClassPathResource("/publisherApplicationContext.xml"));
            ctx.refresh();
            
            this.factory = ctx;
        }

        return this.factory;
    }
    
    /**
     * Publishes all the menus in the XML specified by the URL.
     * 
     * @param xmlSourceUrl URL to the menu XML.
     */
    public void publishMenus(URL xmlSourceUrl) {
        LOG.info("Publishing menus from: " + xmlSourceUrl);

        try {
            //The XMLReader will read in the XML document
            final XMLReader reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            
            try {
                reader.setFeature("http://apache.org/xml/features/validation/dynamic", true);     
                reader.setFeature("http://apache.org/xml/features/validation/schema", true);
                
                final URL menuSchema = this.getClass().getResource("/menu.xsd");
                if (menuSchema == null) {
                    throw new MissingResourceException("Could not load menu schema. '/menu.xsd'", this.getClass().getName(), "/menu.xsd");
                }
                
                reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", menuSchema.toString());
            }
            catch (SAXNotRecognizedException snre) {
                LOG.warn("Could not enable XSD validation", snre);
            }
            catch (SAXNotSupportedException xnse) {
                LOG.warn("Could not enable XSD validation", xnse);
            }
            
            final MenuItemGeneratingHandler handler = new MenuItemGeneratingHandler();
            
            reader.setContentHandler(handler);
            reader.parse(new InputSource(xmlSourceUrl.openStream()));
            
            final Map menus = handler.getMenus();
            
            final BeanFactory factory = this.getFactory();
            final MenuDao dao = (MenuDao)factory.getBean("menuDao", MenuDao.class);
            
            for (final Iterator nameItr = menus.entrySet().iterator(); nameItr.hasNext();) {
                final Map.Entry entry = (Map.Entry)nameItr.next();
                final String menuName = (String)entry.getKey();
                final MenuItem rootItem = (MenuItem)entry.getValue();
                
                LOG.info("Publishing menu='" + menuName + "' item='" + rootItem + "'");
                dao.storeMenu(menuName, rootItem);
            }
            
            LOG.info("Published menus from: " + xmlSourceUrl);
        }
        catch (IOException ioe) {
            LOG.error("Error publishing menus", ioe);
        }
        catch (SAXException saxe) {
            LOG.error("Error publishing menus", saxe);
        }
    }
}
