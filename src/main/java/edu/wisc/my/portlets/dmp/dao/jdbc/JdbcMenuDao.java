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
package edu.wisc.my.portlets.dmp.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.WindowState;
import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import edu.wisc.my.portlets.dmp.beans.MenuItem;
import edu.wisc.my.portlets.dmp.dao.MenuDao;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @since 1.0
 */
public class JdbcMenuDao extends JdbcDaoSupport implements MenuDao {
    private DataFieldMaxValueIncrementer menuIdIncrementer;
    
    private SelectPublishedMenuNames publishedMenuNamesQuery;
    private SelectRootItemId rootItemIdQuery;
    private SelectMenuItem menuItemQuery;
    private SelectGroups groupsQuery;
    private SelectWindowStates windowStatesQuery;
    private SelectRelations relationsQuery;
    private StoreMenuItem menuItemInsert;
    private StoreGroup groupInsert;
    private StoreWindowState windowStateInsert;
    private StoreRelation relationInsert;
    private StoreMenuRoot menuRootInsert;
    private DeleteRelations relationsDelete;
    private DeleteGroups groupsDelete;
    private DeleteWindowStates windowStatesDelete;
    private DeleteMenuItem menuItemDelete;
    private DeleteMenuRoot menuRootDelete;
    
    /**
     * @return Returns the menuIdIncrementer.
     */
    public DataFieldMaxValueIncrementer getMenuIdIncrementer() {
        return this.menuIdIncrementer;
    }
    /**
     * @param menuIdIncrementer The menuIdIncrementer to set.
     */
    public void setMenuIdIncrementer(DataFieldMaxValueIncrementer menuIdIncrementer) {
        this.menuIdIncrementer = menuIdIncrementer;
    }
    
    
    /*
     * @see org.springframework.jdbc.core.support.JdbcDaoSupport#initDao()
     */
    protected void initDao() throws Exception {
        super.initDao();
        
        this.publishedMenuNamesQuery = new SelectPublishedMenuNames(this.getDataSource());
        this.rootItemIdQuery = new SelectRootItemId(this.getDataSource());
        this.menuItemQuery = new SelectMenuItem(this.getDataSource());
        this.groupsQuery = new SelectGroups(this.getDataSource());
        this.windowStatesQuery = new SelectWindowStates(this.getDataSource());
        this.relationsQuery = new SelectRelations(this.getDataSource());
        this.menuItemInsert = new StoreMenuItem(this.getDataSource());
        this.groupInsert = new StoreGroup(this.getDataSource());
        this.windowStateInsert = new StoreWindowState(this.getDataSource());
        this.relationInsert = new StoreRelation(this.getDataSource());
        this.menuRootInsert = new StoreMenuRoot(this.getDataSource());
        this.relationsDelete = new DeleteRelations(this.getDataSource());
        this.groupsDelete = new DeleteGroups(this.getDataSource());
        this.windowStatesDelete = new DeleteWindowStates(this.getDataSource());
        this.menuItemDelete = new DeleteMenuItem(this.getDataSource());
        this.menuRootDelete = new DeleteMenuRoot(this.getDataSource());
    }
     
    public String[] getPublishedMenuNames() {
        return this.publishedMenuNamesQuery.getPublishedMenuNames();
    }

    public MenuItem getMenu(String menuName) {
        if (menuName == null)
            throw new IllegalArgumentException("menuName cannot be null.");

        final Long rootItemId = this.rootItemIdQuery.getRootItemId(menuName);
        if (rootItemId == null) {
            throw new DataRetrievalFailureException("No menu exists in the database for menuName='" + menuName + "'");
        }
        
        final MenuItem rootItem = this.getMenuItemForIdAndGroups(rootItemId, null, new HashSet());
        return rootItem;
    }

    public MenuItem getMenu(String menuName, String[] userGroups) {
        if (menuName == null)
            throw new IllegalArgumentException("menuName cannot be null.");
        if (userGroups == null)
            throw new IllegalArgumentException("userGroups cannot be null.");
        if (userGroups.length == 0)
            return null;
        
        final Long rootItemId = this.rootItemIdQuery.getRootItemId(menuName);
        if (rootItemId == null) {
            this.logger.warn("No menu exists in the database for menuName='" + menuName + "'");
            return null;
        }

        final Set userGroupsSet = new HashSet(Arrays.asList(userGroups));
        final MenuItem rootItem = this.getMenuItemForIdAndGroups(rootItemId, userGroupsSet, new HashSet());
        return rootItem;
    }
    
    public void storeMenu(String menuName, MenuItem rootItem) {
        if (menuName == null)
            throw new IllegalArgumentException("menuName cannot be null.");
        if (rootItem == null)
            throw new IllegalArgumentException("rootItem cannot be null.");
        
        this.deleteMenu(menuName);
        
        final Long id = this.storeMenuItem(rootItem);
        this.menuRootInsert.insert(menuName, id);
    }

    public void deleteMenu(String menuName) {
        if (menuName == null)
            throw new IllegalArgumentException("menuName cannot be null.");

        final Long rootItemId = this.rootItemIdQuery.getRootItemId(menuName);

        if (rootItemId != null) {
            this.menuRootDelete.delete(menuName);
            this.deleteMenuItem(rootItemId, new HashSet());
        }
    }
    
    /**
     * Loads a complete MenuItem including groups, display states and children.
     * 
     * @param itemId The ID of the item to load and return as the root item.
     * @param userGroups The groups to filter the menu items with, if null no group filtering is done.
     * @param parentIds A Set of item IDs that have been loaded and are parents, grandparents, etc.. of this item.
     * @return The item for the specified ID, will be null if no matching group is found and userGroups is not null.
     */
    private MenuItem getMenuItemForIdAndGroups(Long itemId, Set userGroups, Set parentIds) {
        //Check for loops in the menu structure
        if (parentIds.contains(itemId)) {
            throw new DataIntegrityViolationException("Loop found in menu structure. itemId='" + itemId + "' exists in set of parentIds='" + parentIds + "'");
        }
        
        //Get MenuItem (no groups, states, children)
        final MenuItem item = this.menuItemQuery.getItem(itemId);
        if (item == null) {
            throw new DataIntegrityViolationException("No MenuItem found for itemId='" + itemId + "'");
        }
        
        //populate groups[]
        final String[] groups = this.groupsQuery.getGroups(itemId);
        
        //Check groups to see if this item should be returned based on the specified userGroups
        if (userGroups != null) {
            boolean validItem = false;
            
            //Check the item groups against the Set
            for (int index = 0; index < groups.length && !validItem; index++) {
                validItem = userGroups.contains(groups[index]);
            }
            
            //If no matching group was found retun null from this method
            if (!validItem) {
                return null;
            }
        }
        item.setGroups(groups);
        
        //populate states[]
        final WindowState[] states = this.windowStatesQuery.getWindowStates(itemId);
        item.setDisplayStates(states);
        
        //get child ID's
        final Long[] childIds = this.relationsQuery.getChildIds(itemId);
        final List childList = new ArrayList(childIds.length);

        //Track the parent item ID when loading children
        parentIds.add(itemId);
        try {
            for (int index = 0; index < childIds.length; index++) {
                final MenuItem childItem = this.getMenuItemForIdAndGroups(childIds[index], userGroups, parentIds);
                if (childItem != null) {
                    childList.add(childItem);
                }
            }
        }
        finally {
            //Ensure the parent id is cleared from the Set.
            parentIds.remove(itemId);
        }
        
        final MenuItem[] children = (MenuItem[])childList.toArray(new MenuItem[childList.size()]); 
        item.setChildren(children);
        
        return item;
    }
    
    /**
     * Recursivly stores a menu structure starting from the specified MenuItem
     * 
     * @param item The item to store.
     * @return The id of the stored item. 
     */
    private Long storeMenuItem(MenuItem item) {
        //store the item
        final Long id = this.menuItemInsert.insert(item);
        
        //store groups
        final String[] groups = item.getGroups(); 
        for (int index = 0; index < groups.length; index++) {
            this.groupInsert.insert(id, groups[index]);
        }

        //store states
        final WindowState[] states = item.getDisplayStates();
        for (int index = 0; index < states.length; index++) {
            this.windowStateInsert.insert(id, states[index]);
        }
        
        final MenuItem[] children = item.getChildren();
        for (int index = 0; index < children.length; index++) {
            //recurse on each child
            final Long childId = this.storeMenuItem(children[index]);

            //store relations
            this.relationInsert.insert(id, childId, index);
        }
        
        return id;
    }


    /**
     * Recursivly deletes a menu structure starting with the specified itemId
     * 
     * @param itemId The root id to start deleting from, all children, groups, states will be removed.
     * @param parentIds A Set of item IDs that have been stored and are parents, grandparents, etc.. of this item.
     */
    private void deleteMenuItem(Long itemId, Set parentIds) {
        //Check for loops in the menu structure
        if (parentIds.contains(itemId)) {
            throw new DataIntegrityViolationException("Loop found in menu structure. itemId='" + itemId + "' exists in set of parentIds='" + parentIds + "'");
        }
        
        //Get the child IDs before removing the relations
        final Long[] childIds = this.relationsQuery.getChildIds(itemId);
        
        //Delete relations
        this.relationsDelete.delete(itemId);
        
        //Recurse on children
        //Track the parent item ID when deleting children
        parentIds.add(itemId);
        try {
            for (int index = 0; index < childIds.length; index++) {
                //recurse on each child
                this.deleteMenuItem(childIds[index], parentIds);
            }
        }
        finally {
            //Ensure the parent id is cleared from the Set.
            parentIds.remove(itemId);
        }
        
        //Delete states
        this.windowStatesDelete.delete(itemId);
        
        //Delete groups
        this.groupsDelete.delete(itemId);
        
        //Delete item
        this.menuItemDelete.delete(itemId);
    }

    
    
    /**
     * Gets an Array of names of menu roots.
     */
    protected class SelectPublishedMenuNames extends MappingSqlQuery  {
        private static final String SELECT_PUBLISHED_MENU = 
            "SELECT NAME " + 
            "FROM MENU_ROOTS";
        
        protected SelectPublishedMenuNames(DataSource ds) {
            super(ds, SELECT_PUBLISHED_MENU);
            this.compile();
        }
        
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getString("NAME");
        }
        
        public String[] getPublishedMenuNames() {
            final List namesList = this.execute();
            return (String[])namesList.toArray(new String[namesList.size()]);
        }
    }
    
    /**
     * Gets a menu item id for the root item in a menu for a root menu name.
     */
    protected class SelectRootItemId extends MappingSqlQuery {
        static final String SELECT_ROOT_ITEM_ID = 
            "SELECT ITEM_ID " + 
            "FROM MENU_ROOTS " + 
            "WHERE NAME=?";
        
        protected SelectRootItemId(DataSource ds) {
            super(ds, SELECT_ROOT_ITEM_ID);
            this.declareParameter(new SqlParameter(Types.VARCHAR));
            this.compile();
        }
  
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            return new Long(rs.getLong("ITEM_ID"));
        }
        
        public Long getRootItemId(String menuName) {
            final Object[] args = new Object[] { menuName };
            final List ids = this.execute(args);
            return (Long)DataAccessUtils.uniqueResult(ids);
        }
    }
    
    /**
     * Gets a menu item for an item id.
     */
    protected class SelectMenuItem extends MappingSqlQuery {
        private static final String SELECT_MENU_ITEM = 
            "SELECT * " +
            "FROM MENU_ITEMS " +
            "WHERE ID=?";
        
        protected SelectMenuItem(DataSource ds) {
            super(ds, SELECT_MENU_ITEM);
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.compile();
        }
        
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            //Create and populate the new item
            final MenuItem menuItem = new MenuItem();
            menuItem.setName(rs.getString("NAME"));
            menuItem.setDescription(rs.getString("DESCRIPTION"));
            menuItem.setUrl(rs.getString("URL"));
            menuItem.setTarget(rs.getString("TARGET"));
            
            return menuItem;
        }
        
        public MenuItem getItem(Long id) {
            final Object[] args = new Object[] { id };
            final List items = this.execute(args);
            return (MenuItem)DataAccessUtils.uniqueResult(items);
        }
    }
    
    /**
     * Gets an array of group names for an item id.
     */
    protected class SelectGroups extends MappingSqlQuery {
        private static final String SELECT_GROUPS = 
            "SELECT ITEM_GROUP " + 
            "FROM MENU_GROUPS " +
            "WHERE ITEM_ID=?";
        
        protected SelectGroups(DataSource ds) {
            super(ds, SELECT_GROUPS);
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.compile();
        }
  
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException { 
            return rs.getString("ITEM_GROUP");
        }
        
        public String[] getGroups(Long id) {
            final Object[] args = new Object[] { id };
            final List groups = this.execute(args);
            return (String[])groups.toArray(new String[groups.size()]);
        }
    }
    
    /**
     * Gets an array of WindowStates for an item id.
     */
    protected class SelectWindowStates extends MappingSqlQuery {
        private static final String SELECT_WINDOW_STATES = 
            "SELECT WINDOW_STATE " +
            "FROM MENU_WINDOW_STATES " +
            "WHERE ITEM_ID=?";
        
        protected SelectWindowStates(DataSource ds) {
            super(ds, SELECT_WINDOW_STATES);
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.compile();
        }
  
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            return new WindowState(rs.getString("WINDOW_STATE"));
        }
        
        public WindowState[] getWindowStates(Long id) {
            final Object[] args = new Object[] { id };
            final List states = this.execute(args);
            return (WindowState[])states.toArray(new WindowState[states.size()]);
        }
    }
    
    /**
     * Gets an array of child item IDs for an item id.
     */
    protected class SelectRelations extends MappingSqlQuery {
        private static final String SELECT_RELATIONS = 
            "SELECT CHILD_ITEM_ID " +
            "FROM MENU_RELATIONS " +
            "WHERE ITEM_ID=? " +
            "ORDER BY ITEM_ORDER";
        
        protected SelectRelations(DataSource ds) {
            super(ds,SELECT_RELATIONS);
            declareParameter(new SqlParameter(Types.NUMERIC));
            compile();
        }
  
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            return new Long(rs.getLong("CHILD_ITEM_ID"));
        }
        
        public Long[] getChildIds(Long id) {
            final List childIds = this.getChildIdsAsList(id);
            return (Long[])childIds.toArray(new Long[childIds.size()]);
        }
        
        public List getChildIdsAsList(Long id) {
            final Object[] args = new Object[] { id };
            return this.execute(args);
        }
    }
   
    /**
     * Stores a MenuItem (not groups[], states[] or children[]
     */
    protected class StoreMenuItem extends SqlUpdate {
        private static final String INSERT_MENU_ITEM = 
            "INSERT INTO MENU_ITEMS (ID, NAME, DESCRIPTION, URL, TARGET) " +
            "VALUES(?, ?, ?, ?, ?)";
        
        protected StoreMenuItem(DataSource ds) {
            super(ds, INSERT_MENU_ITEM);
            
            this.declareParameter(new SqlParameter(Types.BIGINT));
            this.declareParameter(new SqlParameter(Types.VARCHAR));
            this.declareParameter(new SqlParameter(Types.VARCHAR));
            this.declareParameter(new SqlParameter(Types.VARCHAR));
            this.declareParameter(new SqlParameter(Types.VARCHAR));
            
            this.compile();
        }
        
        public Long insert(MenuItem rootItem) {
            //Generate item id
            final Long id = new Long(JdbcMenuDao.this.menuIdIncrementer.nextLongValue());
            
            //Create arg array
            final Object[] args = new Object[] {id, 
                    rootItem.getName(),
                    rootItem.getDescription(),
                    rootItem.getUrl(),
                    rootItem.getTarget() };
            
            //Store item
            super.update(args);

            //Return the id of the item just stored
            return id;
        }
    }
    
    /**
     * Stores a group for a menu item
     */
    protected class StoreGroup extends SqlUpdate {
        protected static final String STORE_MENU_GROUPS = 
            "INSERT INTO MENU_GROUPS (ITEM_ID, ITEM_GROUP) " +
            "VALUES(?, ?)";
        
        protected StoreGroup(DataSource ds) {
            super(ds, STORE_MENU_GROUPS);
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.declareParameter(new SqlParameter(Types.VARCHAR));
            this.compile();
        }
        
        public void insert(Long itemId, String group) {
            final Object[] args = new Object[] { itemId, group };
            super.update(args);
        }
    }
    
    /**
     * Stores a window state for a menu item
     */
    protected class StoreWindowState extends SqlUpdate {
        private static final String STORE_MENU_WINDOW_STATES = 
            "INSERT INTO MENU_WINDOW_STATES (ITEM_ID, WINDOW_STATE) " +
            "VALUES(?, ?)";
        
        protected StoreWindowState(DataSource ds) {
            super(ds, STORE_MENU_WINDOW_STATES);
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.declareParameter(new SqlParameter(Types.VARCHAR));
            this.compile();
        }
        
        public void insert(Long itemId, WindowState windowState) {
            final Object[] agrs = new Object[] { itemId, windowState.toString() };
            super.update(agrs);
        }
    }
    
    /**
     * Stores a child relation for a menu item
     */
    protected class StoreRelation extends SqlUpdate {
        private static final String STORE_MENU_RELATIONS = 
            "INSERT INTO MENU_RELATIONS (ITEM_ID, CHILD_ITEM_ID, ITEM_ORDER) " +
            "VALUES(?, ?, ?)";
        
        protected StoreRelation(DataSource ds) {
            super(ds, STORE_MENU_RELATIONS);
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.compile();
        }
        
        public void insert(Long itemId, Long childItemId, int order) {
            final Object[] args = new Object[] { itemId, childItemId, new Long(order) };
            super.update(args);
        }
    }
    
    /**
     * Stores a menu name to root item ID mapping.
     */
    protected class StoreMenuRoot extends SqlUpdate {
        private static final String STORE_MENU_ROOTS = 
            "INSERT INTO MENU_ROOTS (NAME, ITEM_ID) " +
            "VALUES(?, ?)";
        
        protected StoreMenuRoot(DataSource ds) {
            super(ds, STORE_MENU_ROOTS);
            this.declareParameter(new SqlParameter(Types.VARCHAR));
            this.declareParameter(new SqlParameter(Types.NUMERIC));
            this.compile();
        }
        
        public void insert(String menuName, Long rootItemId) {
            final Object[] args = new Object[] { menuName, rootItemId };
            super.update(args);
        }
    }
    
    /**
     * Delete all relations for an item id.
     */
    protected class DeleteRelations extends SqlUpdate {
        private static final String MENU_RELATIONS_DELETE = 
            "DELETE FROM MENU_RELATIONS " +
            "WHERE ITEM_ID=?";
        
        protected DeleteRelations(DataSource ds) {
            super(ds, MENU_RELATIONS_DELETE);
            this.declareParameter(new SqlParameter(Types.BIGINT));
            this.compile();
        }
        
        public void delete(Long itemId) {
            final Object[] args = { itemId };
            this.update(args);
        }
    }
    
    /**
     * Delete all window states for an item id.
     */
    protected class DeleteWindowStates extends SqlUpdate {
        private static final String MENU_WINDOW_STATES_DELETE = 
            "DELETE FROM MENU_WINDOW_STATES " +
            "WHERE ITEM_ID=?";
        
        protected DeleteWindowStates(DataSource ds) {
            super(ds, MENU_WINDOW_STATES_DELETE);
            this.declareParameter(new SqlParameter(Types.BIGINT));
            this.compile();
        }
        
        public void delete(Long itemId) {
            final Object[] args = { itemId };
            this.update(args);
        }
    }
    
    /**
     * Delete all groups for an item id.
     */
    protected class DeleteGroups extends SqlUpdate {
        private static final String MENU_GROUPS_DELETE = 
            "DELETE FROM MENU_GROUPS " +
            "WHERE ITEM_ID=?";
        
        protected DeleteGroups(DataSource ds) {
            super(ds, MENU_GROUPS_DELETE);
            this.declareParameter(new SqlParameter(Types.BIGINT));
            this.compile();
        }
        
        public void delete(Long itemId) {
            final Object[] args = { itemId };
            this.update(args);
        }
    }
    
    /**
     * Delete item for an item id.
     */
    protected class DeleteMenuItem extends SqlUpdate {
        private static final String MENU_ITEM_DELETE = 
            "DELETE FROM MENU_ITEMS " +
            "WHERE ID=?";
        
        protected DeleteMenuItem(DataSource ds) {
            super(ds, MENU_ITEM_DELETE);
            this.declareParameter(new SqlParameter(Types.BIGINT));
            this.compile();
        }
        
        public void delete(Long itemId) {
            final Object[] args = { itemId };
            this.update(args);
        }
    }
    
    /**
     * Delete a menu root name mapping for the menu name.
     */
    protected class DeleteMenuRoot extends SqlUpdate {
        private static final String MENU_ROOT_DELETE = 
            "DELETE FROM MENU_ROOTS " +
            "WHERE NAME=?";
        
        protected DeleteMenuRoot(DataSource ds) {
            super(ds, MENU_ROOT_DELETE);

            this.declareParameter(new SqlParameter(Types.VARCHAR));

            this.compile();
        }

        public void delete(String menuName) {
            final Object[] args = new Object[] { menuName };
            super.update(args);
        }
    }
}
