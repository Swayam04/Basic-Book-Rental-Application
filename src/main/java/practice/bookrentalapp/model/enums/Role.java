package practice.bookrentalapp.model.enums;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public boolean hasPermission(Role requiredRole) {
        if (this == ROLE_ADMIN) return true;
        return this == requiredRole;
    }
}
