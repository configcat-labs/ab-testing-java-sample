package org.example;
import com.amplitude.Amplitude;
import com.amplitude.Event;
import com.configcat.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        GUI gui = new GUI();
        String userID = "user100";
        ConfigCatClient client = ConfigCatClient.newBuilder()
                .build("YOUR-SDK-KEY"); // <-- This is the actual SDK Key for your 'Test Environment' environment.

        User userObject = User.newBuilder()
                .build(userID);

        //get flag value
        boolean greenbutton = client.getValue(Boolean.class, "greenbutton", userObject, false);

        System.out.println("greenbutton's value from ConfigCat: " + greenbutton);

        //Connecting to amplitude via API
        Amplitude amplitude = Amplitude.getInstance();
        amplitude.init("YOUR-AMPLITUDE-API-KEY");

        //flush events to Amplitude when 5 of them are created
        amplitude.setEventUploadThreshold(5);

        gui.showGUI(greenbutton, userID, amplitude);

        //flush events manually
        amplitude.flushEvents();
    }
}

class GUI{
 public void showGUI(boolean flagValue, String userID, Amplitude client) {
     //setup for JFrame
     JFrame frame=new JFrame("A/B Testing");
     frame.getContentPane().setBackground(Color.lightGray );
     frame.setSize(500,400);
     frame.setLayout(null);
     frame.setVisible(true);

     JButton btn=new JButton("Learn More");
     btn.setBounds(150,100,150,30);

     if(flagValue) {
         btn.setBackground(Color.GREEN);
     }
     else{
         btn.setBackground(Color.BLUE);
     }
     btn.setForeground(Color.WHITE);
     btn.setFocusPainted(false);

     JLabel question = new JLabel("Q: What is computer science?");
     question.setBounds(15,30,500,20);

     JLabel answer = new JLabel("A: Computer science is the study of computation, automation, and information.");
     answer.setBounds(15,50,500,20);

     frame.add(btn);
     frame.add(question);
     frame.add(answer);

     btn.addActionListener(e -> {
         //creating event
         Event event = new Event("Learn More Button Clicked", userID);

         JSONObject userProps = new JSONObject();

         //adding User extra props
         try {
             userProps.put("feature_flag", flagValue);
         } catch (JSONException err) {
             err.printStackTrace();
             System.err.println("Invalid JSON");
         }

         event.userProperties = userProps;

         //log event
         client.logEvent(event);
     });
 }
}