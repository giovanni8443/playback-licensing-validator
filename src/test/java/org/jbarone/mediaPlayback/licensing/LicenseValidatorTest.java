package org.jbarone.mediaPlayback.licensing;

import java.io.File;
import java.util.*;

import org.jbarone.mediaPlayback.Device;
import org.jbarone.mediaPlayback.Member;
import org.jbarone.mediaPlayback.Viewable;
import org.jbarone.utils.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LicenseValidatorTest {

  @Test
  public void testValidateActiveMember() throws Exception {
    Member member = new Member();
    member.setMembershipStatus(Member.MembershipStatus.Active);

    Device device = new Device();
    Viewable viewable = new Viewable();

    assertTrue(LicenseValidator.validateActiveMember(member, device, viewable));

    member.setMembershipStatus(Member.MembershipStatus.Inactive);
    assertFalse(LicenseValidator.validateActiveMember(member, device, viewable));

    member.setMembershipStatus(Member.MembershipStatus.Banned);
    assertFalse(LicenseValidator.validateActiveMember(member, device, viewable));
  }

  @Test
  public void testValidateBelowStreamCount() throws Exception {
    Member member = new Member();
    member.setMaxNumberStreamsAllowed(10);
    member.setCurrentStreamsViewedCount(3);

    Device device = new Device();
    Viewable viewable = new Viewable();

    assertTrue(LicenseValidator.validateBelowStreamCount(member, device, viewable));

    member.setCurrentStreamsViewedCount(10);
    assertFalse(LicenseValidator.validateBelowStreamCount(member, device, viewable));

    member.setCurrentStreamsViewedCount(13);
    assertFalse(LicenseValidator.validateBelowStreamCount(member, device, viewable));
  }

  @Test
  public void testValidateAcceptableResolution() throws Exception {
    Member member = new Member();
    Device device = new Device();
    device.setMaxResolutionWidth(1280);
    device.setMaxResolutionHeight(720);
    device.setMinResolutionWidth(720);
    device.setMinResolutionHeight(480);

    Viewable viewable = new Viewable();
    viewable.setResolutionWidth(1280);
    viewable.setResolutionHeight(720);

    assertTrue(LicenseValidator.validateAcceptableResolution(member, device, viewable));

    viewable.setResolutionHeight(800);
    assertFalse(LicenseValidator.validateAcceptableResolution(member, device, viewable));

    viewable.setResolutionHeight(720);
    viewable.setResolutionWidth(1440);
    assertFalse(LicenseValidator.validateAcceptableResolution(member, device, viewable));

    viewable.setResolutionWidth(705);
    assertFalse(LicenseValidator.validateAcceptableResolution(member, device, viewable));

    viewable.setResolutionWidth(1280);
    viewable.setResolutionHeight(475);
    assertFalse(LicenseValidator.validateAcceptableResolution(member, device, viewable));
  }

  @Test
  public void testValidateAllowedLocale() throws Exception {
    Locale aLocale = new Locale("fr", "CA");
    Locale bLocale = new Locale("en", "US");
    Locale cLocale = new Locale("en", "GB");
    Locale dLocale = new Locale("jb", "JP");

    Member member = new Member();
    member.setLocale(aLocale);

    Device device = new Device();

    Viewable viewable = new Viewable();
    viewable.setAllowedCountries(Arrays.asList("CA", "US", "GB"));

    assertTrue(LicenseValidator.validateAllowedLocale(member, device, viewable));

    member.setLocale(bLocale);
    assertTrue(LicenseValidator.validateAllowedLocale(member, device, viewable));

    member.setLocale(cLocale);
    assertTrue(LicenseValidator.validateAllowedLocale(member, device, viewable));

    member.setLocale(dLocale);
    assertFalse(LicenseValidator.validateAllowedLocale(member, device, viewable));
  }

  @Test
  public void testValidateAllowedRating() throws Exception {
    Member member = new Member();
    member.setAge(1);

    Device device = new Device();

    Viewable viewable = new Viewable();
    viewable.setRating(Viewable.Rating.G);
    assertTrue(LicenseValidator.validateAllowedRating(member, device, viewable));

    viewable.setRating(Viewable.Rating.PG);
    assertFalse(LicenseValidator.validateAllowedRating(member, device, viewable));

    member.setAge(7);
    assertTrue(LicenseValidator.validateAllowedRating(member, device, viewable));

    viewable.setRating(Viewable.Rating.PG13);
    assertFalse(LicenseValidator.validateAllowedRating(member, device, viewable));

    member.setAge(13);
    assertTrue(LicenseValidator.validateAllowedRating(member, device, viewable));

    viewable.setRating(Viewable.Rating.R);
    assertFalse(LicenseValidator.validateAllowedRating(member, device, viewable));

    member.setAge(17);
    assertTrue(LicenseValidator.validateAllowedRating(member, device, viewable));

    // anonymous setting
    member = new Member();
    viewable.setRating(Viewable.Rating.PG13);
    assertTrue(LicenseValidator.validateAllowedRating(member, device, viewable));

    viewable.setRating(Viewable.Rating.R);
    assertFalse(LicenseValidator.validateAllowedRating(member, device, viewable));
  }

  @Test
  public void testValidateForPlayback_baseRuleSet() throws Exception {
    LicenseValidator validator = new LicenseValidator();

    File jsmithFile  = TestUtils.loadResourceFile("members/jsmith.json");
    Member jsmith = Member.fromJsonFile(jsmithFile);

    File sGS9File = TestUtils.loadResourceFile("devices/samsung_GS9.json");
    Device sGS9 = Device.fromJsonFile(sGS9File);

    File anonymousFile = TestUtils.loadResourceFile("members/anonymous.json");
    Member anonymous = Member.fromJsonFile(anonymousFile);

    File stIVFile = TestUtils.loadResourceFile("viewables/StarTrekIV.json");
    Viewable stIV = Viewable.fromJsonFile(stIVFile);

    // jsmith
    assertTrue(validator.validateForPlayback(jsmith, sGS9, stIV));

    // anonymous
    assertTrue(validator.validateForPlayback(anonymous, sGS9, stIV));

    File tbradyFile = TestUtils.loadResourceFile("members/tbrady.json");
    Member tbrady = Member.fromJsonFile(tbradyFile);

    File schListFile = TestUtils.loadResourceFile("viewables/SchindlersList.json");
    Viewable schList = Viewable.fromJsonFile(schListFile);

    // Jsmith is old enough
    assertTrue(validator.validateForPlayback(jsmith, sGS9, schList));

    // Tbrady is too young
    assertFalse(validator.validateForPlayback(tbrady, sGS9, schList));

    // anonymous is too young for R
    assertFalse(validator.validateForPlayback(anonymous, sGS9, schList));

    File bwilsonFile = TestUtils.loadResourceFile("members/bwilson.json");
    Member bwilson = Member.fromJsonFile(bwilsonFile);

    // BWilson is inactive AND too young
    assertFalse(validator.validateForPlayback(bwilson, sGS9, schList));

    // reorder the rules
    List<String> reorderedRuleSet = Arrays.asList(LicenseValidator.ONLY_ALLOWED_RATINGS_RULE,
                                                  LicenseValidator.ONLY_ACTIVE_MEMBERS_RULE,
                                                  LicenseValidator.ONLY_ALLOWED_LOCALES_RULE,
                                                  LicenseValidator.STREAM_COUNT_BELOW_LIMIT_RULE,
                                                  LicenseValidator.DEVICE_RESOLUTION_CHECK_RULE);
    validator.setCurrentRuleSet(reorderedRuleSet);

    // base test case
    assertTrue(validator.validateForPlayback(jsmith, sGS9, stIV));

    // Jsmith is still old enough
    assertTrue(validator.validateForPlayback(jsmith, sGS9, schList));

    // Tbrady is still too young
    assertFalse(validator.validateForPlayback(tbrady, sGS9, schList));

    // BWilson is still too young AND inactive
    assertFalse(validator.validateForPlayback(bwilson, sGS9, schList));
  }

  @Test
  public void testValidateForPlayback_dynamicRuleSet() throws Exception {
    LicenseValidator validator = new LicenseValidator();

    // omit the age/rating rule
    List<String> dynamicRuleSet = Arrays.asList(LicenseValidator.ONLY_ACTIVE_MEMBERS_RULE,
      LicenseValidator.ONLY_ALLOWED_LOCALES_RULE,
      LicenseValidator.STREAM_COUNT_BELOW_LIMIT_RULE,
      LicenseValidator.DEVICE_RESOLUTION_CHECK_RULE);

    validator.setCurrentRuleSet(dynamicRuleSet);

    File jsmithFile  = TestUtils.loadResourceFile("members/jsmith.json");
    Member jsmith = Member.fromJsonFile(jsmithFile);

    File sGS9File = TestUtils.loadResourceFile("devices/samsung_GS9.json");
    Device sGS9 = Device.fromJsonFile(sGS9File);

    File tbradyFile = TestUtils.loadResourceFile("members/tbrady.json");
    Member tbrady = Member.fromJsonFile(tbradyFile);

    File bwilsonFile = TestUtils.loadResourceFile("members/bwilson.json");
    Member bwilson = Member.fromJsonFile(bwilsonFile);

    File anonymousFile = TestUtils.loadResourceFile("members/anonymous.json");
    Member anonymous = Member.fromJsonFile(anonymousFile);

    File schListFile = TestUtils.loadResourceFile("viewables/SchindlersList.json");
    Viewable schList = Viewable.fromJsonFile(schListFile);

    // Jsmith is old enough
    assertTrue(validator.validateForPlayback(jsmith, sGS9, schList));

    // Tbrady is too young, but shhh
    assertTrue(validator.validateForPlayback(tbrady, sGS9, schList));

    // anonymous is likely too young, but oh well
    assertTrue(validator.validateForPlayback(anonymous, sGS9, schList));

    // BWilson is inactive
    assertFalse(validator.validateForPlayback(bwilson, sGS9, schList));
  }
}
