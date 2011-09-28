package org.iplantc.phyloviewer.viewer.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.iplantc.phyloviewer.viewer.server.db.PersistTreeData;
import org.postgresql.ds.PGPoolingDataSource;

public class DatabaseListener implements ServletContextListener
{
	PGPoolingDataSource pool;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent)
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "contextInitialized: Getting database connection pool");
		ServletContext servletContext = contextEvent.getServletContext();
		
		String server = servletContext.getInitParameter("db.server");
		String database = servletContext.getInitParameter("db.database");
		String user = servletContext.getInitParameter("db.user");
		String password = servletContext.getInitParameter("db.password");
		
		pool = new PGPoolingDataSource();
		pool.setServerName(server);
		pool.setDatabaseName(database);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setMaxConnections(10);
		
		servletContext.setAttribute("db.connectionPool", pool);
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.iplantc.phyloviewer");
		
		ITreeData treeData = new UnpersistTreeData(emf);
		servletContext.setAttribute(Constants.TREE_DATA_KEY, treeData);
		
		DatabaseLayoutData layoutData = new DatabaseLayoutData(pool);
		servletContext.setAttribute(Constants.LAYOUT_DATA_KEY, layoutData);
		
		DatabaseOverviewImage overviewData = new DatabaseOverviewImage(pool);
		servletContext.setAttribute(Constants.OVERVIEW_DATA_KEY, overviewData);
		
		String imagePath = servletContext.getInitParameter("image.path");
		imagePath = servletContext.getRealPath(imagePath);
		String treeBackupPath = servletContext.getInitParameter("treefile.path");
		treeBackupPath = servletContext.getRealPath(treeBackupPath);
		IImportTreeData importer = new PersistTreeData(emf);
		servletContext.setAttribute(Constants.IMPORT_TREE_DATA_KEY, importer);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "contextDestroyed: Closing database connection pool");
		pool.close();
	}
}
