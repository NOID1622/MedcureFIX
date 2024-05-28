package com.medcure.utils;
import java.io.*;

public class Session implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SESSION_FILE = "session.ser";

    private static volatile Session instance;
    private boolean isLoggedIn;
    private String loggedInUsername;
    private String userEmail; // Add this field to store the user's email

    public String getUserEmail() {
        return userEmail;
    }

    private Session() {
        isLoggedIn = false;
        loggedInUsername = ""; // Initialize the logged-in username
    }
    public void setUserEmail(String email) {
        this.userEmail = email;
        saveSession(); // Save the session after setting the email
    }

    public static Session getInstance() {
        if (instance == null) {
            synchronized (Session.class) {
                if (instance == null) {
                    instance = new Session();
                    instance.createSessionFile();
                }
            }
        }
        return instance;
    }

    public void createSessionFile() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            saveSession();
        } else {
            loadSession();
        }
    }

    private void loadSession() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SESSION_FILE))) {
            Session session = (Session) ois.readObject();
            this.isLoggedIn = session.isLoggedIn;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading session: " + e.getMessage());
        }
    }

    private void saveSession() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() { // Changed to non-static
        return isLoggedIn;
    }

    public void login(String username) {
        isLoggedIn = true;
        loggedInUsername = username; // Set the logged-in username
        saveSession();
    }

    public void logout() {
        isLoggedIn = false;
        loggedInUsername = ""; // Clear the logged-in username
        saveSession();
    }
    public String getLoggedInUsername() {
        return loggedInUsername; // Return the logged-in username
    }
}
