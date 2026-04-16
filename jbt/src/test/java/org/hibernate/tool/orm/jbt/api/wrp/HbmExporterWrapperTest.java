/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.orm.jbt.internal.factory.ConfigurationWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.HbmExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.ConfigurationMetadataDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HbmExporterWrapperTest {

	private HbmExporterWrapper hbmExporterWrapper = null;
	private Exporter wrappedHbmExporter = null;

	private ConfigurationWrapper cfg = null;
	private File f = null;

	@TempDir private File tempFolder;
	
	@BeforeEach
	public void beforeEach() {
		cfg = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		f = new File(tempFolder, "foo");
		hbmExporterWrapper = HbmExporterWrapperFactory.createHbmExporterWrapper(cfg, f);
		wrappedHbmExporter = (Exporter)hbmExporterWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() throws Exception {
		assertTrue(tempFolder.exists());
		assertFalse(f.exists());
		assertNotNull(wrappedHbmExporter);
		assertNotNull(hbmExporterWrapper);
		assertSame(f, wrappedHbmExporter.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		ConfigurationMetadataDescriptor descriptor = (ConfigurationMetadataDescriptor)wrappedHbmExporter
				.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(descriptor);
		Field configurationField = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		configurationField.setAccessible(true);
		assertSame(cfg.getWrappedObject(), configurationField.get(descriptor));
	}
	
	@Test
	public void testStart() throws Exception {
		// Build a real Configuration with an annotated entity
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		configuration.addAnnotatedClass(Foo.class);
		ConfigurationMetadataDescriptor descriptor = new ConfigurationMetadataDescriptor(configuration);
		Properties properties = wrappedHbmExporter.getProperties();
		properties.put(ExporterConstants.METADATA_DESCRIPTOR, descriptor);
		File outputDir = new File(tempFolder, "output");
		outputDir.mkdirs();
		properties.put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		hbmExporterWrapper.start();
		// The entity class is org.hibernate.tool.orm.jbt.api.wrp.HbmExporterWrapperTest$Foo
		// so the generated file is under the package directory
		File generatedHbmXml = new File(outputDir,
				"org/hibernate/tool/orm/jbt/api/wrp/HbmExporterWrapperTest$Foo.hbm.xml");
		assertTrue(generatedHbmXml.exists());
	}
	
	@Test
	public void testGetOutputDirectory() {
		assertNull(hbmExporterWrapper.getOutputDirectory());
		File file = new File("testGetOutputDirectory");
		wrappedHbmExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, file);
		assertSame(file, hbmExporterWrapper.getOutputDirectory());
	}
	
	@Test
	public void testSetOutputDirectory() {
		assertNull(wrappedHbmExporter.getProperties().get(ExporterConstants.DESTINATION_FOLDER));
		File file = new File("testSetOutputDirectory");
		hbmExporterWrapper.setOutputDirectory(file);
		assertSame(file, wrappedHbmExporter.getProperties().get(ExporterConstants.DESTINATION_FOLDER));
	}
	
	@Test
	public void testExportPOJO() throws Exception {
		// exportPOJO is now a no-op in the new HbmXmlExporter; verify it doesn't throw
		Map<Object, Object> context = new HashMap<>();
		hbmExporterWrapper.exportPOJO(context, new Object());
	}
	
	@Test
	public void testSetExportPOJODelegate() throws Exception {
		Object delegate = new Object() {
			@SuppressWarnings("unused")
			public void exportPojo(Map<Object, Object> map, Object pojoClass, String qualifiedDeclarationName) { }
		};
		Field delegateField = HbmExporterWrapperFactory.HbmExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		assertNull(delegateField.get(wrappedHbmExporter));
		hbmExporterWrapper.setExportPOJODelegate(delegate);
		assertSame(delegate, delegateField.get(wrappedHbmExporter));
	}

	@Entity
	@Table(name = "FOO")
	public static class Foo {
		@Id
		public String id;
	}

}
