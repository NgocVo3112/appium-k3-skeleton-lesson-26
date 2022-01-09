package models.components.authentication;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.internal.CapabilityHelpers;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class LoginDialogComponent {

    private final AppiumDriver<MobileElement> appiumDriver;
    private static final By okBtnSel = MobileBy.AccessibilityId("OK");

    @AndroidFindBy(id = "android:id/alertTitle")
    @iOSXCUITFindBy(iOSNsPredicate = "label == \"Success\"")
    private MobileElement msgTitle;

    @AndroidFindBy(id = "android:id/button1")
    @iOSXCUITFindBy(accessibility = "OK")
    private MobileElement okBtn;

    public LoginDialogComponent(AppiumDriver<MobileElement> appiumDriver) {
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(new AppiumFieldDecorator(appiumDriver, Duration.ofSeconds(10)), this);
    }

    public String msgTitle(){
        return msgTitle.getText();
    }

    public void clickOnOKBtn(){
        Capabilities caps = this.appiumDriver.getCapabilities();
        String platform = CapabilityHelpers.getCapability(caps, "platformName", String.class);
        if(platform.equalsIgnoreCase("ios")) okBtn.click();
        okBtn.click();
    }

}
