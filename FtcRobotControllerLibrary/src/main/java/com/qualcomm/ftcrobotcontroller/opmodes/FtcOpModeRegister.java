/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * Register Op Modes
 */
public class FtcOpModeRegister implements OpModeRegister {

  /**
   * The Op Mode Manager will call this method when it wants a list of all
   * available op modes. Add your op mode to the list to enable it.
   *
   * @param manager op mode manager
   */
  public void register(OpModeManager manager) {

    File opModeDex = new File(Environment.getExternalStorageDirectory(), "FIRST/opmodes.dex");

    if(opModeDex.canRead()) {
      Log.d("RegisterOpMode", "Registering op modes from opmodes.dex");
    } else {
      Log.e("RegisterOpMode", "No opmodes available to register");
    }

    try {
      DexFile df = new DexFile(opModeDex);

      final Class<?> activityThreadClass =
              Class.forName("android.app.ActivityThread");
      final Method method = activityThreadClass.getMethod("currentApplication");
      Context context = (Application) method.invoke(null, (Object[]) null);
      File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);

      ClassLoader loader = new DexClassLoader(opModeDex.getPath(), dexOutputDir.getAbsolutePath(), null, getClass().getClassLoader());


      for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
        String s = iter.nextElement();
        Class<RegisterOpMode> activeClass = RegisterOpMode.class;
        Class currentClass = loader.loadClass(s);
        if (currentClass.isAnnotationPresent(activeClass)) {
          Annotation annotation = currentClass.getAnnotation(activeClass);
          String name = ((RegisterOpMode) annotation).value();
          if (name.length() == 0) {
            name = currentClass.getSimpleName();
          }
          Log.i("RegisterOpMode", "Registering op mode " + name + " [" + currentClass.getName() + "]");
          manager.register(name, currentClass);
        }
      }
    //None of these exceptions should every occur, so don't bother with full error handling
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }
}
