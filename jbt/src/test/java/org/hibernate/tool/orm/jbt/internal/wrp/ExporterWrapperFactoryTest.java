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
package org.hibernate.tool.orm.jbt.internal.wrp;

import org.hibernate.tool.orm.jbt.api.wrp.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.hibernate.tool.orm.jbt.internal.wrp.ExporterWrapperFactory.ExporterWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExporterWrapperFactoryTest {

	private ExporterWrapper exporterWrapper = null;

	@BeforeEach
	public void beforeEach() {
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(POJOExporter.class.getName());
	}

	@Test
	public void testConstruction() {
		assertNotNull(exporterWrapper);
		assertNotNull(exporterWrapper.getWrappedObject());
		assertTrue(exporterWrapper.getWrappedObject() instanceof org.hibernate.tool.internal.exporter.entity.EntityExporter);
	}

	@Test
	public void testSetConfiguration() {
		ConfigurationWrapper configurationWrapper = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		Properties properties = new Properties();
		configurationWrapper.setProperties(properties);
		// POJOExporter: setConfiguration stores the configuration
		exporterWrapper.setConfiguration(configurationWrapper);
		assertSame(configurationWrapper, exporterWrapper.getProperties().get("configuration"));
		// CfgExporter: setConfiguration also sets customProperties
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(HibernateConfigurationExporter.class.getName());
		ExporterWrapperImpl impl = (ExporterWrapperImpl) exporterWrapper;
		assertNull(impl.getCustomProperties());
		exporterWrapper.setConfiguration(configurationWrapper);
		assertSame(properties, impl.getCustomProperties());
	}

	@Test
	public void testSetArtifactCollector() {
		ArtifactCollectorWrapper artifactCollectorWrapper = ArtifactCollectorWrapperFactory.createArtifactCollectorWrapper();
		Object wrappedArtifactCollector = artifactCollectorWrapper.getWrappedObject();
		assertNotSame(wrappedArtifactCollector, exporterWrapper.getProperties().get("artifact_collector"));
		exporterWrapper.setArtifactCollector(artifactCollectorWrapper);
		assertSame(wrappedArtifactCollector, exporterWrapper.getProperties().get("artifact_collector"));
	}

	@Test
	public void testSetOutputDirectory() {
		File file = new File("");
		assertNotSame(file, exporterWrapper.getProperties().get("output_directory"));
		exporterWrapper.setOutputDirectory(file);
		assertSame(file, exporterWrapper.getProperties().get("output_directory"));
	}

	@Test
	public void testSetTemplatePath() {
		String[] templatePath = new String[] {};
		assertNotSame(templatePath, exporterWrapper.getProperties().get("template_path"));
		exporterWrapper.setTemplatePath(templatePath);
		assertSame(templatePath, exporterWrapper.getProperties().get("template_path"));
	}

	@Test
	public void testGetProperties() {
		Properties properties = exporterWrapper.getProperties();
		assertNotNull(properties);
	}

	@Test
	public void testGetGenericExporter() {
		// POJOExporter should not return a GenericExporterWrapper
		assertNull(exporterWrapper.getGenericExporter());
		// try with GenericExporter
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(GenericExporter.class.getName());
		GenericExporterWrapper genericExporterWrapper = exporterWrapper.getGenericExporter();
		assertNotNull(genericExporterWrapper);
	}

	@Test
	public void testGetHbm2DDlExporter() {
		// POJOExporter should not return a DdlExporterWrapper
		assertNull(exporterWrapper.getHbm2DDLExporter());
		// try with Hbm2DDLExporter
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(Hbm2DDLExporter.class.getName());
		DdlExporterWrapper ddlExporterWrapper = exporterWrapper.getHbm2DDLExporter();
		assertNotNull(ddlExporterWrapper);
	}

	@Test
	public void testGetQueryExporter() {
		// POJOExporter should not return a QueryExporterWrapper
		assertNull(exporterWrapper.getQueryExporter());
		// try with QueryExporter
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(QueryExporter.class.getName());
		QueryExporterWrapper queryExporterWrapper = exporterWrapper.getQueryExporter();
		assertNotNull(queryExporterWrapper);
	}

	@Test
	public void testSetCustomProperties() {
		Properties properties = new Properties();
		// setCustomProperties should have no effect on non-CfgExporter
		ExporterWrapperImpl impl = (ExporterWrapperImpl) exporterWrapper;
		assertNull(impl.getCustomProperties());
		exporterWrapper.setCustomProperties(properties);
		assertNull(impl.getCustomProperties());
		// try with CfgExporter
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(HibernateConfigurationExporter.class.getName());
		impl = (ExporterWrapperImpl) exporterWrapper;
		assertNull(impl.getCustomProperties());
		exporterWrapper.setCustomProperties(properties);
		assertSame(properties, impl.getCustomProperties());
	}

	@Test
	public void testSetOutput() {
		StringWriter stringWriter = new StringWriter();
		// setOutput should have no effect on non-CfgExporter
		ExporterWrapperImpl impl = (ExporterWrapperImpl) exporterWrapper;
		assertNull(impl.getOutput());
		exporterWrapper.setOutput(stringWriter);
		assertNull(impl.getOutput());
		// try with CfgExporter
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(HibernateConfigurationExporter.class.getName());
		impl = (ExporterWrapperImpl) exporterWrapper;
		assertNull(impl.getOutput());
		exporterWrapper.setOutput(stringWriter);
		assertSame(stringWriter, impl.getOutput());
	}

}
