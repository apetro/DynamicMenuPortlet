/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.rdbm;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * A DataSource implementation backed by an in-memory HSQLDb instance,
 * suitable for implementing testcases for DataSource-consuming DAO impls.
 * @author andrew.petro@yale.edu
 * @version $Revision: 1.3 $ $Date: 2013/05/13 19:55:51 $
 */
public class TransientDatasource implements DataSource {
    
    private DataSource delegate;
    
    public TransientDatasource() {
        final BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setMaxActive(1);
        basicDataSource.setInitialSize(1);
        basicDataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        basicDataSource.setUrl("jdbc:hsqldb:mem:adhommemds");
        basicDataSource.setMaxIdle(0);
        
        this.delegate = basicDataSource;

    }

    /**
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        return this.delegate.getConnection();
    }

    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    public Connection getConnection(String username, String password) throws SQLException {
        return this.delegate.getConnection(username, password);
    }

    /**
     * @see javax.sql.DataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        return this.delegate.getLoginTimeout();
    }

    /**
     * @see javax.sql.DataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        return this.delegate.getLogWriter();
    }

    /**
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int seconds) throws SQLException {
        this.delegate.setLoginTimeout(seconds);
    }

    /**
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.delegate.setLogWriter(out);
    }

    /* (non-Javadoc)
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> wrapperClass) throws SQLException {
        return wrapperClass.isAssignableFrom(this.delegate.getClass());
    }
    
    /* (non-Javadoc)
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> wrapperClass) throws SQLException {
        if (!this.isWrapperFor(wrapperClass)) {
            throw new SQLException(this.getClass() + " is not a wrapper for " + wrapperClass);
        }
        
        return (T)this.delegate;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.delegate.getParentLogger();
    }
}