import {Component, Input, OnInit} from '@angular/core';
import {Board} from '../shared/board.model';

@Component({
  selector: 'app-board-cell',
  templateUrl: './board-cell.component.html',
  styleUrls: ['./board-cell.component.scss']
})
export class BoardCellComponent implements OnInit {

  @Input() board: Board;
  @Input() row: number;
  @Input() col: number;

  constructor() {
  }

  ngOnInit() {
  }

  getPiece() {
    if (this.board && this.board.getPieceAt(this.row, this.col)) {
      return this.board.getPieceAt(this.row, this.col).occupyingPiece;
    }
  }

  isSelected() {
    return this.board && this.board.getPieceAt(this.row, this.col) && this.board.getPieceAt(this.row, this.col).isSelected;
  }
}
