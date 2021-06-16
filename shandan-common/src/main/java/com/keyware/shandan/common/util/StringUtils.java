package com.keyware.shandan.common.util;


import java.util.Arrays;

public class StringUtils extends org.springframework.util.StringUtils {
    /**
     * 检查给定的String是否包含实际文本。
     * 更具体地说，如果String不为null ，其长度大于0，并且包含至少一个非空白字符，则此方法返回true 。
     * @param text 要检查的String （可以为null ）
     * @return 如果String不为null ，其长度大于0，并且不仅包含空格，则返回true
     */
    public static Boolean isBlank(String text){
        return !isNotBlank(text);
    }

    /**
     * 检查给定的String是否包含实际文本。
     * 更具体地说，如果String不为null ，其长度大于0，并且包含至少一个非空白字符，则此方法返回true 。
     * @param text 要检查的String （可以为null ）
     * @return 如果String不为null ，其长度大于0，并且不仅包含空格，则返回true
     */
    public static Boolean isNotBlank(String text) {
        return hasText(text);
    }

    /**
     * 检查多个字符串是否包含空字符串
     * @param texts 要检查的字符串
     * @return 如果参数中包含空字符串则返回true
     */
    public static Boolean isBlankAny(String ...texts){
        return Arrays.stream(texts).anyMatch(StringUtils::isBlank);
    }
}
