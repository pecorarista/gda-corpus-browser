package jp.or.gsk.gdacb.search_engine;

import jp.or.gsk.gdacb.E_Corpus;
import jp.or.gsk.gdacb.E_KeywordType;
import jp.or.gsk.gdacb.gui.KWIC_TableModel;
import jp.or.gsk.gdacb.gui.KWIC_TableModel_MAI;
import jp.or.gsk.gdacb.gui.KWIC_TableModel_IWA;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

public class TSV_Exporter {
	/**
	 * 一括検索の入力ファイルの先頭の行
	 */
	private static final String first_line_of_batch_input = "# Keyword File for Batch Search";
	private File output_file;
	private E_Corpus corpus;
	private boolean print_header_flag = false;
	private boolean print_search_key_flag = false;
	private boolean print_sense_desc_flag = false;
	private boolean print_reverse_left_context_flag = true;
	private String output_coding = "Shift_JIS";
	private IwanamiDic iwa_dic = null;
	private static String line_sep = System.getProperty("line.separator");
	
	public TSV_Exporter(String fn,E_Corpus c) {
		this.output_file = new File(fn); 
		this.corpus = c;
	}
	public TSV_Exporter(File f,E_Corpus c) {
		this.output_file = f;
		this.corpus = c;
	}
	
	/**
	 * ヘッダーを出力するかを切りかえるメソッド
	 */
	public void setPrintHeader (boolean b) {
		this.print_header_flag = b;
	}
	/**
	 * 全ての列の第1カラムに検索キーを入れるのかを切りかえるメソッド
	 * 検索キーは export()メソッドの第2引数で与える
	 */
	public void setPrintSearchKey (boolean b) {
		this.print_search_key_flag = b;
	}
	/**
	 * 語義ID出力時に語釈文も出力する設定するメソッド
	 */
	public void enablePrintSenseDesc (IwanamiDic i){
		if(i==null){
			System.err.println("Argument IwanamiDic in enablePrintSenseDesc() is null");
			System.err.println("Sense description will not be printed");
			this.print_sense_desc_flag = false;
		}else{
			this.print_sense_desc_flag = true;
			this.iwa_dic = i;
		}
	}
	/**
	 * 語義ID出力時に語釈文を出力しないように設定するメソッド
	 */
	public void disablePrintSenseDesc (){
		this.print_sense_desc_flag = false;
	}
	/**
	 * 左文脈の逆順の文字列を出力するかを切り換えるメソッド
	 */
	public void setPrintReverseLeftContext (boolean b) {
		this.print_reverse_left_context_flag = b;
	}
	/**
	 * 出力文字コードを指定する
	 */
	public void setOutputCoding(String coding){
		this.output_coding = coding;
	}

	/**
	 * 検索結果をタブ区切りテキストに出力する
	 */
	public void export(RetrievedKeyword[] rkl,String key) throws SE_Exception {
		// rkl == RetrievedKeyword の list
		BufferedWriter out = null;

		if(rkl == null) {
			SE_Exception e = new SE_Exception();
			e.setMsgJ("キーワードが検索されていません");
			e.setMsgE("Empty keyword list is given");
			throw e;
		}
		try {
			out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(output_file), this.output_coding ) );
			output_body(out,rkl,key);
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("文字エンコードがサポートされていません");
			e2.setMsgE("Unsupported Encoding ERROR");
			throw e2;
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("出力ファイルの作成に失敗しました");
			e2.setMsgE("Fail to create output file");
			throw e2;
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {}
		}
	}
	public void export(RetrievedKeyword[] rkl) throws SE_Exception {
		export(rkl,null);
	}
	
	/**
	 * テーブルに表示されている検索結果をエクスポートするメソッド
	 */
	public void export(JTable tbl,String key) throws SE_Exception {
		BufferedWriter out = null;

		if(tbl == null) {
			SE_Exception e = new SE_Exception();
			e.setMsgJ("テーブルの実体がありません");
			e.setMsgE("Empty table is given in export_table()");
			throw e;
		}
		try {
			out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(output_file), this.output_coding ) );
			output_table_body(out,tbl,key);
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("文字エンコードがサポートされていません");
			e2.setMsgE("Unsupported Encoding ERROR");
			throw e2;
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("出力ファイルの作成に失敗しました");
			e2.setMsgE("Fail to create output file");
			throw e2;
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {}
		}
	}
	public void export(JTable tbl) throws SE_Exception {
		export(tbl,null);
	}
	
	/**
	 * 複数のキーワードに対して検索を繰り返し、検索結果を1つのTSVに出力するメソッド
	 */
	public void batch_search (SearchEngine se,File key_file,E_KeywordType type,int display_num,int left_len,int right_len) throws SE_Exception {
	    BufferedReader in = null;
		BufferedWriter out = null;
	    ArrayList<String> list_of_keyword = null;
	    String s = null;
	    
		if(se == null) {
			SE_Exception e = new SE_Exception();
			e.setMsgJ("検索エンジンが使用できません");
			e.setMsgE("Cannot use search engine");
			throw e;			
		}
		if(! key_file.isFile()){
			SE_Exception e = new SE_Exception();
			e.setMsgJ("キーワードファイル("+key_file.getName()+")が存在しません");
			e.setMsgE("Keyword file("+key_file.getName()+") does not exist");
			throw e;
		}
		// ファイルからキーワードのリストを読み込む
	    try {
		    in = new BufferedReader( new InputStreamReader( new FileInputStream(key_file),"JISAutoDetect") );
		    list_of_keyword = new ArrayList<String>();
		    s = in.readLine();
		    if(! s.equals(first_line_of_batch_input)){
		    	SE_Exception e = new SE_Exception();
		    	e.setMsgE("CODE: illegal format");	// 呼び出し側で処理をする
		    	e.setMsgJ("エラー: 入力ファイルの最初の行が不正です(詳細はヘルプを御覧下さい)");
		    	throw e;
		    }
		    while( (s=in.readLine()) != null ){
		    	if(! s.equals("") && ! s.startsWith("#"))
		    		list_of_keyword.add(s);
		    }
	     } catch (IOException e) {
//	        e.printStackTrace();
	    	 SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("キーワードファイル("+key_file.getName()+")の読み込みに失敗しました");
			e2.setMsgE("Fail to read keyword file("+key_file.getName()+")");
			throw e2;
	     } finally {
	        try {
	        	if (in != null) in.close();
	        } catch (IOException e) {}
	     }
	     // キーワードで検索をかけ、結果を出力
	     boolean prev_print_search_key_flag = print_search_key_flag;
	     print_search_key_flag = true;
	     try {
		     out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(output_file), this.output_coding ) );

		     if(print_header_flag) print_header(out);
	    	 for(String key: list_of_keyword){
	    		 // System.out.println("Keyword="+key);
	    		 RetrievedKeyword[] result = se.search(key, type, display_num, left_len, right_len);
	    		 // System.out.println("hit number="+se.hit_num());
	    		 if( result == null ){
	    			 // ヒットしなかったとき
	    			 out.write(key);
	    			 out.write("\t");
	    			 out.write("0");
	    			 out.write(line_sep);
	    		 }else{
	    			 for(int i=0 ; i < result.length ; i++) {
	    				 output_one_line(out, result[i], i+1, key);
	    			 }
	    		 }
	    	 }
	     } catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("文字エンコードがサポートされていません");
			e2.setMsgE("Unsupported Encoding ERROR");
			throw e2;
	     } catch (FileNotFoundException e) {
//	    	 e.printStackTrace();
	    	 SE_Exception e2 = new SE_Exception();
	    	 e2.setMsgJ("出力ファイルの作成に失敗しました");
	    	 e2.setMsgE("Fail to create output file");
	    	 throw e2;
//	     } catch (SE_Exception e) {
//	    	 throw e;
	     } catch (IOException e) {
//			 e.printStackTrace();
	    	 SE_Exception e2 = new SE_Exception();
	    	 e2.setMsgJ("出力ファイルの作成に失敗しました");
	    	 e2.setMsgE("Fail to create output file");
	    	 throw e2;
	     } finally {
	    	 try {
	    		 if (out != null) out.close();
	    	 } catch (IOException e) {}
		     print_search_key_flag = prev_print_search_key_flag;
	     }
	}
	public void batch_search (SearchEngine se,String fn,E_KeywordType t,int n,int l,int r) throws SE_Exception {
		File f = new File(fn);
		batch_search(se,f,t,n,l,r);
	}
	
	private void output_body(BufferedWriter o,RetrievedKeyword[] rkl,String key) throws SE_Exception {
		// rkl == RetrievedKeyword の list
		try {
			// ヘッダの表示
			if(print_header_flag) print_header(o);
			for(int i=0 ; i < rkl.length ; i++){
				output_one_line(o,rkl[i],i+1,key);
			}
		} catch (SE_Exception e) {
			throw e;
		}
	}
	private void output_one_line(BufferedWriter o,RetrievedKeyword rk,int id,String key) throws SE_Exception {
		int i;
		Object[] obj;
		Class<?> c;
		try {
			if(print_search_key_flag) {
				o.write( (key == null) ? "NA" : key );
				o.write("\t");
			}
			if(this.corpus == E_Corpus.MAINITI){
				obj = KWIC_TableModel_MAI.get_row_object_list( (RetrievedKeyword_MAI)rk, id );
				if(print_sense_desc_flag){
					int idx_s = KWIC_TableModel_MAI.idx_of_sense; 
					String s = iwa_dic.addSenseDescToSEM((String)obj[idx_s]);
					if(s != null) obj[idx_s] = s;
				}
				for(i=0 ; i < obj.length ; i++){
					if(i != 0) o.write("\t");
					c = KWIC_TableModel_MAI.columnArray[i].columnClass;
					if(c == Integer.class){
						o.write( Integer.toString((Integer)obj[i]) );
					}else if(c == String.class ){
						o.write((String)obj[i]);
					}else{
						o.write( obj[i].toString() );
					}
				}
				if(print_reverse_left_context_flag){
					int idx_l = KWIC_TableModel_MAI.idx_of_left_context; 
					o.write('\t');
					o.write( gen_reverse_string( (String)obj[idx_l] ) );
				}
			}else{
				obj = KWIC_TableModel_IWA.get_row_object_list( (RetrievedKeyword_IWA)rk, id );
				if(print_sense_desc_flag){
					int idx_s = KWIC_TableModel_IWA.idx_of_sense; 
					String s = iwa_dic.addSenseDescToSEM((String)obj[idx_s]);
					if(s != null) obj[idx_s] = s;
				}
				for(i=0 ; i < obj.length ; i++){
					if(i != 0) o.write("\t");
					c = KWIC_TableModel_IWA.columnArray[i].columnClass;
					if(c == Integer.class){
						o.write( Integer.toString((Integer)obj[i]) );
					}else if(c == String.class ){
						o.write((String)obj[i]);
					}else{
						o.write( obj[i].toString() );
					}
				}
				if(print_reverse_left_context_flag){
					int idx_l = KWIC_TableModel_IWA.idx_of_left_context; 
					o.write('\t');
					o.write( gen_reverse_string( (String)obj[idx_l] ) );
				}
			}
			o.write(line_sep);
		} catch (IOException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("検索結果のエクスポートに失敗しました");
			e2.setMsgE("Fail to export TSV file in output_one_line()");
			throw e2;
		}
	}

	private void output_table_body(BufferedWriter o,JTable tbl,String key) throws SE_Exception {
		int row, col, model_col, idx_s, idx_l, idx_disp_l = 0;
		Class<?> c;
		try {
			TableColumnModel tcm = tbl.getColumnModel();
			KWIC_TableModel tm = (KWIC_TableModel) tbl.getModel();
			
			idx_s =	(this.corpus == E_Corpus.MAINITI) ?
					KWIC_TableModel_MAI.idx_of_sense :  KWIC_TableModel_IWA.idx_of_sense;
			
			// ヘッダの表示
			if(print_header_flag) print_header_in_table(o,tbl);
			
			for(row=0 ; row < tbl.getRowCount() ; row++){
				if(print_search_key_flag) {
					o.write( (key == null) ? "NA" : key );
					o.write("\t");
				}
				for(col=0 ; col < tcm.getColumnCount() ; col++){
					if(col != 0) o.write("\t");
					model_col = tcm.getColumn(col).getModelIndex();
					c = tm.getColumnClass(model_col);
					if(print_sense_desc_flag && model_col == idx_s){
						String s = iwa_dic.addSenseDescToSEM( (String)tbl.getValueAt(row,col) );
						if(s != null){
							o.write(s);
						}else{
							o.write( (String)tbl.getValueAt(row,col) );
						}
					}else if(c == Integer.class){
						o.write( Integer.toString((Integer)tbl.getValueAt(row,col)) );
					}else if(c == String.class){
						o.write( (String)tbl.getValueAt(row,col) );
					}else{
						o.write( tbl.getValueAt(row,col).toString() );
					}
				}
				if(print_reverse_left_context_flag){
					idx_l =	(this.corpus == E_Corpus.MAINITI) ?
							KWIC_TableModel_MAI.idx_of_left_context :  KWIC_TableModel_IWA.idx_of_left_context;
					for(col=0 ; col < tcm.getColumnCount() ; col++){
						if(tcm.getColumn(col).getModelIndex() == idx_l){
							idx_disp_l = col;
							break;
						}
					}
					o.write('\t');
					o.write( gen_reverse_string( (String)tbl.getValueAt(row,idx_disp_l) ) );
				}
				o.write(line_sep);
			}
		} catch (IOException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("検索結果のエクスポートに失敗しました");
			e2.setMsgE("Fail to export TSV file in output_one_line()");
			throw e2;
		}
	}
	
	private void print_header(BufferedWriter o) throws SE_Exception {
		int i;
		try {
			if(print_search_key_flag)
				o.write("検索キー\t");
			if(this.corpus == E_Corpus.MAINITI) {
				for(i=0 ; i < KWIC_TableModel_MAI.columnArray.length ; i++){
					if(i != 0) o.write("\t");
					o.write( KWIC_TableModel_MAI.columnArray[i].columnName );
				}
			}else{
				for(i=0 ; i < KWIC_TableModel_IWA.columnArray.length ; i++){
					if(i != 0) o.write("\t");
					o.write( KWIC_TableModel_IWA.columnArray[i].columnName );
				}
			}
			if(print_reverse_left_context_flag)
				o.write("\t左文脈(逆順)");
			o.write(line_sep);
		} catch (IOException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("検索結果のエクスポートに失敗しました");
			e2.setMsgE("Fail to export TSV file in print_header()");
			throw e2;
		}
	}
	
	private void print_header_in_table(BufferedWriter o,JTable tbl) throws SE_Exception {
		int col;
		TableColumnModel tcm = tbl.getColumnModel();
		try {
			
			if(print_search_key_flag)
				o.write("検索キー\t");
			for(col=0 ; col < tcm.getColumnCount() ; col++){
				if(col != 0) o.write('\t');
				o.write( (String)tcm.getColumn(col).getHeaderValue() );
			}
			if(print_reverse_left_context_flag)
				o.write("\t左文脈(逆順)");
			o.write(line_sep);
		} catch (IOException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("検索結果のエクスポートに失敗しました");
			e2.setMsgE("Fail to export TSV file in print_header()");
			throw e2;
		}
	}
	
	private String gen_reverse_string (String s) {
		StringBuilder sb = new StringBuilder(s.length());
		for(int i=s.length()-1 ; i >= 0 ; i--){
			sb.append(s.substring(i,i+1));
		}
		return sb.toString();
	}
}
