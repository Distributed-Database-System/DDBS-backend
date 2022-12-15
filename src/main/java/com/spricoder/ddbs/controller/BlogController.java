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
import com.spricoder.ddbs.vo.UserVO;
import com.spricoder.ddbs.vo.request.GetArticleReq;
import com.spricoder.ddbs.vo.request.ListArticleReq;
import com.spricoder.ddbs.vo.request.ListUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
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
    public ResponseEntity<PageList<UserVO>> listUser(ListUserReq req) {
        return ResponseUtils.success(page(blogService.getUserList(req.getUid(), req.getName()), req.getPageNo(), req.getPageSize()));
    }

    @ResponseBody
    @RequestMapping(value = "/listArticle", method = RequestMethod.POST)
    public ResponseEntity<PageList<ArticleVO>> listArticle(ListArticleReq req) {
        return ResponseUtils.success(page(blogService.getArticleList(req.getAid(), req.getTitle()), req.getPageNo(), req.getPageSize()));
    }

    @ResponseBody
    @RequestMapping(value = "/getArticle", method = RequestMethod.POST)
    public ResponseEntity<ArticleDetailVO> getArticle(GetArticleReq req) {
        return ResponseUtils.success(blogService.getArticleDetail(req.getAid(), req.getUid()));
    }

    @ResponseBody
    @RequestMapping(value = "/image-resource", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageAsResource() throws IOException {
        byte[] bytes = StreamUtils.copyToByteArray(servletContext.getResourceAsStream("/image/img.png"));
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    @RequestMapping(value = "/picture/{pictureName}", method = RequestMethod.GET)
    public void queryPicture(@PathVariable String pictureName, HttpServletResponse response) {
        try {
            response.setContentType("text/javascript");
            System.out.println(response.getContentType());
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + pictureName);
            StreamUtils.copy(servletContext.getResourceAsStream("/image/img.png"), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private static <T> PageList<T> page(List<T> list, int pageNo, int pageNum) {
        List<T> subList = list.subList((pageNo - 1) * pageNum, pageNo * pageNum);
        return new PageList<>(list.size(), subList);
    }

}
