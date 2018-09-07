/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.facade;

import java.sql.SQLException;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public interface NewWriteService {
    /**
     * add new
     * @param title
     * @return
     */
    int addNew(String author, String title) throws SQLException;

    /**
     * delete new
     * @param author
     */
    void deleteNew(String author)throws SQLException;
}