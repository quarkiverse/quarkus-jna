package io.quarkiverse.jna;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class JnaProcessor {

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("jna");
    }
}
