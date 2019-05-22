package com.alipay.sofa.boot.examples.demo.rpc.bean;

import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.filter.FilterInvoker;

/**
 *
 * @author liangen
 * @version $Id: Filter.java, v 0.1 2018年04月09日 下午4:17 liangen Exp $
 */
public class PersonServiceFilter extends Filter {
    @Override
    public SofaResponse invoke(FilterInvoker invoker, SofaRequest request) throws SofaRpcException {

        System.out.println("PersonFilter before");
        try {
            return invoker.invoke(request);
        } finally {
            System.out.println("PersonFilter after");
        }
    }
}