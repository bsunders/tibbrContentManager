package ben.tibbr.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

public class TibbrContentManager {
	
	

	private String username;
	private String password;
	private String urlBase;
	private String auth_token=null;
	private String client_key=null;
	
	
public TibbrContentManager(String _urlbase, String _username, String _password){
		
		this.urlBase=_urlbase;
		this.username=_username;
		this.password=_password;
		client_key=UUID.randomUUID().toString();
		
}

// logs in as specified user and sets the class member "auth_token" which is needed for all subsequent API calls
public Boolean loginUser() throws UnsupportedEncodingException{
	
	DefaultHttpClient httpClient = new DefaultHttpClient();
	System.out.println("URL: "+urlBase+"/a/users/login.xml");  // THIS IS A POST
	System.out.println("client_key: " + this.client_key); 
	HttpPost postRequest = new HttpPost(urlBase+"/a/users/login.xml");
	
	StringEntity input = new StringEntity("params[login]=" + URLEncoder.encode(this.username,"UTF-8") + 
            							  "&params[password]=" + URLEncoder.encode(this.password,"UTF-8") + 
            							  "&client_key=" + URLEncoder.encode(client_key,"UTF-8"));

	input.setContentType("application/x-www-form-urlencoded");
	postRequest.setEntity(input); // attach params to request
	
	try {
		HttpResponse response = httpClient.execute(postRequest); // execute POST
		String body = readStream(response.getEntity().getContent()); // get text output from response
		Document xmlDoc = parseXml(body); // convert to xml doc
		
		if (xmlContainsError(xmlDoc)){
			System.out.println("Login Failed. Please check account details and try again.\n");
			return false;
		}else
		{
			Node auth_token_node = firstElementByTag(xmlDoc, "auth-token");
			this.auth_token=auth_token_node.getNodeValue();
			System.out.println("Login Successful with auth token:"+ auth_token.toString());
			return true;
		}
		
		
		
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalStateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;
	
	
	
	
}



public void createMessages(String message){
	
	// call this to get all the user IDs in to hashmap.
	//getAllUsers();
	URL server;
	try {
		server = new URL(this.urlBase+"/a"+"/messages.xml");   // POST
		
		HttpURLConnection conn= (HttpURLConnection)server.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("content-type", "text/xml");

		System.out.println("auth_token "+auth_token);
		System.out.println("client_key "+client_key);
		conn.setRequestProperty("auth_token", this.auth_token);
		conn.setRequestProperty("client_key", this.client_key);

		
		OutputStream out = conn.getOutputStream();
		out.write(message.getBytes());

		out.flush();
		
		out.close();
		System.out.println(conn.getResponseCode());
		
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

public void createSubjectsFromFile(String csvFilename) throws InterruptedException{
	
	
	URL server;
	try {
		
		CSVReader csvReader = new CSVReader(new FileReader(csvFilename)); // updated to pipe delimiter
		String[] row = null;
		
		while((row = csvReader.readNext()) != null) {		

			server = new URL(this.urlBase+"/a"+"/subjects.xml");
			HttpURLConnection conn= (HttpURLConnection)server.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("content-type", "text/xml");
			conn.setRequestProperty("auth_token", this.auth_token);
			conn.setRequestProperty("client_key", this.client_key);
			
			//String subjectOwnerID = getUserIDfromUserName(row[1]);
			OutputStream out = conn.getOutputStream();
	 
			
//			<?xml version="1.0" encoding="UTF-8"?>
//            <subject>
//            <name>testt4</name># To create a child subject, specify the fully
//            qualified name, for example, testt4.childsubjectname
//            <description></description>
//            <scope>public</scope>
//            <allow-children>1</allow-children>
//            <user-id type="integer">52</user-id>
//            </subject>
            
			//  sysname, desc, display name, security
            //  CMT4,X Y Z,test name,public
			// note subjects need at least 3 chars
			
			
			 
			String subjName = row[0];
			//String subjDesc = row[1];
			String subjDisplayName = row[1];
			String subjSecurity = row[2];

			
			
	// -----------  HLB subject load specific code START		
			
			// remove traliing "." from name and displayname
			if (subjName.substring(subjName.length() - 1).equals(".")) 	
				subjName = subjName.substring(0, subjName.length() - 1 );
			
			if (subjDisplayName.substring(subjDisplayName.length() - 1).equals(".")) 	
				subjDisplayName = subjDisplayName.substring(0, subjDisplayName.length() - 1 );
			
			// grab last display name if more than 1
			String[] arrDisplayName =  subjDisplayName.split("\\.");
			
			if (arrDisplayName.length > 1)	
				subjDisplayName = arrDisplayName[arrDisplayName.length -1];
			
			// replace & with &amp;
			subjDisplayName = subjDisplayName.replace("&", "&amp;");
			subjDisplayName = subjDisplayName.replace("|", ",");
			
	// -----------------  HLB subject load specific code END
			
			
			String message="<subject>" + 
	            "<name>"+subjName+"</name>" +
	            "<description></description>"+
	            "<scope>"+subjSecurity+"</scope>" + 
	           "<render_name>"+subjDisplayName+"</render_name>" + 
	            "<allow-children>1</allow-children>"+     // note you cant assign owner other than logged in user
	            "<user-id>1</user-id>"+
	            "</subject>";
	            
			System.out.println(message);
			out.write(message.getBytes());
			out.flush();
			
			out.close();
			
			Integer retVal = conn.getResponseCode();
			
			String error = conn.getResponseMessage();
			
			if (retVal.toString().startsWith("2")){
				System.out.println("Successfully created subject");  //  201  = success
				Thread.sleep(500);
			}
			else
				System.out.println("Failed to create subject with error: " + error);   // 422 (subject not created ) 
															
		
		}
		csvReader.close();
		System.out.println("Finished Creating Subjects.");
		System.out.println("---------------------------");
		
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}



public void createUsersFromFile(String csvFilename){
	
	
	URL server;
	try {
		
		CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
		String[] row = null;
		
		while((row = csvReader.readNext()) != null) {		

			//server = new URL(this.urlBase+"/a"+"/users.xml?params[activate_user]=true");
			//dont_notify
			server = new URL(this.urlBase+"/a"+"/users.xml?params[dont_notify]=true");
			
			HttpURLConnection conn= (HttpURLConnection)server.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("content-type", "text/xml");
	
			//System.out.println("auth_token "+auth_token);
			//System.out.println("client_key "+client_key);
			conn.setRequestProperty("auth_token", this.auth_token);
			conn.setRequestProperty("client_key", this.client_key);
	
			OutputStream out = conn.getOutputStream();
			
			//bsunderl,password,Ben,Sunderland,bsunderl@tibco.com
			String message="<user>"+
            		"<login>"+row[0]+"</login>"+
            		"<password>"+row[1]+"</password>"+
            		"<password-confirmation>"+row[1]+"</password-confirmation>"+
            		"<email>"+row[4]+"</email>"+
            		"<first-name>"+row[2]+"</first-name>"+
            		"<last-name>"+row[3]+"</last-name>"+
            		"</user>";
				
			System.out.println(message);
			out.write(message.getBytes());
			out.flush();
			out.close();
			
			Integer result = conn.getResponseCode();
			
			if (result.toString().startsWith("2"))
				System.out.println("Created user successfully.");
			else// 422 (user not created - already exists ? 
				System.out.println("ERROR - Couldn't create user - please check input details.");											// or 201  ??
		
		}
		csvReader.close();
		System.out.println("Finished Creating users");
		System.out.println("---------------------------");
		
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

/**
 * readStream
 * @param is
 * @return
 * @throws IOException
 */
private String readStream(InputStream is) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(is));

         StringBuffer sb = new StringBuffer();
         String output;
         while ((output = br.readLine()) != null) {
             sb.append(output);
         }   
         return sb.toString();
 }

/**
 * parseXml
 * @param xml
 * @return
 */
private Document parseXml(String xml) {
    InputSource is = new InputSource(new StringReader(xml));
    DocumentBuilderFactory builderFactory =  DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    
    try {
            builder = builderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
            e.printStackTrace();  
    }

    try {
            return  builder.parse(is);
    } catch (SAXException e) {
            e.printStackTrace();
    } catch (IOException e) {
            e.printStackTrace();
    }
    return null;
}


/**
 * xmlContainsError
 * @param d
 * @return
 */
private Boolean xmlContainsError(Document doc){
	NodeList list = doc.getElementsByTagName("Error");
	if (list.getLength() == 0)  
        return false;
	else
		return true;
	
}



/**
 * firstElementByTag
 * @param d
 * @param tag
 * @return
 */
private Node firstElementByTag(Document d, String tag) {
    NodeList list = d.getElementsByTagName(tag);
    if (list == null) {
            return null;
    }
    Element e = (Element)list.item(0);
    list = e.getChildNodes();
    if (list == null) {
            return null;
    }
    return list.item(0);
}



}