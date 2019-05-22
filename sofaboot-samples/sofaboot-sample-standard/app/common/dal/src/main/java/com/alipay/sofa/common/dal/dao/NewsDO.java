package com.alipay.sofa.common.dal.dao;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class NewsDO {
    /**
     * News title
     */
    private String title;

    /**
     * News id
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