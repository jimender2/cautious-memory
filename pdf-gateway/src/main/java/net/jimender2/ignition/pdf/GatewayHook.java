package net.jimender2.ignition.pdf;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayHook extends AbstractGatewayModuleHook {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setup(GatewayContext gatewayContext) {
        logger.info("setup()");
    }

    @Override
    public void startup(LicenseState licenseState) {
        logger.info("startup()");
    }

    @Override
    public void shutdown() {
        logger.info("shutdown()");
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }
}
