/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.tool.hbm2x.Hbm2JavaEqualsTest;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {
	
	private static final String TEST_ENTITY_HBM_XML = 
            "<hibernate-mapping package='org.hibernate.tool.hbm2x.Hbm2JavaEquals'>"+
            "  <class name='UnProxiedTestEntity'>                                 "+
            "    <id name='id' type='int'>                                        "+
		    "      <meta attribute='use-in-equals'>true</meta>                    "+
		    "    </id>                                                            "+
	        "  </class>                                                           "+
	        "  <class name='ProxiedTestEntity' proxy='TestEntityProxy'>           "+
		    "    <id name='id' type='int'>                                        "+
		    "      <meta attribute='use-in-equals'>true</meta>                    "+
		    "    </id>                                                            "+
	        "  </class>                                                           "+
            "</hibernate-mapping>                                                 ";	
	
	private static final String TEST_ENTITY_PROXY_JAVA = 
			"package org.hibernate.tool.hbm2x.Hbm2JavaEquals;"+ System.lineSeparator() +
	        "interface TestEntityProxy {                     "+ System.lineSeparator() +
			"  int getId();                                  "+ System.lineSeparator() +
	        "}                                               ";
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private File resourcesDir = null;

	@Before
	public void setUp() throws Exception {
		// create output folder
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		// export class ProxiedTestEntity.java and UnProxiedTestEntity
		File hbmXml = new File(resourcesDir, "testEntity.hbm.xml");
		FileWriter fileWriter = new FileWriter(hbmXml);
		fileWriter.write(TEST_ENTITY_HBM_XML);
		fileWriter.close();
		Properties properties = new Properties();
		properties.put(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, new File[] { hbmXml }, properties);
		Exporter exporter = ExporterFactory.createExporter(ExporterType.JAVA);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.start();
		// copy interface EntityProxy.java
		File file = new File(outputDir, "org/hibernate/tool/hbm2x/Hbm2JavaEquals/TestEntityProxy.java");
		FileWriter writer = new FileWriter(file);
		writer.write(TEST_ENTITY_PROXY_JAVA);
		writer.close();
		// compile the source files
		JavaUtil.compile(outputDir);
	}	
	
	@Test
	public void testEqualsWithoutProxy() throws Exception {
		// load the entity class and lookup the setId method
        URL[] urls = new URL[] { outputDir.toURI().toURL() };
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, oldLoader );
        Class<?> entityClass = ucl.loadClass("org.hibernate.tool.hbm2x.Hbm2JavaEquals.UnProxiedTestEntity");
        Method setId = entityClass.getMethod("setId", new Class[] { int.class });

        // create a first entity and check the 'normal' behavior: 
        // - 'true' when comparing against itself
        // - 'false' when comparing against null
        // - 'false' when comparing against an object of a different class
        Object firstEntity = entityClass.newInstance();
        setId.invoke(firstEntity, new Object[] { Integer.MAX_VALUE });
        Assert.assertTrue(firstEntity.equals(firstEntity));
        Assert.assertFalse(firstEntity.equals(null));
        Assert.assertFalse(firstEntity.equals(new Object()));

        // create a second entity and check the 'normal behavior
        // - 'true' if the id property is the same
        // - 'false' if the id property is different
        Object secondEntity = entityClass.newInstance();
        setId.invoke(secondEntity, new Object[] { Integer.MAX_VALUE });
        Assert.assertTrue(firstEntity.equals(secondEntity));
        Assert.assertTrue(secondEntity.equals(firstEntity));
        setId.invoke(secondEntity, new Object[] { Integer.MIN_VALUE });
        Assert.assertFalse(firstEntity.equals(secondEntity));
        Assert.assertFalse(secondEntity.equals(firstEntity));

        ucl.close();
	}

	@Test
	public void testEqualsWithProxy() throws Exception {

		// load the entity and proxy classes, lookup the setId method and create a proxy object
        URL[] urls = new URL[] { outputDir.toURI().toURL() };
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, oldLoader );
        Class<?> entityClass = ucl.loadClass("org.hibernate.tool.hbm2x.Hbm2JavaEquals.ProxiedTestEntity");
        Class<?> entityProxyInterface = ucl.loadClass("org.hibernate.tool.hbm2x.Hbm2JavaEquals.TestEntityProxy");
        Method setId = entityClass.getMethod("setId", new Class[] { int.class });
        TestEntityProxyInvocationHandler handler = new TestEntityProxyInvocationHandler();
        Object testEntityProxy = Proxy.newProxyInstance(
        		ucl, 
        		new Class[] { entityProxyInterface }, 
        		handler);
        
        // create a first proxied entity and check the 'normal' behavior: 
        // - 'true' when comparing against itself
        // - 'false' when comparing against null
        // - 'false' when comparing against an object of a different class (that is not the proxy class)
        Object firstEntity = entityClass.newInstance();
        setId.invoke(firstEntity, new Object[] { Integer.MAX_VALUE });
        Assert.assertTrue(firstEntity.equals(firstEntity));
        Assert.assertFalse(firstEntity.equals(null));
        Assert.assertFalse(firstEntity.equals(new Object()));

        // create a second proxied entity and check the 'normal behavior
        // - 'true' if the id property is the same
        // - 'false' if the id property is different
        Object secondEntity = entityClass.newInstance();
        setId.invoke(secondEntity, new Object[] { Integer.MAX_VALUE });
        Assert.assertTrue(firstEntity.equals(secondEntity));
        Assert.assertTrue(secondEntity.equals(firstEntity));
        setId.invoke(secondEntity, new Object[] { Integer.MIN_VALUE });
        Assert.assertFalse(firstEntity.equals(secondEntity));
        Assert.assertFalse(secondEntity.equals(firstEntity));

        // compare both proxied entities with the proxy
        handler.id = Integer.MAX_VALUE;
        Assert.assertTrue(firstEntity.equals(testEntityProxy));
        Assert.assertFalse(secondEntity.equals(testEntityProxy));        
        handler.id = Integer.MIN_VALUE;
        Assert.assertFalse(firstEntity.equals(testEntityProxy));
        Assert.assertTrue(secondEntity.equals(testEntityProxy));
        
        ucl.close();
	}

	private class TestEntityProxyInvocationHandler implements InvocationHandler {
		public int id = 0;
		@Override public Object invoke(
				Object proxy, 
				Method method, 
				Object[] args) throws Throwable {
			if ("getId".equals(method.getName())) {
				return id;
			}
			return null;
		}		
	}
	
}
