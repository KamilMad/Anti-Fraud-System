package com.example.AntiFraudSystem.utilities;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Status {
    ALLOWED,
    PROHIBITED,
    MANUAL_PROCESSING
}
