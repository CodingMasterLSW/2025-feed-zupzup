package feedzupzup.backend.feedback.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface EmbeddingClusterRepository extends JpaRepository<EmbeddingCluster, Long> {

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<EmbeddingCluster> findById(Long id);
}
