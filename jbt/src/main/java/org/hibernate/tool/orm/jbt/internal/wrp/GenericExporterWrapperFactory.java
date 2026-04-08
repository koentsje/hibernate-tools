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
package org.hibernate.tool.orm.jbt.internal.wrp;

import org.hibernate.tool.orm.jbt.api.wrp.GenericExporterWrapper;

public class GenericExporterWrapperFactory {

	public static GenericExporterWrapper createGenericExporterWrapper() {
		return new GenericExporterWrapperImpl();
	}

	public static class GenericExporterWrapperImpl implements GenericExporterWrapper {

		private String filePattern;
		private String templateName;
		private String forEach;

		@Override
		public Object getWrappedObject() {
			return this;
		}

		@Override
		public void setFilePattern(String filePattern) {
			this.filePattern = filePattern;
		}

		@Override
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		@Override
		public void setForEach(String forEach) {
			this.forEach = forEach;
		}

		@Override
		public String getFilePattern() {
			return filePattern;
		}

		@Override
		public String getTemplateName() {
			return templateName;
		}

	}

}
