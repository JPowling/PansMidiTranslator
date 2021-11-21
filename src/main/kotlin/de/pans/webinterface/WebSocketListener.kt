package de.pans.webinterface

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.TimeUnit
import kotlin.random.Random


fun main(args: Array<String>) {
//    Dot2WebsocketClient().startClient()
    WebAPI()
}

class Dot2WebsocketClient(
    url: String = "ws://localhost:80/",
    user: String = "remote",
    password: String = "81dc9bdb52d04dc20036dbd8313ed055",
) : WebSocketListener() {

    //    private val NORMAL_CLOSURE_STATUS = 1000
    private var isRunning = false

    private val watingThread = Thread()

    private var loginResponse = SynchronousQueue<ServerResponse>()


    var sessionNr = 0
        private set
    private var requestCounter = 0
    private var maxRequests = 0


    fun startClient(): ServerResponse {
        val httpClient = OkHttpClient()
        val request = Request.Builder().url("ws://localhost:80/").build()

        val listener = Dot2WebsocketClient()
        val webSocket = httpClient.newWebSocket(request, listener)
        log("starting up Client")

//        Thread.sleep(500)
//        println("loginResponse: $loginResponse")


//        waitUntilLoggedIn(5000)

        var retVal = loginResponse.poll(5000, TimeUnit.MILLISECONDS)

        if(retVal == null) retVal = ServerResponse.TimeOut

        return retVal
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        isRunning = true
        log("onOpen")
        GlobalScope.launch {
            while (isRunning) {
                webSocket.send(Random(0).nextInt().toString())
                Thread.sleep(1000)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)

        log("in  $text")

        val obj = JSONObject(text)

        if (obj.has("status")) {
            when (obj.getString("status")) {
                "server ready" -> sendMessage(webSocket, "{\"session\":$sessionNr}")
            }
        }

        if (obj.has("session")) {
            sessionNr = obj.getInt("session")
            log("in  sessionNr=$sessionNr")
        }

        if (obj.has("forceLogin")) {
            when (obj.getBoolean("forceLogin")) {
                true -> {
                    sendMessage(webSocket,
                        "{\"requestType\":\"login\",\"username\":\"remote\",\"password\":\"81dc9bdb52d04dc20036dbd8313ed055\",\"session\":$sessionNr,\"maxRequests\":10}\'")
                }
            }
        }

        if (obj.has("responseType") && obj.getString("responseType") == "login") {
            if (obj.has("result") && obj.getBoolean("result")) {
                log("logged in successfully------")


                println("loginResponse: putting OK $loginResponse")
                try {
                    loginResponse.put(ServerResponse.OK)
                } catch (e: Exception) {
                    println(e)
                }
                watingThread.stop()

//                runtime(webSocket)

            } else {
                log("failed to log in")
                try {
                    loginResponse.put(ServerResponse.Unauthorized)
                } catch (e: Exception) {
                    println(e)
                }
                watingThread.stop()

            }
        }
}

override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
    super.onMessage(webSocket, bytes)
    log("in:  bytes: ${bytes.hex()}")
}

override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
    super.onClosing(webSocket, code, reason)
    log("closing: $code $reason")
    isRunning = false
    log("webSocket closed")
}

fun runtime(webSocket: WebSocket) {
    GlobalScope.launch {
        sendMessage(webSocket,
            "{\"requestType\":\"playbacks\",\"startIndex\":[1,105,200],\"itemsCount\":[0,1,0],\"pageIndex\":3,\"itemsType\":[2,3,3],\"view\":2,\"execButtonViewMode\":1,\"buttonsViewMode\":0,\"session\":$sessionNr,\"maxRequests\":1}"
        )
    }
}


fun sendMessage(webSocket: WebSocket, msg: String) {
    GlobalScope.launch {
        webSocket.send(msg)
        log("out $msg")
    }
}

private fun log(obj: Any?) {
    println("${
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())
    }: ${obj.toString()}")
}

private fun waitUntilLoggedIn(timeout: Long) {
//        val endTime = System.currentTimeMillis() + timeout

    watingThread.run {
        Thread.sleep(timeout)
        println("loginResponse: putting TimeOut $loginResponse")
        try {
            loginResponse.add(ServerResponse.TimeOut)
        } catch (e: Exception) {
            println(e)
        }
    }
    return
}
}

enum class ServerResponse(i: Int) {
    Undefined(-1),
    OK(200),
    Unauthorized(401),
    TimeOut(408)
}