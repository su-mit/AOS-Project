package com.tass.lifegame.interfaces;

public interface Updatable {
	public void setCell(int pos);
	public boolean getCell(int pos);
	public void updateField();
	public int[] getValuesToCheck();
}
