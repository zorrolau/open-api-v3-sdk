package com.okcoin.commons.okex.open.api.bean.websocket.param;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MarkCandleChannelEnum {
//    candle60s
//    candle180s
//            candle300s
//    candle900s
//            candle1800s
//    candle3600s
//            candle7200s
//    candle14400s
//            candle21600s
//    candle43200s
//            candle86400s
//    candle604800s
    MARK_CANDLE_60("mark_candle60s"),
    MARK_CANDLE_180("mark_candle180s"),
    MARK_CANDLE_300("mark_candle300s"),
    MARK_CANDLE_900("mark_candle900s"),
    MARK_CANDLE_1800("mark_candle1800s"),
    MARK_CANDLE_3600("mark_candle3600s"),
    MARK_CANDLE_7200("mark_candle7200s"),
    MARK_CANDLE_14400("mark_candle14400s"),
    MARK_CANDLE_21600("mark_candle21600s"),
    MARK_CANDLE_43200("mark_candle43200s"),
    MARK_CANDLE_86400("mark_candle86400s"),
    MARK_CANDLE_604800("mark_candle604800s");


    @Getter
    private final String value;
}
