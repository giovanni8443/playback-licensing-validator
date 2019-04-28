package org.jbarone.mediaPlayback;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Member {
  public static enum MembershipStatus {
    Active,
    Inactive,
    Banned
  }

  private String _id;
  private String _userName;
  private String _displayName;
  private MembershipStatus _membershipStatus = MembershipStatus.Inactive;
  private int _maxNumberStreamsAllowed;
  private int _maxStreamsPerPeriodDays;
  private int _currentStreamsViewedCount;
  private LocalDateTime _maxStreamsExpiry;
  private Locale _locale;
  private Integer _age;

  public String getId() {
    return _id;
  }

  public void setId(String id) {
    this._id = id;
  }
  public String getUserName() {
    return _userName;
  }

  public void setUserName(String userName) {
    _userName = userName;
  }

  public String getDisplayName() {
    return _displayName;
  }

  public void setDisplayName(String displayName) {
    _displayName = displayName;
  }

  public MembershipStatus getMembershipStatus() {
    return _membershipStatus;
  }

  public void setMembershipStatus(MembershipStatus membershipStatus) {
    this._membershipStatus = membershipStatus;
  }

  public int getMaxNumberStreamsAllowed() {
    return _maxNumberStreamsAllowed;
  }

  public void setMaxNumberStreamsAllowed(int maxNumberStreamsAllowed) {
    this._maxNumberStreamsAllowed = maxNumberStreamsAllowed;
  }

  public int getMaxStreamsPerPeriodDays() {
    return _maxStreamsPerPeriodDays;
  }

  public void setMaxStreamsPerPeriodDays(int maxStreamsPerPeriodDays) {
    this._maxStreamsPerPeriodDays = maxStreamsPerPeriodDays;
  }

  public int getCurrentStreamsViewedCount() {
    return _currentStreamsViewedCount;
  }

  public void setCurrentStreamsViewedCount(int currentStreamsViewedCount) {
    this._currentStreamsViewedCount = currentStreamsViewedCount;
  }

  public LocalDateTime getMaxStreamsExpiry() {
    LocalDateTime maxStreamsExpiry = _maxStreamsExpiry;
    if (maxStreamsExpiry == null) {
      maxStreamsExpiry = LocalDateTime.now().plusDays(getMaxStreamsPerPeriodDays());
    }
    return maxStreamsExpiry;
  }

  public void setMaxStreamsExpiry(LocalDateTime maxStreamsExpiry) {
    this._maxStreamsExpiry = maxStreamsExpiry;
  }

  public Locale getLocale() {
    return _locale;
  }

  public void setLocale(Locale locale) {
    this._locale = locale;
  }

  public Integer getAge() {
    return _age;
  }

  public void setAge(Integer age) {
    this._age = age;
  }

  public static Member fromJsonFile(File jsonFile) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(jsonFile, Member.class);
  }
}
