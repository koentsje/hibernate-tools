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
import java.util.List;

import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ForeignKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;

public class ForeignKeyWrapperFactory {

	public static ForeignKeyWrapper createForeignKeyWrapper() {
		return new ForeignKeyWrapperImpl();
	}

	public static class ForeignKeyWrapperImpl implements ForeignKeyWrapper {

		private TableWrapper referencedTable;
		private boolean isReferenceToPrimaryKey = true;
		private final List<ColumnWrapper> columns = new ArrayList<>();
		private final List<ColumnWrapper> referencedColumns = new ArrayList<>();

		@Override
		public TableWrapper getReferencedTable() {
			return referencedTable;
		}

		@Override
		public Iterator<ColumnWrapper> columnIterator() {
			return columns.iterator();
		}

		@Override
		public boolean isReferenceToPrimaryKey() {
			return isReferenceToPrimaryKey;
		}

		@Override
		public List<ColumnWrapper> getReferencedColumns() {
			return Collections.unmodifiableList(referencedColumns);
		}

		@Override
		public boolean containsColumn(ColumnWrapper column) {
			return columns.contains(column);
		}

		// --- Mutators ---

		public void setReferencedTable(TableWrapper table) {
			this.referencedTable = table;
		}

		public void setReferenceToPrimaryKey(boolean b) {
			this.isReferenceToPrimaryKey = b;
		}

		public void addColumn(ColumnWrapper column) {
			columns.add(column);
		}

		public void addReferencedColumn(ColumnWrapper column) {
			referencedColumns.add(column);
		}

		public List<ColumnWrapper> getColumns() {
			return Collections.unmodifiableList(columns);
		}
	}

}
