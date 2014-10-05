import com.jaunt.*;
import com.jaunt.component.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * program to download resumes from bric.postech.ac.kr
 * @author anish
 *
 */
public class crawl {
	
	 public static void main(String[] args){
		
		 /**
		  * Start by downloading the PDF file, we check to see if it is able to be downloaded, otherwise download HTML resume
		  */
		 String dirName = "/Users/anish/data/"; //where the PDF files will be saved
		 try {
			 String base = "http://bric.postech.ac.kr/myboard/view.php?Board=hbs_author&filename=cvfile_";
			 
			 String base2 = ".pdf&id=";
			 
			 String endBase = "&mode=down&type=cvf"; //we dynamically construct the URL using the ID of the professor
				 System.out.println("Downloading ...");
			  for(int i = 100; i < 2501; i++) //change the 100 to whatever number, it corresponds to the ID of the professor
			  {
					 String finalURL = base + encodeX(i) + base2 + i + endBase; //dynamically construct final URL
					 System.out.println(finalURL);
					
					 saveFileWithURL(
							  dirName + i + ".pdf", i, 
							  finalURL); //save the PDF to file
				  
			  }
			  
			  
			  System.out.println("Downloads Complete!");
			  
			  
			  
			  
			  
			  } catch (MalformedURLException e) {
			  e.printStackTrace();
			  System.out.println("not downloaded");
			  } catch (IOException e) {
			  e.printStackTrace();
			  System.out.println("not downloaded");
			  }

		  }
	 /**
	  * Function to save the HTML resume
	  * @param id
	  */
	 public static void saveHTMLResume(int id)
	 {
		 try{
		      UserAgent userAgent = new UserAgent();
		      String base = "http://bric.postech.ac.kr/hanbitsa/treatise_index_for_author.php?idauthorid=";
		      userAgent.visit(base + id);  //open the first page for the professor
		      Elements elements = userAgent.doc.findEvery("<a href"); //find every link on the page
		      String result = null;
		      for(Element table : elements){                               //iterate through Results
		    	  if(check(table)){
		    	  System.out.println(table.outerHTML() + "\n----");
		    	  		result = table.outerHTML(); break;}    // the variable result is now equal to the link that opens to the next page for the professor (this is where their HTML resume is)
		    	  } 
		      
		      int charOfEqual = 0; //we have to find the ID number of the professor (every url has an ID number other than the regular ID of the professor)
		      for (int i = -1; (i = result.indexOf('=', i + 1)) != -1; ) {
		    	    if(i > 7){
		    	    charOfEqual = i; //use the URL and find the ID, which is located after the '=' sign in the URL, we look for the second '=' which is after the index 7 of the URL, very hackish method

		    	    break; }
		    	    
		    	}
		      StringBuilder sb = new StringBuilder(7);
		      for (int i = charOfEqual+1; i < result.length(); i++){
		    	 
		    		 if(Character.isDigit(result.charAt(i)))
		    		 {
		    			 sb.append(result.charAt(i)); //we iterate through the URL and stop after we obtain the ID, we save it to a string
		    		 }
		    		 else{
		    			 break;
		    		 }
		    	  
		    	}
		     
		    	  String resumeBase = "http://bric.postech.ac.kr/hanbitsa/author_cvpop.php?id=";
		    	  String resumeBaseSecond = "&Board=hbs_treatise&ttype=0&idauthorid=";
		    	  userAgent.visit(resumeBase + sb.toString() + resumeBaseSecond + id); //visit the HTML resume page of the professor, using the previously obtained ID
		    	  File file = new File(id + "-HTMLresume.txt");
					if (!file.exists()) {
						file.createNewFile(); //create a new file for the resume and save it
					}
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(userAgent.doc.innerHTML()); //write the HTML page to file
					// TODO Automate the process and obtain relevant information 
					bw.close();
					System.out.println("Downloaded HTML Resume!");
		    }
		    catch(JauntException e){
		      System.err.println(e);
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Unable to download the HTML resume");
			}
	 }
	 /**
	  * Saves a PDF file from the given URL and if the PDF file is not available, calls the download HTML resume function instead
	  * @param fileName
	  * @param i
	  * @param fileUrl
	  * @throws MalformedURLException
	  * @throws IOException
	  */
	 public static void saveFileWithURL(String fileName, int i, String fileUrl)
			 throws MalformedURLException, IOException {
			 BufferedInputStream in = null;
			 FileOutputStream fout = null;
			 try {
			 in = new BufferedInputStream(new URL(fileUrl).openStream());
			 fout = new FileOutputStream(fileName);
			 
			byte data[] = new byte[1024];
			 int count = 0;
			 while ((count = in.read(data, 0, 1024)) != -1) {
			 if(count == 121)
			 {
				 System.out.println("NOT DOWNLOADED, trying to download HTML resume instead");
				
				 saveHTMLResume(i);
			 }
			 else{
				 fout.write(data, 0, count);

			 }

			 }
			 } finally {
			 if (in != null)
			 in.close();
			 if (fout != null)
			 fout.close();
			 }
			 }
	 /**
	  * Simple function to help narrow down the links I get from the page, it allows me to only focus on the link I need by cutting out links that don't have the keyword I am looking for
	  * @param element
	  * @return
	  */
	 public static boolean check(Element element)
	 {
		 String test = element.outerHTML();
		 if(test.contains("idauthorid="))
			 return true;
		return false;
	 }
	 /**
	  * Encodes the XXXXXX for the PDF URL depending on what the ID of the professor is
	  * @param num
	  * @return
	  */
	 public static String encodeX(int num)
	 {
		 String finalString;
		 
		 if( num < 10)
		 {
			 finalString = "000000";
		 }
		 else if (num > 9 && num < 100)
		 {
			 finalString = "00000";

		 }
		 else if (num > 99 && num < 1000)
		 {
			 finalString = "0000";

		 }
		 else 
		 {
			 finalString = "000";

		 }
		
		 return finalString + num;
	 }
	}

