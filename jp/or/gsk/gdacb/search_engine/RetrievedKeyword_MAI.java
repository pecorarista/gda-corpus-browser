package jp.or.gsk.gdacb.search_engine;

public class RetrievedKeyword_MAI extends RetrievedKeyword {
	@Override
	void set_artID (String fn) {
		int len = fn.length();
		int idx1 = len - 4;
		int idx2 = len - 12;
		if(idx2 >= 0 && fn.substring(idx1,len).equals(".gda") &&
		   (len == 12 || fn.substring(idx2-1,idx2).equals("/")) ){
			artID = fn.substring(idx2,idx2+8);
		}else{
			artID = null;
		}
	}
}
