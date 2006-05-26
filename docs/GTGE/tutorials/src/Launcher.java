// JFC
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

// GTGE
import com.golden.gamedev.*;


/**
 * Tutorial source code launcher.
 */
public class Launcher extends JFrame implements ActionListener, Runnable {


	private String action;


	public Launcher() {
		super("Launch Tutorial Example");


		JPanel contentPane = new JPanel(new BorderLayout());

		JLabel label = new JLabel("GTGE Tutorial Example", JLabel.CENTER);
		label.setFont(new Font("Verdana", Font.PLAIN, 30));
		contentPane.add(label, BorderLayout.NORTH);

		JPanel pane = new JPanel(new GridLayout(0, 1));

			// Tutorial 4
			pane.add(createButton("4", "GTGE Game Skeleton", false));

			// Tutorial 5
			pane.add(createButton("5_1", "Empty Game in Windowed Mode"));
			pane.add(createButton("5_2", "Empty Game in Fullscreen Mode"));
			pane.add(createButton("5_3", "Empty Game in Applet Mode (the game is embedded in Tutorial5_3.html)", false));

			// Tutorial 6
			pane.add(createButton("6", "Show all GTGE game engines basic usage"));

			// Tutorial 7
			pane.add(createButton("7_1", "Sprite"));
			pane.add(createButton("7_2", "Sprite in action"));
			pane.add(createButton("7_3", "Controlling sprite behaviour using Timer class"));

			// Tutorial 8
			pane.add(createButton("8_1", "Background"));
			pane.add(createButton("8_2", "Various background types"));
			pane.add(createButton("8_3", "Background view port"));

			// Tutorial 9
			pane.add(createButton("9_1", "Grouping sprites"));
			pane.add(createButton("9_2", "Removing sprite from group"));

			// Tutorial 10
			pane.add(createButton("10", "Collision in game"));

			// Tutorial 11
			pane.add(createButton("11", "Playfield for automate the game"));
		contentPane.add(pane, BorderLayout.CENTER);

		setContentPane(contentPane);
		pack();

		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JPanel createButton(String num, String desc) {
		return createButton(num, desc, true);
	}

	private JPanel createButton(String num, String desc, boolean enabled) {
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		pane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

			JButton btn = new JButton("Tutorial" + num);
			btn.setPreferredSize(new Dimension(120,25));
			btn.addActionListener(this);
			btn.setEnabled(enabled);
			btn.setFont(new Font("Verdana", Font.BOLD, 12));

			JLabel label = new JLabel(desc);
			label.setFont(new Font("Verdana", Font.PLAIN, 11));

		pane.add(btn);
		pane.add(label);

		return pane;
	}


	public void actionPerformed(ActionEvent e) {
		action = e.getActionCommand();

		new Thread(this).start();
	}

	public void run() {
		Game gameObject = null;
		Dimension dimension = new Dimension(640, 480);
		boolean fullscreen = false;

		if (action.equals("Tutorial5_1")) {
			gameObject = new Tutorial5_1() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial5_2")) {
			gameObject = new Tutorial5_2();
			fullscreen = true;

		} else if (action.equals("Tutorial6")) {
			gameObject = new Tutorial6() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial7_1")) {
			gameObject = new Tutorial7_1() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial7_2")) {
			gameObject = new Tutorial7_2() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial7_3")) {
			gameObject = new Tutorial7_3() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial8_1")) {
			gameObject = new Tutorial8_1() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial8_2")) {
			gameObject = new Tutorial8_2() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial8_3")) {
			gameObject = new Tutorial8_3() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial9_1")) {
			gameObject = new Tutorial9_1() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial9_2")) {
			gameObject = new Tutorial9_2() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial10")) {
			gameObject = new Tutorial10() {
				protected void notifyExit() { }
			};

		} else if (action.equals("Tutorial11")) {
			gameObject = new Tutorial11() {
				protected void notifyExit() { }
			};
		}


		// create the game!
		GameLoader game = new GameLoader();
		game.setup(gameObject, dimension, fullscreen);
		game.start();
	}


	public static void main(String[] args) {
		new Launcher();
	}

}