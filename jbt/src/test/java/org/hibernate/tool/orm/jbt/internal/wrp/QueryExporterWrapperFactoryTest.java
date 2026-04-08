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
package org.hibernate.tool.orm.jbt.internal.wrp;

import org.hibernate.tool.orm.jbt.api.wrp.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.List;

import org.hibernate.tool.orm.jbt.internal.wrp.QueryExporterWrapperFactory.QueryExporterWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryExporterWrapperFactoryTest {

	private QueryExporterWrapper queryExporterWrapper = null;

	@BeforeEach
	public void beforeEach() {
		queryExporterWrapper = QueryExporterWrapperFactory.createQueryExporterWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(queryExporterWrapper);
		assertNotNull(queryExporterWrapper.getWrappedObject());
	}

	@Test
	public void testSetQueries() {
		List<String> queries = Collections.emptyList();
		QueryExporterWrapperImpl impl = (QueryExporterWrapperImpl) queryExporterWrapper;
		assertNull(impl.getQueries());
		queryExporterWrapper.setQueries(queries);
		assertSame(queries, impl.getQueries());
	}

	@Test
	public void testSetFileName() {
		QueryExporterWrapperImpl impl = (QueryExporterWrapperImpl) queryExporterWrapper;
		assertNull(impl.getFilename());
		queryExporterWrapper.setFilename("foo");
		assertEquals("foo", impl.getFilename());
	}

}
