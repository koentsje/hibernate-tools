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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.HbmExporterWrapper;

public class HbmExporterWrapperFactory {

	public static HbmExporterWrapper createHbmExporterWrapper(
			ConfigurationWrapper configurationWrapper, File file) {
		return new HbmExporterWrapperImpl(configurationWrapper, file);
	}

	public static class HbmExporterWrapperImpl implements HbmExporterWrapper {

		private ConfigurationWrapper configurationWrapper;
		private File outputFile;
		private File outputDirectory;
		private Object delegateExporter;

		HbmExporterWrapperImpl(ConfigurationWrapper configurationWrapper, File file) {
			this.configurationWrapper = configurationWrapper;
			this.outputFile = file;
		}

		@Override
		public Object getWrappedObject() {
			return this;
		}

		@Override
		public void start() {
			// TODO: bridge to new HbmXmlExporter
		}

		@Override
		public File getOutputDirectory() {
			return outputDirectory;
		}

		@Override
		public void setOutputDirectory(File f) {
			this.outputDirectory = f;
		}

		@Override
		public void exportPOJO(Map<Object, Object> map, Object pojoClass) {
			if (delegateExporter != null) {
				delegateExporterExportPOJO(map, pojoClass);
			}
		}

		@Override
		public void setExportPOJODelegate(Object delegate) {
			this.delegateExporter = delegate;
		}

		public File getOutputFile() {
			return outputFile;
		}

		public ConfigurationWrapper getConfigurationWrapper() {
			return configurationWrapper;
		}

		public Object getDelegateExporter() {
			return delegateExporter;
		}

		private void delegateExporterExportPOJO(Map<Object, Object> map, Object pojoClass) {
			try {
				Method method = delegateExporter
						.getClass()
						.getDeclaredMethod("exportPojo", Map.class, Object.class, String.class);
				method.setAccessible(true);
				method.invoke(delegateExporter, map, pojoClass, String.valueOf(pojoClass));
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
