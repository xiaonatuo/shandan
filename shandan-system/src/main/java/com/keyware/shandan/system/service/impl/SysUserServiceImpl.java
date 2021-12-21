package com.keyware.shandan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.utils.SysSettingUtil;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.service.BaseServiceImpl;
import com.keyware.shandan.common.util.*;
import com.keyware.shandan.frame.annotation.DataPermissions;
import com.keyware.shandan.system.entity.SysShortcutMenu;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.entity.SysUserRole;
import com.keyware.shandan.system.mapper.SysUserMapper;
import com.keyware.shandan.system.service.SysShortcutMenuService;
import com.keyware.shandan.system.service.SysUserRoleService;
import com.keyware.shandan.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author
 * @since 2021-05-07
 */
@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser, String> implements SysUserService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysShortcutMenuService sysShortcutMenuService;

    @Override
    public Result<Boolean> deleteById(String id) {

        String url = "";
        try{
            String result = restfulConnect(url,id);
        }catch (Exception e){
            e.printStackTrace();
            return Result.of(null,false,"删除用户信息失败");
        }
        // 删除权限关系
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(id);
        sysUserRoleService.remove(new QueryWrapper<>(userRole));


        //删除个性菜单
        SysShortcutMenu menu = new SysShortcutMenu();
        menu.setUserId(id);
        sysShortcutMenuService.remove(new QueryWrapper<>(menu));

        return super.deleteById(id);
    }

    @Override
    @DataPermissions
    public Page<SysUser> page(Page<SysUser> page, QueryWrapper<SysUser> wrapper) {
        SysUser user = wrapper.getEntity();
        boolean condition = StringUtils.isNotBlank(user.getLoginName());
        wrapper.eq("IS_DELETE", "0")
                .and(StringUtils.isNotBlank(user.getOrgId()), w->w.eq("ORG_ID", user.getOrgId()))
                .and(condition, w -> w.like(condition, "LOGIN_NAME", user.getLoginName()).or(condition).like(condition, "USER_NAME", user.getLoginName()));
        Page<SysUser> userPage = super.page(page, wrapper);
        userPage.setRecords(userPage.getRecords().stream().map(sysUser -> {
            sysUser.setPassword(null);
            return sysUser;
        }).collect(Collectors.toList()));

        return userPage;
    }

    @Override
    public Result<SysUser> findByLoginName(String username) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("LOGIN_NAME", username);
        SysUser user = super.getOne(wrapper);
        if (user == null) {
            user = new SysUser();
        }
        return Result.of(user);
    }

    @Override
    public Result<SysUser> updateOrSave(SysUser entity) throws Exception {

        //用户注册接口地址
        String url = null;
        if(Objects.isNull(entity.getUserId())){
            //insert
            url = "http://localhost:8080/rest/user/getUser/";
        }else{
            //update
            url = "http://localhost:8080/rest/user/getUser/";
        }

        try{
            String result = restfulConnect(url,entity.toString());
        }catch (Exception e){
            e.printStackTrace();
            return Result.of(null,false,"同步用户信息失败");
        }
        if (!StringUtils.isNotBlank(entity.getUserId())) {
            SysUser userCondition = new SysUser();
            userCondition.setLoginName(entity.getLoginName());
            List list = super.list(new QueryWrapper<>(userCondition));
            if (list != null && list.size() > 0) {
                return Result.of(entity, false, "用户名已存在");
            }
            //需要设置初始密码
            entity.setPassword(MD5Util.getMD5(SysSettingUtil.getCurrentSysSetting().getUserInitPassword()));
        }
        return super.updateOrSave(entity);
    }

    @Override
    public Result<SysUser> resetPassword(String userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(MD5Util.getMD5(SysSettingUtil.getCurrentSysSetting().getUserInitPassword()));
        boolean flag = super.update(new QueryWrapper<>(user));
        if (flag) {
            user.setPassword(null);
            return Result.of(user);
        }
        return null;
    }

    @Override
    public Result<Boolean> updatePassword(String oldPassword, String newPassword) {
        SysUser user = findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        boolean ok = false;
        if (user.getPassword().equals(MD5Util.getMD5(oldPassword))) {
            user.setPassword(MD5Util.getMD5(newPassword));

            user.setLastChangePwdTime(new Date());

            ok = saveOrUpdate(user);

        }

        return Result.of(ok, ok, ok ? "修改成功" : "修改失败，你输入的原密码错误！");
    }

    @Override
    public Result<SysUser> updateUser(SysUser sysUser) {
        SysUser user = findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        if (user == null) {
            return Result.of(null, false, "修改失败，用户不存在");
        }
        CopyUtil.copyNotNullProperties(sysUser, user);
        boolean ok = super.update(new QueryWrapper<>(user));
        return Result.of(user, ok, ok ? "修改成功" : "修改失败");
    }


    /**
     *  根据地址调用不同数据接口
     * @param url
     * @param parm
     * @return
     */
    public static String restfulConnect(String url,String parm){

        //返回值
        String output = null;

        if(StringUtils.isNotBlank(url)){

            try{
                URL serverUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
                // 提交模式
                connection.setRequestMethod("POST");// POST GET PUT DELETE
                //connection.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");//YWRtaW46YWRtaW4=");
                // 设置访问提交模式，表单提交
                //connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(15000);// 连接超时 单位毫秒
                connection.setReadTimeout(15000);// 读取超时 单位毫秒
                connection.setRequestMethod("POST");
                OutputStream out = connection.getOutputStream();// 获得一个输出流,向服务器写数据
                out.write(parm.getBytes());
                out.flush();
                out.close();
                if (connection.getResponseCode() != 200) {
                    throw new RuntimeException(
                            "HTTP GET Request Failed with Error code : "
                                    + connection.getResponseCode());
                }
                BufferedReader responseBuffer = new BufferedReader(
                        new InputStreamReader((connection.getInputStream())));

                System.out.println("Output from Server:  \n");
                while ((output = responseBuffer.readLine()) != null) {
                    System.out.println(output);
                }
                connection.disconnect();
            }catch (Exception e){
                e.printStackTrace();
                return null ;
            }
        }
        return output;
    }


}
