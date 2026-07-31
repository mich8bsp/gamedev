use macroquad::{
    camera::{Camera2D, set_camera, set_default_camera},
    color::{BLACK, WHITE},
    input::{is_key_down, is_key_pressed},
    math::{Rect, Vec2, vec2},
    shapes::{draw_circle, draw_rectangle},
    text::{Font, draw_text, load_ttf_font, measure_text},
    texture::{DrawTextureParams, draw_texture_ex, render_target},
    time::{get_fps, get_frame_time},
    window::{clear_background, next_frame, screen_height, screen_width},
};

const VIRTUAL_WIDTH: f32 = 800.0;
const VIRTUAL_HEIGHT: f32 = 600.0;
const PADDLE_WIDTH: f32 = 10.0;
const PADDLE_HEIGHT: f32 = 50.0;
const PLAYER_PADDLE_SPEED: f32 = 500.0;
const OPPONENT_PADDLE_SPEED: f32 = 300.0;
const BALL_RADIUS: f32 = 10.0;
const BALL_INIT_VEL: Vec2 = vec2(-400.0, 100.0);
const SCORE_FONT_SIZE: u16 = 50;
const MAX_BOUNCE_ANGLE: f32 = 60.0;

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
    is_quit: bool,
}

fn get_user_input() -> KeyInput {
    let is_down =
        is_key_down(macroquad::input::KeyCode::S) || is_key_down(macroquad::input::KeyCode::Down);
    let is_up =
        is_key_down(macroquad::input::KeyCode::W) || is_key_down(macroquad::input::KeyCode::Up);
    let is_quit = is_key_pressed(macroquad::input::KeyCode::Escape);
    return KeyInput {
        is_up: is_up,
        is_down: is_down,
        is_quit: is_quit,
    };
}

fn update(state: GameState, input: KeyInput, t: f32) -> GameState {
    //handle user input
    let player_velocity = if input.is_down == input.is_up {
        0.0
    } else if input.is_down {
        PLAYER_PADDLE_SPEED
    } else {
        -PLAYER_PADDLE_SPEED
    };

    //opponent AI
    let deadzone = 5.0;
    let opponent_velocity = if state.ball.vel.x > 0.0
        && state.ball.pos.y > state.opponent_paddle.pos.y + deadzone
    {
        OPPONENT_PADDLE_SPEED
    } else if state.ball.vel.x > 0.0 && state.ball.pos.y < state.opponent_paddle.pos.y - deadzone {
        -OPPONENT_PADDLE_SPEED
    } else {
        0.0
    };

    // simulate physics
    let player_pos = Vec2 {
        x: state.player_paddle.pos.x,
        y: (state.player_paddle.pos.y + player_velocity * t)
            .clamp(PADDLE_HEIGHT / 2.0, VIRTUAL_HEIGHT - PADDLE_HEIGHT / 2.0),
    };
    let opponent_pos = Vec2 {
        x: state.opponent_paddle.pos.x,
        y: (state.opponent_paddle.pos.y + opponent_velocity * t)
            .clamp(PADDLE_HEIGHT / 2.0, VIRTUAL_HEIGHT - PADDLE_HEIGHT / 2.0),
    };
    let mut ball_pos = Vec2 {
        x: state.ball.pos.x + state.ball.vel.x * t,
        y: state.ball.pos.y + state.ball.vel.y * t,
    };

    let mut ball_vel = state.ball.vel;
    //collision detection
    if ball_pos.y - BALL_RADIUS <= 0.0 || ball_pos.y + BALL_RADIUS >= VIRTUAL_HEIGHT {
        ball_vel = vec2(ball_vel.x, -ball_vel.y);
    }

    let player_paddle_rect = Rect::new(
        player_pos.x - PADDLE_WIDTH / 2.0,
        player_pos.y - PADDLE_HEIGHT / 2.0,
        PADDLE_WIDTH,
        PADDLE_HEIGHT,
    );
    let opponent_paddle_rect = Rect::new(
        opponent_pos.x - PADDLE_WIDTH / 2.0,
        opponent_pos.y - PADDLE_HEIGHT / 2.0,
        PADDLE_WIDTH,
        PADDLE_HEIGHT,
    );
    let ball_rect = Rect::new(
        ball_pos.x - BALL_RADIUS,
        ball_pos.y - BALL_RADIUS,
        BALL_RADIUS * 2.0,
        BALL_RADIUS * 2.0,
    );

    let mut player_score = state.player_score;
    let mut opponent_score = state.opponent_score;

    if player_paddle_rect.overlaps(&ball_rect) || opponent_paddle_rect.overlaps(&ball_rect) {
        let offset = if player_paddle_rect.overlaps(&ball_rect) {
            (ball_pos.y - player_pos.y) / (PADDLE_HEIGHT / 2.0)
        } else {
            (ball_pos.y - opponent_pos.y) / (PADDLE_HEIGHT / 2.0)
        };
        let bounce_angle = (offset * MAX_BOUNCE_ANGLE).to_radians();
        let ball_speed = ball_vel.length();
        ball_vel = vec2(
            ball_speed * bounce_angle.cos() * -ball_vel.x.signum(),
            ball_speed * bounce_angle.sin(),
        );
    } else if ball_pos.x <= BALL_RADIUS || ball_pos.x >= VIRTUAL_WIDTH - BALL_RADIUS {
        if ball_pos.x <= BALL_RADIUS {
            opponent_score += 1;
        } else {
            player_score += 1;
        }
        ball_pos = vec2(VIRTUAL_WIDTH / 2.0, VIRTUAL_HEIGHT / 2.0);
        ball_vel = BALL_INIT_VEL;
    }

    return GameState {
        player_paddle: Paddle {
            pos: player_pos,
            vel: player_velocity,
        },
        opponent_paddle: Paddle {
            pos: opponent_pos,
            vel: opponent_velocity,
        },
        ball: Ball {
            pos: ball_pos,
            vel: ball_vel,
        },
        player_score: player_score,
        opponent_score: opponent_score,
    };
}

async fn render(state: &GameState, world_camera: &Camera2D, font: &Font) {
    set_camera(world_camera);
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

    let text_dimensions = measure_text(&score_text, Some(&font), SCORE_FONT_SIZE, 1.0);

    draw_text(
        score_text.as_str(),
        VIRTUAL_WIDTH / 2.0 - text_dimensions.width / 2.0,
        20.0 + text_dimensions.height,
        SCORE_FONT_SIZE as f32,
        WHITE,
    );

    let fps = get_fps();
    draw_text(
        &format!("FPS: {}", fps),
        VIRTUAL_WIDTH - 80.0,
        20.0,
        20.0,
        WHITE,
    );

    set_default_camera();
    let scale = (screen_width() / VIRTUAL_WIDTH).min(screen_height() / VIRTUAL_HEIGHT);
    let dest_size = vec2(VIRTUAL_WIDTH * scale, VIRTUAL_HEIGHT * scale);
    let offset = vec2(
        (screen_width() - dest_size.x) / 2.0,
        (screen_height() - dest_size.y) / 2.0,
    );
    draw_texture_ex(
        &world_camera.render_target.as_ref().unwrap().texture,
        offset.x,
        offset.y,
        WHITE,
        DrawTextureParams {
            dest_size: Some(dest_size),
            flip_y: true,
            ..Default::default()
        },
    );
}

#[macroquad::main("Pong")]
async fn main() {
    let render_target = render_target(VIRTUAL_WIDTH as u32, VIRTUAL_HEIGHT as u32);
    let world_camera = Camera2D {
        render_target: Some(render_target.clone()),
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
            vel: BALL_INIT_VEL,
        },
        player_score: 0,
        opponent_score: 0,
    };

    let font = load_ttf_font("assets/NotoSansBrahmi-Regular.ttf")
        .await
        .unwrap();

    loop {
        let t: f32 = get_frame_time();
        let input: KeyInput = get_user_input();
        if input.is_quit {
            break;
        }
        state = update(state, input, t);
        render(&state, &world_camera, &font).await;
        next_frame().await;
    }
}
