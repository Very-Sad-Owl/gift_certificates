package ru.clevertec.ecl.webutils.clusterproperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes =  ClusterPropertiesConfiguration.class,
        initializers = ConfigDataApplicationContextInitializer.class)
public class ClusterPropertiesTest {

    @Autowired
    private ClusterProperties clusterProperties;

    @Test
    public void changePortTest_8080To8091_8091AndRedirectedMark() {
        int portToChange = 8080;
        int replacerPort = 8091;
        StringBuffer url = new StringBuffer(String.format("localhost:%s/status", portToChange));

        String expected = String.format("localhost:%s/status?redirected=[]", replacerPort);

        String actual = ClusterProperties.changePort(url, portToChange, replacerPort).toString();

        assertEquals(expected, actual);

    }

    @Test
    public void changePortTest_nonExistingPort_markAsRedirectedOnly() {
        int actualPort = 8080;
        int portToChange = 8081;
        int replacerPort = 8091;
        StringBuffer url = new StringBuffer(String.format("localhost:%s/status", actualPort));

        String expected = String.format("localhost:%s/status?redirected=[]", actualPort);

        String actual = ClusterProperties.changePort(url, portToChange, replacerPort).toString();

        assertEquals(expected, actual);

    }

    @Test
    public void definePortByIdTest_1_8080() {
        int id = 1;

        int expected = 8080;

        int actual = clusterProperties.definePortById(id);

        assertEquals(expected, actual);

    }

    @Test
    public void definePortByIdTest_15_8070() {
        int id = 15;

        int expected = 8070;

        int actual = clusterProperties.definePortById(id);

        assertEquals(expected, actual);

    }

    @Test
    public void defineNodeByPortTest_8091_8090() {
        int port = 8091;

        int expected = 8090;

        int actual = clusterProperties.defineNodeByPort(port);

        assertEquals(expected, actual);
    }

    @Test
    public void defineNodeByPortTest_8090_8090() {
        int port = 8090;

        int expected = 8090;

        int actual = clusterProperties.defineNodeByPort(port);

        assertEquals(expected, actual);
    }
}
