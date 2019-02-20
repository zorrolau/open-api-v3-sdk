#include <iostream>
#include <cpprest/http_client.h>
#include "okapi.h"
#include "okapi_ws.h"
#include <algorithm>

string instrument_id = "BCH-USD-181228";
string order_id = "1641326222656512";
string currency  = "bch";

int main(int argc, char *args[]) {
    OKAPI okapi;
    /************************** set config **********************/
    struct Config config;
    config.SecretKey = "";
    config.ApiKey = "";
    config.Endpoint = "https://www.okex.com";
    config.I18n = "en_US";
    config.IsPrint = true;
    config.Passphrase = "";

    okapi.SetConfig(config);
    /************************** test examples **********************/
    if (0) {
        okapi.GetServerTime();
        okapi.GetCurrencies();
        okapi.GetWalletCurrency(currency);
        okapi.GetWithdrawFee();
    }
    value obj  = value::object(true);
    obj["instrument_id"] = value::string(instrument_id);
    obj["direction"] = value::string("long");

    value obj2;
    obj2["afds"] = value::string("sa");

    value abc = value::array();
    abc[0] = obj;
    abc[1] = obj2;
    cout << abc.serialize() << std::endl;

    value obj3;
    obj3["asf"] = abc;
    cout << obj3.serialize() << std::endl;

    value obj4 = value::object(true);
    obj4["afdf"] = obj;
    cout << obj4.serialize() << std::endl;

    /************************** futures test examples **********************/
    if (0) {
        value obj;
        okapi.GetFuturesPositions();
        okapi.GetFuturesProductPosition(instrument_id);
        okapi.GetFuturesAccountsByCurrency(currency);
        okapi.GetFuturesLeverageByCurrency(currency);
        obj["instrument_id"] = value::string(instrument_id);
        obj["direction"] = value::string("long");
        obj["leverage"] = value::string("20");
        okapi.SetFuturesLeverageByCurrency(currency, obj);
        okapi.GetFuturesAccountsLedgerByCurrency(currency);

        obj["instrument_id"] = value::string(instrument_id);
        obj["type"] = value::number(2);
        obj["price"] = value::number(10000.1);
        obj["size"] = value::number(1);
        obj["margin_price"] = value::number(0);
        obj["leverage"] = value::number(10);
        okapi.FuturesOrder(obj);
        okapi.CancelFuturesProductOrder(instrument_id, order_id);

        okapi.GetFuturesOrders("2", instrument_id);
        okapi.GetFuturesOrderList(instrument_id, order_id);
        okapi.GetFuturesFills(instrument_id, order_id);
        okapi.GetFuturesProducts();
        okapi.GetFuturesProductBook(instrument_id, 50);
        okapi.GetFuturesTicker();
        okapi.GetFuturesProductTicker(instrument_id);
        okapi.GetFuturesProductTrades(instrument_id);
        okapi.GetFuturesProductCandles(instrument_id);
        okapi.GetFuturesIndex(instrument_id);
        okapi.GetFuturesRate();
        okapi.GetFuturesProductEstimatedPrice(instrument_id);
        okapi.GetFuturesProductOpenInterest(instrument_id);
        okapi.GetFuturesProductPriceLimit(instrument_id);
        okapi.GetFuturesProductLiquidation(instrument_id, 0);
        okapi.GetFuturesProductHolds(instrument_id);
    }


    /************************** websocket test examples **********************/
    if (0) {
        string uri = U("ws://real.okex.com:10442/ws/v3");
        pplx::create_task([=] {
            okapi_ws::SubscribeWithoutLogin(uri, U("swap/ticker:BTC-USD-SWAP"));
        });
        sleep(20);
        okapi_ws::UnsubscribeWithoutLogin(uri, U("swap/ticker:BTC-USD-SWAP"));

        sleep(20);
        pplx::create_task([=] {
            okapi_ws::Subscribe(uri, U("swap/account:BTC-USD-SWAP"), config.ApiKey, config.Passphrase, config.SecretKey);
        });
        sleep(20);
        okapi_ws::Unsubscribe(uri, U("swap/account:BTC-USD-SWAP"), config.ApiKey, config.Passphrase, config.SecretKey);
    }
    return 0;
}