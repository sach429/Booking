package com.sach429.booking.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
}
