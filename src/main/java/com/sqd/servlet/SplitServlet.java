package com.sqd.servlet;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.sqd.file.FileInfo;
import com.sqd.thread.CloseFileThread;
import com.sqd.thread.FilePieceThread;
import com.sqd.thread.ThreadPool;
import com.sqd.util.Comm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@WebServlet("/uploadFile")
public class SplitServlet extends HttpServlet {

    public String path = "D:\\test\\";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        if(!ServletFileUpload.isMultipartContent(req)){
            return;
        }

        List<FileItem> list = null;
        try {
             list = upload.parseRequest(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        long start = 0;
        long end = 0;
        FileItem file = null;
        String filename = null;
        String uuid = null;
        int chunks = 0;
        int chunk = 0;
        long filesize = 0;
        for (FileItem item : list) {
            if (item.isFormField()) {
                String name = item.getFieldName();
                String value = item.getString("UTF-8");

                //表单数据
                if ("start".equals(name)) {
                   start = Long.valueOf(value);
                }
                if ("end".equals(name)) {
                   end = Long.valueOf(value);
                }
                if ("uuid".equals(name)) {
                   uuid = value;
                }
                if ("chunks".equals(name)) {
                    chunks = Integer.valueOf(value);
                }
                if ("chunk".equals(name)) {
                    chunk = Integer.valueOf(value);
                }
                if ("filesize".equals(name)) {
                    filesize = Long.valueOf(value);
                }
            }
            //获取文件参数
            if(!item.isFormField()){
                //key值
                String fieldName = item.getFieldName();
                filename = item.getName();
                file = item;

            }
        }

        String filePath = path + filename;
        File file0 = new File(filePath);


        if(!file0.exists()){
            file0.createNewFile();
        }

        if(!Comm.FILE_MAP.containsKey(uuid)){
            synchronized (Comm.FILE_MAP){
                if(!Comm.FILE_MAP.containsKey(uuid)){
                    FileInfo fileInfo = new FileInfo(file0,chunks);
                    Comm.FILE_MAP.put(uuid,fileInfo);
                    ThreadPool.getInstance().execute(new CloseFileThread(uuid));
                }

            }
        }

        ThreadPool.getInstance().execute(new FilePieceThread(uuid,start,end, file,chunk));

        resp.setContentType("application/json;charset=UTF-8");
        OutputStreamWriter osw = new OutputStreamWriter(resp.getOutputStream(),"UTF-8");
        osw.write("{\"code\":1}");
        osw.close();
    }

}
