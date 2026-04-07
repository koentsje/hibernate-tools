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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.hibernate.tool.orm.jbt.api.wrp.JoinWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.JoinWrapperFactory.JoinWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinWrapperFactoryTest {

	private JoinWrapper joinWrapper = null;

	@BeforeEach
	public void beforeEach() {
		joinWrapper = JoinWrapperFactory.createJoinWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(joinWrapper);
	}

	@Test
	public void testGetPropertyIteratorEmpty() {
		Iterator<PropertyWrapper> iter = joinWrapper.getPropertyIterator();
		assertFalse(iter.hasNext());
	}

	@Test
	public void testAddPropertyAndGetPropertyIterator() {
		PropertyWrapper prop = PropertyWrapperFactory.createPropertyWrapper();
		prop.setName("foo");
		((JoinWrapperImpl) joinWrapper).addProperty(prop);
		Iterator<PropertyWrapper> iter = joinWrapper.getPropertyIterator();
		assertTrue(iter.hasNext());
		assertSame(prop, iter.next());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testMultipleProperties() {
		PropertyWrapper p1 = PropertyWrapperFactory.createPropertyWrapper();
		PropertyWrapper p2 = PropertyWrapperFactory.createPropertyWrapper();
		((JoinWrapperImpl) joinWrapper).addProperty(p1);
		((JoinWrapperImpl) joinWrapper).addProperty(p2);
		Iterator<PropertyWrapper> iter = joinWrapper.getPropertyIterator();
		assertSame(p1, iter.next());
		assertSame(p2, iter.next());
		assertFalse(iter.hasNext());
	}

}
