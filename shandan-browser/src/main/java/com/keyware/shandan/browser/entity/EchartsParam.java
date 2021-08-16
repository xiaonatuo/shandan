package com.keyware.shandan.browser.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class EchartsParam implements Serializable {
    private static final long serialVersionUID = 9070268981665321988L;

    private String title;

    private String remark;

    private String fieldX;

    private String filedY;

    private String aggregationType;

    private List<Map<String, Object>> data;

    private String image;
}
