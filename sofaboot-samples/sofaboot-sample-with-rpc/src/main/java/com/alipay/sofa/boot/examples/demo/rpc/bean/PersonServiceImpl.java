package com.alipay.sofa.boot.examples.demo.rpc.bean;

/**
 *
 * @author liangen
 * @version $Id: PersonImpl.java, v 0.1 2018年04月09日 下午3:32 liangen Exp $
 */
public class PersonServiceImpl implements PersonService {

    @Override
    public String sayName(String string) {
        return "hi " + string + "!";
    }
}