/**
 *Richard Yang
 *ICS4U - Mr A
 *Created 3:07:57 PM, Mar 23, 2016
 *Completed 7:50:20 PM, Mar 23, 2016
 *gui.java
 *Desc: A gui based implementation of the "Snow Plow" Problem
 *Notes: A new thread is declared everytime the recursive function runs
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

    // the user's name
    String name = "";

    // boolean to control bg color of labels
    boolean haveBG = true;

    // text fields for input settings
    JButton opaquetB;
    JButton opaquefB;
    JTextField nameTF;
    JTextField widthTF;
    JTextField lengthTF;
    JTextField resolutionTF;
    JTextField speedTF;

    // label array to display values (see draw method)
    JLabel jl[][];
    // label to display status
    JLabel stat = new JLabel("Status", SwingConstants.CENTER);

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

    // color and font scheme
    String font = "Segoe UI";
    Color numColor = new Color(255, 255, 255); // white
    Color settingColor = new Color(59, 58, 54); // d grey
    Color tfColor = new Color(39, 38, 34); // dd grey
    Color textColor = new Color(227, 227, 227); // light grey
    Color plowColor = new Color(144, 104, 190); // purp
    Color unplowColor = new Color(110, 211, 207); // cyan
    Color cityColor = new Color(230, 39, 57); // red

    // ---------GUI RELATED METHODS---------

    public gui() { // gui object
        startProgram();
    }

    public void startWelcome() {
        // basic details of the welcome frame
        welcome.setTitle("Snow Plow - Welcome");
        welcome.setSize(800, 1000);
        welcome.setLayout(new BorderLayout());
        welcome.setVisible(true);
        welcome.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // input labels
        JLabel nameL = customLabel("Please enter your name", new Font(font, Font.PLAIN, 20), settingColor, textColor,
                false);
        JLabel widthL = customLabel("Please enter number of rows", new Font(font, Font.PLAIN, 20), settingColor,
                textColor, false);
        JLabel lengthL = customLabel("Please enter number of columns", new Font(font, Font.PLAIN, 20), settingColor,
                textColor, false);
        JLabel resolutionL = customLabel("Please enter resolution", new Font(font, Font.PLAIN, 20), settingColor,
                textColor, false);
        JLabel speedL = customLabel("Please enter delay in ms", new Font(font, Font.PLAIN, 20), settingColor, textColor,
                false);

        FocusListener highlighter = new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                e.getComponent().setBackground(settingColor);
                e.getComponent().setForeground(textColor);
            }

            @Override
            public void focusLost(FocusEvent e) {
                e.getComponent().setBackground(tfColor);
                e.getComponent().setForeground(textColor);
            }
        };

        // input fields
        opaquetB = customButton("Yes Background", new Font(font, Font.PLAIN, 20), settingColor, textColor);
        opaquefB = customButton("No Background", new Font(font, Font.PLAIN, 20), settingColor, textColor);
        nameTF = customTF(30, new Font(font, Font.PLAIN, 20), tfColor, textColor, highlighter);
        widthTF = customTF(3, new Font(font, Font.PLAIN, 20), tfColor, textColor, highlighter);
        lengthTF = customTF(3, new Font(font, Font.PLAIN, 20), tfColor, textColor, highlighter);
        resolutionTF = customTF(3, new Font(font, Font.PLAIN, 20), tfColor, textColor, highlighter);
        speedTF = customTF(3, new Font(font, Font.PLAIN, 20), tfColor, textColor, highlighter);

        // user input panel
        input.removeAll();
        input.setLayout(new GridLayout(6, 2));
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
        input.add(opaquetB);
        input.add(opaquefB);
        welcome.add(input, BorderLayout.CENTER);

        // add button to continue to next frame
        start.removeAll();
        JButton doStart = customButton("Start", new Font(font, Font.PLAIN, 20), settingColor, textColor);
        start.setLayout(new GridLayout(1, 1));
        start.setVisible(true);
        start.add(doStart);
        welcome.add(start, BorderLayout.SOUTH);

        welcome.validate();
        welcome.repaint();
    }

    public void startProgram() {

        // destroy welcome frame
        welcome.dispose();

        // create arrays
        a = new int[W][L];
        jl = new JLabel[W][L];

        // create values for the array
        init(a);

        // basic details of the frame
        setTitle("Snow Plow");
        setSize(L * res, (W + 1) * res);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminate on close
        setVisible(true);

        // 3 buttons in setting, start algorithm, get new data set, change size
        JButton begin = new JButton("Begin");
        JButton del = new JButton("New");
        JButton custom = new JButton("Advanced");

        begin.setBackground(settingColor);
        begin.setForeground(textColor);
        begin.setFont(new Font(font, Font.PLAIN, res / 5));
        del.setBackground(settingColor);
        del.setForeground(textColor);
        del.setFont(new Font(font, Font.PLAIN, res / 5));
        custom.setBackground(settingColor);
        custom.setForeground(textColor);
        custom.setFont(new Font(font, Font.PLAIN, res / 5));

        // label for users name
        JLabel nameLabel = new JLabel("Hello " + name, SwingConstants.CENTER);
        nameLabel.setForeground(textColor);
        nameLabel.setFont(new Font(font, Font.PLAIN, res / 5));

        // settings panel
        settings.setVisible(true);
        // reset settings
        settings.removeAll();
        // settings in grid layout for organization
        settings.setLayout(new GridLayout(2, 3));
        // add action listeners to buttons then add to panel
        begin.addActionListener(this);
        settings.add(begin);
        del.addActionListener(this);
        settings.add(del);
        custom.addActionListener(this);
        settings.add(custom);
        settings.add(nameLabel);
        // status color
        stat.setForeground(textColor);
        stat.setFont(new Font(font, Font.PLAIN, res / 5));
        // add label to panel
        settings.add(stat);
        settings.setBackground(settingColor);
        // add panel to frame (top and small)
        add(settings, BorderLayout.NORTH);

        // array panel
        num.setVisible(true);
        // set to grid layout center for the array to be displayed biggest
        num.setLayout(new GridLayout(W, L));
        num.setBackground(numColor);
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
                        clear(0, first(a));
                        // change status to complete
                        whatStatus("Complete");
                    }
                }.start();
            }
        } else if (command.equals("New")) { // all options for the redo button

            // create new array
            init(a);
            // draw the new array
            draw(a);
            // change status back to idle
            whatStatus("Idle");
        } else if (command.equals("Start")) { // get info from form
            if (!(nameTF.getText().equals(""))) {
                name = nameTF.getText();
            }
            if(!(widthTF.getText().equals(""))){
                W = Integer.parseInt(widthTF.getText());
            }
            if(!(lengthTF.getText().equals(""))){
                L = Integer.parseInt(lengthTF.getText());
            }
            if(!(resolutionTF.getText().equals(""))){
                res = Integer.parseInt(resolutionTF.getText());
            }
            if(!(speedTF.getText().equals(""))){
                s = Integer.parseInt(speedTF.getText());
            }
            startProgram();

        } else if (command.equals("Advanced")) {
            dispose();
            startWelcome();
        } else if (command.equals("No Background")) {
            haveBG = false;
        } else if (command.equals("Yes Background")) {
            haveBG = true;
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
                if (i >= 0 && j >= 0 && i < W && j < L && a[i][j] == 1) {
                    // recursively call itself on the unplowed pos
                    clear(i, j);
                }
            }
        }
    }

    public JTextField customTF(int size, Font f, Color bg, Color fg, FocusListener fl) {
        JTextField TF = new JTextField(size);
        TF.setFont(f);
        TF.setBackground(bg);
        TF.setForeground(fg);
        TF.addFocusListener(fl);
        return TF;
    }

    public JLabel customLabel(String s, Font f, Color bg, Color fg, Boolean isGrid) {
        JLabel JL = new JLabel(s, SwingConstants.CENTER);
        JL.setFont(f);
        if (!isGrid) {
            JL.setBackground(bg);
            JL.setForeground(fg);
            JL.setOpaque(true);
        } else {
            if (haveBG) {
                JL.setBackground(bg);
                JL.setForeground(fg);
                JL.setOpaque(true);
            } else {
                JL.setBackground(fg);
                JL.setForeground(bg);
                JL.setOpaque(false);
            }
        }
        return JL;
    }

    public JButton customButton(String s, Font f, Color bg, Color fg) {
        JButton JB = new JButton(s);
        JB.setForeground(fg);
        JB.setBackground(bg);
        JB.setFont(f);
        JB.addActionListener(this);
        return JB;
    }

    public void draw(int a[][]) { // function to draw new array

        // get rid of the old array
        num.removeAll();

        // change the numerical values into color coded labels
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < L; j++) {
                if (a[i][j] == 1) { // unplowed - 1
                    jl[i][j] = customLabel("1", new Font(font, Font.PLAIN, res / 2), unplowColor, textColor,
                            true);
                    num.add(jl[i][j]);
                } else if (a[i][j] == 2) { // city - 2
                    jl[i][j] = customLabel("2", new Font(font, Font.BOLD, res / 2), cityColor, textColor, true);
                    num.add(jl[i][j]);
                } else if (a[i][j] == 0) { // plowed - 0
                    jl[i][j] = customLabel("0", new Font(font, Font.ITALIC, res / 2), plowColor, textColor, true);
                    num.add(jl[i][j]);
                }
            }
        }

        // paint the array panel again
        num.validate();
        num.repaint();
    }

    public void whatStatus(String s) { // function to quickly change status

        settings.remove(stat);
        stat = new JLabel(s, SwingConstants.CENTER);
        stat.setForeground(textColor);
        stat.setFont(new Font(font, Font.PLAIN, res / 5));
        settings.add(stat);
        settings.validate();
        settings.repaint();
    }

    // ---------INITALLY USED METHODS---------

    public static void init(int a[][]) { // method to initialize array
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < L; j++) {
                // random number from range 1 to 2
                a[i][j] = (int) (Math.random() * 2) + 1;
            }
        }
    }

    public static int first(int a[][]) { // method to find first "1" in row 1
        for (int i = 0; i < L; i++) {
            if (a[0][i] == 1) {
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
