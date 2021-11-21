package de.pans.webinterface

import de.pans.webinterface.executer.Button
import org.json.JSONObject

class WebAPI(){

    val sessionNr = 0


    fun readButtonState(ID: Int): Int{

        var startIndex = arrayOf<Int>()
        var itemsCount = arrayOf<Int>()

        when (ID) {
            in 0..100 -> {
                startIndex = arrayOf(ID, 100, 200)
                itemsCount = arrayOf(1,0,0)
            }
            in 101..200 -> {
                startIndex = arrayOf(0, ID, 200)
                itemsCount = arrayOf(0,1,0)
            }
            in 201..300 -> {
                startIndex = arrayOf(0, 100, ID)
                itemsCount = arrayOf(0,0,1)
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


}