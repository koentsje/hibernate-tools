package org.hibernate.tool.hbm2x.query.QueryExporterTest;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.hibernate.tool.metadata.MetadataSources;
import org.hibernate.tool.metadata.MetadataSourcesFactory;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File destinationDir = null;
	private File resourcesDir = null;
	private File userGroupHbmXmlFile = null;
	
	@Before
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		destinationDir = new File(temporaryFolder.getRoot(), "destination");
		destinationDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		String[] resources = { "UserGroup.hbm.xml" };		
		String resourcesLocation = ResourceUtil.getResourcesLocation(this);
		ResourceUtil.createResources(this, resourcesLocation, resources, resourcesDir);
		userGroupHbmXmlFile = new File(resourcesDir, "UserGroup.hbm.xml");
		SessionFactory factory = buildMetadata().buildSessionFactory();		
		Session s = factory.openSession();	
		Transaction t = s.beginTransaction();
		User user = new User("max", "jboss");
		s.persist( user );		
		user = new User("gavin", "jboss");
		s.persist( user );		
		s.flush();		
		t.commit();
		s.close();
		factory.close();		
	}
	
	@Test
	public void testQueryExporter() throws Exception {		
		QueryExporter exporter = new QueryExporter();
		MetadataSources metadataSources = MetadataSourcesFactory
				.createNativeSources(
						null, 
						new File[] { userGroupHbmXmlFile }, 
						null);
		exporter.getProperties().put(AvailableSettings.HBM2DDL_AUTO, "update");
		exporter.setMetadataSources(metadataSources);
		exporter.setOutputDirectory(destinationDir);
		exporter.setFilename("queryresult.txt");
		List<String> queries = new ArrayList<String>();
		queries.add("from java.lang.Object");
		exporter.setQueries( queries );		
		exporter.start();
		JUnitUtil.assertIsNonEmptyFile(new File(destinationDir, "queryresult.txt"));		
	}
	
	@After
	public void tearDown() throws Exception {
		SchemaExport export = new SchemaExport();
		final EnumSet<TargetType> targetTypes = EnumSet.noneOf( TargetType.class );
		targetTypes.add( TargetType.DATABASE );
		export.drop(targetTypes, buildMetadata());		
		if (export.getExceptions() != null && export.getExceptions().size() > 0){
			Assert.fail("Schema export failed");
		}		
		JdbcUtil.dropDatabase(this);
	}
	
	private MetadataSources buildMetadataSources() {
		Properties properties = new Properties();
		properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
		return MetadataSourcesFactory
				.createNativeSources(
						null, 
						new File[] { userGroupHbmXmlFile }, 
						properties);
	}
	
	private Metadata buildMetadata() {
		return buildMetadataSources().buildMetadata();
	}
	
}
