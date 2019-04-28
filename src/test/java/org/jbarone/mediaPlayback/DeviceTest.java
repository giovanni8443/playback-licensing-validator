package org.jbarone.mediaPlayback;

import org.jbarone.utils.ResourceUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class DeviceTest {

  @Test
  public void testFromJsonFile() throws Exception {
    File jsonFile = ResourceUtils.loadResourceFile("devices/samsung_GS9.json");
    Device actual = Device.fromJsonFile(jsonFile);

    assertEquals("Samsung", actual.getBrandName());
    assertEquals("Galaxy S9", actual.getName());
    assertEquals(2960, actual.getMaxResolutionWidth());
    assertEquals(1440, actual.getMaxResolutionHeight());
    assertEquals(1280, actual.getMinResolutionWidth());
    assertEquals(720, actual.getMinResolutionHeight());
  }
}
