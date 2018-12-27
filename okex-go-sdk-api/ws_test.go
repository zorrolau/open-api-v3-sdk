package okex

/*
 OKEX ws api websocket test & sample
 @author Lingting Fu
 @date 2018-12-27
 @version 1.0.0
*/

import (
	"testing"
	"time"
)

func TestOKWSAgent_AllInOne(t *testing.T) {
	agent := OKWSAgent{}
	config := GetDefaultConfig()

	// Step1: Start agent.
	agent.Start(config)

	// Step2: Subscribe channel
	// Step2.0: Subscribe public channel swap/ticker successfully.
	agent.Subscribe("swap/ticker", "BTC-USD-SWAP", DefaultDataCallBack)

	// Step2.1: Subscribe private channel swap/position before login, so it would be a fail.
	agent.Subscribe("swap/position", "BTC-USD-SWAP", DefaultDataCallBack)

	// Step3: Wait for the ws server's pushed table responses.
	time.Sleep(60 * time.Second)

	// Step4. Unsubscribe public channel swap/ticker
	agent.UnSubscribe("swap/ticker", "BTC-USD-SWAP")
	time.Sleep(1 * time.Second)

	// Step5. Login
	agent.Login(config.ApiKey, config.Passphrase)

	// Step6. Subscribe private channel swap/position after login, so it would be a success.
	agent.Subscribe("swap/position", "BTC-USD-SWAP", DefaultDataCallBack)
	time.Sleep(120 * time.Second)

	// Step7. Stop all the go routine run in background.
	agent.Stop()
	time.Sleep(1 * time.Second)
}
