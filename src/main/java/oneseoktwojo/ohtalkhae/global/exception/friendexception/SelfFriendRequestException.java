package oneseoktwojo.ohtalkhae.global.exception.friendexception;

public class SelfFriendRequestException extends RuntimeException{
    public SelfFriendRequestException() { super("Cannot send a friend request to yourself."); }
}
