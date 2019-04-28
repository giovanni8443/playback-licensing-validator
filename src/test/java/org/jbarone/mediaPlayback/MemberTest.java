package org.jbarone.mediaPlayback;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import org.jbarone.utils.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MemberTest {

  @Test
  public void testFromJsonFile() throws Exception {
    File jsonFile = TestUtils.loadResourceFile("members/jsmith.json");
    Member actual = Member.fromJsonFile(jsonFile);

    assertEquals("5678", actual.getId());
    assertEquals("jsmith", actual.getUserName());
    assertEquals("Jeffery Smith", actual.getDisplayName());
    assertEquals( Member.MembershipStatus.Active, actual.getMembershipStatus());
    assertEquals(10, actual.getMaxNumberStreamsAllowed());
    assertEquals(2, actual.getMaxStreamsPerPeriodDays());
    assertEquals(0, actual.getCurrentStreamsViewedCount());
    assertEquals("US", actual.getLocale().getCountry());

    Duration duration = Duration.between(actual.getMaxStreamsExpiry(), LocalDateTime.now().plusDays(2));
    long durationSeconds = duration.getSeconds();
    assertTrue((Math.abs(durationSeconds) < 10));  // fudge factor for test execution

    assertEquals(new Integer(22), actual.getAge());
  }
}
