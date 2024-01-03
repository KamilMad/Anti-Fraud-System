package com.example.AntiFraudSystem.payload;

import com.example.AntiFraudSystem.utilities.Status;

public record TransactionResponseDTO(
        Status result,
        String info) { }

