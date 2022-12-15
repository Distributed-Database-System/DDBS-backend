package com.spricoder.ddbs.metric.utils;

public enum Tag {
  TYPE,
  NAME,
  REGION,
  STATUS;

  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }
}
