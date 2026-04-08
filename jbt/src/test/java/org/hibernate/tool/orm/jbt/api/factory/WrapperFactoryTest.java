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
package org.hibernate.tool.orm.jbt.api.factory;


import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.DatabaseReaderWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ExporterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.HqlCodeAssistWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.HqlCompletionProposalWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.NamingStrategyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.OverrideRepositoryWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.RevengSettingsWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.RevengStrategyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.SchemaExportWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableFilterWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeFactoryWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.HbmExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.internal.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.internal.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.internal.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.internal.wrp.ConfigurationWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.wrp.PersistentClassWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.wrp.PersistentClassWrapperFactory.EntityKind;
import org.hibernate.tool.orm.jbt.internal.wrp.RevengStrategyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.wrp.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.wrp.ValueWrapperFactory.ValueKind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WrapperFactoryTest {

	@Test
	public void testCreateArtifactCollectorWrapper() {
		Object artifactCollectorWrapper = WrapperFactory.createArtifactCollectorWrapper();
		assertNotNull(artifactCollectorWrapper);
		assertTrue(artifactCollectorWrapper instanceof Wrapper);
		Object wrappedArtifactCollector = ((Wrapper)artifactCollectorWrapper).getWrappedObject();
		assertTrue(wrappedArtifactCollector instanceof ArtifactCollector);
	}

	@Test
	public void testCreateCfg2HbmWrapper() {
		Object cfg2HbmWrapper = WrapperFactory.createCfg2HbmWrapper();
		assertNotNull(cfg2HbmWrapper);
		assertTrue(cfg2HbmWrapper instanceof Wrapper);
	}

	@Test
	public void testCreateNamingStrategyWrapper() {
		Object namingStrategyWrapper = WrapperFactory.createNamingStrategyWrapper(ImplicitNamingStrategyJpaCompliantImpl.class.getName());
		assertNotNull(namingStrategyWrapper);
		assertTrue(namingStrategyWrapper instanceof NamingStrategyWrapper);
		Object wrappedNamingStrategy = ((NamingStrategyWrapper)namingStrategyWrapper).getWrappedObject();
		assertTrue(wrappedNamingStrategy instanceof ImplicitNamingStrategyJpaCompliantImpl);
		namingStrategyWrapper = null;
		assertNull(namingStrategyWrapper);
		try {
			namingStrategyWrapper = WrapperFactory.createNamingStrategyWrapper("foo");
			fail();
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Exception while looking up class 'foo'");
		}
		assertNull(namingStrategyWrapper);
	}

	@Test
	public void testCreateOverrideRepositoryWrapper() {
		Object overrideRepositoryWrapper = WrapperFactory.createOverrideRepositoryWrapper();
		assertNotNull(overrideRepositoryWrapper);
		assertTrue(overrideRepositoryWrapper instanceof OverrideRepositoryWrapper);
		Object wrappedOverrideRepository = ((Wrapper)overrideRepositoryWrapper).getWrappedObject();
		assertTrue(wrappedOverrideRepository instanceof OverrideRepository);
	}

	@Test
	public void testCreateRevengStrategyWrapper() throws Exception {
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		Object reverseEngineeringStrategyWrapper = WrapperFactory
				.createRevengStrategyWrapper();
		assertNotNull(reverseEngineeringStrategyWrapper);
		assertTrue(reverseEngineeringStrategyWrapper instanceof Wrapper);
		assertTrue(((Wrapper)reverseEngineeringStrategyWrapper).getWrappedObject() instanceof DefaultStrategy);
		RevengStrategyWrapper delegate = (RevengStrategyWrapper)reverseEngineeringStrategyWrapper;
		reverseEngineeringStrategyWrapper = WrapperFactory
				.createRevengStrategyWrapper(
						TestDelegatingStrategy.class.getName(),
						delegate);
		assertNotNull(reverseEngineeringStrategyWrapper);
		assertTrue(reverseEngineeringStrategyWrapper instanceof Wrapper);
		assertTrue(((Wrapper)reverseEngineeringStrategyWrapper).getWrappedObject() instanceof TestDelegatingStrategy);
		assertSame(
				delegateField.get(((Wrapper)reverseEngineeringStrategyWrapper).getWrappedObject()),
				delegate.getWrappedObject());
	}

	@Test
	public void testCreateRevengSettingsWrapper() {
		Object reverseEngineeringSettingsWrapper = null;
		RevengStrategyWrapper strategy = RevengStrategyWrapperFactory.createRevengStrategyWrapper();
		reverseEngineeringSettingsWrapper = WrapperFactory.createRevengSettingsWrapper(strategy);
		assertNotNull(reverseEngineeringSettingsWrapper);
		assertTrue(reverseEngineeringSettingsWrapper instanceof RevengSettingsWrapper);
		RevengSettings revengSettings = (RevengSettings)((RevengSettingsWrapper)reverseEngineeringSettingsWrapper).getWrappedObject();
		assertSame(strategy.getWrappedObject(), revengSettings.getRootStrategy());
	}

	@Test
	public void testCreateNativeConfigurationWrapper() {
		Object configurationWrapper = WrapperFactory.createNativeConfigurationWrapper();
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof ConfigurationWrapper);
		Object wrappedConfiguration = ((ConfigurationWrapper)configurationWrapper).getWrappedObject();
		assertTrue(wrappedConfiguration instanceof NativeConfiguration);
	}

	@Test
	public void testCreateRevengConfigurationWrapper() {
		Object configurationWrapper = WrapperFactory.createRevengConfigurationWrapper();
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof ConfigurationWrapper);
		Object wrappedConfiguration = ((ConfigurationWrapper)configurationWrapper).getWrappedObject();
		assertTrue(wrappedConfiguration instanceof RevengConfiguration);
	}

	@Test
	public void testCreateJpaConfigurationWrapper() {
		Object configurationWrapper = WrapperFactory.createJpaConfigurationWrapper(null, null);
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof ConfigurationWrapper);
		Object wrappedConfiguration = ((ConfigurationWrapper)configurationWrapper).getWrappedObject();
		assertTrue(wrappedConfiguration instanceof JpaConfiguration);
	}

	@Test
	public void testCreateColumnWrapper() {
		Object columnWrapper = WrapperFactory.createColumnWrapper("foo");
		assertNotNull(columnWrapper);
		assertTrue(columnWrapper instanceof ColumnWrapper);
		assertEquals("foo", ((ColumnWrapper)columnWrapper).getName());
	}

	@Test
	public void testCreateRootClassWrapper() {
		Object rootClassWrapper = WrapperFactory.createRootClassWrapper();
		assertNotNull(rootClassWrapper);
		assertTrue(rootClassWrapper instanceof PersistentClassWrapper);
		PersistentClassWrapperFactory.PersistentClassWrapperImpl impl =
				(PersistentClassWrapperFactory.PersistentClassWrapperImpl) rootClassWrapper;
		assertEquals(EntityKind.ROOT, impl.getEntityKind());
	}

	@Test
	public void testCreateSingleTableSubclassWrapper() {
		Object rootClassWrapper = WrapperFactory.createRootClassWrapper();
		Object singleTableSubclassWrapper = WrapperFactory.createSingleTableSubClassWrapper(
				rootClassWrapper);
		assertNotNull(singleTableSubclassWrapper);
		assertTrue(singleTableSubclassWrapper instanceof PersistentClassWrapper);
		PersistentClassWrapperFactory.PersistentClassWrapperImpl impl =
				(PersistentClassWrapperFactory.PersistentClassWrapperImpl) singleTableSubclassWrapper;
		assertEquals(EntityKind.SINGLE_TABLE_SUBCLASS, impl.getEntityKind());
		assertSame(rootClassWrapper, ((PersistentClassWrapper)singleTableSubclassWrapper).getRootClass());
	}

	@Test
	public void testCreateJoinedSubclassWrapper() {
		Object rootClassWrapper = WrapperFactory.createRootClassWrapper();
		Object joinedTableSubclassWrapper = WrapperFactory.createJoinedTableSubClassWrapper(
				rootClassWrapper);
		assertNotNull(joinedTableSubclassWrapper);
		assertTrue(joinedTableSubclassWrapper instanceof PersistentClassWrapper);
		PersistentClassWrapperFactory.PersistentClassWrapperImpl impl =
				(PersistentClassWrapperFactory.PersistentClassWrapperImpl) joinedTableSubclassWrapper;
		assertEquals(EntityKind.JOINED_SUBCLASS, impl.getEntityKind());
		assertSame(rootClassWrapper, ((PersistentClassWrapper)joinedTableSubclassWrapper).getRootClass());
	}

	@Test
	public void testCreateSpecialRootClassWrapper() {
		PropertyWrapper propertyWrapper = (PropertyWrapper)WrapperFactory.createPropertyWrapper();
		Object specialRootClassWrapper = WrapperFactory.createSpecialRootClassWrapper(propertyWrapper);
		assertNotNull(specialRootClassWrapper);
		assertInstanceOf(PersistentClassWrapper.class, specialRootClassWrapper);
		assertSame(propertyWrapper, ((PersistentClassWrapper)specialRootClassWrapper).getProperty());
	}

	@Test
	public void testCreatePropertyWrapper() {
		Object propertyWrapper = WrapperFactory.createPropertyWrapper();
		assertNotNull(propertyWrapper);
		assertTrue(propertyWrapper instanceof PropertyWrapper);
	}

	@Test
	public void testCreateHqlCompletionProposalWrapper() {
		HQLCompletionProposal hqlCompletionProposalTarget =
				new HQLCompletionProposal(HQLCompletionProposal.PROPERTY, Integer.MAX_VALUE);
		Object hqlCompletionProposalWrapper =
				WrapperFactory.createHqlCompletionProposalWrapper(hqlCompletionProposalTarget);
		assertNotNull(hqlCompletionProposalWrapper);
		assertTrue(hqlCompletionProposalWrapper instanceof HqlCompletionProposalWrapper);
	}

	@Test
	public void testCreateArrayWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object arrayWrapper = WrapperFactory.createArrayWrapper(persistentClassWrapper);
		assertTrue(arrayWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) arrayWrapper;
		assertEquals(ValueKind.ARRAY, impl.getKind());
	}

	@Test
	public void testCreateBagWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object bagWrapper = WrapperFactory.createBagWrapper(persistentClassWrapper);
		assertTrue(bagWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) bagWrapper;
		assertEquals(ValueKind.BAG, impl.getKind());
	}

	@Test
	public void testCreateListWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object listWrapper = WrapperFactory.createListWrapper(persistentClassWrapper);
		assertTrue(listWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) listWrapper;
		assertEquals(ValueKind.LIST, impl.getKind());
	}

	@Test
	public void testCreateDatabaseReaderWrapper() {
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		RevengStrategyWrapper strategy = RevengStrategyWrapperFactory.createRevengStrategyWrapper();
		Object databaseReaderWrapper = WrapperFactory.createDatabaseReaderWrapper(
				properties, strategy);
		assertNotNull(databaseReaderWrapper);
		assertTrue(databaseReaderWrapper instanceof DatabaseReaderWrapper);
	}

	@Test
	public void testCreateTableWrapper() {
		Object tableWrapper = WrapperFactory.createTableWrapper("foo");
		assertNotNull(tableWrapper);
		assertTrue(tableWrapper instanceof TableWrapper);
		assertEquals("foo", ((TableWrapper)tableWrapper).getName());
	}

	@Test
	public void testCreateManyToOneWrapper() {
		TableWrapper tableWrapper = (TableWrapper) WrapperFactory.createTableWrapper("foo");
		Object manyToOneWrapper = WrapperFactory.createManyToOneWrapper(tableWrapper);
		assertTrue(manyToOneWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) manyToOneWrapper;
		assertEquals(ValueKind.MANY_TO_ONE, impl.getKind());
	}

	@Test
	public void testCreateMapWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object mapWrapper = WrapperFactory.createMapWrapper(persistentClassWrapper);
		assertTrue(mapWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) mapWrapper;
		assertEquals(ValueKind.MAP, impl.getKind());
	}

	@Test
	public void testCreateOneToManyWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object oneToManyWrapper = WrapperFactory.createOneToManyWrapper(persistentClassWrapper);
		assertTrue(oneToManyWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) oneToManyWrapper;
		assertEquals(ValueKind.ONE_TO_MANY, impl.getKind());
	}

	@Test
	public void testCreateOneToOneWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object oneToOneWrapper = WrapperFactory.createOneToOneWrapper(persistentClassWrapper);
		assertTrue(oneToOneWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) oneToOneWrapper;
		assertEquals(ValueKind.ONE_TO_ONE, impl.getKind());
	}

	@Test
	public void testCreatePrimitiveArrayWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object primitiveArrayWrapper = WrapperFactory.createPrimitiveArrayWrapper(persistentClassWrapper);
		assertTrue(primitiveArrayWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) primitiveArrayWrapper;
		assertEquals(ValueKind.PRIMITIVE_ARRAY, impl.getKind());
	}

	@Test
	public void testCreateSetWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object setWrapper = WrapperFactory.createSetWrapper(persistentClassWrapper);
		assertTrue(setWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) setWrapper;
		assertEquals(ValueKind.SET, impl.getKind());
	}

	@Test
	public void testCreateSimpleValueWrapper() {
		Object simpleValueWrapper = WrapperFactory.createSimpleValueWrapper();
		assertTrue(simpleValueWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) simpleValueWrapper;
		assertEquals(ValueKind.SIMPLE_VALUE, impl.getKind());
	}

	@Test
	public void testCreateComponentWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object componentWrapper = WrapperFactory.createComponentWrapper(persistentClassWrapper);
		assertTrue(componentWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) componentWrapper;
		assertEquals(ValueKind.COMPONENT, impl.getKind());
	}

	@Test
	public void testCreateDependantValueWrapper() {
		TableWrapper tableWrapper = (TableWrapper) WrapperFactory.createTableWrapper("foo");
		Object valueWrapper = WrapperFactory.createSimpleValueWrapper();
		Object dependantValueWrapper = WrapperFactory.createDependantValueWrapper(tableWrapper, valueWrapper);
		assertTrue(dependantValueWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) dependantValueWrapper;
		assertEquals(ValueKind.DEPENDANT_VALUE, impl.getKind());
	}

	@Test
	public void testCreateAnyValueWrapper() {
		TableWrapper tableWrapper = (TableWrapper) WrapperFactory.createTableWrapper("foo");
		Object anyValueWrapper = WrapperFactory.createAnyValueWrapper(tableWrapper);
		assertTrue(anyValueWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) anyValueWrapper;
		assertEquals(ValueKind.ANY, impl.getKind());
	}

	@Test
	public void testCreateIdentifierBagValueWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		Object identifierBagValueWrapper = WrapperFactory.createIdentifierBagValueWrapper(persistentClassWrapper);
		assertTrue(identifierBagValueWrapper instanceof ValueWrapper);
		ValueWrapperFactory.ValueWrapperImpl impl = (ValueWrapperFactory.ValueWrapperImpl) identifierBagValueWrapper;
		assertEquals(ValueKind.IDENTIFIER_BAG, impl.getKind());
	}

	@Test
	public void testCreateTableFilterWrapper() {
		Object tableFilterWrapper = WrapperFactory.createTableFilterWrapper();
		assertNotNull(tableFilterWrapper);
		assertTrue(tableFilterWrapper instanceof TableFilterWrapper);
		Object wrappedTableFilter = ((Wrapper)tableFilterWrapper).getWrappedObject();
		assertTrue(wrappedTableFilter instanceof TableFilter);
	}

	@Test
	public void testCreateTypeFactoryWrapper() {
		Object typeFactoryWrapper = WrapperFactory.createTypeFactoryWrapper();
		assertNotNull(typeFactoryWrapper);
		assertTrue(typeFactoryWrapper instanceof TypeFactoryWrapper);
	}

	@Test
	public void testCreateEnvironmentWrapper() {
		assertNotNull(WrapperFactory.createEnvironmentWrapper());
	}

	@Test
	public void testCreateSchemaExport() throws Exception {
		ConfigurationWrapper configurationWrapper =
				ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		Object schemaExport = WrapperFactory.createSchemaExport(configurationWrapper);
		assertNotNull(schemaExport);
		assertTrue(schemaExport instanceof SchemaExportWrapper);
	}

	@Test
	public void testCreateHbmExporterWrapper() {
		ConfigurationWrapper configuration = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		File file = new File("foo");
		Object hbmExporterWrapper = WrapperFactory.createHbmExporterWrapper(configuration, file);
		assertNotNull(hbmExporterWrapper);
		assertTrue(hbmExporterWrapper instanceof HbmExporterWrapper);
		HbmExporterWrapperFactory.HbmExporterWrapperImpl impl =
				(HbmExporterWrapperFactory.HbmExporterWrapperImpl) ((Wrapper) hbmExporterWrapper).getWrappedObject();
		assertSame(file, impl.getOutputFile());
		assertSame(configuration, impl.getConfigurationWrapper());
	}

	@Test
	public void testCreateExporterWrapper() {
		Object exporterWrapper = WrapperFactory.createExporterWrapper(Hbm2DDLExporter.class.getName());
		assertNotNull(exporterWrapper);
		assertTrue(exporterWrapper instanceof ExporterWrapper);
	}

	@Test
	public void testCreateHqlCodeAssistWrapper() throws Exception {
		ConfigurationWrapper configurationWrapper = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		configurationWrapper.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		Metadata metadata = MetadataHelper.getMetadata((Configuration)configurationWrapper.getWrappedObject());
		Object hqlCodeAssistWrapper = WrapperFactory.createHqlCodeAssistWrapper(configurationWrapper);
		assertTrue(hqlCodeAssistWrapper instanceof HqlCodeAssistWrapper);
		Field metadataField = HQLCodeAssist.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertSame(metadata, metadataField.get(((Wrapper)hqlCodeAssistWrapper).getWrappedObject()));
	}

	public static class TestRevengStrategy extends DefaultStrategy {}
	public static class TestDelegatingStrategy extends DelegatingStrategy {
		public TestDelegatingStrategy(RevengStrategy delegate) {
			super(delegate);
		}
	}

}
