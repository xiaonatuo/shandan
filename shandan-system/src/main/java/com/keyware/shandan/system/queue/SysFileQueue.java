package com.keyware.shandan.system.queue;

import com.keyware.shandan.system.entity.SysFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 需要存储到ES的系统文件队列
 *
 * @author Administrator
 * @since 2021/6/30
 */
@Slf4j
@Component
public class SysFileQueue {

    /**
     * 待保存到ES的文件队列
     */
    private final BlockingQueue<SysFile> queue = new ArrayBlockingQueue<>(50);

    /**
     * 添加文件到队列
     *
     * @param file
     * @throws InterruptedException -
     */
    public void append(SysFile file) throws InterruptedException {
        queue.put(file);
    }

    /**
     * 获取队列
     *
     * @return
     */
    public BlockingQueue<SysFile> getQueue() {
        return queue;
    }
}
