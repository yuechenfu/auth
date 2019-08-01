package com.hiveel.auth.init;


import com.hiveel.auth.model.config.Memcached;
import com.hiveel.core.memcached.FolsomManagerImpl;
import com.hiveel.core.util.MemcachedUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ProjectInit implements ApplicationRunner {
    @Autowired
    private Memcached memcached;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        MemcachedUtil.setUp(new FolsomManagerImpl(), memcached.getHost(), memcached.getPort());
    }
}
