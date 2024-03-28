package org.lab1java.sunsetsunriseapi.service;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for counting requests.
 */
@Service
@NoArgsConstructor
public class RequestCounterService {
  private final AtomicInteger requestCount = new AtomicInteger(0);

  public void requestIncrement() {
    requestCount.incrementAndGet();
  }

  public int getRequestCount() {
    return requestCount.get();
  }
}
