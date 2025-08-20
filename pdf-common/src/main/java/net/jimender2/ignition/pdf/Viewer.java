package net.jimender2.ignition.pdf;

import javax.swing.JPanel;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.ViewerPropertiesManager;

import com.inductiveautomation.factorypmi.application.components.PropChangeHolder;
import com.inductiveautomation.ignition.client.util.EDTUtil;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.vision.api.client.components.model.AbstractVisionPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import org.apache.commons.lang3.StringUtils;
import org.icepdf.core.pobjects.Catalog;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.MyAnnotationCallback;

public class Viewer extends AbstractVisionPanel {
    public static final Dimension PREFERRED_SIZE = new Dimension(800, 800);
    SwingController controller;
    protected String filePath = "";
    protected boolean toolBarVisible = true;
    protected boolean footerVisible = true;
    protected boolean utilityPaneVisible = false;
    protected int fitMode = 1;
    protected int viewMode = 1;
    protected JPanel footer;
    protected PropChangeHolder<Integer> pageProp = new PropChangeHolder<>(this, "page", 0);
    private static LoggerEx log = LogUtil.getLogger("Viewer");

    public Viewer() {
        super(new BorderLayout());
        this.setPreferredSize(PREFERRED_SIZE);
        ViewerPropertiesManager properties = ViewerPropertiesManager.getInstance();
        properties.setBoolean("application.toolbar.show.utility.save", false);
        properties.setBoolean("application.toolbar.show.annotation", false);
        properties.setBoolean("application.toolbar.show.search", false);
    }

    protected void onStartup() {
        this.controller = new SwingController() {
            @Override
            protected void applyViewerPreferences(Catalog catalog, ViewerPropertiesManager propertiesManager) {
                super.applyViewerPreferences(catalog, propertiesManager);
                Viewer.this.controller.setToolBarVisible(Viewer.this.toolBarVisible);
                Viewer.this.controller.setUtilityPaneVisible(Viewer.this.utilityPaneVisible);
                Viewer.this.controller.setPageFitMode(Viewer.this.fitMode, true);
                Viewer.this.controller.setPageViewMode(Viewer.this.viewMode, true);
            }
        };
        this.controller.setPageFitMode(this.fitMode, true);
        this.controller.setPageViewMode(this.viewMode, true);
        this.controller.getDocumentViewController()
                .setAnnotationCallback(new MyAnnotationCallback(this.controller.getDocumentViewController()));
        SwingViewBuilder factory = new SwingViewBuilder(this.controller) {
            @Override
            public JPanel buildStatusPanel() {
                Viewer.this.footer = super.buildStatusPanel();
                return Viewer.this.footer;
            }

            @Override
            public JToolBar buildCompleteToolBar(boolean embeddableComponent) {
                JToolBar toolbar = super.buildCompleteToolBar(embeddableComponent);
                if (toolbar != null)
                    toolbar.setVisible(Viewer.this.toolBarVisible);

                return toolbar;
            }

            @Override
            public JTabbedPane buildUtilityTabbedPane() {
                JTabbedPane pane = super.buildUtilityTabbedPane();
                if (pane != null)
                    pane.setVisible(Viewer.this.utilityPaneVisible);

                return pane;
            }
        };
        JPanel viewerComponentPanel = factory.buildViewerPanel();
        ComponentKeyBinding.install(this.controller, viewerComponentPanel);
        this.add(viewerComponentPanel, "Center");
        EventQueue.invokeLater(() -> {
            this.footer.setVisible(this.footerVisible);
            if (StringUtils.isNotBlank(this.filePath))
                this.tryOpen(this.filePath);

            this.setUtilityPaneVisible(this.utilityPaneVisible);
        });
    }

    protected void onShutdown() {
        this.controller.dispose();
        this.remove(0);
    }

    public SwingController getController() {
        return this.controller;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        String old = this.filePath;
        this.filePath = filePath;
        boolean unchanged = StringUtils.equals(old, this.filePath);
        if (this.controller != null && !unchanged)
            EDTUtil.coalescingInvokeLater(new FilePathSetter(this, filePath));

        this.firePropertyChange("filePath", old, filePath);
    }

    public boolean tryOpen(String path) {
        if (this.controller == null) {
            log.warnf("Viewer controller is null, cannot open PDF at path '%s'", path);
            return false;
        }
        if (StringUtils.isBlank(path)) {
            log.warnf("Path is blank, cannot open PDF.");
            return false;
        }

        boolean success = false;

        File f = new File(path);
        boolean isFile = f.isFile() && f.canRead();

        if (isFile) {
            log.tracef("A file was detected at '%s', attempting to open.", path);
            this.controller.openDocument(path);
            success = true;
            log.trace("Done trying to open pdf");
        } else {
            try {
                log.tracef("Trying to open the PDF at '%s'.", path);
                URL url = new URL(path);
                this.controller.openDocument(url);
                success = true;
                log.trace("Attempt to open pdf completed.");
            } catch (MalformedURLException e) {
                log.warnf("Attempt to open URL '%s' failed", path, e);
            } catch (Exception e) {
                log.warnf("Attempt to open PDF at path '%s' failed", path, e);
            }
        }

        return success;
    }

    public boolean isToolBarVisible() {
        return this.toolBarVisible;
    }

    public void setToolBarVisible(boolean show) {
        this.toolBarVisible = show;
        if (this.controller != null) {
            this.controller.setToolBarVisible(show);
        }

    }

    public boolean isUtilityPaneVisible() {
        return this.utilityPaneVisible;
    }

    public void setUtilityPaneVisible(boolean visible) {
        this.utilityPaneVisible = visible;
        if (this.controller != null) {
            this.controller.setUtilityPaneVisible(visible);
        }
    }

    public int getPageFitMode() {
        return this.controller != null ? this.controller.getDocumentViewController().getFitMode() : this.fitMode;
    }

    public void setPageFitMode(int newMode) {
        this.fitMode = newMode;
        if (this.controller != null)
            this.controller.setPageFitMode(newMode, true);
    }

    public int getPageViewMode() {
        return this.controller != null ? this.controller.getDocumentViewController().getViewMode() : this.viewMode;
    }

    public void setPageViewMode(int newMode) {
        this.viewMode = newMode;
        if (this.controller != null)
            this.controller.setPageViewMode(newMode, true);
    }

    public boolean isFooterVisible() {
        return this.footerVisible;
    }

    public void setFooterVisible(boolean visible) {
        this.footerVisible = visible;
        if (this.footer != null)
            this.footer.setVisible(visible);
    }

    public void loadPDFBytes(byte[] pdfBytes, String description) {
        this.controller.openDocument(new ByteArrayInputStream(pdfBytes), description, (String) null);
    }

    public void print() {
        this.print(true);
    }

    public void print(boolean withDialog) {
        if (this.controller == null) {
            ErrorUtil.showError("Viewer controller is null, cannot print PDF.");
            return;
        }

        if (StringUtils.isBlank(this.filePath)) {
            ErrorUtil.showError("Filepath is blank");
            return;
        }

        if (this.controller.getDocument() == null) {
            ErrorUtil.showError("No PDF found at path: " + this.filePath);
            return;
        }

        this.controller.print(withDialog);
    }

    public float getZoomFactor() {
        float zoomFactor = 0.0F;
        if (this.controller != null) {
            zoomFactor = this.controller.getDocumentViewController().getZoom();
        }

        return zoomFactor;
    }

    public void setZoomFactor(float zoomFactor) {
        if (this.controller != null)
            this.controller.setZoom(zoomFactor);
    }

    static {
        System.getProperties().put("org.icepdf.core.imageReference", "scaled");
        System.getProperties().put("org.icepdf.core.screen.interpolation", "VALUE_INTERPOLATION_BICUBIC");
    }
}
