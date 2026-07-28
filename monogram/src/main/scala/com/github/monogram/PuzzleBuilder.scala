package com.github.monogram

import java.io.{File, PrintWriter}

import scala.math.sqrt

object PuzzleBuilder {

  def calculateBoardSize(elements: List[MusicalElement]): (Int, Int) ={
    val minDuration = elements.minBy(x => x.duration).duration
    assert(minDuration>0)

    val totalDuration = elements.map(x => x.duration).sum

    println(s"Total duration is $totalDuration and min duration is $minDuration")

    val squaresCount: Double = totalDuration.toDouble / minDuration


    val sideSize = sqrt(squaresCount).ceil.toInt
    println(s"making board of $sideSize x $sideSize with $squaresCount squares")
    (sideSize, sideSize)
  }

  def persistToFile(board: Board, puzzleName: String): Unit = {
    val pw = new PrintWriter(FileResourceSupplier.getPuzzlePath(puzzleName))
    pw.write(s"${board.boardRows},${board.boardCols}\n")
    for(i <- 0 until board.boardRows){
      for(j <- 0 until board.boardCols){
        pw.write(s"$i,$j,${board.getColor(i,j)},${board.getText(i,j)}\n")
      }
    }
    pw.close()
  }

    def main(args: Array[String]): Unit = {
      val puzzleName = PuzzleRepository.repo(1)._1
      val puzzleMidi = new File(FileResourceSupplier.getMidiPath(puzzleName))

      val (_, tracks) = MIDIParser.parse(puzzleMidi)
      val notesList: List[MusicalElement] = MIDIParser.convertTrack(tracks(0))

      val (boardRows, boardCols) = calculateBoardSize(notesList)

      println(s"building board of size $boardRows X $boardCols")

      val board = new Board(boardRows = boardRows ,boardCols = boardCols)
      board.buildBoard(notesList)

      for(i <- 0 until boardRows){
        for(j <- 0 until boardCols){
          println(s"($i,$j) = ${board.getColor(i, j)}")
        }
      }

      persistToFile(board, puzzleName)
      BoardVisualizer.visualize(puzzleName)

    }

}
