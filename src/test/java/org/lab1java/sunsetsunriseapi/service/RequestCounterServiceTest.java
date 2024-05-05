package org.lab1java.sunsetsunriseapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.core.sunsetsunrise.service.RequestCounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.concurrent.atomic.AtomicInteger;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestCounterServiceTest {

  @Mock
  private AtomicInteger requestCount;

  @InjectMocks
  private RequestCounterService requestCounterService;

  @BeforeEach
  public void setUp() {
    requestCounterService = new RequestCounterService();
  }

  @Test

  void testRequestIncrement() {
    requestCounterService.requestIncrement();
    requestCounterService.requestIncrement();
    requestCounterService.requestIncrement();

    assertEquals(3, requestCounterService.getRequestCount());
  }
}
