package com.tass.lifegame.logic;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.tass.lifegame.interfaces.Updatable;

public class processTurn implements Runnable {
	private Updatable gameField;
	private int no, CPUs, sideSize;
	private CyclicBarrier barrier;
	
	public processTurn(Updatable gameField, int no, int CPUs, int sideSize, CyclicBarrier barrier) {
		this.gameField = gameField;
		this.no = no;
		this.CPUs = CPUs;
		this.sideSize = sideSize;
		this.barrier = barrier;
	}
	
	public void setSideSize(int sideSize) {
		this.sideSize = sideSize;
	}
	
	public void run() {
		int[] toProcess;
		while(!Thread.currentThread().isInterrupted()) {
			toProcess = gameField.getValuesToCheck();
			try {
				for (int counter = 0; counter < toProcess.length; counter++) 
				{
					if ((counter % CPUs) == no) {
						int neighboursCount = 0;

						int quotient = toProcess[counter] / sideSize;
						int remainder = toProcess[counter] % sideSize;
						
						if (quotient != 0 && remainder != 0) {
							if (gameField.getCell(toProcess[counter] - sideSize - 1))
								neighboursCount++;
						}
						if (quotient != 0) {
							if (gameField.getCell(toProcess[counter] - sideSize))
								neighboursCount++;
						}
						if (quotient != 0 && remainder != (sideSize - 1)) {
							if (gameField.getCell(toProcess[counter] - sideSize + 1))
								neighboursCount++;
						}
						if (remainder != 0) {
							if (gameField.getCell(toProcess[counter] - 1))
								neighboursCount++;
						}
						if (remainder != (sideSize - 1)) {
							if (gameField.getCell(toProcess[counter] + 1))
								neighboursCount++;
						}
						if (quotient != (sideSize - 1) && remainder != 0) {
							if (gameField.getCell(toProcess[counter] + sideSize - 1))
								neighboursCount++;
						}
						if (quotient != (sideSize - 1)) {
							if (gameField.getCell(toProcess[counter] + sideSize))
								neighboursCount++;
						}
						if (quotient != (sideSize - 1) && remainder != (sideSize - 1)) {
							if (gameField.getCell(toProcess[counter] + sideSize + 1))
								neighboursCount++;
						}
												
						if ((2 == neighboursCount || 3 == neighboursCount) && gameField.getCell(toProcess[counter]))
							gameField.setCell(toProcess[counter]);
						if (!gameField.getCell(toProcess[counter]) && 3 == neighboursCount)
							gameField.setCell(toProcess[counter]);
					}
				}
				barrier.await();
			} catch (BrokenBarrierException bException) {
				bException.printStackTrace();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
