package jp.or.gsk.gdacb.search_engine;

import java.io.File;

import jp.or.gsk.gdacb.E_Corpus;

public class SearchEngine_MAI extends SearchEngine {
	
	// constructor
	public SearchEngine_MAI(String dir) throws SE_Exception {
		super(dir);
		this.corpus_type = E_Corpus.MAINITI;
		File f = new File(dir+File.separator+"a01-1"+File.separator+"00000240.gda");
		if( ! f.exists() ){
			SE_Exception e = new SE_Exception();
			e.setMsgJ("CODE: illegal directory");	// GUIでメッセージを変更する
			e.setMsgE("GDA directory is illegal");
			throw e;
		}
	}	
	
	public void read_file_list () throws SE_Exception {
		if(this.filelist != null) return;
		reread_file_list();
	}
	public void reread_file_list() throws SE_Exception {
		this.filelist = new FilelistFile(
							E_Corpus.MAINITI.indexBaseFilename()+"_fn.txt",
							E_Corpus.MAINITI.numberOfFile() );
		this.filelist.read();
	}
	public void open_surface_index() throws SE_Exception {
		if(this.hw_idx != null) return;
		reopen_surface_index();
	}
	public void reopen_surface_index() throws SE_Exception{
		this.hw_idx = new IndexFile(
							E_Corpus.MAINITI.indexBaseFilename()+"_hw.key",
							E_Corpus.MAINITI.indexBaseFilename()+"_hw.idx" );
		this.hw_idx.open();
	}
	public void open_base_index() throws SE_Exception {
		if(this.bs_idx != null) return;
		reopen_base_index();
	}
	public void reopen_base_index() throws SE_Exception {
		this.bs_idx = new IndexFile(
							E_Corpus.MAINITI.indexBaseFilename()+"_bs.key",
							E_Corpus.MAINITI.indexBaseFilename()+"_bs.idx" );
		this.bs_idx.open();
	}
	public void open_suffix_array() throws SE_Exception {
		if(this.sa == null){
			reopen_suffix_array();
		}else if(this.sa.byte_text_len < 0){
			reopen_suffix_array();
		}else if(this.sa.bin_sa == null || this.sa.bin_sa.size_of_suffix_array < 0){
			reopen_suffix_array();
		}
		return;
	}
	public void reopen_suffix_array() throws SE_Exception {
		this.sa = new SA_FullText(
						E_Corpus.MAINITI.indexBaseFilename()+"_sa.mer",
						E_Corpus.MAINITI.indexBaseFilename()+"_sa.idx" );
		this.sa.open();
	}
	
	
	@Override
	protected GDA_File create_GDA (String filepath) {
		return new GDA_File_MAI(filepath);
	}
	@Override
	protected RetrievedKeyword[] create_kw_list (int size) {
		return new RetrievedKeyword_MAI[size];
	}
	@Override
	protected RetrievedKeyword create_kw () {
		return new RetrievedKeyword_MAI();
	}
}
