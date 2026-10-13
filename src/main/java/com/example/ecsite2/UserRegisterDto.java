package com.example.ecsite2;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterDto {

	@NotBlank(message = "ユーザー名を入力してください")
	private String username;

	@NotBlank(message = "パスワードを入力してください")
	@Size(min = 8,message = "パスワードは8文字以上にしてください")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
