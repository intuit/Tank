package com.intuit.tank.conversation;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum MethodType {
    GET,
    POST;
}
