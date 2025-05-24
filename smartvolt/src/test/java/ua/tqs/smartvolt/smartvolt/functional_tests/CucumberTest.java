package ua.tqs.smartvolt.smartvolt.functional_tests;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("ua/tqs/smartvolt/smartvolt")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "ua.tqs.smartvolt.smartvolt.functional_tests")
// @ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@smoke")
public class CucumberTest {}
