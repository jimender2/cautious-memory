package net.jimender2.ignition.pdf.beaninfos;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;

import net.jimender2.ignition.pdf.Viewer;

import java.beans.IntrospectionException;

public class ViewerBeanInfo extends CommonBeanInfo {
    public ViewerBeanInfo() {
        super(Viewer.class, DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();
        this.removeProp("opaque");
        this.removeProp("font");
        this.removeProp("foreground");
        this.removeProp("background");
        this.removeProp("toolTipText");
        this.addProp("filePath", "File Path", "FilePath for the PDF file to render.", "Data", 49);
        this.addEnumProp("pageFitMode", "Page Fit Mode", "Set the PDF fit mode", "Appearance", new int[] { 1, 2, 3, 4 },
                new String[] { "None", "Actual Size", "Fit Height", "Fit Width" });
        this.addEnumProp("pageViewMode", "Page View Mode", "Set the view mode for displaying PDF pages", "Appearance",
                new int[] { 2, 1, 4, 3, 6, 5 }, new String[] { "Column View", "One Page", "Two Column, Left",
                        "Two Page, Left", "Two Column, Right", "Two Page, Right" });
        this.addProp("toolBarVisible", "Toolbar Visible", "Should the icepdf toolbar be visible.", "Appearance");
        this.addProp("utilityPaneVisible", "Utility Visible", "Should the icepdf utility pane be visible.",
                "Appearance");
        this.addProp("footerVisible", "Footer Visible", "Should the icdpdf footer be visible.", "Appearance");
    }

    @Override
    protected void initDesc() {
        this.getBeanDescriptor().setName("Modern PDF Viewer");
        this.getBeanDescriptor().setDisplayName("Modern PDF Viewer");
        this.getBeanDescriptor().setShortDescription("Adds a modern PDF viewer component.");
    }
}
