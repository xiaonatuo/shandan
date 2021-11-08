package com.keyware.shandan.browser.entity;

import com.keyware.shandan.browser.enums.ViewType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class ViewVo implements Serializable {

    private static final long serialVersionUID = -3073840524238749044L;

    /**
     * 视图类型
     */
    private ViewType type;

    public ViewVo(ViewType type) {
        this.type = type;
    }

    /**
     * 视图数据
     */
    private List<View> views;

    public void addView(View view) {
        if(Objects.isNull(views)) views = new ArrayList<>();
        views.add(view);
    }

    public static View newView(String id, String title, Long value){
        return new View(id, title, value);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class View{

        private String id;

        private String title;

        private Long value;

    }
}
