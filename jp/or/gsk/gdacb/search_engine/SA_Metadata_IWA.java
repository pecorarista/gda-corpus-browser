package jp.or.gsk.gdacb.search_engine;

import jp.or.gsk.gdacb.E_Corpus;
import java.io.File;
import java.util.TreeSet;

public class SA_Metadata_IWA extends SA_Metadata {
	
	public SA_Metadata_IWA(File text_file, File sa_file) {
		super(text_file, sa_file);
		this.number_of_gda_files = E_Corpus.IWANAMI.numberOfFile();
	}
	public SA_Metadata_IWA(String text_file_name,String sa_file_name){
		super(text_file_name, sa_file_name);
		this.number_of_gda_files = E_Corpus.IWANAMI.numberOfFile();
	}
	
	/**
	 * SA_Metadata#fileID_set() メソッドとほとんど同じだが、
	 * 検索キーと見出しが完全に一致しているファイルIDだけを抽出する
	 */
	public TreeSet<Integer> fileID_set_midasi_exact_match() throws SE_Exception {
		int posit, file_id, key_len;
		TreeSet<Integer> file_ID_set = new TreeSet<Integer>();
		if(cONSULT_hit_number == -1) return null;
		key_len = key_length();
		for(int i=0 ; i < cONSULT_hit_number ; i++){
			posit = position_at_text(i);
			file_id = fileID(i);
			if((posit > 0 && byte_text[posit-1] == 9) && // == '\t'
			   (byte_text[posit+key_len] == 10 ||	// == '\n'
			    byte_text[posit+key_len] == 91)){	// == '['
				file_ID_set.add(file_id);
			}
		}
		return file_ID_set;
	}
	
	public static void main(String[] args) {
		//String key = "の";
		//String key = "んで";
		//String key = "ん";
		String key = "00001";
		//String key = "00012-1";
		//String key = "56257";
		//String key = "00";
		SA_Metadata_IWA sa_test = new SA_Metadata_IWA("index/iwanami_md.mer","index/iwanami_md.idx");
		try {
			System.out.println("Start to read index");
			//sa_test.set_target_corpus(E_Corpus.MAINITI);
			sa_test.open();
			sa_test.create_line_index();
			System.out.println("Finish to read index");
			sa_test.consult(key);
			System.out.println("hit number: "+sa_test.hit_number());
			//int tmp = (sa_test.hit_number() > 10) ? 10 : sa_test.hashCode();
			//for(int i=0 ; i < tmp ; i++) System.out.print(i+":"+sa_test.position_at_text(i)+":"+sa_test.fileID(i)+" ");
			//System.out.print("\n");
			
			//TreeSet<Integer> hit_file_ID = sa_test.fileID_set();
			//TreeSet<Integer> hit_file_ID = sa_test.fileID_set_midasi_exact_match();
			TreeSet<Integer> hit_file_ID = sa_test.fileID_set_entryID_exact_match();
			System.out.println("hit file number: "+hit_file_ID.size());
			int tmp = (hit_file_ID.size() > 10) ? 10 : hit_file_ID.size();
			int ct = 0;
			for(Integer id: hit_file_ID){
				System.out.println(id+" "+sa_test.extract_metadata(id));
				ct++;
				if(ct >= tmp) break;
			}
			sa_test.close();
		} catch (SE_Exception e) {
			e.printStackTrace();
			System.err.println(e.MsgJ);
		}
	}

}
