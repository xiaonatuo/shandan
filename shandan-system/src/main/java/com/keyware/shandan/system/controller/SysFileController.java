package com.keyware.shandan.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.keyware.shandan.common.controller.BaseController;
import com.keyware.shandan.common.entity.Result;
import com.keyware.shandan.common.util.StringUtils;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.entity.SysFileChunk;
import com.keyware.shandan.system.service.SysFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping("/layer/dir")
    public ModelAndView fileUploadLayerDir() {
        return new ModelAndView("sys/file/dirUploadLayer");
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
     * 根据文件的MD5值判断文件是否已经上传
     * 由于MD5值是仅是基于文件内容得出的值，并不包括文件名称，所以需要在根据文件名称判断，是否需要以新的文件名称重新保存一条记录到数据库
     *
     * @param fileMd5 文件的MD5值，用于判断文件是否已经上传
     * @param name    文件名
     * @return 结果集
     */
    @PostMapping("/upload/check")
    public Result<Object> uploadFileCheck(@RequestBody String fileMd5,
                                          @RequestBody String name) {
        JSONObject data = new JSONObject();

        List<SysFile> files = sysFileService.getFilesByMd5(fileMd5);
        if (files.size() == 0) {
            data.put("exists", false);
        } else {
            data.put("file", files.stream().filter(SysFile::isFirst).findFirst().orElse(null));
            data.put("name_exists", files.stream().anyMatch(file -> file.getFileName().equals(name)));
        }
        return Result.of(data);
    }

    /**
     * 大文件分片上传
     *
     * @param file      分片文件
     * @param fileChunk 分片数据
     * @return 结果集
     */
    @PostMapping("/upload/chunk")
    public Result<Object> uploadChunk(MultipartFile file, SysFile fileInfo, SysFileChunk fileChunk) {
        try {
            SysFile sysFile = sysFileService.uploadFileChunk(file, fileInfo, fileChunk);
            return Result.of(sysFile);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.of(null, false, "上传失败");
        }
    }

    /**
     * 复制文件信息并保存到指定目录
     *
     * @param fileId
     * @param target
     * @return
     * @throws Exception
     */
    @PostMapping("/copy")
    public Result<Object> fileCopy(@RequestBody String fileId,
                                   @RequestBody String target) throws Exception {
        if (StringUtils.isNotBlank(fileId)) {
            SysFile file = sysFileService.getById(fileId);
            SysFile file2 = new SysFile();
            BeanUtils.copyProperties(file, file2);
            file2.setId(null);
            file2.setIsFirst(false);
            save(file2);
            return Result.of(file2);
        }
        return Result.of(null, false);
    }

    /**
     * 文件分片上传完成，需要自动生成目录结构，并保存目录文件关系
     *
     * @param fileId       上传的文件ID
     * @param currentDirId 当前目录ID
     * @param fileFullName 文件的全路径名称
     * @return 结果
     */
    @PostMapping("/upload/chunk/complete")
    public Result<Object> mergerChunk(@RequestBody String fileId,
                                      @RequestBody String currentDirId,
                                      @RequestBody String fileFullName) throws Exception {
        boolean flag = sysFileService.autoCreateDirAndUpdateFile(fileId, currentDirId, fileFullName);
        return Result.of(flag, flag);
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
