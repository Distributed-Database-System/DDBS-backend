package com.spricoder.ddbs.bl;

import com.spricoder.ddbs.vo.ArticleDetailVO;
import com.spricoder.ddbs.vo.ArticleVO;
import com.spricoder.ddbs.vo.PageList;
import com.spricoder.ddbs.vo.ReadingVO;
import com.spricoder.ddbs.vo.UserVO;

import java.util.List;

public interface BlogService {
    /**
     * 获取user列表，uid和name都可能为空，可能其中一个有值或都有值。都为空代表全量查询。
     * @param uid
     * @param name
     * @param pageNo 分页查询，从1开始
     * @param pageSize 每页的条目数
     * @return
     */
    PageList<UserVO> getUserList(String uid, String name, int pageNo, int pageSize);

    /**
     * 获取文章列表，aid和title都可能为空，可能其中一个有值或都有值。都为空代表全量查询。
     * @param aid
     * @param title
     * @param pageNo 分页查询，从1开始
     * @param pageSize 每页的条目数
     * @return
     */
    PageList<ArticleVO> getArticleList(String aid, String title, int pageNo, int pageSize);

    /**
     * 获取某个uid的阅读记录
     * @param uid 非空
     * @param pageNo 分页查询，从1开始
     * @param pageSize 每页的条目数
     * @return
     */
    PageList<ReadingVO> getArticleByAid(String uid, int pageNo, int pageSize);

    /**
     * 获取某个aid对应的具体文章，需要返回结果，并记录根据uid、aid更新read、beread和popular(?)
     * @param aid 非空
     * @param uid uid
     * @return
     */
    ArticleDetailVO getArticleDetail(String aid, String uid);

    /**
     * 获取热门文章top5，就返回5篇文章就好
     * @param type 简单起见我直接硬编码了，daily/weekly/monthly
     * @param timestamp 终止时间。可以参考明辉的实现，以查询月度热门文章为例：
     * • 如果查询的月份是当前月份，则热门数据可能会变化，因此需要重新计算热门数据并将其 返回。
     * • 如果查询的月份是历史月份，则直接返回 popular_rank 集合中最后一次计算（时间戳最大） 的数据。
     * （如果来不及我们就去掉timestamp，只支持实时热榜就好）
     * @return
     */
    List<ReadingVO> getRank(String type, long timestamp);

    /**
     * 从HDFS中获取图片，转为字节数组并返回
     * 可以用 StreamUtils.copyToByteArray 将 InputStream转为byte[]，获得InputStream的方式详见坤哥
     *
     * @param pictureName 图片名
     * @return
     */
    byte[] queryPicture(String pictureName);

    /**
     * 从HDFS中获取视频，转为字节数组并返回
     * 可以用 StreamUtils.copyToByteArray 将 InputStream转为byte[]，获得InputStream的方式详见坤哥
     *
     * @param videoName 视频名
     * @return
     */
    byte[] queryVideo(String videoName);
}
