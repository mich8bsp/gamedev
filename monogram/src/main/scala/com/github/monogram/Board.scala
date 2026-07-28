package com.github.monogram

import com.github.monogram.Board.Color
import com.github.monogram.NoteNotation._

import scala.math.max

case class SideMetadataElement(color: Color, text: String, count: Int)

case class BoardElement(color: Color, text: String)

class Board(val boardRows: Int, val boardCols: Int) {

  import Board._

  var boardConfig: Array[Array[BoardElement]] = Array.ofDim[BoardElement](boardRows, boardCols)
  val boardSize: Int = boardRows * boardCols

  def buildBoard(elements: List[MusicalElement]): Unit = {
    val totalDuration = elements.map(x=>x.duration).sum

    def counterToRowCol(counter: Int): (Int, Int) = (counter / boardCols, counter - (counter / boardCols) * boardCols)

    def durationToNumOfSquares(duration: Long): Int = {
      println(s"total duration is $totalDuration current duration is $duration")
      max(((duration.toDouble / totalDuration) * boardSize).floor.toInt, 1)
    }

    def fillBoard(remainingToFill: List[MusicalElement], currentBoardCounter: Int): Int = remainingToFill match {
      case x :: xs =>
        val duration = x.duration
        val squaresToFill = durationToNumOfSquares(duration)
        println(s"at counter $currentBoardCounter filling $squaresToFill squares")
        val color = musicalElementToColor(x)
        val text = musicalElementToText(x)

        if (squaresToFill > 0) {
          Range(currentBoardCounter, currentBoardCounter + squaresToFill).foreach(i => {
            val (r, c) = counterToRowCol(i)
            boardConfig(r)(c) = BoardElement(color, text)
          })
        }
        fillBoard(xs, currentBoardCounter + squaresToFill)
      case Nil => currentBoardCounter
    }

    fillBoard(elements, 0)
    for (i <- 0 until boardRows) {
      for (j <- 0 until boardCols) {
        if (boardConfig(i)(j) == null) {
          boardConfig(i)(j) = BoardElement(musicalElementToColor(Rest(0, 0)), musicalElementToText(Rest(0, 0)))
        }
      }
    }
  }

  def clearBoard(): Unit = boardConfig = Array.ofDim[BoardElement](boardRows, boardCols)

  def getColor(r: Int, c: Int): Color = boardConfig(r)(c).color

  def getText(r: Int, c: Int): String = boardConfig(r)(c).text

  def fillSquare(i: Int, j: Int, color: String, text: String = " "): Unit = {
    boardConfig(i)(j) = BoardElement(color, text)
  }


  def buildBoardMetadata(): (List[List[SideMetadataElement]], List[List[SideMetadataElement]]) = {

    def consolidateRow(elements: List[BoardElement], currentMeta: SideMetadataElement): List[SideMetadataElement] = elements match {
      case BoardElement(color, text) :: xs => if (currentMeta.color == color) {
        consolidateRow(xs, SideMetadataElement(color, text, currentMeta.count + 1))
      } else {
        currentMeta :: consolidateRow(xs, SideMetadataElement(color, text, 1))
      }
      case Nil => currentMeta::Nil
    }

    val rowsMetadata: List[List[SideMetadataElement]] = Range(0, boardRows).map(i => {
      val currRow: List[BoardElement] = boardConfig(i).toList
      consolidateRow(currRow.tail, SideMetadataElement(currRow.head.color, currRow.head.text, 1))
        .filter(x => x.color != musicalElementToColor(Rest(0,0)))
    }).toList

    val colsMetadata: List[List[SideMetadataElement]] = Range(0, boardCols).map(i => {
      val currCol: List[BoardElement] = boardConfig.map(row => row(i)).toList
      consolidateRow(currCol.tail, SideMetadataElement(currCol.head.color, currCol.head.text, 1))
        .filter(x => x.color != musicalElementToColor(Rest(0,0)))
    }).toList

    (rowsMetadata, colsMetadata)
  }
}

object Board {
  type Color = String

  def musicalElementToText(element: MusicalElement): String = element match {
    case x: Note => x.noteNotation.toString.replaceFirst("_SH", "#")
    case _: Rest => " "
  }

  def musicalElementToColor(element: MusicalElement): Color = element match {
    case x: Note => x.noteNotation match {
      case C => "#990027"
      case C_SH => "#d1a835"
      case D => "#ff7f19"
      case D_SH => "#f9d5d5"
      case E => "#ffff66"
      case F => "#12b629"
      case F_SH => "#c1e1de"
      case G => "#07b3de"
      case G_SH => "#c1e1de"
      case A => "#2441e2"
      case A_SH => "#602147"
      case B => "#57389f"
    }
    case _: Rest => "#ffffff"
  }

}

