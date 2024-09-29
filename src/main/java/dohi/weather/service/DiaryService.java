package dohi.weather.service;

import dohi.weather.WeatherApplication;
import dohi.weather.domain.DateWeather;
import dohi.weather.domain.Diary;
import dohi.weather.error.InvalidDate;
import dohi.weather.repository.DateWeatherRepository;
import dohi.weather.repository.DiaryRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Service
public class DiaryService {
    private final Docket api;
    @Value("${openweathermap.key}")
    private String api_key;
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository, Docket api) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
        this.api = api;
    }


    @Transactional
    @Scheduled(cron = "0 0 1 * * *") //매시각 1시마다 데이터불러오기
    public void saveWeatherData() {
        logger.info("Saving data...");
        dateWeatherRepository.save(getWeatherFromApi());
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date , String text) {
        logger.info("created diary of text : " + text);

        //날씨데이터 가져오기 DB-> 없을시 APi
        DateWeather nowDateWeather =getDateWeather(date);
        System.out.println(date);
        Diary nowDiary = new Diary();
        nowDiary.setText(text);
        nowDiary.setDate(date);
        nowDiary.setDateWeather(nowDateWeather);
        diaryRepository.save(nowDiary);
        logger.info("end create");
    }



    private DateWeather getWeatherFromApi() {
        String weatherData = getWeatherString();
        //날씨 json파싱
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double)parsedWeather.get("temp"));
        dateWeatherRepository.save(dateWeather);
        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);

        //미래의 일기인 경우 입력불가 하게
        if(date.isAfter(LocalDate.now())) {
            throw new InvalidDate();
        }
        if (dateWeatherListFromDB.size() > 0) {
            logger.info("get dateWeather from db");
            return (DateWeather)dateWeatherListFromDB.get(0);
             } else if (!date.isEqual(LocalDate.now())) { //DB에 자료도 없고 현재가 아니면 없는 정보로 업데이트
            logger.info("not exist dateWeather from db");
            return new DateWeather(date);
      }else {
           logger.info("get dateWeather from api");
            return getWeatherFromApi();
        }
    }





    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary =diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
        logger.error("deleted");
    }



    private long convertDateToUnixTimestamp(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }
/* //API가 오차피 현재날짜만불러올수있음
    private String getOldWeatherString(LocalDate date) {
        long timestamp = convertDateToUnixTimestamp(date);

        String apiUrl = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=Seoul&dt=%d&appid=%s",
                timestamp,
                api_key
        );  System.out.println(apiUrl);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();
            return response.toString();
        } catch (Exception var8) {
            logger.error("failed to get weather from OpenOldWeatherMapApi");
            return "failed to get response";
        }
    }

    private DateWeather getOldWeatherFromApi(LocalDate date) {
        String weatherData = getOldWeatherString(date);
        //날씨 json파싱
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(date);
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double)parsedWeather.get("temp"));
        dateWeatherRepository.save(dateWeather);
        return dateWeather;
    }
*/

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + api_key;
       // System.out.println(apiUrl);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();
            return response.toString();
        } catch (Exception e) {
            logger.error("failed to get weather from OpenWeatherMapApi");
            return "failed to get response";
        }
    }




    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject)jsonParser.parse(jsonString);
        } catch (ParseException e) {
            logger.error("failed to parse weather data, data: " + jsonString);
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap();
        JSONObject mainData = (JSONObject)jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray)jsonObject.get("weather");
        JSONObject weatherData = (JSONObject)weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }


}
