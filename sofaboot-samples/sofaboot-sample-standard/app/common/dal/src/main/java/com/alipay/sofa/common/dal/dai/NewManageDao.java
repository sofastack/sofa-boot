/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.common.dal.dai;

import com.alipay.sofa.common.dal.dao.NewDO;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public interface NewManageDao {
    /**
     * insert a new
     * @param newDO
     * @return
     */
    int insert(NewDO newDO) throws SQLException;


    /**
     * query a new
     * @param author
     * @return
     */
    List<NewDO> query(String author) throws SQLException;

    /**
     * delete a new
     * @param author
     * @return
     */
    void delete(String author) throws SQLException;
}