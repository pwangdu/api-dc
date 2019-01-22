package in.disruptech.platform.cucumber.stepdefs;

import in.disruptech.platform.PlatformApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = PlatformApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
