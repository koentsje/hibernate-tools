package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Map;

public interface SessionFactoryWrapper extends Wrapper {

	void close();
	Map<String, ClassMetadataWrapper> getAllClassMetadata();
	Map<String, CollectionMetadataWrapper> getAllCollectionMetadata();
	SessionWrapper openSession();
	ClassMetadataWrapper getClassMetadata(String s);
	ClassMetadataWrapper getClassMetadata(Class<?> c);
	CollectionMetadataWrapper getCollectionMetadata(String s);

}
