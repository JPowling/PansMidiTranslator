package de.pans.webinterface

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ArrayBlockingQueue


fun main(args: Array<String>) {
    val client = WebSocket()
    val loginState = client.login("81dc9bdb52d04dc20036dbd8313ed055")

    println(loginState)

    while (true) {
    }
}

class WebSocket : WebSocketClient(URI("ws://127.0.0.1/?ma=1")) {

    private var isRunning = false
    var sessionNr = 0
        private set
    private var requestCounter = 0
    private var maxRequests = 0

    lateinit var passwd: String

    val loginResponse = ArrayBlockingQueue<ServerResponse>(1)

    fun login(passwd: String): ServerResponse {
        this.passwd = passwd
        connect()
        val response = loginResponse.take()
        println(response)
        return loginResponse.take()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
    }

    override fun onMessage(message: String?) {
        log("in  $message")

        val json = JSONObject(message)

        if (json.has("status")) {
            when (json.getString("status")) {
                "server ready" -> send("{\"session\":$sessionNr}")
            }
        }

        if (json.has("session")) {
            sessionNr = json.getInt("session")
            log("in  sessionNr=$sessionNr")
        }

        if (json.has("forceLogin")) {
            when (json.getBoolean("forceLogin")) {
                true -> {
                    send(
                        "{\"requestType\":\"login\",\"username\":\"remote\",\"password\":\"$passwd\",\"session\":$sessionNr,\"maxRequests\":10}\'"
                    )
                }
            }
        }

        if (json.has("responseType") && json.getString("responseType") == "login") {
            if (json.has("result") && json.getBoolean("result")) {
                log("logged in successfully------")
                log("loginResponse: putting OK ${json.getString("responsetype")}")

                loginResponse.put(ServerResponse.OK)
            } else {
                log("failed to log in")

                loginResponse.put(ServerResponse.Undefined)
            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
    }

    override fun onError(ex: Exception?) {
    }
}

private fun log(obj: Any?) {
    println(
        "${
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())
        }: ${obj.toString()}"
    )
}

enum class ServerResponse(i: Int) {
    Undefined(-1),
    OK(200),
    Unauthorized(401),
    TimeOut(408)
}

