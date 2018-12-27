package com.okcoin.commons.okex.open.api.bean.websocket.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Depth {
    private String contract;
    private List<List<Double>> asks;
    private List<List<Double>> bids;
    private String timestamp;
    private String checksum;
}
