package dohi.weather.repository;

import dohi.weather.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemoRepostiory extends JpaRepository<Memo,Integer> {
}
