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

import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.models.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.models.PropertyWrapperFactory.PropertyWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyWrapperFactoryTest {

	private PropertyWrapper propertyWrapper = null;

	@BeforeEach
	public void beforeEach() {
		propertyWrapper = PropertyWrapperFactory.createPropertyWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(propertyWrapper);
	}

	@Test
	public void testGetSetName() {
		assertNull(propertyWrapper.getName());
		propertyWrapper.setName("foo");
		assertEquals("foo", propertyWrapper.getName());
	}

	@Test
	public void testGetSetValue() {
		assertNull(propertyWrapper.getValue());
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		propertyWrapper.setValue(value);
		assertSame(value, propertyWrapper.getValue());
	}

	@Test
	public void testGetSetPersistentClass() {
		assertNull(propertyWrapper.getPersistentClass());
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		propertyWrapper.setPersistentClass(pcw);
		assertSame(pcw, propertyWrapper.getPersistentClass());
	}

	@Test
	public void testIsComposite() {
		assertFalse(propertyWrapper.isComposite());
		ValueWrapper simpleValue = ValueWrapperFactory.createSimpleValueWrapper();
		propertyWrapper.setValue(simpleValue);
		assertFalse(propertyWrapper.isComposite());
		// Component values report isComponent() = true
		PersistentClassWrapper pcw =
				org.hibernate.tool.orm.jbt.models.PersistentClassWrapperFactory.createRootClassWrapper();
		ValueWrapper componentValue = ValueWrapperFactory.createComponentWrapper(pcw);
		propertyWrapper.setValue(componentValue);
		assertTrue(propertyWrapper.isComposite());
	}

	@Test
	public void testGetSetPropertyAccessorName() {
		assertNull(propertyWrapper.getPropertyAccessorName());
		propertyWrapper.setPropertyAccessorName("foo");
		assertEquals("foo", propertyWrapper.getPropertyAccessorName());
	}

	@Test
	public void testGetType() {
		assertNull(propertyWrapper.getType());
	}

	@Test
	public void testGetSetCascade() {
		assertEquals("none", propertyWrapper.getCascade());
		propertyWrapper.setCascade("all");
		assertEquals("all", propertyWrapper.getCascade());
	}

	@Test
	public void testIsBackRef() {
		assertFalse(propertyWrapper.isBackRef());
		((PropertyWrapperImpl) propertyWrapper).setBackRef(true);
		assertTrue(propertyWrapper.isBackRef());
	}

	@Test
	public void testIsSelectable() {
		assertTrue(propertyWrapper.isSelectable());
		((PropertyWrapperImpl) propertyWrapper).setSelectable(false);
		assertFalse(propertyWrapper.isSelectable());
	}

	@Test
	public void testIsInsertable() {
		assertTrue(propertyWrapper.isInsertable());
		((PropertyWrapperImpl) propertyWrapper).setInsertable(false);
		assertFalse(propertyWrapper.isInsertable());
	}

	@Test
	public void testIsUpdateable() {
		assertTrue(propertyWrapper.isUpdateable());
		((PropertyWrapperImpl) propertyWrapper).setUpdateable(false);
		assertFalse(propertyWrapper.isUpdateable());
	}

	@Test
	public void testIsLazy() {
		assertFalse(propertyWrapper.isLazy());
		((PropertyWrapperImpl) propertyWrapper).setLazy(true);
		assertTrue(propertyWrapper.isLazy());
	}

	@Test
	public void testIsOptional() {
		assertFalse(propertyWrapper.isOptional());
		((PropertyWrapperImpl) propertyWrapper).setOptional(true);
		assertTrue(propertyWrapper.isOptional());
	}

	@Test
	public void testIsNaturalIdentifier() {
		assertFalse(propertyWrapper.isNaturalIdentifier());
		((PropertyWrapperImpl) propertyWrapper).setNaturalIdentifier(true);
		assertTrue(propertyWrapper.isNaturalIdentifier());
	}

	@Test
	public void testIsOptimisticLocked() {
		assertTrue(propertyWrapper.isOptimisticLocked());
		((PropertyWrapperImpl) propertyWrapper).setOptimisticLocked(false);
		assertFalse(propertyWrapper.isOptimisticLocked());
	}

}
