package com.example.Transmission;

import java.io.Serializable;

public class UserModel implements Serializable {

		private String email;

		public UserModel(String email) {
				this.email = email;
		}

		public String getEmail() {
				return email;
		}

		public void setEmail(String email) {
				this.email = email;
		}
}
