package ua.tqs.smartvolt.smartvolt.services;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.OperatorSessionsResponse;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSession;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSessionRepository;

@Service
public class ChargingSessionService {
  private final ChargingSessionRepository chargingSessionRepository;

  public ChargingSessionService(ChargingSessionRepository chargingSessionRepository) {
    this.chargingSessionRepository = chargingSessionRepository;
  }

  public OperatorSessionsResponse getSessions() {
    OperatorSessionsResponse response = new OperatorSessionsResponse();
    List<ChargingSession> sessions = chargingSessionRepository.findAll();
    Map<String, Integer> monthSessions = new LinkedHashMap<>();
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime oneYearAgo = today.minusYears(1).plusMonths(1);

    YearMonth startMonth = YearMonth.from(oneYearAgo).plusMonths(1);
    YearMonth endMonth = YearMonth.from(today);
    while (!startMonth.isAfter(endMonth)) {
      String monthName = startMonth.getMonth().toString().substring(0, 3).toLowerCase();
      monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
      monthSessions.put(monthName, 0);
      startMonth = startMonth.plusMonths(1);
    }

    for (ChargingSession session : sessions) {
      Booking booking = session.getBooking();
      if (booking != null) {
        LocalDateTime startTime = booking.getStartTime();
        if (startTime.isAfter(oneYearAgo) && startTime.isBefore(today)) {
          String month = startTime.getMonth().toString().substring(0, 3).toLowerCase();
          month =
              month.substring(0, 1).toUpperCase() + month.substring(1); // Capitalize first letter
          monthSessions.put(month, monthSessions.getOrDefault(month, 0) + 1);
        }
      }
    }
    response.setMonthSessions(monthSessions);
    int totalSessions = monthSessions.values().stream().mapToInt(Integer::intValue).sum();
    response.setTotalSessions(totalSessions);
    double averageSessionsPerMonth =
        Math.round(
                monthSessions.values().stream().mapToInt(Integer::intValue).average().orElse(0.0)
                    * 100.0)
            / 100.0;
    response.setAverageSessionsPerMonth(averageSessionsPerMonth);

    return response;
  }
}
