package com.okcoin.commons.okex.open.api.config;

import com.okcoin.commons.okex.open.api.enums.I18nEnum;
import lombok.Data;

import java.util.List;

@Data
public class WebSocketConfig {
    /**
     * Rest api endpoint url.
     */
    private String url;
    /**
     * The user's api key provided by OKEx.
     */
    private String apiKey;
    /**
     * The user's secret key provided by OKEx. The secret key used to sign your request data.
     */
    private String secretKey;
    /**
     * The Passphrase will be provided by you to further secure your API access.
     */
    private String passphrase;
    /**
     * Channel's name
     **/
    private I18nEnum i18n;

    private boolean print;

    private List<String> list;

    public WebSocketConfig(){}

    public WebSocketConfig(String url){
        this.url = url;
        this.print = false;
        this.i18n = I18nEnum.ENGLISH;
    }
}
