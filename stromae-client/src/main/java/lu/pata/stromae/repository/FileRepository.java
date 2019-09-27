package lu.pata.stromae.repository;

import lu.pata.stromae.domain.FileSync;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FileRepository extends CrudRepository<FileSync,Long> {
    Optional<FileSync> findByName(String name);
}
