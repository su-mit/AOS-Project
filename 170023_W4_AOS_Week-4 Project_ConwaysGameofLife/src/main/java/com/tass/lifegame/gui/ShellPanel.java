package com.tass.lifegame.gui;

//@author : Preetinder Singh

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.tass.lifegame.interfaces.UpdateShell;
import com.tass.lifegame.logic.GameField;
import com.tass.lifegame.logic.processTurn;
import com.tass.lifegame.logic.updateField;

public class ShellPanel extends JFrame implements UpdateShell, ActionListener {
	private static final long serialVersionUID = 1L;
	
	private int sideSize;
	private GameField gameField;
	private Set<Integer> field;
	private GameFieldPanel gameFieldPanel;
	private boolean isGameStarted;
	private final int CPUs = Runtime.getRuntime().availableProcessors();
	
	private CyclicBarrier barrier;
	private List<processTurn> processList;
	private List<Future> futureList;
	private updateField updater;
	private ExecutorService executor;
	//SWING components
	private JMenuItem gameStart;
	private JMenuItem gamePause;
	private JMenuItem gameClear;
	private JMenuItem gameQuit;
	private JMenu sizeMenu;
	private JMenu speedMenu;
	//SWING action commands
	private final String GAME_START = "GAME_START";
	private final String GAME_PAUSE = "GAME_PAUSE";
	private final String GAME_CLEAR = "GAME_CLEAR";
	private final String GAME_QUIT = "GAME_QUIT";
	private final String SIZE_FIELD_20 = "SIZE_FIELD_20";
	private final String SIZE_FIELD_30 = "SIZE_FIELD_30";
	private final String SIZE_FIELD_40 = "SIZE_FIELD_40";
	private final String SET_SPEED_SLOW = "SET_SPEED_SLOW";
	private final String SET_SPEED_NORMAL = "SET_SPEED_NORMAL";
	private final String SET_SPEED_FAST = "SET_SPEED_FAST";
	
	private final int GAME_SPEED_SLOW = 500;
	private final int GAME_SPEED_NORMAL = 250;
	private final int GAME_SPEED_FAST = 125;
		
	public ShellPanel() {
		super();
		
		sideSize = 20;
		gameField = new GameField(null, sideSize);
		field = gameField.getField();
		gameFieldPanel = new GameFieldPanel(field, sideSize);
		isGameStarted = false;
		
		updater = new updateField(gameField, GAME_SPEED_NORMAL, this);
		barrier = new CyclicBarrier(CPUs, updater);
		processList = new ArrayList<processTurn>();
		for (int i = 0; i < CPUs; i++)
			processList.add(new processTurn(gameField, i, 
					CPUs, sideSize, barrier));
		futureList = new ArrayList<Future>();
		executor = Executors.newFixedThreadPool(CPUs);
		
		initSwing();
	}
	
	public void initSwing() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		gameStart = new JMenuItem("Start");
		gameStart.setMnemonic(KeyEvent.VK_S);
		gameStart.setActionCommand(GAME_START);
		gameStart.addActionListener(this);
		gameMenu.add(gameStart);
		gamePause = new JMenuItem("Pause");
		gamePause.setMnemonic(KeyEvent.VK_P);
		gamePause.setActionCommand(GAME_PAUSE);
		gamePause.addActionListener(this);
		gamePause.setEnabled(false);
		gameMenu.add(gamePause);
		gameClear = new JMenuItem("Clear");
		gameClear.setMnemonic(KeyEvent.VK_C);
		gameClear.setActionCommand(GAME_CLEAR);
		gameClear.addActionListener(this);
		gameMenu.add(gameClear);
		gameQuit = new JMenuItem("Quit");
		gameQuit.setMnemonic(KeyEvent.VK_Q);
		gameQuit.setActionCommand(GAME_QUIT);
		gameQuit.addActionListener(this);
		gameMenu.add(gameQuit);
		menuBar.add(gameMenu);
		
		sizeMenu = new JMenu("Game field size");
		sizeMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem sizeSmall = new JMenuItem("20x20");
		sizeSmall.setMnemonic(KeyEvent.VK_2);
		sizeSmall.setActionCommand(SIZE_FIELD_20);
		sizeSmall.addActionListener(this);
		sizeMenu.add(sizeSmall);
		JMenuItem sizeMiddle = new JMenuItem("30x30");
		sizeMiddle.setMnemonic(KeyEvent.VK_3);
		sizeMiddle.setActionCommand(SIZE_FIELD_30);
		sizeMiddle.addActionListener(this);
		sizeMenu.add(sizeMiddle);
		JMenuItem sizeBig = new JMenuItem("40x40");
		sizeBig.setMnemonic(KeyEvent.VK_4);
		sizeBig.setActionCommand(SIZE_FIELD_40);
		sizeBig.addActionListener(this);
		sizeMenu.add(sizeBig);
		menuBar.add(sizeMenu);
		
		speedMenu = new JMenu("Game speed");
		speedMenu.setMnemonic(KeyEvent.VK_A);
		JMenuItem speedSlow = new JMenuItem("Slow");
		speedSlow.setMnemonic(KeyEvent.VK_L);
		speedSlow.setActionCommand(SET_SPEED_SLOW);
		speedSlow.addActionListener(this);
		speedMenu.add(speedSlow);
		JMenuItem speedNormal = new JMenuItem("Normal");
		speedNormal.setMnemonic(KeyEvent.VK_N);
		speedNormal.setActionCommand(SET_SPEED_NORMAL);
		speedNormal.addActionListener(this);
		speedMenu.add(speedNormal);
		JMenuItem speedHigh = new JMenuItem("Fast");
		speedHigh.setMnemonic(KeyEvent.VK_T);
		speedHigh.setActionCommand(SET_SPEED_FAST);
		speedHigh.addActionListener(this);
		speedMenu.add(speedHigh);
		menuBar.add(speedMenu);
		
		setJMenuBar(menuBar);
		
		setLayout(new BorderLayout());
		add(gameFieldPanel);		

		pack();
		setResizable(false);
		
		setTitle("Conway's Game of Life");
				
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void startGame() {
		gameField.setField(field, sideSize);
		for (processTurn pTurn : processList)
		{
			pTurn.setSideSize(sideSize);
			futureList.add(executor.submit(pTurn));
		}
	}
	
	private void stopGame() {
		for (Future future : futureList) {
			future.cancel(true);
		}
		barrier.reset();
	}
	
	public void update() {
		field = gameField.getField();
		gameFieldPanel.setGameField(field, sideSize);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (GAME_QUIT == command) {
			executor.shutdownNow();
			this.dispose();
		}
		if (isGameStarted) {
			if (GAME_PAUSE == command) {
				isGameStarted = false;
				gameFieldPanel.setGameStarted(isGameStarted);
				gameStart.setEnabled(true);
				gameClear.setEnabled(true);
				gamePause.setEnabled(false);
				sizeMenu.setEnabled(true);
				speedMenu.setEnabled(true);
				stopGame();
			}
		}
		else {
			if(GAME_START == command) {
				isGameStarted = true;
				gameFieldPanel.setGameStarted(isGameStarted);
				gameStart.setEnabled(false);
				gameClear.setEnabled(false);
				gamePause.setEnabled(true);
				sizeMenu.setEnabled(false);
				speedMenu.setEnabled(false);
				startGame();
			}
			if (GAME_CLEAR == command) {
				field.clear();
				gameFieldPanel.repaint();
			}
			if (SIZE_FIELD_20 == command) {
				sideSize = 20;
				field.clear();
				gameFieldPanel.setFieldSize(sideSize);
				pack();
			}
			if (SIZE_FIELD_30 == command) {
				sideSize = 30;
				field.clear();
				gameFieldPanel.setFieldSize(sideSize);
				pack();
			}
			if (SIZE_FIELD_40 == command) {
				sideSize = 40;
				field.clear();
				gameFieldPanel.setFieldSize(sideSize);
				pack();
			}
			if (SET_SPEED_SLOW == command) {
				updater.setPause(GAME_SPEED_SLOW);
			}
			if (SET_SPEED_NORMAL == command) {
				updater.setPause(GAME_SPEED_NORMAL);
			}
			if (SET_SPEED_FAST == command) {
				updater.setPause(GAME_SPEED_FAST);
			}
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ShellPanel();
			}
		});
	}
}
