import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NegativeTest {
    private static Response response;
    public static final Logger logger = LogManager.getLogger(NegativeTest.class);

    //1.2 - 1.3
    @Test
    public void UnauthorizedUsersTest(){
        logger.info("Testing that a user-friendly error returns if the users is unauthorized.");
        // Expected error: "You have not supplied a valid API Access Key."
        response = given().get(Consts.URL+"1111");
        System.out.println(response.asString());
        response.then().statusCode(401);
        // The received message:"Invalid authentication credentials"
    }

    //2.2.3
    @ParameterizedTest
    @ValueSource(strings = {"111", "ABCD", "1A1A", "sasdsdfas", "!#!@$%"})
    public void  IncorrectCurrencyCode(String currencies){
        // Expected error: Code - 202 "User entered one or more invalid currency codes."
        response = given().get(Consts.URL + Consts.TOKEN + Consts.CURRENCIES + currencies);
        System.out.println(response.asString());
        // Actual result: Code - 200 "You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]"
        response.then().statusCode(200);
        // The last test returned code 101 "You have not supplied an API Access Key. [Required format: access_key=YOUR_ACCESS_KEY]"
    }

    //2.3 user-friendly error returns if the parameter is incorrect or missing
    @ParameterizedTest
    @ValueSource (strings = {"201801-01", "1999-mm-02", "20s6-1a-1d", "25-35-33","#@$!%$!#@#"})
    public void HistoricalEndpointFunctionalTest(String date) {
        logger.info("Testing that a user-friendly error returns if the parameter is incorrect or missing.");
        // Expected error: Code - 302 "User entered an invalid date. [historical]"
        response = given().get(Consts.DATE_URL + date + Consts.TOKEN_DATE_URL);
        System.out.println(response.asString());
        response.then().statusCode(200);
        // The last test returned code 101 "You have not supplied an API Access Key. [Required format: access_key=YOUR_ACCESS_KEY]"
    }

    //2.3.4 User-friendly errors are expected to be returned in case of wrong/missing parameters.
    @ParameterizedTest
    @ValueSource (strings = {"111", "sss", "2a3s", "!$@~"})
    public void currenciesCouldBeRetrievedWithHistoricalEndpointUserFriendlyErrors(String currencies){
        logger.info("Testing that the API endpoint returns a user-friendly errors about Currency Codes.");
        //First date
        response = given().get(Consts.DATE_URL + "2018-01-01" + Consts.TOKEN_DATE_URL + Consts.CURRENCIES + currencies);
        System.out.println(response.asString());
        response.then().statusCode(200);
        //Second date
        response = given().get(Consts.DATE_URL + "2016-11-01" + Consts.TOKEN_DATE_URL + Consts.CURRENCIES + currencies);
        System.out.println(response.asString());
        response.then().statusCode(200);
        //Third date
        response = given().get(Consts.DATE_URL + "2021-06-12" + Consts.TOKEN_DATE_URL + Consts.CURRENCIES + currencies);
        System.out.println(response.asString());
        response.then().statusCode(200);
    }
}
