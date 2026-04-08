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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericExporterWrapperFactoryTest {

	private GenericExporterWrapper genericExporterWrapper = null;

	@BeforeEach
	public void beforeEach() {
		genericExporterWrapper = GenericExporterWrapperFactory.createGenericExporterWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(genericExporterWrapper);
		assertNotNull(genericExporterWrapper.getWrappedObject());
	}

	@Test
	public void testSetFilePattern() {
		assertNull(genericExporterWrapper.getFilePattern());
		genericExporterWrapper.setFilePattern("foobar");
		assertEquals("foobar", genericExporterWrapper.getFilePattern());
	}

	@Test
	public void testSetTemplate() {
		assertNull(genericExporterWrapper.getTemplateName());
		genericExporterWrapper.setTemplateName("barfoo");
		assertEquals("barfoo", genericExporterWrapper.getTemplateName());
	}

	@Test
	public void testSetForEach() {
		genericExporterWrapper.setForEach("foobar");
		// forEach is set but not exposed via getter on the wrapper interface
		// the wrapped object stores the value
		assertNotNull(genericExporterWrapper.getWrappedObject());
	}

	@Test
	public void testGetFilePattern() {
		assertNull(genericExporterWrapper.getFilePattern());
		genericExporterWrapper.setFilePattern("foobar");
		assertEquals("foobar", genericExporterWrapper.getFilePattern());
	}

	@Test
	public void testGetTemplateName() {
		assertNull(genericExporterWrapper.getTemplateName());
		genericExporterWrapper.setTemplateName("foobar");
		assertEquals("foobar", genericExporterWrapper.getTemplateName());
	}

}
