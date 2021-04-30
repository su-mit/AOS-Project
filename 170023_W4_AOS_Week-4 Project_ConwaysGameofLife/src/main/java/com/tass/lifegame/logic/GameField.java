package com.tass.lifegame.logic;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.tass.lifegame.interfaces.Updatable;

public class GameField implements Updatable {
	private int[] valuesToCheck;
	private Set<Integer> currentField;
	private Set<Integer> futureField;
	
	private int sideSize;
	
	private Object syncronizeValuesToCheck = new Object();
	
	public GameField(Set<Integer> field, int sideSize) {
		currentField = new TreeSet<Integer>();
		futureField = new TreeSet<Integer>();
		
		this.sideSize = sideSize;
		
		if (null != field) {
			for (Integer i : field) {
				currentField.add(new Integer(i));
			}
		}
		
		searchValuesToCheck();
	}
	
	public synchronized Set<Integer> getField() {
		Set<Integer> tmp = new TreeSet<Integer>();
		
		for (Integer i : currentField) {
			tmp.add(new Integer(i));
		}
		
		return tmp;
	}

	public synchronized void setField(Set<Integer> field, int sideSize) {
		this.sideSize = sideSize;  
		
		currentField.clear();
		futureField.clear();
		
		for (Integer i : field) {
			currentField.add(new Integer(i));
		}
		
		searchValuesToCheck();
	}

	public synchronized void setCell(int pos) {
		if (0 <= pos && pos < Math.pow(sideSize, 2)) {
			futureField.add(pos);
		}
	}

	public synchronized boolean getCell(int pos) {
		if (0 <= pos && pos < Math.pow(sideSize, 2)) {
			return currentField.contains(pos);
		}
		return false;
	}

	public synchronized void updateField() {
		currentField.clear();
		for (Integer i : futureField) {
			currentField.add(new Integer(i));
		}
		futureField.clear();
		
		searchValuesToCheck();
	}

	public int[] getValuesToCheck() {
		synchronized (syncronizeValuesToCheck) {
			return valuesToCheck;
		}
	}
	
	private void searchValuesToCheck() {
		Set<Integer> tmp = new TreeSet<Integer>();
		
		synchronized (syncronizeValuesToCheck) {
			for (Integer i : currentField) {
				tmp.add(new Integer(i));
				
				int quotient = i / sideSize;
				int remainder = i % sideSize;
				
				if (quotient != 0 && remainder != 0) {
					tmp.add(new Integer(i - sideSize - 1));
				}
				if (quotient != 0) {
					tmp.add(new Integer(i - sideSize));
				}
				if (quotient != 0 && remainder != (sideSize - 1)) {
					tmp.add(new Integer(i - sideSize + 1));
				}
				if (remainder != 0) {
					tmp.add(new Integer(i - 1));
				}
				if (remainder != (sideSize - 1)) {
					tmp.add(new Integer(i + 1));
				}
				if (quotient != (sideSize - 1) && remainder != 0) {
					tmp.add(new Integer(i + sideSize - 1));
				}
				if (quotient != (sideSize - 1)) {
					tmp.add(new Integer(i + sideSize));
				}
				if (quotient != (sideSize - 1) && remainder != (sideSize - 1)) {
					tmp.add(new Integer(i + sideSize + 1));
				}
			}
			
			valuesToCheck = new int[tmp.size()];
			Iterator<Integer> iterator = tmp.iterator();
			
			for (int i = 0; i < tmp.size(); i++) {
				valuesToCheck[i] = iterator.next();
			}
		}
	}
}
