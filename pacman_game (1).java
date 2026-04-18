import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class Food {
    int row, col;
    boolean eaten;

    Food(int row, int col) {
        this.row = row;
        this.col = col;
        eaten = false;
    }

    void eat() { eaten = true; }
    boolean isEaten() { return eaten; }
    int getRow() { return row; }
    int getCol() { return col; }
}

class Pacman {
    int row, col, score;

    Pacman(int row, int col) {
        this.row = row;
        this.col = col;
        score = 0;
    }

    void move(int dRow, int dCol, int totalRows, int totalCols) {
        int newRow = row + dRow;
        int newCol = col + dCol;
        if (newRow >= 0 && newRow < totalRows) row = newRow;
        if (newCol >= 0 && newCol < totalCols) col = newCol;
    }

    void increaseScore() { score += 10; }
    int getRow() { return row; }
    int getCol() { return col; }
    int getScore() { return score; }
}

class Ghost {
    int row, col;
    String name;
    Color color;

    Ghost(int row, int col, String name, Color color) {
        this.row = row;
        this.col = col;
        this.name = name;
        this.color = color;
    }

    void moveRandom(int totalRows, int totalCols) {
        int step = (int)(Math.random() * 4);
        if (step == 0 && row > 0) row--;
        else if (step == 1 && row < totalRows - 1) row++;
        else if (step == 2 && col > 0) col--;
        else if (step == 3 && col < totalCols - 1) col++;
    }

    boolean caughtPacman(Pacman p) {
        return row == p.getRow() && col == p.getCol();
    }

    int getRow() { return row; }
    int getCol() { return col; }
}

class GameBoard {
    int totalRows, totalCols;
    ArrayList<Food> foodList;
    ArrayList<Ghost> ghostList;

    GameBoard(int totalRows, int totalCols) {
        this.totalRows = totalRows;
        this.totalCols = totalCols;
        foodList = new ArrayList<Food>();
        ghostList = new ArrayList<Ghost>();
    }

    void addFood(Food food) { foodList.add(food); }
    void addGhost(Ghost ghost) { ghostList.add(ghost); }
    ArrayList<Ghost> getGhostList() { return ghostList; }

    int remainingFood() {
        int count = 0;
        for (int i = 0; i < foodList.size(); i++) {
            if (!foodList.get(i).isEaten()) count++;
        }
        return count;
    }

    void checkFoodEaten(Pacman pacman) {
        for (int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            if (!food.isEaten() && food.getRow() == pacman.getRow() && food.getCol() == pacman.getCol()) {
                food.eat();
                pacman.increaseScore();
            }
        }
    }

    ArrayList<Food> getFoodList() { return foodList; }
}

public class pacman_game extends JPanel implements KeyListener, ActionListener {

    static final int CELL = 80;
    static final int ROWS = 5;
    static final int COLS = 7;
    static final int HUD = 60;

    GameBoard board;
    Pacman pacman;
    Timer timer;
    int mouthAngle = 45;
    int mouthDir = -5;
    int dRow = 0, dCol = 0;
    String state = "PLAYING";
    int frameCount = 0;

    pacman_game() {
        setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL + HUD));
        setBackground(new Color(10, 10, 30));
        setFocusable(true);
        addKeyListener(this);

        board = new GameBoard(ROWS, COLS);
        pacman = new Pacman(0, 0);

        board.addGhost(new Ghost(0, COLS - 1, "Blinky", new Color(220, 50, 50)));
        board.addGhost(new Ghost(ROWS - 1, COLS - 1, "Pinky", new Color(220, 100, 200)));
        board.addGhost(new Ghost(ROWS - 1, 0, "Inky", new Color(50, 200, 220)));

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (i == 0 && j == 0) continue;
                if (i == 0 && j == COLS - 1) continue;
                if (i == ROWS - 1 && j == COLS - 1) continue;
                if (i == ROWS - 1 && j == 0) continue;
                board.addFood(new Food(i, j));
            }
        }

        timer = new Timer(150, this);
        timer.start();
    }

    public void actionPerformed(ActionEvent e) {
        if (!state.equals("PLAYING")) return;

        frameCount++;
        mouthAngle += mouthDir;
        if (mouthAngle <= 5 || mouthAngle >= 45) mouthDir = -mouthDir;

        pacman.move(dRow, dCol, ROWS, COLS);
        board.checkFoodEaten(pacman);

        ArrayList<Ghost> ghosts = board.getGhostList();
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).moveRandom(ROWS, COLS);
        }

        for (int i = 0; i < ghosts.size(); i++) {
            if (ghosts.get(i).caughtPacman(pacman)) {
                state = "GAME_OVER";
                timer.stop();
            }
        }

        if (board.remainingFood() == 0) {
            state = "WIN";
            timer.stop();
        }

        repaint();
    }

    public void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g);
        drawFood(g);
        drawPacman(g);
        drawGhosts(g);
        drawHUD(g);

        if (!state.equals("PLAYING")) drawOverlay(g);
    }

    void drawGrid(Graphics2D g) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                g.setColor(new Color(20, 20, 60));
                g.fillRoundRect(j * CELL + 3, i * CELL + 3, CELL - 6, CELL - 6, 12, 12);
                g.setColor(new Color(40, 40, 100));
                g.setStroke(new BasicStroke(1));
                g.drawRoundRect(j * CELL + 3, i * CELL + 3, CELL - 6, CELL - 6, 12, 12);
            }
        }
    }

    void drawFood(Graphics2D g) {
        ArrayList<Food> foods = board.getFoodList();
        for (int i = 0; i < foods.size(); i++) {
            Food food = foods.get(i);
            if (!food.isEaten()) {
                int cx = food.getCol() * CELL + CELL / 2;
                int cy = food.getRow() * CELL + CELL / 2;
                g.setColor(new Color(255, 220, 100));
                g.fillOval(cx - 5, cy - 5, 10, 10);
                g.setColor(new Color(255, 255, 180));
                g.fillOval(cx - 2, cy - 4, 4, 3);
            }
        }
    }

    void drawPacman(Graphics2D g) {
        int cx = pacman.getCol() * CELL + 8;
        int cy = pacman.getRow() * CELL + 8;
        int size = CELL - 16;

        g.setColor(new Color(255, 220, 0));
        g.fillArc(cx, cy, size, size, mouthAngle, 360 - mouthAngle * 2);

        g.setColor(new Color(10, 10, 30));
        g.fillOval(cx + size / 2 - 4, cy + 8, 7, 7);
    }

    void drawGhosts(Graphics2D g) {
        ArrayList<Ghost> ghosts = board.getGhostList();
        for (int i = 0; i < ghosts.size(); i++) {
            Ghost ghost = ghosts.get(i);
            int gx = ghost.getCol() * CELL + 8;
            int gy = ghost.getRow() * CELL + 8;
            int size = CELL - 16;

            g.setColor(ghost.color);
            g.fillArc(gx, gy, size, size, 0, 180);
            g.fillRect(gx, gy + size / 2, size, size / 2 - 4);

            int waveW = size / 4;
            for (int w = 0; w < 4; w++) {
                g.setColor(new Color(10, 10, 30));
                g.fillArc(gx + w * waveW, gy + size - 10, waveW, 12, 180, 180);
            }

            g.setColor(Color.WHITE);
            g.fillOval(gx + size / 4 - 4, gy + size / 3, 12, 12);
            g.fillOval(gx + size * 3 / 4 - 8, gy + size / 3, 12, 12);
            g.setColor(new Color(30, 50, 200));
            g.fillOval(gx + size / 4 - 1, gy + size / 3 + 3, 6, 6);
            g.fillOval(gx + size * 3 / 4 - 5, gy + size / 3 + 3, 6, 6);
        }
    }

    void drawHUD(Graphics2D g) {
        int y = ROWS * CELL;
        g.setColor(new Color(15, 15, 45));
        g.fillRect(0, y, COLS * CELL, HUD);
        g.setColor(new Color(40, 40, 100));
        g.drawLine(0, y, COLS * CELL, y);

        g.setColor(new Color(255, 220, 0));
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.drawString("SCORE: " + pacman.getScore(), 20, y + 25);

        g.setColor(new Color(100, 180, 255));
        g.drawString("FOOD: " + board.remainingFood(), 200, y + 25);

        g.setColor(new Color(150, 150, 200));
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString("ARROW KEYS to move", 20, y + 48);
    }

    void drawOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, COLS * CELL, ROWS * CELL + HUD);

        if (state.equals("WIN")) {
            g.setColor(new Color(80, 255, 120));
            g.setFont(new Font("Monospaced", Font.BOLD, 36));
            g.drawString("YOU WIN!", COLS * CELL / 2 - 90, ROWS * CELL / 2 - 10);
        } else {
            g.setColor(new Color(255, 80, 80));
            g.setFont(new Font("Monospaced", Font.BOLD, 32));
            g.drawString("GAME OVER", COLS * CELL / 2 - 105, ROWS * CELL / 2 - 10);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        g.drawString("Score: " + pacman.getScore(), COLS * CELL / 2 - 45, ROWS * CELL / 2 + 30);
        g.drawString("Press R to restart", COLS * CELL / 2 - 80, ROWS * CELL / 2 + 58);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP)    { dRow = -1; dCol = 0; }
        if (key == KeyEvent.VK_DOWN)  { dRow = 1;  dCol = 0; }
        if (key == KeyEvent.VK_LEFT)  { dRow = 0;  dCol = -1; }
        if (key == KeyEvent.VK_RIGHT) { dRow = 0;  dCol = 1; }
        if (key == KeyEvent.VK_R) restart();
    }

    void restart() {
        board = new GameBoard(ROWS, COLS);
        pacman = new Pacman(0, 0);
        dRow = 0; dCol = 0;
        state = "PLAYING";
        frameCount = 0;

        board.addGhost(new Ghost(0, COLS - 1, "Blinky", new Color(220, 50, 50)));
        board.addGhost(new Ghost(ROWS - 1, COLS - 1, "Pinky", new Color(220, 100, 200)));
        board.addGhost(new Ghost(ROWS - 1, 0, "Inky", new Color(50, 200, 220)));

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (i == 0 && j == 0) continue;
                if (i == 0 && j == COLS - 1) continue;
                if (i == ROWS - 1 && j == COLS - 1) continue;
                if (i == ROWS - 1 && j == 0) continue;
                board.addFood(new Food(i, j));
            }
        }

        timer.start();
        repaint();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pacman Game - OOP Lab");
        pacman_game game = new pacman_game();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}
