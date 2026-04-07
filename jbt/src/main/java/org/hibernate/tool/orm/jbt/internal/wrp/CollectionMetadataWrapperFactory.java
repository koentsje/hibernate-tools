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

import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.tool.orm.jbt.api.wrp.CollectionMetadataWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.type.Type;

public class CollectionMetadataWrapperFactory {

	public static CollectionMetadataWrapper createCollectionMetadataWrapper(final CollectionPersister collectionPersister) {
		return new CollectionMetadataWrapperImpl(collectionPersister);
	}

	public static class CollectionMetadataWrapperImpl implements CollectionMetadataWrapper {

		private CollectionPersister wrappedCollectionMetadata = null;
		private TypeWrapper elementTypeWrapper = null;

		CollectionMetadataWrapperImpl(CollectionPersister collectionPersister) {
			wrappedCollectionMetadata = collectionPersister;
		}

		@Override
		public Object getWrappedObject() {
			return wrappedCollectionMetadata;
		}

		@Override
		public TypeWrapper getElementType() {
			Type elementType = wrappedCollectionMetadata.getElementType();
			if (elementTypeWrapper == null || elementTypeWrapper.getWrappedObject() != elementType) {
				elementTypeWrapper = TypeWrapperFactory.createTypeWrapper(elementType);
			}
			return elementTypeWrapper;
		}
	}

}
