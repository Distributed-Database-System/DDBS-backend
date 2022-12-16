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
import com.spricoder.ddbs.data.Article;
import com.spricoder.ddbs.data.BeReadDetail;
import com.spricoder.ddbs.data.ReadDetail;
import com.spricoder.ddbs.data.User;
import com.spricoder.ddbs.vo.ArticleDetailVO;
import com.spricoder.ddbs.vo.ArticleUpsertVO;
import com.spricoder.ddbs.vo.ArticleVO;
import com.spricoder.ddbs.vo.ReadingVO;
import com.spricoder.ddbs.vo.UserUpsertVO;
import com.spricoder.ddbs.vo.UserVO;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {
  @Autowired private HDFSManager hdfsManager;
  @Autowired private MongoTemplate mongoTemplate;
  private final Random random = new Random();

  @Override
  public List<UserVO> getUserList(String uid, String name, int pageNo, int pageSize) {
    ArrayList<Criteria> criteria = new ArrayList<>();
    if (uid != null) {
      criteria.add(Criteria.where("uid").is(uid));
    }
    if (name != null) {
      criteria.add(Criteria.where("name").is(name));
    }
    List<User> results;
    Query query = generateQuery(criteria, pageNo, pageSize);
    results = mongoTemplate.find(query, User.class);
    return results.stream().map(UserVO::new).collect(Collectors.toList());
  }

  @Override
  public List<ArticleVO> getArticleList(String aid, String title, int pageNo, int pageSize) {
    ArrayList<Criteria> criteria = new ArrayList<>();
    if (aid != null) {
      criteria.add(Criteria.where("aid").is(aid));
    }
    if (title != null) {
      criteria.add(Criteria.where("title").is(title));
    }
    List<Article> results;
    Query query = generateQuery(criteria, pageNo, pageSize);
    results = mongoTemplate.find(query, Article.class);
    return results.stream().map(ArticleVO::new).collect(Collectors.toList());
  }

  @Override
  public List<ReadingVO> getReadingList(String uid, int pageNo, int pageSize) {
    Query findByUidQuery = generateQuery(Criteria.where("uid").is(uid), pageNo, pageSize);
    List<ReadDetail> readDetails = mongoTemplate.find(findByUidQuery, ReadDetail.class);
    List<String> aids = readDetails.stream().map(ReadDetail::getAid).collect(Collectors.toList());
    // Get the map from aid to title
    Map<String, String> aidToTitle = new HashMap<>();
    Query findByAidQuery = generateQuery(Criteria.where("aid").in(aids));
    List<Article> articles = mongoTemplate.find(findByAidQuery, Article.class);
    for (Article article : articles) {
      aidToTitle.put(article.getAid(), article.getTitle());
    }
    // generate result
    List<ReadingVO> result = new ArrayList<>();
    for (ReadDetail readDetail : readDetails) {
      ReadingVO readingVO = new ReadingVO(readDetail);
      String title = aidToTitle.get(readingVO.getAid());
      if (title == null) {
        readingVO.setTitle("No title");
      } else {
        readingVO.setTitle(aidToTitle.get(readingVO.getAid()));
      }
      result.add(readingVO);
    }
    return result;
  }

  @Override
  public ArticleDetailVO getArticleDetail(String aid, String uid) {
    // find article
    Query findByAidQuery = generateQuery(Criteria.where("aid").is(aid));
    Article article = mongoTemplate.findOne(findByAidQuery, Article.class);
    if (article == null) {
      ArticleDetailVO articleDetailVO = new ArticleDetailVO();
      articleDetailVO.setAid(aid);
      articleDetailVO.setTitle("Not find!");
      return articleDetailVO;
    }
    // update
    Query readQuery =
        generateQuery(Arrays.asList(Criteria.where("uid").is(uid), Criteria.where("aid").is(aid)));
    ReadDetail readDetail = mongoTemplate.findOne(readQuery, ReadDetail.class);
    Query beReadQuery = generateQuery(Criteria.where("aid").is(aid));
    BeReadDetail beReadDetail = mongoTemplate.findOne(beReadQuery, BeReadDetail.class);
    Update readDetailUpdate = new Update();
    Update beReadDetailUpdate = new Update();
    if (beReadDetail == null) {
      beReadDetail = new BeReadDetail();
      beReadDetailUpdate.set("aid", aid);
      beReadDetailUpdate.set("category", article.getCategory());
    }
    readDetailUpdate.set("timestamp", String.valueOf(System.currentTimeMillis()));
    beReadDetailUpdate.set("timestamp", String.valueOf(System.currentTimeMillis()));
    if (readDetail == null) {
      // not read before
      readDetailUpdate.set("id", "r" + article.getId());
      readDetailUpdate.set("uid", uid);
      readDetailUpdate.set("aid", aid);
      readDetailUpdate.set("region", random.nextBoolean() ? "Hong Kong" : "Beijing");
      readDetailUpdate.set("category", article.getCategory());
      readDetailUpdate.set("readTimeLength", random.nextInt(100));
      beReadDetailUpdate.set("readNum", beReadDetail.getReadNum() + 1);
      List<String> readUidList = beReadDetail.getReadUidList();
      readUidList.add(uid);
      beReadDetailUpdate.set("readUidList", readUidList);
    } else {
      // already read before
      beReadDetailUpdate.set("readNum", beReadDetail.getReadNum());
    }
    int agreeNum = beReadDetail.getAgreeNum();
    List<String> agreeUidList = beReadDetail.getAgreeUidList();
    if (random.nextBoolean()) {
      if (!agreeUidList.contains(uid)) {
        agreeNum++;
        agreeUidList.add(uid);
        readDetailUpdate.set("agreeOrNot", "1");
      }
    } else {
      if (agreeUidList.contains(uid)) {
        agreeNum--;
        agreeUidList.remove(uid);
        readDetailUpdate.set("agreeOrNot", "0");
      }
    }
    beReadDetailUpdate.set("agreeNum", agreeNum);
    beReadDetailUpdate.set("agreeUidList", agreeUidList);
    int shareNum = beReadDetail.getShareNum();
    List<String> shareUidList = beReadDetail.getShareUidList();
    if (random.nextBoolean()) {
      if (!shareUidList.contains(uid)) {
        shareNum++;
        shareUidList.add(uid);
        readDetailUpdate.set("shareOrNot", "1");
      }
    } else {
      if (shareUidList.contains(uid)) {
        shareNum--;
        shareUidList.remove(uid);
        readDetailUpdate.set("shareOrNot", "0");
      }
    }
    beReadDetailUpdate.set("shareNum", shareNum);
    beReadDetailUpdate.set("shareUidList", shareUidList);
    int commentNum = beReadDetail.getCommentNum();
    List<String> commentUidList = beReadDetail.getCommentUidList();
    if (random.nextBoolean()) {
      if (!commentUidList.contains(uid)) {
        commentNum++;
        commentUidList.add(uid);
        readDetailUpdate.set("commentOrNot", "1");
        readDetailUpdate.set("commentDetail", "comment");
      }
    } else {
      if (commentUidList.contains(uid)) {
        commentNum--;
        commentUidList.remove(uid);
        readDetailUpdate.set("commentOrNot", "0");
        readDetailUpdate.set("commentDetail", "Not comment");
      }
    }
    beReadDetailUpdate.set("commentNum", commentNum);
    beReadDetailUpdate.set("commentUidList", commentUidList);
    mongoTemplate.upsert(readQuery, readDetailUpdate, ReadDetail.class);
    mongoTemplate.upsert(beReadQuery, beReadDetailUpdate, BeReadDetail.class);

    ArticleDetailVO articleDetailVO = new ArticleDetailVO();
    articleDetailVO.setAid(article.getAid());
    articleDetailVO.setTimestamp(article.getTimestamp());
    articleDetailVO.setTitle(article.getTitle());
    articleDetailVO.setCategory(article.getCategory());
    articleDetailVO.setArticleAbstract(article.getArticleAbstract());
    articleDetailVO.setTags(article.getArticleTags());
    articleDetailVO.setAuthors(article.getAuthors());
    articleDetailVO.setLanguage(article.getLanguage());
    articleDetailVO.setText(article.getText());
    articleDetailVO.setImageList(Arrays.asList(article.getImage().split(",")));
    articleDetailVO.setVideo(article.getVideo());
    articleDetailVO.setReadNum(beReadDetail.getReadNum());
    articleDetailVO.setAgreeNum(beReadDetail.getAgreeNum());
    articleDetailVO.setCommentNum(beReadDetail.getCommentNum());
    articleDetailVO.setShareNum(beReadDetail.getShareNum());
    return articleDetailVO;
  }

  @Override
  public List<ArticleVO> getRank(String type, long timestamp) {
    //    String timeStr = transform(type, timestamp);
    //    if (timeStr.length() == 0) {
    //      return new ArrayList<>();
    //    }
    //    List<Criteria> criteriaList =
    //        Arrays.asList(
    //            Criteria.where("temporal_granularity").is(type),
    //            Criteria.where("timestamp").is(timeStr));
    //    Query getRankQuery = generateQuery(criteriaList);
    //    Rank rank = mongoTemplate.findOne(getRankQuery, Rank.class);
    //    if (rank == null) {
    //      return new ArrayList<>();
    //    }
    //    // TODO score ?
    //    Query articleQuery = generateQuery(Criteria.where("aid").in(rank.getArticleAids()));
    //    List<ReadDetail> articles = mongoTemplate.find(articleQuery, ReadDetail.class);
    //    return articles.stream().map(ReadingVO::new).collect(Collectors.toList());
    return null;
  }

  public static String transform(String type, long timestamp) {
    StringBuilder result = new StringBuilder("year:");
    Calendar calendar = Calendar.getInstance(Locale.getDefault(Locale.Category.FORMAT));
    calendar.setTimeInMillis(timestamp);
    result.append(calendar.get(Calendar.YEAR));
    if ("daily".equals(type)) {
      result.append("-day:");
      result.append(calendar.get(Calendar.DAY_OF_YEAR));
    } else if ("weekly".equals(type)) {
      result.append("-week:");
      result.append(calendar.get(Calendar.WEEK_OF_YEAR));
    } else if ("monthly".equals(type)) {
      result.append("-month:");
      int month = calendar.get(Calendar.MONTH) + 1;
      if (month < 10) {
        result.append("0");
      }
      result.append(calendar.get(Calendar.MONTH) + 1);
    } else {
      return "";
    }
    return result.toString();
  }

  private Query generateQuery(Criteria criteria) {
    return new Query(criteria);
  }

  private Query generateQuery(List<Criteria> criteriaList) {
    Query query;
    if (criteriaList.size() == 0) {
      query = new Query();
    } else if (criteriaList.size() == 1) {
      query = generateQuery(criteriaList.get(0));
    } else {
      Criteria[] arr = new Criteria[criteriaList.size()];
      criteriaList.toArray(arr);
      Criteria c = new Criteria().andOperator(arr);
      query = new Query(c);
    }
    return query;
  }

  private Query generateQuery(Criteria criteria, int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), pageSize);
    return generateQuery(criteria).with(pageable);
  }

  private Query generateQuery(List<Criteria> criteriaList, int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), pageSize);
    return generateQuery(criteriaList).with(pageable);
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

  @Override
  public List<ReadDetail> getReadRecord(long leftTimeStamp, long rightTimeStamp) {
    List<Criteria> criteriaList = new ArrayList<>();
    criteriaList.add(Criteria.where("timestamp").lte(String.valueOf(rightTimeStamp)));
    criteriaList.add(Criteria.where("timestamp").gte(String.valueOf(leftTimeStamp)));
    Query query = generateQuery(criteriaList);
    return mongoTemplate.find(query, ReadDetail.class);
  }
}
