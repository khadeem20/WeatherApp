
import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;





public class GUI extends JFrame{

    private JSONObject weatherData;


    public GUI(){
        //setup the gui and add a title
        super("Weather App");

        //set gui to end programs programs's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set gui size in pixels
        setSize(450, 650);

        //set load location
        setLocationRelativeTo(null);


        //make layout manager null to manually postion components within the gui
        setLayout(null);

        // prevent any resize of the gui
        setResizable(false);

        addGuiComponents();
    }
    
    public void addGuiComponents(){
        //search field
        JTextField searchTextField = new JTextField();

        //set the location and size of the componnet
        searchTextField.setBounds(15,15,351,45);

        //change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
    
        add(searchTextField);
    
       
        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("C://Users//khade//OneDrive//Documents//Projects//WeatherApp//src//Assets//cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather conditions description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //   humidity image
        JLabel humidityImage = new JLabel(loadImage("C://Users//khade//OneDrive//Documents//Projects//WeatherApp//src//Assets//humidity.png"));
        humidityImage.setBounds(15,500, 76,66);
        add(humidityImage);

        // humidity Text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed Image
        JLabel windspeedImage = new JLabel(loadImage("C://Users//khade//OneDrive//Documents//Projects//WeatherApp//src//Assets//windspeed.png"));
        windspeedImage.setBounds(220,500, 74,66);
        add(windspeedImage);

        // windspeed Text
        JLabel windspeedText = new JLabel("<html><b>windspeed</b> 15km/h </html>");
        windspeedText.setBounds(310,500,85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

         //searchButton

       JButton searchButton= new JButton(loadImage("C://Users//khade//OneDrive//Documents//Projects//WeatherApp//src//Assets//search.png"));
        

       //change cursor to a hand courser when hovering over this buttion
       searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
       searchButton.setBounds(375,13, 47, 45);
       searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                //get loction from user
                String userInput = searchTextField.getText();

                //validate input - get rid of whitespace to ensure non-emptytext
                if(userInput.replaceAll("//s", "").length() <= 0){
                    return;
                }

                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);


                //update gui
                

                //update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                //weather imnage will change to correspond to the condition
                switch(weatherCondition){
                   
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("C://Users//khade//OneDrive//Documents//Projects//WeatherApp//src//Assets//clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("C://Users//khade//OneDrive//Documents//Projects//WeatherApp//src//Assets//cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("C://Users//khade//OneDrive//Documents//Projects//WeatherApp//src//Assets//rian.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("C://Users//khade//OneDrive//Documents//Projects//WeatherApp//src//Assets//snow.png"));
                        break;
                }

                //update temperature text
                double temperature =(double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");

                //update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //update humidity text
                long humidity= (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b>" + humidity +  "%</html");

                //update windspeed text
                double windspeed= (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b>" + windspeed +  "km/h </html");
            }
       });
       add(searchButton);

        


    }

    // create images for our gui components
    private ImageIcon loadImage(String imagepath){
        try{
            // read the image file form the path given
            BufferedImage image = ImageIO.read(new File(imagepath));

            //return the image icon so our component can render it
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;

    }


}