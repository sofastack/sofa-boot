package com.alipay.sofa.facade;

import java.sql.SQLException;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public interface NewsWriteService {
    /**
     * add a new
     * @param author
     * @param title
     * @return
     * @throws SQLException
     */
    int addNews(String author, String title) throws SQLException;

    /**
     * delete a new
     * @param author
     * @throws SQLException
     */
    void deleteNews(String author) throws SQLException;
}