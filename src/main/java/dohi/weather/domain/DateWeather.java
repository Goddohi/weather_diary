package dohi.weather.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity(name = "date_weather")
@Setter
@Getter
@NoArgsConstructor
public class DateWeather {
    @Id
    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;

    public DateWeather(LocalDate date) {
        this.date = date;
        this.weather = "정보없음";
        this.icon = "정보없음";
        this.temperature = 0;
    }

}
