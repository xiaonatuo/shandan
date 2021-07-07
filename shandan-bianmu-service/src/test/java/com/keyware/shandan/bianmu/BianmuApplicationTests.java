package com.keyware.shandan.bianmu;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.bianmu.es.repository.EsSysFileRepository;
import com.keyware.shandan.system.entity.SysFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BianmuApplicationTests {

    @Autowired
    private EsSysFileRepository repository;

    //@Test
    public void contextLoads() {
        SysFile file = new SysFile();
        file.setEntityId("1234");
        file.setFileName("1234.txt");
        file.setFileType("txt");
        SysFile count = repository.save(file);
        System.out.println(JSONObject.toJSON(count));
    }
}
