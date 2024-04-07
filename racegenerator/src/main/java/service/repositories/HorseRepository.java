package service.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import service.core.Horse;

public interface HorseRepository extends MongoRepository<Horse, String> {
}
