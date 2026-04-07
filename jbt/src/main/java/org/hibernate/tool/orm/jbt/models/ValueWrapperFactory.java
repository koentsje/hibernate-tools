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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;

public class ValueWrapperFactory {

	public enum ValueKind {
		SIMPLE_VALUE,
		COMPONENT,
		ONE_TO_MANY,
		MANY_TO_ONE,
		ONE_TO_ONE,
		MAP,
		SET,
		LIST,
		BAG,
		IDENTIFIER_BAG,
		ARRAY,
		PRIMITIVE_ARRAY,
		DEPENDANT_VALUE,
		ANY
	}

	public static ValueWrapper createSimpleValueWrapper() {
		return new ValueWrapperImpl(ValueKind.SIMPLE_VALUE);
	}

	public static ValueWrapper createComponentWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.COMPONENT);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createOneToManyWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.ONE_TO_MANY);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createManyToOneWrapper() {
		return new ValueWrapperImpl(ValueKind.MANY_TO_ONE);
	}

	public static ValueWrapper createOneToOneWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.ONE_TO_ONE);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createMapWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.MAP);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createSetWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.SET);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createListWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.LIST);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createBagWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.BAG);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createIdentifierBagWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.IDENTIFIER_BAG);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createArrayWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.ARRAY);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createPrimitiveArrayWrapper(PersistentClassWrapper owner) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.PRIMITIVE_ARRAY);
		v.setOwner(owner);
		return v;
	}

	public static ValueWrapper createDependantValueWrapper(TableWrapper table, ValueWrapper wrappedValue) {
		ValueWrapperImpl v = new ValueWrapperImpl(ValueKind.DEPENDANT_VALUE);
		v.setTable(table);
		return v;
	}

	public static ValueWrapper createAnyValueWrapper() {
		return new ValueWrapperImpl(ValueKind.ANY);
	}

	public static class ValueWrapperImpl implements ValueWrapper {

		private final ValueKind kind;

		private TableWrapper table;
		private TableWrapper collectionTable;
		private ValueWrapper element;
		private ValueWrapper key;
		private ValueWrapper index;
		private String typeName;
		private String componentClassName;
		private String referencedEntityName;
		private String entityName;
		private String elementClassName;
		private String foreignKeyName;
		private String parentProperty;
		private String role;
		private boolean isEmbedded;
		private boolean isInverse;
		private boolean isLazy;
		private boolean isFetchModeJoin;
		private boolean isTypeSpecified;
		private PersistentClassWrapper owner;
		private PersistentClassWrapper associatedClass;
		private Properties typeParameters;

		private final List<ColumnWrapper> columns = new ArrayList<>();
		private final List<PropertyWrapper> properties = new ArrayList<>();

		ValueWrapperImpl(ValueKind kind) {
			this.kind = kind;
		}

		public ValueKind getKind() {
			return kind;
		}

		@Override
		public boolean isSimpleValue() {
			return kind == ValueKind.SIMPLE_VALUE
					|| kind == ValueKind.MANY_TO_ONE
					|| kind == ValueKind.ONE_TO_ONE
					|| kind == ValueKind.DEPENDANT_VALUE
					|| kind == ValueKind.ANY
					|| kind == ValueKind.COMPONENT;
		}

		@Override
		public boolean isCollection() {
			return kind == ValueKind.MAP
					|| kind == ValueKind.SET
					|| kind == ValueKind.LIST
					|| kind == ValueKind.BAG
					|| kind == ValueKind.IDENTIFIER_BAG
					|| kind == ValueKind.ARRAY
					|| kind == ValueKind.PRIMITIVE_ARRAY;
		}

		@Override
		public ValueWrapper getCollectionElement() {
			if (isCollection()) {
				return element;
			}
			return null;
		}

		@Override
		public boolean isOneToMany() {
			return kind == ValueKind.ONE_TO_MANY;
		}

		@Override
		public boolean isManyToOne() {
			return kind == ValueKind.MANY_TO_ONE;
		}

		@Override
		public boolean isOneToOne() {
			return kind == ValueKind.ONE_TO_ONE;
		}

		@Override
		public boolean isMap() {
			return kind == ValueKind.MAP;
		}

		@Override
		public boolean isComponent() {
			return kind == ValueKind.COMPONENT;
		}

		@Override
		public boolean isEmbedded() {
			if (isComponent()) {
				return isEmbedded;
			}
			return false;
		}

		@Override
		public boolean isToOne() {
			return kind == ValueKind.MANY_TO_ONE || kind == ValueKind.ONE_TO_ONE;
		}

		@Override
		public TableWrapper getTable() {
			return table;
		}

		@Override
		public TypeWrapper getType() {
			return null;
		}

		@Override
		public void setElement(ValueWrapper v) {
			if (isCollection()) {
				this.element = v;
			}
		}

		@Override
		public void setCollectionTable(TableWrapper table) {
			if (isCollection()) {
				this.collectionTable = table;
			}
		}

		@Override
		public void setTable(TableWrapper table) {
			this.table = table;
		}

		@Override
		public boolean isList() {
			return kind == ValueKind.LIST;
		}

		@Override
		public void setIndex(ValueWrapper v) {
			if (kind == ValueKind.LIST || kind == ValueKind.MAP
					|| kind == ValueKind.ARRAY || kind == ValueKind.PRIMITIVE_ARRAY) {
				this.index = v;
			}
		}

		@Override
		public void setTypeName(String s) {
			this.typeName = s;
			if (s != null) {
				this.isTypeSpecified = true;
			}
		}

		@Override
		public String getComponentClassName() {
			if (isComponent()) {
				return componentClassName;
			}
			return null;
		}

		@Override
		public Iterator<ColumnWrapper> getColumnIterator() {
			return columns.iterator();
		}

		@Override
		public boolean isTypeSpecified() {
			if (isSimpleValue()) {
				return isTypeSpecified;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'isTypeSpecified()'.");
		}

		@Override
		public TableWrapper getCollectionTable() {
			if (isCollection()) {
				return collectionTable;
			}
			return null;
		}

		@Override
		public ValueWrapper getKey() {
			if (isCollection()) {
				return key;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getKey()'.");
		}

		@Override
		public ValueWrapper getIndex() {
			if (kind == ValueKind.LIST || kind == ValueKind.MAP
					|| kind == ValueKind.ARRAY || kind == ValueKind.PRIMITIVE_ARRAY) {
				return index;
			}
			return null;
		}

		@Override
		public String getElementClassName() {
			if (kind == ValueKind.ARRAY || kind == ValueKind.PRIMITIVE_ARRAY) {
				return elementClassName;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getElementClassName()'.");
		}

		@Override
		public String getTypeName() {
			return typeName;
		}

		@Override
		public boolean isDependantValue() {
			return kind == ValueKind.DEPENDANT_VALUE;
		}

		@Override
		public boolean isAny() {
			return kind == ValueKind.ANY;
		}

		@Override
		public boolean isSet() {
			return kind == ValueKind.SET;
		}

		@Override
		public boolean isPrimitiveArray() {
			return kind == ValueKind.PRIMITIVE_ARRAY;
		}

		@Override
		public boolean isArray() {
			return kind == ValueKind.ARRAY || kind == ValueKind.PRIMITIVE_ARRAY;
		}

		@Override
		public boolean isIdentifierBag() {
			return kind == ValueKind.IDENTIFIER_BAG;
		}

		@Override
		public boolean isBag() {
			return kind == ValueKind.BAG;
		}

		@Override
		public String getReferencedEntityName() {
			if (isManyToOne() || isOneToOne() || isOneToMany()) {
				return referencedEntityName;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getReferencedEntityName()'.");
		}

		@Override
		public String getEntityName() {
			if (isOneToOne()) {
				return entityName;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getEntityName()'.");
		}

		@Override
		public Iterator<PropertyWrapper> getPropertyIterator() {
			if (isComponent()) {
				return properties.iterator();
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getPropertyIterator()'.");
		}

		@Override
		public void addColumn(ColumnWrapper column) {
			if (isSimpleValue() && column != null) {
				columns.add(column);
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'addColumn(ColumnWrapper)'.");
			}
		}

		@Override
		public void setTypeParameters(Properties properties) {
			if (isCollection() || isSimpleValue()) {
				this.typeParameters = properties;
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'setTypeParameters(Properties)'.");
			}
		}

		@Override
		public String getForeignKeyName() {
			if (isSimpleValue()) {
				return foreignKeyName;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getForeignKeyName()'.");
		}

		@Override
		public PersistentClassWrapper getOwner() {
			if (isCollection() || isComponent()) {
				return owner;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getOwner()'.");
		}

		@Override
		public ValueWrapper getElement() {
			if (isCollection()) {
				return element;
			}
			return null;
		}

		@Override
		public String getParentProperty() {
			if (isComponent()) {
				return parentProperty;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getParentProperty()'.");
		}

		@Override
		public void setElementClassName(String name) {
			if (isArray()) {
				this.elementClassName = name;
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'setElementClassName(String)'.");
			}
		}

		@Override
		public void setKey(ValueWrapper value) {
			if (isCollection()) {
				this.key = value;
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'setKey(ValueWrapper)'.");
			}
		}

		@Override
		public void setFetchModeJoin() {
			if (isCollection() || isToOne() || isAny()) {
				this.isFetchModeJoin = true;
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'setFetchModeJoin()'.");
			}
		}

		@Override
		public boolean isInverse() {
			if (isCollection()) {
				return isInverse;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'isInverse()'.");
		}

		@Override
		public PersistentClassWrapper getAssociatedClass() {
			if (isOneToMany()) {
				return associatedClass;
			}
			throw new UnsupportedOperationException(
					"Value kind '" + kind + "' does not support 'getAssociatedClass()'.");
		}

		@Override
		public void setLazy(boolean b) {
			if (isCollection() || isToOne() || isAny()) {
				this.isLazy = b;
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'setLazy(boolean)'.");
			}
		}

		@Override
		public void setRole(String role) {
			if (isCollection()) {
				this.role = role;
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'setRole(String)'.");
			}
		}

		@Override
		public void setReferencedEntityName(String name) {
			if (isToOne() || isOneToMany()) {
				this.referencedEntityName = name;
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'setReferencedEntityName(String)'.");
			}
		}

		@Override
		public void setAssociatedClass(PersistentClassWrapper pc) {
			if (isOneToMany()) {
				this.associatedClass = pc;
			} else {
				throw new UnsupportedOperationException(
						"Value kind '" + kind + "' does not support 'setAssociatedClass(PersistentClassWrapper)'.");
			}
		}

		// --- Mutators ---

		public void setOwner(PersistentClassWrapper owner) {
			this.owner = owner;
		}

		public void setEmbedded(boolean embedded) {
			this.isEmbedded = embedded;
		}

		public void setInverse(boolean inverse) {
			this.isInverse = inverse;
		}

		public void setComponentClassName(String className) {
			this.componentClassName = className;
		}

		public void setEntityName(String entityName) {
			this.entityName = entityName;
		}

		public void setForeignKeyName(String foreignKeyName) {
			this.foreignKeyName = foreignKeyName;
		}

		public void setParentProperty(String parentProperty) {
			this.parentProperty = parentProperty;
		}

		public void setTypeSpecified(boolean typeSpecified) {
			this.isTypeSpecified = typeSpecified;
		}

		public void addProperty(PropertyWrapper property) {
			this.properties.add(property);
		}

		public List<ColumnWrapper> getColumns() {
			return Collections.unmodifiableList(columns);
		}

		public List<PropertyWrapper> getProperties() {
			return Collections.unmodifiableList(properties);
		}

		public Properties getTypeParameters() {
			return typeParameters;
		}

		public String getRole() {
			return role;
		}

		public boolean isLazy() {
			return isLazy;
		}

		public boolean isFetchModeJoin() {
			return isFetchModeJoin;
		}
	}

}
