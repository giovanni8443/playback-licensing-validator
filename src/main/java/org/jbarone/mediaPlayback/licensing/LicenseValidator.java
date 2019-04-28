package org.jbarone.mediaPlayback.licensing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

  public List<String> getCurrentRuleSet() {
    return _currentRuleSet;
  }

  public void setCurrentRuleSet(List<String> ruleSet) {
    this._currentRuleSet = ruleSet;
  }

  public boolean validateForPlayback(Member member, Device device, Viewable viewable) {
    boolean isValid = false;
    List <String> ruleSet = getCurrentRuleSet();

    for (String rule : ruleSet) {
      Method method = null;
      try {
        String methodName = RuleMap.get(rule);
        method = LicenseValidator.class.getDeclaredMethod(methodName, Member.class, Device.class, Viewable.class);
      } catch (NoSuchMethodException nsme) {

      }

      boolean localIsValid = false;
      if (method != null) {
        try {
          localIsValid = (boolean)method.invoke(null, member, device, viewable);
        } catch (InvocationTargetException ite) {

        } catch (IllegalAccessException iae) {

        }
      }
      isValid = localIsValid;
      if (!localIsValid) {
        break;
      }
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
