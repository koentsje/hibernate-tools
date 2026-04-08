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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.xml.XMLPrettyPrinter;
import org.hibernate.tool.orm.jbt.api.wrp.ArtifactCollectorWrapper;

public class ArtifactCollectorWrapperFactory {

	public static ArtifactCollectorWrapper createArtifactCollectorWrapper() {
		return new ArtifactCollectorWrapperImpl();
	}

	public static class ArtifactCollectorWrapperImpl implements ArtifactCollectorWrapper, ArtifactCollector {

		private final Map<String, List<File>> files = new HashMap<>();

		@Override
		public Object getWrappedObject() {
			return this;
		}

		@Override
		public void addFile(File file, String type) {
			files.computeIfAbsent(type, k -> new ArrayList<>()).add(file);
		}

		@Override
		public int getFileCount(String type) {
			List<File> existing = files.get(type);
			return existing == null ? 0 : existing.size();
		}

		@Override
		public Set<String> getFileTypes() {
			return files.keySet();
		}

		@Override
		public void formatFiles() {
			formatXml("xml");
			formatXml("hbm.xml");
			formatXml("cfg.xml");
		}

		@Override
		public File[] getFiles(String type) {
			List<File> existing = files.get(type);
			return existing == null ? new File[0] : existing.toArray(new File[0]);
		}

		private void formatXml(String type) {
			List<File> list = files.get(type);
			if (list != null) {
				for (File xmlFile : list) {
					try {
						XMLPrettyPrinter.prettyPrintFile(xmlFile);
					} catch (IOException e) {
						throw new RuntimeException("Could not format XML file: " + xmlFile, e);
					}
				}
			}
		}
	}

}
