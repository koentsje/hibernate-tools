package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Iterator;

public interface TableWrapper extends Wrapper {

	String getName();
	void addColumn(ColumnWrapper column);
	String getCatalog();
	String getSchema();
	PrimaryKeyWrapper getPrimaryKey();
	Iterator<ColumnWrapper> getColumnIterator();
	Iterator<ForeignKeyWrapper> getForeignKeyIterator();
	String getComment();
	String getRowId();
	String getSubselect();
	boolean hasDenormalizedTables() ;
	boolean isAbstract();
	boolean isAbstractUnionTable();
	boolean isPhysicalTable();
	ValueWrapper getIdentifierValue();
	
}
