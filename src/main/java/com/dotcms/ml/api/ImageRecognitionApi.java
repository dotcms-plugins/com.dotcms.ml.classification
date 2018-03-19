package com.dotcms.ml.api;

import com.dotcms.ml.util.PropertyBundle;

import com.dotmarketing.business.DotStateException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.trainedmodels.TrainedModels;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.ResNet50;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

import com.google.common.collect.ImmutableList;

public class ImageRecognitionApi {



    private final float minConfidence;
    private final int maxLabels;

    public ImageRecognitionApi() throws IOException {


        this.maxLabels = 0;//Integer.parseInt(PropertyBundle.getProperty("max.labels", "15"));
        this.minConfidence = 0;//Float.parseFloat(PropertyBundle.getProperty("min.confidence", "75"));

    }

    public List<String> detectLabels(File file, int maxLabels, float minConfidence) {
        try {
            return _detectLabels(file, maxLabels, minConfidence);
        } catch (Exception e) {
            throw new DotStateException(e.getMessage(), e);
        }

    }


    public List<String> detectLabels(File file) {
        try {
            ZooModel zooModel = new ResNet50();
            ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
            NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
            INDArray image = loader.asMatrix(file);


            DataNormalization scaler = new VGG16ImagePreProcessor();
            scaler.transform(image);
            INDArray[] output = vgg16.output(false,image);
            String predictions = TrainedModels.VGG16.decodePredictions(output[0]);
            
            System.out.println(output[0]);
            return ImmutableList.of(predictions);
            
        } catch (Exception e) {
            throw new DotStateException(e.getMessage(), e);
        }

    }


    private List<String> _detectLabels(File file, int maxLabels, float minConfidence) throws IOException {



        List<String> labels = new ArrayList<>();


        return labels;



    }



}
