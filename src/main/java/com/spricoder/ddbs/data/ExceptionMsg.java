package com.spricoder.ddbs.data;

import java.time.LocalDateTime;

public class ExceptionMsg {
  private Integer exceptionId;
  private Integer code;
  private String msg;
  private LocalDateTime happenTime;

  public Integer getExceptionId() {
    return exceptionId;
  }

  public void setExceptionId(Integer exceptionId) {
    this.exceptionId = exceptionId;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public LocalDateTime getHappenTime() {
    return happenTime;
  }

  public void setHappenTime(LocalDateTime happenTime) {
    this.happenTime = happenTime;
  }

  @Override
  public String toString() {
    return "ExceptionMsg{"
        + "exceptionId="
        + exceptionId
        + ", code="
        + code
        + ", msg='"
        + msg
        + '\''
        + ", happenTime="
        + happenTime
        + '}';
  }
}
