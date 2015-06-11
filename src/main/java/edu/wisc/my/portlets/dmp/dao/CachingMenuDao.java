/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.portlets.dmp.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.wisc.my.portlets.dmp.beans.MenuItem;

/**
 * @author Eric Dalquist
 * @since 1.0
 */
public class CachingMenuDao implements MenuDao {
    private MenuDao menuDao;
    private Map<Object, MenuItem> menuCache;
    
    
    /**
     * @return the menuDao
     */
    public MenuDao getMenuDao() {
        return this.menuDao;
    }
    /**
     * @param menuDao the menuDao to set
     */
    public void setMenuDao(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    /**
     * @return the menuCache
     */
    public Map<Object, MenuItem> getMenuCache() {
        return this.menuCache;
    }
    /**
     * @param menuCache the menuCache to set
     */
    public void setMenuCache(Map<Object, MenuItem> menuCache) {
        this.menuCache = menuCache;
    }


    /* (non-Javadoc)
     * @see edu.wisc.my.portlets.dmp.dao.MenuDao#deleteMenu(java.lang.String)
     */
    public void deleteMenu(String menuName) {
        this.menuCache.remove(menuName);
        this.menuDao.deleteMenu(menuName);
    }
    
    /* (non-Javadoc)
     * @see edu.wisc.my.portlets.dmp.dao.MenuDao#getMenu(java.lang.String, java.lang.String[])
     */
    public MenuItem getMenu(String menuName, String[] userGroups) {
        if (userGroups == null || userGroups.length == 0) {
            return this.getMenu(menuName);
        }
        
        final List<?> cacheKey = Arrays.asList(menuName, Arrays.asList(userGroups));
        
        //If the menus cache is not null try getting the menu item from it
        MenuItem menuItem = this.menuCache.get(cacheKey);
        if (menuItem != null) {
            return menuItem;
        }
        
        //Nothing in cache, retrieve the object
        menuItem = this.menuDao.getMenu(menuName, userGroups);
        this.menuCache.put(cacheKey, menuItem);
        
        return menuItem;
    }
    
    /* (non-Javadoc)
     * @see edu.wisc.my.portlets.dmp.dao.MenuDao#getMenu(java.lang.String)
     */
    public MenuItem getMenu(String menuName) {
        //If the menus cache is not null try getting the menu item from it
        MenuItem menuItem = this.menuCache.get(menuName);
        if (menuItem != null) {
            return menuItem;
        }
        
        //Nothing in cache, retrieve the object
        menuItem = this.menuDao.getMenu(menuName);
        this.menuCache.put(menuName, menuItem);
        
        return menuItem;
    }
    
    /* (non-Javadoc)
     * @see edu.wisc.my.portlets.dmp.dao.MenuDao#getPublishedMenuNames()
     */
    public String[] getPublishedMenuNames() {
        return this.menuDao.getPublishedMenuNames();
    }
    
    /* (non-Javadoc)
     * @see edu.wisc.my.portlets.dmp.dao.MenuDao#storeMenu(java.lang.String, edu.wisc.my.portlets.dmp.beans.MenuItem)
     */
    public void storeMenu(String menuName, MenuItem rootItem) {
        this.menuDao.storeMenu(menuName, rootItem);
        this.menuCache.remove(menuName);
    }
}
