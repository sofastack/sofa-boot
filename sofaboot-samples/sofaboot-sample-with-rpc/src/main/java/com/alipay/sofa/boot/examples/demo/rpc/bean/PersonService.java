package com.alipay.sofa.boot.examples.demo.rpc.bean;

import javax.ws.rs.*;

/**
 *
 * @author liangen
 * @version $Id: Person.java, v 0.1 2018年04月09日 下午3:30 liangen Exp $
 */
@Path("/webapi/rest/person")
@Consumes("application/json;charset=UTF-8")
@Produces("application/json;charset=UTF-8")
public interface PersonService {

    @GET
    @Path("/sayName/{string}")
    String sayName(@PathParam("string") String string);

}
