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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengDialect;
import org.hibernate.tool.api.reveng.RevengDialectFactory;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.descriptor.ColumnDescriptor;
import org.hibernate.tool.internal.descriptor.TableDescriptor;
import org.hibernate.tool.internal.reader.ModelsDatabaseSchemaReader;
import org.hibernate.tool.orm.jbt.api.wrp.DatabaseReaderWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.RevengStrategyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class DatabaseReaderWrapperFactory {

	public static DatabaseReaderWrapper createDatabaseReaderWrapper(
			Properties properties,
			RevengStrategyWrapper revengStrategy) {
		return new DatabaseReaderWrapperImpl(properties, (RevengStrategy)revengStrategy.getWrappedObject());
	}

	static class DatabaseReaderWrapperImpl
			extends AbstractWrapper
			implements DatabaseReaderWrapper {

		private final Properties properties;
		private final RevengStrategy revengStrategy;

		public DatabaseReaderWrapperImpl(
				Properties properties,
				RevengStrategy revengStrategy) {
			this.properties = properties;
			this.revengStrategy = revengStrategy;
		}

		public Map<String, List<TableWrapper>> collectDatabaseTables() {
			StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(properties)
					.build();
			try {
				RevengDialect revengDialect = RevengDialectFactory
						.createMetaDataDialect(
								serviceRegistry.getService(JdbcServices.class).getDialect(),
								properties);
				try {
					revengDialect.configure(
							serviceRegistry.getService(
									org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class));
					String defaultCatalog = (String) properties.get(AvailableSettings.DEFAULT_CATALOG);
					String defaultSchema = (String) properties.get(AvailableSettings.DEFAULT_SCHEMA);
					List<TableDescriptor> tables = ModelsDatabaseSchemaReader
							.create(revengDialect, revengStrategy, defaultCatalog, defaultSchema)
							.readSchema();
					Map<String, List<TableWrapper>> result = new HashMap<>();
					for (TableDescriptor table : tables) {
						String qualifier = "";
						if (table.getCatalog() != null) {
							qualifier += table.getCatalog();
						}
						if (table.getSchema() != null) {
							if (!"".equals(qualifier)) {
								qualifier += ".";
							}
							qualifier += table.getSchema();
						}
						List<TableWrapper> list = result.computeIfAbsent(qualifier, k -> new ArrayList<>());
						TableWrapper tw = TableWrapperFactory.createTableWrapper(table.getTableName());
						Table wrappedTable = (Table) tw.getWrappedObject();
						wrappedTable.setCatalog(table.getCatalog());
						wrappedTable.setSchema(table.getSchema());
						for (ColumnDescriptor column : table.getColumns()) {
							tw.addColumn(ColumnWrapperFactory.createColumnWrapper(column.getColumnName()));
						}
						list.add(tw);
					}
					return result;
				} finally {
					revengDialect.close();
				}
			} finally {
				StandardServiceRegistryBuilder.destroy(serviceRegistry);
			}
		}

	}

}
