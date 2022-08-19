package ru.clevertec.ecl.service.health;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity containing node's number and ints status.
 *
 * @author Olga Mailychko
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status {
    private int port;
    private boolean isOk;

    public Status(JsonNode jsonNode) {
        this.port = jsonNode.get("node").intValue();
        this.isOk = (jsonNode.get("isOK") != null && jsonNode.get("isOK").textValue().equals("true"))
                || (jsonNode.get("status") != null && jsonNode.get("status").textValue().equals("UP"));
        System.out.println("");
    }
}
