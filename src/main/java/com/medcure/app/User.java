package com.medcure.app;

public class User {
    private String email;
    private String username;
    private String password;
    private boolean loggedin = false;

    public User(String username, String password,String email, boolean loggedin) {
        this.username = username;
        this.password = password;
        this.loggedin = loggedin;
        this.email = email;
    }

    public User(String username, String userEmail, String password) {
        this.username = username;
        this.email = userEmail; // Assign userEmail to email
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedin() {
        return loggedin;
    }

    public void setLoggedin(boolean loggedin) {
        this.loggedin = loggedin;
    }

}
