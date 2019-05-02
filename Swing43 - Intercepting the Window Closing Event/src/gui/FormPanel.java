package gui;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class FormPanel extends JPanel {

	private JLabel nameLabel;
	private JLabel occupationLabel;
	private JTextField nameField;
	private JTextField occuaptionField;
	private JButton okBtn;
	private FormListener formListener;
	private JList<AgeCategory> ageList;
	private JComboBox<String> empCombo;
	private JCheckBox citizenCheck;
	private JTextField taxField;
	private JLabel taxLabel;
	
	private JRadioButton maleRadio;
	private JRadioButton femaleRadio;
	private ButtonGroup genderGroup;

	public FormPanel() {
		Dimension dim = getPreferredSize();
		dim.width = 250;
		setPreferredSize(dim);

		nameLabel = new JLabel("Name: ");
		occupationLabel = new JLabel("Occupation: ");
		nameField = new JTextField(10);
		occuaptionField = new JTextField(10);
		ageList = new JList<>();
		empCombo = new JComboBox<>();
		citizenCheck = new JCheckBox();
		taxField = new JTextField(10);
		taxLabel = new JLabel("Tax ID: ");
		okBtn = new JButton("OK");
		
		maleRadio = new JRadioButton("Male");
		femaleRadio = new JRadioButton("Female");
		genderGroup = new ButtonGroup();
		
		
		//Set up mnemonics
		okBtn.setMnemonic(KeyEvent.VK_O);
		nameLabel.setDisplayedMnemonic(KeyEvent.VK_N);
		nameLabel.setLabelFor(nameField);
		
		//Set up gender radios
		maleRadio.setSelected(true);
		maleRadio.setActionCommand("male");
		femaleRadio.setActionCommand("female");
		genderGroup.add(maleRadio);
		genderGroup.add(femaleRadio);
		

		//Set up tax ID
		taxLabel.setEnabled(false);
		taxField.setEnabled(false);
		
		citizenCheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isTicked = citizenCheck.isSelected();
				
				if (!isTicked) {
					taxField.setText(null);
				}

				taxLabel.setEnabled(isTicked);
				taxField.setEnabled(isTicked);
			}
		});
		
		// Set up list box
		DefaultListModel<AgeCategory> ageModel = new DefaultListModel<>();
		ageModel.addElement(new AgeCategory(0, "Under 18"));
		ageModel.addElement(new AgeCategory(1, "18 - 65"));
		ageModel.addElement(new AgeCategory(2, "65+"));

		ageList.setModel(ageModel);
		ageList.setPreferredSize(new Dimension(110, 70));
		ageList.setBorder(BorderFactory.createEtchedBorder());
		ageList.setSelectedIndex(1);

		// Set up combo box
		DefaultComboBoxModel<String> empModel = new DefaultComboBoxModel<>();
		empModel.addElement("Employed");
		empModel.addElement("Self-employed");
		empModel.addElement("Unemployed");
		empCombo.setModel(empModel);
		empCombo.setSelectedIndex(0);
		empCombo.setEditable(true);

		
		
		
		

		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				String occupation = occuaptionField.getText();
				AgeCategory ageCat = (AgeCategory) ageList.getSelectedValue();
				String empCat = (String)empCombo.getSelectedItem();
				String taxID = (String)taxField.getText();
				boolean usCitizen = citizenCheck.isSelected();
				String genderCommand = genderGroup.getSelection().getActionCommand();
				
				FormEvent ev = new FormEvent(this, name, occupation, ageCat.getId(), empCat,
						taxID, usCitizen, genderCommand);

				if (formListener != null) {
					formListener.formEventOccured(ev);
				}
			}

		});

		Border innerBorder = BorderFactory.createTitledBorder("Add person");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		layoutComponents();

	}

	public void layoutComponents() {

		setLayout(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();

		/////////////////////// First Row /////////////////////////////////
		gc.gridy = 0;

		gc.gridx = 0;
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(nameLabel, gc);

		gc.gridy = 0;
		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.LINE_START;
		add(nameField, gc);

		/////////////////////// Second Row /////////////////////////////////
		gc.gridy++;

		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		add(occupationLabel, gc);

		gc.gridy = 1;
		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.LINE_START;
		add(occuaptionField, gc);

		/////////////////////// Next Row /////////////////////////////////
		gc.gridy++;

		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Age: "), gc);
		
		gc.weightx = 1;
		gc.weighty = 0.2;
		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(ageList, gc);

		/////////////////////// Next Row /////////////////////////////////
		gc.gridy++;

		
		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Employment: "), gc);
	

		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(empCombo, gc);

		
		/////////////////////// Next Row /////////////////////////////////
		gc.gridy++;

		
		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("US Citizen: "), gc);
	

		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(citizenCheck, gc);

		
		/////////////////////// Next Row /////////////////////////////////
		gc.gridy++;

		
		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(taxLabel, gc);
	

		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(taxField, gc);
		
		
		/////////////////////// Next Row /////////////////////////////////
		gc.gridy++;

		gc.weighty = 0.05;
		
		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Gender: "), gc);
	
		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(maleRadio, gc);	
		
		
		/////////////////////// Next Row /////////////////////////////////
		gc.gridy++;

		gc.gridx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(femaleRadio, gc);
		
		
		/////////////////////// Next Row /////////////////////////////////
		gc.gridy++;

		gc.weightx = 1;
		gc.weighty = 2;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(okBtn, gc);
	}

	public void setFormListener(FormListener formListener) {
		this.formListener = formListener;
	}
}

class AgeCategory {
	private int id;
	private String text;

	public AgeCategory(int id, String text) {
		this.id = id;
		this.text = text;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return text;
	}
}
