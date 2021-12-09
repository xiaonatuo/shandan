package com.keyware.shandan.controller;

import com.keyware.shandan.beans.ClientDetailsDTO;
import com.keyware.shandan.config.OauthClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户端管理接口类
 *
 * @author GuoXin
 * @since 2021/11/30
 */
@RestController
@RequestMapping("/oauth/client")
public class ClientManagerController {

    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;

    /**
     * 客户端注册接口
     *
     * @param details 客户端详情
     * @return 响应
     */
    @PostMapping("/register")
    public ResponseEntity<Object> clientRegister(@RequestBody ClientDetailsDTO details) {
        oauthClientDetailsService.addClientDetails(details);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 客户端更新接口
     *
     * @param details 客户端信息
     * @return 响应
     */
    @PostMapping("/update")
    public ResponseEntity<Object> update(@RequestBody ClientDetailsDTO details) {
        oauthClientDetailsService.updateClientDetails(details);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 客户端删除接口
     *
     * @param clientId 客户端ID
     * @return 响应
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Object> delete(String clientId) {
        oauthClientDetailsService.removeClientDetails(clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 客户端列表接口
     *
     * @return 客户端列表
     */
    @GetMapping("/list")
    public List<ClientDetailsDTO> list() {
        return oauthClientDetailsService.list();
    }
}
