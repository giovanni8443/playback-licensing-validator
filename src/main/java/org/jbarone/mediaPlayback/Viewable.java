package org.jbarone.mediaPlayback;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Viewable {
  public static enum Rating {
    G,
    PG,
    PG13,
    R
  }

  private String _name;
  private String _description;
  private int _resolutionWidth;
  private int _resolutionHeight;
  private List<String> _allowedCountries;
  private Rating _rating;
  private int _runningTime;

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public int getResolutionWidth() {
    return _resolutionWidth;
  }

  public void setResolutionWidth(int resolutionWidth) {
    this._resolutionWidth = resolutionWidth;
  }

  public int getResolutionHeight() {
    return _resolutionHeight;
  }

  public void setResolutionHeight(int resolutionHeight) {
    this._resolutionHeight = resolutionHeight;
  }

  public String getDescription() {
    return _description;
  }

  public void setDescription(String description) {
    this._description = description;
  }

  public List<String> getAllowedCountries() {
    return _allowedCountries;
  }

  public void setAllowedCountries(List<String> allowedCountries) {
    this._allowedCountries = allowedCountries;
  }

  public Rating getRating() {
    return _rating;
  }

  public void setRating(Rating rating) {
    this._rating = rating;
  }

  public int getRunningTime() {
    return _runningTime;
  }

  public void setRunningTime(int runningTime) {
    this._runningTime = runningTime;
  }

  public static Viewable fromJsonFile(File jsonFile) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(jsonFile, Viewable.class);
  }
}
