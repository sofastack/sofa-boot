/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.controller;

import com.alipay.sofa.common.dal.dao.NewDO;
import com.alipay.sofa.facade.NewReadService;
import com.alipay.sofa.facade.NewWriteService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qilong.zql
 * @since 2.5.8
 */
@RestController
public class SampleRestController {

    @SofaReference
    private DataSource dataSource;

    @SofaReference
    private NewReadService newReadService;

    @SofaReference
    private NewWriteService newWriteService;

    /**
     * Create a news table
     * @return
     */
    @RequestMapping("/create")
    public Map<String, Object> create() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Connection cn = dataSource.getConnection();
            Statement st = cn.createStatement();
            st.execute("DROP TABLE IF EXISTS NewsTable;"
                    + "CREATE TABLE NewsTable(ID INT AUTO_INCREMENT, PRIMARY KEY (ID), AUTHOR VARCHAR(50),TITLE VARCHAR(255));");
            resultMap.put("success", true);
            resultMap.put("result", "CREATE TABLE NewsTable(ID INT AUTO_INCREMENT, PRIMARY KEY (ID), AUTHOR VARCHAR(50), TITLE VARCHAR(255))");
        } catch (Throwable throwable) {
            resultMap.put("success", false);
            resultMap.put("error", throwable.getMessage());
        }
        return resultMap;
    }

    @RequestMapping("/insert/{author}/{title}")
    public Map<String, Object> insert(@PathVariable("author") String author, @PathVariable("title") String title) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            newWriteService.addNew(author, title);
            resultMap.put("success", true);
        } catch (Throwable throwable) {
            resultMap.put("success", false);
            resultMap.put("error", throwable.getMessage());
        }
        return resultMap;
    }

    @RequestMapping("/delete/{author}")
    public Map<String, Object> delete(@PathVariable("author") String author) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            newWriteService.deleteNew(author);
            resultMap.put("success", true);
        } catch (Throwable throwable) {
            resultMap.put("success", false);
            resultMap.put("error", throwable.getMessage());
        }
        return resultMap;
    }

    @RequestMapping("/query/{author}")
    public Map<String, Object> query(@PathVariable("author") String author) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            List<NewDO> ret = newReadService.read(author);
            resultMap.put("success", true);
            resultMap.put("count", ret.size());
            int i = 0;
            for (NewDO newDO : ret) {
                resultMap.put(String.valueOf(++i), newDO.getAuthor() + "-" + newDO.getTitle());
            }
        } catch (Throwable throwable) {
            resultMap.put("success", false);
            resultMap.put("error", throwable.getMessage());
        }
        return resultMap;
    }

}