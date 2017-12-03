package com.pijingzhanji.order.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.pijingzhanji.common.ResponseEntity;
import com.pijingzhanji.common.util.JsonUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 披荆斩棘
 * @date : 2017/6/22
 */
public class JacksonFilter {

    User user;

    @Before
    public void setUp () throws Exception {
        user = new User();
    }

    @Test
    public void name () throws Exception {
        final ResponseEntity empty = ResponseEntity.empty();
        final ResponseEntity ok    = ResponseEntity.ok();
        System.err.println( empty );
    }

    @Test
    public void responseEntityFields () throws Exception {
        final ResponseEntity< User > entity = ResponseEntity.ok()
                                                            .setResponseContent( user )
                                                            .setFilterFields( "*" );
        System.err.println( "entity = " + entity );
        System.err.println( "entity = " + entity.toJson() );
        System.err.println( "entity = " + entity.filterFieldsFlush() );
    }

    @Test
    public void squiggly () throws Exception {
        final String filter       = "name,address[zip]";
        ObjectMapper objectMapper = Squiggly.init( new ObjectMapper() , filter );
        System.err.println( SquigglyUtils.stringify( objectMapper , user ) );
        System.err.println( objectMapper.writeValueAsString( user ) );
        System.err.println( JsonUtils.toFilterJson( user , filter ) );
    }

    @Test
    public void responseEntity () throws Exception {
        String filter = "name,address[zip]";
        System.err.println( ResponseEntity.ok( "success" )
                                          .setResponseContent( user )
                                          .setFilterFields( filter )
                                          .toJson() );
        filter = "username,ip";
        System.err.println( ResponseEntity.ok( "success" )

                                          .add( "username" , "披荆斩棘" )
                                          .add( "password" , "123456" )
                                          .add( "ip" , "localhost" )
                                          .setFilterFields( filter )
                                          .toJson() );

        System.err.println( ResponseEntity.ok( "success" )

                                          .add( "username" , "披荆斩棘" )
                                          .add( "password" , "123456" )
                                          .add( "ip" , "localhost" ) );


    }

    @Test
    public void filter () throws Exception {
        // {'(', '-', '~', '/', '?', IDENTIFIER, '*', '**'}
        // 如果是 ""
        String filter = "";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // null
        filter = null;
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // 通配符
        filter = "*";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // 只序列号某些字段,字段以 ',' 分隔
        filter = "username,address";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // 配合通配符
        filter = "na*";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // 
        filter = "**";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // 对象字段内部
        filter = "address[province,zip]";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // 同时指定多个对象字段内部
        filter = "(address,order)[name]";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // '.' 的方式
        filter = "address.zip,address.name";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // '.' 的方式 2 
        filter = "address.zip,address[name]";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // '-' 排除字段
        filter = "-names,-username";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // '-' 排除字段
        filter = "-names,username";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // '-' 排除字段
        filter = "-names,-username,*";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // '-' 排除字段
        filter = "-names,username,*";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );
        // 正则
        filter = "~na[a-z]es~";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( user , filter ) );

        Map< String, Object > map = new HashMap<>();
        map.put( "user" , user );
        // {'(', '-', '~', '/', '?', IDENTIFIER, '*', '**'} 
        filter = "user[-names,-order.id]";
        filter = "-user.names,-order.id";
        System.err.println( filter + ",result = " + JsonUtils.toFilterJson( map , filter ) );
    }


}
