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

import java.util.EnumSet;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.SchemaExportWrapper;
import org.hibernate.tool.orm.jbt.internal.util.MetadataHelper;
import org.hibernate.tool.schema.TargetType;

public class SchemaExportWrapperFactory {

	public static SchemaExportWrapper createSchemaExportWrapper(ConfigurationWrapper configurationWrapper) {
		return new SchemaExportWrapperImpl(
				new SchemaExport(),
				(Configuration) configurationWrapper.getWrappedObject());
	}

	public static class SchemaExportWrapperImpl implements SchemaExportWrapper {

		private Configuration configuration = null;
		private SchemaExport schemaExport = null;

		SchemaExportWrapperImpl(SchemaExport se, Configuration c) {
			this.configuration = c;
			this.schemaExport = se;
		}

		@Override
		public Object getWrappedObject() {
			return schemaExport;
		}

		@Override
		public void create() {
			schemaExport.create(
					EnumSet.of(TargetType.DATABASE),
					MetadataHelper.getMetadata(configuration));
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Throwable> getExceptions() {
			return schemaExport.getExceptions();
		}

	}

}
