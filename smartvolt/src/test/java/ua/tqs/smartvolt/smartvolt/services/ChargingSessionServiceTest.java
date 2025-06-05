package ua.tqs.smartvolt.smartvolt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dto.OperatorSessionsResponse;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSession;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSessionRepository;

@ExtendWith(MockitoExtension.class)
class ChargingSessionServiceTest {

  @Mock private ChargingSessionRepository chargingSessionRepository;

  private ChargingSessionService chargingSessionService;

  // Common test data
  private EvDriver testDriver;
  private ChargingSlot testSlot;
  private ChargingStation testStation;

  @BeforeEach
  void setUp() {
    chargingSessionService = new ChargingSessionService(chargingSessionRepository);

    testDriver = new EvDriver();
    testDriver.setUserId(101L);
    testDriver.setEmail("driver@example.com");
    testDriver.setName("Test Driver");

    StationOperator testOperator = new StationOperator();
    testOperator.setUserId(1L);

    testStation =
        new ChargingStation("Test Station", 40.0, -8.0, "Test Address", true, testOperator);
    testStation.setStationId(200L);

    testSlot = new ChargingSlot();
    testSlot.setSlotId(201L);
    testSlot.setPower(10.0);
    testSlot.setPricePerKWh(0.15);
    testSlot.setChargingSpeed("Fast");
    testSlot.setStation(testStation);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-36")
  void getSessions_ShouldReturnOperatorSessionsResponse() {
    // Arrange
    List<ChargingSession> testSessions = createTestSessions();
    when(chargingSessionRepository.findAll()).thenReturn(testSessions);

    // Act
    OperatorSessionsResponse response = chargingSessionService.getSessions();

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getTotalSessions()).isEqualTo(5);
    assertThat(response.getAverageSessionsPerMonth()).isEqualTo(0.45);
  }

  // ======================== Auxiliar Methods ========================
  List<ChargingSession> createTestSessions() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startOfCurrentMonth =
        now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
    LocalDateTime startOfPreviousOfPreviousMonth = startOfPreviousMonth.minusMonths(1);

    // Create bookings
    Booking booking1 =
        new Booking(
            testDriver, testSlot, startOfPreviousMonth.plusDays(5).plusHours(10), "used", 20.0);
    ChargingSession session1 = new ChargingSession(5, booking1);
    booking1.setChargingSession(session1);
    Booking booking2 =
        new Booking(
            testDriver, testSlot, startOfPreviousMonth.plusDays(10).plusHours(12), "used", 25.0);
    ChargingSession session2 = new ChargingSession(5, booking2);
    booking2.setChargingSession(session2);
    Booking booking3 =
        new Booking(
            testDriver,
            testSlot,
            startOfPreviousOfPreviousMonth.plusDays(3).plusHours(8),
            "used",
            30.0);
    ChargingSession session3 = new ChargingSession(5, booking3);
    booking3.setChargingSession(session3);
    Booking booking4 =
        new Booking(
            testDriver,
            testSlot,
            startOfPreviousOfPreviousMonth.plusDays(15).plusHours(14),
            "used",
            35.0);
    ChargingSession session4 = new ChargingSession(5, booking4);
    booking4.setChargingSession(session4);
    Booking booking5 =
        new Booking(
            testDriver,
            testSlot,
            startOfPreviousOfPreviousMonth.plusDays(20).plusHours(16),
            "not_used",
            40.0);
    ChargingSession session5 = new ChargingSession(5, booking5);
    booking5.setChargingSession(session5);

    return List.of(session1, session2, session3, session4, session5);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-28")
  void createChargingSession_WhenNoSessionExists_CreatesSession() {
    // Arrange
    Booking booking = mock(Booking.class);
    ChargingSlot slot = mock(ChargingSlot.class);
    when(booking.getChargingSession()).thenReturn(null);
    when(booking.getSlot()).thenReturn(slot);
    when(slot.getPower()).thenReturn(22.0);

    // Act
    chargingSessionService.createChargingSession(booking);

    // Assert using argThat
    verify(chargingSessionRepository, times(1))
        .save(
            argThat(
                session ->
                    session.getEnergyDelivered() == 11.0 && session.getBooking() == booking));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-28")
  void createChargingSession_WhenSessionExists_ThrowsException() {
    // Arrange
    Booking booking = mock(Booking.class);
    ChargingSession existingSession = mock(ChargingSession.class);
    when(booking.getChargingSession()).thenReturn(existingSession);

    // Act & Assert
    IllegalStateException ex =
        assertThrows(
            IllegalStateException.class,
            () -> {
              chargingSessionService.createChargingSession(booking);
            });
    assertEquals("Charging session already exists for this booking.", ex.getMessage());
    verify(chargingSessionRepository, never()).save(any());
  }
}
