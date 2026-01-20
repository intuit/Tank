package com.intuit.tank.conversation;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum Protocol {
    http,
    https,
    ws,
    wss;
}
