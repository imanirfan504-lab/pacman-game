# Pacman Game

**Course:** Object Oriented Programming (CMPE-221L)  
**Assignment:** Complex Engineering Problem (Lab 1)  
**Semester:** 2nd | **Session:** 2025  
**Teacher:** Mr. Shahmeer  
**University:** University of Engineering and Technology, Lahore — Computer Engineering Department

---

## Description

A console-based Pacman game built in Java using Object Oriented Programming concepts. The game runs in the terminal where the player controls Pacman using keyboard inputs to eat all the food while avoiding ghosts.

The `pacman_game` class contains the main method and initializes the game by creating instances of `GameBoard`, `Pacman`, `Ghost`, and `Food`. It also contains the game loop and handles user input, allowing the player to control Pacman's movement around the board.

---

## Classes

| Class | Role |
|---|---|
| `pacman_game` | Main method, game loop, user input |
| `GameBoard` | Board display and game logic |
| `Pacman` | Player movement, position, score |
| `Ghost` | Enemy random movement |
| `Food` | Food pellets on the board |

---

## How to Play

- Run the program in terminal or IntelliJ IDEA
- Use **U** = Up, **D** = Down, **L** = Left, **R** = Right to move
- Eat all the food dots to **win**
- Avoid ghosts or it's **game over**
- Press **Q** to quit anytime

---

## OOP Concepts Used

- Classes and Objects
- Aggregation and Composition
- ArrayList for managing Food and Ghost objects
- Encapsulation using getters and methods
