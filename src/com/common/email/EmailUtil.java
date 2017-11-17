package com.common.email;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;
import javax.swing.JOptionPane;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

public class EmailUtil {

	private String hostName;
	private String account;
	private String password;
	private int port;

	/**
	 * 构造方法，初始化EmailUtil对象
	 */
	private EmailUtil(String hostName, int port, String account, String password) {
		this.hostName = hostName;
		this.port = port;
		this.account = account;
		this.password = password;
	}

	/**
	 * 获取构造器，根据类初始化Logger对象
	 * 
	 * @param Class
	 *            Class对象
	 * @return Logger对象
	 */
	public static EmailUtil getEmail(String hostName, int port, String account, String pass) {
		return new EmailUtil(hostName, port, account, pass);
	}

	// 发送简单邮件
	public void sendSimpleMail(String to, String subject, String content) {
		try {
			Email email = new SimpleEmail();
			email.setHostName(hostName); // 发送服务器
			email.setSmtpPort(port);
			email.setAuthenticator(new DefaultAuthenticator(account, password)); // 发送邮件的用户名和密码
			// email.setSSLOnConnect(true);
			email.setStartTLSEnabled(true);
			email.setFrom(account); // 发送邮箱
			email.setSubject(subject);// 主题
			email.setMsg(content); // 内容
			email.addTo(to); // 接收邮箱
			email.send();
		} catch (EmailException ex) {
			JOptionPane.showMessageDialog(null, "向 " + to + " 发送邮件失败");
			ex.printStackTrace();
		}
	}

	// 发送带附件的邮件
	public void sendMutiMail() {
		try {
			EmailAttachment attachment = new EmailAttachment();
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("python resource");
			attachment.setPath("src/com/beckham/common/email/附件.txt");
			attachment.setName(MimeUtility.encodeText("附件.txt")); // 设置附件的中文编码

			MultiPartEmail email = new MultiPartEmail();
			email.setHostName("smtp.163.com"); // 发送服务器
			email.setAuthentication("gaowm0207@163.com", "password"); // 发送邮件的用户名和密码
			email.addTo("459978392@qq.com", "a"); // 接收邮箱
			email.setFrom("gaowm0207@163.com", "a"); // 发送邮箱
			email.setSubject("测试主题");// 主题
			email.setMsg("这里是邮件内容"); // 内容
			email.setCharset("GBK"); // 编码
			// 添加附件
			email.attach(attachment);

			// 发送邮件
			email.send();
		} catch (EmailException | UnsupportedEncodingException ex) {

			ex.printStackTrace();
		}
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
