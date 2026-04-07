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

import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;

public class PropertyWrapperFactory {

	public static PropertyWrapper createPropertyWrapper() {
		return new PropertyWrapperImpl();
	}

	public static class PropertyWrapperImpl implements PropertyWrapper {

		private String name;
		private String propertyAccessorName;
		private String cascade = "none";
		private boolean isLazy;
		private boolean isOptional;
		private boolean isNaturalIdentifier;
		private boolean isOptimisticLocked = true;
		private boolean isInsertable = true;
		private boolean isUpdateable = true;
		private boolean isSelectable = true;
		private boolean isBackRef;

		private ValueWrapper value;
		private PersistentClassWrapper persistentClass;

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public ValueWrapper getValue() {
			return value;
		}

		@Override
		public void setValue(ValueWrapper value) {
			this.value = value;
		}

		@Override
		public void setPersistentClass(PersistentClassWrapper pc) {
			this.persistentClass = pc;
		}

		@Override
		public PersistentClassWrapper getPersistentClass() {
			return persistentClass;
		}

		@Override
		public boolean isComposite() {
			return value != null && value.isComponent();
		}

		@Override
		public String getPropertyAccessorName() {
			return propertyAccessorName;
		}

		@Override
		public void setPropertyAccessorName(String s) {
			this.propertyAccessorName = s;
		}

		@Override
		public TypeWrapper getType() {
			if (value != null) {
				return value.getType();
			}
			return null;
		}

		@Override
		public void setCascade(String s) {
			this.cascade = s;
		}

		@Override
		public String getCascade() {
			return cascade;
		}

		@Override
		public boolean isBackRef() {
			return isBackRef;
		}

		@Override
		public boolean isSelectable() {
			return isSelectable;
		}

		@Override
		public boolean isInsertable() {
			return isInsertable;
		}

		@Override
		public boolean isUpdateable() {
			return isUpdateable;
		}

		@Override
		public boolean isLazy() {
			return isLazy;
		}

		@Override
		public boolean isOptional() {
			return isOptional;
		}

		@Override
		public boolean isNaturalIdentifier() {
			return isNaturalIdentifier;
		}

		@Override
		public boolean isOptimisticLocked() {
			return isOptimisticLocked;
		}

		// --- Mutators ---

		public void setBackRef(boolean b) {
			this.isBackRef = b;
		}

		public void setSelectable(boolean b) {
			this.isSelectable = b;
		}

		public void setInsertable(boolean b) {
			this.isInsertable = b;
		}

		public void setUpdateable(boolean b) {
			this.isUpdateable = b;
		}

		public void setLazy(boolean b) {
			this.isLazy = b;
		}

		public void setOptional(boolean b) {
			this.isOptional = b;
		}

		public void setNaturalIdentifier(boolean b) {
			this.isNaturalIdentifier = b;
		}

		public void setOptimisticLocked(boolean b) {
			this.isOptimisticLocked = b;
		}
	}

}
