package de.pans

import de.pans.webinterface.WebIO

fun main(args: Array<String>) {
    val webIO = WebIO
    webIO.connect()

//    webIO.sendButton(100, 0,0, true)
//    webIO.sendButton(100, 0,0, false)
//
//    Thread.sleep(1000)
//    webIO.sendButton(100, 0,0, true)
//    webIO.sendButton(100, 0,0, false)
//
//    webIO.sendFaderPos(0, 0, 1.0)
//
//
//    var j: Double
//    for (i in 0..100) {
//        j = i / 100.0
//        webIO.sendFaderPos(0, 0, j)
//        Thread.sleep(100)
//    }

    println("------------------")
    for (i in 0..5){
        println("101 Mode: ${webIO.readButtonType(100 + i, 0, 0)}, State: ${webIO.readButtonState(100 + i, 0, 0)}")
        println("201 Mode: ${webIO.readButtonType(200 + i, 0, 0)}, State: ${webIO.readButtonState(200 + i, 0, 0)}")
        println("1   Mode: ${webIO.readFaderType(0 + i, 0)}, State: ${webIO.readFaderVal(0 + i, 0)}")
        println("1   Mode: ${webIO.readButtonType(0 + i, 1, 0)}, State: ${webIO.readButtonState(0 + i, 1, 0)}")
        println("1   Mode: ${webIO.readButtonType(0 + i, 2, 0)}, State: ${webIO.readButtonState(0 + i, 2, 0)}")
        println("------------------")
    }



//    while (System.currentTimeMillis() < System.currentTimeMillis() + 5000){
//        webIO.readFaderPos(1,0)
//        Thread.sleep(100)
//    }


}