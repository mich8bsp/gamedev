import {GameStore} from './game.store';
import {map, tap} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {Board, BoardPiece, PieceMove} from '../shared/board.model';
import {GameQuery} from './game.query';
import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class GameService {

  constructor(private store: GameStore,
              private query: GameQuery,
              private http: HttpClient) {
  }

  makeMove(move) {
    return this.http.post<MakeMoveResponse>('http://localhost:9921/make-move/', move)
      .subscribe((makeMoveRes: MakeMoveResponse) => {
        console.log('got make move result ', makeMoveRes);
        if (makeMoveRes.isSuccess) {
          this.store.update(state => {
            return {
              board: state.board.updateWithMove(makeMoveRes.move),
              lastSelected: undefined
            };
          });
        }
      });
  }

  selectCell(row: number, col: number, board: Board, lastSelected?: BoardPiece) {
    console.log('select cell called');
    console.log('last selected is', lastSelected);
    console.log('board is ', board);
    const currSelected = board.getPieceAt(row, col);
    console.log('current selected', currSelected);
    if (!lastSelected) {
      if (currSelected.occupyingPiece) {
        console.log('selecting piece at', currSelected);
        const boardAfterSelection = board.changeSelection(row, col, true);
        this.store.update({
            lastSelected: board.getPieceAt(row, col),
            board: boardAfterSelection
          }
        );
      }
    } else if (currSelected.row == lastSelected.row && currSelected.col == lastSelected.col) {
      console.log('deselecting piece at', currSelected);
      this.store.update({
        lastSelected: undefined,
        board: board.changeSelection(row, col, false)
      });
    } else {
      console.log('making move from ', lastSelected, currSelected);
      this.makeMove(new PieceMove(
        lastSelected,
        currSelected
      ));
    }
  }
}

class MakeMoveResponse {
  isSuccess: boolean;
  move: PieceMove[];
}
