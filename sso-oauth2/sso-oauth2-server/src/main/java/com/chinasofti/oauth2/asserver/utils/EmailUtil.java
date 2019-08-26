package com.chinasofti.oauth2.asserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtil {
	private static final String SENDER_HOST = "sender_host";
	private static final String SENDER_USER = "sender_user";
	private static final String SENDER_PASS = "sender_pass";
	private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
	private static Properties properties;
	static{
		//加载发送邮件的配置信息
		properties = new Properties();
		InputStream inputStream = EmailUtil.class.getClassLoader().getResourceAsStream("application.properties");
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	 public static void main(String[] args) throws MessagingException {
		 EmailUtil.sendEmailBySMTP("178431729@qq.com" ,"sssssss", "登录信息通知");// 发送邮件通知用户
//		 sendEmail("niujianchao@126.com","");
//		 sendEmailByC("niujianchao@chinasofti.com","");
	 }
	 public static String getContent(String account,String pwd){
		 String content = "";
		 content  = "用户名："+account+" \n";
		 content +="密码："+pwd+"\n";
//		 content +="<a href='http://www.baidu.com'>测试的HTML邮件</a>";
		 return content;
	 }
	 public static String getContent(String account){
		 String content = "";
		 content  = "用户名："+account+" \n";
		 content +="忘记密码，申请管理员重置。\n";
//		 content +="<a href='http://www.baidu.com'>测试的HTML邮件</a>";
		 return content;
	 }

	 /**
	  * <p>功能:组装申请信任邮件内容
	  *
	  * @作者: liuwei
	  * @时间：2015-5-20 下午6:08:03
	  * @param applyOrgName  请求机构名
	  * @param receiveOrgName 接收机构名
	  * @param applyTime  请求时间
	  * @return
	  */
	 public static String getApplyTrustContent(String applyOrgName, String receiveOrgName, String applyTime){
		 StringBuffer sb = new StringBuffer();
		 /*
		  * 天津分社管理员 您好:
		  * 	北京分社于2015-5-5 15:11:22向贵机构发起了信任请求,请您登录系统进行审核.
		  */
		 sb.append(receiveOrgName).append("管理员").append(" 您好:").append("\n");
		 sb.append("\t");
		 sb.append(applyOrgName).append("于").append(applyTime).append("向贵机构发起了信任请求,请您登录系统进行处理!");
		 return sb.toString();
	 }

	 /**
	  * <p>功能:组装申请信任成功邮件内容
	  *
	  * @作者: liuwei
	  * @时间：2015-5-20 下午6:08:03
	  * @param approveOrgName  同意信任机构名
	  * @param receiveOrgName 接收机构名
	  * @param approveTime  请求时间
	  * @return
	  */
	 public static String getTrustedContent(String approveOrgName, String receiveOrgName, String approveTime){
		 StringBuffer sb = new StringBuffer();
		 /*
		  * 天津分社管理员 您好:
		  * 	北京分社于2015-5-5 15:11:22审核通过了您的信任请求.
		  */
		 sb.append(receiveOrgName).append("管理员").append(" 您好:").append("\n");
		 sb.append("\t");
		 sb.append(approveOrgName).append("于").append(approveTime).append("审核通过了您的信任请求.");
		 return sb.toString();
	 }

	 /**
	  * <p>功能:组装拒绝信任邮件内容
	  *
	  * @作者: liuwei
	  * @时间：2015-5-20 下午6:37:22
	  * @param refuseOrgName  拒绝发起机构
	  * @param receiveOrgName 拒绝接受机构
	  * @param reasonStr   拒绝原因
	  * @param refuseTime  拒绝时间
	  * @return
	  */
	 public static String getRefuseTrustContent(String refuseOrgName, String receiveOrgName, String reasonStr, String refuseTime){
		 StringBuffer sb = new StringBuffer();
		 /*
		  * 北京分社管理员 您好:
		  * 	天津分社于2015-5-6 15:11:22拒绝了您的信任申请,拒绝原因为:我很忙,我恋爱了...
		  */
		 sb.append(receiveOrgName).append("管理员").append(" 您好:").append("\n");
		 sb.append("\t");
		 sb.append(refuseOrgName).append("于").append(refuseTime).append("决绝了您的信任申请,拒绝原因为:").append(reasonStr);
		 return sb.toString();
	 }

	 public static void sendEmailByC(String toEmail,String content)throws MessagingException{
			// 配置发送邮件的环境属性
	        final Properties props = new Properties();
	        /*
	         * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host /
	         * mail.user / mail.from
	         */
	        // 表示SMTP发送邮件，需要进行身份验证
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.host", "SMTP.chinasofti.com");
	        // 发件人的账号
	        props.put("mail.user", "lijianwei");
	        // 访问SMTP服务时需要提供的密码
	        props.put("mail.password", "1qaz@WSX");
	        props.setProperty("mail.transport.protocol", "smtp");
	        // 构建授权信息，用于进行SMTP进行身份验证
	        Authenticator authenticator = new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                // 用户名、密码
	                String userName = props.getProperty("mail.user");
	                String password = props.getProperty("mail.password");
	                return new PasswordAuthentication(userName, password);
	            }
	        };
	        // 使用环境属性和授权信息，创建邮件会话
	        Session mailSession = Session.getInstance(props, authenticator);
	        // 创建邮件消息
	        MimeMessage message = new MimeMessage(mailSession);
	        // 设置发件人
	        InternetAddress form = new InternetAddress(
	                props.getProperty("mail.user"));
	        message.setFrom(form);

	        // 设置收件人
	        InternetAddress to = new InternetAddress(toEmail);
	        message.setRecipient(RecipientType.TO, to);

	        // 设置抄送
//	        InternetAddress cc = new InternetAddress("luo_aaaaa@yeah.net");
//	        message.setRecipient(RecipientType.CC, cc);

	        // 设置密送，其他的收件人不能看到密送的邮件地址
//	        InternetAddress bcc = new InternetAddress("aaaaa@163.com");
//	        message.setRecipient(RecipientType.CC, bcc);

	        // 设置邮件标题
	        message.setSubject("测试邮件");
	        content = "<a href='http://www.baidu.com'>测试的HTML邮件</a>";
	        // 设置邮件的内容体
//	        message.setContent("<a href='http://www.baidu.com'>测试的HTML邮件</a>", "text/html;charset=UTF-8");
	        message.setContent(content, "text/html;charset=UTF-8");
	        // 发送邮件
	        Transport.send(message);
	 }

	public static void sendEmailBySMTPSync(String toEmail,String content,String title) throws MessagingException{
		// 配置发送邮件的环境属性
        final Properties props = new Properties();
        /*
         * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host /
         * mail.user / mail.from
         */
        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", properties.get(SENDER_HOST));
        // 发件人的账号
        props.put("mail.user", properties.get(SENDER_USER));
        // 访问SMTP服务时需要提供的密码
        props.put("mail.password", properties.get(SENDER_PASS));
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(
                props.getProperty("mail.user"));
        message.setFrom(form);

        // 设置收件人
        InternetAddress to = new InternetAddress(toEmail);
        message.setRecipient(RecipientType.TO, to);

        // 设置抄送
//        InternetAddress cc = new InternetAddress("luo_aaaaa@yeah.net");
//        message.setRecipient(RecipientType.CC, cc);

        // 设置密送，其他的收件人不能看到密送的邮件地址
//        InternetAddress bcc = new InternetAddress("aaaaa@163.com");
//        message.setRecipient(RecipientType.CC, bcc);

        // 设置邮件标题
        message.setSubject(title);
//        content = "<a href='http://www.baidu.com'>测试的HTML邮件</a>";
        // 设置邮件的内容体
//        message.setContent("<a href='http://www.baidu.com'>测试的HTML邮件</a>", "text/html;charset=UTF-8");
        message.setContent(content, "text/html;charset=UTF-8");
        // 发送邮件
        Transport.send(message);
	}

	/**
	 * 异步发送邮件
	 * <p>功能:
	 *
	 * @作者:
	 * @时间：2015年5月18日 下午6:32:52
	 * @param toEmail
	 * @param content
	 * @param title
	 * @throws MessagingException
	 */
	public static void sendEmailBySMTP(final String toEmail,
			final String content, final String title) throws MessagingException {
		Thread command = new Thread() {
			public void run() {
				logger.info("开始发送邮件 " + title);
				try {
					sendEmailBySMTPSync(toEmail, content, title);
				} catch (MessagingException e) {
					logger.error("邮件发送失败", e);
					throw new RuntimeException(e);
				}
			}
		};
		command.start();
	}
}
