package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.junit.jupiter.api.Test;

public class TypeRegistryTest {
	
	@Test
	public void testGetType() {
		assertEquals("boolean", TypeRegistry.getType("boolean").getName());
		assertEquals("byte", TypeRegistry.getType("byte").getName());
		assertEquals("big_integer", TypeRegistry.getType("big_integer").getName());
		assertEquals("short", TypeRegistry.getType("short").getName());
		assertEquals("calendar", TypeRegistry.getType("calendar").getName());
		assertEquals("calendar_date", TypeRegistry.getType("calendar_date").getName());
		assertEquals("integer", TypeRegistry.getType("integer").getName());
		assertEquals("big_decimal", TypeRegistry.getType("big_decimal").getName());
		assertEquals("character", TypeRegistry.getType("character").getName());
		assertEquals("class", TypeRegistry.getType("class").getName());
		assertEquals("currency", TypeRegistry.getType("currency").getName());
		assertEquals("date", TypeRegistry.getType("date").getName());
		assertEquals("double", TypeRegistry.getType("double").getName());
		assertEquals("float", TypeRegistry.getType("float").getName());
		assertEquals("locale", TypeRegistry.getType("locale").getName());
		assertEquals("long", TypeRegistry.getType("long").getName());
		assertEquals("string", TypeRegistry.getType("string").getName());
		assertEquals("text", TypeRegistry.getType("text").getName());
		assertEquals("time", TypeRegistry.getType("time").getName());
		assertEquals("timestamp", TypeRegistry.getType("timestamp").getName());
		assertEquals("timezone", TypeRegistry.getType("timezone").getName());
		assertEquals("true_false", TypeRegistry.getType("true_false").getName());
		assertEquals("yes_no", TypeRegistry.getType("yes_no").getName());
		assertNull(TypeRegistry.TYPE_REGISTRY.get(String.class.getName()));
		TypeWrapper stringWrapper = TypeRegistry.getType(String.class.getName());
		assertNotNull(stringWrapper);
		assertSame(TypeRegistry.TYPE_REGISTRY.get(String.class.getName()), stringWrapper);
		assertNull(TypeRegistry.TYPE_REGISTRY.get(Foo.class.getName()));
		TypeWrapper fooWrapper = TypeRegistry.getType(Foo.class.getName());
		assertNull(fooWrapper);
		assertNull(TypeRegistry.TYPE_REGISTRY.get(Foo.class.getName()));
	}
	
	private class Foo {}

}
