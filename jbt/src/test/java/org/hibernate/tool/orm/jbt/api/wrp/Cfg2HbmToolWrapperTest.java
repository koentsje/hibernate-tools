package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.orm.jbt.internal.factory.Cfg2HbmToolWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PersistentClassWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Cfg2HbmToolWrapperTest {
	
	private Cfg2HbmToolWrapper wrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrapper = Cfg2HbmToolWrapperFactory.createCfg2HbmToolWrapper();
	}

	@Test
	public void testGetTagPersistentClass() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createPersistentClassWrapper(
				new RootClass(DummyMetadataBuildingContext.INSTANCE));
		assertEquals("class", wrapper.getTag(persistentClassWrapper));
	}

	@Test
	public void testGetTagProperty() throws Exception {
		PropertyWrapper propertyWrapper = PropertyWrapperFactory.createPropertyWrapper();
		Property property = (Property)propertyWrapper.getWrappedObject();
		RootClass rc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		BasicValue basicValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		basicValue.setTypeName("foobar");
		property.setValue(basicValue);
		property.setPersistentClass(rc);
		rc.setVersion(property);
		assertEquals("version", wrapper.getTag(propertyWrapper));
	}
	
}
