/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.portlets.dmp.dao.gap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;

import edu.wisc.my.apilayer.groups.GroupService;
import edu.wisc.my.apilayer.groups.GroupsException;
import edu.wisc.my.apilayer.groups.IGroupMember;
import edu.wisc.my.apilayer.person.IPerson;
import edu.wisc.my.apilayer.person.PersonServices;
import edu.wisc.my.portlets.dmp.dao.GroupsDao;

/**
 * Backs the GroupsDao interface with the uPortal Groups service.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 */
public class UPortalGroupsDao implements GroupsDao {
    private final Log LOG = LogFactory.getLog(UPortalGroupsDao.class);

    public String[] getContainingGroups(String userName) {
        // get the user's group membership info
        final IPerson person = PersonServices.getPersonByUserName(userName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Found IPerson='" + person + "' for user='" + userName + "'");
        }
        
        final IGroupMember gm;
        try {
            gm = GroupService.getGroupMember(person.getIdentifier());
        }
        catch (GroupsException ge) {
            throw new DataRetrievalFailureException("Error retrieving IGroupMemeber for IPerson.IEntityIdentifier='" + person.getIdentifier() + "'", ge);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Found IGroupMember='" + gm + "' for IPerson.IEntityIdentifier='" + person.getIdentifier() + "'");
        }

        final Set names = new HashSet();

        if (gm != null) {
            final Iterator groupItr;
            try {
                groupItr = gm.getAllContainingGroups();
            }
            catch (GroupsException ge) {
                throw new DataRetrievalFailureException("IGroupMember.getAllContainingGroups() failed on IGroupMember='" + gm + "' for IPerson.IEntityIdentifier='" + person.getIdentifier() + "'", ge);
            }
            
            while (groupItr.hasNext()) {
                final IGroupMember g = (IGroupMember)groupItr.next();
                final String key = g.getKey();
                
                names.add(key);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Found containing groups='" + names + "' for user='" + userName + "'");
        }

        return (String[])names.toArray(new String[names.size()]);
    }

}
