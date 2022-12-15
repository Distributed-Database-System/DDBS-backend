/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spricoder.ddbs.blImpl;

import com.spricoder.ddbs.bl.BlogService;
import com.spricoder.ddbs.config.hdfs.HDFSManager;
import com.spricoder.ddbs.mongo.ArticleRepository;
import com.spricoder.ddbs.mongo.BeReadDetailRepository;
import com.spricoder.ddbs.mongo.RankRepository;
import com.spricoder.ddbs.mongo.ReadDetailRepository;
import com.spricoder.ddbs.mongo.UserRepository;
import com.spricoder.ddbs.vo.ArticleDetailVO;
import com.spricoder.ddbs.vo.ArticleUpsertVO;
import com.spricoder.ddbs.vo.ArticleVO;
import com.spricoder.ddbs.vo.PageList;
import com.spricoder.ddbs.vo.ReadingVO;
import com.spricoder.ddbs.vo.UserUpsertVO;
import com.spricoder.ddbs.vo.UserVO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BlogServiceImpl implements BlogService {
  @Autowired
  private HDFSManager hdfsManager;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private RankRepository rankRepository;
  @Autowired
  private ReadDetailRepository readDetailRepository;
  @Autowired
  private BeReadDetailRepository beReadDetailRepository;
  
  @Override
  public PageList<UserVO> getUserList(String uid, String name, int pageNo, int pageSize) {
    return null;
  }

  @Override
  public PageList<ArticleVO> getArticleList(String aid, String title, int pageNo, int pageSize) {
    return null;
  }

  @Override
  public PageList<ReadingVO> getArticleByAid(String uid, int pageNo, int pageSize) {
    return null;
  }

  @Override
  public ArticleDetailVO getArticleDetail(String aid, String uid) {
    return null;
  }

  @Override
  public List<ReadingVO> getRank(String type, long timestamp) {
    return null;
  }

  @Override
  public byte[] queryPicture(String pictureName) {
    try {
      try (InputStream inputStream =
               hdfsManager.getFileInputStream(
                   "/articles/articles/article"
                       + pictureName.substring(7, pictureName.lastIndexOf('_'))
                       + "/"
                       + pictureName)) {
        return IOUtils.toByteArray(inputStream);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new byte[0];
  }

  @Override
  public byte[] queryVideo(String videoName) {
    try {
      try (InputStream inputStream =
               hdfsManager.getFileInputStream(
                   "/articles/articles/article"
                       + videoName.substring(7, videoName.lastIndexOf('_'))
                       + "/"
                       + videoName)) {
        return IOUtils.toByteArray(inputStream);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new byte[0];
  }

  @Override
  public boolean upsertArticle(ArticleUpsertVO articleUpsertVO) {
    return false;
  }

  @Override
  public boolean upsertUser(UserUpsertVO userUpsertVO) {
    return false;
  }

  @Override
  public String pingHDFS() throws IOException {
    return hdfsManager.createFile();
  }
}
