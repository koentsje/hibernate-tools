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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.tool.orm.jbt.api.wrp.JoinWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;

public class PersistentClassWrapperFactory {

	public enum EntityKind {
		ROOT,
		SINGLE_TABLE_SUBCLASS,
		JOINED_SUBCLASS
	}

	public static PersistentClassWrapper createRootClassWrapper() {
		return new PersistentClassWrapperImpl(
				new DynamicClassDetails(
						"UnknownEntity",
						null,
						false,
						null,
						null,
						DummyModelsContext.INSTANCE),
				EntityKind.ROOT,
				null,
				DummyModelsContext.INSTANCE);
	}

	public static PersistentClassWrapper createSingleTableSubClassWrapper(PersistentClassWrapper superClassWrapper) {
		PersistentClassWrapperImpl superImpl = (PersistentClassWrapperImpl) superClassWrapper;
		DynamicClassDetails subclassDetails = new DynamicClassDetails(
				"UnknownSubEntity",
				null,
				false,
				(ClassDetails) superClassWrapper.getWrappedObject(),
				null,
				superImpl.modelsContext);
		PersistentClassWrapperImpl subWrapper = new PersistentClassWrapperImpl(
				subclassDetails,
				EntityKind.SINGLE_TABLE_SUBCLASS,
				superImpl,
				superImpl.modelsContext);
		superImpl.addSubclassWrapper(subWrapper);
		return subWrapper;
	}

	public static PersistentClassWrapper createJoinedTableSubClassWrapper(PersistentClassWrapper superClassWrapper) {
		PersistentClassWrapperImpl superImpl = (PersistentClassWrapperImpl) superClassWrapper;
		DynamicClassDetails subclassDetails = new DynamicClassDetails(
				"UnknownJoinedSubEntity",
				null,
				false,
				(ClassDetails) superClassWrapper.getWrappedObject(),
				null,
				superImpl.modelsContext);
		PersistentClassWrapperImpl subWrapper = new PersistentClassWrapperImpl(
				subclassDetails,
				EntityKind.JOINED_SUBCLASS,
				superImpl,
				superImpl.modelsContext);
		superImpl.addSubclassWrapper(subWrapper);
		return subWrapper;
	}

	public static PersistentClassWrapper createSpecialRootClassWrapper(PropertyWrapper propertyWrapper) {
		return new SpecialRootClassWrapperImpl(propertyWrapper);
	}

	public static PersistentClassWrapper createPersistentClassWrapper(ClassDetails classDetails) {
		return new PersistentClassWrapperImpl(
				classDetails,
				EntityKind.ROOT,
				null,
				DummyModelsContext.INSTANCE);
	}

	public static class PersistentClassWrapperImpl
			implements PersistentClassWrapper {

		final ClassDetails classDetails;
		final EntityKind entityKind;
		final PersistentClassWrapperImpl superClassWrapper;
		final ModelsContext modelsContext;

		private String entityName;
		private String className;
		private Boolean isAbstract;
		private boolean isLazy = true;
		private boolean isMutable = true;
		private boolean isPolymorphic;
		private int batchSize = 1;
		private String discriminatorValue;
		private String proxyInterfaceName;
		private String loaderName;
		private String where;
		private String cacheConcurrencyStrategy;
		private int optimisticLockMode = 0; // VERSION
		private boolean lazyPropertiesCacheable = true;
		private boolean forceDiscriminator;
		private boolean discriminatorInsertable = true;

		private String customSQLInsert;
		private boolean customInsertCallable;
		private String customSQLUpdate;
		private boolean customUpdateCallable;
		private String customSQLDelete;
		private boolean customDeleteCallable;

		private TableWrapper table;
		private ValueWrapper identifier;
		private ValueWrapper discriminator;
		private PropertyWrapper identifierProperty;
		private PropertyWrapper version;
		private ValueWrapper key;

		private final List<PropertyWrapper> properties = new ArrayList<>();
		private final List<JoinWrapper> joins = new ArrayList<>();
		private final List<PersistentClassWrapperImpl> subclasses = new ArrayList<>();

		PersistentClassWrapperImpl(
				ClassDetails classDetails,
				EntityKind entityKind,
				PersistentClassWrapperImpl superClassWrapper,
				ModelsContext modelsContext) {
			this.classDetails = classDetails;
			this.entityKind = entityKind;
			this.superClassWrapper = superClassWrapper;
			this.modelsContext = modelsContext;
		}

		public EntityKind getEntityKind() {
			return entityKind;
		}

		@Override
		public Object getWrappedObject() {
			return classDetails;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!Wrapper.class.isAssignableFrom(o.getClass())) return false;
			return getWrappedObject().equals(((Wrapper) o).getWrappedObject());
		}

		@Override
		public boolean isAssignableToRootClass() {
			return isInstanceOfRootClass();
		}

		@Override
		public boolean isRootClass() {
			return entityKind == EntityKind.ROOT;
		}

		@Override
		public boolean isInstanceOfRootClass() {
			return entityKind == EntityKind.ROOT;
		}

		@Override
		public boolean isInstanceOfSubclass() {
			return entityKind == EntityKind.SINGLE_TABLE_SUBCLASS
					|| entityKind == EntityKind.JOINED_SUBCLASS;
		}

		@Override
		public boolean isInstanceOfJoinedSubclass() {
			return entityKind == EntityKind.JOINED_SUBCLASS;
		}

		@Override
		public PropertyWrapper getProperty() {
			throw new RuntimeException("getProperty() is only allowed on SpecialRootClass");
		}

		@Override
		public void setTable(TableWrapper tableWrapper) {
			if (isInstanceOfRootClass()) {
				this.table = tableWrapper;
			} else if (isInstanceOfJoinedSubclass()) {
				this.table = tableWrapper;
			} else {
				throw new RuntimeException("Method 'setTable(Table)' is not supported.");
			}
		}

		@Override
		public void setIdentifier(ValueWrapper value) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'setIdentifier(Value)' can only be called on RootClass instances");
			}
			this.identifier = value;
		}

		@Override
		public void setKey(ValueWrapper value) {
			if (!isInstanceOfJoinedSubclass()) {
				throw new RuntimeException("setKey(Value) is only allowed on JoinedSubclass");
			}
			this.key = value;
		}

		@Override
		public boolean isInstanceOfSpecialRootClass() {
			return false;
		}

		@Override
		public PropertyWrapper getParentProperty() {
			throw new RuntimeException("getParentProperty() is only allowed on SpecialRootClass");
		}

		@Override
		public void setIdentifierProperty(PropertyWrapper p) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("setIdentifierProperty(Property) is only allowed on RootClass instances");
			}
			this.identifierProperty = p;
		}

		@Override
		public void setDiscriminator(ValueWrapper value) {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'setDiscriminator(Value)' can only be called on RootClass instances");
			}
			this.discriminator = value;
		}

		@Override
		public boolean isLazyPropertiesCacheable() {
			if (!isInstanceOfRootClass()) {
				throw new RuntimeException("Method 'isLazyPropertiesCacheable()' can only be called on RootClass instances");
			}
			return lazyPropertiesCacheable;
		}

		@Override
		public Iterator<PropertyWrapper> getPropertyIterator() {
			return getProperties().iterator();
		}

		@Override
		public Iterator<JoinWrapper> getJoinIterator() {
			return getJoins().iterator();
		}

		@Override
		public Iterator<PersistentClassWrapper> getSubclassIterator() {
			return getSubclasses().iterator();
		}

		@Override
		public Iterator<PropertyWrapper> getPropertyClosureIterator() {
			return getPropertyClosure().iterator();
		}

		@Override
		public String getEntityName() {
			return entityName;
		}

		@Override
		public String getClassName() {
			return className;
		}

		@Override
		public PropertyWrapper getIdentifierProperty() {
			if (isInstanceOfRootClass()) {
				return identifierProperty;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.identifierProperty : null;
		}

		@Override
		public boolean hasIdentifierProperty() {
			return getIdentifierProperty() != null;
		}

		@Override
		public PersistentClassWrapper getRootClass() {
			if (superClassWrapper == null) {
				return this;
			}
			return superClassWrapper.getRootClass();
		}

		@Override
		public PersistentClassWrapper getSuperclass() {
			return superClassWrapper;
		}

		@Override
		public PropertyWrapper getProperty(String name) {
			for (PropertyWrapper p : properties) {
				if (name.equals(p.getName())) {
					return p;
				}
			}
			if (superClassWrapper != null) {
				return superClassWrapper.getProperty(name);
			}
			throw new RuntimeException(
					"property [" + name + "] not found on entity [" + entityName + "]");
		}

		@Override
		public TableWrapper getTable() {
			if (table != null) {
				return table;
			}
			if (entityKind == EntityKind.SINGLE_TABLE_SUBCLASS && superClassWrapper != null) {
				return superClassWrapper.getTable();
			}
			return null;
		}

		@Override
		public Boolean isAbstract() {
			return isAbstract;
		}

		@Override
		public ValueWrapper getDiscriminator() {
			if (isInstanceOfRootClass()) {
				return discriminator;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.discriminator : null;
		}

		@Override
		public ValueWrapper getIdentifier() {
			if (isInstanceOfRootClass()) {
				return identifier;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.identifier : null;
		}

		@Override
		public PropertyWrapper getVersion() {
			if (isInstanceOfRootClass()) {
				return version;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.version : null;
		}

		@Override
		public void setClassName(String name) {
			this.className = name;
		}

		@Override
		public void setEntityName(String name) {
			this.entityName = name;
		}

		@Override
		public void setDiscriminatorValue(String str) {
			this.discriminatorValue = str;
		}

		@Override
		public void setAbstract(Boolean b) {
			this.isAbstract = b;
		}

		@Override
		public void addProperty(PropertyWrapper p) {
			properties.add(p);
		}

		@Override
		public void setProxyInterfaceName(String name) {
			this.proxyInterfaceName = name;
		}

		@Override
		public void setLazy(boolean b) {
			this.isLazy = b;
		}

		@Override
		public boolean isCustomDeleteCallable() {
			return customDeleteCallable;
		}

		@Override
		public boolean isCustomInsertCallable() {
			return customInsertCallable;
		}

		@Override
		public boolean isCustomUpdateCallable() {
			return customUpdateCallable;
		}

		@Override
		public boolean isDiscriminatorInsertable() {
			if (isInstanceOfRootClass()) {
				return discriminatorInsertable;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.discriminatorInsertable : true;
		}

		@Override
		public boolean isDiscriminatorValueNotNull() {
			String dv = discriminatorValue;
			return dv != null && !"null".equals(dv);
		}

		@Override
		public boolean isDiscriminatorValueNull() {
			return "null".equals(discriminatorValue);
		}

		@Override
		public boolean isExplicitPolymorphism() {
			return false;
		}

		@Override
		public boolean isForceDiscriminator() {
			if (isInstanceOfRootClass()) {
				return forceDiscriminator;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.forceDiscriminator : false;
		}

		@Override
		public boolean isInherited() {
			return isInstanceOfSubclass();
		}

		@Override
		public boolean isJoinedSubclass() {
			return entityKind == EntityKind.JOINED_SUBCLASS;
		}

		@Override
		public boolean isLazy() {
			return isLazy;
		}

		@Override
		public boolean isMutable() {
			if (isInstanceOfRootClass()) {
				return isMutable;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.isMutable : true;
		}

		@Override
		public boolean isPolymorphic() {
			if (isInstanceOfSubclass()) {
				return true;
			}
			if (isInstanceOfRootClass()) {
				return isPolymorphic;
			}
			return false;
		}

		@Override
		public boolean isVersioned() {
			return getVersion() != null;
		}

		@Override
		public int getBatchSize() {
			return batchSize;
		}

		@Override
		public String getCacheConcurrencyStrategy() {
			if (isInstanceOfRootClass()) {
				return cacheConcurrencyStrategy;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.cacheConcurrencyStrategy : null;
		}

		@Override
		public String getCustomSQLDelete() {
			return customSQLDelete;
		}

		@Override
		public String getCustomSQLInsert() {
			return customSQLInsert;
		}

		@Override
		public String getCustomSQLUpdate() {
			return customSQLUpdate;
		}

		@Override
		public String getDiscriminatorValue() {
			return discriminatorValue;
		}

		@Override
		public String getLoaderName() {
			return loaderName;
		}

		@Override
		public int getOptimisticLockMode() {
			if (isInstanceOfRootClass()) {
				return optimisticLockMode;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.optimisticLockMode : 0;
		}

		@Override
		public String getWhere() {
			if (isInstanceOfRootClass()) {
				return where;
			}
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.where : null;
		}

		@Override
		public TableWrapper getRootTable() {
			PersistentClassWrapperImpl root = getRootImpl();
			return root != null ? root.table : table;
		}

		@Override
		public List<PropertyWrapper> getProperties() {
			return new ArrayList<>(properties);
		}

		@Override
		public List<JoinWrapper> getJoins() {
			return new ArrayList<>(joins);
		}

		@Override
		public List<PersistentClassWrapper> getSubclasses() {
			return new ArrayList<>(subclasses);
		}

		@Override
		public List<PropertyWrapper> getPropertyClosure() {
			List<PropertyWrapper> result = new ArrayList<>();
			if (superClassWrapper != null) {
				result.addAll(superClassWrapper.getPropertyClosure());
			}
			result.addAll(properties);
			return result;
		}

		// --- Mutators ---

		public void addSubclassWrapper(PersistentClassWrapperImpl sub) {
			subclasses.add(sub);
		}

		public void addJoinWrapper(JoinWrapper join) {
			joins.add(join);
		}

		public void setCustomSQLDelete(String sql, boolean callable) {
			this.customSQLDelete = sql;
			this.customDeleteCallable = callable;
		}

		public void setCustomSQLInsert(String sql, boolean callable) {
			this.customSQLInsert = sql;
			this.customInsertCallable = callable;
		}

		public void setCustomSQLUpdate(String sql, boolean callable) {
			this.customSQLUpdate = sql;
			this.customUpdateCallable = callable;
		}

		public void setVersion(PropertyWrapper version) {
			this.version = version;
		}

		public void setLazyPropertiesCacheable(boolean b) {
			this.lazyPropertiesCacheable = b;
		}

		public void setMutable(boolean b) {
			this.isMutable = b;
		}

		public void setPolymorphic(boolean b) {
			this.isPolymorphic = b;
		}

		public void setBatchSize(int size) {
			this.batchSize = size;
		}

		public void setCacheConcurrencyStrategy(String s) {
			this.cacheConcurrencyStrategy = s;
		}

		public void setLoaderName(String name) {
			this.loaderName = name;
		}

		public void setOptimisticLockMode(int mode) {
			this.optimisticLockMode = mode;
		}

		public void setWhere(String where) {
			this.where = where;
		}

		public void setForceDiscriminator(boolean b) {
			this.forceDiscriminator = b;
		}

		public void setDiscriminatorInsertable(boolean b) {
			this.discriminatorInsertable = b;
		}

		public String getProxyInterfaceName() {
			return proxyInterfaceName;
		}

		public ValueWrapper getKey() {
			return key;
		}

		private PersistentClassWrapperImpl getRootImpl() {
			if (superClassWrapper == null) {
				return this;
			}
			return superClassWrapper.getRootImpl();
		}
	}

	public static class SpecialRootClassWrapperImpl extends PersistentClassWrapperImpl {

		private final PropertyWrapper propertyWrapper;
		private PropertyWrapper parentPropertyWrapper;

		private SpecialRootClassWrapperImpl(PropertyWrapper propertyWrapper) {
			super(new DynamicClassDetails(
							"SpecialRootClass",
							null,
							false,
							null,
							null,
							DummyModelsContext.INSTANCE),
					EntityKind.ROOT,
					null,
					DummyModelsContext.INSTANCE);
			this.propertyWrapper = propertyWrapper;
		}

		@Override
		public PropertyWrapper getProperty() {
			return propertyWrapper;
		}

		@Override
		public PropertyWrapper getParentProperty() {
			return parentPropertyWrapper;
		}

		@Override
		public boolean isInstanceOfSpecialRootClass() {
			return true;
		}

		@Override
		public boolean isRootClass() {
			return false;
		}

		public void setParentPropertyWrapper(PropertyWrapper pw) {
			this.parentPropertyWrapper = pw;
		}
	}

}
