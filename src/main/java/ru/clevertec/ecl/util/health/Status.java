package ru.clevertec.ecl.util.health;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status {
    private int port;
    private boolean isOk;

    public Status(JsonNode jsonNode) {
        this.port = jsonNode.get("node").intValue();
        this.isOk = jsonNode.get("status").textValue().equals("UP");
    }
}
