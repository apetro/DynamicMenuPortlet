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

package edu.wisc.my.portlets.dmp.beans;

import javax.portlet.WindowState;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents an item displayed on the menu portlet.
 * 
 * @author sschwartz
 * @since 1.0
 */
public class MenuItem {
    private String name = null;
    private String description = null;
    private String[] groups = new String[0];
    private MenuItem[] children = new MenuItem[0];
    private String url = null;
    private String target = null;
    private WindowState[] displayStates = new WindowState[0];
    
    
    
    /**
     * An ordered list of children this portlet has. May not be null. May be
     * zero length.
     * 
     * @return Returns the children.
     */
    public MenuItem[] getChildren() {
        return this.children;
    }
    /**
     * The description of the item. May be null.
     * 
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * The portlet WindowStates the item will display in. May not be null. May be
     * zero length.
     * 
     * @return Returns the displayStates.
     */
    public WindowState[] getDisplayStates() {
        return this.displayStates;
    }
    /**
     * The groups that are allowed to view this portlet. May not be null. May be
     * zero length.
     * 
     * @return Returns the groups.
     */
    public String[] getGroups() {
        return this.groups;
    }
    /**
     * The name of the item, this is displayed in the portlet. May not be null.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }
    /**
     * The value of the target attribute of the anchor tag. Can be used to make
     * sure the link opens in a new window. May be null.
     * 
     * @return Returns the target.
     */
    public String getTarget() {
        return this.target;
    }
    /**
     * The URL that the menu item links to. May be null in which case just the
     * name of the item will be displayed without a link.
     * 
     * @return Returns the url.
     */
    public String getUrl() {
        return this.url;
    }
    /**
     * An ordered list of children this portlet has. May not be null. May be
     * zero length. May not result in a circular child relationship
     * 
     * @param children The children to set.
     * @throws IllegalArgumentException If the childrent are null or adding them would result in a circular structure.
     */
    public void setChildren(MenuItem[] children) {
        if (children == null)
            throw new IllegalArgumentException("children cannot be null");
        
        boolean validChildren = true;
        final int parentItemId = this.identityHashCode();
        for (int index = 0; index < children.length && validChildren; index++) {
            validChildren = this.checkChildrenForLoop(children[index], parentItemId);
        }
        
        if (!validChildren) {
            throw new IllegalArgumentException("Adding children would result in loop. children='" + children + "'");
        }
        
        this.children = children;
    }
    private boolean checkChildrenForLoop(MenuItem child, int parentItemId) {
        if (child.identityHashCode() == parentItemId) {
            return false;
        }
        
        final MenuItem[] children = child.getChildren();
        for (int index = 0; index < children.length; index++) {
            final boolean validChild = this.checkChildrenForLoop(children[index], parentItemId);
            if (!validChild) {
                return false;
            }
        }
        
        return true;
    }
    /**
     * The description of the item. May be null.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * The portlet WindowStates the item will display in. May not be null. May be
     * zero length.
     * 
     * @param displayStates The displayStates to set.
     */
    public void setDisplayStates(WindowState[] displayStates) {
        if (displayStates == null)
            throw new IllegalArgumentException("displayStates cannot be null");

        this.displayStates = displayStates;
    }
    /**
     * The groups that are allowed to view this portlet. May not be null. May be
     * zero length.
     * 
     * @param groups The groups to set.
     */
    public void setGroups(String[] groups) {
        if (groups == null)
            throw new IllegalArgumentException("groups cannot be null");

        this.groups = groups;
    }
    /**
     * The name of the item, this is displayed in the portlet. May not be null.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");

        this.name = name;
    }
    /**
     * The value of the target attribute of the anchor tag. Can be used to make
     * sure the link opens in a new window. May be null.
     * 
     * @param target The target to set.
     */
    public void setTarget(String target) {
        this.target = target;
    }
    /**
     * The URL that the menu item links to. May be null in which case just the
     * name of the item will be displayed without a link.
     * 
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof MenuItem)) {
            return false;
        }
        
        final MenuItem rhs = (MenuItem)object;
        return new EqualsBuilder()
            .append(this.groups, rhs.groups)
            .append(this.target, rhs.target)
            .append(this.description, rhs.description)
            .append(this.displayStates, rhs.displayStates)
            .append(this.url, rhs.url)
            .append(this.name, rhs.name)
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(-1272048437, -517658551)
            .append(this.groups)
            .append(this.target)
            .append(this.description)
            .append(this.displayStates)
            .append(this.url)
            .append(this.name)
            .toHashCode();
    }
    
    /**
     * @return The hashCode from the super object.
     */
    public int identityHashCode() {
        return super.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .appendSuper(super.toString())
            .append("name", this.name)
            .append("description", this.description)
            .append("url", this.url)
            .append("target", this.target)
            .append("groups", this.groups)
            .append("children", this.children)
            .append("displayStates", this.displayStates)
            .toString();
    }
    
}
