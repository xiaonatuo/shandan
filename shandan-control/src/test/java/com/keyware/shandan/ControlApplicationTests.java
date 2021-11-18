package com.keyware.shandan;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ControlApplicationTests {

    /**
     * 保存数据源测试用例
     */
    public void saveDatasource(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://host:port/control/control/datasource/save";

        // 准备数据
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id","");
        map.add("name","测试");
        map.add("jdbcDriverClass","dm.jdbc.driver.DmDriver");
        map.add("jdbcUrl","jdbc:dm://localhost:5236");
        map.add("jdbcUserName","sysdba");
        map.add("jdbcPassword","*********");
        map.add("jdbcSchema","SYSDBA");
        map.add("remark","");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token", "****************");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = restTemplate.postForEntity( url, request , Object.class );

        Object dto = response.getBody();
        /*

         业务代码

         */
    }

    /**
     * 数据源查询测试用例
     */
    public void queryDatasource() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://host:port/control/control/datasource/page";

        // 准备数据
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("current", 1);
        params.add("size", 10);
        params.add("name", "测试");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token", "****************");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, request, Object.class, params);

        Object dto = response.getBody();
        /*

         业务代码

         */
    }

}
