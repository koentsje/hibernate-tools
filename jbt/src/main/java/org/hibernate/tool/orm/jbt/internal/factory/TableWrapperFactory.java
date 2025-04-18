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

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ForeignKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PrimaryKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class TableWrapperFactory {

	public static TableWrapper createTableWrapper(String name) {
		Table t = new Table("Hibernate Tools", name);
		t.setPrimaryKey(new PrimaryKey(t));
		return TableWrapperFactory.createTableWrapper(t);
	}

	static TableWrapper createTableWrapper(Table wrappedTable) {
		return new TableWrapperImpl(wrappedTable);
	}
	
	private static class TableWrapperImpl 
			extends AbstractWrapper
			implements TableWrapper {
		
		private Table table = null;
		
		private TableWrapperImpl(Table table) {
			this.table = table;
		}
		
		@Override 
		public Table getWrappedObject() { 
			return table; 
		}
		
		@Override
		public String getName() { 
			return table.getName(); 
		}

		@Override
		public void addColumn(ColumnWrapper column) { 
			table.addColumn((Column)column.getWrappedObject()); 
		}

		@Override
		public String getCatalog() { 
			return table.getCatalog(); 
		}

		@Override
		public String getSchema() { 
			return table.getSchema(); 
		}

		@Override
		public PrimaryKeyWrapper getPrimaryKey() { 
			PrimaryKey pk = table.getPrimaryKey();
			return pk == null ? null : PrimaryKeyWrapperFactory.createPrimaryKeyWrapper(pk); 
		}

		@Override
		public Iterator<ColumnWrapper> getColumnIterator() { 
			Iterator<Column> iterator = table.getColumns().iterator();
			return new Iterator<ColumnWrapper>() {
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}
				@Override
				public ColumnWrapper next() {
					return ColumnWrapperFactory.createColumnWrapper(iterator.next());
				}
				
			};
		}
		
		@Override 
		public Iterator<ForeignKeyWrapper> getForeignKeyIterator() {
			Iterator<ForeignKey> iterator = table.getForeignKeys().values().iterator();
			return new Iterator<ForeignKeyWrapper>() {
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}
				@Override
				public ForeignKeyWrapper next() {
					return ForeignKeyWrapperFactory.createForeignKeyWrapper(iterator.next());
				}
				
			};
		}

		@Override
		public String getComment() { 
			return table.getComment(); 
		}

		@Override
		public String getRowId() { 
			return table.getRowId(); 
		}

		@Override
		public String getSubselect() { 
			return table.getSubselect(); 
		}

		@Override
		public boolean hasDenormalizedTables() { 
			return table.hasDenormalizedTables(); 
		}

		@Override
		public boolean isAbstract() { 
			return table.isAbstract(); 
		}

		@Override
		public boolean isAbstractUnionTable() { 
			return table.isAbstractUnionTable(); 
		}

		@Override
		public boolean isPhysicalTable() { 
			return table.isPhysicalTable(); 
		}

		@Override
		public ValueWrapper getIdentifierValue() { 
			// Method 'Table#getIdentifierValue()' has been removed starting from Hibernate 7.0.0.Alpha3
			// TODO see JBIDE-29213
			// Value v = table.getIdentifierValue();
			// return v == null ? null : ValueWrapperFactory.createValueWrapper(v); 
			return null;
		}

		
		
	}

}
