package models.components.authentication;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CredsFormComponent {

    private final AppiumDriver<MobileElement> appiumDriver;
    private static final By usernameSel = MobileBy.AccessibilityId("input-email");
    private static final By passwordSel = MobileBy.AccessibilityId("input-password");

    public CredsFormComponent(AppiumDriver<MobileElement> appiumDriver) {
        this.appiumDriver = appiumDriver;
    }

    @Step("Input username as {username}")
    public CredsFormComponent inputUsername(String username){
        MobileElement usernameElem = appiumDriver.findElement(usernameSel);
        usernameElem.clear();
        usernameElem.sendKeys(username);
        return this;
    }

    @Step("Input password as {password}")
    public CredsFormComponent inputPassword(String password){
        MobileElement passwordElem = appiumDriver.findElement(passwordSel);
        passwordElem.clear();
        passwordElem.sendKeys(password);
        return this;
    }
}
