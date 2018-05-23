package gui_orientation.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import orientation.LogAbstract;

/**
 * This class extends the JToolbar of Java to create a status bar including some
 * of the following component ProgressBar, Help Button About Button and Close
 * Button
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 * 
 */
public class WalkBar extends JToolBar implements ActionListener, LogAbstract {

	private JProgressBar		progress	= new JProgressBar();
	private JButton			bnHelp		= new JButton("Help");
	private JButton			bnAbout		= new JButton("About");
	private JButton			bnClose		= new JButton("Close");
	private String			about[]		= { "About", "Version", "Description", "Author", "Biomedical Image Group", "2008", "http://bigwww.epfl.ch" };
	private String			help;
	private double			chrono;
	private int				xSizeAbout	= 400;
	private int				ySizeAbout	= 400;
	private int				xSizeHelp	= 400;
	private int				ySizeHelp	= 400;

	/**
	 * Class SetValue in the swing thread.
	 */
	private static class SetValue implements Runnable {
		private int				value;
		private JProgressBar	progress;

		public SetValue(JProgressBar progress, int value) {
			this.progress = progress;
			this.value = value;
		}

		public void run() {
			progress.setValue(value);
		}
	}

	/**
	 * Class IncValue in the swing thread.
	 */
	private static class IncValue implements Runnable {
		private double			inc;
		private JProgressBar	progress;

		public IncValue(JProgressBar progress, double inc) {
			this.progress = progress;
			this.inc = inc;
		}

		public void run() {
			progress.setValue((int) Math.round(progress.getValue() + inc));
		}
	}

	/**
	 * Class SetMessage in the swing thread.
	 */
	private static class SetMessage implements Runnable {
		private String			msg;
		private JProgressBar	progress;

		public SetMessage(JProgressBar progress, String msg) {
			this.progress = progress;
			this.msg = msg;
		}

		public void run() {
			progress.setString(msg);
		}
	}

	/**
	 * Constructor.
	 */
	public WalkBar() {
		super("Walk Bar");
		build("", false, false, false, 100);
	}

	public WalkBar(String initialMessage, boolean isAbout, boolean isHelp, boolean isClose) {
		super("Walk Bar");
		build(initialMessage, isAbout, isHelp, isClose, 100);
	}

	public WalkBar(String initialMessage, boolean isAbout, boolean isHelp, boolean isClose, int size) {
		super("Walk Bar");
		build(initialMessage, isAbout, isHelp, isClose, size);

	}

	private void build(String initialMessage, boolean isAbout, boolean isHelp, boolean isClose, int size) {
		if (isAbout)
			add(bnAbout);
		if (isHelp)
			add(bnHelp);
		addSeparator();
		add(progress);
		addSeparator();
		if (isClose)
			add(bnClose);

		progress.setStringPainted(true);
		progress.setString(initialMessage);
		// progress.setFont(new Font("Arial", Font.PLAIN, 20));
		progress.setMinimum(0);
		progress.setMaximum(100);
		progress.setPreferredSize(new Dimension(size, 20));
		bnAbout.addActionListener(this);
		bnHelp.addActionListener(this);

		setFloatable(false);
		setRollover(true);
		setBorderPainted(false);
		chrono = System.currentTimeMillis();
	}

	/**
	 * Implements the actionPerformed for the ActionListener.
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnHelp) {
			showHelp();
		}
		else if (e.getSource() == bnAbout) {
			showAbout();
		}
		else if (e.getSource() == bnClose) {
		}
	}

	/**
	 * Return a reference to the Close button.
	 */
	public JButton getButtonClose() {
		return bnClose;
	}

	/**
	 * Set a value and a message in the progress bar.
	 */
	public void progress(String msg, int value) {
		double elapsedTime = System.currentTimeMillis() - chrono;
		String t = " [" + (elapsedTime > 3000 ? Math.round(elapsedTime / 10) / 100.0 + "s." : elapsedTime + "ms") + "]";
		SwingUtilities.invokeLater(new SetValue(progress, value));
		SwingUtilities.invokeLater(new SetMessage(progress, msg + t));
	}

	/**
	 * Set a value and a message in the progress bar.
	 */
	public void increment(double inc) {
		SwingUtilities.invokeLater(new IncValue(progress, inc));
	}

	/**
	 * Set a value in the progress bar.
	 */
	public void setValue(int value) {
		SwingUtilities.invokeLater(new SetValue(progress, value));
	}

	/**
	 * Set a message in the progress bar.
	 */
	public void setMessage(String msg) {
		SwingUtilities.invokeLater(new SetMessage(progress, msg));
	}

	/**
	 * Set a value and a message in the progress bar.
	 */
	public void progress(String msg, double value) {
		progress(msg, (int) Math.round(value));
	}

	/**
	 * Set to 0 the progress bar.
	 */
	public void reset() {
		chrono = System.currentTimeMillis();
		progress("Start", 0);
	}

	/**
	 * Set to 100 the progress bar.
	 */
	public void finish() {
		progress("End", 100);
	}

	/**
	 * Set to 100 the progress bar with an additional message.
	 */
	public void finish(String msg) {
		progress(msg, 100);
	}

	/**
	 * Specify the content of the About window.
	 */
	public void fillAbout(String name, String version, String description, String author, String organisation, String date, String info) {
		this.about[0] = name;
		this.about[1] = version;
		this.about[2] = description;
		this.about[3] = author;
		this.about[4] = organisation;
		this.about[5] = date;
		this.about[6] = info;
	}

	/**
	 * Specify the content of the Help window.
	 */
	public void fillHelp(String help) {
		this.help = help;
	}

	/**
	 * Show the content of the About window.
	 */
	public void showAbout() {

		final JFrame frame = new JFrame("About " + about[0]);
		JEditorPane pane = new JEditorPane();
		pane.setEditable(false);
		pane.setContentType("text/html; charset=ISO-8859-1");
		pane.setText("<html><head><title>" + about[0] + "</title>" + getStyle() + "</head><body>" + (about[0] == "" ? "" : "<p class=\"name\">" + about[0] + "</p>")
				+ // Name
				(about[1] == "" ? "" : "<p class=\"vers\">" + about[1] + "</p>")
				+ // Version
				(about[2] == "" ? "" : "<p class=\"desc\">" + about[2] + "</p><hr>")
				+ // Description
				(about[3] == "" ? "" : "<p class=\"auth\">" + about[3] + "</p>")
				+ // author
				(about[4] == "" ? "" : "<p class=\"orga\">" + about[4] + "</p>") + (about[5] == "" ? "" : "<p class=\"date\">" + about[5] + "</p>")
				+ (about[6] == "" ? "" : "<p class=\"more\">" + about[6] + "</p>") + "</html>");

		final JButton bnClose = new JButton("Close");
		bnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		pane.setCaret(new DefaultCaret());
		JScrollPane scrollPane = new JScrollPane(pane);
		// helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(xSizeAbout, ySizeAbout));
		frame.getContentPane().add(scrollPane, BorderLayout.NORTH);
		frame.getContentPane().add(bnClose, BorderLayout.CENTER);

		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		center(frame);
	}

	/**
	 * Show the content of the Help window of a given size.
	 */
	public void showHelp() {
		final JFrame frame = new JFrame("Help " + about[0]);
		JEditorPane pane = new JEditorPane();
		pane.setEditable(false);
		pane.setContentType("text/html; charset=ISO-8859-1");
		pane.setText("<html><head><title>" + about[0] + "</title>" + getStyle() + "</head><body>" + (about[0] == "" ? "" : "<p class=\"name\">" + about[0] + "</p>") + // Name
				(about[1] == "" ? "" : "<p class=\"vers\">" + about[1] + "</p>") + // Version
				(about[2] == "" ? "" : "<p class=\"desc\">" + about[2] + "</p>") + // Description
				"<hr><p class=\"help\">" + help + "</p>" + "</html>");
		final JButton bnClose = new JButton("Close");
		bnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		pane.setCaret(new DefaultCaret());
		JScrollPane scrollPane = new JScrollPane(pane);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(xSizeHelp, ySizeHelp));
		frame.setPreferredSize(new Dimension(xSizeHelp, ySizeHelp));
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(bnClose, BorderLayout.SOUTH);
		frame.setVisible(true);
		frame.pack();
		center(frame);
	}

	/*
	 * Place the window in the center of the screen.
	 */
	private void center(Window w) {
		Dimension screenSize = new Dimension(0, 0);
		boolean isWin = System.getProperty("os.name").startsWith("Windows");
		if (isWin) { // GraphicsEnvironment.getConfigurations is *very* slow on
						// Windows
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		}
		if (GraphicsEnvironment.isHeadless())
			screenSize = new Dimension(0, 0);
		else {
			// Can't use Toolkit.getScreenSize() on Linux because it returns
			// size of all displays rather than just the primary display.
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] gd = ge.getScreenDevices();
			GraphicsConfiguration[] gc = gd[0].getConfigurations();
			Rectangle bounds = gc[0].getBounds();
			if (bounds.x == 0 && bounds.y == 0)
				screenSize = new Dimension(bounds.width, bounds.height);
			else
				screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		}
		Dimension window = w.getSize();
		if (window.width == 0)
			return;
		int left = screenSize.width / 2 - window.width / 2;
		int top = (screenSize.height - window.height) / 4;
		if (top < 0)
			top = 0;
		w.setLocation(left, top);
	}

	/*
	 * Defines the CSS style for the help and about window.
	 */
	private String getStyle() {
		return "<style type=text/css>" + "body {backgroud-color:#222277}" + "hr {width:80% color:#333366; padding-top:7px }"
				+ "p, li {margin-left:10px;margin-right:10px; color:#000000; font-size:1em; font-family:Verdana,Helvetica,Arial,Geneva,Swiss,SunSans-Regular,sans-serif}"
				+ "p.name {color:#ffffff; font-size:1.2em; font-weight: bold; background-color: #333366; text-align:center;}" + "p.vers {color:#333333; text-align:center;}"
				+ "p.desc {color:#333333; font-weight: bold; text-align:center;}" + "p.auth {color:#333333; font-style: italic; text-align:center;}"
				+ "p.orga {color:#333333; text-align:center;}" + "p.date {color:#333333; text-align:center;}" + "p.more {color:#333333; text-align:center;}"
				+ "p.help {color:#000000; text-align:left;}" + "</style>";
	}
}
