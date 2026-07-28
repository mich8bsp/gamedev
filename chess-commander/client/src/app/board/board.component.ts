import {Component, OnInit} from '@angular/core';
import {Board, BoardPiece, PieceColor} from '../shared/board.model';
import {GameStore} from '../store/game.store';
import {GameQuery} from '../store/game.query';
import {GameService} from '../store/game.service';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {

  private playerColor: PieceColor = PieceColor.WHITE;
  private opponentColor: PieceColor;

  public board: Board;
  public lastSelected?: BoardPiece;

  rowsRange: Array<number> = Array.from(Array(8).keys());
  colsRange: Array<number> = Array.from(Array(8).keys());

  constructor(private gameQuery: GameQuery,
              private gameStore: GameStore,
              private gameService: GameService) {
  }

  ngOnInit() {
    this.opponentColor = (this.playerColor === PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

    this.gameQuery.lastSelected$.subscribe(v => {
      console.log('updated last selected to', v)
      this.lastSelected = v;
    });

    this.gameQuery.board$.subscribe(v => {
      console.log('updated board to', v);
      this.board = v;
    });
  }

  selectCell(row: number, col: number){
    console.log("clicked on " + row  + ", " + col);

    this.gameService.selectCell(row, col, this.board, this.lastSelected);
  }
}
