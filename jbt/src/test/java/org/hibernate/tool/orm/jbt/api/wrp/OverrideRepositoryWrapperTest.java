/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
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
package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.internal.factory.OverrideRepositoryWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.RevengStrategyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.TableFilterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverrideRepositoryWrapperTest {

	private static final String HIBERNATE_REVERSE_ENGINEERING_XML =
			"<?xml version='1.0' encoding='UTF-8'?>                                 "+
			"<!DOCTYPE hibernate-reverse-engineering PUBLIC                         "+
			"      '-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN'         "+
			"      'http://hibernate.org/dtd/hibernate-reverse-engineering-3.0.dtd'>"+
			"<hibernate-reverse-engineering>                                        "+
			"    <table name='FOO'/>                                                "+
			"</hibernate-reverse-engineering>                                       ";

	private OverrideRepositoryWrapper overrideRepositoryWrapper = null;
	private OverrideRepository wrappedOverrideRepository = null;
	
	@BeforeEach
	public void beforeEach() {
		overrideRepositoryWrapper = OverrideRepositoryWrapperFactory.createOverrideRepositoryWrapper();
		wrappedOverrideRepository = (OverrideRepository)overrideRepositoryWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedOverrideRepository);
		assertNotNull(overrideRepositoryWrapper);
	}
	
	@Test
	public void testAddFile() throws Exception {
		File file = File.createTempFile("addFile", "tst");
		file.deleteOnExit();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(HIBERNATE_REVERSE_ENGINEERING_XML);
		fileWriter.close();
		Field tablesField = wrappedOverrideRepository.getClass().getDeclaredField("tables");
		tablesField.setAccessible(true);
		Object object = tablesField.get(wrappedOverrideRepository);
		List<?> tables = (List<?>)object;
		assertNotNull(tables);
		assertTrue(tables.isEmpty());
		overrideRepositoryWrapper.addFile(file);
		object = tablesField.get(wrappedOverrideRepository);
		tables = (List<?>)object;
		assertNotNull(tables);
		assertFalse(tables.isEmpty());
		Table table = (Table)tables.get(0);
		assertNotNull(table);
		assertEquals("FOO", table.getName());
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() throws Exception {
		RevengStrategyWrapper revWrapper = RevengStrategyWrapperFactory.createRevengStrategyWrapper();
		RevengStrategy rev = (RevengStrategy)revWrapper.getWrappedObject();
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		RevengStrategyWrapper delegatingStrategy = overrideRepositoryWrapper.getReverseEngineeringStrategy(revWrapper);
		assertNotNull(delegatingStrategy);
		assertSame(rev, delegateField.get(delegatingStrategy.getWrappedObject()));
	}
	
	@Test
	public void testAddTableFilter() throws Exception {
		TableFilterWrapper tableFilterWrapper = TableFilterWrapperFactory.createTableFilterWrapper();
		TableFilter tableFilter = (TableFilter)tableFilterWrapper.getWrappedObject();
		Field tableFiltersField = OverrideRepository.class.getDeclaredField("tableFilters");
		tableFiltersField.setAccessible(true);
		List<?> tableFilters = (List<?>)tableFiltersField.get(wrappedOverrideRepository);
		assertTrue(tableFilters.isEmpty());
		overrideRepositoryWrapper.addTableFilter(tableFilterWrapper);
		tableFilters = (List<?>)tableFiltersField.get(wrappedOverrideRepository);
		assertSame(tableFilter, tableFilters.get(0));		
	}
	
}
