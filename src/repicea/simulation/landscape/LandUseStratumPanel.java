package repicea.simulation.landscape;

import java.awt.GridLayout;

import javax.swing.JTextField;

import repicea.gui.REpiceaPanel;
import repicea.gui.UIControlManager;
import repicea.gui.components.NumberFormatFieldFactory;
import repicea.gui.components.NumberFormatFieldFactory.JFormattedNumericField;
import repicea.gui.components.NumberFormatFieldFactory.NumberFieldDocument.NumberFieldEvent;
import repicea.gui.components.NumberFormatFieldFactory.NumberFieldListener;
import repicea.gui.components.NumberFormatFieldFactory.Range;
import repicea.gui.components.NumberFormatFieldFactory.Type;

@SuppressWarnings("serial")
final class LandUseStratumPanel extends REpiceaPanel implements NumberFieldListener {

	final LandUseStratum caller;
	final JTextField nbPlotsTextBox;
	final JTextField individualPlotAreaHaTextBox;
	final JFormattedNumericField stratumAreaHaTextBox;
	
	
	LandUseStratumPanel(LandUseStratum caller) {
		super();
		this.caller = caller;
		nbPlotsTextBox = new JTextField();
		nbPlotsTextBox.setEditable(false);
		individualPlotAreaHaTextBox = new JTextField();
		individualPlotAreaHaTextBox.setEditable(false);
		stratumAreaHaTextBox = NumberFormatFieldFactory.createNumberFormatField(Type.Double, Range.Positive, false);
		initUI();
		refreshInterface();
	}
	
	private void initUI() {
		setLayout(new GridLayout(1,4));
		add(UIControlManager.getLabel(caller.landUse.toString()));
		add(nbPlotsTextBox);
		add(individualPlotAreaHaTextBox);
		add(stratumAreaHaTextBox);
	}
	
	@Override
	public void refreshInterface() {
		nbPlotsTextBox.setText("" + caller.nbPlots);
		individualPlotAreaHaTextBox.setText("" + caller.individualPlotAreaHa);
		stratumAreaHaTextBox.setText("" + caller.stratumAreaHa);
	}

	@Override
	public void listenTo() {
		stratumAreaHaTextBox.addNumberFieldListener(this);
	}

	@Override
	public void doNotListenToAnymore() {
		stratumAreaHaTextBox.removeNumberFieldListener(this);
	}

	@Override
	public void numberChanged(NumberFieldEvent e) {
		if (e.getSource().equals(stratumAreaHaTextBox)) {
			caller.stratumAreaHa = stratumAreaHaTextBox.getValue().doubleValue();
		}
	}

}
