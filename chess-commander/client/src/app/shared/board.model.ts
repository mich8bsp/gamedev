export class Board {
  private boardPieces: Map<string, BoardPiece> = new Map<string, BoardPiece>();

  private static boardMapKey(row: number, col: number) {
    return '' + row + ',' + col;
  }

  getPieceAt(row: number, col: number) {
    const a = this.boardPieces.get(Board.boardMapKey(row, col));
    return (a) ? a : new BoardPiece(row, col);
  }

  setPieceAt(row: number, col: number, piece: Piece) {
    this.boardPieces.set(Board.boardMapKey(row, col), new BoardPiece(row, col, piece));
  }

  setEmptyCell(row: number, col: number) {
    this.boardPieces.set(Board.boardMapKey(row, col), new BoardPiece(row, col));
  }

  updateWithMove(moves: PieceMove[]) {
    const boardCpy = new Board();
    boardCpy.boardPieces = new Map(this.boardPieces);
    moves.forEach(move => {
      boardCpy.setPieceAt(move.dest.row, move.dest.col, move.source.occupyingPiece);
      boardCpy.setEmptyCell(move.source.row, move.source.col);
    });
    return boardCpy;
  }

  changeSelection(row: number, col: number, isSelected: boolean) {
    const boardCpy = new Board();
    boardCpy.boardPieces = new Map(this.boardPieces);
    boardCpy.boardPieces.set(Board.boardMapKey(row, col), new BoardPiece(row, col, this.getPieceAt(row, col).occupyingPiece, isSelected));
    return boardCpy;
  }
}

export class BoardPiece {

  constructor(public row: number,
              public col: number,
              public occupyingPiece?: Piece,
              public isSelected: boolean = false) {


  }
}

export class Piece {

  constructor(public color: PieceColor,
              public kind: PieceKind) {
  }

  getPieceName() {
    return 'chess-' + this.kind + '-' + this.color;
  }
}

export class PieceMove {

  constructor(public source: BoardPiece,
              public dest: BoardPiece) {
  }
}


export enum PieceColor {
  WHITE = 'white',
  BLACK = 'black'
}

export enum PieceKind {
  PAWN = 'pawn',
  KNIGHT = 'knight',
  BISHOP = 'bishop',
  ROOK = 'rook',
  QUEEN = 'queen',
  KING = 'king'
}
