package jp.or.gsk.gdacb.search_engine;

// 検索の結果得られたキーワード
// 形態素情報、KWICなどの表示結果を格納する
public class RetrievedKeyword {
	public String keyword_in_text;
	public String POS, conj, base, yomi, sense;
	public String left_context, right_context;
	public String gda_file;
	public int posit_at_gda_file, length_in_gda_file;

	// Fields below are used in RetrievedKeyword_MAI
	public String artID;
	
	// Fields below are used in RetrievedKeyword_MAI
	public String midasi;
	
	void set_keyword (String s){
		keyword_in_text = s;
	}
	void set_POS (String s){
		POS = s;
	}
	void set_CONJ (String s){
		conj = s;
	}
	void set_BASE (String s){
		base = s;
	}
	void set_YOMI (String s){
		yomi = s;
	}
	void set_SENSE (String s){
		sense = s;
	}
	void set_left_context (String s){
		left_context = s;
	}
	void set_right_context (String s){
		right_context = s;
	}
	void set_gda_file (String s){
		gda_file = s;
	}
	void set_posit_at_gda_file (int p){
		posit_at_gda_file = p;
	}
	void set_length_in_gda_file (int l){
		length_in_gda_file = l;
	}
	
	// Methods below will be overridden by RetrievedKeyword_MAI 
	void set_artID(String s){ }
	
	// Methods below will be overridden by RetrievedKeyword_IWA
	void set_midasi(String s){ }
}

