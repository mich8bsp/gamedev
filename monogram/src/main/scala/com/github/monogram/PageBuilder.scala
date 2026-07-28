//package com.github.monogram
//
//import org.scalajs.dom.document
//
//import scala.io.Source
//
//object PageBuilder {
//  def buildPage(board: Board): Unit = {
//    val boardTable = document.createElement("table")
//    for(i <- 0 until board.boardRows){
//      val row = document.createElement("tr")
//      for (j<- 0 until board.boardCols){
//        val col = document.createElement("td")
//        col.setAttribute("bgcolor", board.getColor(i, j))
//        row.appendChild(col)
//      }
//      boardTable.appendChild(row)
//    }
//
//    document.body.appendChild(boardTable)
//  }
//
//  def main(args: Array[String]): Unit = {
//    val puzzlePath = "puzzles/chopin_nocturne_9_2.txt"
//    var board = new Board(0,0)
//    var first = true
//    for( line <- Source.fromFile(puzzlePath).getLines()){
//      if(first){
//        val boardSizeSplit = line.split(",")
//        board = new Board(boardSizeSplit(0).toInt, boardSizeSplit(1).toInt)
//        first = false
//      }else{
//        val parsedLine = line.split(",")
//        board.fillSquare(parsedLine(0).toInt, parsedLine(1).toInt, parsedLine(2))
//      }
//    }
//    println("building a board")
//    buildPage(board)
//  }
//}
