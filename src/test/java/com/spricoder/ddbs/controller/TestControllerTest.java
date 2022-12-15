package com.spricoder.ddbs.controller;

import com.spricoder.ddbs.constant.MyResponse;
import com.spricoder.ddbs.constant.ResponseCode;
import com.spricoder.ddbs.form.TestForm;
import com.spricoder.ddbs.vo.TestVO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TestControllerTest {
  @Autowired MyRequestTemplate myRequestTemplate;

  @BeforeEach
  public void setupMockMvc() throws Exception {
    // do nothing
  }

  @Test
  void heartbeat() throws Exception {
    Map<String, Object> params = new HashMap<>();
    Map<String, Object> headers = new HashMap<>();

    myRequestTemplate.getTemplate("/test/heartbeat", params, ResponseCode.OK, headers);
  }

  @Test
  void testPost() throws Exception {
    Map<String, Object> headers = new HashMap<>();
    TestForm testForm = new TestForm("value");

    MyResponse response =
        myRequestTemplate.postTemplate("/test/testPost", testForm, ResponseCode.OK, headers);

    Assertions.assertTrue(response.getData() instanceof TestVO);
    TestVO testVO = (TestVO) response.getData();
    Assertions.assertEquals(testForm.getTestString(), testVO.getTestString());
  }
}
