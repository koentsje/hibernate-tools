package org.hibernate.tool.orm.jbt.api.wrp;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface CollectionMetadataWrapper extends Wrapper {
	
	TypeWrapper getElementType();

}