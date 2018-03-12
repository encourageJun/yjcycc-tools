package org.yjcycc.tools.fastdfs;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.csource.common.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yjcycc.tools.common.util.ImageToolUtil;

@Service
public class FileToolsServiceImpl implements FileToolsService {


    private static String groupName="group1";

    @Autowired
    private FdfsConnectionPoolService fs;

    public String uploadAttachFile(File file) throws Exception {
        String fileName = file.getName();
        long fileSize = file.length();
        byte[] fileBuff = getBytesFromFile(file);
        return this.uploadFile(fileBuff, fileName, fileSize, null);
    }

   /* public String uploadAttachFile(MultipartFile uploadFile) throws Exception {
        String fileName = uploadFile.getOriginalFilename();
        long fileSize = uploadFile.getSize();
        byte[] fileBuff =uploadFile.getBytes();
        return this.uploadFile(fileBuff, fileName, fileSize, null);
    }*/


    @SuppressWarnings("static-access")
	public String uploadFile(byte[] fileBuff, String fileName, long fileSize, Rectangle rectangle) throws Exception{
        FdfsManagerConnection conn = null;
        String url = null;
        String fileExtName = getExtName(fileName);
        List<String> fileTypes=this.getListFileType();
        //如果上传文件是图片，则时行重绘
        for(String fileType:fileTypes){
            if(StringUtils.equalsIgnoreCase(fileType, fileExtName)){
                ImageToolUtil.checkImage(fileBuff, fileExtName);
                break;
            }
        }
        try{
            conn = fs.getFdfsConnection();
            NameValuePair[] metaList = new NameValuePair[3];
            metaList[0] = new NameValuePair("fileName", fileName);
            metaList[1] = new NameValuePair("fileExtName", fileExtName);
            metaList[2] = new NameValuePair("fileSize", String.valueOf(fileSize));
            String[] fileIds = conn.getClient().upload_file(groupName, fileBuff, fileExtName, metaList);
            url =fileIds[1];
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            if(fs!=null)
                fs.releaseFdfsConnection(conn);
        }
        return url;
    }

    @SuppressWarnings("static-access")
	public String uploadFile( String master_filename, String prefix_name, byte[] fileBuff, String fileName, long fileSize, Rectangle rectangle) throws Exception{
        FdfsManagerConnection conn = null;
        String url = null;
        String fileExtName = getExtName(fileName);
        List<String> fileTypes=this.getListFileType();
        //如果上传文件是图片，则时行重绘
//		if(fileTypes.contains(fileExtName)){
        for(String fileType:fileTypes){
            if(StringUtils.equalsIgnoreCase(fileType, fileExtName)){
                ImageToolUtil.checkImage(fileBuff, fileExtName);
                break;
            }
        }
        try{
            conn = fs.getFdfsConnection();
            NameValuePair[] metaList = new NameValuePair[3];
            metaList[0] = new NameValuePair("fileName", fileName);
            metaList[1] = new NameValuePair("fileExtName", fileExtName);
            metaList[2] = new NameValuePair("fileSize", String.valueOf(fileSize));
            if(master_filename==null){
                String[] fileIds = conn.getClient().upload_file(groupName, fileBuff, fileExtName, metaList);
                url =fileIds[1];
            }else{
                conn.getClient().upload_file(groupName, master_filename,prefix_name, fileBuff , fileExtName, metaList);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            if(fs!=null)
                fs.releaseFdfsConnection(conn);
        }
        return url;
    }


    public void downloadFile(String fastUrl, OutputStream outStream, String thumnailSize) throws Exception{
        try{
            byte[] fileBuff=downloadFile(fastUrl,thumnailSize);

            InputStream inStream = new ByteArrayInputStream(fileBuff);
            BufferedOutputStream bos  = new BufferedOutputStream(outStream);
            BufferedInputStream  bis  = new BufferedInputStream(inStream);
            byte[] bytes = new byte[1024];
            int readLength = 0;
            while((readLength = bis.read(bytes))!=-1){
                bos.write(bytes,0,readLength);
                bos.flush();
            }
            //释放资源
            outStream.close();
            bis.close();
            inStream.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public byte[] downloadFile(String fastUrl, String thumnailSize) throws Exception{
        if(fastUrl == null){
            throw new NullPointerException("下载路径为空!");
        }
        FdfsManagerConnection conn = null;
        byte[] fileBuff=null;
        try {
            conn = fs.getFdfsConnection();
            if (fastUrl.charAt(0) == '/') {
                fileBuff = conn.getClient().download_file(
                        getGroupName(getUrl(fastUrl, thumnailSize)),
                        getRemote_fileName(getUrl(fastUrl, thumnailSize)));
            } else {
                fileBuff = conn.getClient().download_file(groupName,
                        getUrl(fastUrl, thumnailSize));
            }

            if (fileBuff == null || fileBuff.length == 0||"null".equals(fileBuff)) {
            }
        } catch (Exception e) {
        } finally {
            if (fs != null)
                fs.releaseFdfsConnection(conn);
        }
        return fileBuff;
    }

    public void deleteFile(String url) throws Exception{
        FdfsManagerConnection conn = null;
        try{
            conn = fs.getFdfsConnection();
            conn.getClient().delete_file(groupName, url);
        }catch(Exception ex){
        }finally{
            if(fs!=null)
                fs.releaseFdfsConnection(conn);
        }
    }



    public static boolean isGetThumnail(String fileName){
        return getListFileType().contains(getExtName(fileName).toLowerCase());
    }

    private static  String getExtName(String fileName){
        return fileName.substring(fileName.lastIndexOf('.')+1);
    }



    public static  List<String> getListFileType(){
        List<String> fileTypes = new ArrayList<String>();
        fileTypes.add("jpg");
        fileTypes.add("jpeg");
        fileTypes.add("bmp");
        fileTypes.add("gif");
        fileTypes.add("png");
        return fileTypes;
    }

    public static byte[] getBytesFromFile(File file){
        if (file == null) {
            return null;
        }
        FileInputStream in=null;
        ByteArrayOutputStream out=null;

        try{
            in = new FileInputStream(file);
            out = new ByteArrayOutputStream(1024);

            byte[] buff = new byte[1024];
            int readLength;
            while ((readLength = in.read(buff)) != -1) {
                out.write(buff, 0, readLength);
                out.flush();
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }



    public String getUrl(String url,String thumnailSize){
        if(StringUtils.isNotBlank(url)){
            if(StringUtils.isNotBlank(thumnailSize)&& !ImageToolUtil.ATTACH_MASTER_NAME.equalsIgnoreCase(thumnailSize)){
                url = url.substring(0,url.lastIndexOf('.')) + thumnailSize + "." + url.substring(url.lastIndexOf('.')+1);
            }
        }
        return url;
    }


    private String getGroupName(String url){
        int pos = url.indexOf('/',1);
        String group_name = url.substring(1,pos);
        return group_name;
    }


    private String getRemote_fileName(String url){
        int pos = url.indexOf('/',1);
        String remote_filename = url.substring(pos+1);
        return remote_filename;
    }
}
