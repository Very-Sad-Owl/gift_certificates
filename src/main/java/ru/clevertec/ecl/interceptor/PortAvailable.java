package ru.clevertec.ecl.interceptor;

import lombok.Getter;

@Getter
public enum PortAvailable {
    NODE_FIRST("node8080"),
    NODE_SECOND("node8081"),
    NODE_THIRD("node8082");

    private String portName;

    PortAvailable(String portName) {
        this.portName = portName;
    }
}
