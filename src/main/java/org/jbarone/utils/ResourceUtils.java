package org.jbarone.utils;

import java.io.File;
import java.io.IOException;

public class ResourceUtils {
  public static File loadResourceFile(String relativePath) throws IOException {
    ClassLoader classLoader = ResourceUtils.class.getClassLoader();
    return new File(classLoader.getResource(relativePath).getFile());
  }
}
