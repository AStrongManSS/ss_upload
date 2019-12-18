package com.sqd.thread;

import com.sqd.file.FileInfo;
import com.sqd.util.Comm;
import com.sqd.util.IOUtil;
import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;

public class FilePieceThread implements Runnable {


    private String uuid;
    //要保存的文件
    private File file;
    //文件总块数
    private int chunks;
    //文件数据开始位置
    private long start;
    //文件数据结束位置
    private long end;
    //文件块
    private FileItem fileItem;
    //第几块
    private int chunk;

    //文件输出通道
    private FileChannel out;

    //结束计数器,用于关闭文件输出通道
    private CountDownLatch closeFile;

    private CountDownLatch uploadFile;


    public FilePieceThread(String uuid, long start, long end, FileItem fileItem, int chunk) {
        this.uuid = uuid;
        this.start = start;
        this.end = end;
        this.fileItem = fileItem;
        this.chunk = chunk;
        getFileInfo();
    }

    public void splitJoint(){
        //InputStream in = null;
        FileChannel in = null;
        try{
            //in = fileItem.getInputStream();

            in = ((FileInputStream)fileItem.getInputStream()).getChannel();
            MappedByteBuffer buf = out.map(FileChannel.MapMode.READ_WRITE, start, end - start);

            int length = 1024;
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(length);

            while (buf.position() + length < buf.limit()){
                in.read(byteBuffer);
                byteBuffer.flip();
                buf.put(byteBuffer);
                byteBuffer.clear();
            }
            if(buf.limit() > buf.position()){
                byteBuffer = ByteBuffer.allocateDirect(buf.limit() - buf.position());
                in.read(byteBuffer);
                byteBuffer.flip();
                buf.put(byteBuffer);
                byteBuffer.clear();
            }

           /* int len = 0;
            int length = 1024;
            byte[] b = new byte[length];
            while (buf.position() + length < buf.limit()){
                in.read(b);
                buf.put(b);
            }
            if(buf.limit() > buf.position()){
                b = new byte[buf.limit() - buf.position()];
                in.read(b);
                buf.put(b);
            }*/
            IOUtil.closeMappedByteBuffer(buf);
        }catch (Exception e){
            e.getMessage();
        }finally {
            try {
                in.close();
                fileItem.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeFile.countDown();
    }

    public void getFileInfo(){
        if(Comm.FILE_MAP.containsKey(uuid)){
            FileInfo fileInfo = Comm.FILE_MAP.get(uuid);
            out = fileInfo.getFileChannel();
            closeFile = fileInfo.getClosefile();
            uploadFile = fileInfo.getUploadfile();
            uploadFile.countDown();
        }else{
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void run() {
        try {
            uploadFile.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("piece " + chunk + " start");
        splitJoint();
        System.out.println("piece " + chunk + " end");

    }




}
