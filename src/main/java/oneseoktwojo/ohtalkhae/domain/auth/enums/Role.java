package oneseoktwojo.ohtalkhae.domain.auth.enums;

public enum Role {
    ROLE_USER, ROLE_ADMIN;

    @Override
    public String toString() {
        return name();
    }
}
