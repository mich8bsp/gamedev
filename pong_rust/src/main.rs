use macroquad::{
    camera::{Camera2D, set_camera},
    color::{BLACK, WHITE},
    input::is_key_down,
    math::{Vec2, vec2},
    shapes::{draw_circle, draw_rectangle},
    text::draw_text,
    time::get_frame_time,
    window::{clear_background, next_frame},
};

const VIRTUAL_WIDTH: f32 = 800.0;
const VIRTUAL_HEIGHT: f32 = 600.0;
const PADDLE_WIDTH: f32 = 10.0;
const PADDLE_HEIGHT: f32 = 50.0;
const BALL_RADIUS: f32 = 10.0;

struct Paddle {
    pos: Vec2,
    vel: f32,
}

struct Ball {
    pos: Vec2,
    vel: Vec2,
}

struct GameState {
    player_paddle: Paddle,
    opponent_paddle: Paddle,
    ball: Ball,
    player_score: i32,
    opponent_score: i32,
}

struct KeyInput {
    is_up: bool,
    is_down: bool,
}

fn get_user_input() -> KeyInput {
    let is_down =
        is_key_down(macroquad::input::KeyCode::S) || is_key_down(macroquad::input::KeyCode::Down);
    let is_up =
        is_key_down(macroquad::input::KeyCode::W) || is_key_down(macroquad::input::KeyCode::Up);
    return KeyInput {
        is_up: is_up,
        is_down: is_down,
    };
}

fn update(state: GameState, input: KeyInput, t: f32) -> GameState {
    //handle user input
    let player_velocity = if input.is_down == input.is_up {
        state.player_paddle.vel
    } else {
        let delta_dy = if input.is_down { -10.0 } else { 10.0 };
        state.player_paddle.vel + delta_dy
    };

    // simulate physics
    let player_pos = Vec2 {
        x: state.player_paddle.pos.x,
        y: state.player_paddle.pos.y + player_velocity * t,
    };
    let opponent_pos = Vec2 {
        x: state.opponent_paddle.pos.x,
        y: state.opponent_paddle.pos.y + state.opponent_paddle.vel * t,
    };
    let ball_pos = Vec2 {
        x: state.ball.pos.x + state.ball.vel.x * t,
        y: state.ball.pos.y + state.ball.vel.y * t,
    };

    //todo: logic

    return GameState {
        player_paddle: Paddle {
            pos: player_pos,
            vel: player_velocity,
        },
        opponent_paddle: Paddle {
            pos: opponent_pos,
            vel: state.opponent_paddle.vel,
        },
        ball: Ball {
            pos: ball_pos,
            vel: state.ball.vel,
        },
        player_score: state.player_score,
        opponent_score: state.opponent_score,
    };
}

fn render(state: &GameState) {
    clear_background(BLACK);
    let player_paddle_pos = state.player_paddle.pos;
    draw_rectangle(
        player_paddle_pos.x - PADDLE_WIDTH / 2.0,
        player_paddle_pos.y - PADDLE_HEIGHT / 2.0,
        PADDLE_WIDTH,
        PADDLE_HEIGHT,
        WHITE,
    );
    let opponent_paddle_pos = state.opponent_paddle.pos;

    draw_rectangle(
        opponent_paddle_pos.x - PADDLE_WIDTH / 2.0,
        opponent_paddle_pos.y - PADDLE_HEIGHT / 2.0,
        PADDLE_WIDTH,
        PADDLE_HEIGHT,
        WHITE,
    );

    draw_circle(state.ball.pos.x, state.ball.pos.y, BALL_RADIUS, WHITE);
    let score_text = format!("{}:{}", state.player_score, state.opponent_score);
    draw_text(
        score_text.as_str(),
        VIRTUAL_WIDTH / 2.0 - 20.0,
        VIRTUAL_HEIGHT - 20.0,
        20.0,
        WHITE,
    );
}

#[macroquad::main("Pong")]
async fn main() {
    let camera = Camera2D {
        target: vec2(VIRTUAL_WIDTH / 2.0, VIRTUAL_HEIGHT / 2.0),
        zoom: vec2(2.0 / VIRTUAL_WIDTH, -2.0 / VIRTUAL_HEIGHT),
        ..Default::default()
    };

    let mut state: GameState = GameState {
        player_paddle: Paddle {
            pos: vec2(PADDLE_WIDTH / 2.0, VIRTUAL_HEIGHT / 2.0),
            vel: 0.0,
        },
        opponent_paddle: Paddle {
            pos: vec2(VIRTUAL_WIDTH - PADDLE_WIDTH / 2.0, VIRTUAL_HEIGHT / 2.0),
            vel: 0.0,
        },
        ball: Ball {
            pos: vec2(VIRTUAL_WIDTH / 2.0, VIRTUAL_HEIGHT / 2.0),
            vel: vec2(-80.0, 40.0),
        },
        player_score: 0,
        opponent_score: 0,
    };

    set_camera(&camera);
    loop {
        let t: f32 = get_frame_time();
        let input: KeyInput = get_user_input();
        state = update(state, input, t);
        render(&state);
        next_frame().await;
    }
}
