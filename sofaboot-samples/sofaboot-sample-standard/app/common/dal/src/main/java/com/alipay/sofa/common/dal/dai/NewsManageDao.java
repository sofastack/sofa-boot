package com.alipay.sofa.common.dal.dai;

import com.alipay.sofa.common.dal.dao.NewsDO;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public interface NewsManageDao {

    /**
     * insert a new
     * @param newDO
     * @return
     * @throws SQLException
     */
    int insert(NewsDO newDO) throws SQLException;

    /**
     * query new according to author
     * @param author
     * @return
     * @throws SQLException
     */
    List<NewsDO> query(String author) throws SQLException;

    /**
     * delete new according to author
     * @param author
     * @throws SQLException
     */
    void delete(String author) throws SQLException;
}