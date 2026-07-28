package com.github.monogram.gameserver

import com.github.monogram._
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

import scala.collection.mutable

class GameController extends Controller {

  case class Puzzle(id: Int, name: String, solution: Board)

  val puzzlesMapping: mutable.Map[Int, Puzzle] = mutable.Map()

  case class BoardResponse(board: Array[Array[BoardElement]],
                           rows: Int,
                           cols: Int,
                           rowsMetadata: List[List[SideMetadataElement]],
                           colsMetadata: List[List[SideMetadataElement]])

  get("/:*") { request: Request =>
    println(s"got request $request")
    response.ok.fileOrIndex(
      request.params("*"),
      "/client/index.html")
  }

  get("/getPuzzle") { request: Request =>
    val puzzleId = request.params.getOrElse("puzzleId", "1").toInt
    val puzzleName = PuzzleRepository.repo(puzzleId)._1
    val puzzle = puzzlesMapping.getOrElse(puzzleId, Puzzle(puzzleId, puzzleName, PuzzleReader.readPuzzle(puzzleName)))
    if(!puzzlesMapping.contains(puzzleId)){
      puzzlesMapping(puzzleId) = puzzle
    }
    val metadata = puzzle.solution.buildBoardMetadata()
    BoardResponse(board = puzzle.solution.boardConfig,
      rows = puzzle.solution.boardRows,
      cols = puzzle.solution.boardCols,
      rowsMetadata = metadata._1,
      colsMetadata = metadata._2)
  }

}