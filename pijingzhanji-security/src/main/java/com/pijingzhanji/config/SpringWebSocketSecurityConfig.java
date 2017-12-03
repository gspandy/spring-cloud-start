package com.pijingzhanji.config;

import com.pijingzhanji.websocket.JwtWebSocketInterceptorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * @author : 披荆斩棘
 * @date : 2017/6/8
 */
@Configuration
public class SpringWebSocketSecurityConfig {

    @Bean
    public ChannelInterceptor channelInterceptor () {
        return new JwtWebSocketInterceptorAdapter();
    }
}
