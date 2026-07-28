package com.github.monogram

object FileResourceSupplier {
  val midiDir = "fileResources/midis"
  val puzzlesDir = "fileResources/puzzles"

  def getMidiPath(puzzleName: String): String ={
    s"$midiDir/$puzzleName.mid"
  }

  def getPuzzlePath(puzzleName: String): String = {
    s"$puzzlesDir/$puzzleName.txt"
  }

  def getPuzzleHTMLPath(puzzleName: String): String = {
    s"$puzzlesDir/$puzzleName.html"
  }
}
