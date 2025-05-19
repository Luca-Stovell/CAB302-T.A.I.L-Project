package com.example.cab302tailproject.model;

/**
 * A simple record to hold basic user details like first and last name.
 * Used for populating the UserSession after login.
 *
 * @param firstName The user's first name.
 * @param lastName The user's last name.
 */
public record UserDetail(String firstName, String lastName) {
}