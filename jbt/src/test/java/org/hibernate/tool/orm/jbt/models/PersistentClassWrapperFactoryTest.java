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
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.tool.orm.jbt.api.wrp.JoinWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.TableWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.models.PersistentClassWrapperFactory.PersistentClassWrapperImpl;
import org.hibernate.tool.orm.jbt.models.PersistentClassWrapperFactory.SpecialRootClassWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersistentClassWrapperFactoryTest {

	private PersistentClassWrapper rootClassWrapper = null;
	private PersistentClassWrapper singleTableSubclassWrapper = null;
	private PersistentClassWrapper joinedSubclassWrapper = null;
	private PersistentClassWrapper specialRootClassWrapper = null;

	private PropertyWrapper property = null;

	@BeforeEach
	public void beforeEach() {
		rootClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		singleTableSubclassWrapper = PersistentClassWrapperFactory.createSingleTableSubClassWrapper(rootClassWrapper);
		joinedSubclassWrapper = PersistentClassWrapperFactory.createJoinedTableSubClassWrapper(rootClassWrapper);
		property = PropertyWrapperFactory.createPropertyWrapper();
		specialRootClassWrapper = PersistentClassWrapperFactory.createSpecialRootClassWrapper(property);
	}

	@Test
	public void testConstruction() {
		assertNotNull(rootClassWrapper);
		assertNotNull(rootClassWrapper.getWrappedObject());
		assertTrue(rootClassWrapper.getWrappedObject() instanceof ClassDetails);
		assertNotNull(singleTableSubclassWrapper);
		assertNotNull(singleTableSubclassWrapper.getWrappedObject());
		assertTrue(singleTableSubclassWrapper.getWrappedObject() instanceof ClassDetails);
		assertNotNull(joinedSubclassWrapper);
		assertNotNull(joinedSubclassWrapper.getWrappedObject());
		assertTrue(joinedSubclassWrapper.getWrappedObject() instanceof ClassDetails);
		assertNotNull(specialRootClassWrapper);
		assertNotNull(specialRootClassWrapper.getWrappedObject());
		assertTrue(specialRootClassWrapper.getWrappedObject() instanceof ClassDetails);
	}

	@Test
	public void testGetEntityName() {
		assertNull(rootClassWrapper.getEntityName());
		assertNull(singleTableSubclassWrapper.getEntityName());
		assertNull(joinedSubclassWrapper.getEntityName());
		assertNull(specialRootClassWrapper.getEntityName());
		rootClassWrapper.setEntityName("foo");
		singleTableSubclassWrapper.setEntityName("bar");
		joinedSubclassWrapper.setEntityName("raz");
		specialRootClassWrapper.setEntityName("oof");
		assertEquals("foo", rootClassWrapper.getEntityName());
		assertEquals("bar", singleTableSubclassWrapper.getEntityName());
		assertEquals("raz", joinedSubclassWrapper.getEntityName());
		assertEquals("oof", specialRootClassWrapper.getEntityName());
	}

	@Test
	public void testGetClassName() {
		assertNull(rootClassWrapper.getClassName());
		assertNull(singleTableSubclassWrapper.getClassName());
		assertNull(joinedSubclassWrapper.getClassName());
		assertNull(specialRootClassWrapper.getClassName());
		rootClassWrapper.setClassName("foo");
		singleTableSubclassWrapper.setClassName("bar");
		joinedSubclassWrapper.setClassName("raz");
		specialRootClassWrapper.setClassName("oof");
		assertEquals("foo", rootClassWrapper.getClassName());
		assertEquals("bar", singleTableSubclassWrapper.getClassName());
		assertEquals("raz", joinedSubclassWrapper.getClassName());
		assertEquals("oof", specialRootClassWrapper.getClassName());
	}

	@Test
	public void testIsAssignableToRootClass() {
		assertTrue(rootClassWrapper.isAssignableToRootClass());
		assertFalse(singleTableSubclassWrapper.isAssignableToRootClass());
		assertFalse(joinedSubclassWrapper.isAssignableToRootClass());
		assertTrue(specialRootClassWrapper.isAssignableToRootClass());
	}

	@Test
	public void testIsRootClass() {
		assertTrue(rootClassWrapper.isRootClass());
		assertFalse(singleTableSubclassWrapper.isRootClass());
		assertFalse(joinedSubclassWrapper.isRootClass());
		assertFalse(specialRootClassWrapper.isRootClass());
	}

	@Test
	public void testGetIdentifierProperty() {
		assertNull(rootClassWrapper.getIdentifierProperty());
		assertNull(singleTableSubclassWrapper.getIdentifierProperty());
		assertNull(joinedSubclassWrapper.getIdentifierProperty());
		assertNull(specialRootClassWrapper.getIdentifierProperty());
		PropertyWrapper property = PropertyWrapperFactory.createPropertyWrapper();
		rootClassWrapper.setIdentifierProperty(property);
		assertSame(property, rootClassWrapper.getIdentifierProperty());
		assertSame(property, singleTableSubclassWrapper.getIdentifierProperty());
		assertSame(property, joinedSubclassWrapper.getIdentifierProperty());
		assertNull(specialRootClassWrapper.getIdentifierProperty());
		PropertyWrapper specialProp = PropertyWrapperFactory.createPropertyWrapper();
		specialRootClassWrapper.setIdentifierProperty(specialProp);
		assertSame(specialProp, specialRootClassWrapper.getIdentifierProperty());
	}

	@Test
	public void testHasIdentifierProperty() {
		assertFalse(rootClassWrapper.hasIdentifierProperty());
		assertFalse(singleTableSubclassWrapper.hasIdentifierProperty());
		assertFalse(joinedSubclassWrapper.hasIdentifierProperty());
		assertFalse(specialRootClassWrapper.hasIdentifierProperty());
		rootClassWrapper.setIdentifierProperty(PropertyWrapperFactory.createPropertyWrapper());
		assertTrue(rootClassWrapper.hasIdentifierProperty());
		assertTrue(singleTableSubclassWrapper.hasIdentifierProperty());
		assertTrue(joinedSubclassWrapper.hasIdentifierProperty());
		assertFalse(specialRootClassWrapper.hasIdentifierProperty());
	}

	@Test
	public void testIsInstanceOfRootClass() {
		assertTrue(rootClassWrapper.isInstanceOfRootClass());
		assertFalse(singleTableSubclassWrapper.isInstanceOfRootClass());
		assertFalse(joinedSubclassWrapper.isInstanceOfRootClass());
		assertTrue(specialRootClassWrapper.isInstanceOfRootClass());
	}

	@Test
	public void testIsInstanceOfSubclass() {
		assertFalse(rootClassWrapper.isInstanceOfSubclass());
		assertTrue(singleTableSubclassWrapper.isInstanceOfSubclass());
		assertTrue(joinedSubclassWrapper.isInstanceOfSubclass());
		assertFalse(specialRootClassWrapper.isInstanceOfSubclass());
	}

	@Test
	public void testGetRootClass() {
		assertSame(rootClassWrapper, rootClassWrapper.getRootClass());
		assertSame(rootClassWrapper, singleTableSubclassWrapper.getRootClass());
		assertSame(rootClassWrapper, joinedSubclassWrapper.getRootClass());
		assertSame(specialRootClassWrapper, specialRootClassWrapper.getRootClass());
	}

	@Test
	public void testGetPropertyClosureIterator() {
		assertFalse(rootClassWrapper.getPropertyClosureIterator().hasNext());
		assertFalse(singleTableSubclassWrapper.getPropertyClosureIterator().hasNext());
		assertFalse(joinedSubclassWrapper.getPropertyClosureIterator().hasNext());
		assertFalse(specialRootClassWrapper.getPropertyClosureIterator().hasNext());
		PropertyWrapper property = PropertyWrapperFactory.createPropertyWrapper();
		property.setName("prop1");
		rootClassWrapper.addProperty(property);
		Iterator<PropertyWrapper> iter = rootClassWrapper.getPropertyClosureIterator();
		assertTrue(iter.hasNext());
		assertSame(property, iter.next());
		iter = singleTableSubclassWrapper.getPropertyClosureIterator();
		assertTrue(iter.hasNext());
		assertSame(property, iter.next());
		iter = joinedSubclassWrapper.getPropertyClosureIterator();
		assertTrue(iter.hasNext());
		assertSame(property, iter.next());
		assertFalse(specialRootClassWrapper.getPropertyClosureIterator().hasNext());
		specialRootClassWrapper.addProperty(property);
		iter = specialRootClassWrapper.getPropertyClosureIterator();
		assertTrue(iter.hasNext());
		assertSame(property, iter.next());
	}

	@Test
	public void testGetSuperclass() {
		assertNull(rootClassWrapper.getSuperclass());
		assertSame(rootClassWrapper, singleTableSubclassWrapper.getSuperclass());
		assertSame(rootClassWrapper, joinedSubclassWrapper.getSuperclass());
		assertNull(specialRootClassWrapper.getSuperclass());
	}

	@Test
	public void testGetPropertyIterator() {
		assertFalse(rootClassWrapper.getPropertyIterator().hasNext());
		assertFalse(singleTableSubclassWrapper.getPropertyIterator().hasNext());
		assertFalse(joinedSubclassWrapper.getPropertyIterator().hasNext());
		assertFalse(specialRootClassWrapper.getPropertyIterator().hasNext());
		PropertyWrapper property = PropertyWrapperFactory.createPropertyWrapper();
		property.setName("prop1");
		rootClassWrapper.addProperty(property);
		assertTrue(rootClassWrapper.getPropertyIterator().hasNext());
		assertFalse(singleTableSubclassWrapper.getPropertyIterator().hasNext());
		singleTableSubclassWrapper.addProperty(property);
		assertTrue(singleTableSubclassWrapper.getPropertyIterator().hasNext());
		assertFalse(joinedSubclassWrapper.getPropertyIterator().hasNext());
		joinedSubclassWrapper.addProperty(property);
		assertTrue(joinedSubclassWrapper.getPropertyIterator().hasNext());
		assertFalse(specialRootClassWrapper.getPropertyIterator().hasNext());
		specialRootClassWrapper.addProperty(property);
		assertTrue(specialRootClassWrapper.getPropertyIterator().hasNext());
	}

	@Test
	public void testGetProperty() {
		try {
			rootClassWrapper.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertTrue(t.getMessage().contains("property [foo] not found on entity"));
		}
		try {
			singleTableSubclassWrapper.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertTrue(t.getMessage().contains("property [foo] not found on entity"));
		}
		try {
			joinedSubclassWrapper.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertTrue(t.getMessage().contains("property [foo] not found on entity"));
		}
		PropertyWrapper p = PropertyWrapperFactory.createPropertyWrapper();
		p.setName("foo");
		rootClassWrapper.addProperty(p);
		assertSame(p, rootClassWrapper.getProperty("foo"));
		assertSame(p, singleTableSubclassWrapper.getProperty("foo"));
		assertSame(p, joinedSubclassWrapper.getProperty("foo"));
		try {
			specialRootClassWrapper.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertTrue(t.getMessage().contains("property [foo] not found on entity"));
		}
		specialRootClassWrapper.addProperty(p);
		assertSame(p, specialRootClassWrapper.getProperty("foo"));
		try {
			rootClassWrapper.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals("getProperty() is only allowed on SpecialRootClass", t.getMessage());
		}
		assertSame(property, specialRootClassWrapper.getProperty());
	}

	@Test
	public void testGetTable() {
		assertNull(rootClassWrapper.getTable());
		assertNull(singleTableSubclassWrapper.getTable());
		assertNull(joinedSubclassWrapper.getTable());
		assertNull(specialRootClassWrapper.getTable());
		TableWrapper table = TableWrapperFactory.createTableWrapper("test");
		rootClassWrapper.setTable(table);
		assertSame(table, rootClassWrapper.getTable());
		assertSame(table, singleTableSubclassWrapper.getTable());
		assertNull(joinedSubclassWrapper.getTable());
		TableWrapper joinedTable = TableWrapperFactory.createTableWrapper("joined");
		joinedSubclassWrapper.setTable(joinedTable);
		assertSame(joinedTable, joinedSubclassWrapper.getTable());
		assertNull(specialRootClassWrapper.getTable());
		specialRootClassWrapper.setTable(table);
		assertSame(table, specialRootClassWrapper.getTable());
	}

	@Test
	public void testIsAbstract() {
		assertNull(rootClassWrapper.isAbstract());
		assertNull(singleTableSubclassWrapper.isAbstract());
		assertNull(joinedSubclassWrapper.isAbstract());
		assertNull(specialRootClassWrapper.isAbstract());
		rootClassWrapper.setAbstract(true);
		singleTableSubclassWrapper.setAbstract(true);
		joinedSubclassWrapper.setAbstract(true);
		specialRootClassWrapper.setAbstract(true);
		assertTrue(rootClassWrapper.isAbstract());
		assertTrue(singleTableSubclassWrapper.isAbstract());
		assertTrue(joinedSubclassWrapper.isAbstract());
		assertTrue(specialRootClassWrapper.isAbstract());
		rootClassWrapper.setAbstract(false);
		singleTableSubclassWrapper.setAbstract(false);
		joinedSubclassWrapper.setAbstract(false);
		specialRootClassWrapper.setAbstract(false);
		assertFalse(rootClassWrapper.isAbstract());
		assertFalse(singleTableSubclassWrapper.isAbstract());
		assertFalse(joinedSubclassWrapper.isAbstract());
		assertFalse(specialRootClassWrapper.isAbstract());
	}

	@Test
	public void testGetDiscriminator() {
		assertNull(rootClassWrapper.getDiscriminator());
		assertNull(singleTableSubclassWrapper.getDiscriminator());
		assertNull(joinedSubclassWrapper.getDiscriminator());
		assertNull(specialRootClassWrapper.getDiscriminator());
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		rootClassWrapper.setDiscriminator(value);
		assertSame(value, rootClassWrapper.getDiscriminator());
		assertSame(value, singleTableSubclassWrapper.getDiscriminator());
		assertSame(value, joinedSubclassWrapper.getDiscriminator());
		specialRootClassWrapper.setDiscriminator(value);
		assertSame(value, specialRootClassWrapper.getDiscriminator());
	}

	@Test
	public void testGetIdentifier() {
		assertNull(rootClassWrapper.getIdentifier());
		assertNull(singleTableSubclassWrapper.getIdentifier());
		assertNull(joinedSubclassWrapper.getIdentifier());
		assertNull(specialRootClassWrapper.getIdentifier());
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		rootClassWrapper.setIdentifier(value);
		assertSame(value, rootClassWrapper.getIdentifier());
		assertSame(value, singleTableSubclassWrapper.getIdentifier());
		assertSame(value, joinedSubclassWrapper.getIdentifier());
		specialRootClassWrapper.setIdentifier(value);
		assertSame(value, specialRootClassWrapper.getIdentifier());
	}

	@Test
	public void testGetJoinIterator() {
		assertFalse(rootClassWrapper.getJoinIterator().hasNext());
		assertFalse(singleTableSubclassWrapper.getJoinIterator().hasNext());
		assertFalse(joinedSubclassWrapper.getJoinIterator().hasNext());
		assertFalse(specialRootClassWrapper.getJoinIterator().hasNext());
		JoinWrapper join = new DummyJoinWrapper();
		((PersistentClassWrapperImpl) rootClassWrapper).addJoinWrapper(join);
		assertSame(join, rootClassWrapper.getJoinIterator().next());
		JoinWrapper join2 = new DummyJoinWrapper();
		((PersistentClassWrapperImpl) singleTableSubclassWrapper).addJoinWrapper(join2);
		assertSame(join2, singleTableSubclassWrapper.getJoinIterator().next());
		JoinWrapper join3 = new DummyJoinWrapper();
		((PersistentClassWrapperImpl) joinedSubclassWrapper).addJoinWrapper(join3);
		assertSame(join3, joinedSubclassWrapper.getJoinIterator().next());
		JoinWrapper join4 = new DummyJoinWrapper();
		((PersistentClassWrapperImpl) specialRootClassWrapper).addJoinWrapper(join4);
		assertSame(join4, specialRootClassWrapper.getJoinIterator().next());
	}

	@Test
	public void testGetVersion() {
		assertNull(rootClassWrapper.getVersion());
		assertNull(singleTableSubclassWrapper.getVersion());
		assertNull(joinedSubclassWrapper.getVersion());
		assertNull(specialRootClassWrapper.getVersion());
		PropertyWrapper versionProperty = PropertyWrapperFactory.createPropertyWrapper();
		((PersistentClassWrapperImpl) rootClassWrapper).setVersion(versionProperty);
		assertSame(versionProperty, rootClassWrapper.getVersion());
		assertSame(versionProperty, singleTableSubclassWrapper.getVersion());
		assertSame(versionProperty, joinedSubclassWrapper.getVersion());
		PropertyWrapper specialVersion = PropertyWrapperFactory.createPropertyWrapper();
		((PersistentClassWrapperImpl) specialRootClassWrapper).setVersion(specialVersion);
		assertSame(specialVersion, specialRootClassWrapper.getVersion());
	}

	@Test
	public void testSetClassName() {
		assertNull(rootClassWrapper.getClassName());
		rootClassWrapper.setClassName("foo");
		assertEquals("foo", rootClassWrapper.getClassName());
		singleTableSubclassWrapper.setClassName("bar");
		assertEquals("bar", singleTableSubclassWrapper.getClassName());
		joinedSubclassWrapper.setClassName("oof");
		assertEquals("oof", joinedSubclassWrapper.getClassName());
		specialRootClassWrapper.setClassName("rab");
		assertEquals("rab", specialRootClassWrapper.getClassName());
	}

	@Test
	public void testSetEntityName() {
		assertNull(rootClassWrapper.getEntityName());
		rootClassWrapper.setEntityName("foo");
		assertEquals("foo", rootClassWrapper.getEntityName());
		singleTableSubclassWrapper.setEntityName("bar");
		assertEquals("bar", singleTableSubclassWrapper.getEntityName());
		joinedSubclassWrapper.setEntityName("oof");
		assertEquals("oof", joinedSubclassWrapper.getEntityName());
		specialRootClassWrapper.setEntityName("rab");
		assertEquals("rab", specialRootClassWrapper.getEntityName());
	}

	@Test
	public void testSetDiscriminatorValue() {
		assertNull(rootClassWrapper.getDiscriminatorValue());
		rootClassWrapper.setDiscriminatorValue("foo");
		assertEquals("foo", rootClassWrapper.getDiscriminatorValue());
		singleTableSubclassWrapper.setDiscriminatorValue("bar");
		assertEquals("bar", singleTableSubclassWrapper.getDiscriminatorValue());
		joinedSubclassWrapper.setDiscriminatorValue("oof");
		assertEquals("oof", joinedSubclassWrapper.getDiscriminatorValue());
		specialRootClassWrapper.setDiscriminatorValue("rab");
		assertEquals("rab", specialRootClassWrapper.getDiscriminatorValue());
	}

	@Test
	public void testSetAbstract() {
		assertNull(rootClassWrapper.isAbstract());
		rootClassWrapper.setAbstract(true);
		assertTrue(rootClassWrapper.isAbstract());
		rootClassWrapper.setAbstract(false);
		assertFalse(rootClassWrapper.isAbstract());
	}

	@Test
	public void testAddProperty() {
		try {
			rootClassWrapper.getProperty("foo");
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("property [foo] not found on entity"));
		}
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		pw.setName("foo");
		rootClassWrapper.addProperty(pw);
		assertSame(pw, rootClassWrapper.getProperty("foo"));
		assertSame(pw, singleTableSubclassWrapper.getProperty("foo"));
		assertSame(pw, joinedSubclassWrapper.getProperty("foo"));
	}

	@Test
	public void testIsInstanceOfJoinedSubclass() {
		assertFalse(rootClassWrapper.isInstanceOfJoinedSubclass());
		assertFalse(singleTableSubclassWrapper.isInstanceOfJoinedSubclass());
		assertTrue(joinedSubclassWrapper.isInstanceOfJoinedSubclass());
		assertFalse(specialRootClassWrapper.isInstanceOfJoinedSubclass());
	}

	@Test
	public void testSetTable() {
		TableWrapper tableWrapper = TableWrapperFactory.createTableWrapper("");
		rootClassWrapper.setTable(tableWrapper);
		assertSame(tableWrapper, rootClassWrapper.getTable());
		assertSame(tableWrapper, singleTableSubclassWrapper.getTable());
		try {
			singleTableSubclassWrapper.setTable(tableWrapper);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setTable(Table)' is not supported.", e.getMessage());
		}
		joinedSubclassWrapper.setTable(tableWrapper);
		assertSame(tableWrapper, joinedSubclassWrapper.getTable());
		specialRootClassWrapper.setTable(tableWrapper);
		assertSame(tableWrapper, specialRootClassWrapper.getTable());
	}

	@Test
	public void testSetKey() {
		ValueWrapper valueWrapper = ValueWrapperFactory.createSimpleValueWrapper();
		try {
			rootClassWrapper.setKey(valueWrapper);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(Value) is only allowed on JoinedSubclass", e.getMessage());
		}
		try {
			singleTableSubclassWrapper.setKey(valueWrapper);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(Value) is only allowed on JoinedSubclass", e.getMessage());
		}
		joinedSubclassWrapper.setKey(valueWrapper);
		assertSame(valueWrapper, ((PersistentClassWrapperImpl) joinedSubclassWrapper).getKey());
		try {
			specialRootClassWrapper.setKey(valueWrapper);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(Value) is only allowed on JoinedSubclass", e.getMessage());
		}
	}

	@Test
	public void testIsInstanceOfSpecialRootClass() {
		assertFalse(rootClassWrapper.isInstanceOfSpecialRootClass());
		assertFalse(singleTableSubclassWrapper.isInstanceOfSpecialRootClass());
		assertFalse(joinedSubclassWrapper.isInstanceOfSpecialRootClass());
		assertTrue(specialRootClassWrapper.isInstanceOfSpecialRootClass());
	}

	@Test
	public void testGetParentProperty() {
		try {
			rootClassWrapper.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		try {
			singleTableSubclassWrapper.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		try {
			joinedSubclassWrapper.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		assertNull(specialRootClassWrapper.getParentProperty());
		PropertyWrapper parentProp = PropertyWrapperFactory.createPropertyWrapper();
		parentProp.setName("foo");
		((SpecialRootClassWrapperImpl) specialRootClassWrapper).setParentPropertyWrapper(parentProp);
		PropertyWrapper parentProperty = specialRootClassWrapper.getParentProperty();
		assertNotNull(parentProperty);
		assertEquals("foo", parentProperty.getName());
	}

	@Test
	public void testSetIdentifierProperty() {
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		rootClassWrapper.setIdentifierProperty(pw);
		assertSame(pw, rootClassWrapper.getIdentifierProperty());
		specialRootClassWrapper.setIdentifierProperty(pw);
		assertSame(pw, specialRootClassWrapper.getIdentifierProperty());
		try {
			singleTableSubclassWrapper.setIdentifierProperty(pw);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setIdentifierProperty(Property) is only allowed on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassWrapper.setIdentifierProperty(pw);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setIdentifierProperty(Property) is only allowed on RootClass instances", e.getMessage());
		}
	}

	@Test
	public void testSetIdentifier() {
		ValueWrapper vw = ValueWrapperFactory.createSimpleValueWrapper();
		rootClassWrapper.setIdentifier(vw);
		assertSame(vw, rootClassWrapper.getIdentifier());
		assertSame(vw, singleTableSubclassWrapper.getIdentifier());
		assertSame(vw, joinedSubclassWrapper.getIdentifier());
		try {
			singleTableSubclassWrapper.setIdentifier(vw);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setIdentifier(Value)' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassWrapper.setIdentifier(vw);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setIdentifier(Value)' can only be called on RootClass instances", e.getMessage());
		}
		specialRootClassWrapper.setIdentifier(vw);
		assertSame(vw, specialRootClassWrapper.getIdentifier());
	}

	@Test
	public void testSetDiscriminator() {
		ValueWrapper vw = ValueWrapperFactory.createSimpleValueWrapper();
		rootClassWrapper.setDiscriminator(vw);
		assertSame(vw, rootClassWrapper.getDiscriminator());
		assertSame(vw, singleTableSubclassWrapper.getDiscriminator());
		assertSame(vw, joinedSubclassWrapper.getDiscriminator());
		try {
			singleTableSubclassWrapper.setDiscriminator(vw);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setDiscriminator(Value)' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassWrapper.setDiscriminator(vw);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setDiscriminator(Value)' can only be called on RootClass instances", e.getMessage());
		}
		specialRootClassWrapper.setDiscriminator(vw);
		assertSame(vw, specialRootClassWrapper.getDiscriminator());
	}

	@Test
	public void testSetProxyInterfaceName() {
		assertNull(((PersistentClassWrapperImpl) rootClassWrapper).getProxyInterfaceName());
		rootClassWrapper.setProxyInterfaceName("foo");
		assertEquals("foo", ((PersistentClassWrapperImpl) rootClassWrapper).getProxyInterfaceName());
	}

	@Test
	public void testSetLazy() {
		rootClassWrapper.setLazy(true);
		assertTrue(rootClassWrapper.isLazy());
		rootClassWrapper.setLazy(false);
		assertFalse(rootClassWrapper.isLazy());
	}

	@Test
	public void testGetSubclassIterator() {
		PersistentClassWrapper freshRoot = PersistentClassWrapperFactory.createRootClassWrapper();
		assertFalse(freshRoot.getSubclassIterator().hasNext());
		PersistentClassWrapper sub = PersistentClassWrapperFactory.createSingleTableSubClassWrapper(freshRoot);
		Iterator<PersistentClassWrapper> iter = freshRoot.getSubclassIterator();
		assertTrue(iter.hasNext());
		assertSame(sub, iter.next());
	}

	@Test
	public void testIsCustomDeleteCallable() {
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLDelete("foo", false);
		assertFalse(rootClassWrapper.isCustomDeleteCallable());
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLDelete("bar", true);
		assertTrue(rootClassWrapper.isCustomDeleteCallable());
	}

	@Test
	public void testIsCustomInsertCallable() {
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLInsert("bar", false);
		assertFalse(rootClassWrapper.isCustomInsertCallable());
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLInsert("foo", true);
		assertTrue(rootClassWrapper.isCustomInsertCallable());
	}

	@Test
	public void testIsCustomUpdateCallable() {
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLUpdate("foo", false);
		assertFalse(rootClassWrapper.isCustomUpdateCallable());
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLUpdate("bar", true);
		assertTrue(rootClassWrapper.isCustomUpdateCallable());
	}

	@Test
	public void testIsDiscriminatorValueInsertable() {
		assertTrue(rootClassWrapper.isDiscriminatorInsertable());
		assertTrue(singleTableSubclassWrapper.isDiscriminatorInsertable());
		assertTrue(joinedSubclassWrapper.isDiscriminatorInsertable());
		((PersistentClassWrapperImpl) rootClassWrapper).setDiscriminatorInsertable(false);
		assertFalse(rootClassWrapper.isDiscriminatorInsertable());
		assertFalse(singleTableSubclassWrapper.isDiscriminatorInsertable());
		assertFalse(joinedSubclassWrapper.isDiscriminatorInsertable());
	}

	@Test
	public void testIsDiscriminatorValueNotNull() {
		rootClassWrapper.setDiscriminatorValue("null");
		assertFalse(rootClassWrapper.isDiscriminatorValueNotNull());
		rootClassWrapper.setDiscriminatorValue("not null");
		assertTrue(rootClassWrapper.isDiscriminatorValueNotNull());
	}

	@Test
	public void testIsDiscriminatorValueNull() {
		rootClassWrapper.setDiscriminatorValue("not null");
		assertFalse(rootClassWrapper.isDiscriminatorValueNull());
		rootClassWrapper.setDiscriminatorValue("null");
		assertTrue(rootClassWrapper.isDiscriminatorValueNull());
	}

	@Test
	public void testIsExplicitPolymorphism() {
		assertFalse(rootClassWrapper.isExplicitPolymorphism());
		assertFalse(singleTableSubclassWrapper.isExplicitPolymorphism());
		assertFalse(joinedSubclassWrapper.isExplicitPolymorphism());
		assertFalse(specialRootClassWrapper.isExplicitPolymorphism());
	}

	@Test
	public void testIsForceDiscriminator() {
		assertFalse(rootClassWrapper.isForceDiscriminator());
		((PersistentClassWrapperImpl) rootClassWrapper).setForceDiscriminator(true);
		assertTrue(rootClassWrapper.isForceDiscriminator());
		assertTrue(singleTableSubclassWrapper.isForceDiscriminator());
		assertTrue(joinedSubclassWrapper.isForceDiscriminator());
	}

	@Test
	public void testIsInherited() {
		assertFalse(rootClassWrapper.isInherited());
		assertTrue(singleTableSubclassWrapper.isInherited());
		assertTrue(joinedSubclassWrapper.isInherited());
		assertFalse(specialRootClassWrapper.isInherited());
	}

	@Test
	public void testIsJoinedSubclass() {
		assertFalse(rootClassWrapper.isJoinedSubclass());
		assertFalse(singleTableSubclassWrapper.isJoinedSubclass());
		assertTrue(joinedSubclassWrapper.isJoinedSubclass());
		assertFalse(specialRootClassWrapper.isJoinedSubclass());
	}

	@Test
	public void testIsLazy() {
		rootClassWrapper.setLazy(true);
		assertTrue(rootClassWrapper.isLazy());
		rootClassWrapper.setLazy(false);
		assertFalse(rootClassWrapper.isLazy());
	}

	@Test
	public void testIsLazyPropertiesCacheable() {
		assertTrue(rootClassWrapper.isLazyPropertiesCacheable());
		((PersistentClassWrapperImpl) rootClassWrapper).setLazyPropertiesCacheable(false);
		assertFalse(rootClassWrapper.isLazyPropertiesCacheable());
		try {
			singleTableSubclassWrapper.isLazyPropertiesCacheable();
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'isLazyPropertiesCacheable()' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassWrapper.isLazyPropertiesCacheable();
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'isLazyPropertiesCacheable()' can only be called on RootClass instances", e.getMessage());
		}
	}

	@Test
	public void testIsMutable() {
		assertTrue(rootClassWrapper.isMutable());
		((PersistentClassWrapperImpl) rootClassWrapper).setMutable(false);
		assertFalse(rootClassWrapper.isMutable());
		assertFalse(singleTableSubclassWrapper.isMutable());
		assertFalse(joinedSubclassWrapper.isMutable());
	}

	@Test
	public void testIsPolymorphic() {
		assertFalse(rootClassWrapper.isPolymorphic());
		assertTrue(singleTableSubclassWrapper.isPolymorphic());
		assertTrue(joinedSubclassWrapper.isPolymorphic());
		((PersistentClassWrapperImpl) rootClassWrapper).setPolymorphic(true);
		assertTrue(rootClassWrapper.isPolymorphic());
	}

	@Test
	public void testIsVersioned() {
		assertFalse(rootClassWrapper.isVersioned());
		assertFalse(singleTableSubclassWrapper.isVersioned());
		assertFalse(joinedSubclassWrapper.isVersioned());
		((PersistentClassWrapperImpl) rootClassWrapper).setVersion(PropertyWrapperFactory.createPropertyWrapper());
		assertTrue(rootClassWrapper.isVersioned());
		assertTrue(singleTableSubclassWrapper.isVersioned());
		assertTrue(joinedSubclassWrapper.isVersioned());
	}

	@Test
	public void testGetBatchSize() {
		((PersistentClassWrapperImpl) rootClassWrapper).setBatchSize(42);
		assertEquals(42, rootClassWrapper.getBatchSize());
	}

	@Test
	public void testGetCacheConcurrencyStrategy() {
		assertNull(rootClassWrapper.getCacheConcurrencyStrategy());
		((PersistentClassWrapperImpl) rootClassWrapper).setCacheConcurrencyStrategy("foo");
		assertEquals("foo", rootClassWrapper.getCacheConcurrencyStrategy());
		assertEquals("foo", singleTableSubclassWrapper.getCacheConcurrencyStrategy());
		assertEquals("foo", joinedSubclassWrapper.getCacheConcurrencyStrategy());
	}

	@Test
	public void testGetCustomSQLDelete() {
		assertNull(rootClassWrapper.getCustomSQLDelete());
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLDelete("foo", false);
		assertEquals("foo", rootClassWrapper.getCustomSQLDelete());
	}

	@Test
	public void testGetCustomSQLInsert() {
		assertNull(rootClassWrapper.getCustomSQLInsert());
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLInsert("foo", false);
		assertEquals("foo", rootClassWrapper.getCustomSQLInsert());
	}

	@Test
	public void testGetCustomSQLUpdate() {
		assertNull(rootClassWrapper.getCustomSQLUpdate());
		((PersistentClassWrapperImpl) rootClassWrapper).setCustomSQLUpdate("foo", false);
		assertEquals("foo", rootClassWrapper.getCustomSQLUpdate());
	}

	@Test
	public void testGetDiscriminatorValue() {
		assertNull(rootClassWrapper.getDiscriminatorValue());
		rootClassWrapper.setDiscriminatorValue("foo");
		assertEquals("foo", rootClassWrapper.getDiscriminatorValue());
	}

	@Test
	public void testGetLoaderName() {
		assertNull(rootClassWrapper.getLoaderName());
		((PersistentClassWrapperImpl) rootClassWrapper).setLoaderName("foo");
		assertEquals("foo", rootClassWrapper.getLoaderName());
	}

	@Test
	public void testGetOptimisticLockMode() {
		((PersistentClassWrapperImpl) rootClassWrapper).setOptimisticLockMode(-1);
		assertEquals(-1, rootClassWrapper.getOptimisticLockMode());
		assertEquals(-1, singleTableSubclassWrapper.getOptimisticLockMode());
		assertEquals(-1, joinedSubclassWrapper.getOptimisticLockMode());
	}

	@Test
	public void testGetWhere() {
		assertNull(rootClassWrapper.getWhere());
		((PersistentClassWrapperImpl) rootClassWrapper).setWhere("foo");
		assertEquals("foo", rootClassWrapper.getWhere());
		assertEquals("foo", singleTableSubclassWrapper.getWhere());
		assertEquals("foo", joinedSubclassWrapper.getWhere());
	}

	@Test
	public void testGetRootTable() {
		TableWrapper tw = TableWrapperFactory.createTableWrapper("");
		assertNull(rootClassWrapper.getRootTable());
		rootClassWrapper.setTable(tw);
		assertSame(tw, rootClassWrapper.getRootTable());
		assertSame(tw, singleTableSubclassWrapper.getRootTable());
		assertSame(tw, joinedSubclassWrapper.getRootTable());
	}

	/**
	 * Minimal JoinWrapper for testing — avoids dependency on the old
	 * JoinWrapperFactory which wraps org.hibernate.mapping.Join.
	 */
	private static class DummyJoinWrapper implements JoinWrapper {
		@Override
		public Iterator<PropertyWrapper> getPropertyIterator() {
			return java.util.Collections.emptyIterator();
		}
	}

}
