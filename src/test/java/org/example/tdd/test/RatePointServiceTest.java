package org.example.tdd.test;

import org.assertj.core.api.Assertions;
import org.example.tdd.service.RatePointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RatePointServiceTest {
    @InjectMocks
    private RatePointService ratePointService;

    @Test
    void _10000원의적립은100원() {
        final int price = 10000;

        final int result = ratePointService.calculateAmount(price);
        Assertions.assertThat(result).isEqualTo(100);
    }
}
