### 打包编译:
- 清理
``` 
./gradlew clean
```
- 打包sdk
```
./gradlew jar
```
- 安装到Maven本地库
```
./gradlew publishToMavenLocal
```

- 上传sdk
```
./gradlew publish
```

- 打包web 应用
```
./gradlew web:bootJar
```

### co:
```
./co.sh
```
OR 第一次
```
chmod +x co.sh
./co.sh
```

### 使用sdk:
- build.gradle 加入依赖
``` 
compile 'com.hiveel:upload-sdk:1.0.19'
```
- yml配置文件 upload服务地址
```
autossav:
  accountServer: http://127.0.0.1:9081/upload
```
- yml配置文件 开启连接错误检查
```
feign:
  hystrix:
    enabled: true

```
- yml配置文件 配置连接upload服务超时
```
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 300000

```
- 使用代码示例

``` 

package com.hiveel.autotrace.manager;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.auth.sdk.service.AccountService;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthManager {
    @Autowired
    private AccountService accountService;

    public Account findAccountByToken(String token){
        //**********************************************
        Rest<Account> rest = accountService.me(token);
         //**********************************************
        if(BasicRestCode.SUCCESS != rest.getCode()){
            return Account.NULL;
        }
        return rest.getData();
    }
}

``` 