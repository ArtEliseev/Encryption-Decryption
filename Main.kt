package encryptdecrypt

import java.io.File

val path = System.getProperty("user.dir")
val separator = File.separator
val cyphertext = mutableListOf<Char>()
var mode = "enc"
var data = ""
var key = 0
var inputFileName = ""
var outputFileName = ""
var alg = "shift"
var source = ""
var base = ""

fun main(args: Array<String>) {
    // parsing arguments
    for (i in args.indices) {
        val next = i + 1
        when (args[i]) {
            "-data" -> data = args[next]
            "-in" -> inputFileName = args[next]
            "-out" -> outputFileName = args[next]
            "-key" -> key = args[next].toInt()
            "-mode" -> {
                if (args[next] == "enc" || args[next] == "dec") mode = args[next]
                else {
                    println("Error. Encryption mode is incorrect!")
                    return
                }
            }
            "-alg" -> {
                if (args[next] == "shift" || args[next] == "unicode") alg = args[next]
                else {
                    println("Error. Encryption algorithm is incorrect!")
                    return
                }
            }
        }
    }

    // setting source (file or data)
    source = if (data != "" && inputFileName != "" || data != "" && inputFileName == "") data
    else inputFileName

    // setting base for enc/dec
    try {
        when (source) {
            data -> base = data
            inputFileName -> base = File("$path$separator$inputFileName").readText()
        }
    } catch (e: NoSuchFileException) {
        println("Error. Input file does not exist!")
        return
    }

    // processing arguments
    process()
}

fun process () {
    var newCharCode = 0
    when (alg) {
       "unicode" -> {
           for (char in base) {
               when (mode) {
                   "enc" -> newCharCode = char.code + key
                   "dec" -> newCharCode = char.code - key
               }
               cyphertext.add(newCharCode.toChar())
           }
       }
       "shift" ->  {
           for (char in base) {
               if (
                   mode == "enc" && char.code in 65..90 && char.code + key in 65..90 ||
                   mode == "enc" && char.code in 97..122 && char.code + key in 97..122
                   ) {
                   newCharCode = char.code + key
               } else if (
                   mode == "enc" && char.code in 65..90 && char.code + key > 90 ||
                   mode == "enc" && char.code in 97..122 && char.code + key > 122
                   ) {
                   newCharCode = (char.code + key) - 26
               } else if (
                   mode == "dec" && char.code in 65..90 && char.code - key in 65..90 ||
                   mode == "dec" && char.code in 97..122 && char.code - key in 97..122
               ) {
                   newCharCode = char.code - key
               } else if (
                   mode == "dec" && char.code in 65..90 && char.code - key < 65 ||
                   mode == "dec" && char.code in 97..122 && char.code - key < 97
               ) {
                   newCharCode = (char.code - key) + 26
               } else {
                   cyphertext.add(char)
                   continue
               }
               cyphertext.add(newCharCode.toChar())
           }
       }
    }
    when (outputFileName) {
        "" -> println(cyphertext.joinToString(""))
        else -> File("$path$separator$outputFileName").writeText(cyphertext.joinToString(""))
    }
}