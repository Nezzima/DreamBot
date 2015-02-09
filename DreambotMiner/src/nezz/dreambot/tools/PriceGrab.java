package nezz.dreambot.tools;

import java.io.*;
import java.net.*;

public class PriceGrab {


    private static PriceGrab oneInstance;
    private URL zybez;
    private URLConnection urlConnection;
    private BufferedReader inputScan;
    private final String zybezUrl = "http://forums.zybez.net/runescape-2007-prices/api/item/";

    public static PriceGrab getInstance(){
        if(oneInstance == null){
            oneInstance = new PriceGrab();
        }
        return oneInstance;
    }

    public int getPrice(String itemName, int command){
        final String AVERAGE = "average",LOW= "recent_high", HIGH="recent_low";
        String item = format(itemName),extracted;
        int price = 0;
        openStream(item);
        extracted = retrieveData(item);
        switch (command){
            case 1:
                return parseInfo(extracted,LOW);

            case 2:
                return parseInfo(extracted,AVERAGE);

            case 3:
               return parseInfo(extracted,HIGH);
        }
        return price;
    }

    private String format(final String string){
        if(string.contains(" "))
            return string.replaceAll(" ", "+");
        else
            return string;
    }

    private void openStream(final String param){
        String appended = zybezUrl.concat(param);

                try {
                	zybez = new URL(appended);
            		urlConnection = zybez.openConnection();
            		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            	}
                catch (MalformedURLException e) {
                    System.out.println("Web address formatted incorrectly, printing stack trace");
                    e.printStackTrace();
                }
                catch(IOException exception) {
                    System.out.println("Url connection has thrown an IOException, printing stack trace");
                    exception.printStackTrace();
                }
    }

    private String retrieveData(final String param){
        String output = null;
        try {
            openStream(param);
            InputStream is = urlConnection.getInputStream();
    		inputScan = new BufferedReader(new InputStreamReader(is));
    		output =  inputScan.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {inputScan.close();} catch (IOException e){e.printStackTrace();}
        }
        return output;
    }

    private int parseInfo(String extracted,String value){
        int start, end, price = 0;
        if(extracted != null && extracted.contains(value)){
            start = extracted.indexOf(value);
            end = extracted.indexOf(",",start);
            price = Integer.parseInt(extracted.substring(start, end).replaceFirst(".*?(\\d+).*", "$1"));
        }
        else
            System.out.println("Could not retrieve price");
        return price;
    }
}