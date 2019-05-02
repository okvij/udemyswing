package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import controller.Controller;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private TextPanel textPanel;
	private Toolbar toolbar;
	private FormPanel formPanel;
	private JFileChooser fileChooser;
	private Controller controller;
	private TablePanel tablePanel;
	private PrefsDialog prefsDialog;
	private Preferences prefs;

	public MainFrame() {
		super("Hello World!");

		setLayout(new BorderLayout());

		toolbar = new Toolbar();
		textPanel = new TextPanel();
		formPanel = new FormPanel();
		tablePanel = new TablePanel();
		prefsDialog = new PrefsDialog(this);
		prefs = Preferences.userRoot().node("db");

		controller = new Controller();
		
		tablePanel.setData(controller.getPeople());
		tablePanel.setPersonTableListener(new PersonTableListener() {

			@Override
			public void rowDeleted(int row) {
				controller.removePerson(row);
			}
		});

		prefsDialog.setPrefsListener(new PrefsListener() {

			public void preferencesSet(String user, String password, int port) {
				prefs.put("user", user);
				prefs.put("password", password);
				prefs.putInt("port", port);
			}
			
		});
		
		String user = prefs.get("user","");
		String password = prefs.get("password", "");
		int port = prefs.getInt("port", 3306);
		prefsDialog.setDefaults(user, password, port);
		
		
		
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new PersonFileFilter());

		setJMenuBar(createMenuBar());

		toolbar.setToolbarListener(new ToolbarListener() {
		
			public void saveEventOccured() {
				connect(user, password);
				try {
					controller.save();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(MainFrame.this, "Unable to save to database",
							"Database Connection Problem",
							JOptionPane.ERROR_MESSAGE); 
				}

			}

			public void refreshEventOccured() {
				connect(user, password);
				try {
					controller.load();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(MainFrame.this, "Unable to load from database",
							"Database Connection Problem",
							JOptionPane.ERROR_MESSAGE); 
				}
				
				tablePanel.refresh();
			}
		});

		formPanel.setFormListener(new FormListener() {
			public void formEventOccured(FormEvent e) {
				controller.addPerson(e);
				tablePanel.refresh();
			}
		});
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Window closing...");

				
			}
			
		});


		add(formPanel, BorderLayout.WEST);
		add(toolbar, BorderLayout.NORTH);
		add(tablePanel, BorderLayout.CENTER);

		setMinimumSize(new Dimension(500, 400));
		setSize(600, 500);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}

	private void connect(String user, String password) {
		try {
			controller.connect(user, password);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MainFrame.this, "Cannot connect to database",
						"Database Connection Problem",
						JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu windowMenu = new JMenu("Window");
		JMenu showMenu = new JMenu("Show");
		
		JMenuItem exportDataItem = new JMenuItem("Export Data...");
		JMenuItem importDataItem = new JMenuItem("Import Data...");
		JMenuItem exitItem = new JMenuItem("Exit");

		fileMenu.add(exportDataItem);
		fileMenu.add(importDataItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		
		JCheckBoxMenuItem showFormItem = new JCheckBoxMenuItem("Person Form");
		JMenuItem prefsItem = new JMenuItem("Preferences...");
		showFormItem.setSelected(true);
		windowMenu.add(showMenu);
		windowMenu.add(prefsItem);
		showMenu.add(showFormItem);

		menuBar.add(fileMenu);
		menuBar.add(windowMenu);

		prefsItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				prefsDialog.setVisible(true);
			}
			
		});
		
		showFormItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();

				formPanel.setVisible(menuItem.isSelected());
			}

		});

		fileMenu.setMnemonic(KeyEvent.VK_F);
		exitItem.setMnemonic(KeyEvent.VK_X);
		
		prefsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK)); //accelerator
		
		importDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK)); //accelerator
		
		
		importDataItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						controller.loadFromFile(fileChooser.getSelectedFile());
						tablePanel.refresh();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainFrame.this, 
									"Could not load data from file.","Error", JOptionPane.ERROR_MESSAGE);	
					}

				}
			}
		});
		
		exportDataItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						controller.saveToFile(fileChooser.getSelectedFile());
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainFrame.this, 
									"Could not save data from file.",
									"Error", 
									JOptionPane.ERROR_MESSAGE);	
					}

				}
			}
		});
		
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int action = JOptionPane.showConfirmDialog(MainFrame.this, 
						"Do you really want to exit the application?",
						"Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
				
				if (action == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});

		return menuBar;

	}
}
