package com.tass.lifegame.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Set;

import javax.swing.JPanel;

public class GameFieldPanel extends JPanel implements MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 1L;

	private final int CELL_SIZE = 20;
	
	private int fieldSize;
	private boolean gameStarted;
	private Set<Integer> gameField;
	
	private Color selectionColor;
	
	private boolean mouseIN;
	private int mouseX, mouseY;

	public GameFieldPanel(Set<Integer> gameField, int fieldSize) {
		super();
		setGameStarted(false);
		
		this.fieldSize = fieldSize;
		this.gameField = gameField;

		selectionColor = new Color(0f, 0f, 1f, 0.3f);
		
		setPreferredSize(new Dimension(fieldSize * CELL_SIZE, fieldSize * CELL_SIZE));
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void setFieldSize(int fieldSize) {
		this.fieldSize = fieldSize;
		setPreferredSize(new Dimension(fieldSize * CELL_SIZE, fieldSize * CELL_SIZE));
		repaint();
	}
	
	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
		if (!gameStarted)
			mouseIN = true;
	}
	
	public void setGameField(Set<Integer> gameField, int fieldSize) {
		this.gameField = gameField;
		this.fieldSize = fieldSize;
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//Paint selected cells
		g.setColor(Color.RED);
		for (Integer i : gameField) {
			int x = i % fieldSize;
			int y = i / fieldSize;
			g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
		}
		//Paint cell grid
		g.setColor(Color.BLUE);
		for (int i = 0; i < (fieldSize) + 1; i++) {
			g.drawLine(0, i * CELL_SIZE, fieldSize * CELL_SIZE, i * CELL_SIZE);
			g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, fieldSize * CELL_SIZE);
		}
		//Paint cursor
		if (!gameStarted && mouseIN && mouseX < fieldSize && mouseY < fieldSize) {
			g.setColor(selectionColor);
			g.fillRect(mouseX * CELL_SIZE, mouseY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	public void mouseMoved(MouseEvent e) {
		if (!gameStarted && mouseIN) {
			int cellX = (e.getX() / CELL_SIZE);
			int cellY = (e.getY() / CELL_SIZE);
			
			if (cellX != mouseX || cellY != mouseY) {
				mouseX = cellX;
				mouseY = cellY;
				repaint();
			}
		}
	}
	public void mouseClicked(MouseEvent e) {
		if (!gameStarted && mouseIN && mouseX < fieldSize && mouseY < fieldSize) {		
			int position = (fieldSize * (e.getY() / CELL_SIZE)) + (e.getX() / CELL_SIZE);
			if (gameField.contains(position)) {
				gameField.remove(position);
			} 
			else {
				gameField.add(position);
			}
			repaint();
		}
	}
	public void mousePressed(MouseEvent e) {		
	}
	public void mouseReleased(MouseEvent e) {	
	}
	public void mouseEntered(MouseEvent e) {
		if (!gameStarted)
			mouseIN = true;
	}
	public void mouseExited(MouseEvent e) {
		if (!gameStarted) {
			mouseIN = false;
			repaint();
		}
	}
}
