package com.sqd.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;


public class FileInfo {

    //用于上传完了，再启动保存文件。避免上传速度受影响
    private CountDownLatch uploadfile;
    //关闭文件通道
    private CountDownLatch closefile;

    private FileChannel fileChannel;

    public FileInfo(File file, int chunks) throws IOException {
        uploadfile = new CountDownLatch(chunks);
        closefile = new CountDownLatch(chunks);
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
    }

    public CountDownLatch getUploadfile() {
        return uploadfile;
    }

    public void setUploadfile(CountDownLatch uploadfile) {
        this.uploadfile = uploadfile;
    }

    public CountDownLatch getClosefile() {
        return closefile;
    }

    public void setClosefile(CountDownLatch closefile) {
        this.closefile = closefile;
    }

    public FileChannel getFileChannel() {
        return fileChannel;
    }

    public void setFileChannel(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }
}
