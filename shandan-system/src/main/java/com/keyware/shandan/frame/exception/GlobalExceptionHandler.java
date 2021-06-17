package com.keyware.shandan.frame.exception;

import com.keyware.shandan.common.entity.Result;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

/**
 * <p>
 * 全局异常处理类
 * </p>
 *
 * @author Administrator
 * @since 2021/6/7
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private Environment env;

    @ExceptionHandler(MultipartException.class)
    public Result<Object> exception(MaxUploadSizeExceededException e){
        String maxFileSize = env.getProperty("spring.servlet.multipart.max-file-size");
        String maxRequestSize = env.getProperty("spring.servlet.multipart.max-request-size");
        if (e.getCause().getCause() instanceof FileSizeLimitExceededException){//单个文件大小超出限制抛出的异常
            return Result.of(null, false, "单个上传文件大小不能超过" + maxFileSize);
        }else if (e.getCause().getCause() instanceof SizeLimitExceededException){//总文件大小超出限制抛出的异常
            return Result.of(null, false, "总上传文件大小不能超过200MB" + maxRequestSize);
        }
        return null;
    }
}
