/**
 * 
 */
package org.goko.viewer.jogl.preferences;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.goko.common.preferences.GkFieldEditorPreferencesPage;
import org.goko.common.preferences.fieldeditor.preference.BooleanFieldEditor;
import org.goko.common.preferences.fieldeditor.preference.ColorFieldEditor;
import org.goko.common.preferences.fieldeditor.preference.IntegerFieldEditor;
import org.goko.common.preferences.fieldeditor.preference.QuantityFieldEditor;
import org.goko.core.common.exception.GkException;
import org.goko.core.common.measure.quantity.Length;
import org.goko.core.common.measure.units.Unit;
import org.goko.core.config.GokoPreference;
import org.goko.core.log.GkLog;

/**
 * @author PsyKo
 *
 */
public class GridPreferencesPage extends GkFieldEditorPreferencesPage {
	private GkLog LOG = GkLog.getLogger(GridPreferencesPage.class);	
	private QuantityFieldEditor<Length> majorSpacingFieldEditor;
	private QuantityFieldEditor<Length> minorSpacingFieldEditor;
	private QuantityFieldEditor<Length> startXFieldEditor;
	private QuantityFieldEditor<Length> startYFieldEditor;
	private QuantityFieldEditor<Length> endXFieldEditor;
	private QuantityFieldEditor<Length> endYFieldEditor;
	
	public GridPreferencesPage() {
		setTitle("Grid");
		setPreferenceStore(JoglViewerPreference.getInstance());
	}

	/** (inheritDoc)
	 * @see org.goko.common.preferences.GkFieldEditorPreferencesPage#createPreferencePage(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createPreferencePage(Composite parent) throws GkException {
		
		Group grpSettings = new Group(parent, SWT.NONE);
		grpSettings.setText("Settings");
		grpSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpSettings.setLayout(new GridLayout(1, false));
		
		majorSpacingFieldEditor = new QuantityFieldEditor<Length>(grpSettings, SWT.NONE);
		majorSpacingFieldEditor.setLabelWidthInChar(15);
		majorSpacingFieldEditor.setPreferenceName("grid.majorSpacing");
		majorSpacingFieldEditor.setWidthInChars(5);
		majorSpacingFieldEditor.setLabel("Major grid spacing");		
		majorSpacingFieldEditor.setEmptyStringAllowed(false);
		majorSpacingFieldEditor.setPreferenceName(JoglViewerPreference.MAJOR_GRID_SPACING);
		
		minorSpacingFieldEditor = new QuantityFieldEditor<Length>(grpSettings, SWT.NONE);
		minorSpacingFieldEditor.setLabelWidthInChar(15);
		minorSpacingFieldEditor.setEmptyStringAllowed(false);
		minorSpacingFieldEditor.setPreferenceName("grid.minorSpacing");
		minorSpacingFieldEditor.setWidthInChars(5);
		minorSpacingFieldEditor.setLabel("Minor grid spacing");
		minorSpacingFieldEditor.setPreferenceName(JoglViewerPreference.MINOR_GRID_SPACING);
		
		ColorFieldEditor majorColorFieldEditor = new ColorFieldEditor(grpSettings, SWT.NONE);
		majorColorFieldEditor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));		
		majorColorFieldEditor.setLabel("Major grid color");
		majorColorFieldEditor.setPreferenceName(JoglViewerPreference.MAJOR_GRID_COLOR);
		
		ColorFieldEditor minorColorFieldEditor = new ColorFieldEditor(grpSettings, SWT.NONE);		
		minorColorFieldEditor.setLabel("Minor grid color");
		minorColorFieldEditor.setPreferenceName(JoglViewerPreference.MINOR_GRID_COLOR);
		
		IntegerFieldEditor gridOpacityFieldEditor = new IntegerFieldEditor(grpSettings, SWT.NONE);
		gridOpacityFieldEditor.setLabel("Grid opacity");
		gridOpacityFieldEditor.setWidthInChars(4);
		gridOpacityFieldEditor.setLabelWidthInChar(12);
		gridOpacityFieldEditor.setPreferenceName(JoglViewerPreference.GRID_OPACITY);
		
		Group grpLimits = new Group(parent, SWT.NONE);
		grpLimits.setLayout(new GridLayout(1, false));
		grpLimits.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpLimits.setText("Limits");
		
		BooleanFieldEditor booleanFieldEditor = new BooleanFieldEditor(grpLimits, SWT.NONE);
		booleanFieldEditor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		booleanFieldEditor.setLabel("Inherit from controller");
		
		Composite composite = new Composite(grpLimits, SWT.NONE);
		GridLayout gl_composite = new GridLayout(5, false);
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblStart = new Label(composite, SWT.NONE);
		GridData gd_lblStart = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblStart.widthHint = 35;
		lblStart.setLayoutData(gd_lblStart);
		lblStart.setBounds(0, 0, 55, 15);
		lblStart.setText("Start");
		
		startXFieldEditor = new QuantityFieldEditor(composite, SWT.NONE);
		startXFieldEditor.setEmptyStringAllowed(false);
		startXFieldEditor.setWidthInChars(6);
		startXFieldEditor.setLabel("X");
		startXFieldEditor.setPreferenceName(JoglViewerPreference.GRID_START_X);
		
		Label lblNewLabel = new Label(composite, SWT.HORIZONTAL);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Label lblEnd = new Label(composite, SWT.NONE);
		lblEnd.setText("End");
		
		endXFieldEditor = new QuantityFieldEditor(composite, SWT.NONE);
		endXFieldEditor.setEmptyStringAllowed(false);
		endXFieldEditor.setWidthInChars(6);
		endXFieldEditor.setLabel("X");
		endXFieldEditor.setPreferenceName(JoglViewerPreference.GRID_END_X);
		new Label(composite, SWT.NONE);
		
		startYFieldEditor = new QuantityFieldEditor(composite, SWT.NONE);
		startYFieldEditor.setEmptyStringAllowed(false);
		startYFieldEditor.setWidthInChars(6);
		startYFieldEditor.setLabel("Y");
		startYFieldEditor.setPreferenceName(JoglViewerPreference.GRID_START_Y);
		
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		endYFieldEditor = new QuantityFieldEditor(composite, SWT.NONE);
		endYFieldEditor.setEmptyStringAllowed(false);
		endYFieldEditor.setWidthInChars(6);
		endYFieldEditor.setLabel("Y");
		endYFieldEditor.setPreferenceName(JoglViewerPreference.GRID_END_Y);
		
		// Adding fields
		addField(majorSpacingFieldEditor);
		addField(minorSpacingFieldEditor);
		
		addField(majorColorFieldEditor);
		addField(minorColorFieldEditor);
		addField(gridOpacityFieldEditor);
		addField(startXFieldEditor);
		addField(startYFieldEditor);
		addField(endXFieldEditor);
		addField(endYFieldEditor);
		
	}
	
	@Inject
	public void onUnitPreferenceChange(@Preference(nodePath = GokoPreference.NODE_ID, value = GokoPreference.KEY_DISTANCE_UNIT) String unit) {			
		try {
			Unit<Length> lengthUnit;lengthUnit = GokoPreference.getInstance().getLengthUnit();
			if(majorSpacingFieldEditor != null && minorSpacingFieldEditor != null){
				majorSpacingFieldEditor.setUnit(lengthUnit);
				minorSpacingFieldEditor.setUnit(lengthUnit);
				startXFieldEditor.setUnit(lengthUnit);
				startYFieldEditor.setUnit(lengthUnit);
				endXFieldEditor.setUnit(lengthUnit);
				endYFieldEditor.setUnit(lengthUnit);
			}
		} catch (GkException e) {
			LOG.error(e);
		}					
	}
}
