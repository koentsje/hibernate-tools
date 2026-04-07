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
package org.hibernate.tool.orm.jbt.models;

import java.util.Properties;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.api.wrp.DdlExporterWrapper;

public class DdlExporterWrapperFactory {

	public static DdlExporterWrapper createDdlExporterWrapper(final DdlExporter wrappedDdlExporter) {
		return new DdlExporterWrapperImpl(wrappedDdlExporter);
	}

	public static class DdlExporterWrapperImpl implements DdlExporterWrapper {

		private DdlExporter ddlExporter = null;

		DdlExporterWrapperImpl(DdlExporter ddlExporter) {
			this.ddlExporter = ddlExporter;
		}

		@Override
		public Object getWrappedObject() {
			return ddlExporter;
		}

		@Override
		public void setExport(boolean b) {
			ddlExporter.getProperties().put(ExporterConstants.EXPORT_TO_DATABASE, b);
		}

		@Override
		public Properties getProperties() {
			return ddlExporter.getProperties();
		}

	}

}
