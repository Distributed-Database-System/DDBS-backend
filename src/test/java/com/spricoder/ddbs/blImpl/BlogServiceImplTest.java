package com.spricoder.ddbs.blImpl;

import com.spricoder.ddbs.data.ReadDetail;
import com.spricoder.ddbs.vo.ArticleDetailVO;
import com.spricoder.ddbs.vo.ArticleVO;
import com.spricoder.ddbs.vo.ReadingVO;
import com.spricoder.ddbs.vo.UserVO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BlogServiceImplTest {
  @Autowired BlogServiceImpl blogService;

  private final int pageNo = 0;
  private final int pageSize = 20;

  @Test
  void getUserList() {
    List<UserVO> users = blogService.getUserList(null, null, pageNo, pageSize);
    Assertions.assertEquals(pageSize, users.size());
    UserVO userVO = users.get(0);
    users = blogService.getUserList(userVO.getUid(), null, pageNo, pageSize);
    Assertions.assertEquals(1, users.size());
    users = blogService.getUserList(null, userVO.getName(), pageNo, pageSize);
    Assertions.assertEquals(1, users.size());
    users = blogService.getUserList(userVO.getUid(), userVO.getName(), pageNo, pageSize);
    Assertions.assertEquals(1, users.size());
  }

  @Test
  void getArticleList() {
    List<ArticleVO> articleVOS = blogService.getArticleList(null, null, pageNo, pageSize);
    Assertions.assertEquals(pageSize, articleVOS.size());
    ArticleVO articleVO = articleVOS.get(0);
    articleVOS = blogService.getArticleList(articleVO.getAid(), null, pageNo, pageSize);
    Assertions.assertEquals(1, articleVOS.size());
    articleVOS = blogService.getArticleList(null, articleVO.getTitle(), pageNo, pageSize);
    Assertions.assertEquals(1, articleVOS.size());
    articleVOS =
        blogService.getArticleList(articleVO.getAid(), articleVO.getTitle(), pageNo, pageSize);
    Assertions.assertEquals(1, articleVOS.size());
  }

  @Test
  void getReadingList() {
    List<ReadingVO> readingVOS = blogService.getReadingList("88", pageNo, pageSize);
    Assertions.assertNotEquals(0, readingVOS.size());
  }

  @Test
  void getArticleDetail() {
    ArticleDetailVO articleVO = blogService.getArticleDetail("40", "22");
    Assertions.assertNotNull(articleVO);
    Assertions.assertNotEquals("Not find!", articleVO.getTitle());
  }

  @Test
  void getRank() {
    List<ReadingVO> readingVOS = blogService.getRank("daily", 1506347897000L);
    Assertions.assertNotEquals(0, readingVOS.size());
    readingVOS = blogService.getRank("weekly", 1506347897000L);
    Assertions.assertNotEquals(0, readingVOS.size());
    readingVOS = blogService.getRank("monthly", 1506347897000L);
    Assertions.assertNotEquals(0, readingVOS.size());
  }

  @Test
  void getReadRecord() {
    List<ReadDetail> readDetails = blogService.getReadRecord(1506334607000L, 1506334717000L);
    Assertions.assertNotEquals(0, readDetails.size());
  }
}
