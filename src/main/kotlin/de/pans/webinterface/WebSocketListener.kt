package de.pans.webinterface

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import kotlin.random.Random


fun main(args: Array<String>) {
    val httpClient = OkHttpClient()
    val request = Request.Builder().url("ws://localhost:80/").build()

    val listener = EchoWebSocketListener()
    val webSocket = httpClient.newWebSocket(request, listener)

    Thread.sleep(2000)
//
//    listener.sendMessage(webSocket,"{\"requestType\":\"playbacks\",\"startIndex\":[0,100,200],\"itemsCount\":[5,5,5],\"pageIndex\":0,\"itemsType\":[2,3,3],\"view\":2,\"execButtonViewMode\":1,\"buttonsViewMode\":0,\"session\":1,\"maxRequests\":1}")

}

class EchoWebSocketListener : WebSocketListener() {

    private val NORMAL_CLOSURE_STATUS = 1000
    private var isRunning = false

    private var sessionNr = 0


    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
//         webSocket.send("Hello It is me")
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
            sendMessage(webSocket,"{\"session\":$sessionNr}")

//            sendMessage(webSocket,"{\"requestType\":\"getdata\",\"data\":\"set,clear,high\",\"session\":$sessionNr,\"maxRequests\":1}")
            println("send session = 0")
        }


        if (obj.has("forceLogin") && obj.getBoolean("forceLogin")) {
            println("send login")
            sessionNr = obj.getInt("session")
            sendMessage(webSocket,"{\"requestType\":\"login\",\"username\":\"remote\",\"password\":\"81dc9bdb52d04dc20036dbd8313ed055\",\"session\":$sessionNr,\"maxRequests\":10}\'")
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
//        val jsonString = outputString.substringAfter("Receiving ")
//        try {
//            val jsonObj = JSONObject(jsonString)
//
//            println("current JSON: $jsonObj")
//            val value = jsonObj.getJSONArray("itemGroups").getJSONObject(0)
//                .getJSONArray("items").getJSONArray(3)
//                .getJSONObject(0)
//
//
//            println(value)
////                .getJSONArray("executorBlocks").getJSONObject(0)
////                .getJSONObject("fader").get("vT")
//            print("the fader is at $value")
//
//        }catch (e: Exception){
//            println(e)
//            println("----------------")
//        }


    }

    public fun sendMessage(webSocket: WebSocket, msg: String) {
        GlobalScope.launch {
            println("sending Message: $msg")
            webSocket.send(msg)
        }


    }
}