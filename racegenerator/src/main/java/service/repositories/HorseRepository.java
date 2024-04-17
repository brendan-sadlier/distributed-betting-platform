package service.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import service.core.Horse;

@Repository
public interface HorseRepository extends MongoRepository<Horse, String> {
}
