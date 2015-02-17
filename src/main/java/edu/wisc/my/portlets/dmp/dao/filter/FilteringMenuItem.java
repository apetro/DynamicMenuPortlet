/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.portlets.dmp.dao.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.WindowState;

import edu.wisc.my.portlets.dmp.beans.MenuItem;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.2 $
 */
public class FilteringMenuItem extends MenuItem {
    private final MenuItem filteredMenuItem;
    private final Set<String> allowedGroups;
    private MenuItem[] children;

    public FilteringMenuItem(MenuItem filteredMenuItem, Set<String> allowedGroups) {
        this.filteredMenuItem = filteredMenuItem;
        if (allowedGroups != null && allowedGroups.size() == 0) {
            this.allowedGroups = Collections.unmodifiableSet(new HashSet<String>(allowedGroups));
        }
        else {
            this.allowedGroups = null;
        }
    }

    public FilteringMenuItem(MenuItem filteredMenuItem, String... allowedGroups) {
        this.filteredMenuItem = filteredMenuItem;
        if (allowedGroups != null && allowedGroups.length == 0) {
            this.allowedGroups = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(allowedGroups)));
        }
        else {
            this.allowedGroups = null;
        }
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.portlets.dmp.beans.MenuItem#getChildren()
     */
    @Override
    public MenuItem[] getChildren() {
        if (this.allowedGroups == null) {
            return this.filteredMenuItem.getChildren();
        }
        
        //This isn't necessarily thread safe but the worst case would be extra CPU work. There
        //shouldn't be any data consistency issues.
        MenuItem[] children = this.children;
        if (children == null) {
            final MenuItem[] allChildren = this.filteredMenuItem.getChildren();
            if (allChildren == null) {
                return null;
            }

            final List<MenuItem> childBuilder = new ArrayList<MenuItem>(allChildren.length);
            for (final MenuItem child : allChildren) {
                final String[] groups = child.getGroups();
                
                for (final String group : groups) {
                    if (this.allowedGroups.contains(group)) {
                        childBuilder.add(child);
                        break;
                    }
                }
            }
            
            children = childBuilder.toArray(new MenuItem[childBuilder.size()]);
            this.children = children;
        }
        
        return children;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.portlets.dmp.beans.MenuItem#setGroups(java.lang.String[])
     */
    @Override
    public void setGroups(String[] groups) {
        this.children = null;
        
        super.setGroups(groups);
    }


    @Override
    public boolean equals(Object object) {
        return this.filteredMenuItem.equals(object);
    }

    @Override
    public String getDescription() {
        return this.filteredMenuItem.getDescription();
    }

    @Override
    public WindowState[] getDisplayStates() {
        return this.filteredMenuItem.getDisplayStates();
    }

    @Override
    public String[] getGroups() {
        return this.filteredMenuItem.getGroups();
    }

    @Override
    public String getName() {
        return this.filteredMenuItem.getName();
    }

    @Override
    public String getTarget() {
        return this.filteredMenuItem.getTarget();
    }

    @Override
    public String getUrl() {
        return this.filteredMenuItem.getUrl();
    }

    @Override
    public int hashCode() {
        return this.filteredMenuItem.hashCode();
    }

    @Override
    public int identityHashCode() {
        return this.filteredMenuItem.identityHashCode();
    }

    @Override
    public void setChildren(MenuItem[] children) {
        this.filteredMenuItem.setChildren(children);
    }

    @Override
    public void setDescription(String description) {
        this.filteredMenuItem.setDescription(description);
    }

    @Override
    public void setDisplayStates(WindowState[] displayStates) {
        this.filteredMenuItem.setDisplayStates(displayStates);
    }

    @Override
    public void setName(String name) {
        this.filteredMenuItem.setName(name);
    }

    @Override
    public void setTarget(String target) {
        this.filteredMenuItem.setTarget(target);
    }

    @Override
    public void setUrl(String url) {
        this.filteredMenuItem.setUrl(url);
    }

    @Override
    public String toString() {
        return this.filteredMenuItem.toString();
    }
    
    
}
