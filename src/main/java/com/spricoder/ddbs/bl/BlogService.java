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

package com.spricoder.ddbs.bl;

import com.spricoder.ddbs.data.ReadDetail;
import com.spricoder.ddbs.vo.ArticleDetailVO;
import com.spricoder.ddbs.vo.ArticleUpsertVO;
import com.spricoder.ddbs.vo.ArticleVO;
import com.spricoder.ddbs.vo.ReadingVO;
import com.spricoder.ddbs.vo.UserUpsertVO;
import com.spricoder.ddbs.vo.UserVO;

import java.io.IOException;
import java.util.List;

public interface BlogService {
  /**
   * 获取user列表，uid和name都可能为空，可能其中一个有值或都有值。都为空代表全量查询。
   *
   * @param uid
   * @param name
   * @param pageNo
   * @param pageSize
   * @return
   */
  List<UserVO> getUserList(String uid, String name, int pageNo, int pageSize);

  /**
   * 获取文章列表，aid和title都可能为空，可能其中一个有值或都有值。都为空代表全量查询。
   *
   * @param aid
   * @param title
   * @param pageNo
   * @param pageSize
   * @return
   */
  List<ArticleVO> getArticleList(String aid, String title, int pageNo, int pageSize);

  /**
   * 获取某个uid的阅读记录
   *
   * @param uid 非空
   * @param pageNo
   * @param pageSize
   * @return
   */
  List<ReadingVO> getReadingList(String uid, int pageNo, int pageSize);

  /**
   * 获取某个aid对应的具体文章，需要返回结果，并记录根据uid、aid更新read、beread和popular(?)
   *
   * @param aid 非空
   * @param uid uid
   * @return
   */
  ArticleDetailVO getArticleDetail(String aid, String uid);

  /**
   * 获取热门文章top5，就返回5篇文章就好
   *
   * @param type 简单起见我直接硬编码了，daily/weekly/monthly
   * @param timestamp 终止时间。可以参考明辉的实现，以查询月度热门文章为例： • 如果查询的月份是当前月份，则热门数据可能会变化，因此需要重新计算热门数据并将其 返回。 •
   *     如果查询的月份是历史月份，则直接返回 popular_rank 集合中最后一次计算（时间戳最大） 的数据。 （如果来不及我们就去掉timestamp，只支持实时热榜就好）
   * @return
   */
  List<ArticleVO> getRank(String type, long timestamp);

  /**
   * 从HDFS中获取图片，转为字节数组并返回 可以用 StreamUtils.copyToByteArray 将 InputStream转为byte[]，获得InputStream的方式详见坤哥
   *
   * @param pictureName 图片名
   * @return
   */
  byte[] queryPicture(String pictureName);

  /**
   * 从HDFS中获取视频，转为字节数组并返回 可以用 StreamUtils.copyToByteArray 将 InputStream转为byte[]，获得InputStream的方式详见坤哥
   *
   * @param videoName 视频名
   * @return
   */
  byte[] queryVideo(String videoName);

  /**
   * 插入或更新文章，只更新部分字段，像图片视频这种太麻烦就不做了
   *
   * @param articleUpsertVO 如果aid为空就是插入，否则就是更新
   * @return
   */
  boolean upsertArticle(ArticleUpsertVO articleUpsertVO);

  /**
   * 插入或更新用户，只更新部分字段
   *
   * @param userUpsertVO 如果uid为空就是插入，否则就是更新
   * @return
   */
  boolean upsertUser(UserUpsertVO userUpsertVO);

  /** Test connection of hdfs */
  String pingHDFS() throws IOException;

  List<ReadDetail> getReadRecord(long leftTimeStamp, long rightTimeStamp);
}
