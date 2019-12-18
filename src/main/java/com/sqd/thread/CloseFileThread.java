package com.sqd.thread;

import com.sqd.file.FileInfo;
import com.sqd.util.Comm;

import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;

public class CloseFileThread implements Runnable{

    private String uuid;

    private FileChannel out;

    private CountDownLatch countDownLatch;

    public CloseFileThread(String uuid) {
        this.uuid = uuid;
    }

    public void getFileInfo()  {
        if(Comm.FILE_MAP.containsKey(uuid)){
            FileInfo fileInfo = Comm.FILE_MAP.get(uuid);
            out = fileInfo.getFileChannel();
            countDownLatch = fileInfo.getClosefile();
        }else {
           throw new NullPointerException();
        }
    }

    @Override
    public void run() {
        getFileInfo();
        try {
            countDownLatch.await();
            out.close();
            Comm.FILE_MAP.remove(uuid);
            System.out.println("清除了！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
