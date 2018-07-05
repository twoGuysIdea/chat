package com.chat.springboot.domain;

import java.util.Date;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_info")
public class UserInfo {
	
	/**
	 * 用户id标志
	 */
	@Id
	private String id;
	
	/**
	 * 用户名
	 */
	@NotNull(message = "用户名不能为空")
	@Field("user_name")
	private String userName;
	
	/**
	 * 用过户密码
	 */
	@NotNull(message = "密码不能为空")
	private String password;
	
	/**
	 * 密码盐 加密使用
	 */
	private String salt;
	
	/**
	 * 用户性别 
	 */
	private Integer sex;
	
	/**
	 * 用户生日
	 */
	private Date birthday; 
	
	/**
	 * 用户签名
	 */
	private String sign;
	
	public UserInfo() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", userName=" + userName + ", password=" + password + ", salt=" + salt + ", sex="
				+ sex + ", birthday=" + birthday + "]";
	}
	
	
	
	
}
