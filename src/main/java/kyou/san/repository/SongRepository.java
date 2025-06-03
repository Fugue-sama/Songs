package kyou.san.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import kyou.san.models.Song;

public interface SongRepository extends MongoRepository<Song, String> {
	
	List<Song> findByLanguage(String language);
	
}
