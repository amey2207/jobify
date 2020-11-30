package com.sheridan.jobpill.ML;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;
import com.sheridan.jobpill.Job.JobPostingActivity;
import com.sheridan.jobpill.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageModerate {

    private Bitmap imageBitmap;
    private Context context;
    private boolean imageClean = false;

    //constants
    final String LIKELY_LIKELIHOOD = "LIKELY";
    final String VERY_LIKELY_LIKELIHOOD = "VERY_LIKELY";
    final String ERROR = "ERROR";
    final String EMPTY = "EMPTY";

    public ImageModerate(Context context, Bitmap imageBitmap){
        this.context = context;
        this.imageBitmap = imageBitmap;
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

    private Map<String, String> responseToString(BatchAnnotateImagesResponse response, Map<String, String> visionResults){

        List<AnnotateImageResponse> responses = response.getResponses();

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

        return visionResults;
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
                    feature.setType("SAFE_SEARCH_DETECTION");
                    add(feature);
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

                    return responseToString(response, visionResults);
                }
                catch (GoogleJsonResponseException err){
                    visionResults.put(ERROR, "API Request Failed: " + err.getMessage());
                }
                catch (IOException err){
                    visionResults.put(ERROR, "API Request Failed (IO Exception): " + err.getMessage());
                }

                return visionResults;
            }

            @Override
            protected void onPostExecute(Map<String, String> results) {
                super.onPostExecute(results);

                String message = null;

                //check if results found innaprpriate content in image
                for(Map.Entry<String, String> result : results.entrySet()){
                    if(result.getValue().equals(LIKELY_LIKELIHOOD) || result.getValue().equals(VERY_LIKELY_LIKELIHOOD)){
                        message += result.getKey() + ", ";
                    }
                }

                if(message != null){
                    //trim message ('null' at start of message ', ' at end of message) and build message
                    message = message.substring(4, message.length() - 2);
                    message += " content in image, please select another image.";

                    Toast.makeText(context, "Detected " + message, Toast.LENGTH_LONG).show();
                }
                else{
                    imageClean = true;
                }
            }
        }.execute();
    }

}
