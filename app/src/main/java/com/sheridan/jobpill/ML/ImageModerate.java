package com.sheridan.jobpill.ML;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;
import com.sheridan.jobpill.Job.JobPostingActivity;
import com.sheridan.jobpill.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImageModerate {

    private Context context;
    private Bitmap imageBitmap;
    private HashMap<String, Float> extraLabels;
    private boolean imageClean = false;

    //constants
    final String LIKELY_LIKELIHOOD = "LIKELY";
    final String VERY_LIKELY_LIKELIHOOD = "VERY_LIKELY";
    final String ERROR = "ERROR";
    final String EMPTY = "EMPTY";

    //list of extra inappropriate labels
    final Set<String> weaponLabels = new HashSet<>(Arrays.asList("Gun", "Firearm", "Soldier", "Shooting"));
    final Set<String> drugLabels = new HashSet<>(Arrays.asList("Pill", "Capsule", "Prescription Drug", "Pharmaceutical Drug"));

    final int confidenceThreshold = 80;

    public ImageModerate(Context context, Bitmap imageBitmap){
        this.context = context;
        this.imageBitmap = imageBitmap;
        this.extraLabels = new HashMap<>();
    }

    public boolean isImageClean() {
        return imageClean;
    }

    public void setImageClean(boolean imageClean) {
        this.imageClean = imageClean;
    }

    private Image encodeImage(Bitmap bitmap){
        Image encodedImage = new Image();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //convert bitmap to jpeg
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

        //encode the jpeg image to base64
        byte[] imageBytes = outputStream.toByteArray();
        encodedImage.encodeContent(imageBytes);

        return encodedImage;
    }

    private Map<String, String> convertResponse(BatchAnnotateImagesResponse response, Map<String, String> visionResults){

        //get the safe search results
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        SafeSearchAnnotation annotation = response.getResponses().get(0).getSafeSearchAnnotation();

        if(labels != null && annotation != null){
            if (labels != null){

                //get extra label descriptions and confidence scores and store results into hash map
                for (EntityAnnotation label : labels) {
                    extraLabels.put(label.getDescription(), label.getScore());
                }
            }

            if (annotation != null){

                //get safe search results and store results into hash map
                visionResults.put("adult", annotation.getAdult());
                visionResults.put("medical", annotation.getMedical());
                visionResults.put("spoof", annotation.getSpoof());
                visionResults.put("violent", annotation.getViolence());
            }
        }
        else{
            visionResults.put(EMPTY, "No Results");
        }

        return visionResults;

        /*List<AnnotateImageResponse> responses = response.getResponses();

        for(AnnotateImageResponse res: responses){
            if(res.getError() == null){

                //get the safe search results
                SafeSearchAnnotation annotation = res.getSafeSearchAnnotation();

                //store results into hash map
                visionResults.put("adult", annotation.getAdult());
                visionResults.put("medical", annotation.getMedical());
                visionResults.put("spoof", annotation.getSpoof());
                visionResults.put("violent", annotation.getViolence());
            }
            else{
                visionResults.put(EMPTY, "No Results");
            }
        }

        return visionResults;*/
    }

    public void callVisionAPI(){

        new AsyncTask<Object, Void, Map<String, String>>(){
            @Override
            protected Map<String, String> doInBackground(Object... objects) {

                //create results map for api and a list of requests for vision api
                HashMap<String, String> visionResults = new HashMap<>();
                ArrayList<AnnotateImageRequest> imageRequests = new ArrayList<>();

                //create request with safe search detection feature
                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                    Feature feature = new Feature();
                    feature.setType("LABEL_DETECTION");
                    feature.setMaxResults(10);
                    add(feature);

                    Feature safeFeature = new Feature();
                    safeFeature.setType("SAFE_SEARCH_DETECTION");
                    add(safeFeature);
                }});

                //encode the image and add it to request list
                annotateImageRequest.setImage(encodeImage(imageBitmap));
                imageRequests.add(annotateImageRequest);

                try{
                    //use http transport protocol and json parser to connect to cloud vision API
                    HttpTransport httpTransport = new NetHttpTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    //build vision initializer request
                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(context.getString(R.string.API_KEY));
                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);
                    Vision vision = builder.build();

                    //set batch annotation request
                    BatchAnnotateImagesRequest request = new BatchAnnotateImagesRequest();
                    request.setRequests(imageRequests);

                    //get batch annotation response
                    Vision.Images.Annotate annotate = vision.images().annotate(request);
                    annotate.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotate.execute();

                    return convertResponse(response, visionResults);
                }
                catch (GoogleJsonResponseException err){
                    visionResults.put(ERROR, "Request Failed: " + err.getMessage());
                }
                catch (IOException err){
                    visionResults.put(ERROR, "Request Failed (IO Exception): " + err.getMessage());
                }

                return visionResults;
            }

            @Override
            protected void onPostExecute(Map<String, String> results) {
                super.onPostExecute(results);

                String mainMessage = EMPTY;
                String extraLabelMessage = EMPTY;

                //check if results found explicit content in image
                for(Map.Entry<String, String> result : results.entrySet()){
                    if(result.getValue().equals(LIKELY_LIKELIHOOD) || result.getValue().equals(VERY_LIKELY_LIKELIHOOD)){
                        mainMessage += result.getKey() + ", ";
                    }
                }

                //check if results found extra inappropriate content (weapons, drugs) in image
                for(Map.Entry<String, Float> result : extraLabels.entrySet()){

                    int labelResult = checkLabel(result.getKey());

                    //check if label matches any inappropriate labels
                    if(labelResult != 0 && result.getValue()*100 > confidenceThreshold){

                        if(labelResult == 1){
                            extraLabelMessage = extraLabelMessage.indexOf("weapons") == -1 ? extraLabelMessage + "weapons, " : extraLabelMessage;  //check if weapons label is already found
                        }
                        else if(labelResult == 2 && extraLabelMessage != null){
                            extraLabelMessage = extraLabelMessage.indexOf("drugs") == -1 ? extraLabelMessage + "drugs, " : extraLabelMessage;  //check if drugs label is already found
                        }
                    }
                }

                if(!mainMessage.equals(EMPTY) || !extraLabelMessage.equals(EMPTY)){

                    //build and display message
                    String message = buildMessage(mainMessage, extraLabelMessage);
                    Toast.makeText(context, "Detected " + message, Toast.LENGTH_LONG).show();
                }
                else{
                    imageClean = true;
                }
            }
        }.execute();
    }

    /*
    *  checks if resulted labels are part of the weapons or drugs category
    *  returns 0 if label is not part of any list category
    *  returns 1 if label is part of weapons list category
    *  returns 2 if label is part of drugs list category
     */
    private int checkLabel(String label){

        if(weaponLabels.contains(label)){
            return 1;
        }
        else if(drugLabels.contains(label)){
            return 2;
        }

        /*for (String weaponLabel : weaponLabels){
            if(label.equals(weaponLabel)){
                return 1;
            }
        }

        for(String drugLabel : drugLabels){
            if(label.equals(drugLabel)){
                return 2;
            }
        }*/

        return 0;
    }

    private String buildMessage(String mainMessage, String extraLabelMessage){

        if(!mainMessage.equals(EMPTY) && !extraLabelMessage.equals(EMPTY)){

            //trim both messages ('null' and ', ') and build message
            mainMessage = mainMessage.substring(EMPTY.length(), mainMessage.length() - 2);
            extraLabelMessage = extraLabelMessage.substring(EMPTY.length(), extraLabelMessage.length() - 2);
            mainMessage = extraLabelMessage + " as well as " + mainMessage + " in image, please select another image.";
        }
        else if(!extraLabelMessage.equals(EMPTY)){

            //trim message ('null' and ', ') and build message
            extraLabelMessage = extraLabelMessage.substring(EMPTY.length(), extraLabelMessage.length() - 2);
            mainMessage = extraLabelMessage + " in image, please select another image.";
        }
        else if(!mainMessage.equals(EMPTY)){

            //trim message ('null' and ', ') and build message
            mainMessage = mainMessage.substring(EMPTY.length(), mainMessage.length() - 2);
            mainMessage += " content in image, please select another image.";
        }

        return mainMessage;
    }

}
