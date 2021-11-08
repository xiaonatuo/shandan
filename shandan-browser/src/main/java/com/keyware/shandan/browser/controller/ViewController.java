package com.keyware.shandan.browser.controller;

import com.keyware.shandan.browser.entity.ViewVo;
import com.keyware.shandan.browser.enums.ViewType;
import com.keyware.shandan.browser.service.ViewService;
import com.keyware.shandan.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/views")
public class ViewController {

    @Autowired
    private ViewService viewService;

    @GetMapping("/{type}")
    public Result<ViewVo> getViews(@PathVariable String type) {
        ViewType viewType = null;
        try {
            viewType = ViewType.valueOf(type);
        } catch (Exception e) {
            return Result.of(null, false, "”type“参数传递异常：无法识别");
        }
        ViewVo view = null;
        try {
            switch (viewType) {
                case year:
                    view = viewService.getYearViews();
                    break;
                case task:
                    view = viewService.getTaskViews();
                    break;
                case troop:
                    view = viewService.getTroopViews();
                    break;
                case equipment:
                    view = viewService.getEquipmentViews();
                    break;
                case org:
                    view = viewService.getOrgViews();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.of(null, false, "获取视图异常：" + e.getMessage());
        }
        return Result.of(view);
    }

}
