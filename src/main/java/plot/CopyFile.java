package plot;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import java.util.logging.Logger;
//import org.apache.log4j.Logger;

public class CopyFile {

	private static SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
	public  static Logger logg = Logger.getLogger(CopyFile.class.getName());
	private String logName = new String ("ADLLog"+date.format(new Date()) + ".txt");
	private File file = new File(logName);


	public static String NewFile()
	{
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
		String s=df.format(new Date());
		String d="d:/";
		d=d.concat(s);
		return d;
	}

	public static void Copy(String url1,String url2) throws IOException
	{

		File f=new File(url1);
		if(f.isDirectory())
		{
			copyDirectiory(url1,url2);
		}

	}

	public static void copyFile(File sourceFile,File targetFile) throws IOException
	{
		// 新建文件输入流并对它进行缓冲
		FileInputStream input=new FileInputStream(sourceFile);
		BufferedInputStream inBuff=new BufferedInputStream(input);
		// 新建文件输出流并对它进行缓冲
		FileOutputStream output =new FileOutputStream(targetFile);
		BufferedOutputStream outBuff=new BufferedOutputStream(output);
		// 缓冲数组
		byte[] b=new byte[1024*5];
		int len;
		while((len=inBuff.read(b))!=-1)
		{
			outBuff.write(b,0,len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();
		//关闭流
		inBuff.close();
		outBuff.close();
		input.close();
		output.close();
	}

	public static void copyDirectiory(String sourceDir,String targetDir) throws IOException
	{
		(new File(targetDir)).mkdirs();
		File[] file=(new File(sourceDir)).listFiles();
		for(int i=0;i<file.length;i++)
		{
			if(file[i].isFile())
			{
				File sourceFile=file[i];
				File targetFile=new File(new File(targetDir).getAbsolutePath()+File.separator+file[i].getName());
				copyFile(sourceFile,targetFile);
			}
			if(file[i].isDirectory())
			{
				// 准备复制的源文件夹
				String dir1=sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2=targetDir + "/"+ file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}
	public static void createFile(String source) throws IOException
	{
		CopyFile f1=new CopyFile();
		String target=f1.NewFile();
		//	String source="C:/ontologyOWL/sumoOWL2";
		f1.Copy(source,target);
		//	target=target.concat("/sumo_phone3.owl");
		//	return target;

	}


	public static void main(String args[]) throws IOException
	{
			/*file f1=new file();
			String target=f1.NewFile();
			String source="C:/ontologyOWL/sumoOWL2";
			f1.Copy(source,target);*/

	}

	public  void Log(String str){

		try {
			FileHandler fhandler = new FileHandler(logName);
			fhandler.setFormatter(new SimpleFormatter());
			logg.setUseParentHandlers(false);
			logg.addHandler(fhandler);
			logg.info("--> "+str);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

