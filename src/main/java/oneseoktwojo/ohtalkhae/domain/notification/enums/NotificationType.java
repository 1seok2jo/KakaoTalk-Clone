package oneseoktwojo.ohtalkhae.domain.notification.enums;

public enum NotificationType {
    NEW_MESSAGE, BIRTHDAY, INFO;

    @Override
    public String toString() {
        return name();
    }
}
