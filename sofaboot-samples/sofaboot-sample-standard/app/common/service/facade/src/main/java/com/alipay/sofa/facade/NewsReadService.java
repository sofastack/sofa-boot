package com.alipay.sofa.facade;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public interface NewsReadService<T> {
    /**
     * read new
     * @param author
     * @return
     * @throws SQLException
     */
    List<T> read(String author) throws SQLException;
}