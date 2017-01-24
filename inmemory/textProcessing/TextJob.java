package inmemory.textProcessing;

import com.sun.xml.internal.ws.developer.Serialization;

import java.io.Console;
import java.io.Serializable;
import java.util.*;
import java.io.*;

/**
 * Created by Grzegorz on 2017-01-23.
 */

public class TextJob implements Serializable {

    private String jobName;
    public TextJob()
    {
        jobName = "XD";
    }

    public void displayName()
    {
        System.out.println("JOB: " + jobName);
    }



}
