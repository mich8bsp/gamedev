package com.github.monogram

case class Song(title: String, artist: String)

object PuzzleRepository {

  val repo = Map(
    1 -> ("chopin_nocturne_9_2", Song("Nocturne No.2 in E-Flat Major, Op.9 No.2", "Chopin"))
  )
}
