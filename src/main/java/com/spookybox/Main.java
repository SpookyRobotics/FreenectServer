package com.spookybox;

import com.spookybox.applications.*;

public class Main {

    private static ApplicationInstance instance = new DisplayCamera();

    public static void main(String[] args){
        System.out.println("-- Application Start --");
        instance.run();
        instance.haltCameraAndTilt();
        instance.shutdownKinect();
        System.out.println("-- Application End --");
    }

}
