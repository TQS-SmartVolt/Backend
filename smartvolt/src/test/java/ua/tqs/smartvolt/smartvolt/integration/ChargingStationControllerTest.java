// package ua.tqs.smartvolt.smartvolt.integration;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Tag;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.test.web.servlet.MockMvc;

// import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
// import ua.tqs.smartvolt.smartvolt.models.StationOperator;
// import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
// import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;

// @WebMvcTest(ChargingStationController.class)
// class ChargingStationControllerIT {

//     @Autowired
//     private MockMvc mockMvc; //Mock the MVC layer

//     @Autowired
//     private ChargingStationRepository chargingStationRepository;

//     @Autowired
//     private StationOperatorRepository stationOperatorRepository;

//     @BeforeEach
//     void setUp() {
//         // Clear the repository before each test
//         chargingStationRepository.deleteAll();
//         stationOperatorRepository.deleteAll();
//     }

//     @Test
//     @Tag("IT-Fast")
//     @Requirement("SV-34")
//     void testGetAllChargingStations_WhenOperatorNotExists_ThrowsResourceNotFoundException()
//             throws Exception {
//         Long operatorId = 999L; // Non-existing operator ID

//         // When & Then
//         mockMvc
//                 .perform(get("/api/v1/stations?operatorId=" + operatorId))
//                 .andExpect(status().isNotFound())
//                 .andExpect(content().string("Operator not found with id: " + operatorId));
//     }

//     @Test
//     @Tag("IT-Fast")
//     @Requirement("SV-34")
//     void testGetAllChargingStations_WhenOperatorExists_ReturnsListOfChargingStations()
//             throws Exception {
//         // Given
//         StationOperator operator = new StationOperator();
//         operator.setName("Test Operator");
//         operator.setEmail("test@example.com");
//         operator.setPassword("password");
//         stationOperatorRepository.save(operator);

//         ChargingStation station1 = new ChargingStation();
//         station1.setName("Station 1");
//         station1.setLatitude(12.34);
//         station1.setLongitude(56.78);
//         station1.setAddress("Address 1");
//         station1.setAvailability(true);
//         station1.setOperator(operator);
//         chargingStationRepository.save(station1);

//         // When & Then
//         mockMvc
//                 .perform(get("/api/v1/stations?operatorId=" + operator.getUserId()))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType("application/json"))
//                 .andExpect(jsonPath("$[0].name").value("Station 1"));
//     }

//     @Test
//     @Tag("IT-Fast")
//     @Requirement("SV-34")
//     void testCreateChargingStation_WhenOperatorExists_CreatesChargingStation() throws Exception {
//         // Given
//         StationOperator operator = new StationOperator();
//         operator.setName("Test Operator");
//         operator.setEmail("test@example.com");
//         operator.setPassword("password");
//         stationOperatorRepository.save(operator);

//         String requestBody = "{ \"name\": \"Station 1\", \"latitude\": 12.34, \"longitude\":
// 56.78, \"operatorId\": "
//                 + operator.getUserId()
//                 + " }";
//         // When & Then
//         mockMvc

// .perform(post("/api/v1/stations").contentType("application/json").content(requestBody))
//                 .andExpect(status().isCreated())
//                 .andExpect(content().contentType("application/json"))
//                 .andExpect(jsonPath("$.name").value("Station 1"))
//                 .andExpect(jsonPath("$.latitude").value(12.34))
//                 .andExpect(jsonPath("$.longitude").value(56.78));
//     }
// }
