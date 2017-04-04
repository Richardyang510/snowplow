/**
 *Richard Yang
 *ICS4U - Mr A
 *Created 3:07:57 PM, Mar 23, 2016
 *Completed 7:50:20 PM, Mar 23, 2016
 *gui.java
 *Desc: A gui based implementation of the "Snow Plow" Problem
 *Notes: The arrays for calculations is given dimensions [W + 2][L + 2]
 *	 to make detecting bordering values easier. This way it is not
 *       required to do edge cases as there will always be values 
 *       surrounding the inner array
 *       
 *       A new thread is declared everytime the recursive function runs
 *       once as this way, thread.sleep() can be used. Otherwise, the 
 *       repaint() function will just stack on itself and pick the
 *       last call. Using thread.sleep() will also sleep the awt
 *       thread, thus it makes no difference.
 *       source:
 *       http://www.scs.ryerson.ca/mes/courses/cps530/programs/threads/Repaint/
 */

package plow;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class gui extends JFrame implements ActionListener {

	// ---------GLOBAL VARIABLES---------

	// adjustable values, width, length, size, speed
	static int W = 8, L = 8, res = 100, s = 100;

	// array to do calculations on, see notes for explaining size
	static int a[][];
	
	//the user's name
	String name = "Richard";

	// text fields for input settings
	JTextField nameTF;
	JTextField widthTF;
	JTextField lengthTF;
	JTextField resolutionTF;
	JTextField speedTF;

	// label array to display values (see draw method)
	JLabel jl[][];
	// label to display status
	JLabel stat = new JLabel("Idle");

	// panel to hold array of labels
	JPanel num = new JPanel();
	// panel to hold settings
	JPanel settings = new JPanel();
	// panel to hold user input
	JPanel input = new JPanel();
	// panel to start program and quit
	JPanel start = new JPanel();

	// frame to hold start screen
	JFrame welcome = new JFrame();

	// ---------GUI RELATED METHODS---------

	public gui() { // gui object

		// basic details of the welcome frame
		welcome.setTitle("Snow Plow - Welcome");
		welcome.setSize(800, 1000);
		welcome.setLayout(new BorderLayout());
		welcome.setVisible(true);

		// input labels
		JLabel nameL = new JLabel("Please enter your name (leave blank to set default)", SwingConstants.CENTER);
		JLabel widthL = new JLabel("Please enter number of rows", SwingConstants.CENTER);
		JLabel lengthL = new JLabel("Please enter number of coloumns", SwingConstants.CENTER);
		JLabel resolutionL = new JLabel("Please enter resolution of each number", SwingConstants.CENTER);
		JLabel speedL = new JLabel("Please enter delay in ms", SwingConstants.CENTER);

		// input fields
		nameTF = new JTextField(30);
		widthTF = new JTextField(3);
		lengthTF = new JTextField(3);
		resolutionTF = new JTextField(3);
		speedTF = new JTextField(3);

		// user input panel
		input.setLayout(new GridLayout(5, 2));
		input.setVisible(true);
		input.add(nameL);
		input.add(nameTF);
		input.add(widthL);
		input.add(widthTF);
		input.add(lengthL);
		input.add(lengthTF);
		input.add(resolutionL);
		input.add(resolutionTF);
		input.add(speedL);
		input.add(speedTF);
		welcome.add(input, BorderLayout.CENTER);

		//add button to continue to next frame
		JButton doStart = new JButton("Start");
		doStart.addActionListener(this);
		start.setLayout(new GridLayout(1, 1));
		start.setVisible(true);
		start.add(doStart);
		welcome.add(start, BorderLayout.SOUTH);

		welcome.validate();
		welcome.repaint();

	}

	public void startProgram() {

		//destroy welcome frame
		welcome.dispose();
		
		//create arrays
		a = new int[W + 2][L + 2];
		jl = new JLabel[W][L];
		
		// create values for the array
		init(a);

		// basic details of the frame
		setTitle("Snow Plow");
		setSize(L * res, (W + 1) * res);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminate on close
		setVisible(true);

		// 2 buttons in setting, start algorithm and get new data set
		JButton begin = new JButton("Begin");
		JButton del = new JButton("Redo");
		
		// label for users name
		JLabel nameLabel = new JLabel("Hello "+name);

		// settings panel
		settings.setVisible(true);
		// settings in flow layout for ease of use
		settings.setLayout(new FlowLayout());
		settings.add(nameLabel);
		// add action listeners to buttons then add to panel
		begin.addActionListener(this);
		settings.add(begin);
		del.addActionListener(this);
		settings.add(del);
		// add label to panel
		settings.add(stat);
		// add panel to frame (top and small)
		add(settings, BorderLayout.NORTH);

		// array panel
		num.setVisible(true);
		// set to grid layout center for the array to be displayed biggest
		num.setLayout(new GridLayout(W, L));
		add(num, BorderLayout.CENTER);

		// draw the array into the panel
		draw(a);

		validate();
		repaint();
	}

	public void actionPerformed(ActionEvent event) { // action listener
		// get the button's value
		String command = event.getActionCommand();

		// all options for the begin button
		if (command.equals("Begin")) {

			// check if the plow is used
			if (first(a) == -1) {
				whatStatus("Plow is not used!");
			} else {

				// new thread to show step by step, see notes for explanation
				new Thread() {
					public void run() {
						// change status to running
						whatStatus("Running...");
						// run the recursive function on first row, first "1"
						clear(1, first(a));
						// change status to complete
						whatStatus("Complete");
					}
				}.start();
			}
		} else if (command.equals("Redo")) { // all options for the redo button

			// create new array
			init(a);
			// draw the new array
			draw(a);
			// change status back to idle
			whatStatus("Idle");
		} else if (command.equals("Start")) { //get info from form
			if (nameTF.getText().equals("")){
				startProgram();
			}
			name = nameTF.getText();
			W = Integer.parseInt(widthTF.getText());
			L = Integer.parseInt(lengthTF.getText());
			res = Integer.parseInt(resolutionTF.getText());
			s = Integer.parseInt(speedTF.getText());
			startProgram();
		}

	}

	public void clear(int r, int c) { // recursive function to solve

		// sleep the thread to visualize
		try {
			if (s != 0) {
				Thread.sleep(s);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// paint the frame
		validate();
		repaint();

		// set the value of the current pos to plowed
		a[r][c] = 0;

		// draw and display the new array
		draw(a);

		// check the 8 pos around the initial pos and check for unplowed
		for (int i = r - 1; i <= r + 1; i++) {
			for (int j = c - 1; j <= c + 1; j++) {
				if (a[i][j] == 1) {
					// recursively call itself on the unplowed pos
					clear(i, j);
				}
			}
		}
	}

	public void draw(int a[][]) { // function to draw new array

		// get rid of the old array
		num.removeAll();

		// change the numerical values into color coded labels
		for (int i = 1; i <= W; i++) {
			for (int j = 1; j <= L; j++) {
				if (a[i][j] == 1) { // unplowed - 1, normal, blue
					jl[i - 1][j - 1] = new JLabel("1", SwingConstants.CENTER);
					jl[i - 1][j - 1].setFont(new Font("Arial", Font.PLAIN, res / 2));
					jl[i - 1][j - 1].setForeground(Color.blue);
					num.add(jl[i - 1][j - 1]);
				} else if (a[i][j] == 2) { // city - 2, bold, gray
					jl[i - 1][j - 1] = new JLabel("2", SwingConstants.CENTER);
					jl[i - 1][j - 1].setFont(new Font("Arial", Font.BOLD, res / 2));
					jl[i - 1][j - 1].setForeground(Color.GRAY);
					num.add(jl[i - 1][j - 1]);
				} else if (a[i][j] == 0) { // plowed - 0, italic, cyan
					jl[i - 1][j - 1] = new JLabel("0", SwingConstants.CENTER);
					jl[i - 1][j - 1].setFont(new Font("Arial", Font.ITALIC, res / 2));
					jl[i - 1][j - 1].setForeground(Color.cyan);
					num.add(jl[i - 1][j - 1]);
				}
			}
		}

		// paint the array panel again
		num.validate();
		num.repaint();
	}

	public void whatStatus(String s) { // function to quickly change status

		settings.remove(stat);
		stat = new JLabel(s);
		settings.add(stat);
		settings.validate();
		settings.repaint();
	}

	// ---------INITALLY USED METHODS---------

	public static void init(int a[][]) { // method to initialize array
		for (int i = 1; i <= W; i++) {
			for (int j = 1; j <= L; j++) {
				// random number from range 1 to 2
				a[i][j] = (int) (Math.random() * 2) + 1;
			}
		}
	}

	public static int first(int a[][]) { // method to find first "1" in row 1
		for (int i = 1; i <= L; i++) {
			if (a[1][i] == 1) {
				return i;
			}
		}
		// returns -1 if no "1" are found
		return -1;
	}

	// ---------MAIN METHOD---------

	public static void main(String[] args) {

		// create the main frame
		gui Frame = new gui();

	}

}
