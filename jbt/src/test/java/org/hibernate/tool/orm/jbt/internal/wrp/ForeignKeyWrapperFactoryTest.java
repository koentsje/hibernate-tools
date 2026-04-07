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
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.ForeignKeyWrapperFactory.ForeignKeyWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ForeignKeyWrapperFactoryTest {

	private ForeignKeyWrapper foreignKeyWrapper = null;

	@BeforeEach
	public void beforeEach() {
		foreignKeyWrapper = ForeignKeyWrapperFactory.createForeignKeyWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(foreignKeyWrapper);
	}

	@Test
	public void testGetSetReferencedTable() {
		assertNull(foreignKeyWrapper.getReferencedTable());
		TableWrapper tw = TableWrapperFactory.createTableWrapper("ref_table");
		((ForeignKeyWrapperImpl) foreignKeyWrapper).setReferencedTable(tw);
		assertSame(tw, foreignKeyWrapper.getReferencedTable());
	}

	@Test
	public void testColumnIterator() {
		Iterator<ColumnWrapper> iter = foreignKeyWrapper.columnIterator();
		assertFalse(iter.hasNext());
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("fk_col");
		((ForeignKeyWrapperImpl) foreignKeyWrapper).addColumn(col);
		iter = foreignKeyWrapper.columnIterator();
		assertTrue(iter.hasNext());
		assertSame(col, iter.next());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testIsReferenceToPrimaryKey() {
		assertTrue(foreignKeyWrapper.isReferenceToPrimaryKey());
		((ForeignKeyWrapperImpl) foreignKeyWrapper).setReferenceToPrimaryKey(false);
		assertFalse(foreignKeyWrapper.isReferenceToPrimaryKey());
	}

	@Test
	public void testGetReferencedColumns() {
		assertTrue(foreignKeyWrapper.getReferencedColumns().isEmpty());
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("ref_col");
		((ForeignKeyWrapperImpl) foreignKeyWrapper).addReferencedColumn(col);
		assertEquals(1, foreignKeyWrapper.getReferencedColumns().size());
		assertSame(col, foreignKeyWrapper.getReferencedColumns().get(0));
	}

	@Test
	public void testContainsColumn() {
		ColumnWrapper col1 = ColumnWrapperFactory.createColumnWrapper("c1");
		ColumnWrapper col2 = ColumnWrapperFactory.createColumnWrapper("c2");
		((ForeignKeyWrapperImpl) foreignKeyWrapper).addColumn(col1);
		assertTrue(foreignKeyWrapper.containsColumn(col1));
		assertFalse(foreignKeyWrapper.containsColumn(col2));
	}

}
