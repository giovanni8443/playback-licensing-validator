package org.jbarone.mediaPlayback;

import org.jbarone.utils.ResourceUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ViewableTest {

  @Test
  public void testFromJsonFile() throws Exception {
    File jsonFile = ResourceUtils.loadResourceFile("viewables/StarTrekIV.json");
    Viewable actual = Viewable.fromJsonFile(jsonFile);

    assertEquals("Star Trek IV; The Voyage Home", actual.getName());
    assertTrue(actual.getDescription().startsWith("To save Earth from an alien probe"));
    assertEquals(2280, actual.getResolutionWidth());
    assertEquals(1080, actual.getResolutionHeight());
    assertEquals(Viewable.Rating.PG, actual.getRating());
    assertEquals(119, actual.getRunningTime());
    assertEquals(Arrays.asList("US", "CA", "GB", "IT"), actual.getAllowedCountries());
  }
}
