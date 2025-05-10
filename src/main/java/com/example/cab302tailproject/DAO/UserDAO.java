package com.example.cab302tailproject.DAO;

public interface UserDAO {
    boolean checkEmail(String email);
    boolean checkPassword(String email, String password);
}
