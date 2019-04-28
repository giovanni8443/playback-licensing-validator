package org.jbarone.mediaPlayback.licensing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.savoirtech.logging.slf4j.json.LoggerFactory;


import org.jbarone.mediaPlayback.*;

public class LicenseValidator {
  public static String ONLY_ACTIVE_MEMBERS_RULE = "onlyActiveMembers";
  public static String STREAM_COUNT_BELOW_LIMIT_RULE = "streamCountBelowLimit";
  public static String DEVICE_RESOLUTION_CHECK_RULE = "deviceResolutionCheck";
  public static String ONLY_ALLOWED_LOCALES_RULE = "onlyAllowedLocales";
  public static String ONLY_ALLOWED_RATINGS_RULE = "onlyAllowedRatings";

  private static List<String> DefaultRuleList = LicenseValidator.getDefaultRuleSet();
  private static Map<String, String> RuleMap = LicenseValidator.getRuleMap();
  private static Map<Viewable.Rating, Integer> RatingAgeMap = LicenseValidator.getRatingAgeMap();

  private List<String> _currentRuleSet = LicenseValidator.DefaultRuleList;

  private com.savoirtech.logging.slf4j.json.logger.Logger _reportsLogger;
  private Logger _debugLogger;

  public LicenseValidator() {
    _debugLogger = LogManager.getLogger("debugLogger");

    LoggerFactory.setIncludeLoggerName(false);
    LoggerFactory.setDateFormatString("yyyy-MM-dd HH:mm:ss.SSS");

    _reportsLogger =  LoggerFactory.getLogger("reportingLogger");
  }

  public LicenseValidator(com.savoirtech.logging.slf4j.json.logger.Logger reportsLogger, Logger debugLogger) {
    this._reportsLogger = reportsLogger;
    this._debugLogger = debugLogger;
  }

  public List<String> getCurrentRuleSet() {
    return _currentRuleSet;
  }

  public void setCurrentRuleSet(List<String> ruleSet) {
    this._currentRuleSet = ruleSet;
  }

  public boolean validateForPlayback(Member member, Device device, Viewable viewable) {
    boolean isValid = false;
    List <String> ruleSet = getCurrentRuleSet();

    _debugLogger.log(Level.DEBUG, String.format("Starting license validation for playback of '%s'", viewable.getName()));

    Map<String, String> logAttrs = new HashMap<>();
    logAttrs.put("userName", member.getUserName());
    logAttrs.put("deviceBrand", device.getBrandName());
    logAttrs.put("deviceName", device.getName());
    logAttrs.put("viewableName", viewable.getName());

    _reportsLogger.info().message("validation-for-playback.begin")
      .map("validationDetails", logAttrs).log();

    for (String rule : ruleSet) {
      Method method = null;
      String methodName = RuleMap.get(rule);
      logAttrs.put("ruleName", rule);
      try {
        method = LicenseValidator.class.getDeclaredMethod(methodName, Member.class, Device.class, Viewable.class);
      } catch (NoSuchMethodException nsme) {
        _debugLogger.log(Level.ERROR, String.format("NoSuchMethodException getting method '%s'", methodName), nsme);
      }

      boolean localIsValid = false;
      if (method != null) {
        try {
          localIsValid = (boolean)method.invoke(null, member, device, viewable);
        } catch (InvocationTargetException ite) {
          _debugLogger.log(Level.ERROR, String.format("InvocationTargetException invoking method '%s'", methodName), ite);
        } catch (IllegalAccessException iae) {
          _debugLogger.log(Level.ERROR, String.format("IllegalAccessException invoking method '%s'", methodName), iae);
        }
      }
      isValid = localIsValid;
      if (!localIsValid) {
        _reportsLogger.info().message("validation-for-playback.validation.rule.failed")
          .map("validationDetails", logAttrs).log();
        logAttrs.remove("ruleName");
        break;
      }
      _reportsLogger.info().message("validation-for-playback.validation.rule.passed")
        .map("validationDetails", logAttrs).log();
      logAttrs.remove("ruleName");
    }

    if (isValid) {
      _reportsLogger.info().message("validation-for-playback.validation.passed")
        .map("validationDetails", logAttrs).log();
    }

    return isValid;
  }

  protected static boolean validateActiveMember(Member member, Device device, Viewable viewable) {
    return (member.getMembershipStatus() == Member.MembershipStatus.Active);
  }

  protected static boolean validateBelowStreamCount(Member member, Device device, Viewable viewable) {
    return (member.getCurrentStreamsViewedCount() < member.getMaxNumberStreamsAllowed());
  }

  protected static boolean validateAcceptableResolution(Member member, Device device, Viewable viewable) {
    return ((device.getMaxResolutionWidth() >= viewable.getResolutionWidth()) &&
            (device.getMaxResolutionHeight() >= viewable.getResolutionHeight()) &&
            (viewable.getResolutionWidth() >= device.getMinResolutionWidth()) &&
            (viewable.getResolutionHeight() >= device.getMinResolutionHeight()));
  }

  protected static boolean validateAllowedLocale(Member member, Device device, Viewable viewable) {
    return (viewable.getAllowedCountries().contains(member.getLocale().getCountry()));
  }

  protected static boolean validateAllowedRating(Member member, Device device, Viewable viewable) {
    int age = (member.getAge() != null) ? member.getAge() : RatingAgeMap.get(Viewable.Rating.PG13);
    return (age >= RatingAgeMap.get(viewable.getRating()));
  }

  private static List<String> getDefaultRuleSet() {
    List<String> ruleSet = new ArrayList<>();
    ruleSet.add(ONLY_ACTIVE_MEMBERS_RULE);
    ruleSet.add(STREAM_COUNT_BELOW_LIMIT_RULE);
    ruleSet.add(DEVICE_RESOLUTION_CHECK_RULE);
    ruleSet.add(ONLY_ALLOWED_LOCALES_RULE);
    ruleSet.add(ONLY_ALLOWED_RATINGS_RULE);

    return ruleSet;
  }

  private static Map<String,String> getRuleMap() {
    Map<String, String> ruleMap = new HashMap<>();
    ruleMap.put(ONLY_ACTIVE_MEMBERS_RULE, "validateActiveMember");
    ruleMap.put(STREAM_COUNT_BELOW_LIMIT_RULE, "validateBelowStreamCount");
    ruleMap.put(DEVICE_RESOLUTION_CHECK_RULE, "validateAcceptableResolution");
    ruleMap.put(ONLY_ALLOWED_LOCALES_RULE, "validateAllowedLocale");
    ruleMap.put(ONLY_ALLOWED_RATINGS_RULE, "validateAllowedRating");

    return ruleMap;
  }

  private static Map<Viewable.Rating, Integer> getRatingAgeMap() {
    Map<Viewable.Rating, Integer> ratingAgeMap = new HashMap<>();
    ratingAgeMap.put(Viewable.Rating.G, 0);
    ratingAgeMap.put(Viewable.Rating.PG, 7);
    ratingAgeMap.put(Viewable.Rating.PG13, 13);
    ratingAgeMap.put(Viewable.Rating.R, 17);

    return ratingAgeMap;
  }
}
