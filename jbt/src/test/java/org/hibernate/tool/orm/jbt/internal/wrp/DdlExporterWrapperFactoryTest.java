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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.hibernate.tool.orm.jbt.internal.wrp.DdlExporterWrapperFactory.DdlExporterWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DdlExporterWrapperFactoryTest {

	private DdlExporterWrapper ddlExporterWrapper = null;

	@BeforeEach
	public void beforeEach() {
		ddlExporterWrapper = DdlExporterWrapperFactory.createDdlExporterWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(ddlExporterWrapper);
		assertNotNull(ddlExporterWrapper.getWrappedObject());
	}

	@Test
	public void testSetExport() {
		DdlExporterWrapperImpl impl = (DdlExporterWrapperImpl) ddlExporterWrapper;
		assertFalse(impl.isExportToDatabase());
		ddlExporterWrapper.setExport(true);
		assertTrue(impl.isExportToDatabase());
		ddlExporterWrapper.setExport(false);
		assertFalse(impl.isExportToDatabase());
	}

	@Test
	public void testGetProperties() {
		Properties properties = ddlExporterWrapper.getProperties();
		assertNotNull(properties);
	}

}
