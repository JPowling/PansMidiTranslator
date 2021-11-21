package de.pans.webinterface

import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WebAPI(
    val url: String = "ws://localhost:80/",
    val user: String = "remote",
    val pwHash: String = "81dc9bdb52d04dc20036dbd8313ed055",
) {

    lateinit var websocketClient: Dot2WebsocketClient
    var sessionNr = 0
        private set

    init {
        val webSocketClient = Dot2WebsocketClient(url, user, pwHash)

        when(webSocketClient.startClient()){
            ServerResponse.OK ->            log("logged in successfully")
            ServerResponse.TimeOut ->       log("Time out... failed to login")
            ServerResponse.Unauthorized ->  log("Unauthorized... failed to login")
            ServerResponse.Undefined ->     log("an internal error occurred... failed to login")
            else ->                         log("something weired happened")
        }

        sessionNr = webSocketClient.sessionNr
    }


    fun readButtonState(ID: Int): Int {

        var startIndex = arrayOf<Int>()
        var itemsCount = arrayOf<Int>()

        when (ID) {
            in 0..100 -> {
                startIndex = arrayOf(ID, 100, 200)
                itemsCount = arrayOf(1, 0, 0)
            }
            in 101..200 -> {
                startIndex = arrayOf(0, ID, 200)
                itemsCount = arrayOf(0, 1, 0)
            }
            in 201..300 -> {
                startIndex = arrayOf(0, 100, ID)
                itemsCount = arrayOf(0, 0, 1)
            }
        }


        val objString = "{\"requestType\":\"playbacks\"," +
                "\"startIndex\":[${startIndex[0]},${startIndex[1]},${startIndex[2]}]," +
                "\"itemsCount\":[${itemsCount[0]},${itemsCount[1]},${itemsCount[2]}]," +
                "\"pageIndex\":3,\"itemsType\":[2,3,3],\"view\":2,\"execButtonViewMode\":1,\"buttonsViewMode\":0," +
                "\"session\":$sessionNr,\"maxRequests\":1}"
        var obj = JSONObject(objString)



        return 0
    }

    private fun log(obj: Any?) {
        println("${DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())}: ${obj.toString()}")
    }

}