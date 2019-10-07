/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.common.dal.daiimpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alipay.sofa.common.dal.dai.NewsManageDao;
import com.alipay.sofa.common.dal.dao.NewsDO;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class INewsManageDao implements NewsManageDao {

    @Autowired
    private DataSource dataSource;

    public int insert(NewsDO newDO) throws SQLException {
        Assert.notNull(newDO);
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        return statement.executeUpdate(String.format(
            "INSERT INTO NewsTable (AUTHOR, TITLE) VALUES('%s', '%s');", newDO.getAuthor(),
            newDO.getTitle()));
    }

    public List<NewsDO> query(String author) throws SQLException {
        Assert.isTrue(!StringUtils.isEmpty(author));
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format(
            "SELECT * FROM NewsTable WHERE AUTHOR='%s';", author));
        List<NewsDO> answer = new LinkedList<NewsDO>();
        while (resultSet.next()) {
            NewsDO newDO = new NewsDO();
            newDO.setAuthor(resultSet.getString(2));
            newDO.setTitle(resultSet.getString(3));
            answer.add(newDO);
        }
        return answer;
    }

    public void delete(String author) throws SQLException {
        Assert.isTrue(!StringUtils.isEmpty(author));
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(String.format("DELETE FROM NewsTable WHERE AUTHOR='%s';", author));
    }
}