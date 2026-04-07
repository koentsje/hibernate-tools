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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ForeignKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PrimaryKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;

public class TableWrapperFactory {

	public static TableWrapper createTableWrapper(String name) {
		return new TableWrapperImpl(name);
	}

	public static class TableWrapperImpl implements TableWrapper {

		private String name;
		private String catalog;
		private String schema;
		private String comment;
		private String rowId;
		private String subselect;
		private boolean isAbstract;
		private boolean hasDenormalizedTables;
		private PrimaryKeyWrapper primaryKey;
		private ValueWrapper identifierValue;

		private final List<ColumnWrapper> columns = new ArrayList<>();
		private final Map<String, ForeignKeyWrapper> foreignKeys = new LinkedHashMap<>();

		TableWrapperImpl(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void addColumn(ColumnWrapper column) {
			columns.add(column);
		}

		@Override
		public String getCatalog() {
			return catalog;
		}

		@Override
		public String getSchema() {
			return schema;
		}

		@Override
		public PrimaryKeyWrapper getPrimaryKey() {
			return primaryKey;
		}

		@Override
		public Iterator<ColumnWrapper> getColumnIterator() {
			return columns.iterator();
		}

		@Override
		public Iterator<ForeignKeyWrapper> getForeignKeyIterator() {
			return foreignKeys.values().iterator();
		}

		@Override
		public String getComment() {
			return comment;
		}

		@Override
		public String getRowId() {
			return rowId;
		}

		@Override
		public String getSubselect() {
			return subselect;
		}

		@Override
		public boolean hasDenormalizedTables() {
			return hasDenormalizedTables;
		}

		@Override
		public boolean isAbstract() {
			return isAbstract;
		}

		@Override
		public boolean isAbstractUnionTable() {
			return isAbstract && hasDenormalizedTables;
		}

		@Override
		public boolean isPhysicalTable() {
			return subselect == null;
		}

		@Override
		public ValueWrapper getIdentifierValue() {
			return identifierValue;
		}

		// --- Mutators ---

		public void setName(String name) {
			this.name = name;
		}

		public void setCatalog(String catalog) {
			this.catalog = catalog;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public void setRowId(String rowId) {
			this.rowId = rowId;
		}

		public void setSubselect(String subselect) {
			this.subselect = subselect;
		}

		public void setAbstract(boolean isAbstract) {
			this.isAbstract = isAbstract;
		}

		public void setHasDenormalizedTables(boolean b) {
			this.hasDenormalizedTables = b;
		}

		public void setPrimaryKey(PrimaryKeyWrapper primaryKey) {
			this.primaryKey = primaryKey;
		}

		public void setIdentifierValue(ValueWrapper identifierValue) {
			this.identifierValue = identifierValue;
		}

		public void addForeignKey(String name, ForeignKeyWrapper fk) {
			foreignKeys.put(name, fk);
		}

		public List<ColumnWrapper> getColumns() {
			return Collections.unmodifiableList(columns);
		}
	}

}
