package com.keyware.shandan.system.utils;

import java.util.Objects;

public class StringUtil {

    public static String stringReplace(String str) {
        //去掉" "号
        if(!Objects.isNull(str) && "null".equals(str)){
          str = null;
        }
        return str ;
    }
}
