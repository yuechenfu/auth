package com.hiveel.auth.sdk.config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @ConditionalOnMissingBean(Encoder.class)
    @Bean
    public Encoder feignFormEncoder() {
        return new FormEncoder(new SpringEncoder(messageConverters));
    }

    @ConditionalOnMissingBean(Decoder.class)
    @Bean
    public Decoder feignDecoder(){
        return new RestDecoder();
    }
}