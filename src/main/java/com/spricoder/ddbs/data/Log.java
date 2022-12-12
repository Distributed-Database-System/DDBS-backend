package com.spricoder.ddbs.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Log {
    private String ip;
    private String method;
    private String url;
    private String args;
    private Integer code;
    private Long processTime;
    private LocalDateTime requestTime;

    @Override
    public String toString() {
        return "Log{" +
                " ip='" + ip + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", args='" + args + '\'' +
                ", code=" + code +
                ", processTime=" + processTime +
                ", requestTime=" + requestTime +
                '}';
    }
}
