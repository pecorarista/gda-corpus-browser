package jp.or.gsk.gdacb.search_engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class FilelistFile {
	private String filelist_file_name = null;
	private String[] file_name_array;
	private int number_of_files;
	private BufferedReader in = null;		
	// constructor
	FilelistFile (String file,int line_num) {
		this.filelist_file_name = file;
		this.file_name_array = null;
		this.number_of_files = line_num;
	}
	
	void read() throws SE_Exception {
		String s;
		int line_no;
		
		this.file_name_array = new String[this.number_of_files];
		File f = new File(filelist_file_name);

		if(! f.isFile()){
			SE_Exception e = new SE_Exception();
			e.setMsgJ("ファイルリスト`"+f.getName()+"'が存在しません");
			e.setMsgE("FATAL ERROR: FILELIST file `"+f.getName()+"' does not exist");
			throw e;
		}
		try {
			in = new BufferedReader(new FileReader(filelist_file_name));
			line_no = 0;
			while( (s=in.readLine()) != null ) {
				file_name_array[line_no] = s;
				line_no++;
			}
		} catch (IOException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("ファイルリスト`"+f.getName()+"'の読み込みに失敗しました");
			e2.setMsgE("FATAL ERROR: Fail to read FILELIST file `"+f.getName()+"'");
			throw e2;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {}
		}
		//System.out.println("DEBUG: file list is read (FilelistFile.class)");
	}
	
	String filename (int id) {
		if(id >= 0 && id < file_name_array.length) {
			return file_name_array[id];
		}else{
			return null;
		}
	}
}
