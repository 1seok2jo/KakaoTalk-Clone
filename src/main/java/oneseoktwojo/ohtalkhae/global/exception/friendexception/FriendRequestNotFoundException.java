package oneseoktwojo.ohtalkhae.global.exception.friendexception;

public class FriendRequestNotFoundException extends RuntimeException {
    public FriendRequestNotFoundException(String message) {
        super("Friend request not found.");
    }
}
