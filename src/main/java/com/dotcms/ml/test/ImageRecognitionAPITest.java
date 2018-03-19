package com.dotcms.ml.test;

import com.dotcms.ml.api.ImageRecognitionApi;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImageRecognitionAPITest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @Test
  public void test() throws Exception {

    File f = new File("src/main/java/com/dotcms/ml/test/tower-bridge.jpg");
    
    System.err.println(f.getAbsolutePath());
    if(f.exists()){
      
      System.err.println(new ImageRecognitionApi().detectLabels(f));
      
    }
    else {
        System.err.println("image does not exist");
    }
    
    
    
  }

}
