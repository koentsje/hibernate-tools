/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2022-2025 Red Hat, Inc.
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
package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Properties;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HibernateToolsPersistenceProviderTest {
	
	private static final String PERSISTENCE_XML = 
			"<persistence version='2.2'" +
	        "  xmlns='http://xmlns.jcp.org/xml/ns/persistence'" +
		    "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
	        "  xsi:schemaLocation='http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd'>" +
	        "  <persistence-unit name='foobar'/>" +
			"</persistence>";
	
	private ClassLoader original = null;
	
	@TempDir
	public File tempRoot;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		tempRoot = Files.createTempDirectory("temp").toFile();
		File metaInf = new File(tempRoot, "META-INF");
		metaInf.mkdirs();
		File persistenceXml = new File(metaInf, "persistence.xml");
		persistenceXml.createNewFile();
		FileWriter fileWriter = new FileWriter(persistenceXml);
		fileWriter.write(PERSISTENCE_XML);
		fileWriter.close();
		original = Thread.currentThread().getContextClassLoader();
		ClassLoader urlCl = URLClassLoader.newInstance(
				new URL[] { new URL(tempRoot.toURI().toURL().toString())} , 
				original);
		Thread.currentThread().setContextClassLoader(urlCl);
	}
	
	@AfterEach
	public void afterEach() {
		Thread.currentThread().setContextClassLoader(original);
	}
	
	@Test
	public void testCreateEntityManagerFactoryBuilder() {
		Properties properties = new Properties();
		properties.put("foo", "bar");
		assertNull(
				HibernateToolsPersistenceProvider.createEntityManagerFactoryBuilder(
						"barfoo", properties));
		EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = 
				HibernateToolsPersistenceProvider.createEntityManagerFactoryBuilder(
						"foobar", properties);
		assertNotNull(entityManagerFactoryBuilder);
		assertEquals("bar", entityManagerFactoryBuilder.getConfigurationValues().get("foo"));
	}

}
