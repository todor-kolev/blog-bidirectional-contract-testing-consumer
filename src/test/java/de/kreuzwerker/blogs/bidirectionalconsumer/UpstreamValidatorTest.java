package de.kreuzwerker.blogs.bidirectionalconsumer;

import com.atlassian.oai.validator.wiremock.OpenApiValidationListener;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.kreuzwerker.blogs.bidirectionalconsumer.objects.Employee;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
public class UpstreamValidatorTest {

  private final String specPath = "file:///Users/tkolev/Documents/src/p/blog-bidirectional-contract-testing-consumer/src/main/resources/openApi/openapi.yml";

  private static final int PORT = 9876;
  private static final String WIREMOCK_URL = "http://localhost:" + PORT;

  private final OpenApiValidationListener validationListener = new OpenApiValidationListener(specPath);

  private final UUID departmentId = UUID.fromString("6a7e41b9-cacf-44f4-95b7-af1fdd60f3c8");

  //This test doesn't have to be devoted to generating a pact. It can be just a normal test and we're transparently enforcing the contract!
  @Test
  public void testGetValidEmployees() {
    String urlPath = "/demo-service/v1/departments/"+ departmentId +"/employees";
    String resp = new JSONObject(Collections.singletonMap("employees", new JSONArray(Collections.singletonList(createEmployeeFullData())))).toString();
    System.out.println("response: " + resp);
    WireMockServer wireMockServer = new WireMockServer(PORT);
    wireMockServer.start();
    wireMockServer.addMockServiceRequestListener(validationListener);
    wireMockServer.stubFor(
            WireMock.get(urlPath)
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("content-type", "application/json")
                            .withBody(resp)
                    ));

    final Response response = given().get(WIREMOCK_URL + urlPath);

    assertThat(response.getStatusCode()).isEqualTo(200);
    validationListener.assertValidationPassed();
  }

  @After
  public void teardown() {
    validationListener.reset();
  }

  private Employee createEmployeeFullData() {
    Employee emp = new Employee();
    emp.setFirstName("Simone");
    emp.setEmployeeId(UUID.randomUUID().toString());
    emp.setLastName("Giertz");
    emp.setEmail("simone@best-robots.com");
    return emp;
  }

  private Employee createEmployeeMinData() {
    Employee emp = new Employee();
    emp.setFirstName("Michelle");
    emp.setLastName("Obama");
    emp.setEmail("michelle.yeoh@goat.com");
    return emp;
  }
}
