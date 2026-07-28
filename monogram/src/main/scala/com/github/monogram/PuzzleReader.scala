package com.github.monogram

import scala.io.Source

object PuzzleReader {

  def readPuzzle(puzzleId: String): Board ={
    var board = new Board(0,0)
    var first = true
    for( line <- Source.fromFile(FileResourceSupplier.getPuzzlePath(puzzleId)).getLines()){
      if(first){
        val boardSizeSplit = line.split(",")
        board = new Board(boardSizeSplit(0).toInt, boardSizeSplit(1).toInt)
        first = false
      }else{
        val parsedLine = line.split(",")
        val row = parsedLine(0).toInt
        val col = parsedLine(1).toInt
        board.fillSquare(row, col, parsedLine(2), parsedLine(3))
      }
    }
    board
  }
}
