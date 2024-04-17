package edu.northeastern.brainrush.match;

public enum UserRole {
    Host(0),
    Guest(1);
    private final int value;
    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRole fromValue(int value) {
        for (UserRole role : UserRole.values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}
