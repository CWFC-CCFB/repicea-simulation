package repicea.simulation.landscape;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import repicea.gui.OwnedWindow;
import repicea.gui.REpiceaControlPanel;
import repicea.gui.REpiceaDialog;
import repicea.gui.REpiceaMemorizerHandler;
import repicea.gui.UIControlManager;
import repicea.serial.Memorizable;
import repicea.simulation.landscape.LandUseStrataManager.LandUseStratumException;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

@SuppressWarnings("serial")
final class LandUseStrataManagerDialog extends REpiceaDialog implements OwnedWindow {

	private enum MessageID implements TextableEnum {
		LandUseLabel("Land use", "Affectation"),
		NbPlotsLabel("Nb plots", "Nb placettes"),
		IndividualPlotAreaHaLabel("Individual plot area (ha)", "Surface des placettes individuelles (ha)"),
		StratumAreaHaLabel("Stratum area (ha)", "Surface de la strate (ha)"),
		TitleLabel("Land Use Strata Manager", "Gestionnaire d'affectation");

		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
		
		@Override
		public String toString() {return REpiceaTranslator.getString(this);}
		
	}
	
	final List<LandUseStratumPanel> strataPanel;
	final REpiceaControlPanel controlPanel;
	final LandUseStrataManager caller;
	JPanel mainPanel;

	LandUseStrataManagerDialog(LandUseStrataManager caller, Window parent) {
		super(parent);
		this.caller = caller;
		strataPanel = new ArrayList<LandUseStratumPanel>();
		controlPanel = new REpiceaControlPanel(this);
		new REpiceaMemorizerHandler(this);
		initUI();
		pack();
	}
	
	@Override
	public void listenTo() {}

	@Override
	public void doNotListenToAnymore() {}

	@Override
	public void cancelAction() {
		caller.isCancelled = true;
		super.cancelAction();
	}

	@Override
	public void okAction() {
		try {
			caller.getEstimatorType();
		} catch (LandUseStratumException e) {
			JOptionPane.showMessageDialog(this, 
					e.getMessage(), 
					UIControlManager.InformationMessageTitle.Error.toString(), 
					JOptionPane.ERROR_MESSAGE);
			return;
		} 
		super.okAction();
	}
	
	@Override
	protected void initUI() {
		setTitle(MessageID.TitleLabel.toString());
		getContentPane().setLayout(new BorderLayout());
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//		mainPanel.add(Box.createVerticalStrut(10));
		JPanel headerPanel = new JPanel(new GridLayout(1,4));
		headerPanel.add(UIControlManager.getLabel(MessageID.LandUseLabel.toString()));
		headerPanel.add(UIControlManager.getLabel(MessageID.NbPlotsLabel.toString()));
		headerPanel.add(UIControlManager.getLabel(MessageID.IndividualPlotAreaHaLabel.toString()));
		headerPanel.add(UIControlManager.getLabel(MessageID.StratumAreaHaLabel.toString()));
		mainPanel.add(headerPanel);
		synchronizeUIWithOwner();
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.WEST);
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.EAST);
		getContentPane().add(Box.createVerticalStrut(10), BorderLayout.NORTH);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
	}

	@Override
	public void synchronizeUIWithOwner() {
		for (LandUseStratumPanel panel : strataPanel) {
			mainPanel.remove(panel);
		}
		strataPanel.clear();
		for (LandUseStratum lus : caller.landUseStrata.values()) {
			LandUseStratumPanel stratumPanel = lus.getUI();
			mainPanel.add(stratumPanel);
			strataPanel.add(stratumPanel);
		}
	}

	@Override
	public Memorizable getWindowOwner() {
		return caller;
	}

}
