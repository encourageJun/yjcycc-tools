package org.yjcycc.tools.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ZipFileUtil {

	/** 
     * 解压文件 
     * @param zipFile 
     *          解压文件对象 
     * @param descDir 
     *          解压到的目录 
     * @throws Exception 
     */  
    @SuppressWarnings("resource")
	public static void unZipFile(File zipFile,String descDir)throws Exception{    
        int len = -1;  
        File pathFile = new File(descDir);  
        if(!pathFile.exists())pathFile.mkdirs();  
        ZipFile zip = new ZipFile(zipFile, "GBK");  
        for(Enumeration<ZipEntry> entries = zip.getEntries();entries.hasMoreElements();){  
            ZipEntry entry = (ZipEntry)entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir +"/"+ zipEntryName).replaceAll("\\*", "/");  
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists())file.mkdirs();  
            //判断文件全路径是否为文件夹,如果是上面已经创建,不需要解压  
            if(new File(outPath).isDirectory())continue;  
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf = new byte[1024];  
            while((len=in.read(buf))>0){  
                out.write(buf, 0, len);  
            }  
            in.close();  
            out.close();  
        }  
    }
	
}
