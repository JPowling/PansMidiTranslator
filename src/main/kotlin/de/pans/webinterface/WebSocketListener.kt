package de.pans.webinterface

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import kotlin.random.Random


fun main(args: Array<String>) {
    Dot2ClientWebSocket().startClient()
}

class Dot2ClientWebSocket : WebSocketListener() {

    private val NORMAL_CLOSURE_STATUS = 1000
    private var isRunning = false

    private var sessionNr = 0
    private var requestCounter = 0
    private var maxRequests = 0


    fun startClient(){
        val httpClient = OkHttpClient()
        val request = Request.Builder().url("ws://localhost:80/").build()

        val listener = Dot2ClientWebSocket()
        val webSocket = httpClient.newWebSocket(request, listener)

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        isRunning = true
        println("onOpen")
        GlobalScope.launch {
            while (isRunning) {
                webSocket.send(Random(0).nextInt().toString())
                Thread.sleep(1000)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        outputData("Receiving $text")

        val obj = JSONObject(text)

        if (obj.has("session")) {
            sessionNr = obj.getInt("session")
            println("updated sessionNr: $sessionNr")
        }

        if (obj.has("status") && obj.getString("status") == "server ready") {
            sendMessage(webSocket, "{\"session\":$sessionNr}")

//            sendMessage(webSocket,"{\"requestType\":\"getdata\",\"data\":\"set,clear,high\",\"session\":$sessionNr,\"maxRequests\":1}")
            println("send session = 0")
        }

        if (obj.has("responseType") && obj.has("result") && obj.getString("responseType") == "login" && obj.getBoolean("result")) {
            println("logged in successfully")
            runtime(webSocket)
        }


        if (obj.has("forceLogin") && obj.getBoolean("forceLogin")) {
            println("send login")
            sessionNr = obj.getInt("session")
            sendMessage(webSocket,
                "{\"requestType\":\"login\",\"username\":\"remote\",\"password\":\"81dc9bdb52d04dc20036dbd8313ed055\",\"session\":$sessionNr,\"maxRequests\":10}\'")


        }


    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        outputData("Receiving bytes : " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        outputData("$code $reason")
        isRunning = false
        println("log: webSocket closed ${System.currentTimeMillis()}")
    }

    private fun outputData(outputString: String) {
        println("web socket$outputString")
    }


    fun runtime(webSocket: WebSocket) {


        println("runtime")

        GlobalScope.launch {
            sendMessage(webSocket,
                "{\"requestType\":\"playbacks\",\"startIndex\":[1,105,200],\"itemsCount\":[0,1,0],\"pageIndex\":3,\"itemsType\":[2,3,3],\"view\":2,\"execButtonViewMode\":1,\"buttonsViewMode\":0,\"session\":$sessionNr,\"maxRequests\":1}"
            )
        }


    }


    fun sendMessage(webSocket: WebSocket, msg: String) {
        GlobalScope.launch {
            println("sending Message: $msg")
            webSocket.send(msg)
        }
    }
}