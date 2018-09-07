/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.service.shared;

import com.alipay.sofa.common.dal.dai.NewManageDao;
import com.alipay.sofa.common.dal.dao.NewDO;
import com.alipay.sofa.facade.NewReadService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class NewReadServiceImpl implements NewReadService<NewDO> {

    @SofaReference
    private NewManageDao newManageDao;

    public List<NewDO> read(String author) throws SQLException{
        try {
            return newManageDao.query(author);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
}