package sorryclient;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import customUI.ClearPanel;
import customUI.PaintedButton;
import customUI.PaintedPanel;
import library.FontLibrary;
import library.ImageLibrary;
import networking.MessageConstants;

public class ColorSelector extends PaintedPanel{

	private static final long serialVersionUID = 1900724217285760485L;

	private Color selection;

	private Timer timer;
	private Integer timeLeft;

	private JLabel timerLabel;
	
	private final int numOptions = 4;
	private final int timeOut = 30;
	private final PaintedButton[] optionButtons;

	private final PaintedButton confirmButton;

	private final static String selectColorString = "Select your color";

	private final static String[] colorNames = {"Red", "Blue", "Green", "Yellow"};
	private final static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

	private final static Insets spacing = new Insets(60,80,60,80);

	public Color getPlayerColor() {
		return selection;
	}

	public ColorSelector(ActionListener confirmAction, Image inImage) {
		super(inImage,true);

		timerLabel = new JLabel();
		timerLabel.setFont(FontLibrary.getFont("fonts/kenvector_future_thin.ttf", Font.PLAIN, 28));
		timerLabel.setBackground(new Color(0,0,0,0));
		timerLabel.setOpaque(true);
		
		timeLeft = timeOut;
		timerLabel.setText("TIME 0:" + timeLeft.toString());
		
		confirmButton = new PaintedButton(
				"Confirm",
				ImageLibrary.getImage("images/buttons/grey_button00.png"),
				ImageLibrary.getImage("images/buttons/grey_button01.png"),
				22
				);
		confirmButton.addActionListener(confirmAction);
		confirmButton.setEnabled(false);

		JLabel selectColorLabel = new JLabel(selectColorString);
		selectColorLabel.setFont(FontLibrary.getFont("fonts/kenvector_future_thin.ttf", Font.PLAIN, 28));

		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (timeLeft > 0){
					decreaseTimer();
				} else {
					timer.stop();
					sendDisconnectMsg();
				}
			}
		});

		ClearPanel ttopPanel = new ClearPanel();
		ttopPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
		ttopPanel.add(timerLabel);
		
		ClearPanel topPanel = new ClearPanel();
		topPanel.setBorder(new EmptyBorder(20, 80, 60, 80));
		topPanel.add(selectColorLabel);
		

		ClearPanel centerPanel = new ClearPanel();
		centerPanel.setLayout(new GridLayout(2,2,10,10));
		Font buttonFont = new Font("",Font.BOLD,22);
		optionButtons = new PaintedButton[numOptions];
		for(int i = 0; i < numOptions; ++i) {
			optionButtons[i] = new PaintedButton(
					colorNames[i],
					ImageLibrary.getImage("images/buttons/"+colorNames[i].toLowerCase()+"_button00.png"),
					ImageLibrary.getImage("images/buttons/"+colorNames[i].toLowerCase()+"_button01.png"),
					22
					);
			final int buttonSelection = i;
			optionButtons[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent ae) {
					OutMail.send(MessageConstants.BROADCAST, MessageConstants.COLOR_SELECT + colorNames[buttonSelection]);
					//selectionIndex = buttonSelection;
					selection = colors[buttonSelection];
					//for(JButton button : optionButtons) button.setEnabled(true);
					//optionButtons[buttonSelection].setEnabled(false);
					//confirmButton.setEnabled(true);
				}
			});
			optionButtons[i].setFont(buttonFont);
			centerPanel.add(optionButtons[i]);
		}
		centerPanel.setBorder(new EmptyBorder(new Insets(0, 80, 0, 80)));

		ClearPanel bottomPanel = new ClearPanel();
		bottomPanel.setLayout(new GridLayout(1,3));
		bottomPanel.setBorder(new EmptyBorder(spacing));
		bottomPanel.add(Box.createGlue());
		bottomPanel.add(Box.createGlue());
		bottomPanel.add(confirmButton);

		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		add(ttopPanel);
		add(topPanel);
		add(centerPanel);
		add(bottomPanel);
	}

	public void colorReserve(String colors) {
		for(int i = 0; i < colorNames.length; i++) {
			if(colors.contains(colorNames[i])) {
				optionButtons[i].setEnabled(false);
			}
		}
	}

	public void colorSwap(String msg) {
		String[] split = msg.split(MessageConstants.SPLIT);
		if(msg.contains(MessageConstants.SUCCESS)) {
			confirmButton.setEnabled(true);
			return;
		}
		for(int i = 0; i < colorNames.length; i++) {
			if(split[1].startsWith(colorNames[i])) {
				optionButtons[i].setEnabled(true);
			}
			if(split[1].endsWith(colorNames[i])) {
				optionButtons[i].setEnabled(false);
			}
		}
	}

	public void free(String color) {
		for(int i = 0; i < colorNames.length; i++) {
			if(color.equals(colorNames[i])) {
				optionButtons[i].setEnabled(true);
			}
		}
	}

	public void stopTimer() {
		timer.stop();
	}

	public void kickTimerStart() {
		timer.start();
	}

	private void sendDisconnectMsg() {
		System.exit(0);
	}
	
	private void decreaseTimer() {
		timeLeft--;
		if (timeLeft > 9) {
			timerLabel.setText("TIME 0:" + timeLeft.toString());
		} else {
			timerLabel.setText("TIME 0:" + "0" + timeLeft.toString());
		}
		
		revalidate();
		repaint();
	}
}
