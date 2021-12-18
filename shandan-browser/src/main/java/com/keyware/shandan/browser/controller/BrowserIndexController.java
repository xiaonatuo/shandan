package com.keyware.shandan.browser.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keyware.shandan.bianmu.entity.DirectoryVo;
import com.keyware.shandan.bianmu.entity.MetadataBasicVo;
import com.keyware.shandan.bianmu.enums.DirectoryType;
import com.keyware.shandan.bianmu.enums.ReviewStatus;
import com.keyware.shandan.bianmu.service.DirectoryService;
import com.keyware.shandan.bianmu.service.MetadataService;
import com.keyware.shandan.browser.config.BrowserProperties;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.enums.SystemTypes;
import com.keyware.shandan.common.util.RsaUtil;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.datasource.entity.DBTableColumnVo;
import com.keyware.shandan.frame.config.security.SecurityUtil;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.entity.SysSetting;
import com.keyware.shandan.system.entity.SysUser;
import com.keyware.shandan.system.service.SysFileService;
import com.keyware.shandan.system.service.SysUserService;
import com.keyware.shandan.system.utils.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 首页 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021/6/17
 */
@RestController
@RequestMapping("/")
public class BrowserIndexController {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private BrowserProperties browserProperties;

    /**
     * 首页路由
     *
     * @param modelAndView
     * @return
     */
    @GetMapping("/index")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        modelAndView.addObject("appName", SysSettingUtil.getCurrentSysSetting().getSysName());
        //登录用户
        SysUser user = sysUserService.findByLoginName(SecurityUtil.getLoginUser().getUsername()).getData();
        user.setPassword(null);//隐藏部分属性
        modelAndView.addObject("loginUser", user);
        modelAndView.addObject("user", user);
        //系统信息
        modelAndView.addObject("sys", SysSettingUtil.getCurrentSysSetting());
        //后端公钥
        String publicKey = RsaUtil.getPublicKey();
        modelAndView.addObject("publicKey", publicKey);

        SysSetting bianmuSetting = SysSettingUtil.getSysSetting(SystemTypes.BIANMU.name());
        modelAndView.addObject("bianmuServer", bianmuSetting.getSysAddress());
        modelAndView.addObject("dbtoolAddress", browserProperties.getDbtoolAddress());
        return modelAndView;
    }

    @GetMapping("/browser")
    public ModelAndView browser(ModelAndView mov) {
        mov.setViewName("browser");
        SysSetting bianmuSetting = SysSettingUtil.getSysSetting(SystemTypes.BIANMU.name());
        mov.addObject("bianmuServer", bianmuSetting.getSysAddress());
        return mov;
    }

    /**
     * 跳转数据资源搜索页面
     *
     * @param mov    视图模型
     * @param metaId 数据资源ID
     * @return 视图模型
     */
    @GetMapping("/search/meta/{metaId}")
    public ModelAndView metaSearch(ModelAndView mov, @PathVariable String metaId) {
        mov.setViewName("meta_search");
        MetadataBasicVo meta = null;
        List<DBTableColumnVo> columnVoList = new ArrayList<>();
        if (StringUtils.isNotBlank(metaId)) {
            meta = metadataService.get(metaId).getData();
            // 查询字段列
            meta.getMetadataDetailsList().forEach(list -> columnVoList.addAll(list.getColumnList()));
        }
        mov.addObject("metadata", meta);
        mov.addObject("columns", columnVoList);
        return mov;
    }

    /**
     * 获取子级目录
     *
     * @param directory
     * @return
     */
    @GetMapping("/browser/dir/tree")
    public Result<Object> treeChildren(DirectoryVo directory) {
        directory.setParentId(StringUtils.isBlank(directory.getId()) ? "-" : directory.getId());
        directory.setId(null);
        directory.setReviewStatus(ReviewStatus.PASS);

        DirectoryVo parentDir = directoryService.getById(directory.getParentId());
        List<JSONObject> result;
        List<DirectoryVo> directoryList = directoryService.list(new QueryWrapper<>(directory));
        if (parentDir != null && parentDir.getDirectoryType() == DirectoryType.METADATA) {
            // 查询数据资源
            List<MetadataBasicVo> metadataBasicVoList = directoryService.directoryMetadata(directory.getParentId());
            result = metadataBasicVoList.stream().filter(Objects::nonNull).map(vo -> {
                JSONObject json = treeJson(vo, "metadata", true);
                json.put("id", vo.getId());
                json.put("title", StringUtils.isBlank(vo.getMetadataComment()) ? vo.getMetadataName() : vo.getMetadataComment());
                json.put("parentId", directory.getParentId());
                json.put("iconClass", "dtree-icon-sort");
                return json;
            }).collect(Collectors.toList());

            // 查询文件
            SysFile q = new SysFile();
            q.setEntityId(parentDir.getId());
            List<SysFile> files = sysFileService.list(new QueryWrapper<>(q));
            result.addAll(files.stream().map(vo -> {
                JSONObject json = treeJson(vo, "metadata", true);
                json.put("id", vo.getId());
                json.put("title", vo.getFileName() + vo.getFileSuffix());
                json.put("parentId", directory.getParentId());
                json.put("iconClass", "dtree-icon-normal-file");
                return json;
            }).collect(Collectors.toList()));
        } else {
            result = directoryList.stream().map(vo -> {
                JSONObject json = treeJson(vo, "directory", false);
                json.put("id", vo.getId());
                json.put("title", vo.getDirectoryName());
                json.put("parentId", vo.getParentId());
                if (vo.getDirectoryType() == DirectoryType.METADATA) {
                    json.put("iconClass", "dtree-icon-fenzhijigou");
                } else {
                    json.put("iconClass", "dtree-icon-wenjianjiazhankai");
                }
                return json;
            }).collect(Collectors.toList());
        }
        return Result.of(result);
    }


    /**
     * 生成树的json对象
     *
     * @param vo
     * @param type
     * @param last
     * @return
     */
    private JSONObject treeJson(Object vo, String type, Boolean last) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("spread", false);
        json.put("last", last);
        json.put("basicData", JSON.toJSON(vo));
        json.put("children", new ArrayList<>());
        return json;
    }
}
