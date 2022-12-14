package com.spricoder.ddbs.config.hdfs;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class HDFSManager {

  private static final String hdfsDirPath = "/hdfs-test/";
  private static final String hdfsFilePath = hdfsDirPath + "wordCountTest.txt";
  private static final String hdfsFileContent = "HELLO HDFS";

  private final FileSystem fs;

  public HDFSManager(FileSystem fs) {
    this.fs = fs;
  }

  public String createFile() throws IOException {
    Path path = new Path(hdfsFilePath);
    System.out.println(path);
    FSDataOutputStream outputStream = fs.create(path);
    byte[] buff = hdfsFileContent.getBytes();
    outputStream.write(buff, 0, buff.length);
    outputStream.hflush();
    System.out.println(fs.getFileStatus(path));
    return fs.getFileStatus(path).toString();
  }

  public InputStream getFileInputStream(String hdfsFilePath) throws IOException {
    return fs.open(new Path(hdfsFilePath));
  }
}
