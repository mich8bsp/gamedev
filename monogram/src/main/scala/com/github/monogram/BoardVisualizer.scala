package com.github.monogram

import java.io.{File, PrintWriter}

import scala.io.Source

object BoardVisualizer {

  def visualize(puzzleName: String): Unit = {
    var board = new Board(0,0)
    var first = true
    var tableHTML = s"<table width=500px height=500px>"
    var currentRow = -1
    for( line <- Source.fromFile(FileResourceSupplier.getPuzzlePath(puzzleName)).getLines()){
      if(first){
        val boardSizeSplit = line.split(",")
        board = new Board(boardSizeSplit(0).toInt, boardSizeSplit(1).toInt)
        first = false
      }else{
        val parsedLine = line.split(",")
        val row = parsedLine(0).toInt
        val col = parsedLine(1).toInt
        board.fillSquare(row, col, parsedLine(2), parsedLine(3))
        if(currentRow != row){
          if(currentRow>=0){
            tableHTML += s"</tr>"
          }
          tableHTML += s"<tr>"
          currentRow = row
        }else{
          tableHTML += s"<td bgcolor=${parsedLine(2)}>${parsedLine(3)}</td>"
        }
      }
    }
    if(currentRow>=0){
      tableHTML += s"</tr>"
    }
    tableHTML += s"</table>"

    val allHTMLContent = s"""
                            <!DOCTYPE html>
                            <html lang="en">
                            <head>
                                <meta charset="UTF-8">
                                <title>Monogram</title>
                            </head>
                            <body>
                               $tableHTML
                            </body>
                            </html>
  """.trim

    val pw = new PrintWriter(new File(FileResourceSupplier.getPuzzleHTMLPath(puzzleName)))
    pw.write(allHTMLContent)
    pw.close()

  }

}