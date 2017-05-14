package com.fonoster.rest.test;

import static junit.framework.TestCase.assertEquals;

import java.util.Set;
import javax.ws.rs.Path;
import org.junit.Test;
import org.reflections.Reflections;

/**
 * @author ecabrerar
 * @date Feb 12, 2016
 */
public class PathSetupTest {
  @Test
  public void testClassLoadWithReflection() {
    Reflections reflections = new Reflections("com.fonoster.rest");
    Set<Class<?>> r = reflections.getTypesAnnotatedWith(Path.class);
    assertEquals(11, r.size());
  }
}
