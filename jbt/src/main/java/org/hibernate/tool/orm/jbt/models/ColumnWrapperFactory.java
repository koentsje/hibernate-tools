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

import org.hibernate.tool.orm.jbt.api.wrp.ColumnWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;

public class ColumnWrapperFactory {

	public static ColumnWrapper createColumnWrapper(String name) {
		return new ColumnWrapperImpl(name);
	}

	public static class ColumnWrapperImpl implements ColumnWrapper {

		private String name;
		private Integer sqlTypeCode;
		private String sqlType;
		private Long length;
		private Integer precision;
		private Integer scale;
		private boolean nullable = true;
		private boolean unique;
		private ValueWrapper value;

		ColumnWrapperImpl(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Integer getSqlTypeCode() {
			return sqlTypeCode;
		}

		@Override
		public String getSqlType() {
			return sqlType;
		}

		@Override
		public String getSqlType(ConfigurationWrapper configurationWrapper) {
			// Without the old mapping Column, we cannot resolve the SQL type
			// from metadata. Return the explicitly set sqlType.
			return sqlType;
		}

		@Override
		public long getLength() {
			return length == null ? Integer.MIN_VALUE : length;
		}

		@Override
		public int getDefaultLength() {
			return DEFAULT_LENGTH;
		}

		@Override
		public int getPrecision() {
			return precision == null ? Integer.MIN_VALUE : precision;
		}

		@Override
		public int getDefaultPrecision() {
			return DEFAULT_PRECISION;
		}

		@Override
		public int getScale() {
			return scale == null ? Integer.MIN_VALUE : scale;
		}

		@Override
		public int getDefaultScale() {
			return DEFAULT_SCALE;
		}

		@Override
		public boolean isNullable() {
			return nullable;
		}

		@Override
		public ValueWrapper getValue() {
			return value;
		}

		@Override
		public boolean isUnique() {
			return unique;
		}

		@Override
		public void setSqlType(String sqlType) {
			this.sqlType = sqlType;
		}

		// --- Mutators ---

		public void setName(String name) {
			this.name = name;
		}

		public void setSqlTypeCode(Integer sqlTypeCode) {
			this.sqlTypeCode = sqlTypeCode;
		}

		public void setLength(Long length) {
			this.length = length;
		}

		public void setPrecision(Integer precision) {
			this.precision = precision;
		}

		public void setScale(Integer scale) {
			this.scale = scale;
		}

		public void setNullable(boolean nullable) {
			this.nullable = nullable;
		}

		public void setUnique(boolean unique) {
			this.unique = unique;
		}

		public void setValue(ValueWrapper value) {
			this.value = value;
		}
	}

}
