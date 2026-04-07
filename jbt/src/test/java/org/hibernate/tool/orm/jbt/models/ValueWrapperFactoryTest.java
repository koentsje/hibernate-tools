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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.models.ValueWrapperFactory.ValueKind;
import org.hibernate.tool.orm.jbt.models.ValueWrapperFactory.ValueWrapperImpl;
import org.junit.jupiter.api.Test;

public class ValueWrapperFactoryTest {

	// --- Factory method tests ---

	@Test
	public void testCreateSimpleValueWrapper() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNotNull(v);
		assertTrue(v.isSimpleValue());
		assertFalse(v.isCollection());
		assertEquals(ValueKind.SIMPLE_VALUE, ((ValueWrapperImpl) v).getKind());
	}

	@Test
	public void testCreateComponentWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createComponentWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isComponent());
		assertTrue(v.isSimpleValue());
		assertSame(pcw, v.getOwner());
	}

	@Test
	public void testCreateOneToManyWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createOneToManyWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isOneToMany());
		assertFalse(v.isSimpleValue());
		assertFalse(v.isCollection());
	}

	@Test
	public void testCreateManyToOneWrapper() {
		ValueWrapper v = ValueWrapperFactory.createManyToOneWrapper();
		assertNotNull(v);
		assertTrue(v.isManyToOne());
		assertTrue(v.isToOne());
		assertTrue(v.isSimpleValue());
	}

	@Test
	public void testCreateOneToOneWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createOneToOneWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isOneToOne());
		assertTrue(v.isToOne());
		assertTrue(v.isSimpleValue());
	}

	@Test
	public void testCreateMapWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createMapWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isMap());
		assertTrue(v.isCollection());
		assertFalse(v.isSimpleValue());
		assertSame(pcw, v.getOwner());
	}

	@Test
	public void testCreateSetWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createSetWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isSet());
		assertTrue(v.isCollection());
	}

	@Test
	public void testCreateListWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createListWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isList());
		assertTrue(v.isCollection());
	}

	@Test
	public void testCreateBagWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createBagWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isBag());
		assertTrue(v.isCollection());
	}

	@Test
	public void testCreateIdentifierBagWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createIdentifierBagWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isIdentifierBag());
		assertTrue(v.isCollection());
	}

	@Test
	public void testCreateArrayWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createArrayWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isArray());
		assertFalse(v.isPrimitiveArray());
		assertTrue(v.isCollection());
	}

	@Test
	public void testCreatePrimitiveArrayWrapper() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createPrimitiveArrayWrapper(pcw);
		assertNotNull(v);
		assertTrue(v.isPrimitiveArray());
		assertTrue(v.isArray());
		assertTrue(v.isCollection());
	}

	@Test
	public void testCreateDependantValueWrapper() {
		TableWrapper tw = TableWrapperFactory.createTableWrapper("t");
		ValueWrapper wrappedValue = ValueWrapperFactory.createSimpleValueWrapper();
		ValueWrapper v = ValueWrapperFactory.createDependantValueWrapper(tw, wrappedValue);
		assertNotNull(v);
		assertTrue(v.isDependantValue());
		assertTrue(v.isSimpleValue());
		assertSame(tw, v.getTable());
	}

	@Test
	public void testCreateAnyValueWrapper() {
		ValueWrapper v = ValueWrapperFactory.createAnyValueWrapper();
		assertNotNull(v);
		assertTrue(v.isAny());
		assertTrue(v.isSimpleValue());
	}

	// --- Behavior tests ---

	@Test
	public void testGetType() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNull(v.getType());
	}

	@Test
	public void testSetElementAndGetCollectionElement() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertNull(set.getCollectionElement());
		ValueWrapper elem = ValueWrapperFactory.createSimpleValueWrapper();
		set.setElement(elem);
		assertSame(elem, set.getCollectionElement());
	}

	@Test
	public void testGetCollectionElementOnNonCollection() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNull(v.getCollectionElement());
	}

	@Test
	public void testSetAndGetCollectionTable() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertNull(set.getCollectionTable());
		TableWrapper tw = TableWrapperFactory.createTableWrapper("ct");
		set.setCollectionTable(tw);
		assertSame(tw, set.getCollectionTable());
	}

	@Test
	public void testGetCollectionTableOnNonCollection() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNull(v.getCollectionTable());
	}

	@Test
	public void testSetAndGetTable() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNull(v.getTable());
		TableWrapper tw = TableWrapperFactory.createTableWrapper("t");
		v.setTable(tw);
		assertSame(tw, v.getTable());
	}

	@Test
	public void testSetAndGetIndex() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper list = ValueWrapperFactory.createListWrapper(pcw);
		assertNull(list.getIndex());
		ValueWrapper idx = ValueWrapperFactory.createSimpleValueWrapper();
		list.setIndex(idx);
		assertSame(idx, list.getIndex());
	}

	@Test
	public void testGetIndexOnNonIndexed() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertNull(set.getIndex());
	}

	@Test
	public void testSetAndGetTypeName() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNull(v.getTypeName());
		v.setTypeName("string");
		assertEquals("string", v.getTypeName());
	}

	@Test
	public void testGetComponentClassName() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper comp = ValueWrapperFactory.createComponentWrapper(pcw);
		assertNull(comp.getComponentClassName());
		((ValueWrapperImpl) comp).setComponentClassName("com.example.Foo");
		assertEquals("com.example.Foo", comp.getComponentClassName());
	}

	@Test
	public void testGetComponentClassNameOnNonComponent() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNull(v.getComponentClassName());
	}

	@Test
	public void testColumnIterator() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		Iterator<ColumnWrapper> iter = v.getColumnIterator();
		assertFalse(iter.hasNext());
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("c1");
		v.addColumn(col);
		iter = v.getColumnIterator();
		assertTrue(iter.hasNext());
		assertSame(col, iter.next());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testIsTypeSpecified() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertFalse(v.isTypeSpecified());
		v.setTypeName("string");
		assertTrue(v.isTypeSpecified());
	}

	@Test
	public void testIsTypeSpecifiedOnNonSimpleValue() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper otm = ValueWrapperFactory.createOneToManyWrapper(pcw);
		assertThrows(UnsupportedOperationException.class, () -> otm.isTypeSpecified());
	}

	@Test
	public void testSetAndGetKey() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertNull(set.getKey());
		ValueWrapper key = ValueWrapperFactory.createSimpleValueWrapper();
		set.setKey(key);
		assertSame(key, set.getKey());
	}

	@Test
	public void testGetKeyOnNonCollection() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.getKey());
	}

	@Test
	public void testSetKeyOnNonCollection() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		ValueWrapper key = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.setKey(key));
	}

	@Test
	public void testGetElementClassName() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper arr = ValueWrapperFactory.createArrayWrapper(pcw);
		assertNull(arr.getElementClassName());
		arr.setElementClassName("java.lang.String");
		assertEquals("java.lang.String", arr.getElementClassName());
	}

	@Test
	public void testGetElementClassNameOnNonArray() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.getElementClassName());
	}

	@Test
	public void testSetElementClassNameOnNonArray() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.setElementClassName("foo"));
	}

	@Test
	public void testGetReferencedEntityName() {
		ValueWrapper mto = ValueWrapperFactory.createManyToOneWrapper();
		mto.setReferencedEntityName("com.example.Foo");
		assertEquals("com.example.Foo", mto.getReferencedEntityName());
	}

	@Test
	public void testGetReferencedEntityNameOnUnsupported() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.getReferencedEntityName());
	}

	@Test
	public void testGetEntityName() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper oto = ValueWrapperFactory.createOneToOneWrapper(pcw);
		((ValueWrapperImpl) oto).setEntityName("com.example.Bar");
		assertEquals("com.example.Bar", oto.getEntityName());
	}

	@Test
	public void testGetEntityNameOnUnsupported() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.getEntityName());
	}

	@Test
	public void testPropertyIterator() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper comp = ValueWrapperFactory.createComponentWrapper(pcw);
		Iterator<PropertyWrapper> iter = comp.getPropertyIterator();
		assertFalse(iter.hasNext());
		PropertyWrapper prop = PropertyWrapperFactory.createPropertyWrapper();
		((ValueWrapperImpl) comp).addProperty(prop);
		iter = comp.getPropertyIterator();
		assertTrue(iter.hasNext());
		assertSame(prop, iter.next());
	}

	@Test
	public void testPropertyIteratorOnNonComponent() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.getPropertyIterator());
	}

	@Test
	public void testAddColumnOnNonSimpleValue() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper otm = ValueWrapperFactory.createOneToManyWrapper(pcw);
		ColumnWrapper col = ColumnWrapperFactory.createColumnWrapper("c");
		assertThrows(UnsupportedOperationException.class, () -> otm.addColumn(col));
	}

	@Test
	public void testSetTypeParameters() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		Properties props = new Properties();
		props.setProperty("key", "value");
		v.setTypeParameters(props);
		assertSame(props, ((ValueWrapperImpl) v).getTypeParameters());
	}

	@Test
	public void testSetTypeParametersOnCollection() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		Properties props = new Properties();
		set.setTypeParameters(props);
		assertSame(props, ((ValueWrapperImpl) set).getTypeParameters());
	}

	@Test
	public void testGetForeignKeyName() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNull(v.getForeignKeyName());
		((ValueWrapperImpl) v).setForeignKeyName("FK_FOO");
		assertEquals("FK_FOO", v.getForeignKeyName());
	}

	@Test
	public void testGetForeignKeyNameOnNonSimpleValue() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper otm = ValueWrapperFactory.createOneToManyWrapper(pcw);
		assertThrows(UnsupportedOperationException.class, () -> otm.getForeignKeyName());
	}

	@Test
	public void testGetOwnerOnCollection() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertSame(pcw, set.getOwner());
	}

	@Test
	public void testGetOwnerOnComponent() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper comp = ValueWrapperFactory.createComponentWrapper(pcw);
		assertSame(pcw, comp.getOwner());
	}

	@Test
	public void testGetOwnerOnUnsupported() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.getOwner());
	}

	@Test
	public void testGetElement() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertNull(set.getElement());
		ValueWrapper elem = ValueWrapperFactory.createSimpleValueWrapper();
		set.setElement(elem);
		assertSame(elem, set.getElement());
	}

	@Test
	public void testGetElementOnNonCollection() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertNull(v.getElement());
	}

	@Test
	public void testGetParentProperty() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper comp = ValueWrapperFactory.createComponentWrapper(pcw);
		assertNull(comp.getParentProperty());
		((ValueWrapperImpl) comp).setParentProperty("parent");
		assertEquals("parent", comp.getParentProperty());
	}

	@Test
	public void testGetParentPropertyOnNonComponent() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.getParentProperty());
	}

	@Test
	public void testSetFetchModeJoin() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertFalse(((ValueWrapperImpl) set).isFetchModeJoin());
		set.setFetchModeJoin();
		assertTrue(((ValueWrapperImpl) set).isFetchModeJoin());
	}

	@Test
	public void testSetFetchModeJoinOnToOne() {
		ValueWrapper mto = ValueWrapperFactory.createManyToOneWrapper();
		mto.setFetchModeJoin();
		assertTrue(((ValueWrapperImpl) mto).isFetchModeJoin());
	}

	@Test
	public void testSetFetchModeJoinOnUnsupported() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.setFetchModeJoin());
	}

	@Test
	public void testIsInverse() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertFalse(set.isInverse());
		((ValueWrapperImpl) set).setInverse(true);
		assertTrue(set.isInverse());
	}

	@Test
	public void testIsInverseOnNonCollection() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.isInverse());
	}

	@Test
	public void testSetAndGetAssociatedClass() {
		PersistentClassWrapper owner = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper otm = ValueWrapperFactory.createOneToManyWrapper(owner);
		assertNull(otm.getAssociatedClass());
		PersistentClassWrapper assoc = PersistentClassWrapperFactory.createRootClassWrapper();
		otm.setAssociatedClass(assoc);
		assertSame(assoc, otm.getAssociatedClass());
	}

	@Test
	public void testGetAssociatedClassOnUnsupported() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.getAssociatedClass());
	}

	@Test
	public void testSetAssociatedClassOnUnsupported() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.setAssociatedClass(pcw));
	}

	@Test
	public void testSetLazy() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertFalse(((ValueWrapperImpl) set).isLazy());
		set.setLazy(true);
		assertTrue(((ValueWrapperImpl) set).isLazy());
	}

	@Test
	public void testSetLazyOnAny() {
		ValueWrapper any = ValueWrapperFactory.createAnyValueWrapper();
		any.setLazy(true);
		assertTrue(((ValueWrapperImpl) any).isLazy());
	}

	@Test
	public void testSetLazyOnUnsupported() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.setLazy(true));
	}

	@Test
	public void testSetAndGetRole() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper set = ValueWrapperFactory.createSetWrapper(pcw);
		assertNull(((ValueWrapperImpl) set).getRole());
		set.setRole("com.example.Foo.bars");
		assertEquals("com.example.Foo.bars", ((ValueWrapperImpl) set).getRole());
	}

	@Test
	public void testSetRoleOnNonCollection() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class, () -> v.setRole("foo"));
	}

	@Test
	public void testSetReferencedEntityName() {
		ValueWrapper mto = ValueWrapperFactory.createManyToOneWrapper();
		mto.setReferencedEntityName("com.example.Bar");
		assertEquals("com.example.Bar", mto.getReferencedEntityName());
	}

	@Test
	public void testSetReferencedEntityNameOnOneToMany() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper otm = ValueWrapperFactory.createOneToManyWrapper(pcw);
		otm.setReferencedEntityName("com.example.Bar");
		assertEquals("com.example.Bar", otm.getReferencedEntityName());
	}

	@Test
	public void testSetReferencedEntityNameOnUnsupported() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertThrows(UnsupportedOperationException.class,
				() -> v.setReferencedEntityName("foo"));
	}

	@Test
	public void testIsEmbedded() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper comp = ValueWrapperFactory.createComponentWrapper(pcw);
		assertFalse(comp.isEmbedded());
		((ValueWrapperImpl) comp).setEmbedded(true);
		assertTrue(comp.isEmbedded());
	}

	@Test
	public void testIsEmbeddedOnNonComponent() {
		ValueWrapper v = ValueWrapperFactory.createSimpleValueWrapper();
		assertFalse(v.isEmbedded());
	}

}
