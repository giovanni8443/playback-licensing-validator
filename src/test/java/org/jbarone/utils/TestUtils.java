package org.jbarone.utils;

import java.io.File;
import java.io.IOException;

public class TestUtils {
  public static File loadResourceFile(String relativePath) throws IOException {
    ClassLoader classLoader = TestUtils.class.getClassLoader();
    return new File(classLoader.getResource(relativePath).getFile());
  }
}
