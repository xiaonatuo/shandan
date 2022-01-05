package com.keyware.shandan.system.controller;

import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import com.keyware.shandan.common.controller.BaseController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 系统文件表 前端控制器
 * </p>
 *
 * @author GuoXin
 * @since 2021-06-07
 */
@RestController
@RequestMapping("/sys/file")
public class SysFileController extends BaseController<SysFileService, SysFile, String> {

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private CustomProperties customProperties;

    @GetMapping("/layer")
    public ModelAndView fileUploadLayer() {
        return new ModelAndView("sys/file/fileUploadLayer");
    }

    @GetMapping("/view")
    public ModelAndView fileViewer(ModelAndView mav, @RequestParam String fileId) {
        mav.setViewName("sys/file/fileView");
        SysFile file = sysFileService.getById(fileId);
        file.setFileSize(Math.round(file.getFileSize() * 100) / 100d);
        mav.addObject("fileData", file);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mav.addObject("file_inputDate", sdf.format(file.getInputDate()));
        return mav;
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<SysFile> upload(MultipartFile file, SysFile fileVo) {
        try {
            return Result.of(sysFileService.uploadFiles(file, fileVo));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.of(null, false, "文件上传服务异常");
        }
    }

    /**
     * 文件下载
     *
     * @param response
     * @param fileId
     * @return
     */
    @GetMapping("/download/{fileId}")
    public String download(HttpServletResponse response, @PathVariable String fileId) {
        SysFile sysFile = sysFileService.getById(fileId);
        if (sysFile == null) {
            return "文件不存在";
        }
        String filePath = customProperties.getFileStorage().getPath() + "/" + sysFile.getPath();

        File file = new File(filePath);
        if (!file.exists()) {
            return "文件不存在";
        }

        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());

        // 解决下载文件时文件名乱码问题
        byte[] fileNameBytes = sysFile.getFileName().getBytes(StandardCharsets.UTF_8);
        String fileName = new String(fileNameBytes, 0, fileNameBytes.length, StandardCharsets.ISO_8859_1);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + sysFile.getFileSuffix());

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return "下载过程出现错误，文件流读取异常！";
        }
        return Result.of(null).toString();
    }
}
