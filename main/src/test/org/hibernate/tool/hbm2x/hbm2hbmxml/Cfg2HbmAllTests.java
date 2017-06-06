package org.hibernate.tool.hbm2x.hbm2hbmxml;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Cfg2HbmAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.hibernate.tool.cfg2hbm");
		//$JUnit-BEGIN$
		suite.addTest(ListArrayTest.suite());
		suite.addTest(CompositeElementTest.suite());
		suite.addTest(DynamicComponentTest.suite());
		suite.addTest(IdBagTest.suite());
		suite.addTest(Hbm2HbmXmlTest.suite());
		suite.addTest(ManyToManyTest.suite());
		suite.addTest(InheritanceTest.suite());
		suite.addTest(BackrefTest.suite());
		suite.addTest(AbstractTest.suite());
		//$JUnit-END$
		return suite;
	}

}
