package org.yjcycc.tools.common.util;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailUtil {

	private JavaMailSender javaMailSender;  
    private String username;  
  
    public EmailUtil(String host, int port, String username, String password) {  
        this.username = username;  
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();  
        javaMailSenderImpl.setHost(host);  
        javaMailSenderImpl.setPort(port);  
        javaMailSenderImpl.setUsername(username);  
        javaMailSenderImpl.setPassword(password);  
        Properties javaMailProperties = new Properties();  
        javaMailProperties.put("mail.smtp.auth", true);  
        javaMailProperties.put("prop", true);  
        javaMailProperties.put("mail.smtp.timeout", 25000);  
        javaMailSenderImpl.setJavaMailProperties(javaMailProperties);  
        javaMailSender = javaMailSenderImpl;  
    }  
  
    /** 
     * 发送文本内容 
     *  
     * @param to 
     *            目标邮箱 
     * @param subject 
     *            主题 
     * @param body 
     *            内容 
     */  
    public void sendContent(String to, String subject, String body) {  
        SimpleMailMessage simpleMessage = new SimpleMailMessage();  
        simpleMessage.setFrom(username);// 发送人名片  
        simpleMessage.setTo(to);// 收件人邮箱  
        simpleMessage.setSubject(subject);// 邮件主题  
        simpleMessage.setSentDate(new Date());// 邮件发送时间  
        simpleMessage.setText(body);  
        javaMailSender.send(simpleMessage);  
    }  
  
    /** 
     * 发送文本内容，带附件 
     *  
     * @param to 
     *            目标邮箱 
     * @param subject 
     *            主题 
     * @param body 
     *            内容 
     * @param file 
     */  
    public void sendContent(String to, String subject, String body, File file) {  
        sendContent(to, subject, body, new File[] { file });  
    }  
  
    /** 
     * 发送文本内容,带附件 
     *  
     * @param to 
     *            目标邮箱 
     * @param subject 
     *            主题 
     * @param body 
     *            内容 
     */  
    public void sendContent(String to, String subject, String body, File[] files) {  
        if (files == null || files.length == 0) {  
            sendContent(to, subject, body);  
        } else {  
            try {  
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();  
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,  
                        true, "UTF-8");  
                helper.setFrom(username);  
                helper.setTo(to);  
                helper.setSubject(subject);  
                helper.setText(body, true);  
                helper.setSentDate(new Date());  
                for (int i = 0; i < files.length; i++) {  
                    helper.addAttachment(  
                            MimeUtility.encodeText(files[i].getName()),  
                            files[i]);  
                }  
                javaMailSender.send(mimeMessage);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }
	
}
