package com.example.cab302tailproject;

public class LoginPage {
    private String email;
    private String password;

    public LoginPage(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Check if any field is empty
    public boolean isEmpty() {
        return (email == null || email.isBlank() || password == null || password.isBlank());
    }

    // Check if login credentials match (simple fixed user for example)
    public boolean isValidLogin() {
        // 这里可以自定义正确账号密码
        String correctEmail = "user@example.com";
        String correctPassword = "password123";
        return correctEmail.equals(email) && correctPassword.equals(password);
    }
}
