package com.spricoder.ddbs.controller;

import com.spricoder.ddbs.config.preprocess.ReplaceStreamFilter;
import com.spricoder.ddbs.constant.MyResponse;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/** @Author spricoder 标准的Mock工具类 Create by 2021/03/07 @Version 1.0 */
@Component
public class MyRequestTemplate {
  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private ReplaceStreamFilter replaceStreamFilter;

  private MockMvc mockMvc;

  private void init() {
    // 必须要替换filter
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter(replaceStreamFilter)
            .build();
  }

  /**
   * POST 模板
   *
   * @param url 请求的URL
   * @param object Post Body
   * @param expect_code 期望的返回状态码
   * @param headers 请求头
   * @return MyResponse 以供检查
   */
  public MyResponse postTemplate(
      String url, Object object, int expect_code, Map<String, Object> headers) throws Exception {

    init();
    MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
        MockMvcRequestBuilders.post(url)
            .header("Accept-Encoding", "gzip, deflate")
            .header("Connection", "keep-alive")
            .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
            .contentType(MediaType.APPLICATION_JSON);

    if (object != null) {
      String jsonResult = JSONObject.toJSONString(object);
      mockHttpServletRequestBuilder = mockHttpServletRequestBuilder.content(jsonResult);
    }

    if (headers != null) {
      for (Map.Entry<String, Object> entry : headers.entrySet()) {
        mockHttpServletRequestBuilder =
            mockHttpServletRequestBuilder.header(entry.getKey(), entry.getValue());
      }
    }

    MvcResult mvcResult =
        mockMvc
            .perform(mockHttpServletRequestBuilder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String result = mvcResult.getResponse().getContentAsString();

    MyResponse myResponse = JSONObject.parseObject(result, MyResponse.class);

    if (myResponse.getCode() != expect_code) {
      System.out.println(myResponse.getCode());
      System.out.println(
          new String(
              myResponse.getData().toString().getBytes(StandardCharsets.ISO_8859_1), "UTF-8"));
    }

    assert myResponse.getCode() == expect_code;
    return myResponse;
  }

  /**
   * GET 模板
   *
   * @param url 请求的URL
   * @param params 请求的参数对，value为String或String[]
   * @param expect_code 期望的返回状态码
   * @param headers 请求头
   * @return MyResponse 以供检查
   */
  public MyResponse getTemplate(
      String url, Map<String, Object> params, int expect_code, Map<String, Object> headers)
      throws Exception {

    init();
    MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
        MockMvcRequestBuilders.get(url)
            .accept(MediaType.APPLICATION_JSON)
            .header("Accept-Encoding", "gzip, deflate")
            .header("Connection", "keep-alive")
            .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
            .contentType("application/json;charset=UTF-8");

    if (params != null) {
      for (Map.Entry<String, Object> entry : params.entrySet()) {
        mockHttpServletRequestBuilder =
            mockHttpServletRequestBuilder.param(entry.getKey(), entry.getValue().toString());
      }
    }

    if (headers != null) {
      for (Map.Entry<String, Object> entry : headers.entrySet()) {
        mockHttpServletRequestBuilder =
            mockHttpServletRequestBuilder.header(entry.getKey(), entry.getValue());
      }
    }

    MvcResult mvcResult =
        mockMvc
            .perform(mockHttpServletRequestBuilder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String result = mvcResult.getResponse().getContentAsString();

    MyResponse myResponse = JSONObject.parseObject(result, MyResponse.class);

    if (myResponse.getCode() != expect_code) {
      System.out.println(myResponse.getCode());
      System.out.println(
          new String(
              myResponse.getData().toString().getBytes(StandardCharsets.ISO_8859_1), "UTF-8"));
    }

    assert myResponse.getCode() == expect_code;
    return myResponse;
  }
}
