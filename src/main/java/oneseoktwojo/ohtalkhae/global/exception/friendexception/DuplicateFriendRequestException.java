package oneseoktwojo.ohtalkhae.global.exception.friendexception;

public class DuplicateFriendRequestException extends RuntimeException {
    public DuplicateFriendRequestException(String message) {
        super("A friend request already exists.");
    }
}
