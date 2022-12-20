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
package com.spricoder.ddbs.controller;

import com.spricoder.ddbs.bl.BlogService;
import com.spricoder.ddbs.util.ResponseUtils;
import com.spricoder.ddbs.vo.*;
import com.spricoder.ddbs.vo.request.GetArticleReq;
import com.spricoder.ddbs.vo.request.GetRankReq;
import com.spricoder.ddbs.vo.request.ListArticleReq;
import com.spricoder.ddbs.vo.request.ListReadingReq;
import com.spricoder.ddbs.vo.request.ListSimilarArticleReq;
import com.spricoder.ddbs.vo.request.ListUserReq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {

  @Autowired private ServletContext servletContext;

  @Autowired private BlogService blogService;

  @RequestMapping(value = "/listUser", method = RequestMethod.POST)
  public ResponseEntity<PageList<UserVO>> listUser(@RequestBody ListUserReq req) {
    return ResponseUtils.success(
        blogService.getUserList(req.getUid(), req.getName(), req.getPageNo(), req.getPageSize()));
  }

  @RequestMapping(value = "/listArticle", method = RequestMethod.POST)
  public ResponseEntity<PageList<ArticleVO>> listArticle(@RequestBody ListArticleReq req) {
    return ResponseUtils.success(
        blogService.getArticleList(
            req.getAid(), req.getTitle(), req.getPageNo(), req.getPageSize()));
  }

  @RequestMapping(value = "/listArticleByTag", method = RequestMethod.POST)
  public ResponseEntity<PageList<ArticleVO>> listArticleByTag(
      @RequestBody ListSimilarArticleReq req) {
    return ResponseUtils.success(
        blogService.getSimilarArticle(
            req.getTag(), req.getCategory(), req.getPageNo(), req.getPageSize()));
  }

  @RequestMapping(value = "/getArticle", method = RequestMethod.POST)
  public ResponseEntity<ArticleDetailVO> getArticle(@RequestBody GetArticleReq req) {
    return ResponseUtils.success(blogService.getArticleDetail(req.getAid(), req.getUid()));
  }

  @RequestMapping(value = "/getReadingList", method = RequestMethod.POST)
  public ResponseEntity<PageList<ReadingVO>> listReading(@RequestBody ListReadingReq req) {
    return ResponseUtils.success(
        blogService.getReadingList(req.getUid(), req.getPageNo(), req.getPageSize()));
  }

  @RequestMapping(value = "/getRank", method = RequestMethod.POST)
  public ResponseEntity<List<ArticleVO>> gerRank(@RequestBody GetRankReq req) {
    return ResponseUtils.success(blogService.getRank(req.getType(), req.getTimestamp()));
  }

  @RequestMapping(value = "/picture/{pictureName}", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getImage(@PathVariable String pictureName) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .body(blogService.queryPicture(pictureName));
  }

  @RequestMapping(value = "/video/{videoName}", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getVideo(@PathVariable String videoName) {
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(blogService.queryVideo(videoName));
  }

  @RequestMapping(value = "/upsert/article", method = RequestMethod.POST)
  public ResponseEntity<Boolean> upsertArticle(@RequestBody ArticleUpsertVO articleUpsertVO) {
    return ResponseEntity.ok(blogService.upsertArticle(articleUpsertVO));
  }

  @RequestMapping(value = "/upsert/user", method = RequestMethod.POST)
  public ResponseEntity<Boolean> upsertUser(@RequestBody UserUpsertVO userUpsertVO) {
    return ResponseEntity.ok(blogService.upsertUser(userUpsertVO));
  }
}
