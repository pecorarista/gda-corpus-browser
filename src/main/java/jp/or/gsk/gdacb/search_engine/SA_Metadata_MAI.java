package jp.or.gsk.gdacb.search_engine;

import jp.or.gsk.gdacb.E_Corpus;
import java.io.File;
import java.util.TreeSet;

public class SA_Metadata_MAI extends SA_Metadata {

	public SA_Metadata_MAI(File text_file, File sa_file) {
		super(text_file, sa_file);
		this.number_of_gda_files = E_Corpus.MAINITI.numberOfFile();
	}
	public SA_Metadata_MAI(String text_file_name,String sa_file_name){
		super(text_file_name, sa_file_name);
		this.number_of_gda_files = E_Corpus.MAINITI.numberOfFile();
	}
	
	public static void main(String[] args) {
		//String key = "みんなの広場";
		//String key = "学生の飲酒";
		//String key = "の";
		//String key = "00972390";
		String key = "00000240";
		//String key = "00";
		SA_Metadata_MAI sa_test = new SA_Metadata_MAI("index/mainiti_md.mer","index/mainiti_md.idx");
		try {
			System.out.println("Start to read index");
			sa_test.open();
			sa_test.create_line_index();
			System.out.println("Finish to read index");
			sa_test.consult(key);
			System.out.println("hit number: "+sa_test.hit_number());
			//int tmp = (sa_test.hit_number() > 10) ? 10 : sa_test.hashCode();
			//for(int i=0 ; i < tmp ; i++) System.out.print(i+":"+sa_test.position_at_text(i)+":"+sa_test.fileID(i)+" ");
			//System.out.print("\n");
			//TreeSet<Integer> hit_file_ID = sa_test.fileID_set();
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
