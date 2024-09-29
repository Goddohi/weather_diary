package dohi.weather;

import dohi.weather.domain.Memo;
import dohi.weather.repository.JdbcMemoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 이걸 지우면 DB에 들어간다
public class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest(){
    //given
       Memo newMemo = new Memo(2,"this is a test memo");

    //when
       jdbcMemoRepository.save(newMemo);

    //then
        Optional<Memo> result = jdbcMemoRepository.findById(2);
        assertEquals(result.get().getText(), "this is a test memo");
    }

    @Test
    void findAllMemoTest(){
    //given
        List<Memo> memoList = jdbcMemoRepository.findAll();
        System.out.println(memoList);
        assertNotNull(memoList);
    //when
    //then
    }
}
