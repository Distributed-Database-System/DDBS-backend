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
import com.spricoder.ddbs.vo.ArticleDetailVO;
import com.spricoder.ddbs.vo.ArticleVO;
import com.spricoder.ddbs.vo.PageList;
import com.spricoder.ddbs.vo.ReadingVO;
import com.spricoder.ddbs.vo.UserVO;
import com.spricoder.ddbs.vo.request.GetArticleReq;
import com.spricoder.ddbs.vo.request.GetRankReq;
import com.spricoder.ddbs.vo.request.ListArticleReq;
import com.spricoder.ddbs.vo.request.ListReadingReq;
import com.spricoder.ddbs.vo.request.ListUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private BlogService blogService;

    @ResponseBody
    @RequestMapping(value = "/listUser", method = RequestMethod.POST)
    public ResponseEntity<PageList<UserVO>> listUser(@RequestBody ListUserReq req) {
        return ResponseUtils.success(page(blogService.getUserList(req.getUid(), req.getName()), req.getPageNo(), req.getPageSize()));
    }

    @RequestMapping(value = "/listArticle", method = RequestMethod.POST)
    public ResponseEntity<PageList<ArticleVO>> listArticle(@RequestBody ListArticleReq req) {
        return ResponseUtils.success(page(blogService.getArticleList(req.getAid(), req.getTitle()), req.getPageNo(), req.getPageSize()));
    }

    @RequestMapping(value = "/getArticle", method = RequestMethod.POST)
    public ResponseEntity<ArticleDetailVO> getArticle(@RequestBody GetArticleReq req) {
        return ResponseUtils.success(blogService.getArticleDetail(req.getAid(), req.getUid()));
    }

    @RequestMapping(value = "/getReadingList", method = RequestMethod.POST)
    public ResponseEntity<PageList<ReadingVO>> listReading(@RequestBody ListReadingReq req) {
        return ResponseUtils.success(page(blogService.getReadingList(req.getUid()), req.getPageNo(), req.getPageSize()));
    }

    @RequestMapping(value = "/getRank", method = RequestMethod.POST)
    public ResponseEntity<List<ArticleVO>> gerRank(@RequestBody GetRankReq req) {
        return ResponseUtils.success(blogService.getRank(req.getType(), req.getTimestamp()));
    }

    @RequestMapping(value = "/picture/{pictureName}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable String pictureName) throws IOException {
        FileInputStream fin = new FileInputStream("/Users/chenyanze/Documents/课程资料/研一上/分布式数据库系统/作业/大作业/DDBS-backend/src/main/resources/image/img.png");
        byte[] bytes  = new byte[fin.available()];
        fin.read(bytes);
        fin.close();
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    @RequestMapping(value = "/video/{videoName}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getVideo(@PathVariable String videoName) throws IOException {
        FileInputStream fin = new FileInputStream("/Users/chenyanze/Documents/课程资料/研一上/分布式数据库系统/作业/大作业/DDBS-backend/src/main/resources/image/video_a20_video.flv");
        byte[] bytes  = new byte[fin.available()];
        fin.read(bytes);
        fin.close();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    private static <T> PageList<T> page(List<T> list, int pageNo, int pageNum) {
        List<T> subList = list.subList((pageNo - 1) * pageNum, Math.min(pageNo * pageNum,list.size()));
        return new PageList<>(list.size(), subList);
    }

}
