package com.hiveel.auth.sdk.config;

import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import feign.Response;
import feign.Util;
import feign.codec.StringDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 自定义Decoder处理Rest对象
 */
public class RestDecoder extends StringDecoder {
    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.status() == 404)
            return Util.emptyValueOf(type);
        if (response.body() == null)
            return null;
        String typeName = type.getTypeName();
        if (byte[].class.equals(type)) {
            return Util.toByteArray(response.body().asInputStream());
        }
        //Rest对象需要特殊处理
        if (typeName.startsWith("com.hiveel.core.model.rest.Rest")) {
            String json = Util.toString(response.body().asReader());
            return Rest.fromJson(json, BasicRestCode.class, type);
        }
        return super.decode(response, type);
    }
}
