package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.PrimaryKeyWrapper;
import org.hibernate.tool.orm.jbt.api.TableWrapper;
import org.hibernate.tool.orm.jbt.api.ValueWrapper;

public class TableWrapperFactory {

	public static TableWrapper createTableWrapper(Table wrappedTable) {
		return new TableWrapperImpl(wrappedTable);
	}
	
	private static class TableWrapperImpl implements TableWrapper {
		
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
			Value v = table.getIdentifierValue();
			return v == null ? null : ValueWrapperFactory.createValueWrapper(v); 
		}

		
		
	}

}
