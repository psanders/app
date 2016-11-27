package com.fonoster.rest.test;

import org.junit.Test;
import org.reflections.Reflections;

import javax.ws.rs.Path;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 *
 * @author ecabrerar
 * @date   Feb 12, 2016
 */
public class PathSetupTest {

	@Test
	public void testClassLoadWithReflection() {

		Reflections reflections = new Reflections("com.fonoster.rest");

		Set<Class<?>> r = reflections.getTypesAnnotatedWith(Path.class);
		
		assertEquals(10, r.size());
	}
}
