
Portlet Name: DynamicMenuPortlet

Version number: 1.0

Designers: UW 

Developer: John Kliszewski / Eric Dalquist

Requirements spec: Dynamic Menu Portlet Requirements Specification, version 1.0
 - https://mywebspace.wisc.edu/xythoswfs/webui/rybicki/MUM%20Requirements/Dynamic_Menu_Portlet_RS_v1.0.doc

Design spec: Dynamic Menu Portlet Design Document, version 1.0
 - https://mywebspace.wisc.edu/xythoswfs/webui/rybicki/MUM%20Design/Dynamic_Menu_Portlet_Design_v1.6.doc

### Summary
----------------------------------------
The Dynamic Menu Portlet provides a building block for displaying lists of links. It is dynamic in the sense that each link in the menu can be assigned group-based permissions. In other words, the items displayed in a given DMP will be different based on a user�s group membership. When rendering the list of links only links and headers with an associated group that the user is also in will be displayed. When nesting links a child link inherits all the groups associated with the parent.

### Configuration
----------------------------------------
The portlet.xml contains an init parameter, xslFile, with default value dmp.xsl. This stylesheet is used to interpret the XML data generated and is located in the DynamicMenuPortlet\webpages\stylesheets\edu\wisc\my\portlets\dmp\web directory. If one wishes to use a different stylesheet to control the rendering of content, it must be placed in the same directory and the new filename must replace "dmp.xsl" as the init-param value in portlet.xml.

Edit the applicationContext.xml file to set the dataSource bean with the database connection info. Also, one must create the needed tables in the database and then populate them with the desired menu structure. 

The following SQL statements will create the necessary tables and sequences:
```
CREATE TABLE MENU_ITEMS (
    ID          NUMERIC(38,0)   NOT NULL,
    NAME        VARCHAR(512)    NOT NULL,
    DESCRIPTION VARCHAR(4000),
    URL         VARCHAR(4000),
    TARGET      VARCHAR(256),
    CONSTRAINT  MENU_ITMES_PK   PRIMARY KEY (ID)
);

CREATE TABLE MENU_ROOTS (
    NAME        VARCHAR(512)    NOT NULL,
    ITEM_ID     NUMERIC(38,0)   NOT NULL,
    CONSTRAINT  MENU_ROOTS_PK   PRIMARY KEY (NAME),
    CONSTRAINT  ROOTS_FK        FOREIGN KEY (ITEM_ID) REFERENCES MENU_ITEMS(ID)
);

CREATE TABLE MENU_RELATIONS (
    ITEM_ID         NUMERIC(38,0)       NOT NULL,
    CHILD_ITEM_ID   NUMERIC(38,0)       NOT NULL,
    ITEM_ORDER      NUMERIC(38,0)       NOT NULL,
    CONSTRAINT      RELATIONS_FK_ITEM   FOREIGN KEY (ITEM_ID)       REFERENCES MENU_ITEMS(ID),
    CONSTRAINT      RELATIONS_FK_CHILD  FOREIGN KEY (CHILD_ITEM_ID) REFERENCES MENU_ITEMS(ID)
);

CREATE TABLE MENU_GROUPS (
    ITEM_ID     NUMERIC(38,0)   NOT NULL,
    ITEM_GROUP  VARCHAR(512)    NOT NULL,
    CONSTRAINT  GROUPS_FK_ITEM  FOREIGN KEY (ITEM_ID) REFERENCES MENU_ITEMS(ID)
);

CREATE TABLE MENU_WINDOW_STATES (
    ITEM_ID         NUMERIC(38,0)   NOT NULL,
    WINDOW_STATE    VARCHAR(32)     NOT NULL,
    CONSTRAINT      STATES_FK_ITEM  FOREIGN KEY (ITEM_ID) REFERENCES MENU_ITEMS(ID)
);

CREATE SEQUENCE MENU_ITEM_SEQ 
	MINVALUE 1 
	MAXVALUE 999999999999999999999999999
	START WITH  1
	INCREMENT BY  1
	CACHE 20;
	
```	
Populating the tables can be done manually using INSERT and UPDATE statements, but is more easily accomplished using the Administrative Portlet. See the Administrative Portlet documentation for details.

### Build and Installation
----------------------------------------
The ant build file for the project provides a deploy target. This depends
on the clean, init, compile and dist targets to create the WAR and then copies it
into the container. The container home and lib and the portal directory locations 
need to be configured in the build.properties.

### Publishing
----------------------------------------
Be careful to specify the portlet GUID as 'DynamicMenuPortlet.DynamicMenuPortlet'
when publishing the portlet. Also make sure to set the publish parameter 'menuName' to the name 
of the menu exactly as it appears in the NAME column of the MENU_ITEMS table in the database (MENU_ITEMS.NAME).

### Testing
----------------------------------------
The following SQL statements can be used to populate the tables with a simple test menu:
```
INSERT INTO MENU_ITEMS (ID, NAME, DESCRIPTION, URL, TARGET) VALUES (0, 'root1', 'the root', null, null)
INSERT INTO MENU_ITEMS (ID, NAME, DESCRIPTION, URL, TARGET) VALUES (1, 'child1-1', 'the first child of root1', 'http://www.google.com', 'new')
INSERT INTO MENU_ITEMS (ID, NAME, DESCRIPTION, URL, TARGET) VALUES (2, 'child1-2', 'the second child of root1', 'http://www.yahoo.com', 'new')
INSERT INTO MENU_ITEMS (ID, NAME, DESCRIPTION, URL, TARGET) VALUES (3, 'child1-3', 'the third child of root1', 'http://www.cnn.com', 'new')
INSERT INTO MENU_ITEMS (ID, NAME, DESCRIPTION, URL, TARGET) VALUES (4, 'child1-2-1', 'the first child of child1-2', 'http://www.ebay.com', null)


INSERT INTO MENU_RELATIONS (ITEM_ID, CHILD_ITEM_ID, ITEM_ORDER) VALUES (0, 1, 1)
INSERT INTO MENU_RELATIONS (ITEM_ID, CHILD_ITEM_ID, ITEM_ORDER) VALUES (0, 2, 2)
INSERT INTO MENU_RELATIONS (ITEM_ID, CHILD_ITEM_ID, ITEM_ORDER) VALUES (0, 3, 3)
INSERT INTO MENU_RELATIONS (ITEM_ID, CHILD_ITEM_ID, ITEM_ORDER) VALUES (2, 4, 1)

INSERT INTO MENU_ROOTS (NAME, ITEM_ID) VALUES ('root1', 0)


INSERT INTO MENU_GROUPS (ITEM_ID, ITEM_GROUP) VALUES (0, 'pags.mywebspaceactive')
INSERT INTO MENU_GROUPS (ITEM_ID, ITEM_GROUP) VALUES (1, 'pags.mywebspaceactive')
INSERT INTO MENU_GROUPS (ITEM_ID, ITEM_GROUP) VALUES (2, 'pags.mywebspaceactive')
INSERT INTO MENU_GROUPS (ITEM_ID, ITEM_GROUP) VALUES (3, 'pags.mywebspaceactive')
INSERT INTO MENU_GROUPS (ITEM_ID, ITEM_GROUP) VALUES (4, 'pags.mywebspaceactive')
```
Publishing this test DMP with publish parameter 'menuName' set to 'root1' will display as below for a user with access to MyWebSpace.

DYNAMIC MENU PORTLET - root1

    * root1
          o child1-1
          o child1-2
                + child1-2-1
          o child1-3

### Running
----------------------------------------
The menu structure will be displayed by the display portlet in HTML list format. Any URLs present in this structure will be displayed as links. 
