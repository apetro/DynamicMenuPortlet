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

package edu.wisc.my.portlets.dmp.dao.memory;

import edu.wisc.my.portlets.dmp.beans.MenuItem;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

/**
 * Unit tests for the in-memory implementation of the menu DAO.
 *
 * Written in the JUnit3 extends TestCase style to match other tests in this project.
 *
 * @since 1.1
 */
public class InMemoryMenuDaoTest extends TestCase {

    /**
     * When there are no stored menus,
     * the DAO should give an empty array
     * when asked for the names of menus.
     */
    @Test
    public void testReportsNoMenuNames() {

        final InMemoryMenuDao inMemoryMenuDao = new InMemoryMenuDao();
        assertArrayEquals(new String[] {}, inMemoryMenuDao.getPublishedMenuNames());

    }

    /**
     * When there are stored menus,
     * the DAO should accurately report the names of those menus.
     */
    @Test
    public void testReportsMenuNames() {
        final InMemoryMenuDao inMemoryMenuDao = new InMemoryMenuDao();

        final MenuItem thingOne = new MenuItem();
        final MenuItem thingTwo = new MenuItem();

        inMemoryMenuDao.storeMenu("thingOne", thingOne);
        inMemoryMenuDao.storeMenu("thingTwo", thingTwo);

        final String[] actualMenuNames = inMemoryMenuDao.getPublishedMenuNames();
        Arrays.sort(actualMenuNames);

        final String[] expectedMenuNames = { "thingOne", "thingTwo"};
        Arrays.sort(expectedMenuNames);

        assertArrayEquals(expectedMenuNames, actualMenuNames);

    }

    /**
     * When the requested menu does not exist,
     * returns null.
     */
    @Test
    public void testReturnsNullOnGetUnknownMenu() {

        final InMemoryMenuDao inMemoryMenuDao = new InMemoryMenuDao();

        final MenuItem thingOne = new MenuItem();
        final MenuItem thingTwo = new MenuItem();

        inMemoryMenuDao.storeMenu("thingOne", thingOne);
        inMemoryMenuDao.storeMenu("thingTwo", thingTwo);

        assertNull(inMemoryMenuDao.getMenu("does-not-exist"));

    }

    /**
     * When the requested menu does exist,
     * returns it.
     */
    @Test
    public void testReturnsMenu() {

        final InMemoryMenuDao inMemoryMenuDao = new InMemoryMenuDao();

        final MenuItem thingOne = new MenuItem();
        final MenuItem thingTwo = new MenuItem();

        thingOne.setName("Thing One");
        thingOne.setUrl("https://www.google.com");
        thingOne.setTarget("_blank");
        thingOne.setGroups( new String[] { "HuddledMasses", "PrivilegedFew" } );
        thingOne.setDescription("An example menu item.");

        inMemoryMenuDao.storeMenu("thingOne", thingOne);
        inMemoryMenuDao.storeMenu("thingTwo", thingTwo);

        assertEquals(thingOne, inMemoryMenuDao.getMenu("thingOne"));

    }

    /**
     * When the user has a group granting access to the requested menu item,
     * returns the menu item.
     *
     * This is a naive test of the group filtering (when paired with the test demonstrating
     * that it filter away when the group does not match).  These tests are sufficient to verify
     * that the in memory DAO is doing something about filtering; the actual filtering behavior is
     * implemented via FilteringMenuItem, which should have its own comprehensive unit tests.
     */

    /*
    @Test
    public void testReturnsFilteredMenuWhenGroupMatches() {

        final InMemoryMenuDao inMemoryMenuDao = new InMemoryMenuDao();

        final MenuItem thingOne = new MenuItem();
        final MenuItem thingTwo = new MenuItem();

        thingOne.setName("Thing One");
        thingOne.setUrl("https://www.google.com");
        thingOne.setTarget("_blank");
        thingOne.setGroups( new String[] { "HuddledMasses", "PrivilegedFew" } );
        thingOne.setDescription("An example menu item.");

        inMemoryMenuDao.storeMenu("thingOne", thingOne);
        inMemoryMenuDao.storeMenu("thingTwo", thingTwo);

        // the user has a matching group (HuddledMasses).
        final String[] matchingGroups = new String[] { "HuddledMasses", "Proletariat"};

        assertEquals(thingOne, inMemoryMenuDao.getMenu("thingOne", matchingGroups));

    }
    */

    /**
     * When the user has no group granting access to the requested menu item,
     * returns null.
     *
     * This is a naive test of the group filtering sufficient to verify
     * that the in memory DAO is doing something about filtering; the actual filtering behavior is
     * implemented via FilteringMenuItem, which should have its own comprehensive unit tests for
     * deep filtering behavior within the tree.
     */

    /*
    @Test
    public void testFiltersAwayMenuWhenGroupDoesNotMatch() {

        final InMemoryMenuDao inMemoryMenuDao = new InMemoryMenuDao();

        final MenuItem thingOne = new MenuItem();
        final MenuItem thingTwo = new MenuItem();

        thingOne.setName("Thing One");
        thingOne.setUrl("https://www.google.com");
        thingOne.setTarget("_blank");
        thingOne.setGroups(new String[] {"PrivilegedFew", "Superusers"});
        thingOne.setDescription("An example menu item.");

        inMemoryMenuDao.storeMenu("thingOne", thingOne);
        inMemoryMenuDao.storeMenu("thingTwo", thingTwo);

        // the user has no matching group
        final String[] matchingGroups = new String[] { "HuddledMasses", "Proletariat"};

        assertNull(inMemoryMenuDao.getMenu("thingOne", matchingGroups));

    }
    */

}
