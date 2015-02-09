package nezz.dreambot.accountcreate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.dreambot.core.Instance;

public class AccountCreate {
	private String url = "https://secure.runescape.com/m=account-creation/g=oldscape/create_account_funnel.ws";

	private Instance instance;
	public AccountCreate(Instance instance){
		this.instance = instance;
	}
	
	public int makeAccount(String dispName, String email_, String password, String age_) throws IOException{
		String urlParameter = "";
		urlParameter+="onlyOneEmail=1&";
		String age = "age="+age_+"&";
		urlParameter+=age;
		urlParameter+="displayname_present=true&";
		String displayName = "displayname="+dispName+"&";
		urlParameter+=displayName;
		String email = "email1="+email_.replace("@", "%40")+"&";//%40==@
		urlParameter+=email;
		String password1 = "password1="+password+"&";
		String password2 = "password2="+password+"&";
		urlParameter+=password1;
		urlParameter+=password2;
		urlParameter+="agree_email=on&";
		urlParameter+="agree_pp_and_tac=1&";
		urlParameter+="submit=Join+Now";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
		//String USER_AGENT = userAgents[5];
		String USER_AGENT = instance.getUserAgent();// userAgents[rand.nextInt(userAgents.length)];//"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
		System.out.println("User agent: " + USER_AGENT);
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Host", "secure.runescape.com");
		con.setRequestProperty("User-Agent",USER_AGENT);
		con.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		con.setRequestProperty("Accept-Language", "en-US,en);q=0.5");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		con.setRequestProperty("Referer","https://secure.runescape.com/m=account-creation/g=oldscape/create_account_funnel.ws");
		//Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameter);
		wr.flush();
		wr.close();
		
		//get response code
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameter);
		System.out.println("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while((inputLine = in.readLine()) != null){
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
		if(response.toString().contains("Account Created"))
			return 0;
		else
			return 1;
	}


}
