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

			if(!f.delete()&&f.isDirectory()){//������ļ��У��ݹ����
				String files[]=f.list();
				String dirPath=f.getAbsolutePath();
				for (int i = 0; i < files.length; i++) {
					delDir(dirPath+"\\"+files[i]);
				}
				f.delete();
			}
			else f.delete();//��������ļ��У���ֱ��ɾ��
		} catch (Exception e) {
			System.out.println("����֪ʶ��ʱ����");
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	public static void copyDir(String source,String target){
		File sou,tar;
		try {
			sou=new File(source);//Դ�ļ���
			tar=new File(target);//Ŀ���ļ���
			File[] fs=sou.listFiles();
			if(!tar.exists()){
				tar.mkdirs();//Ŀ���ļ��в����ڣ��򴴽�
			}
			for (File f : fs) {
				if(f.isFile()){//������ļ�
					fileCopy(f.getPath(),target+"\\"+f.getName());
				}
				else{//������ļ���
					copyDir(f.getPath(),target+"\\"+f.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�����ļ���ʱ����");
		}
	}

	private static void fileCopy(String src,String des){
		BufferedInputStream br=null;
		BufferedOutputStream ps =null;
		try {
			br=new BufferedInputStream(new FileInputStream(src));//������
			ps=new BufferedOutputStream(new FileOutputStream(des));//�����
			byte[] bys=new byte[1024];
			int len=0;
			while((len=br.read(bys))!=-1){
				ps.write(bys,0,len);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�����ļ���ʱ����");
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
