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
import org.hibernate.tool.orm.jbt.api.wrp.PrimaryKeyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.PrimaryKeyWrapperFactory.PrimaryKeyWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrimaryKeyWrapperFactoryTest {

	private PrimaryKeyWrapper primaryKeyWrapper = null;

	@BeforeEach
	public void beforeEach() {
		primaryKeyWrapper = PrimaryKeyWrapperFactory.createPrimaryKeyWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(primaryKeyWrapper);
	}

	@Test
	public void testGetSetName() {
		assertNull(primaryKeyWrapper.getName());
		((PrimaryKeyWrapperImpl) primaryKeyWrapper).setName("pk_foo");
		assertEquals("pk_foo", primaryKeyWrapper.getName());
	}

	@Test
	public void testGetSetTable() {
		assertNull(primaryKeyWrapper.getTable());
		TableWrapper tw = TableWrapperFactory.createTableWrapper("t");
		((PrimaryKeyWrapperImpl) primaryKeyWrapper).setTable(tw);
		assertSame(tw, primaryKeyWrapper.getTable());
	}

	@Test
	public void testAddColumnAndGetColumnSpan() {
		assertEquals(0, primaryKeyWrapper.getColumnSpan());
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("c1");
		primaryKeyWrapper.addColumn(col);
		assertEquals(1, primaryKeyWrapper.getColumnSpan());
	}

	@Test
	public void testGetColumns() {
		assertTrue(primaryKeyWrapper.getColumns().isEmpty());
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("c1");
		primaryKeyWrapper.addColumn(col);
		assertEquals(1, primaryKeyWrapper.getColumns().size());
		assertSame(col, primaryKeyWrapper.getColumns().get(0));
	}

	@Test
	public void testGetColumn() {
		ColumnWrapper col1 = ColumnWrapperFactory.createColumnWrapper("c1");
		ColumnWrapper col2 = ColumnWrapperFactory.createColumnWrapper("c2");
		primaryKeyWrapper.addColumn(col1);
		primaryKeyWrapper.addColumn(col2);
		assertSame(col1, primaryKeyWrapper.getColumn(0));
		assertSame(col2, primaryKeyWrapper.getColumn(1));
	}

	@Test
	public void testContainsColumn() {
		ColumnWrapper col1 = ColumnWrapperFactory.createColumnWrapper("c1");
		ColumnWrapper col2 = ColumnWrapperFactory.createColumnWrapper("c2");
		primaryKeyWrapper.addColumn(col1);
		assertTrue(primaryKeyWrapper.containsColumn(col1));
		assertFalse(primaryKeyWrapper.containsColumn(col2));
	}

	@Test
	public void testColumnIterator() {
		Iterator<ColumnWrapper> iter = primaryKeyWrapper.columnIterator();
		assertFalse(iter.hasNext());
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("c1");
		primaryKeyWrapper.addColumn(col);
		iter = primaryKeyWrapper.columnIterator();
		assertTrue(iter.hasNext());
		assertSame(col, iter.next());
		assertFalse(iter.hasNext());
	}

}
