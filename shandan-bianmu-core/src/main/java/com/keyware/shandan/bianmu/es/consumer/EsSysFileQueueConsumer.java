package com.keyware.shandan.bianmu.es.consumer;

import com.keyware.shandan.bianmu.es.repository.EsSysFileRepository;
import com.keyware.shandan.common.util.PoiFileReadUtil;
import com.keyware.shandan.frame.properties.CustomProperties;
import com.keyware.shandan.system.entity.EsSysFile;
import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.queue.SysFileQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统文件上传后保存到ES库的消费者线程
 *
 * @author GuoXin
 * @since 2021/6/30
 */
@Slf4j
@Component
public class EsSysFileQueueConsumer extends Thread {

    @Autowired
    private SysFileQueue sysFileQueue;

    @Autowired
    private EsSysFileRepository esBaseRepository;

    @Autowired
    private CustomProperties customProperties;

    @Override
    public void run() {
        log.info("消费者线程启动成功");
        List<SysFile> sysFileList = new ArrayList<>();
        while (true) {
            // 开始消费队列中的数据
            int size = sysFileQueue.getQueue().drainTo(sysFileList, 1);
            if (size > 0) {
                List<EsSysFile> esFiles = transformSysFile(sysFileList);
                esBaseRepository.saveAll(esFiles);
                sysFileList.clear();// 清空列表
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.info("消费者线程执行异常", e);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将系统文件转换成带有大文本字段的ES文件实体类
     *
     * @param sysFileList
     * @return
     */
    private List<EsSysFile> transformSysFile(List<SysFile> sysFileList) {
        String pathPrefix = customProperties.getFileStorage().getPath();
        return sysFileList.stream().map(file -> {
            EsSysFile esFile = (EsSysFile) file;
            String path = pathPrefix + "/" + file.getPath();
            try {
                esFile.setText(PoiFileReadUtil.parseTextByFile(new File(path)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return esFile;
        }).collect(Collectors.toList());
    }
}
