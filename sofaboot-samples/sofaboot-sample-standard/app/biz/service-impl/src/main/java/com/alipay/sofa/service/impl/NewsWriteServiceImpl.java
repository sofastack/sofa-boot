package com.alipay.sofa.service.impl;

import com.alipay.sofa.common.dal.dai.NewsManageDao;
import com.alipay.sofa.common.dal.dao.NewsDO;
import com.alipay.sofa.facade.NewsWriteService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class NewsWriteServiceImpl implements NewsWriteService {

    @Autowired
    private NewsManageDao newManageDao;

    @Override
    public int addNews(String author, String title) throws SQLException {
        try {
            NewsDO newDO = new NewsDO();
            newDO.setAuthor(author);
            newDO.setTitle(title);
            return newManageDao.insert(newDO);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteNews(String author) throws SQLException {
        try {
            newManageDao.delete(author);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
}