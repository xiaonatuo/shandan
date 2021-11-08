package com.keyware.shandan.browser.service;

import com.keyware.shandan.browser.entity.ViewVo;

import java.io.IOException;
import java.util.Map;

public interface ViewService {

    /**
     * @return 年度视图
     */
    ViewVo getYearViews() throws IOException;

    /**
     * @return 部队编号视图
     */
    ViewVo getTroopViews() throws IOException;

    /**
     * @return 装备型号视图
     */
    ViewVo getEquipmentViews() throws IOException;

    /**
     * @return 任务代号视图
     */
    ViewVo getTaskViews() throws IOException;

    /**
     * 获取
     *
     * @return 部门视图
     */
    ViewVo getOrgViews();
}
