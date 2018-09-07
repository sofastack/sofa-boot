/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.facade;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public interface NewReadService<T> {
    /**
     * read new
     * @param author
     * @return
     */
    List<T> read(String author) throws SQLException;
}