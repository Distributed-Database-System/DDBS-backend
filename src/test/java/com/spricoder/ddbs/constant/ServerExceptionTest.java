package com.spricoder.ddbs.constant;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ServerExceptionTest {
    @Test(expected = ServerException.class)
    public void constructorTest1(){
        throw new ServerException();
    }

    @Test(expected = ServerException.class)
    public void constructorTest2(){
        throw new ServerException(-1, "失败");
    }

    @Test(expected = ServerException.class)
    public void constructorTest3(){
        ServerException serverException = new ServerException("失败", -1, "失败");
        Assert.assertEquals("失败", serverException.getMessage());
        Assert.assertEquals("失败", serverException.getMsg());
        Assert.assertEquals(-1, serverException.getCode());

        serverException.setCode(-2);
        serverException.setMsg("new error");
        Assert.assertEquals("new error", serverException.getMsg());
        Assert.assertEquals(-2, serverException.getCode());
        throw serverException;
    }

    @Test(expected = ServerException.class)
    public void constructorTest4(){
        throw new ServerException("失败", new IOException(), -1, "失败");
    }

    @Test(expected = ServerException.class)
    public void constructorTest5(){
        throw new ServerException(new IOException(), -1, "失败");
    }

    @Test(expected = ServerException.class)
    public void constructorTest6(){
        throw new ServerException("失败", new IOException(), false, false, -1, "失败");
    }
}