package test.authentication;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import test.BaseTest;
import test_data.authentication.LoginCreds;
import test_data.DataObjectBuilder;
import test_flows.authentication.LoginFlow;

public class LoginTest extends BaseTest {

    @TmsLink("Login_001") @TmsLink("Login_002") @TmsLink("Login_003")
    @Description("Test login with data driven...")
    @Test(dataProvider = "invalidLoginCreds", description = "Login Test", priority = 1)
    public void loginWithIncorrectCreds(LoginCreds loginCreds) {
        // Init driver
        AppiumDriver<MobileElement> appiumDriver = getDriver();
        LoginFlow loginFlow = new LoginFlow(appiumDriver);
        loginFlow
                .navigateToLoginForm()
                .login(loginCreds)
                .verifyLoginWithIncorrectCreds();
    }

    @TmsLink("Login_004")
    @Description("Test login with correct creds...")
    @Test(description = "Login Test", priority = 2)
    public void loginWithcorrectCreds() {
        String jsonLoc = "/test-data/authentication/ValidLoginCreds.json";
        LoginCreds loginCreds = DataObjectBuilder.buildDataObject(jsonLoc, LoginCreds[].class)[0];

        // Init driver
        AppiumDriver<MobileElement> appiumDriver = getDriver();
        LoginFlow loginFlow = new LoginFlow(appiumDriver);
        loginFlow
                .navigateToLoginForm()
                .login(loginCreds)
                .verifyLoginSuccess();
    }

    @Test(groups = {"smoke"})
    public void mySmokeTestMethod(){
        System.out.println("Smoke Test");
    }

    @DataProvider
    public LoginCreds[] invalidLoginCreds() {
        String jsonLoc = "/test-data/authentication/InvalidLoginCreds.json";
        return DataObjectBuilder.buildDataObject(jsonLoc, LoginCreds[].class);
    }

}
