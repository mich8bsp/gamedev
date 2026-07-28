package com.github.monogram

import com.github.monogram.NoteNotation.NoteNotation


object NoteNotation extends Enumeration {
  type NoteNotation = Value
  val C, C_SH, D, D_SH, E, F, F_SH, G, G_SH, A, A_SH, B = Value

  def fromPitch(pitch: Int): NoteNotation = {
    // 21 is the pitch of C0
    NoteNotation.values.toList((pitch - 21) % NoteNotation.values.size)
  }
}

sealed trait MusicalElement{
  def sameNoteAs(other: MusicalElement): Boolean

  val startTime: Long
  val duration: Long
}

case class Rest(startTime: Long, duration: Long) extends MusicalElement{
  override def sameNoteAs(other: MusicalElement): Boolean = other match {
    case _:Rest => true
    case _:Note => false
  }

}

case class Note(noteNotation: NoteNotation, startTime: Long, duration: Long) extends MusicalElement{
  override def sameNoteAs(other: MusicalElement): Boolean = other match {
    case _:Rest => false
    case Note(otherNoteNotation, _, _) => otherNoteNotation == noteNotation
  }
}