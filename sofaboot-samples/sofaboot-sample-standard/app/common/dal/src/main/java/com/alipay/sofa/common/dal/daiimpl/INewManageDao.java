/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.common.dal.daiimpl;

import com.alipay.sofa.common.dal.dai.NewManageDao;
import com.alipay.sofa.common.dal.dao.NewDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class INewManageDao implements NewManageDao {

    @Autowired
    private DataSource dataSource;

    @Override
    public int insert(NewDO newDO) throws SQLException {
        Assert.notNull(newDO);
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        return statement.executeUpdate(String.format("INSERT INTO NewsTable (AUTHOR, TITLE) VALUES('%s', '%s');", newDO.getAuthor(), newDO.getTitle()));
    }

    @Override
    public List<NewDO> query(String author) throws SQLException {
        Assert.isTrue(!StringUtils.isEmpty(author));
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM NewsTable WHERE AUTHOR='%s';", author));
        List<NewDO> answer = new LinkedList<>();
        while(resultSet.next()) {
            NewDO newDO = new NewDO();
            newDO.setAuthor(resultSet.getString(2));
            newDO.setTitle(resultSet.getString(3));
            answer.add(newDO);
        }
        return answer;
    }

    @Override
    public void delete(String author) throws SQLException{
        Assert.isTrue(!StringUtils.isEmpty(author));
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(String.format("DELETE FROM NewsTable WHERE AUTHOR='%s';", author));
    }
}