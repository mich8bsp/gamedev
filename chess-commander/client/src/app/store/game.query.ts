import { Query } from '@datorama/akita';
import {GameState, GameStore} from './game.store';
import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class GameQuery extends Query<GameState> {
  board$ = this.select(state => state.board);
  lastSelected$ = this.select(state => state.lastSelected);

  constructor(protected store: GameStore) {
    super(store);
  }
}
