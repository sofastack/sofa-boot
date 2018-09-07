/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.service.impl;

import com.alipay.sofa.common.dal.dai.NewManageDao;
import com.alipay.sofa.common.dal.dao.NewDO;
import com.alipay.sofa.facade.NewWriteService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

/**
 * @author qilong.zql
 * @sicne 2.5.0
 */
public class NewWriteServiceImpl implements NewWriteService {

    @Autowired
    private NewManageDao newManageDao;

    @Override
    public int addNew(String author, String title) throws SQLException{
        try {
            NewDO newDO = new NewDO();
            newDO.setAuthor(author);
            newDO.setTitle(title);
            return newManageDao.insert(newDO);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteNew(String author) throws SQLException{
        try {
            newManageDao.delete(author);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
}