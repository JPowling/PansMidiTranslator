package de.pans.webinterface

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class WebIO {

    private val webSocket: WebSocket = WebSocket()

    fun connect(
        ipAddress: String = "localhost",
        userName: String = "remote",
        pwdHash: String = "81dc9bdb52d04dc20036dbd8313ed055",
    ): ServerResponse {
        log("connecting to Dot2 Server... IPAddress=\"$ipAddress\", userName=\"$userName\"")
        val response = webSocket.login(ipAddress, userName, pwdHash)
        log(response)
        return response
    }


    fun sendCMD(cmd: String) {
        webSocket.send(
            "{  \"command\":\"$cmd\"," +
                    "\"session\":${webSocket.sessionNr}," +
                    "\"requestType\":\"command\"," +
                    "\"maxRequests\":0" +
                    "}"
        )
    }

    fun sendButton(
        execIndex: Int,
        buttonID: Int,
        pageIndex: Int,
        isPressed: Boolean,
    ) {
        webSocket.send(
            "{\"requestType\":\"playbacks_userInput\"," +
                    "\"cmdline\":\"\"," +
                    "\"execIndex\":$execIndex," +
                    "\"pageIndex\":$pageIndex," +
                    "\"buttonId\":$buttonID," +
                    "\"pressed\":$isPressed," +
                    "\"released\":${!isPressed}," +
                    "\"type\":0," +
                    "\"session\":${webSocket.sessionNr}," +
                    "\"maxRequests\":0" +
                    "}"
        )
    }

    fun sendFaderPos(
        execIndex: Int,
        pageIndex: Int,
        pos: Double,
    ) {
        webSocket.send(
            "{\"requestType\":\"playbacks_userInput\"," +
                    "\"execIndex\":$execIndex," +
                    "\"pageIndex\":$pageIndex," +
                    "\"faderValue\":$pos," +
                    "\"type\":1,\"" +
                    "session\":${webSocket.sessionNr}," +
                    "\"maxRequests\":0" +
                    "}"
        )
    }

    fun setFaderValue(
        execIndex: Int,
        pageIndex: Int,
        value: Double,
    ) {
        sendCMD("")
    }


    fun readButtonState(
        execIndex: Int,
        buttonID: Int,
        pageIndex: Int,
    ) {
        //TODO
    }

    fun readButtonMode(
        execIndex: Int,
        buttonID: Int,
        pageIndex: Int,
    ) {
        //TODO
    }

    fun readFaderPos(
        execIndex: Int,
        pageIndex: Int,
    ):Double {

        var index = -1

        when (execIndex) {
            in 0..99 -> {
                webSocket.send(
                    "{\"requestType\":\"playbacks\"," +
                            "\"startIndex\":[$execIndex,100,200]," +
                            "\"itemsCount\":[1,0,0]," +
                            "\"pageIndex\":0," +
                            "\"itemsType\":[2,3,3]," +
                            "\"view\":2," +
                            "\"execButtonViewMode\":1," +
                            "\"buttonsViewMode\":0," +
                            "\"session\":${webSocket.sessionNr}," +
                            "\"maxRequests\":1" +
                            "}"
                )
                index = 0
            }
            in 100..199 -> {
                webSocket.send(
                    "{\"requestType\":\"playbacks\"," +
                            "\"startIndex\":[0,$execIndex,200]," +
                            "\"itemsCount\":[0,1,0]," +
                            "\"pageIndex\":0," +
                            "\"itemsType\":[2,3,3]," +
                            "\"view\":2," +
                            "\"execButtonViewMode\":1," +
                            "\"buttonsViewMode\":0," +
                            "\"session\":${webSocket.sessionNr}," +
                            "\"maxRequests\":1" +
                            "}"
                )
                index = 1
            }
            in 200..299 -> {
                webSocket.send(
                    "{\"requestType\":\"playbacks\"," +
                            "\"startIndex\":[0,100,$execIndex]," +
                            "\"itemsCount\":[0,0,1]," +
                            "\"pageIndex\":0," +
                            "\"itemsType\":[2,3,3]," +
                            "\"view\":2," +
                            "\"execButtonViewMode\":1," +
                            "\"buttonsViewMode\":0," +
                            "\"session\":${webSocket.sessionNr}," +
                            "\"maxRequests\":1" +
                            "}"
                )
                index = 2
            }
        }

        var faderVal = -1.0


        val response = webSocket.playbacksResponseQueue.poll(1000, TimeUnit.MILLISECONDS)

//        println("trying, index:$index")
//        println("json: ${response.toString()}")
        var retVal = false
        if (response.has("itemGroups")) {
//            println("found itemGroups")
            val indexObj = response.getJSONArray("itemGroups").getJSONObject(index)
            if (indexObj.has("items")) {
//                println("found items")
                val itmes_0_0 = indexObj.getJSONArray("items").getJSONArray(0).getJSONObject(0)
                if (itmes_0_0.has("executorBlocks")) {
//                    println("found execBlocks")
                    val execBlocks_0 = itmes_0_0.getJSONArray("executorBlocks").getJSONObject(0)
                    if (execBlocks_0.has("fader")) {
//                        println("found fader")
                        val fader = execBlocks_0.getJSONObject("fader")
                        if (fader.has("v")){
//                            println("found v")
                            faderVal = fader.getDouble("v")
                            println("faderVal:$faderVal")
                        }
                    }
                }
            }
        }

        return faderVal

    }

    fun readFaderVal(
        execIndex: Int,
        pageIndex: Int,
    ) {
        //TODO
    }

    fun readFaderMode(
        execIndex: Int,
        pageIndex: Int,
    ) {
        //TODO
    }

    private fun log(obj: Any?) {
        println(
            "${
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())
            }: ${obj.toString()}"
        )
    }
}
