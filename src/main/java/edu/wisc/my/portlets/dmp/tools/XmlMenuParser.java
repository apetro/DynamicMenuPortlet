/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.wisc.my.portlets.dmp.tools;


import edu.wisc.my.portlets.dmp.beans.MenuItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * Parses XML representations of MenuItem trees into MenuItem Java object model tree.
 *
 * @since 1.1
 */
public class XmlMenuParser {

    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Parse a source of XML fulfilling menu.xsd into a map of name-MenuItem entries.
     * @param menuXmlSource non-null source of XML
     * @return Map from menu name to root MenuItem of that menu tree
     */
    public Map<String, MenuItem> parse(InputSource menuXmlSource) {
        try {
            //The XMLReader will read in the XML document
            final XMLReader
                reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");

            try {
                reader.setFeature("http://apache.org/xml/features/validation/dynamic", true);
                reader.setFeature("http://apache.org/xml/features/validation/schema", true);

                final URL menuSchema = this.getClass().getResource("/menu.xsd");
                if (menuSchema == null) {
                    throw new MissingResourceException("Could not load menu schema. '/menu.xsd'",
                        this.getClass().getName(), "/menu.xsd");
                }

                reader.setProperty(
                    "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                    menuSchema.toString());
            } catch (SAXNotRecognizedException snre) {
                log.warn("Could not enable XSD validation", snre);
            } catch (SAXNotSupportedException xnse) {
                log.warn("Could not enable XSD validation", xnse);
            }

            final MenuItemGeneratingHandler handler = new MenuItemGeneratingHandler();

            reader.setContentHandler(handler);
            reader.parse(menuXmlSource);

            final Map menus = handler.getMenus();

            Map<String, MenuItem> menusAsStronglyTypedMap = new HashMap<String, MenuItem>();

            for (final Iterator nameItr = menus.entrySet().iterator(); nameItr.hasNext();) {
                final Map.Entry entry = (Map.Entry)nameItr.next();
                final String menuName = (String)entry.getKey();
                final MenuItem rootItem = (MenuItem)entry.getValue();

                menusAsStronglyTypedMap.put(menuName, rootItem);
            }

            return menusAsStronglyTypedMap;

        } catch (IOException ioe) {
            log.error("Error parsing menu", ioe);
        } catch (SAXException saxe) {
            log.error("Error parsing menu", saxe);
        }

        return null;

    }

}
