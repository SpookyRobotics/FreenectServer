package org.openkinect.freenect;

import com.spookybox.frameConsumers.ArffCreator;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ArffCreatorTest {

    private static final String TEST_FILE_NAME = "TEST_ARFF";
    private ArffCreator mArffCreator;

    @Before
    public void setUp(){
        mArffCreator = new ArffCreator(TEST_FILE_NAME);
    }

    @Test
    public void testArffHeader() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(TEST_FILE_NAME+".arff"));

        writer.print(mArffCreator.getArffDataHeader());
        writer.close();
    }
}
