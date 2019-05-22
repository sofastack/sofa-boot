package com.alipay.sofa.service.shared;

import com.alipay.sofa.common.dal.dai.NewsManageDao;
import com.alipay.sofa.common.dal.dao.NewsDO;
import com.alipay.sofa.facade.NewsReadService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class NewsReadServiceImpl implements NewsReadService<NewsDO> {

    @SofaReference
    private NewsManageDao newManageDao;

    public List<NewsDO> read(String author) throws SQLException {
        try {
            return newManageDao.query(author);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
}