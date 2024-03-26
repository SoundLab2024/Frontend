package com.soundlab.app.presenter.api.request;

import java.sql.Date;

public class RegisterRequest {

        private String email;
        private String password;
        private String username;
        private String gender;
        private Date birth;

        public RegisterRequest(String email, String password, String username, String gender, Date birth){
            this.email = email;
            this.password = password;
            this.username = username;
            this.gender = gender;
            this.birth = birth;
        }

        public void setUsername(String username) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() { return email; }

        public void setEmail(String email) { this.email = email; }

        public String getGender() { return gender; }

        public void setGender(String gender) { this.gender = gender; }

        public Date getBirth() { return birth; }

        public void setBirth(Date birth) { this.birth = birth; }

}
