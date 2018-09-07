/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.common.dal.dao;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class NewDO {
    /**
     * News title
     */
    private String title;

    /**
     * New id
     */
    private String author;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}