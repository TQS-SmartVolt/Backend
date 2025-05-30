package ua.tqs.smartvolt.smartvolt;

import org.springframework.stereotype.Component;

@Component 
public class MyTestConfiguration {

    private static String browserType;
    private static String host;

    static {
        browserType = System.getProperty("browser", "chrome");
        host = System.getProperty("host", "localhost");
    }

    public static String getBrowserType() {
        return browserType;
    }

    public static String getHost() {
        return host;
    }

}
