package oneseoktwojo.ohtalkhae.domain.emoji.repository;

import oneseoktwojo.ohtalkhae.domain.emoji.entity.Emoji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiRepository extends JpaRepository<Emoji, Long> {
}

