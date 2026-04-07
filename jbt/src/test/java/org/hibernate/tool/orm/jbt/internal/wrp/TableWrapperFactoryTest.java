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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ForeignKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PrimaryKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.wrp.TableWrapperFactory.TableWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableWrapperFactoryTest {

	private TableWrapper tableWrapper = null;

	@BeforeEach
	public void beforeEach() {
		tableWrapper = TableWrapperFactory.createTableWrapper("foo");
	}

	@Test
	public void testConstruction() {
		assertNotNull(tableWrapper);
	}

	@Test
	public void testGetName() {
		assertEquals("foo", tableWrapper.getName());
	}

	@Test
	public void testSetName() {
		((TableWrapperImpl) tableWrapper).setName("bar");
		assertEquals("bar", tableWrapper.getName());
	}

	@Test
	public void testAddColumnAndGetColumnIterator() {
		Iterator<ColumnWrapper> iter = tableWrapper.getColumnIterator();
		assertFalse(iter.hasNext());
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("col1");
		tableWrapper.addColumn(col);
		iter = tableWrapper.getColumnIterator();
		assertTrue(iter.hasNext());
		assertSame(col, iter.next());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testGetSetCatalog() {
		assertNull(tableWrapper.getCatalog());
		((TableWrapperImpl) tableWrapper).setCatalog("cat");
		assertEquals("cat", tableWrapper.getCatalog());
	}

	@Test
	public void testGetSetSchema() {
		assertNull(tableWrapper.getSchema());
		((TableWrapperImpl) tableWrapper).setSchema("sch");
		assertEquals("sch", tableWrapper.getSchema());
	}

	@Test
	public void testGetSetPrimaryKey() {
		assertNull(tableWrapper.getPrimaryKey());
		PrimaryKeyWrapper pk = new DummyPrimaryKeyWrapper();
		((TableWrapperImpl) tableWrapper).setPrimaryKey(pk);
		assertSame(pk, tableWrapper.getPrimaryKey());
	}

	@Test
	public void testGetForeignKeyIterator() {
		Iterator<ForeignKeyWrapper> iter = tableWrapper.getForeignKeyIterator();
		assertFalse(iter.hasNext());
		ForeignKeyWrapper fk = new DummyForeignKeyWrapper();
		((TableWrapperImpl) tableWrapper).addForeignKey("fk1", fk);
		iter = tableWrapper.getForeignKeyIterator();
		assertTrue(iter.hasNext());
		assertSame(fk, iter.next());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testGetSetComment() {
		assertNull(tableWrapper.getComment());
		((TableWrapperImpl) tableWrapper).setComment("a comment");
		assertEquals("a comment", tableWrapper.getComment());
	}

	@Test
	public void testGetSetRowId() {
		assertNull(tableWrapper.getRowId());
		((TableWrapperImpl) tableWrapper).setRowId("rid");
		assertEquals("rid", tableWrapper.getRowId());
	}

	@Test
	public void testGetSetSubselect() {
		assertNull(tableWrapper.getSubselect());
		((TableWrapperImpl) tableWrapper).setSubselect("select 1");
		assertEquals("select 1", tableWrapper.getSubselect());
	}

	@Test
	public void testHasDenormalizedTables() {
		assertFalse(tableWrapper.hasDenormalizedTables());
		((TableWrapperImpl) tableWrapper).setHasDenormalizedTables(true);
		assertTrue(tableWrapper.hasDenormalizedTables());
	}

	@Test
	public void testIsAbstract() {
		assertFalse(tableWrapper.isAbstract());
		((TableWrapperImpl) tableWrapper).setAbstract(true);
		assertTrue(tableWrapper.isAbstract());
	}

	@Test
	public void testIsAbstractUnionTable() {
		assertFalse(tableWrapper.isAbstractUnionTable());
		((TableWrapperImpl) tableWrapper).setAbstract(true);
		assertFalse(tableWrapper.isAbstractUnionTable());
		((TableWrapperImpl) tableWrapper).setHasDenormalizedTables(true);
		assertTrue(tableWrapper.isAbstractUnionTable());
	}

	@Test
	public void testIsPhysicalTable() {
		assertTrue(tableWrapper.isPhysicalTable());
		((TableWrapperImpl) tableWrapper).setSubselect("select 1");
		assertFalse(tableWrapper.isPhysicalTable());
	}

	@Test
	public void testGetSetIdentifierValue() {
		assertNull(tableWrapper.getIdentifierValue());
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		((TableWrapperImpl) tableWrapper).setIdentifierValue(value);
		assertSame(value, tableWrapper.getIdentifierValue());
	}

	@Test
	public void testGetColumns() {
		assertTrue(((TableWrapperImpl) tableWrapper).getColumns().isEmpty());
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("col1");
		tableWrapper.addColumn(col);
		assertEquals(1, ((TableWrapperImpl) tableWrapper).getColumns().size());
		assertSame(col, ((TableWrapperImpl) tableWrapper).getColumns().get(0));
	}

	// --- Dummy implementations for testing ---

	private static class DummyPrimaryKeyWrapper implements PrimaryKeyWrapper {
		@Override public void addColumn(ColumnWrapper column) {}
		@Override public int getColumnSpan() { return 0; }
		@Override public java.util.List<ColumnWrapper> getColumns() { return java.util.Collections.emptyList(); }
		@Override public ColumnWrapper getColumn(int i) { return null; }
		@Override public TableWrapper getTable() { return null; }
		@Override public boolean containsColumn(ColumnWrapper column) { return false; }
		@Override public Iterator<ColumnWrapper> columnIterator() { return java.util.Collections.emptyIterator(); }
		@Override public String getName() { return "pk"; }
	}

	private static class DummyForeignKeyWrapper implements ForeignKeyWrapper {
		@Override public TableWrapper getReferencedTable() { return null; }
		@Override public Iterator<ColumnWrapper> columnIterator() { return java.util.Collections.emptyIterator(); }
		@Override public boolean isReferenceToPrimaryKey() { return true; }
		@Override public java.util.List<ColumnWrapper> getReferencedColumns() { return java.util.Collections.emptyList(); }
		@Override public boolean containsColumn(ColumnWrapper column) { return false; }
	}

}
