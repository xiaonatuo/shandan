package com.keyware.shandan.browser.service;

import com.keyware.shandan.browser.entity.ViewVo;
import com.keyware.shandan.system.entity.SysOrg;

import java.io.IOException;
import java.util.Map;

public interface ViewService {

    /**
     * @return 年度视图
     */
    ViewVo<ViewVo.View> getYearViews() throws IOException;

    /**
     * @return 部队编号视图
     */
    ViewVo<ViewVo.View> getTroopViews() throws IOException;

    /**
     * @return 装备型号视图
     */
    ViewVo<ViewVo.View> getEquipmentViews() throws IOException;

    /**
     * @return 任务代号视图
     */
    ViewVo<ViewVo.View> getTaskViews() throws IOException;

    /**
     * 获取
     *
     * @return 部门视图
     */
    ViewVo<SysOrg> getOrgViews();
}
