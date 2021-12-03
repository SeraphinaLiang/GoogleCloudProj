package be.kuleuven.distributedsystems.cloud.entities;

import java.io.Serializable;

public class User implements Serializable {

    private String email;
    private String role;

    public User() {

    }

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public boolean isManager() {
        return this.role != null && this.role.equals("manager");
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
