package com.qualcomm.ftcrobotcontroller.opmodes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The <code>RegisterOpMode</code> annotation is used to specify that an op mode should be registered
 * for display on the driver station.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegisterOpMode {
    /**
     * The name of the op mode.
     * This is displayed on the driver station.
     * If empty, the name of the op mode class will be used.
     */
    String value() default "";
}