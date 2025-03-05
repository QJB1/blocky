package com.gamewerks.blocky.gfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.gamewerks.blocky.engine.BlockyGame;
import com.gamewerks.blocky.engine.Piece;
import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;

public class BlockyPanel extends JPanel {
    private static final int BLOCK_SIZE = 32;
    
    private int width;
    private int height;
    private BlockyGame game;
    
    public BlockyPanel(BlockyGame game) {
        width = Constants.BOARD_WIDTH * BLOCK_SIZE;
        height = (Constants.BOARD_HEIGHT - 2) * BLOCK_SIZE;
        this.game = game;
        setPreferredSize(new Dimension(width, height));
    }
    
    public void paintComponent(Graphics g) {
        boolean[][] well = game.getWell();
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.BLUE);
        Piece activePiece = game.getActivePiece();
        if (activePiece != null) {
            boolean[][] layout = activePiece.getLayout();
            Position activePos = activePiece.getPosition();
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    if (layout[row][col]) {
                        g.fillRect((activePos.col + col) * BLOCK_SIZE,
                                    (Constants.BOARD_HEIGHT - 1 - (activePos.row - row) - 2) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
        }
        
        g.setColor(Color.GREEN);
        for (int row = 0; row < Constants.BOARD_HEIGHT; row++) {
            for (int col = 0; col < Constants.BOARD_WIDTH; col++) {
                if (well[row][col]) {
                    g.fillRect(col * BLOCK_SIZE, BLOCK_SIZE * (Constants.BOARD_HEIGHT - 1 - row - 2), BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
    }
}
