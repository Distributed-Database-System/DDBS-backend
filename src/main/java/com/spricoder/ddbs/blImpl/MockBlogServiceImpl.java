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
import com.spricoder.ddbs.vo.ArticleDetailVO;
import com.spricoder.ddbs.vo.ArticleUpsertVO;
import com.spricoder.ddbs.vo.ArticleVO;
import com.spricoder.ddbs.vo.ReadingVO;
import com.spricoder.ddbs.vo.UserUpsertVO;
import com.spricoder.ddbs.vo.UserVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockBlogServiceImpl implements BlogService {
  @Override
  public List<UserVO> getUserList(String uid, String name, int pageNo, int pageSize) {
    List<UserVO> list = new ArrayList<>();
    list.add(
        new UserVO(
            "0", "user0", "male", "email0", "phone0", "dept0", "grade0", "zh", "Beijing", "role0",
            "tag42", 15));
    list.add(
        new UserVO(
            "1",
            "user1",
            "female",
            "email1",
            "phone1",
            "dept1",
            "grade1",
            "en",
            "Hong kong",
            "role1",
            "tag29",
            99));
    list.add(
        new UserVO(
            "2", "user2", "male", "email2", "phone2", "dept2", "grade2", "zh", "Beijing", "role2",
            "tag42", 17));
    return list;
  }

  @Override
  public List<ArticleVO> getArticleList(String aid, String title, int pageNo, int pageSize) {
    List<ArticleVO> list = new ArrayList<>();
    list.add(new ArticleVO("1", "title1", "technology", "abstract of article 1", "author1417"));
    list.add(new ArticleVO("2", "title2", "technology", "abstract of article 2", "zhy"));
    list.add(new ArticleVO("3", "title3", "technology", "abstract of article 3", "cyz"));
    list.add(new ArticleVO("4", "title4", "technology", "abstract of article 4", "lly"));
    return list;
  }

  @Override
  public List<ReadingVO> getReadingList(String uid, int pageNo, int pageSize) {
    return null;
  }

  @Override
  public ArticleDetailVO getArticleDetail(String aid, String uid) {
    return new ArticleDetailVO(
        aid,
        "1506000000002",
        "title1",
        "technology",
        "abstract of article 1",
        "tags37",
        "author1417",
        "zh",
        "text_a0.txt",
        Arrays.asList("image_a7_0.jpg"),
        "video_a0_video.flv",
        12,
        123,
        1234,
        12345);
  }

  @Override
  public List<ReadingVO> getRank(String type, long timestamp) {
    return null;
  }

  @Override
  public byte[] queryPicture(String pictureName) {
    return new byte[0];
  }

  @Override
  public byte[] queryVideo(String videoName) {
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
    return "Just a mock";
  }
}
