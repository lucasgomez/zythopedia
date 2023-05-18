package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
