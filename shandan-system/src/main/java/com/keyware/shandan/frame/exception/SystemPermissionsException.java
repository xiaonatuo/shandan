package com.keyware.shandan.frame.exception;

/**
 * <p>
 * 系统权限异常
 * </p>
 *
 * @author Administrator
 * @since 2021/5/21
 */
public class SystemPermissionsException extends RuntimeException {
    private static final long serialVersionUID = 8326595055087948243L;

    public SystemPermissionsException() {
        super("无访问权限");
    }

    public SystemPermissionsException(String message) {
        super(message);
    }
}
