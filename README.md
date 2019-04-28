# playback-licensing-validator

A utility API which validates whether a specified Member (user) can stream a Viewable (streamed content) on a
specified Device.

Usage:

```
LicenseValidator validator = new LicenseValidator();

boolean isValid validator.validateForPlayback(member, device, viewable);

if (isValid) {
  // stream Viewable
} else {
  // throw validation error
}
```

By default, the `LicenseValidator` runs the following validation rules in order:

```
  LicenseValidator.ONLY_ACTIVE_MEMBERS_RULE,
  LicenseValidator.STREAM_COUNT_BELOW_LIMIT_RULE,
  LicenseValidator.DEVICE_RESOLUTION_CHECK_RULE,
  LicenseValidator.ONLY_ALLOWED_LOCALES_RULE,
  LicenseValidator.ONLY_ALLOWED_RATINGS_RULE)
```

However, the number and order of the rules in the applied rule set can be overridden by calling the following API
method:

```
  validator.setCurrentRuleSet(ruleSet /* List<String> of named rules */);
```

A sample class can be run from the command line, via gradle, in order experiment with the API, like so:

```
./gradlew runValidateLicense -PappArgs="['<member>','<device>','<viewable>']"

e.g.

./gradlew runValidateLicense -PappArgs="['jsmith','samsung_GS9','StarTrekIV']"

```

(JUnit) Test cases can be run from the commandline, via gradle, like so:

```
./gradlew clean test --info
```
