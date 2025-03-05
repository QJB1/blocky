package com.gamewerks.blocky.engine;

import java.util.LinkedList;
import java.util.List;

import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;

public class Board {
    private boolean[][] well;
    
    public Board() {
        well = new boolean[Constants.BOARD_HEIGHT][Constants.BOARD_WIDTH];
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row <= well.length && col >= 0 && col < well[0].length; // it was row <= well.length before
    }
    
    public boolean collides(Piece p) {
        return collides(p.getLayout(), p.getPosition());
    }
    
    public boolean collides(boolean[][] layout, Position pos) {
        for (int row = 0; row < layout.length; row++) {
            int wellRow = pos.row - row;
            for (int col = 0; col < layout[row].length; col++) {
                int wellCol = col + pos.col;
                if (layout[row][col]) {
                    if (!isValidPosition(wellRow, wellCol)) {
                        return true;
                    } else if (well[wellRow][wellCol]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void addToWell(Piece p) {
        boolean[][] layout = p.getLayout();
        Position pos = p.getPosition();
        for (int row = 0; row < layout.length; row++) {
            int wellRow = pos.row - row;
            for (int col = 0; col < layout[row].length; col++) {
                int wellCol = pos.col + col;
                if (isValidPosition(wellRow, wellCol) && layout[row][col]) {
                    well[wellRow][wellCol] = true;
                }
            }
        }
    }
    
    public void deleteRow(int n) {
        for (int row = 0; row < n - 1; row++) {
            for (int col = 0; col < Constants.BOARD_WIDTH; col++) {
                well[row][col] = well[row+1][col];
            }
        }
        for (int col = 0; col < Constants.BOARD_WIDTH; col++) {
            well[n][col] = false;
        }
    }
    
    public void deleteRows(List rows) {
        for (int i = 0; i < rows.size(); i++) {
            int row = (Integer) rows.get(i);
            deleteRow(row);
        }
    }
    
    public boolean isCompletedRow(int row) {
        boolean isCompleted = true;
        for (int col = 0; col < Constants.BOARD_WIDTH; col++) {
            isCompleted = isCompleted && well[row][col];
        }
        return isCompleted;
    }
    
    public List getCompletedRows() {
        List completedRows = new LinkedList();
        for (int row = 0; row < Constants.BOARD_HEIGHT; row++) {
            if (isCompletedRow(row)) {
                completedRows.add(well[row]); // it was completedRows.add(well[row]);         completedRows.add(row);
            }
        }
        return completedRows;
    }
    
    public boolean[][] getWell() { return well; }
}
