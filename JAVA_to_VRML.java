import java.io.*;
public class VRML 
{
	public static void main(String[] args)
	{
		try{
			PrintWriter pw = new PrintWriter
			(new BufferedWriter(new FileWriter("Test.wrl")));
			
			
			
			pw.close();
		}
		catch(IOException e){
			System.out.println("入出力エラー");
		}
	}
}
