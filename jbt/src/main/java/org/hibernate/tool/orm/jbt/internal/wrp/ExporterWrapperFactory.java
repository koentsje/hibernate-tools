/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2025 Red Hat, Inc.
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

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.tool.internal.exporter.cfg.CfgXmlExporter;
import org.hibernate.tool.internal.exporter.ddl.DdlExporter;
import org.hibernate.tool.internal.exporter.generic.GenericExporter;
import org.hibernate.tool.internal.exporter.query.QueryExporter;
import org.hibernate.tool.orm.jbt.api.wrp.ArtifactCollectorWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.DdlExporterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ExporterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.GenericExporterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.QueryExporterWrapper;
import org.hibernate.tool.orm.jbt.internal.util.ReflectUtil;

public class ExporterWrapperFactory {

	public static ExporterWrapper createExporterWrapper(String className) {
		Object wrappedObject = ReflectUtil.createInstance(className);
		return new ExporterWrapperImpl(wrappedObject);
	}

	public static class ExporterWrapperImpl implements ExporterWrapper {

		private final Object wrappedObject;
		private final Properties properties = new Properties();
		private GenericExporterWrapper genericExporterWrapper;
		private DdlExporterWrapper ddlExporterWrapper;
		private QueryExporterWrapper queryExporterWrapper;
		private Properties customProperties;
		private StringWriter output;

		ExporterWrapperImpl(Object wrappedObject) {
			this.wrappedObject = wrappedObject;
			if (wrappedObject instanceof GenericExporter) {
				genericExporterWrapper = GenericExporterWrapperFactory.createGenericExporterWrapper();
			} else if (wrappedObject instanceof DdlExporter) {
				ddlExporterWrapper = DdlExporterWrapperFactory.createDdlExporterWrapper();
			} else if (wrappedObject instanceof QueryExporter) {
				queryExporterWrapper = QueryExporterWrapperFactory.createQueryExporterWrapper();
			}
		}

		@Override
		public Object getWrappedObject() {
			return wrappedObject;
		}

		@Override
		public void setConfiguration(ConfigurationWrapper configuration) {
			if (wrappedObject instanceof CfgXmlExporter) {
				this.customProperties = configuration.getProperties();
			}
			properties.put("configuration", configuration);
		}

		@Override
		public void setArtifactCollector(ArtifactCollectorWrapper artifactCollectorWrapper) {
			properties.put("artifact_collector", artifactCollectorWrapper.getWrappedObject());
		}

		@Override
		public void setOutputDirectory(File dir) {
			properties.put("output_directory", dir);
		}

		@Override
		public void setTemplatePath(String[] templatePath) {
			properties.put("template_path", templatePath);
		}

		@Override
		public void start() {
			// TODO: bridge to new models-based exporters
		}

		@Override
		public Properties getProperties() {
			return properties;
		}

		@Override
		public GenericExporterWrapper getGenericExporter() {
			return genericExporterWrapper;
		}

		@Override
		public DdlExporterWrapper getHbm2DDLExporter() {
			return ddlExporterWrapper;
		}

		@Override
		public QueryExporterWrapper getQueryExporter() {
			return queryExporterWrapper;
		}

		@Override
		public void setCustomProperties(Properties properties) {
			if (wrappedObject instanceof CfgXmlExporter) {
				this.customProperties = properties;
			}
		}

		@Override
		public void setOutput(StringWriter stringWriter) {
			if (wrappedObject instanceof CfgXmlExporter) {
				this.output = stringWriter;
			}
		}

		public Properties getCustomProperties() {
			return customProperties;
		}

		public StringWriter getOutput() {
			return output;
		}

	}

}
