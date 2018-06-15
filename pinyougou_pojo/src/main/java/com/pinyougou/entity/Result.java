package com.pinyougou.entity;

import java.io.Serializable;

/**
 * @author W
 * @version 1.0 返回结果封装
 * @description com.pinyougou.entity
 * @date 2018/6/15
 */
public class Result implements Serializable {

    private boolean success;//是否添加成功

    private String messages;//返回的信息

    public Result(boolean success, String messages) {
        this.success = success;
        this.messages = messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
