package dohi.weather.controller;

import dohi.weather.domain.Diary;
import dohi.weather.repository.DiaryRepository;
import dohi.weather.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }
    @ApiOperation(
            value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장",
            notes = "테스트 할때 http://localhost:8080/create/diary?date=2024-09-29"
    )
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

  @ApiOperation( value ="선택한 날짜의 모든 일기 데이터를 가져옵니다",
  notes="예시 http://localhost:8080/read/diary?date=2024-09-29")
    @GetMapping({"/read/diary"})
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return this.diaryService.readDiary(date);
    }

    @ApiOperation( value ="선택한 기간중의 모든 일기 데이터를 가져옵니다",
            notes="예시 http://localhost:8080/read/diaries?startDate=2024-07-29&endDate=2024-09-29")
    @GetMapping({"/read/diaries"})
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 첫번째날",example = "2024-09-29") LocalDate startDate
                         , @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 마지막날",example = "2024-09-30") LocalDate endDate) {
        return this.diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation( value ="데이터 맨처음에 나오는 일기를 수정합니다.",
            notes="예시 http://localhost:8080/update/diary?date=2024-09-29")
    @PutMapping({"/update/diary"})
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        this.diaryService.updateDiary(date, text);
    }

    @ApiOperation( value ="해당 날짜 일기를 전체 삭제합니다.",
            notes="예시 http://localhost:8080/delete/diary?date=2024-09-29")
    @DeleteMapping({"/delete/diary"})
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        this.diaryService.deleteDiary(date);
    }


}
