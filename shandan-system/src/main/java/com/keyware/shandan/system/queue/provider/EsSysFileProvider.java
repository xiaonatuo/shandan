package com.keyware.shandan.system.queue.provider;

import com.keyware.shandan.system.entity.SysFile;
import com.keyware.shandan.system.queue.SysFileQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统文件保存到ES的生产者
 *
 * @author Administrator
 * @since 2021/6/30
 */
@Component
public class EsSysFileProvider {

    @Autowired
    private SysFileQueue sysFileQueue;

    /**
     * 追加到队列
     *
     * @param file -
     */
    public void appendQueue(SysFile file) {
        try {
            sysFileQueue.append(file);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 追加到队列
     *
     * @param files -
     */
    public void appendQueue(List<SysFile> files) {
        try {
            sysFileQueue.append(files);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
