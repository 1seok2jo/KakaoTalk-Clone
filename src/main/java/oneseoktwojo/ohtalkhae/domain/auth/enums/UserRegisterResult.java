package oneseoktwojo.ohtalkhae.domain.auth.enums;

public enum UserRegisterResult {
    SUCCESS, DUPLICATED_USERNAME, DUPLICATED_EMAIL, DUPLICATED_PHONE, UNKNOWN_ERROR;

    @Override
    public String toString() {
        return name();
    }
}
