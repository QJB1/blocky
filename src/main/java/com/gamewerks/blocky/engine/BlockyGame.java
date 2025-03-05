package com.gamewerks.blocky.engine;

import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;

public class BlockyGame {
    private static final int LOCK_DELAY_LIMIT = 30;
    
    private Board board;
    private Piece activePiece;
    private Direction movement;
    private int lockCounter;

    private PieceKind[] piece_order;
    private int curr_order;
    
    public BlockyGame() {
        board = new Board();
        movement = Direction.NONE;
        lockCounter = 0;

        curr_order = 0;
        piece_order = PieceKind.ALL.clone(); 
        shuffle();
        trySpawnBlock();
    }
    
    private void trySpawnBlock() {
        if (activePiece == null) { 
            activePiece = new Piece(getNextPiece(), new Position(Constants.BOARD_HEIGHT - 1, Constants.BOARD_WIDTH / 2 - 2));
            if (board.collides(activePiece)) {
                System.exit(0);
            }
        }
    }

    private void shuffle() {
        for (int i = piece_order.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1)); // rand num: 0 <= j <= i
            PieceKind temp = piece_order[i];
            piece_order[i] = piece_order[j];
            piece_order[j] = temp;
        } 
    }

    private PieceKind getNextPiece() {
        if (curr_order >= piece_order.length) {
            shuffle();
            curr_order = 0;
        }
        return piece_order[curr_order++];
    }

    private void processMovement() {
        Position nextPos;
        switch(movement) {
        case NONE:
            nextPos = activePiece.getPosition();
            break;
        case LEFT:
            nextPos = activePiece.getPosition().add(0, -1);
            break;
        case RIGHT:
            nextPos = activePiece.getPosition().add(0, 1);
            break; // this line was missing before, causing RIGHT to not work
        default:
            throw new IllegalStateException("Unrecognized direction: " + movement.name());
        }
        if (!board.collides(activePiece.getLayout(), nextPos)) {
            activePiece.moveTo(nextPos);
        }
    }
    
    private void processGravity() {
        Position nextPos = activePiece.getPosition().add(-1, 0);
        if (!board.collides(activePiece.getLayout(), nextPos)) {
            lockCounter = 0;
            activePiece.moveTo(nextPos);
        } else {
            if (lockCounter < LOCK_DELAY_LIMIT) {
                lockCounter += 1;
            } else {
                board.addToWell(activePiece);
                lockCounter = 0;
                activePiece = null;
            }
        }
    }
    
    private void processClearedLines() {
        board.deleteRows(board.getCompletedRows());
    }
    
    public void step() {
        trySpawnBlock();
        processGravity();
        processClearedLines();
    }
    
    public boolean[][] getWell() {
        return board.getWell();
    }
    
    public Piece getActivePiece() { return activePiece; }
    // setDirection() function was missing processMovement(); causing the L and R key inputs to not register
    public void setDirection(Direction movement) { this.movement = movement; processMovement();} 
    public void rotatePiece(boolean dir) {
        // save curr position before rotation
        Position oldPos = activePiece.getPosition();
    
        // perform rotation
        activePiece.rotate(dir);
    
        // if new orientation collides, moving piece up by one row so it won't go out of bounds
        if (board.collides(activePiece)) {
            activePiece.moveTo(oldPos.add(1, 0));
            
            // if still colliding after the upward shift, revert both the move and the rotation
            if (board.collides(activePiece)) {
                activePiece.moveTo(oldPos);
                // undo rotation by rotating in the opposite direction
                activePiece.rotate(!dir);
            }
        }
    }
}
