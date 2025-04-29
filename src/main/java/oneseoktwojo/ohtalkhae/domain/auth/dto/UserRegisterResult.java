package oneseoktwojo.ohtalkhae.domain.auth.dto;

public enum UserRegisterResult {
    SUCCESS, DUPLICATED_USERNAME, DUPLICATED_EMAIL, DUPLICATED_PHONE, UNKNOWN_ERROR;

    @Override
    public String toString() {
        return name();
    }
}
