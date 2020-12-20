package com.sach429.booking.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "booking.config")
public class BookingConfigurationProperties {
    private Integer maxDaysInAdvance;
    private Integer minDaysInAdvance;
    private Integer maxDuration;
    private Integer minIntervalPerAccount;

    @PostConstruct
    public void validate() {
        if (this.minDaysInAdvance >= this.maxDaysInAdvance) {
            throw new IllegalArgumentException("Advanced min booking days has to be less than mx booking days");
        }
        if (this.minDaysInAdvance < 0) {
            throw new IllegalArgumentException("Advanced min booking days has to be positive");
        }
        if (this.maxDuration <= 0) {
            throw new IllegalArgumentException("Max duration has to be positive");
        }
    }
}
