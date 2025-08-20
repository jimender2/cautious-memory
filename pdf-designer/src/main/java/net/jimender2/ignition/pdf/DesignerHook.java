package net.jimender2.ignition.pdf;

import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.vision.api.designer.VisionDesignerInterface;
import com.inductiveautomation.vision.api.designer.palette.JavaBeanPaletteItem;
import com.inductiveautomation.vision.api.designer.palette.Palette;
import com.inductiveautomation.vision.api.designer.palette.PaletteItemGroup;

public class DesignerHook extends AbstractDesignerModuleHook {
    private final LoggerEx log = LogUtil.getLogger("designer hook");
    private DesignerContext context;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        this.log.info("Starting up Modern PDF Viewer Module. Mode");
        BundleUtil.get().addBundle("modern_pdf", DesignerHook.class.getClassLoader(), "modern_pdf");
        this.context = context;
        context.addBeanInfoSearchPath("net.jimender2.ignition.pdf.beaninfos");
        VisionDesignerInterface sdk = (VisionDesignerInterface) this.context
                .getModule("com.inductiveautomation.vision");
        if (sdk != null) {
            Palette palette = sdk.getPalette();
            PaletteItemGroup group = palette.addGroup("Modern PDF Viewer");
            group.addPaletteItem(new JavaBeanPaletteItem(Viewer.class));
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {

    }
}
