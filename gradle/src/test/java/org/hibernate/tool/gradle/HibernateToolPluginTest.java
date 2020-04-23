package org.hibernate.tool.gradle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

public class HibernateToolPluginTest {

	@Test
	public void hibernateToolTest(){
	    Project project = ProjectBuilder.builder().build();
	    project.getPluginManager().apply("com.baeldung.greeting");
	  
	    assertTrue(project.getPluginManager()
	      .hasPlugin("com.baeldung.greeting"));
	  
	    assertNotNull(project.getTasks().getByName("hello"));
	    
	}
	
}
