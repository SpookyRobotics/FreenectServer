package com.spookybox.applications;

public interface ApplicationInstance {
    void run();
    void shutdownKinect();
    void haltCameraAndTilt();
}
