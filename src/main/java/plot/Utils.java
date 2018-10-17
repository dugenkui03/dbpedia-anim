package plot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Utils {


	public static int delDir(String path){
		File f;
		try {
			f=new File(path);

			if(!f.delete()&&f.isDirectory()){//如果是文件夹，递归调用
				String files[]=f.list();
				String dirPath=f.getAbsolutePath();
				for (int i = 0; i < files.length; i++) {
					delDir(dirPath+"\\"+files[i]);
				}
				f.delete();
			}
			else f.delete();//如果不是文件夹，则直接删除
		} catch (Exception e) {
			System.out.println("覆盖知识库时错误");
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	public static void copyDir(String source,String target){
		File sou,tar;
		try {
			sou=new File(source);//源文件夹
			tar=new File(target);//目标文件夹
			File[] fs=sou.listFiles();
			if(!tar.exists()){
				tar.mkdirs();//目标文件夹不存在，则创建
			}
			for (File f : fs) {
				if(f.isFile()){//如果是文件
					fileCopy(f.getPath(),target+"\\"+f.getName());
				}
				else{//如果是文件夹
					copyDir(f.getPath(),target+"\\"+f.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("复制文件夹时出错");
		}
	}

	private static void fileCopy(String src,String des){
		BufferedInputStream br=null;
		BufferedOutputStream ps =null;
		try {
			br=new BufferedInputStream(new FileInputStream(src));//输入流
			ps=new BufferedOutputStream(new FileOutputStream(des));//输出流
			byte[] bys=new byte[1024];
			int len=0;
			while((len=br.read(bys))!=-1){
				ps.write(bys,0,len);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("复制文件夹时出错");
		}finally{
			try{
				if(br!=null)br.close();
				if(ps!=null)ps.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Utils.delDir("c:\\ontologyOWL\\AllOwlFile\\sumoOWL2");
		Utils.copyDir("c:\\ontologyOWL\\sumoOWL2", "c:\\ontologyOWL\\AllOwlFile\\sumoOWL2");
	}
}
