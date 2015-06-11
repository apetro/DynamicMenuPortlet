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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.portlet.WindowState;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.jasig.portal.rdbm.InMemoryDataFieldMaxValueIncrementer;
import org.jasig.portal.rdbm.TransientDatasource;

import edu.wisc.my.portlets.dmp.beans.MenuItem;

/**
 * @author sschwartz
 */
public class JdbcMenuDaoTest extends TestCase {
    private DataSource testDataSource;
    private JdbcMenuDao menuDao;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        this.testDataSource = new TransientDatasource();
        
        final Connection con = testDataSource.getConnection();
        
        con.prepareStatement("CREATE  TABLE MENU_ITEMS  ( " +
                             "ID          NUMERIC(38,0)   NOT NULL, " +
                             "NAME        VARCHAR(512)    NOT NULL, " +
                             "DESCRIPTION VARCHAR(4000), " +
                             "URL         VARCHAR(4000), " +
                             "TARGET      VARCHAR(256), " +
                             "CONSTRAINT  MENU_ITMES_PK   PRIMARY KEY (ID))").execute();

       con.prepareStatement("CREATE  TABLE MENU_ROOTS ( " +
                             "NAME        VARCHAR(512)    NOT NULL, " +
                             "ITEM_ID     NUMERIC(38,0)   NOT NULL, " +
                             "CONSTRAINT  MENU_ROOTS_PK   PRIMARY KEY (NAME), " +
                             "CONSTRAINT  ROOTS_FK        FOREIGN KEY (ITEM_ID) REFERENCES MENU_ITEMS(ID))").execute();
    
       con.prepareStatement("CREATE  TABLE MENU_RELATIONS ( " +
                             "ITEM_ID         NUMERIC(38,0)       NOT NULL, " +
                             "CHILD_ITEM_ID   NUMERIC(38,0)       NOT NULL, " +
                             "ITEM_ORDER      NUMERIC(38,0)       NOT NULL, " +
                             "CONSTRAINT      RELATIONS_FK_ITEM   FOREIGN KEY (ITEM_ID)       REFERENCES MENU_ITEMS(ID), " +
                             "CONSTRAINT      RELATIONS_FK_CHILD  FOREIGN KEY (CHILD_ITEM_ID) REFERENCES MENU_ITEMS(ID))").execute();
    
       con.prepareStatement("CREATE  TABLE MENU_GROUPS ( " +
                             "ITEM_ID     NUMERIC(38,0)   NOT NULL, " +
                             "ITEM_GROUP  VARCHAR(512)    NOT NULL, " +
                             "CONSTRAINT  GROUPS_FK_ITEM  FOREIGN KEY (ITEM_ID) REFERENCES MENU_ITEMS(ID))").execute();
    
       con.prepareStatement("CREATE  TABLE MENU_WINDOW_STATES ( " +
                             "ITEM_ID         NUMERIC(38,0)   NOT NULL, " +
                             "WINDOW_STATE    VARCHAR(32)     NOT NULL, " +
                             "CONSTRAINT      STATES_FK_ITEM  FOREIGN KEY (ITEM_ID) REFERENCES MENU_ITEMS(ID))").execute();        
        con.close();
        
        this.menuDao = new JdbcMenuDao();
        this.menuDao.setDataSource(this.testDataSource);
        this.menuDao.setMenuIdIncrementer(new InMemoryDataFieldMaxValueIncrementer());
        this.menuDao.initDao();
    }
    
    public void tearDown() throws Exception {
        super.tearDown();

        this.menuDao = null;

        
        final Connection con = this.testDataSource.getConnection();
        
        con.prepareStatement("DELETE FROM MENU_WINDOW_STATES").execute();
        con.prepareStatement("DELETE FROM MENU_GROUPS").execute();
        con.prepareStatement("DELETE FROM MENU_ROOTS").execute();
        con.prepareStatement("DELETE FROM MENU_RELATIONS").execute();
        con.prepareStatement("DELETE FROM MENU_ITEMS").execute();
        
        con.prepareStatement("DROP TABLE MENU_WINDOW_STATES").execute();
        con.prepareStatement("DROP TABLE MENU_GROUPS").execute();
        con.prepareStatement("DROP TABLE MENU_ROOTS").execute();
        con.prepareStatement("DROP TABLE MENU_RELATIONS").execute();
        con.prepareStatement("DROP TABLE MENU_ITEMS").execute();
        
        con.prepareStatement("SHUTDOWN").execute();
        con.close();
        
        this.testDataSource = null;
    }
    
    
    public void testBasicMenu() throws SQLException {
        final MenuItem item1 = new MenuItem();
        item1.setName("Item1");
        item1.setDescription("Item1 Description");
        item1.setUrl("http://www.google.com");
        item1.setTarget("_blank");
        item1.setGroups(new String[] {"admin", "dev"});
        item1.setDisplayStates(new WindowState[] {WindowState.MAXIMIZED, WindowState.NORMAL});
        
        final MenuItem item2 = new MenuItem();
        item2.setName("Item2");
        item2.setDescription("Item2 Description");
        item2.setUrl("http://www.cnn.com");
        item2.setTarget(null);
        item2.setGroups(new String[] {"student", "dev"});
        item2.setDisplayStates(new WindowState[] {WindowState.NORMAL, WindowState.MINIMIZED});

        final MenuItem item3 = new MenuItem();
        item3.setName("Item3");
        item3.setDescription("Item3 Description");
        item3.setUrl("http://www.yahoo.com");
        item3.setTarget("temp");
        item3.setGroups(new String[] {"admin", "student"});
        item3.setDisplayStates(new WindowState[] {WindowState.MINIMIZED, WindowState.MAXIMIZED});
        
        item1.setChildren(new MenuItem[] {item2, item3});

        this.menuDao.storeMenu("Menu1", item1);
        
        final MenuItem storedMenu = this.menuDao.getMenu("Menu1");
        assertEquals(item1, storedMenu);

        this.menuDao.deleteMenu("Menu1");
        
        final Connection con = this.testDataSource.getConnection();
        try {
            final Statement stmnt = con.createStatement();
            try {
                final ResultSet rs1 = stmnt.executeQuery("SELECT COUNT(*) FROM MENU_ITEMS");
                try {
                    assertTrue("No count of MENU_ITEMS table returned", rs1.next());
                    assertEquals("MENU_ITEMS table has too many rows", 0, rs1.getInt(1));
                }
                finally {
                    rs1.close();
                }
                
                final ResultSet rs2 = stmnt.executeQuery("SELECT COUNT(*) FROM MENU_ROOTS");
                try {
                    assertTrue("No count of MENU_ROOTS table returned", rs2.next());
                    assertEquals("MENU_ROOTS table has too many rows", 0, rs2.getInt(1));
                }
                finally {
                    rs2.close();
                }
                
                final ResultSet rs3 = stmnt.executeQuery("SELECT COUNT(*) FROM MENU_RELATIONS");
                try {
                    assertTrue("No count of MENU_RELATIONS table returned", rs3.next());
                    assertEquals("MENU_RELATIONS table has too many rows", 0, rs3.getInt(1));
                }
                finally {
                    rs3.close();
                }
                
                final ResultSet rs4 = stmnt.executeQuery("SELECT COUNT(*) FROM MENU_GROUPS");
                try {
                    assertTrue("No count of MENU_GROUPS table returned", rs4.next());
                    assertEquals("MENU_GROUPS table has too many rows", 0, rs4.getInt(1));
                }
                finally {
                    rs4.close();
                }
                
                final ResultSet rs5 = stmnt.executeQuery("SELECT COUNT(*) FROM MENU_WINDOW_STATES");
                try {
                    assertTrue("No count of MENU_WINDOW_STATES table returned", rs5.next());
                    assertEquals("MENU_WINDOW_STATES table has too many rows", 0, rs5.getInt(1));
                }
                finally {
                    rs5.close();
                }
            }
            finally {
                stmnt.close();
            }
        }
        finally {
            con.close();
        }
    }

    
    public void testGetPublishedMenus() {
        final MenuItem item1 = new MenuItem();
        item1.setName("Item1");
        item1.setDescription("Item1 Description");
        item1.setUrl("http://www.google.com");

        final MenuItem item2 = new MenuItem();
        item2.setName("Item2");
        item2.setDescription("Item2 Description");

        this.menuDao.storeMenu("Menu1", item1);
        this.menuDao.storeMenu("Menu2", item2);
        final Set exptectedNames = new HashSet();
        exptectedNames.add("Menu1");
        exptectedNames.add("Menu2");
        
        final String[] menuNames = this.menuDao.getPublishedMenuNames();
        final Set retrievedNames = new HashSet(Arrays.asList(menuNames));
        
        assertEquals(exptectedNames, retrievedNames);
    }
    
    public void testGroupFiltering() {
        final MenuItem item1 = new MenuItem();
        item1.setName("Item1");
        item1.setGroups(new String[] {"admin", "dev"});
        
        final MenuItem item2 = new MenuItem();
        item2.setName("Item2");
        item2.setGroups(new String[] {"student", "dev"});

        final MenuItem item3 = new MenuItem();
        item3.setName("Item3");
        item3.setGroups(new String[] {"admin", "student"});
        
        final MenuItem item4 = new MenuItem();
        item4.setName("Item4");
        item4.setGroups(new String[] {"admin", "student"});
        
        final MenuItem item5 = new MenuItem();
        item5.setName("Item5");
        item5.setGroups(new String[] {"admin", "student"});
        
        //Configure full tree
        item1.setChildren(new MenuItem[] {item2, item3});
        item2.setChildren(new MenuItem[] {item4});
        item3.setChildren(new MenuItem[] {item5});

        this.menuDao.storeMenu("Menu1", item1);
        
        final MenuItem storedMenu = this.menuDao.getMenu("Menu1", new String[] {"admin"} );
        
        //Configure trimmed tree
        item1.setChildren(new MenuItem[] {item3});
        item3.setChildren(new MenuItem[] {item5});        

        assertEquals(item1, storedMenu);
    }
}