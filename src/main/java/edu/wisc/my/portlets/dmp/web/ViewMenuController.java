/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.portlets.dmp.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import edu.wisc.my.portlets.dmp.beans.MenuItem;
import edu.wisc.my.portlets.dmp.dao.GroupsDao;
import edu.wisc.my.portlets.dmp.dao.MenuDao;

/**
 * Returns the filtered MenuItem tree for the remote user and groups that
 * user is a member of.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 */
public class ViewMenuController extends AbstractController {
    /**
     * Portlet preferences key used to get the name of the menu to display.
     * TODO namespace this parameter name with the package & class name.
     */
    public static final String MENU_NAME = "menuName";
    
    //Keys for the model Map to pass to the view
    public static final String MODEL_ROOT_ITEM = "ROOT_ITEM";
    public static final String MODEL_MENU_NAME = "MENU_NAME";
    
    private static final String VIEW_MENU = "viewMenu";
    private static final String VIEW_NO_MENU = "noMenu";
    
    private MenuDao menuDao;
    private GroupsDao groupsDao;
    
    public ViewMenuController() {
        this.logger.info("Created ViewMenuController. hash=" + this.hashCode() + "");
    }
    

    /**
     * @return Returns the groupsDao.
     */
    public GroupsDao getGroupsDao() {
        return this.groupsDao;
    }
    /**
     * @param groupsDao The groupsDao to set.
     */
    public void setGroupsDao(GroupsDao groupsDao) {
        this.groupsDao = groupsDao;
    }

    /**
     * @return Returns the menuDao.
     */
    public MenuDao getMenuDao() {
        return this.menuDao;
    }
    /**
     * @param menuDao The menuDao to set.
     */
    public void setMenuDao(MenuDao menuDao) {
        this.menuDao = menuDao;
    }


    /**
     * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    @Override
    protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
        // find the username
        final String userName = request.getRemoteUser();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("remoteUser='" + userName + "'");
        }
        
        // get the menu name from the portlet properties
        final PortletPreferences pp = request.getPreferences();
        final String menuName = pp.getValue(MENU_NAME, null);

        //find all the groups this person is a member of
        final String[] userGroups = this.groupsDao.getContainingGroups(userName);
        
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Rendering Dynamic Menu '" + menuName + "' with group list '" + Arrays.asList(userGroups) + "' for user '" + userName + "'");
        }

        final MenuItem menuRoot;
        if (userGroups != null) {
            //get the root MenuItem for the name and list of groups
            menuRoot = this.menuDao.getMenu(menuName, userGroups);
        }
        else {
            //Get the root MenuItem for the name
            menuRoot = this.menuDao.getMenu(menuName);
        }
        
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put(MODEL_MENU_NAME, menuName);
        
        if (menuRoot != null) {
            //All the groups the menu supports
            final Set<String> menuGroups = this.getAllMenuGroups(menuRoot);
            //Reduce menu groups to just those that overlap with the user's group list
            menuGroups.retainAll(Arrays.asList(userGroups));
            
            final Serializable cacheKey = this.getCacheKey(menuName, menuGroups);
            model.put("contentCacheKey", cacheKey);
            model.put(MODEL_ROOT_ITEM, menuRoot);
            
            return new ModelAndView(VIEW_MENU, model);
        }

        return new ModelAndView(VIEW_NO_MENU, model);
    }
    
    protected Serializable getCacheKey(String menuName, Set<String> groupList) {
        final ArrayList<Serializable> cacheKey = new ArrayList<Serializable>(2);
        cacheKey.add(menuName);
        cacheKey.add(new HashSet<String>(groupList));        
        return cacheKey;
    }
    
    protected Set<String> getAllMenuGroups(MenuItem menuRoot) {
        if (menuRoot == null) {
            return null;
        }
        
        final Set<String> allGroups = new HashSet<String>();
        final String[] groups = menuRoot.getGroups();
        if (groups != null) {
            for (final String group : groups) {
                allGroups.add(group);
            }
        }
        
        final MenuItem[] children = menuRoot.getChildren();
        if (children != null) {
            for (final MenuItem childItem : children) {
                final Set<String> childGroups = this.getAllMenuGroups(childItem);
                allGroups.addAll(childGroups);
            }
        }
        
        return allGroups;
    }
}
