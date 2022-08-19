package ru.clevertec.ecl.interceptor.common;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.clevertec.ecl.interceptor.common.RequestEditor.markUrlAsRedirected;
import static ru.clevertec.ecl.interceptor.common.RequestParams.REPLICATED_PARAM;

/**
 * Contains server properties of application.
 *
 * See also {@link ConfigurationProperties}.
 *
 * @author Olga Mailychko
 *
 */
@Component
@ConfigurationProperties(prefix = "server")
@RequiredArgsConstructor
@Data
public class ClusterProperties {
    /**
     * Current host, 'localhost' by default.
     */
    private String host;
    /**
     * Port the application is run on.
     */
    private int port;
    /**
     * Main nodes' ports.
     */
    private List<Integer> sourcesPort;
    /**
     * Map containing main nodes' ports as key and all related replicas including the main one.
     */
    private Map<Integer, List<Integer>> replicas;
    /**
     * Map containing main nodes' ports as key and all related replicas excluding the main one.
     */
    private Map<Integer, List<Integer>> cluster;

    /**
     * Defines one of main ports on which entity with given id must be saved by certain rule.
     * We define needed port's index in the list by mod operation between given id and number of main nodes.
     *
     * @param id id of some entity needed to be stored
     * @return port of some main node which is responsible for storing entity with given id
     */
    public int definePortById(long id) {
        int portsNumber = sourcesPort.size();
        long destinationPortIndex = id % portsNumber;
        return sourcesPort.get((int) destinationPortIndex);
    }

    /**
     * Replaces one port to another in given request string.
     *
     * @param requestString URL of request represented as {@link StringBuffer} object.
     * @param replaced port value needed to be replaced
     * @param replacer replacer port value
     * @return updated request string represented as {@link StringBuffer} object.
     */
    public static StringBuffer changePort(StringBuffer requestString, int replaced, int replacer, List<Integer> replicateTo) {
        return new StringBuffer(
                markUrlAsRedirected(
                        new StringBuffer(requestString.toString().replaceAll(replaced + "", replacer + "")),
                        replicateTo
                )
        );
    }

    public static StringBuffer changePort(StringBuffer requestString, int replaced, int replacer) {
        return new StringBuffer(
                markUrlAsRedirected(
                        new StringBuffer(requestString.toString().replaceAll(replaced + "", replacer + "")),
                        new ArrayList<>()
                )
        );
    }

    /**
     * Replaces one port to another in given request string keeping 'id' request parameter.
     *
     * @param requestString URL of request represented as {@link StringBuffer} object.
     * @param replaced port value needed to be replaced
     * @param replacer replacer port value
     * @param id replacer port value
     * @return updated request string represented as {@link StringBuffer} object.
     */
    public static StringBuffer changePort(StringBuffer requestString, int replaced, int replacer, long id, List<Integer> replicateTo) {
        return new StringBuffer(
                markUrlAsRedirected(
                        new StringBuffer(requestString.toString().replaceAll(replaced + "", replacer + "")
                                .replaceAll(REPLICATED_PARAM, "")
                                + "?id=" + id), replicateTo
                )
        );
    }

    public static StringBuffer changePort(StringBuffer requestString, int replaced, int replacer, long id) {
        return new StringBuffer(
                markUrlAsRedirected(
                        new StringBuffer(requestString.toString().replaceAll(replaced + "", replacer + "")
                                .replaceAll(REPLICATED_PARAM, "")
                                + "?id=" + id), new ArrayList<>()
                )
        );
    }

    /**
     * Defines which main main node belongs the node with given port.
     *
     * @param port port of some node which belonging must be defined
     * @return main node's port to which node with given port belongs
     */
    public int defineNodeByPort(int port) {
        return cluster.entrySet().stream()
                .filter(val -> val.getValue().contains(port))
                .findAny()
                .get()
                .getKey();

    }
}
