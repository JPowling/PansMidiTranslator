package de.pans.webinterface

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.ConnectException
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ArrayBlockingQueue
import kotlin.random.Random


fun main(args: Array<String>) {
    val client = WebSocket()
    val loginState = client.login("81dc9bdb52d04dc20036dbd8313ed045", 3000)

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
    var isLoggedIn = false

    fun login(passwd: String, timeout: Long): ServerResponse {
        GlobalScope.launch {
            Thread.sleep(timeout)

            if (!isLoggedIn && isRunning) {
                loginResponse.put(ServerResponse.TimeOut)
                println("Tomute")
            }
        }

        this.passwd = passwd
        try {
            connect()
        } catch (e: ConnectException) {
            println(e)
        }
        return loginResponse.take()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        isRunning = true
        log("onOpen")
        GlobalScope.launch {
            while (isRunning) {
                send(Random(0).nextInt().toString())
                Thread.sleep(1000)
            }
        }
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

            if (sessionNr == -1) {
                loginResponse.put(ServerResponse.GONE)
                close()
            }

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
                log("logged in successfully")
                log("loginResponse: putting OK ${json.getString("responseType")}")

                loginResponse.put(ServerResponse.OK)
                isLoggedIn = true
            } else {
                log("failed to log in")

                loginResponse.put(ServerResponse.Unauthorized)
                isRunning = false
                close()
            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        log("closing: $code $reason")
        isRunning = false
        log("webSocket closed")
    }

    override fun onError(ex: Exception?) {
        isRunning = false
        ex?.printStackTrace()
    }
}

private fun log(obj: Any?) {
    println(
        "${
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())
        }: ${obj.toString()}"
    )
}

enum class ServerResponse(val i: Int) {
    Undefined(-1),
    OK(200),
    Unauthorized(401),
    TimeOut(408),
    GONE(410),
    ;
    override fun toString(): String {
        return super.toString() + "-$i"
    }
}
