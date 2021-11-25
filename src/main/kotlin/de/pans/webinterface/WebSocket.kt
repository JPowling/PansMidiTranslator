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


class WebSocket(var ipAddress: String = "localhost") :
    WebSocketClient(URI("ws://$ipAddress/?ma=1")) {

    private var isRunning = false
    var sessionNr = 0
        private set
    private var requestCounter = 0
    private var maxRequests = 0

    lateinit var userName: String
    lateinit var passwd: String

    private val loginResponseQueue = ArrayBlockingQueue<ServerResponse>(1)
    val playbacksResponseQueue = ArrayBlockingQueue<JSONObject>(10)
    private var isLoggedIn = false

    fun login(
        ipAddress: String = this.ipAddress,
        userName: String = "remote",
        passwd: String = "81dc9bdb52d04dc20036dbd8313ed055",
        timeout: Long = 3000,
    ): ServerResponse {
        super.uri = URI("ws://$ipAddress/?ma=1")

        GlobalScope.launch {
            Thread.sleep(timeout)

            if (!isLoggedIn && isRunning) {
                loginResponseQueue.put(ServerResponse.TimeOut)
            }
        }

        this.userName = userName
        this.passwd = passwd

        try {
            connect()
        } catch (e: ConnectException) {
            log(e)
            loginResponseQueue.put(ServerResponse.INTERNAL_SERVER_ERROR)
        }
        return loginResponseQueue.take()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        isRunning = true
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
                loginResponseQueue.put(ServerResponse.GONE)
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
                //logged in successfully
//                log("loginResponse: putting OK ${json.getString("responseType")}")

                loginResponseQueue.put(ServerResponse.OK)
                isLoggedIn = true
            } else {
                // failed to login
                loginResponseQueue.put(ServerResponse.Unauthorized)
                isRunning = false
                close()
            }
        }

        if (json.has("responseType") && json.getString("responseType") == "playbacks") {
            log("putting message in playbacksResponseQueue")
            playbacksResponseQueue.put(JSONObject(message))
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

    override fun send(text: String?) {
        super.send(text)
        log("out $text")
    }

}

private fun log(obj: Any?) {
//    println(
//        "${
//            DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())
//        }: ${obj.toString()}"
//    )
}

enum class ServerResponse(val i: Int) {
    Undefined(-1),
    OK(200),
    Unauthorized(401),
    TimeOut(408),
    GONE(410),
    INTERNAL_SERVER_ERROR(500)
    ;

    override fun toString(): String {
        return super.toString() + "-$i"
    }
}