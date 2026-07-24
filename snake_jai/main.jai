#import "Basic";
#import "Random";

tcgetattr :: (fd: s32, t: *Termios) -> s32 #foreign;
tcsetattr :: (fd: s32, action: s32, t: *Termios) -> s32 #foreign;
read      :: (fd: s32, buf: *void, count: u64) -> s64 #foreign;
write     :: (fd: s32, buf: *void, count: u64) -> s64 #foreign;
usleep    :: (microseconds: u32) -> s32 #foreign;

Termios :: struct {
    c_iflag:  u32;
    c_oflag:  u32;
    c_cflag:  u32;
    c_lflag:  u32;
    c_line:   u8;
    c_cc:     [19]u8;
    c_ispeed: u32;
    c_ospeed: u32;
}

STDIN_FILENO  :: 0;
STDOUT_FILENO :: 1;
STDERR_FILENO :: 2;
TCSAFLUSH     :: 2;
ECHO          :: 0x8;
ICANON        :: 0x2;
ISIG          :: 0x1;
VMIN          :: 6;
VTIME         :: 5;

GameInput :: enum {
    NONE;
    UP;
    DOWN;
    LEFT;
    RIGHT;
    QUIT;
    RESTART;
}

Direction :: enum {
    UP;
    DOWN;
    LEFT;
    RIGHT;
}

BOARD_W :: 40;
BOARD_H :: 20;
TICK_MS :: 17;

SPEED_FACTOR :: 3;

Snake :: struct {
    body:                                    [..]Vec2;
    direction:                               Direction;
    factored_frames_per_move:                s32;
    current_move_frames_elapsed:             s32;
}

GameState :: struct {
    snake:   Snake;
    food:    Vec2;
    score:   int;
    running: bool;
}

Vec2 :: struct { x, y: s32; }

write_str :: (s: string) {
    write(STDOUT_FILENO, s.data, cast(u64) s.count);
}

debug :: (s: string) {
    write(STDERR_FILENO, s.data, cast(u64) s.count);
}

read_input :: () -> GameInput {
    buf: [1]u8;
    n := read(STDIN_FILENO, buf.data, 1);
    if n > 0 {
        if buf[0] == #char "q" {
            return GameInput.QUIT;
        }
        if buf[0] == #char "r" {
            return GameInput.RESTART;
        }
        if buf[0] == #char "\e" {
            n := read(STDIN_FILENO, buf.data, 1);
            if n > 0 {
                if buf[0] == #char "[" {
                    n := read(STDIN_FILENO, buf.data, 1);
                    if n > 0 {
                        if buf[0] == #char "A" {
                            return GameInput.UP;
                        }
                        if buf[0] == #char "B" {
                            return GameInput.DOWN;
                        }
                        if buf[0] == #char "C" {
                            return GameInput.RIGHT;
                        }
                        if buf[0] == #char "D" {
                            return GameInput.LEFT;
                        }
                    }
                }
            }
        }
    }
    return GameInput.NONE;
}

place_food :: (state: *GameState) {
    while true {
      curr_placement := Vec2.{
          cast(s32)(random_get() % BOARD_W),
          cast(s32)(random_get() % BOARD_H)
      };
      on_snake := false;
      for state.snake.body {
          if it.x == curr_placement.x && it.y == curr_placement.y {
              on_snake = true;
              break;
          }
      }
      if !on_snake { state.food = curr_placement; break; }
    }  
}

init_game :: () -> GameState {
    state: GameState;
    state.score = 0;
    state.running = true;
    state.snake.direction = Direction.RIGHT;
    state.snake.factored_frames_per_move = 10 * SPEED_FACTOR;
    state.snake.current_move_frames_elapsed = 0;
    array_add(*state.snake.body, Vec2.{BOARD_W / 2 - 2, BOARD_H /2});
    array_add(*state.snake.body, Vec2.{BOARD_W / 2 - 1, BOARD_H /2});
    array_add(*state.snake.body, Vec2.{BOARD_W / 2, BOARD_H /2});

    place_food(*state);
    return state;
}

game_input_to_direction :: (input: GameInput) -> Direction {
    if input == .LEFT return .LEFT;
    if input == .RIGHT return .RIGHT;
    if input == .UP return .UP;
    return .DOWN;
}

direction_to_vec2 :: (direction: Direction) -> Vec2 {
    if direction == .LEFT return Vec2.{-1, 0};
    if direction == .RIGHT return Vec2.{1, 0};
    if direction == .DOWN return Vec2.{0, 1};
    return Vec2.{0, -1};
}

opposite_direction :: (direction: Direction) -> Direction {
    if direction == .LEFT return .RIGHT;
    if direction == .RIGHT return .LEFT;
    if direction == .UP return .DOWN;
    return .UP;
}

update :: (game_state: *GameState, input: GameInput) {
    if input == .LEFT || input == .RIGHT || input == .DOWN || input == .UP {
        input_dir := game_input_to_direction(input);
        if input_dir != game_state.snake.direction && opposite_direction(input_dir) != game_state.snake.direction {
            game_state.snake.direction = input_dir;
        }
    }
    game_state.snake.current_move_frames_elapsed += 1;
    if game_state.snake.current_move_frames_elapsed >= (game_state.snake.factored_frames_per_move / SPEED_FACTOR) {
        game_state.snake.current_move_frames_elapsed = 0;
        direction_vec2 := direction_to_vec2(game_state.snake.direction);

        snake_head := game_state.snake.body[game_state.snake.body.count - 1];
        snake_next := Vec2.{ snake_head.x + direction_vec2.x, snake_head.y + direction_vec2.y };

        is_game_over := false;
        if snake_next.x < 0 || snake_next.x > BOARD_W-1 || snake_next.y < 0 || snake_next.y > BOARD_H-1 {
            is_game_over = true;
        }
        for game_state.snake.body {
            if it.x == snake_next.x && it.y == snake_next.y {
                is_game_over = true;
            }
        }
        if is_game_over {
            game_state.running = false;
        } else {
            array_add(*game_state.snake.body, snake_next);
            if snake_next.x == game_state.food.x && snake_next.y == game_state.food.y {
                place_food(game_state);
                game_state.score += 1;
                if game_state.snake.factored_frames_per_move > 1 {
                    game_state.snake.factored_frames_per_move -= 1;
                }
            } else {
                array_ordered_remove_by_index(*game_state.snake.body, 0);
            }
        }
    }
}

render :: (game_state: *GameState, debug_mode: bool) {
    b: String_Builder;
    //move to corner
    append(*b, "\e[H");

    //draw top wall
    append(*b, "+");
    for 0..BOARD_W-1 append(*b, "--");
    append(*b, "+\n");

    //draw mid rows
    head := game_state.snake.body[game_state.snake.body.count - 1];
    for y: 0..BOARD_H-1 {
        append(*b, "|");
        for x: 0..BOARD_W-1 {
            pos := Vec2.{cast(s32) x, cast(s32) y};
            if pos.x == head.x && pos.y == head.y {
                append(*b, "Q ");
            } else if pos.x == game_state.food.x && pos.y == game_state.food.y {
                append(*b, "x ");
            } else {
                on_body := false;
                for game_state.snake.body {
                    if it.x == pos.x && it.y == pos.y {
                        on_body = true;
                        break;
                    }
                }
                append(*b, ifx on_body then "o " else "  ");
            }
        }
        append(*b, "|\n");
    }

    //draw bottom wall
    append(*b, "+");
    for 0..BOARD_W-1 append(*b, "--");
    append(*b, "+\n");

    print_to_builder(*b, "Score: %\n", game_state.score);
    if !game_state.running {
        print_to_builder(*b, "Game Over\n");
    }
    if debug_mode {
        print_to_builder(*b, "x: %, y: %\n", game_state.snake.body[game_state.snake.body.count-1].x, game_state.snake.body[game_state.snake.body.count-1].y);
        print_to_builder(*b, "factored frames per move: % \n", game_state.snake.factored_frames_per_move);
        print_to_builder(*b, "current move frames count: % \n", game_state.snake.current_move_frames_elapsed);
    }
    
    s := builder_to_string(*b);
    write_str(s);
    free(s);
    free_buffers(*b);
}

main :: () {
    args := get_command_line_arguments();
    debug_mode := false;
    for args if it == "--debug" { debug_mode = true; break; }
    random_seed(current_time_monotonic().low);
    original: Termios;
    tcgetattr(STDIN_FILENO, *original);
    defer tcsetattr(STDIN_FILENO, TCSAFLUSH, *original);

    raw := original;
    raw.c_lflag &= ~cast(u32)(ECHO | ICANON | ISIG);
    raw.c_cc[VMIN]  = 0;
    raw.c_cc[VTIME] = 0;
    tcsetattr(STDIN_FILENO, TCSAFLUSH, *raw);

    write_str("\e[?1049h");  // enter alternate screen
    defer write_str("\e[?1049l"); // leave alternate screen
    write_str("\e[?25l");    // hide cursor
    defer write_str("\e[?25h");    // show cursor

    write_str("press q to quit\r\npress r to restart \r\n");

    game_state := init_game();
    while true {
        input_key: GameInput = read_input();
        if input_key == GameInput.QUIT then break;
        if input_key == GameInput.RESTART {
            game_state = init_game();
        } else {
            if game_state.running {
                update(*game_state, input_key);
                render(*game_state, debug_mode);
            }
            usleep(cast(u32)(TICK_MS * 1000));
        } 
    }
}
