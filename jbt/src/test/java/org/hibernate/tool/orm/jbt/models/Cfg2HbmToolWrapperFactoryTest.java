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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.api.wrp.Cfg2HbmToolWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.models.PersistentClassWrapperFactory.PersistentClassWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Cfg2HbmToolWrapperFactoryTest {

	private Cfg2HbmToolWrapper cfg2HbmToolWrapper = null;

	@BeforeEach
	public void beforeEach() {
		cfg2HbmToolWrapper = Cfg2HbmToolWrapperFactory.createCfg2HbmToolWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(cfg2HbmToolWrapper);
	}

	// --- getTag(PersistentClassWrapper) ---

	@Test
	public void testGetTagForRootClass() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		assertEquals("class", cfg2HbmToolWrapper.getTag(pcw));
	}

	@Test
	public void testGetTagForSingleTableSubclass() {
		PersistentClassWrapper root = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClassWrapper sub = PersistentClassWrapperFactory.createSingleTableSubClassWrapper(root);
		assertEquals("subclass", cfg2HbmToolWrapper.getTag(sub));
	}

	@Test
	public void testGetTagForJoinedSubclass() {
		PersistentClassWrapper root = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClassWrapper sub = PersistentClassWrapperFactory.createJoinedTableSubClassWrapper(root);
		assertEquals("joined-subclass", cfg2HbmToolWrapper.getTag(sub));
	}

	// --- getTag(PropertyWrapper) ---

	@Test
	public void testGetTagForSimpleProperty() {
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		pw.setValue(value);
		assertEquals("property", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForManyToOne() {
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createManyToOneWrapper();
		pw.setValue(value);
		assertEquals("many-to-one", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForOneToOne() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createOneToOneWrapper(pcw);
		pw.setValue(value);
		assertEquals("one-to-one", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForOneToMany() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createOneToManyWrapper(pcw);
		pw.setValue(value);
		assertEquals("one-to-many", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForComponent() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createComponentWrapper(pcw);
		pw.setValue(value);
		assertEquals("component", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForComponentWithEmbeddedAccessor() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		pw.setPropertyAccessorName("embedded");
		ValueWrapper value = ValueWrapperFactory.createComponentWrapper(pcw);
		pw.setValue(value);
		assertEquals("properties", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForMap() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createMapWrapper(pcw);
		pw.setValue(value);
		assertEquals("map", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForSet() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createSetWrapper(pcw);
		pw.setValue(value);
		assertEquals("set", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForList() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createListWrapper(pcw);
		pw.setValue(value);
		assertEquals("list", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForBag() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createBagWrapper(pcw);
		pw.setValue(value);
		assertEquals("bag", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForIdentifierBag() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createIdentifierBagWrapper(pcw);
		pw.setValue(value);
		assertEquals("idbag", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForArray() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createArrayWrapper(pcw);
		pw.setValue(value);
		assertEquals("array", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForPrimitiveArray() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createPrimitiveArrayWrapper(pcw);
		pw.setValue(value);
		assertEquals("primitive-array", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForAny() {
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createAnyValueWrapper();
		pw.setValue(value);
		assertEquals("any", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForDependantValue() {
		TableWrapper tw = TableWrapperFactory.createTableWrapper("t");
		ValueWrapper wrappedValue = ValueWrapperFactory.createSimpleValueWrapper();
		PropertyWrapper pw = PropertyWrapperFactory.createPropertyWrapper();
		ValueWrapper value = ValueWrapperFactory.createDependantValueWrapper(tw, wrappedValue);
		pw.setValue(value);
		assertEquals("property", cfg2HbmToolWrapper.getTag(pw));
	}

	@Test
	public void testGetTagForVersionWithTimestamp() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper versionProp = PropertyWrapperFactory.createPropertyWrapper();
		versionProp.setName("version");
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		value.setTypeName("timestamp");
		versionProp.setValue(value);
		versionProp.setPersistentClass(pcw);
		((PersistentClassWrapperImpl) pcw).setVersion(versionProp);
		assertEquals("timestamp", cfg2HbmToolWrapper.getTag(versionProp));
	}

	@Test
	public void testGetTagForVersionWithDbTimestamp() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper versionProp = PropertyWrapperFactory.createPropertyWrapper();
		versionProp.setName("version");
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		value.setTypeName("dbtimestamp");
		versionProp.setValue(value);
		versionProp.setPersistentClass(pcw);
		((PersistentClassWrapperImpl) pcw).setVersion(versionProp);
		assertEquals("timestamp", cfg2HbmToolWrapper.getTag(versionProp));
	}

	@Test
	public void testGetTagForVersionWithInteger() {
		PersistentClassWrapper pcw = PersistentClassWrapperFactory.createRootClassWrapper();
		PropertyWrapper versionProp = PropertyWrapperFactory.createPropertyWrapper();
		versionProp.setName("version");
		ValueWrapper value = ValueWrapperFactory.createSimpleValueWrapper();
		value.setTypeName("integer");
		versionProp.setValue(value);
		versionProp.setPersistentClass(pcw);
		((PersistentClassWrapperImpl) pcw).setVersion(versionProp);
		assertEquals("version", cfg2HbmToolWrapper.getTag(versionProp));
	}

}
