package com.dotcms.ml.actionlet;


import com.dotcms.contenttype.model.field.BinaryField;
import com.dotcms.contenttype.model.field.Field;
import com.dotcms.contenttype.model.field.TagField;
import com.dotcms.ml.api.ImageRecognitionApi;
import com.dotcms.ml.util.PropertyBundle;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.image.filter.ResizeImageFilter;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.workflows.actionlet.WorkFlowActionlet;
import com.dotmarketing.portlets.workflows.model.WorkflowActionClassParameter;
import com.dotmarketing.portlets.workflows.model.WorkflowActionFailureException;
import com.dotmarketing.portlets.workflows.model.WorkflowActionletParameter;
import com.dotmarketing.portlets.workflows.model.WorkflowProcessor;
import com.dotmarketing.tag.business.TagAPI;
import com.dotmarketing.tag.model.Tag;
import com.dotmarketing.util.UtilMethods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageRecognitionActionlet extends WorkFlowActionlet {

  private static final long serialVersionUID = 1L;

  private final String TAGGED_BY_AWS = "TAGGED_BY_AWS";



  @Override
  public List<WorkflowActionletParameter> getParameters() {
      List<WorkflowActionletParameter> params = new ArrayList<WorkflowActionletParameter>();

      params.add(new WorkflowActionletParameter("maxLabels", "Max Labels", PropertyBundle.getProperty("max.labels", "15"), true));
      params.add(new WorkflowActionletParameter("minConfidence", "Minimum Confidence (percent)", PropertyBundle.getProperty("min.confidence", "75"), true));
      return params;
  }

  @Override
  public String getName() {
    return "Auto Tag Images - AWS";
  }

  @Override
  public String getHowTo() {
    return "Max Labels is the maximum number of labels you are looking to return and Minimum Confidence is the minimum confidence level you will accept as valid tags";
  }



  @Override
  public void executeAction(WorkflowProcessor processor, Map<String, WorkflowActionClassParameter> params)
      throws WorkflowActionFailureException {


    Contentlet con = processor.getContentlet();

    Field tagField = null;
    File image = null;
    TagAPI tapi = APILocator.getTagAPI();
    List<Field> fields;
    try {
        fields = APILocator.getContentTypeAPI(processor.getUser()).find(con.getContentTypeId()).fields();
    } catch (DotDataException | DotSecurityException e1) {
        throw new WorkflowActionFailureException("unable to get fields" , e1);
    }
    
    
    
    for (Field f : fields) {
      if (f instanceof TagField) {
        tagField = f;
        break;
      }
    }
    if (tagField == null) {
      return;
    }


    for (Field f : fields) {
      if (f instanceof BinaryField) {
        try {
          image = con.getBinary(f.variable());
         
          if (UtilMethods.isImage(image.getAbsolutePath())) {
              break;
          } else {
            return;
          }
        } catch (IOException e) {
          return;
        }
      }

    }
    


    try {
      List<Tag> tags = tapi.getTagsByInode(con.getInode());
      if (tags.contains(TAGGED_BY_AWS)){
        return;
      }
      
      String min = params.get("minConfidence").getValue();
      
      
      float minConfidence = Float.parseFloat(min );
      int maxLabels = Integer.parseInt(params.get("maxLabels").getValue());
      


      if(image.length() > 5242879){
        Map<String, String[]> args = new HashMap<>();
        args.put("resize_w", new String[]{"1000"});
        image =  new ResizeImageFilter().runFilter(image, args);
      }
      
      
      List<String> awsTags = new ImageRecognitionApi().detectLabels(image, maxLabels, minConfidence);
      
      
      
      
      awsTags.add(TAGGED_BY_AWS);
      for (String tag : awsTags) {
        tapi.addContentletTagInode(tag, con.getInode(), con.getHost(), tagField.variable());
      }

      APILocator.getContentletAPI().refresh(con);

    } catch (Exception  e) {
      throw new WorkflowActionFailureException(e.getMessage(), e);
    }



  }

}
