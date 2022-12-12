package com.spricoder.ddbs.constant;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class MyResponseTest {
    @Test
    void constructorTest() {
        MyResponse myResponse1 = new MyResponse(true);
        MyResponse myResponse2 = new MyResponse(-1, "失败", "IOException");
        MyResponse myResponse3 = MyResponse.exception("异常");
        MyResponse myResponse4 = MyResponse.error("失败");
        Assert.assertEquals(2500, myResponse3.getCode());
        Assert.assertNotEquals(myResponse1.getCode(), myResponse4.getCode());
        Assert.assertEquals(myResponse2.getCode(), myResponse4.getCode());
    }

    @Test
    void checkNull() {
        MyResponse myResponse = MyResponse.checkNull(null);
        Assert.assertEquals(3, myResponse.getCode());
    }

    @Test
    void checkBoolean() {
        MyResponse myResponse = MyResponse.checkBoolean(true);
        Assert.assertEquals(0, myResponse.getCode());
    }

    @Test
    void checkForbidden() {
        MyResponse myResponse = MyResponse.checkForbidden(false);
        Assert.assertEquals(403, myResponse.getCode());
    }
}