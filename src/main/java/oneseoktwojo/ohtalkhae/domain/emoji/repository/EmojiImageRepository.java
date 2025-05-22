package oneseoktwojo.ohtalkhae.domain.emoji.repository;

import oneseoktwojo.ohtalkhae.domain.emoji.entity.EmojiImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmojiImageRepository extends JpaRepository<EmojiImage, Long> {
    List<EmojiImage> findByEmojiId(Long emojiId);
}