import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.prefs.Preferences;
import java.util.ArrayList;

public class BrickBreakerGame extends JPanel {
    private static final int WINDOW_WIDTH = 700; // 게임 창의 너비
    private static final int WINDOW_HEIGHT = 600; // 게임 창의 높이
    private static final int PADDLE_WIDTH = 100; // 패들의 너비
    private static final int PADDLE_HEIGHT = 30; // 패들의 높이
    private static final int PADDLE_SPEED = 100; // 패들의 이동 속도
    private static final int BRICK_WIDTH = 60; // 벽돌의 너비
    private static final int BRICK_HEIGHT = 40; // 벽돌의 높이
    private static final int BRICK_ROWS = 4; // 벽돌의 행 수
    private static final int BRICK_COLS = 8; // 벽돌의 열 수
    private static final int BALL_SIZE = 30; // 공의 크기
    private static final int BALL_SPEED = 10; // 공의 이동 속도
    private static final String HIGHEST_SCORE_KEY = "highestScore";
    private boolean gameRunning; // 게임 실행 상태
    private boolean gameOver; // 게임 종료 여부
    private int gap = 15; // 벽돌 사이의 간격
    private int life = 3; // 목숨
    private int highestScore = 0; // 최고 점수
    private int currentScore = 0; // 현재 점수
    private int paddleX, paddleY; // 패들의 x, y 좌표
    private int ballX, ballY, ballSpeedX, ballSpeedY; // 공의 x 좌표, y 좌표, x 속도, y 속도
    private ArrayList<Brick> bricks; // 벽돌들을 저장하는 리스트
    private Image brickImage; // 벽돌 이미지
    private Image ballImage; // 공 이미지
    private Image paddleImage;// 패들 이미지
    private Image heartImage; // 하트 이미지

    private BrickBreakerGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        // 이미지 리소스를 로드
        brickImage = new ImageIcon("images/brick.png").getImage();
        ballImage = new ImageIcon("images/ball.png").getImage();
        paddleImage = new ImageIcon("images/paddle.png").getImage();
        heartImage = new ImageIcon("images/heart.png").getImage();

        setFocusable(true);

        // 키 이벤트를 처리하기 위한 키 리스너 추가
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    movePaddle(-PADDLE_SPEED); // 왼쪽 방향키를 누르면 패들을 왼쪽으로 이동
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    movePaddle(PADDLE_SPEED); // 오른쪽 방향키를 누르면 패들을 오른쪽으로 이동
                } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
                    currentScore = 0;
                    life = 3;
                    gameOver = false; // 게임 오버 상태 초기화
                    bricks = new ArrayList<>();
                    for (int i = 0; i < BRICK_ROWS; i++) {
                        for (int j = 0; j < BRICK_COLS; j++) {
                            int brickX = j * (BRICK_WIDTH + gap) + 50; // 벽돌의 x 좌표 계산
                            int brickY = i * (BRICK_HEIGHT + gap) + 50; // 벽돌의 y 좌표 계산
                            bricks.add(new Brick(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT)); // 새로운 벽돌을 리스트에 추가
                        }
                    }
                    initializeGame(); // R 키를 누르고 게임 종료 상태인 경우 게임을 다시 시작
                }
            }
        });
        initializeGame();
    }

    private void initializeGame() {
        ballX = WINDOW_WIDTH / 2 - BALL_SIZE / 2; // 초기 공의 x 좌표를 게임 창 가운데로 설정
        ballY = WINDOW_HEIGHT / 2 + BALL_SIZE / 2 + 150; // 초기 공의 y 좌표를 게임 창 가운데 보다 더 아래쪽으로 설정
        paddleX = WINDOW_WIDTH / 2 - PADDLE_WIDTH / 2; // 초기 패들의 x 좌표를 게임 창 가운데로 설정
        paddleY = WINDOW_HEIGHT - PADDLE_HEIGHT - 10; // 초기 패들의 y 좌표를 바닥으로부터 10픽셀 위로 설정
        ballSpeedX = BALL_SPEED; // 공의 초기 x 속도 설정
        ballSpeedY = -BALL_SPEED; // 공의 초기 y 속도 설정
        gameOver = false; // 게임 오버 상태 초기화
        gameRunning = true; // 게임 실행 상태 초기화

        if (bricks == null || bricks.isEmpty()) {
            bricks = new ArrayList<>();
            // 초기 벽돌 생성
            for (int i = 0; i < BRICK_ROWS; i++) {
                for (int j = 0; j < BRICK_COLS; j++) {
                    int brickX = j * (BRICK_WIDTH + gap) + 50; // 벽돌의 x 좌표 계산
                    int brickY = i * (BRICK_HEIGHT + gap) + 50; // 벽돌의 y 좌표 계산
                    bricks.add(new Brick(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT)); // 새로운 벽돌을 리스트에 추가
                }
            }
        }
        Preferences prefs = Preferences.userNodeForPackage(BrickBreakerGame.class);
        highestScore = prefs.getInt(HIGHEST_SCORE_KEY, 0); // 최고점수 불러오기
    }

    private void increaseCurrentScore() {
        currentScore += 10; // 점수 증가
    }

    private void updateHighestScore() {
        if (currentScore > highestScore) {
            highestScore = currentScore; // 최고점수 업데이트
        }
        // 최고점수를 Preferences에 저장
        Preferences prefs = Preferences.userNodeForPackage(BrickBreakerGame.class);
        prefs.putInt(HIGHEST_SCORE_KEY, highestScore);
    }

    private void movePaddle(int speed) {
        // 게임이 끝난 상태에서 패들을 움직이지 않도록 설정
        if (!gameOver) {
            paddleX += speed;

            // 패들이 게임 창을 벗어나지 않도록 좌표 제한
            if (paddleX < 0)
                paddleX = 0;
            if (paddleX > WINDOW_WIDTH - PADDLE_WIDTH)
                paddleX = WINDOW_WIDTH - PADDLE_WIDTH;
        }
    }

    private void update() {
        if (!gameRunning)
            return;

        // 공의 위치 업데이트
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // 공의 x 좌표가 게임 창의 경계에 닿으면 방향 전환
        if (ballX < 0 || ballX > WINDOW_WIDTH - BALL_SIZE) {
            ballSpeedX *= -1;
        }

        // 공의 y 좌표가 게임 창의 상단에 닿으면 방향 전환
        if (ballY < 0) {
            ballSpeedY *= -1;
        }

        // 공이 바닥에 닿으면 목숨 1감소 후 초기화
        if (ballY > WINDOW_HEIGHT - BALL_SIZE) {
            gameRunning = false;
            life--;

            // 목숨이 0이면 게임 종료
            if (life == 0) {
                gameOver = true;
                return;
            }
            initializeGame();

            // 1초 대기
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 공이 패들과 충돌하면 방향 전환
        if (ballY + BALL_SIZE >= WINDOW_HEIGHT - PADDLE_HEIGHT && ballX + BALL_SIZE >= paddleX
                && ballX <= paddleX + PADDLE_WIDTH) {
            ballSpeedY *= -1;
        }

        // 벽돌과의 충돌 검사
        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            if (brick.isVisible() && ballX + BALL_SIZE >= brick.getX() && ballX <= brick.getX() + brick.getWidth()
                    && ballY + BALL_SIZE >= brick.getY() && ballY <= brick.getY() + brick.getHeight()) {
                brick.setVisible(false); // 충돌한 벽돌을 비활성화
                ballSpeedY *= -1; // 공의 y 방향 전환
                increaseCurrentScore(); // 현재 점수 증가
                updateHighestScore(); // 최고 점수 업데이트
                break;
            }
        }

        // 모든 벽돌이 파괴되었는지 확인
        boolean allBricksDestroyed = true;
        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                allBricksDestroyed = false;
                break;
            }
        }

        // 모든 벽돌이 파괴되었으면 게임 종료 후 초기화
        if (allBricksDestroyed) {
            gameRunning = false;
            bricks = null;
            initializeGame();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 백그라운드 컬러 설정 후 컬러 색으로 채우기
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 현재 점수, 최고 점수 그리기
        Font scoreFont = new Font("Arial", Font.BOLD, 20);
        g.setFont(scoreFont);
        g.setColor(Color.BLACK);
        g.drawString("Current Score: " + currentScore, 10, 40);
        g.drawString("Highest Score: " + highestScore, 10, 20);

        // 남은 목숨을 나타내기 위한 하트 그리기
        int heartWidth = 30;
        int heartHeight = 30;
        int heartMargin = 10;
        for (int i = 0; i < life; i++) {
            int heartX = WINDOW_WIDTH - (i + 1) * (heartWidth + heartMargin);
            int heartY = 10;
            g.drawImage(heartImage, heartX, heartY, heartWidth, heartHeight, this);
        }

        if (gameOver) {
            // 게임 오버 그리기
            Font gameOverFont = new Font("Arial", Font.BOLD, 40);
            g.setFont(gameOverFont);
            g.setColor(Color.RED);
            g.drawString("GAME OVER", WINDOW_WIDTH / 2 - 120, WINDOW_HEIGHT / 2);

            // 재시작 그리기
            Font restartFont = new Font("Arial", Font.BOLD, 20);
            g.setFont(restartFont);
            g.setColor(Color.RED);
            g.drawString("Press R to restart", WINDOW_WIDTH / 2 - 90, WINDOW_HEIGHT / 2 + 40);
        }

        g.drawImage(paddleImage, paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT, null); // 패들 크기에 맞게 이미지 삽입
        g.drawImage(ballImage, ballX, ballY, BALL_SIZE, BALL_SIZE, null); // 공 크기에 맞게 이미지 삽입

        // 벽돌 개수와 크기에 맞게 이미지 삽입
        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                g.drawImage(brickImage, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight(), null);
            }
        }
    }

    private void runGame() {
        JFrame frame = new JFrame("Brick Breaker"); // JFrame 객체 생성 및 제목 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프레임을 닫을 때 프로그램이 종료되도록 설정
        frame.setResizable(false); // 프레임의 크기 조정을 비활성화
        frame.add(this); // 현재 객체(게임 패널)를 프레임에 추가
        frame.pack(); // 프레임을 구성 요소의 최적 크기로 자동 조정
        frame.setLocationRelativeTo(null); // 프레임을 화면 중앙에 배치
        frame.setVisible(true); // 프레임을 화면에 표시

        // 게임 루프
        while (true) {
            update(); // 게임 상태 업데이트
            repaint(); // 그래픽 업데이트
            try {
                Thread.sleep(10); // 10ms 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BrickBreakerGame game = new BrickBreakerGame(); // BrickBreakerGame 객체 생성
        game.runGame(); // 게임 실행 메서드 호출
    }

    // Brick 클래스 선언 (벽돌 정보를 담는 클래스)
    private static class Brick {
        private int x, y, width, height; // 벽돌의 위치와 크기 정보
        private boolean visible; // 벽돌의 가시성 정보

        public Brick(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.visible = true; // 벽돌을 기본적으로 보이도록 설정
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }
}