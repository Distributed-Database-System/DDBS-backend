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
import com.spricoder.ddbs.data.Rank;
import com.spricoder.ddbs.data.ReadDetail;
import com.spricoder.ddbs.data.User;
import com.spricoder.ddbs.vo.ArticleDetailVO;
import com.spricoder.ddbs.vo.ArticleUpsertVO;
import com.spricoder.ddbs.vo.ArticleVO;
import com.spricoder.ddbs.vo.PageList;
import com.spricoder.ddbs.vo.ReadingVO;
import com.spricoder.ddbs.vo.UserUpsertVO;
import com.spricoder.ddbs.vo.UserVO;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogServiceImpl implements BlogService {
  @Autowired private HDFSManager hdfsManager;
  @Autowired private MongoTemplate mongoTemplate;
  private final Random random = new Random();

  @Override
  @Cacheable(value = "getUserList")
  public PageList<UserVO> getUserList(String uid, String name, int pageNo, int pageSize) {
    ArrayList<Criteria> criteria = new ArrayList<>();
    if (!uid.isEmpty()) {
      criteria.add(Criteria.where("uid").is(uid));
    }
    if (!name.isEmpty()) {
      criteria.add(Criteria.where("name").is(name));
    }
    List<User> results;
    Query query = generateQuery(criteria, pageNo, pageSize);
    results = mongoTemplate.find(query, User.class);
    return new PageList<>(
        (int) mongoTemplate.count(generateQuery(criteria), User.class),
        results.stream().map(UserVO::new).collect(Collectors.toList()));
  }

  @Override
  @Cacheable(value = "getArticleList")
  public PageList<ArticleVO> getArticleList(String aid, String title, int pageNo, int pageSize) {
    ArrayList<Criteria> criteria = new ArrayList<>();
    if (!aid.isEmpty()) {
      criteria.add(Criteria.where("aid").is(aid));
    }
    if (!title.isEmpty()) {
      criteria.add(Criteria.where("title").is(title));
    }
    List<Article> results;
    Query query = generateQuery(criteria, pageNo, pageSize);
    results = mongoTemplate.find(query, Article.class);
    return new PageList<>(
        (int) mongoTemplate.count(generateQuery(criteria), Article.class),
        results.stream().map(ArticleVO::new).collect(Collectors.toList()));
  }

  @Override
  @Cacheable(value = "getSimilarArticle")
  public PageList<ArticleVO> getSimilarArticle(
      String tag, String category, int pageNo, int pageSize) {
    List<Criteria> criteria =
        Arrays.asList(
            Criteria.where("articleTags").is(tag), Criteria.where("category").is(category));
    Query query = generateQuery(criteria, pageNo, pageSize);
    List<Article> results = mongoTemplate.find(query, Article.class);
    return new PageList<>(
        (int) mongoTemplate.count(generateQuery(criteria), Article.class),
        results.stream().map(ArticleVO::new).collect(Collectors.toList()));
  }

  @Override
  @Cacheable(value = "getReadingList")
  public PageList<ReadingVO> getReadingList(String uid, int pageNo, int pageSize) {
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
    return new PageList<>(
        (int) mongoTemplate.count(generateQuery(Criteria.where("uid").is(uid)), ReadDetail.class),
        result);
  }

  @Override
  @Cacheable(value = "getArticleDetail", condition = "#aid==1")
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
    String region = "Beijing";
    boolean alreadyRead = false;
    boolean agreeOrNot = random.nextBoolean();
    boolean commentOrNot = random.nextBoolean();
    boolean shareOrNot = random.nextBoolean();
    // update
    Query readQuery =
        generateQuery(Arrays.asList(Criteria.where("uid").is(uid), Criteria.where("aid").is(aid)));
    ReadDetail readDetail = mongoTemplate.findOne(readQuery, ReadDetail.class);
    if (readDetail == null) {
      // not read before
      readDetail = new ReadDetail();
      readDetail.setTimestamp(String.valueOf(System.currentTimeMillis()));
      readDetail.setUid(uid);
      readDetail.setAid(aid);
      readDetail.setCategory(article.getCategory());
      readDetail.setReadTimeLength(random.nextInt(100));
      readDetail.setRegion(region);
      readDetail.setAgreeOrNot(agreeOrNot ? "1" : "0");
      readDetail.setShareOrNot(shareOrNot ? "1" : "0");
      readDetail.setCommentOrNot(commentOrNot ? "1" : "0");
      readDetail.setCommentDetail(commentOrNot ? "comment" : "Not comment");
      mongoTemplate.insert(readDetail);
    } else {
      alreadyRead = true;
      Update readDetailUpdate = new Update();
      readDetailUpdate.set("timestamp", String.valueOf(System.currentTimeMillis()));
      readDetailUpdate.set("uid", uid);
      readDetailUpdate.set("aid", aid);
      readDetailUpdate.set("category", article.getCategory());
      readDetailUpdate.set("readTimeLength", random.nextInt(100));
      readDetailUpdate.set("region", readDetail.getRegion());
      readDetailUpdate.set("agreeOrNot", agreeOrNot ? "1" : "0");
      readDetailUpdate.set("shareOrNot", shareOrNot ? "1" : "0");
      readDetailUpdate.set("commentOrNot", commentOrNot ? "1" : "0");
      readDetailUpdate.set("commentDetail", commentOrNot ? "comment" : "Not comment");
      readQuery =
          generateQuery(
              Arrays.asList(
                  Criteria.where("region").is(region),
                  Criteria.where("uid").is(uid),
                  Criteria.where("aid").is(aid)));
      mongoTemplate.updateMulti(readQuery, readDetailUpdate, ReadDetail.class);
    }
    Query beReadQuery = generateQuery(Criteria.where("aid").is(aid));
    BeReadDetail beReadDetail = mongoTemplate.findOne(beReadQuery, BeReadDetail.class);
    if (beReadDetail == null) {
      // not be read before
      beReadDetail = new BeReadDetail();
      beReadDetail.setTimestamp(String.valueOf(System.currentTimeMillis()));
      beReadDetail.setAid(aid);
      beReadDetail.setCategory(article.getCategory());
      beReadDetail.read(uid);
      if (agreeOrNot) {
        beReadDetail.agree(uid);
      }
      if (shareOrNot) {
        beReadDetail.share(uid);
      }
      if (commentOrNot) {
        beReadDetail.comment(uid);
      }
      mongoTemplate.insert(beReadDetail);
    } else {
      Update beReadDetailUpdate = new Update();
      beReadDetailUpdate.set("timestamp", String.valueOf(System.currentTimeMillis()));
      beReadDetailUpdate.set("aid", aid);
      beReadDetailUpdate.set("category", article.getCategory());
      beReadDetailUpdate.set("readNum", beReadDetail.getReadNum() + 1);
      if (!alreadyRead) {
        List<String> readUidList = beReadDetail.getReadUidList();
        readUidList.add(uid);
        beReadDetailUpdate.set("readUidList", readUidList);
      }
      int agreeNum = beReadDetail.getAgreeNum();
      List<String> agreeUidList = beReadDetail.getAgreeUidList();
      if (agreeOrNot) {
        if (!agreeUidList.contains(uid)) {
          agreeNum++;
          agreeUidList.add(uid);
        }
      } else {
        if (agreeUidList.contains(uid)) {
          agreeNum--;
          agreeUidList.remove(uid);
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
        }
      } else {
        if (shareUidList.contains(uid)) {
          shareNum--;
          shareUidList.remove(uid);
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
        }
      } else {
        if (commentUidList.contains(uid)) {
          commentNum--;
          commentUidList.remove(uid);
        }
      }
      beReadDetailUpdate.set("commentNum", commentNum);
      beReadDetailUpdate.set("commentUidList", commentUidList);
      beReadQuery =
          generateQuery(
              Arrays.asList(
                  Criteria.where("aid").is(aid),
                  Criteria.where("category").is(article.getCategory())));
      mongoTemplate.updateMulti(beReadQuery, beReadDetailUpdate, BeReadDetail.class);
    }

    ArticleDetailVO articleDetailVO = new ArticleDetailVO();
    articleDetailVO.setAid(article.getAid());
    articleDetailVO.setTimestamp(article.getTimestamp());
    articleDetailVO.setTitle(article.getTitle());
    articleDetailVO.setCategory(article.getCategory());
    articleDetailVO.setArticleAbstract(article.getArticleAbstract());
    articleDetailVO.setTags(article.getArticleTags());
    articleDetailVO.setAuthors(article.getAuthors());
    articleDetailVO.setLanguage(article.getLanguage());

    String textName = article.getText();
    try (InputStream inputStream =
        hdfsManager.getFileInputStream(
            "/articles/articles/article"
                + textName.substring(6, textName.lastIndexOf('.'))
                + "/"
                + textName)) {
      StringBuilder sb = new StringBuilder();
      BufferedReader reader = null;

      try {
        reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
        String line;
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } catch (IOException e) {
        log.error("", e);
        throw new RuntimeException(e);
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            log.error("", e);
          }
        }
      }
      articleDetailVO.setText(sb.toString());
    } catch (Exception e) {
      articleDetailVO.setText(article.getText());
      log.error("Failed to get the content of text", e);
    }

    articleDetailVO.setImageList(Arrays.asList(article.getImage().split(",")));
    articleDetailVO.setVideo(article.getVideo());
    articleDetailVO.setReadNum(beReadDetail.getReadNum());
    articleDetailVO.setAgreeNum(beReadDetail.getAgreeNum());
    articleDetailVO.setCommentNum(beReadDetail.getCommentNum());
    articleDetailVO.setShareNum(beReadDetail.getShareNum());
    return articleDetailVO;
  }

  @Override
  @Cacheable(value = "getRank")
  public List<ArticleVO> getRank(String type, long timestamp) {
    String timeStr = transform(type, timestamp);
    if (timeStr.length() == 0) {
      return new ArrayList<>();
    }
    List<Criteria> criteriaList =
        Arrays.asList(
            Criteria.where("temporal_granularity").is(type),
            Criteria.where("timestamp").is(timeStr));
    Query getRankQuery = generateQuery(criteriaList);
    Rank rank = mongoTemplate.findOne(getRankQuery, Rank.class);
    if (rank == null) {
      return new ArrayList<>();
    }
    Map<String, Integer> slot = new HashMap<>();
    for (int i = 0; i < Math.min(10, rank.getArticleAids().size()); i++) {
      slot.put(rank.getArticleAids().get(i), i);
    }
    // TODO score ?
    Query articleQuery =
        generateQuery(
            Criteria.where("aid").in(rank.getArticleAids().subList(0, slot.size())), false);
    List<Article> articles = mongoTemplate.find(articleQuery, Article.class);
    ArticleVO[] res = new ArticleVO[slot.size()];
    for (int i = 0; i < slot.size(); i++) {
      res[slot.get(articles.get(i).getAid())] = new ArticleVO(articles.get(i));
    }
    return Arrays.stream(res).collect(Collectors.toList());
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
    return generateQuery(criteria, true);
  }

  private Query generateQuery(Criteria criteria, boolean orderById) {
    if (orderById) {
      return new Query(criteria).with(Sort.by(Sort.Direction.ASC, "id"));
    } else {
      return new Query(criteria);
    }
  }

  private Query generateQuery(List<Criteria> criteriaList) {
    return generateQuery(criteriaList, true);
  }

  private Query generateQuery(List<Criteria> criteriaList, boolean orderById) {
    Query query;
    if (criteriaList.size() == 0) {
      query = new Query();
    } else if (criteriaList.size() == 1) {
      query = generateQuery(criteriaList.get(0), orderById);
    } else {
      Criteria[] arr = new Criteria[criteriaList.size()];
      criteriaList.toArray(arr);
      Criteria c = new Criteria().andOperator(arr);
      query = new Query(c);
    }
    if (orderById) {
      return query.with(Sort.by(Sort.Direction.ASC, "id"));
    } else {
      return query;
    }
  }

  private Query generateQuery(Criteria criteria, int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), pageSize);
    return generateQuery(criteria, true).with(pageable);
  }

  private Query generateQuery(List<Criteria> criteriaList, int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), pageSize);
    return generateQuery(criteriaList, true).with(pageable);
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
    Article article = new Article();
    article.setId(articleUpsertVO.getAid());
    article.setTimestamp(System.currentTimeMillis() + "");
    article.setTitle(articleUpsertVO.getTitle());
    article.setCategory(articleUpsertVO.getCategory());
    article.setArticleAbstract(articleUpsertVO.getArticleAbstract());
    article.setArticleTags(articleUpsertVO.getTags());
    article.setAuthors(articleUpsertVO.getTags());
    article.setLanguage(articleUpsertVO.getLanguage());
    mongoTemplate.insert(article);
    return true;
  }

  @Override
  public boolean upsertUser(UserUpsertVO userUpsertVO) {
    User user = new User();
    user.setUid(userUpsertVO.getUid());
    user.setName(userUpsertVO.getName());
    user.setGender(userUpsertVO.getGender());
    user.setEmail(userUpsertVO.getEmail());
    user.setPhone(userUpsertVO.getPhone());
    user.setDept(userUpsertVO.getDept());
    user.setGrade(userUpsertVO.getGrade());
    user.setLanguage(userUpsertVO.getLanguage());
    user.setRegion(userUpsertVO.getRegion());
    user.setRole(userUpsertVO.getRole());
    mongoTemplate.insert(user);
    return true;
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
