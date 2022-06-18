package ru.clevertec.ecl.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "server")
@RequiredArgsConstructor
@Data
public class ClusterProperties {
    private int port;
    private List<Integer> sources_port; //TODO:
    private Map<Integer, List<Integer>> replicas;
    private Map<Integer, List<Integer>> cluster;

    public int definePort(long destination) {
        int portsNum = sources_port.size(); //TODO: full name
        long factor = destination % portsNum; //TODO:
        return sources_port.get((int) factor);
    }

    public static StringBuffer changePort(StringBuffer req, int replaced, int replacer) {
        return new StringBuffer(req.toString().replaceAll(replaced + "", replacer + "")
                .replaceAll("replicated", "")
                + "?redirected=true");
    }

    public int defineNodeByPort(int port) {
        return cluster.entrySet().stream()
                .filter(val -> val.getValue().contains(port))
                .findAny()
                .get()
                .getKey();

    }
}
