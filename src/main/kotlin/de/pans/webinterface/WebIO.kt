package de.pans.webinterface

import de.pans.webinterface.executer.ExecButtonType
import de.pans.webinterface.executer.ExecFaderType
import org.json.JSONException
import java.lang.Exception
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

    fun disconnect() {
        webSocket.close()
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
    ): Int {
        var index = -1

        when (execIndex) {
            in 0..99 -> {
                webSocket.send(
                    "{\"requestType\":\"playbacks\"," +
                            "\"startIndex\":[$execIndex,100,200]," +
                            "\"itemsCount\":[1,0,0]," +
                            "\"pageIndex\":$pageIndex," +
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
                            "\"pageIndex\":$pageIndex," +
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
                            "\"pageIndex\":$pageIndex," +
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

        val response = webSocket.playbacksResponseQueue.poll(1000, TimeUnit.MILLISECONDS)

        try {
            when (index) {
                0 -> {
                    return response.getJSONArray("itemGroups").getJSONObject(index).getJSONArray("items")
                        .getJSONArray(0)
                        .getJSONObject(0).getInt("isRun")
                }
                in 1..2 -> {
                    return response.getJSONArray("itemGroups").getJSONObject(index).getJSONArray("items")
                        .getJSONArray(0)
                        .getJSONObject(0).getInt("isRun")
                }
            }
        } catch (e: Exception) {
            log(e)
        }

        return -1
    }

    fun readButtonType(
        execIndex: Int,
        buttonID: Int,
        pageIndex: Int,
    ): ExecButtonType {
        var index = -1

        when (execIndex) {
            in 0..99 -> {
                webSocket.send(
                    "{\"requestType\":\"playbacks\"," +
                            "\"startIndex\":[$execIndex,100,200]," +
                            "\"itemsCount\":[1,0,0]," +
                            "\"pageIndex\":$pageIndex," +
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
                            "\"pageIndex\":$pageIndex," +
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
                            "\"pageIndex\":$pageIndex," +
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

        val response = webSocket.playbacksResponseQueue.poll(1000, TimeUnit.MILLISECONDS)

        try {
            when (index) {
                0 -> {
                    return ExecButtonType.getByType(response.getJSONArray("itemGroups").getJSONObject(index)
                        .getJSONArray("items")
                        .getJSONArray(0)
                        .getJSONObject(0).getJSONArray("executorBlocks").getJSONObject(0)
                        .getJSONObject("button$buttonID").getString("Toggle"))
                }
                in 1..2 -> {
                    return ExecButtonType.getByType(response.getJSONArray("itemGroups").getJSONObject(index)
                        .getJSONArray("items").getJSONArray(0)
                        .getJSONObject(0).getJSONObject("bottomButtons").getJSONArray("items").getJSONObject(0)
                        .getJSONObject("n").getString("t"))
                }
            }
        } catch (e: JSONException) {
//            log(e)
            return ExecButtonType.EMPTY
        }

        return ExecButtonType.UNDIFINED

    }

    fun readFaderPos(
        execIndex: Int,
        pageIndex: Int,
    ): Double {

        var index = -1

        when (execIndex) {
            in 0..99 -> {
                webSocket.send(
                    "{\"requestType\":\"playbacks\"," +
                            "\"startIndex\":[$execIndex,100,200]," +
                            "\"itemsCount\":[1,0,0]," +
                            "\"pageIndex\":$pageIndex," +
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
                            "\"pageIndex\":$pageIndex," +
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
                            "\"pageIndex\":$pageIndex," +
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

        if (response.has("itemGroups")) {
            val indexObj = response.getJSONArray("itemGroups").getJSONObject(index)
            if (indexObj.has("items")) {
                val itmes_0_0 = indexObj.getJSONArray("items").getJSONArray(0).getJSONObject(0)
                if (itmes_0_0.has("executorBlocks")) {
                    val execBlocks_0 = itmes_0_0.getJSONArray("executorBlocks").getJSONObject(0)
                    if (execBlocks_0.has("fader")) {
                        val fader = execBlocks_0.getJSONObject("fader")
                        if (fader.has("v")) {
                            faderVal = fader.getDouble("v")
//                            println("faderVal:$faderVal")
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
    ): String {
        var index = -1

        when (execIndex) {
            in 0..99 -> {
                webSocket.send(
                    "{\"requestType\":\"playbacks\"," +
                            "\"startIndex\":[$execIndex,100,200]," +
                            "\"itemsCount\":[1,0,0]," +
                            "\"pageIndex\":$pageIndex," +
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
            else -> index - 1
        }

        var faderPos = ""

        val response = webSocket.playbacksResponseQueue.poll(1000, TimeUnit.MILLISECONDS)

        if (response.has("itemGroups")) {
            val indexObj = response.getJSONArray("itemGroups").getJSONObject(index)
            if (indexObj.has("items")) {
                val itmes_0_0 = indexObj.getJSONArray("items").getJSONArray(0).getJSONObject(0)
                if (itmes_0_0.has("executorBlocks")) {
                    val execBlocks_0 = itmes_0_0.getJSONArray("executorBlocks").getJSONObject(0)
                    if (execBlocks_0.has("fader")) {
                        val fader = execBlocks_0.getJSONObject("fader")
                        if (fader.has("vT")) {
                            faderPos = fader.getString("vT")
                        }
                    }
                }
            }
        }
        return faderPos
    }

    fun readFaderType(
        execIndex: Int,
        pageIndex: Int,
    ): ExecFaderType {
        var index = -1

        when (execIndex) {
            in 0..99 -> {
                webSocket.send(
                    "{\"requestType\":\"playbacks\"," +
                            "\"startIndex\":[$execIndex,100,200]," +
                            "\"itemsCount\":[1,0,0]," +
                            "\"pageIndex\":$pageIndex," +
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
            else -> index - 1
        }

        var faderType = ""

        val response = webSocket.playbacksResponseQueue.poll(1000, TimeUnit.MILLISECONDS)

        if (response.has("itemGroups")) {
            val indexObj = response.getJSONArray("itemGroups").getJSONObject(index)
            if (indexObj.has("items")) {
                val itmes_0_0 = indexObj.getJSONArray("items").getJSONArray(0).getJSONObject(0)
                if (itmes_0_0.has("executorBlocks")) {
                    val execBlocks_0 = itmes_0_0.getJSONArray("executorBlocks").getJSONObject(0)
                    if (execBlocks_0.has("fader")) {
                        val fader = execBlocks_0.getJSONObject("fader")
                        if (fader.has("vT")) {
                            faderType = fader.getString("tt")
                        }
                    }
                }
            }
        }
        return ExecFaderType.getByType(faderType)
    }

    private fun log(obj: Any?) {
        println(
            "${
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())
            }: ${obj.toString()}"
        )
    }
}
