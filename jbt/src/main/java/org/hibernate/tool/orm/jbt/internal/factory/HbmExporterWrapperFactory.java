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
package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.reveng.models.exporter.hbm.HbmXmlExporter;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.internal.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class HbmExporterWrapperFactory {

	public static HbmExporterWrapper createHbmExporterWrapper(
			ConfigurationWrapper configurationWrapper,
			File file) {
		return createHbmExporterWrapper((Configuration)configurationWrapper.getWrappedObject(), file);
	}

	private static HbmExporterWrapper createHbmExporterWrapper(Configuration cfg, File f) {
		return new HbmExporterWrapperImpl(new HbmExporterExtension(cfg, f)) ;
	}

	public static class HbmExporterExtension implements Exporter {

		public Object delegateExporter = null;

		private final HbmXmlExporter delegate = HbmXmlExporter.create();

		private HbmExporterExtension(Configuration cfg, File f) {
			delegate.getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR,
					new ConfigurationMetadataDescriptor(cfg));
			if (f != null) {
				delegate.getProperties().put(ExporterConstants.OUTPUT_FILE_NAME, f);
			}
		}

		@Override
		public Properties getProperties() {
			return delegate.getProperties();
		}

		@Override
		public void start() {
			delegate.start();
		}
	}

	private static class HbmExporterWrapperImpl
			extends AbstractWrapper
			implements HbmExporterWrapper {

		private HbmExporterExtension hbmExporterExtension;

		private HbmExporterWrapperImpl(HbmExporterExtension hbmExporterExtension) {
			this.hbmExporterExtension = hbmExporterExtension;
		}

		@Override
		public Exporter getWrappedObject() {
			return hbmExporterExtension;
		}

		@Override
		public void start() {
			hbmExporterExtension.start();
		}

		@Override
		public File getOutputDirectory() {
			return (File)hbmExporterExtension.getProperties().get(ExporterConstants.DESTINATION_FOLDER);
		}

		@Override
		public void setOutputDirectory(File f) {
			hbmExporterExtension.getProperties().put(ExporterConstants.DESTINATION_FOLDER, f);
		}

		@Override
		public void exportPOJO(Map<Object, Object> map, Object pojoClass) {
			// exportPOJO is no longer supported; the new HbmXmlExporter
			// uses FreeMarker templates and ClassDetails directly
		}

		@Override
		public void setExportPOJODelegate(Object delegate) {
			hbmExporterExtension.delegateExporter = delegate;
		}



	}

}
