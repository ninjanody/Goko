package org.goko.tools.serial.jssc.preferences.connection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.goko.common.preferences.GkFieldEditorPreferencesPage;
import org.goko.common.preferences.fieldeditor.preference.ComboFieldEditor;
import org.goko.core.connection.serial.SerialParameter;
import org.goko.tools.serial.jssc.service.JsscParameter;

/**
 * Serial connection preference page
 * @author PsyKo
 *
 */
public class SerialConnectionPreferencesPage extends GkFieldEditorPreferencesPage {
	
	public SerialConnectionPreferencesPage() {
		setDescription("Configure your connection settings");		
		setTitle("Serial");
		setPreferenceStore(SerialConnectionPreference.getInstance());
	}

	/** (inheritDoc)
	 * @see org.goko.common.preferences.GkPreferencesPage#createPreferencePage(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createPreferencePage(Composite parent) {
		ComboFieldEditor baudrateField = new ComboFieldEditor(parent, SWT.READ_ONLY);
		((GridData) baudrateField.getControl().getLayoutData()).widthHint = 60;
		baudrateField.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		baudrateField.setLabelWidthInChar(10);
		baudrateField.setPreferenceName(JsscParameter.BAUDRATE.toString());
		baudrateField.setLabel("Baudrate");
		initBaudrateChoices(baudrateField);
		
		ComboFieldEditor databitsField = new ComboFieldEditor(parent, SWT.READ_ONLY);
		((GridData) databitsField.getControl().getLayoutData()).widthHint = 60;
		databitsField.setPreferenceName(JsscParameter.DATABITS.toString());
		databitsField.setLabelWidthInChar(10);
		databitsField.setLabel("Data bits");
		initDataBitsChoices(databitsField);
	
		ComboFieldEditor parityField = new ComboFieldEditor(parent, SWT.READ_ONLY);
		((GridData) parityField.getControl().getLayoutData()).widthHint = 60;
		parityField.setPreferenceName(JsscParameter.PARITY.toString());
		parityField.setLabelWidthInChar(10);
		parityField.setLabel("Parity");
		parityField.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		initParityChoices(parityField);
		
		ComboFieldEditor stopbitField = new ComboFieldEditor(parent, SWT.READ_ONLY);
		((GridData) stopbitField.getControl().getLayoutData()).widthHint = 60;
		stopbitField.setPreferenceName(JsscParameter.STOPBITS.toString());
		stopbitField.setLabelWidthInChar(10);
		stopbitField.setLabel("Stop bits");
		initStopBitsChoices(stopbitField);
		
		
		ComboFieldEditor flowControlFieldEditor = new ComboFieldEditor(parent, SWT.READ_ONLY);
		((GridData) flowControlFieldEditor.getControl().getLayoutData()).widthHint = 60;
		flowControlFieldEditor.setLabel("Flow control");
		flowControlFieldEditor.setLabelWidthInChar(10);
		flowControlFieldEditor.setPreferenceName(JsscParameter.FLOWCONTROL.toString());
		initFlowControlChoices(flowControlFieldEditor);
		
		addField(stopbitField);
		addField(parityField);
		addField(databitsField);
		addField(baudrateField);
		addField(flowControlFieldEditor);

	}
	
	private void initBaudrateChoices(ComboFieldEditor field){
		String[][] baudrateItems = new String[][]{
				{String.valueOf(SerialParameter.BAUDRATE_110)	,"5"},
			    {String.valueOf(SerialParameter.BAUDRATE_110) 	,"110"},
			    {String.valueOf(SerialParameter.BAUDRATE_300) 	,"300"},
			    {String.valueOf(SerialParameter.BAUDRATE_600) 	,"600"},
			    {String.valueOf(SerialParameter.BAUDRATE_1200) 	,"1200"},
			    {String.valueOf(SerialParameter.BAUDRATE_4800) 	,"4800"},
			    {String.valueOf(SerialParameter.BAUDRATE_9600) 	,"9600"},
			    {String.valueOf(SerialParameter.BAUDRATE_14400) 	,"14400"},
			    {String.valueOf(SerialParameter.BAUDRATE_19200) 	,"19200"},
			    {String.valueOf(SerialParameter.BAUDRATE_38400) 	,"38400"},
			    {String.valueOf(SerialParameter.BAUDRATE_57600) 	,"57600"},
			    {String.valueOf(SerialParameter.BAUDRATE_115200) ,"115200"},
			    {String.valueOf(SerialParameter.BAUDRATE_128000) ,"128000"},
			    {String.valueOf(SerialParameter.BAUDRATE_230400) ,"230400"},			    
			    {String.valueOf(SerialParameter.BAUDRATE_256000) ,"256000"}};
		field.setEntry(baudrateItems);
	}

	private void initFlowControlChoices(ComboFieldEditor field){
		String[][] baudrateItems = new String[][]{
				{"Off", String.valueOf(SerialParameter.FLOWCONTROL_NONE)},
				{"Xon/Xoff", String.valueOf(SerialParameter.FLOWCONTROL_XONXOFF)},
				{"RTS/CTS", String.valueOf(SerialParameter.FLOWCONTROL_RTSCTS)}};	   

		field.setEntry(baudrateItems);
	}
	
	private void initDataBitsChoices(ComboFieldEditor field){		
		String[][] databitsItems = new String[][]{
				{"5", String.valueOf(SerialParameter.DATABITS_5)},
				{"6", String.valueOf(SerialParameter.DATABITS_6)},
				{"7", String.valueOf(SerialParameter.DATABITS_7)},
				{"8", String.valueOf(SerialParameter.DATABITS_8)},
		};	
		field.setEntry(databitsItems);
	}

	private void initParityChoices(ComboFieldEditor field){
		String[][] parityItems = new String[][]{
				{"None", String.valueOf(SerialParameter.PARITY_NONE)},
				{"Even", String.valueOf(SerialParameter.PARITY_EVEN)},
				{"Odd" , String.valueOf(SerialParameter.PARITY_ODD)}};
		field.setEntry(parityItems);
	}

	private void initStopBitsChoices(ComboFieldEditor field){
		String[][] stopBitItems = new String[][]{		
				{"1",String.valueOf(SerialParameter.STOPBITS_1)},
				{"1.5",String.valueOf(SerialParameter.STOPBITS_1_5)},
				{"2",String.valueOf(SerialParameter.STOPBITS_2)}};
		field.setEntry(stopBitItems);
	}

}
