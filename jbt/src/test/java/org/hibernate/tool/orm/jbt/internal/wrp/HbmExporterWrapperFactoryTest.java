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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.tool.orm.jbt.internal.wrp.HbmExporterWrapperFactory.HbmExporterWrapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HbmExporterWrapperFactoryTest {

	private HbmExporterWrapper hbmExporterWrapper = null;
	private HbmExporterWrapperImpl wrappedImpl = null;

	private ConfigurationWrapper cfg = null;
	private File f = null;

	private boolean delegateHasExported = false;

	@TempDir private File tempFolder;

	@BeforeEach
	public void beforeEach() {
		cfg = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		f = new File(tempFolder, "foo");
		hbmExporterWrapper = HbmExporterWrapperFactory.createHbmExporterWrapper(cfg, f);
		wrappedImpl = (HbmExporterWrapperImpl) hbmExporterWrapper;
	}

	@Test
	public void testConstruction() {
		assertNotNull(hbmExporterWrapper);
		assertNotNull(hbmExporterWrapper.getWrappedObject());
		assertSame(f, wrappedImpl.getOutputFile());
		assertSame(cfg, wrappedImpl.getConfigurationWrapper());
	}

	@Test
	public void testGetOutputDirectory() {
		assertNull(hbmExporterWrapper.getOutputDirectory());
		File file = new File("testGetOutputDirectory");
		wrappedImpl.setOutputDirectory(file);
		assertSame(file, hbmExporterWrapper.getOutputDirectory());
	}

	@Test
	public void testSetOutputDirectory() {
		assertNull(wrappedImpl.getOutputDirectory());
		File file = new File("testSetOutputDirectory");
		hbmExporterWrapper.setOutputDirectory(file);
		assertSame(file, wrappedImpl.getOutputDirectory());
	}

	@Test
	public void testExportPOJOWithDelegate() {
		Map<Object, Object> context = new HashMap<>();
		Object pojoClass = "testPojo";
		Object delegate = new Object() {
			@SuppressWarnings("unused")
			private void exportPojo(Map<Object, Object> map, Object object, String string) {
				assertSame(map, context);
				assertSame(object, pojoClass);
				delegateHasExported = true;
			}
		};
		hbmExporterWrapper.setExportPOJODelegate(delegate);
		assertFalse(delegateHasExported);
		hbmExporterWrapper.exportPOJO(context, pojoClass);
		assertTrue(delegateHasExported);
	}

	@Test
	public void testExportPOJOWithoutDelegate() {
		Map<Object, Object> context = new HashMap<>();
		// without delegate, exportPOJO should be a no-op (no exception)
		hbmExporterWrapper.exportPOJO(context, "testPojo");
	}

	@Test
	public void testSetExportPOJODelegate() {
		assertNull(wrappedImpl.getDelegateExporter());
		Object delegate = new Object() {
			@SuppressWarnings("unused")
			public void exportPojo(Map<Object, Object> map, Object pojoClass, String qualifiedDeclarationName) { }
		};
		hbmExporterWrapper.setExportPOJODelegate(delegate);
		assertSame(delegate, wrappedImpl.getDelegateExporter());
	}

}
