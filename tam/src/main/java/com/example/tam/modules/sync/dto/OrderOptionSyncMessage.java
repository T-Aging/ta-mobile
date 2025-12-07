package com.example.tam.modules.sync.dto;

public record OrderOptionSyncMessage(
        Integer orderOptionId,
        Integer optionId,
        Integer groupId,
        Integer valueId,
        Integer extraNum,
        Integer extraPrice
) {
}
