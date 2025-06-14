package ua.tqs.smartvolt.smartvolt.steps;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("ua/tqs/smartvolt/smartvolt")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "ua.tqs.smartvolt.smartvolt.steps")
@ConfigurationParameter(key = "cucumber.filter.tags", value = "@UAT-Web")
public class CucumberTest {}
