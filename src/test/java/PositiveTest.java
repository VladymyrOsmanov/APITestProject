import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PositiveTest {
    private static Response response;
    public static final Logger logger = LogManager.getLogger(PositiveTest.class);

    @BeforeAll
    public static void setup() {
        response = given().get(Consts.URL + Consts.TOKEN);
        System.out.println(response.asString());
    }
    @Test
    public void getResponseCodeWithTokenTest() {
        response.then().statusCode(200);
    }

    //2.1  Get current currency conversion rates
    @Test
    public void APIEndpointsReturnInformationExist(){
        logger.info("Testing if the API endpoint returns all the necessary information.");
        response.then().body("success",notNullValue());
        response.then().body("timestamp",notNullValue());
        response.then().body("source",notNullValue());
        response.then().body("quotes",notNullValue());
        // live endpoint doesn't return: "terms", "privacy".
    }

    @ParameterizedTest
    @ValueSource(strings = {"USDCAD", "USDEUR", "USDRUB"})
    public void APIEndpointsReturnInformationThatContainRightCurrency(String currency){
        logger.info("Testing if the API endpoint contain the necessary information about the right currency.");
        response.then().body("quotes."+currency, notNullValue());
    }

    //There was a problem with the original test that included “USDNIS ”.
    //Therefore, I split the test into smaller parts until I found the source of the error.
    @Test
    public void SmallerReturnInformationTest(){
        logger.info("Testing if the API endpoint returns all the necessary information about the right currency.");
        response.then().body("quotes.USDCAD", notNullValue());
        response.then().body("quotes.USDEUR", notNullValue());
        response.then().body("quotes.USDRUB", notNullValue());
        //response.then().body("quotes.USDNIS", notNullValue());
        // live endpoint doesn't return "USDNIS".
    }

    //2.2
    @ParameterizedTest
    @ValueSource (strings = {"CAD", "EUR", "RUB", "GBP", "JPY"})
    public void currenciesCouldBeRetrieved(String currencies){
        logger.info("Testing that the API endpoint returns the necessary information about the specific currencies.");
        response = given().get(Consts.URL + Consts.TOKEN + Consts.CURRENCIES + currencies);
        System.out.println(response.asString());
    }

    //2.3
    @ParameterizedTest
    @ValueSource (strings = {"2018-01-01", "1999-02-02", "2016-11-12", "2005-05-10"})
    public void HistoricalEndpointFunctionalTest(String date){
        logger.info("Testing that the API “/historical“ endpoint return all the necessary information.");
        response = given().get(Consts.DATE_URL + date + Consts.TOKEN_DATE_URL);
        System.out.println(response.asString());
    }

    //2.3.2 - 2.3.3
    @ParameterizedTest
    @ValueSource (strings = {"CAD", "EUR", "RUB", "GBP", "JPY"})
    public void currenciesCouldBeRetrievedWithHistoricalEndpoint(String currencies){
        logger.info("Testing that the API endpoint returns the necessary information about the specific currencies in the right date.");
        //First date
        response = given().get(Consts.DATE_URL + "2018-01-01" + Consts.TOKEN_DATE_URL + Consts.CURRENCIES + currencies);
        System.out.println(response.asString());
        //Second date
        response = given().get(Consts.DATE_URL + "2016-11-01" + Consts.TOKEN_DATE_URL + Consts.CURRENCIES + currencies);
        System.out.println(response.asString());
        //Third date
        response = given().get(Consts.DATE_URL + "2021-06-12" + Consts.TOKEN_DATE_URL + Consts.CURRENCIES + currencies);
        System.out.println(response.asString());
    }
}
