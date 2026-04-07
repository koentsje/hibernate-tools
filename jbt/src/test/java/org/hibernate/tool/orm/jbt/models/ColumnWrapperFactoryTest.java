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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.factory.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.models.ColumnWrapperFactory.ColumnWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnWrapperFactoryTest {

	private ColumnWrapper columnWrapper = null;

	@BeforeEach
	public void beforeEach() {
		columnWrapper = ColumnWrapperFactory.createColumnWrapper("foo");
	}

	@Test
	public void testConstruction() {
		assertNotNull(columnWrapper);
	}

	@Test
	public void testGetName() {
		assertEquals("foo", columnWrapper.getName());
	}

	@Test
	public void testGetSetSqlTypeCode() {
		assertNull(columnWrapper.getSqlTypeCode());
		((ColumnWrapperImpl) columnWrapper).setSqlTypeCode(12);
		assertEquals(Integer.valueOf(12), columnWrapper.getSqlTypeCode());
	}

	@Test
	public void testGetSetSqlType() {
		assertNull(columnWrapper.getSqlType());
		columnWrapper.setSqlType("varchar(255)");
		assertEquals("varchar(255)", columnWrapper.getSqlType());
	}

	@Test
	public void testGetSqlTypeWithConfiguration() {
		assertNull(columnWrapper.getSqlType(null));
		columnWrapper.setSqlType("integer");
		assertEquals("integer", columnWrapper.getSqlType(null));
	}

	@Test
	public void testGetSetLength() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getLength());
		((ColumnWrapperImpl) columnWrapper).setLength(100L);
		assertEquals(100L, columnWrapper.getLength());
	}

	@Test
	public void testGetDefaultLength() {
		assertEquals(255, columnWrapper.getDefaultLength());
	}

	@Test
	public void testGetSetPrecision() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getPrecision());
		((ColumnWrapperImpl) columnWrapper).setPrecision(10);
		assertEquals(10, columnWrapper.getPrecision());
	}

	@Test
	public void testGetDefaultPrecision() {
		assertEquals(19, columnWrapper.getDefaultPrecision());
	}

	@Test
	public void testGetSetScale() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getScale());
		((ColumnWrapperImpl) columnWrapper).setScale(5);
		assertEquals(5, columnWrapper.getScale());
	}

	@Test
	public void testGetDefaultScale() {
		assertEquals(2, columnWrapper.getDefaultScale());
	}

	@Test
	public void testIsNullable() {
		assertTrue(columnWrapper.isNullable());
		((ColumnWrapperImpl) columnWrapper).setNullable(false);
		assertFalse(columnWrapper.isNullable());
	}

	@Test
	public void testGetSetValue() {
		assertNull(columnWrapper.getValue());
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		((ColumnWrapperImpl) columnWrapper).setValue(value);
		assertSame(value, columnWrapper.getValue());
	}

	@Test
	public void testIsUnique() {
		assertFalse(columnWrapper.isUnique());
		((ColumnWrapperImpl) columnWrapper).setUnique(true);
		assertTrue(columnWrapper.isUnique());
	}

	@Test
	public void testSetName() {
		assertEquals("foo", columnWrapper.getName());
		((ColumnWrapperImpl) columnWrapper).setName("bar");
		assertEquals("bar", columnWrapper.getName());
	}

}
