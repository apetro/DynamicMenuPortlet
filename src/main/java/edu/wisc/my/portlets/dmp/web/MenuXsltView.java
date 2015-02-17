/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.portlets.dmp.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.XMLReader;

import edu.wisc.my.portlets.dmp.beans.MenuItem;

/**
 * Creates a {@link Source} from a root MenuItem and menu name provided in the model.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision: 1.1 $
 */
public class MenuXsltView extends CachingXsltView {
    
    /**
     * @see org.springframework.web.servlet.view.xslt.AbstractXsltView#createXsltSource(java.util.Map, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Source createXsltSource(Map model, String root, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //TODO deal with 'root' argument (can I use it as the menu name?)
        
        final String menuName = (String)model.get(ViewMenuController.MODEL_MENU_NAME);
        if (menuName == null) {
            throw new IllegalArgumentException("model Map must contain a menu name String with a key='" + ViewMenuController.MODEL_MENU_NAME + "'");
        }
        
        final MenuItem rootItem = (MenuItem)model.get(ViewMenuController.MODEL_ROOT_ITEM);
        if (rootItem == null) {
            throw new IllegalArgumentException("model Map must contain a root MenuItem with a key='" + ViewMenuController.MODEL_ROOT_ITEM + "'");
        }
        
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Creating Xslt Source for menuName='" + menuName + "', menu='" + rootItem + "'");
        }
        
        final XMLReader reader = new MenuItemXmlReader();
        final MenuItemInputSource inputSource = new MenuItemInputSource(menuName, rootItem);
        final Source source = new SAXSource(reader, inputSource);
        return source;
    }
}
