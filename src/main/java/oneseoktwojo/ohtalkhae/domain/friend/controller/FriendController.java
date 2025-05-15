package oneseoktwojo.ohtalkhae.domain.friend.controller;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendListDto;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendRequestDto;
import oneseoktwojo.ohtalkhae.domain.friend.dto.FriendResponseDto;
import oneseoktwojo.ohtalkhae.domain.friend.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<Void> request(@RequestBody FriendRequestDto dto) {
        friendService.sendRequest(dto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendListDto>> getRequests(@RequestParam Long userId) {
        return ResponseEntity.ok(friendService.getReceivedRequests(userId));
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> accept(@RequestBody FriendResponseDto dto) {
        friendService.acceptRequest(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject")
    public ResponseEntity<Void> reject(@RequestBody FriendResponseDto dto) {
        friendService.rejectRequest(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<FriendListDto>> list(@RequestParam Long userId) {
        return ResponseEntity.ok(friendService.getFriendsList(userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam Long userId, @RequestParam Long targetId) {
        friendService.deleteFriend(userId, targetId);
        return ResponseEntity.noContent().build();
    }

}
