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

import org.hibernate.tool.orm.jbt.api.wrp.Cfg2HbmToolWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.models.PersistentClassWrapperFactory.EntityKind;
import org.hibernate.tool.orm.jbt.models.ValueWrapperFactory.ValueKind;
import org.hibernate.tool.orm.jbt.models.ValueWrapperFactory.ValueWrapperImpl;

public class Cfg2HbmToolWrapperFactory {

	public static Cfg2HbmToolWrapper createCfg2HbmToolWrapper() {
		return new Cfg2HbmToolWrapperImpl();
	}

	public static class Cfg2HbmToolWrapperImpl implements Cfg2HbmToolWrapper {

		@Override
		public String getTag(PersistentClassWrapper pcw) {
			if (pcw instanceof PersistentClassWrapperFactory.PersistentClassWrapperImpl impl) {
				return switch (impl.getEntityKind()) {
					case ROOT -> "class";
					case SINGLE_TABLE_SUBCLASS -> "subclass";
					case JOINED_SUBCLASS -> "joined-subclass";
				};
			}
			return "class";
		}

		@Override
		public String getTag(PropertyWrapper pw) {
			PersistentClassWrapper pcw = pw.getPersistentClass();
			if (pcw != null) {
				PropertyWrapper version = pcw.getVersion();
				if (version != null && version == pw) {
					ValueWrapper value = pw.getValue();
					if (value != null) {
						String typeName = value.getTypeName();
						if ("timestamp".equals(typeName) || "dbtimestamp".equals(typeName)) {
							return "timestamp";
						}
					}
					return "version";
				}
			}
			ValueWrapper value = pw.getValue();
			if (value instanceof ValueWrapperImpl impl) {
				return getTagForValue(impl, pw.getPropertyAccessorName());
			}
			return "property";
		}

		private String getTagForValue(ValueWrapperImpl value, String propertyAccessorName) {
			ValueKind kind = value.getKind();
			String tag = switch (kind) {
				case SIMPLE_VALUE -> "property";
				case MANY_TO_ONE -> "many-to-one";
				case ONE_TO_ONE -> "one-to-one";
				case ONE_TO_MANY -> "one-to-many";
				case DEPENDANT_VALUE -> "property";
				case ANY -> "any";
				case COMPONENT -> "component";
				case MAP -> "map";
				case SET -> "set";
				case LIST -> "list";
				case BAG -> "bag";
				case IDENTIFIER_BAG -> "idbag";
				case ARRAY -> "array";
				case PRIMITIVE_ARRAY -> "primitive-array";
			};
			if ("component".equals(tag) && "embedded".equals(propertyAccessorName)) {
				tag = "properties";
			}
			return tag;
		}
	}

}
