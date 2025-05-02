package oneseoktwojo.ohtalkhae.domain.emoji.repository.;

import oneseoktwojo.ohtalkhae.domain.emoji.entity.EmojiImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiImageRepository extends JpaRepository<EmojiImage, Long> {
}