package com.github.monogram

import java.io.File

import de.sciss.midi.MetaMessage.EndOfTrack
import de.sciss.midi._

object MIDIParser {

  def parse(midiFile: File): (Sequence, Map[Int, Track]) = {
    val sq = Sequence.readFile(midiFile)

    def hasMusic(track: Track): Boolean = {
      track.events.exists(e => e.message match {
        case NoteOn(_, _, _) => true
        case _ => false
      })
    }

    val tracksWithMusic = sq.tracks.filter(hasMusic)

    implicit val tickRate: TickRate = TickRate(1)

    val channels: Map[Int, Track] = tracksWithMusic.flatMap(_.events)
      .groupBy(event => event.message match {
        case NoteOn(channel, _, _) => channel
        case NoteOff(channel, _, _) => channel
        case _ => -1
      })
      .filter(_._1>=0)
      .mapValues(Track(_))

    (sq, channels)
  }

  def consolidate(elements: List[MusicalElement]): List[MusicalElement] = {

    def consolidationStep(remainingElements: List[MusicalElement], lastElement: MusicalElement) :List[MusicalElement] = remainingElements match {
      case Nil => lastElement::Nil
      case x::xs => if(x.sameNoteAs(lastElement)){
        val updatedLast = lastElement match {
          case Rest(start, duration) => Rest(start, duration + x.duration)
          case Note(notation, start, duration) => Note(notation, start, duration + x.duration)
        }
        consolidationStep(xs, updatedLast)
      }else{
        lastElement::consolidationStep(xs, x)
      }
    }

    consolidationStep(elements.tail, elements.head)
  }

  def convertTrack(track: Track): List[MusicalElement]= {

    def updateWithDuration(element: MusicalElement, stopTime: Long): MusicalElement = element match {
      case Rest(startTime, _) => Rest(startTime, stopTime - startTime)
      case Note(note, startTime, _) => Note(note, startTime, stopTime - startTime)
    }

    def isMatchingPitch(element: MusicalElement, pitch: Int): Boolean = element match {
      case Rest(_,_) => false
      case Note(note, _, _) => NoteNotation.fromPitch(pitch) == note
    }

    def convertNotes(remainingNotes: List[Event], lastNote: MusicalElement): List[MusicalElement] = remainingNotes match {
      case Nil => List()
      case x::xs => x match {
        case Event(time, NoteOn(_, pitch, _)) =>
          if(lastNote==null){
            //if first note we should wait for second to calculate duration
            convertNotes(xs, Note(NoteNotation.fromPitch(pitch), time, 0))
          }else{
            //if new note is played we should stop the previous note/rest and update its duration
            updateWithDuration(lastNote, time)::convertNotes(xs, Note(NoteNotation.fromPitch(pitch), time, 0))
          }
        case Event(time, NoteOff(_, pitch, _)) =>
          if(lastNote!=null && isMatchingPitch(lastNote, pitch)){
            //note off means we should stop the last note and update duration, the next element is rest until the next notes comes
            updateWithDuration(lastNote, time)::convertNotes(xs, Rest(time, 0))
          }else{
            // note on after note on will act as note off for the first one, which means we can disregard its note-off
            convertNotes(xs, lastNote)
          }

        case Event(time, EndOfTrack) => updateWithDuration(lastNote, time)::convertNotes(xs, Rest(time, 0))
        case _ => convertNotes(xs, lastNote)
      }
    }

    val convertedRes = consolidate(convertNotes(track.events.toList, null))

    val totalDuration = convertedRes.last.startTime + convertedRes.last.duration - convertedRes.head.startTime

    val REST_INFLATION_FACTOR = 5
    convertedRes.filter(_ match {
      case Rest(_, duration) => duration > 0 //.001 * totalDuration
      case Note(_, _, duration) => duration > 0 //0.0001 * totalDuration
    }).map {
      case Rest(start, duration) => Rest(start, duration * REST_INFLATION_FACTOR)
      case x: Note => x
    }
  }

//  def main(args: Array[String]): Unit = {
//    val (originalSequence, channels) = parse("midis/chopin_nocturne_9_2.mid")
//
//    val pl = Sequencer.open()
//
////    channels.values
////      .map(x => Sequence(scala.collection.immutable.IndexedSeq(x)))
////      .foreach(pl.play)
//
//    pl.play(originalSequence)
//  }
}