package dohi.weather;

import dohi.weather.domain.Memo;
import dohi.weather.repository.JpaMemoRepostiory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class JPAMemoRepositoryTest {

    @Autowired
    JpaMemoRepostiory jpaMemoRepostiory;
    @Test
    void insertMemoTest(){
    //given
        Memo memo = new Memo(10,"Jpa memo");
    //when
        jpaMemoRepostiory.save(memo);
    //then
        List<Memo> memoList = jpaMemoRepostiory.findAll();
        assertTrue(memoList.size() > 0);
    }
    @Test
    void findByIdTest(){
    //given
        Memo newMemo = new Memo(11,"Jpa");
    //when
        Memo memo = jpaMemoRepostiory.save(newMemo);
    //then
        Optional<Memo> result = jpaMemoRepostiory.findById(memo.getId());
        assertEquals(result.get().getText(),"Jpa");
    }
}
