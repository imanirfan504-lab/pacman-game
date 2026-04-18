
import java.util.Scanner;
import java.util.ArrayList;

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

    void move(char input, int totalRows, int totalCols) {
        if(input == 'U') {
            if(row > 0) row--;
        }
        if(input == 'D') {
            if(row < totalRows-1) row++;
        }
        if(input == 'L') {
            if(col > 0) col--;
        }
        if(input == 'R') {
            if(col < totalCols-1) col++;
        }
    }

    void increaseScore() { score = score + 10; }
    int getRow() { return row; }
    int getCol() { return col; }
    int getScore() { return score; }
}

class Ghost {
    int row, col;
    String name;

    Ghost(int row, int col, String name) {
        this.row = row;
        this.col = col;
        this.name = name;
    }

    void moveRandom(int totalRows, int totalCols) {
        int step = (int)(Math.random() * 4);
        if(step == 0) {
            if(row > 0) row--;
        }
        if(step == 1) {
            if(row < totalRows-1) row++;
        }
        if(step == 2) {
            if(col > 0) col--;
        }
        if(step == 3) {
            if(col < totalCols-1) col++;
        }
    }

    boolean caughtPacman(Pacman pacman) {
        if(row == pacman.getRow() && col == pacman.getCol())
            return true;
        return false;
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
        for(int i = 0; i < foodList.size(); i++) {
            if(foodList.get(i).isEaten() == false)
                count++;
        }
        return count;
    }

    void checkFoodEaten(Pacman pacman) {
        for(int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            if(food.isEaten() == false) {
                if(food.getRow() == pacman.getRow() && food.getCol() == pacman.getCol()) {
                    food.eat();
                    pacman.increaseScore();
                }
            }
        }
    }

    void printBoard(Pacman pacman) {
        System.out.println();
        for(int i = 0; i < totalCols; i++) System.out.print("+--");
        System.out.println("+");

        for(int i = 0; i < totalRows; i++) {
            System.out.print("|");
            for(int j = 0; j < totalCols; j++) {

                if(pacman.getRow() == i && pacman.getCol() == j) {
                    System.out.print("C |");
                    continue;
                }

                boolean ghostHere = false;
                for(int k = 0; k < ghostList.size(); k++) {
                    if(ghostList.get(k).getRow() == i && ghostList.get(k).getCol() == j) {
                        System.out.print("G |");
                        ghostHere = true;
                        break;
                    }
                }
                if(ghostHere == true) continue;

                boolean foodHere = false;
                for(int k = 0; k < foodList.size(); k++) {
                    if(foodList.get(k).isEaten() == false && foodList.get(k).getRow() == i && foodList.get(k).getCol() == j) {
                        System.out.print(". |");
                        foodHere = true;
                        break;
                    }
                }
                if(foodHere == true) continue;

                System.out.print("  |");
            }
            System.out.println();
            for(int i2 = 0; i2 < totalCols; i2++) System.out.print("+--");
            System.out.println("+");
        }

        System.out.println("score: " + pacman.getScore() + "   food left: " + remainingFood());
        System.out.println("U=up  D=down  L=left  R=right  Q=quit");
    }
}

public class pacman_game{
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int numRows = 5;
        int numCols = 7;

        GameBoard board = new GameBoard(numRows, numCols);
        Pacman pacman = new Pacman(0, 0);

        Ghost ghost1 = new Ghost(0, numCols-1, "ghost1");
        Ghost ghost2 = new Ghost(numRows-1, numCols-1, "ghost2");
        board.addGhost(ghost1);
        board.addGhost(ghost2);

        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                if(i == 0 && j == 0) continue;
                if(i == 0 && j == numCols-1) continue;
                if(i == numRows-1 && j == numCols-1) continue;
                board.addFood(new Food(i, j));
            }
        }

        System.out.println("=== PACMAN GAME ===");
        System.out.println("C=you  G=ghost  .=food");
        System.out.println("EAT ALL FOOD TO WIN!");

        boolean running = true;
        boolean playerWon = false;

        while(running == true) {
            board.printBoard(pacman);

            System.out.print("your move: ");
            String input = scanner.nextLine().trim();

            if(input.length() == 0) continue;

            char move = Character.toUpperCase(input.charAt(0));

            if(move == 'Q') {
                System.out.println("YOU QUIT THE GAME.");
                break;
            }

            pacman.move(move, numRows, numCols);
            board.checkFoodEaten(pacman);

            ArrayList<Ghost> ghostList = board.getGhostList();
            for(int i = 0; i < ghostList.size(); i++) {
                ghostList.get(i).moveRandom(numRows, numCols);
            }

            for(int i = 0; i < ghostList.size(); i++) {
                if(ghostList.get(i).caughtPacman(pacman) == true) {
                    running = false;
                    break;
                }
            }

            if(board.remainingFood() == 0) {
                playerWon = true;
                running = false;
            }
        }

        board.printBoard(pacman);
        System.out.println();

        if(playerWon == true) {
            System.out.println("YOU WIN! score: " + pacman.getScore());
        } else {
            System.out.println("GAME OVER! score: " + pacman.getScore());
        }

        scanner.close();
    }
}
