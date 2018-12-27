package com.okcoin.commons.okex.open.api.bean.websocket.param;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CandleChannelEnum {
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
    CANDLE_60("candle60s"),
    CANDLE_180("candle180s"),
    CANDLE_300("candle300s"),
    CANDLE_900("candle900s"),
    CANDLE_1800("candle1800s"),
    CANDLE_3600("candle3600s"),
    CANDLE_7200("candle7200s"),
    CANDLE_14400("candle14400s"),
    CANDLE_21600("candle21600s"),
    CANDLE_43200("candle43200s"),
    CANDLE_86400("candle86400s"),
    CANDLE_604800("candle604800s");


    @Getter
    private final String value;
}
