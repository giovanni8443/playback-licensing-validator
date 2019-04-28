package org.jbarone.mediaPlayback;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Device {
  private String _brandName;
  private String _name;
  private int _maxResolutionWidth;
  private int _maxResolutionHeight;
  private int _minResolutionWidth;
  private int _minResolutionHeight;

  public String getBrandName() {
    return _brandName;
  }

  public void setBrandName(String brandName) {
    _brandName = brandName;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public static Device fromJsonFile(File jsonFile) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(jsonFile, Device.class);
  }

  public int getMaxResolutionWidth() {
    return _maxResolutionWidth;
  }

  public void setMaxResolutionWidth(int maxResolutionWidth) {
    this._maxResolutionWidth = maxResolutionWidth;
  }

  public int getMaxResolutionHeight() {
    return _maxResolutionHeight;
  }

  public void setMaxResolutionHeight(int maxResolutionHeight) {
    this._maxResolutionHeight = maxResolutionHeight;
  }

  public int getMinResolutionWidth() {
    return _minResolutionWidth;
  }

  public void setMinResolutionWidth(int minResolutionWidth) {
    this._minResolutionWidth = minResolutionWidth;
  }

  public int getMinResolutionHeight() {
    return _minResolutionHeight;
  }

  public void setMinResolutionHeight(int minResolutionHeight) {
    this._minResolutionHeight = minResolutionHeight;
  }
}
