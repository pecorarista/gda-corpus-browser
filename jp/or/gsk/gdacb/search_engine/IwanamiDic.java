// 岩波国語辞典から語釈文などを得るクラス
// sense_id:	A.B.C.D  といった形式の語義ID (GDAにおけるsenseid属性の値)
// sem:			GDAにおけるsem属性の値(下記の one_sem を空白でつなげた文字列)
// sem_elm:		iwa:...*,  jpn:...,  no_ws  など、sem属性のひとつの要素

package jp.or.gsk.gdacb.search_engine;

import java.io.File;
import java.util.HashMap;

public class IwanamiDic {
	public String BASE_DIR;
	
	// 語釈文のキャッシュ
	// キー: sense_id(語義ID)、値: 語釈文
	private HashMap<String,String> defenition_sentence_cache = new HashMap<String,String>();
	// sem属性の値に語釈文の情報を付与した文字列のキャッシュ
	// キー: sem(sem属性の値、複数可)、値: 語釈文の情報を付与した文字列
	private HashMap<String,String> sem_attr_with_def_sent_cache = new HashMap<String,String>();
	// 内部で保持している GDA_File_IWA のインスタンス
	private GDA_File_IWA gda_iwa = null;
	
	public IwanamiDic (String d) {
		this.BASE_DIR = d;
	}
	
	// 語義IDを引数に取り、語釈文を得るメソッド
	public String senseDef (String sense_id) throws SE_Exception {
		String def = null;
		String midasi = null;
		
		if(defenition_sentence_cache.containsKey(sense_id)) {
			// System.out.println("Cache hit for "+sense_id);
			return defenition_sentence_cache.get(sense_id);
		}

		String path = get_file_path(sense_id);
		if(path == null) {
			defenition_sentence_cache.put(sense_id, null);
			return null;
		}
		File f = new File(path);
		if(! f.isFile()){
			defenition_sentence_cache.put(sense_id, null);
			return null;
		}
		
		if(gda_iwa == null || ! path.equals( gda_iwa.filename() )){
			gda_iwa = new GDA_File_IWA(path);
			// System.out.println("GDA_File_IWA is updated ("+path+")");
//			if(gda_iwa == null) {
//				defenition_sentence_cache.put(sense_id, null);
//				return null;			
//			}
			gda_iwa.read_all();
		}

		if(sense_id.endsWith(".all")){
			midasi = gda_iwa.extract_midasi();
			if(midasi.endsWith("]")){
				// 見出しに漢字表記があるとき (ex. へる[経る,歴る])
				def = midasi + "の語義全て";
			}else{
				// 見出しに漢字表記がないとき (ex. アイス)
				def = "[" + midasi + "]の語義全て";
			}
		}else if(sense_id.contains("+")){
			// A.B.C.D+A'.B'.C'.D'+... 形式
			// (岩波の語義を細分したケース、全ての和が元の語義、毎日コーパスのみに出現)
			StringBuilder sb = new StringBuilder();
			for(String s: sense_id.split("\\+")){
				sb.append( gda_iwa.extract_sense_def(s) );
				sb.append("+");
			}
			def = sb.substring(0,sb.length()-1);
		}else{
			def = gda_iwa.extract_sense_def(sense_id);
		}
		defenition_sentence_cache.put(sense_id, def);
		return def; 
	}

	// sem属性の値を引数に取り、それに語釈文の情報を付加した文字列を返すメソッド
	public String addSenseDescToSEM(String sem) {
		String result;
		if(sem_attr_with_def_sent_cache.containsKey(sem)) {
			// System.out.println("Cache hit for "+sem);
			return sem_attr_with_def_sent_cache.get(sem);
		}
		if(sem.contains(" ")){
			StringBuilder sb = new StringBuilder();
			for(String s: sem.split(" ")){
				try {
					sb.append( addSenseDescToSEMELM(s) );
				} catch (SE_Exception e) {
					System.err.println(e.MsgE);
					sb.append(s+"=ERROR");
				}
				sb.append('\n');
			}
			result = sb.substring(0, sb.length()-1);
		}else{
			try {
				result = addSenseDescToSEMELM(sem);
			} catch (SE_Exception e) {
				System.err.println(e.MsgE);
				result = sem+"=ERROR";
			}
		}
		sem_attr_with_def_sent_cache.put(sem, result);
		return result;
	}

	// sem属性の値の一要素を引数に取り、それに語釈文の情報を付加した文字列を返すメソッド
	private String addSenseDescToSEMELM(String sem_elm) throws SE_Exception {
		if(isIwanamiSenseID(sem_elm)){
			String s;
			s = senseDef_of_sem_elm(sem_elm);
			if(s == null){
				return sem_elm + "=NA";
			}else{
				return sem_elm + "=" + s;
			}
		}else{
			return sem_elm;
		}
	}
	
	
	// sem属性の値を1つ取り、語釈文を返すメソッド
	private String senseDef_of_sem_elm (String sem_elm) throws SE_Exception {
		String sid = convSEMELMtoSID(sem_elm);
		if(sid == null) return null;
		return senseDef(sid);
	}

	// sem属性の値の一要素が岩波の語義であるかを判定するメソッド
	public static boolean isIwanamiSenseID (String sem_elm){
		return sem_elm.startsWith("iwa:") ? true : false;
	}

	// 岩波の語義を表わすsem属性の値の一要素を sense ID に変換する 
	public String convSEMELMtoSID (String sem_elm) {
		String s;
		if(! isIwanamiSenseID(sem_elm)) return null;
		if(sem_elm.endsWith("*") || sem_elm.endsWith("?")){
			s = sem_elm.substring(4,sem_elm.length()-1);
		}else{
			s = sem_elm.substring(4);
		}
		int p = s.indexOf("%");
		if(p == -1){
			return s;
		}else{
			// 語義IDが「A.B.C%D.E.F」形式のとき、「A.B.C」を返す
			if(s.charAt(p-1) == '*' || s.charAt(p-1) == '?'){
				return s.substring(0,p-1);
			}else{
				return s.substring(0,p);
			}
		}
	}
	
	// 語義IDを引数にとり、その語釈を含むファイルのパスを返すメソッド
	private String get_file_path (String sense_id) throws SE_Exception {
		String midasi_id = get_midasi_id(sense_id);
		if(midasi_id == null) return null;
		String subdir = get_sub_directory(midasi_id);
		if(subdir == null) return null;
		return BASE_DIR + File.separator + subdir + File.separator + midasi_id + ".dict";
	}

	// 語釈文のキャッシュを全てクリアする
	private void clear_def_sent_cache () {
		defenition_sentence_cache.clear();
	}
	// 語釈文を付加したsem属性のキャッシュを全てクリアする
	private void clear_sem_attr_cache () {
		sem_attr_with_def_sent_cache.clear();
	}
	public void clear_cache(){
		clear_def_sent_cache();
		clear_sem_attr_cache();
	}
	
	// 指定された語義IDの語釈文のキャッシュをクリアする
//	public void clear_def_sent_cache (String sid) {
//		defenition_sentence_cache.remove(sid);	
//	}

	// 見出しIDから、「見出しID.dict」を含むサブディレクトリを返すメソッド
	private String get_sub_directory (String midasi_id) throws SE_Exception {
		int midasi_id_key, p;
		
		try {
			if(midasi_id.contains("-")){
				String[] tmp = midasi_id.split("-");
				midasi_id_key = Integer.parseInt(tmp[0]);
			}else{
				midasi_id_key = Integer.parseInt(midasi_id);
			}
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			SE_Exception e2 = new SE_Exception();
			e2.setMsgJ("語義IDが不正(IwanamiDic#get_sub_directory())");
			e2.setMsgE("Illegal sense ID in IwanamiDic#get_sub_directory()");
			throw e2;
		}
		
		int start = 0;
		int end = SubDirs.info.length - 1;

		// 二分探索でサブディレクトリを検索
		while(start <= end){
			p = (start + end) / 2;
			if(SubDirs.info[p].last_midasi_id == midasi_id_key){
				return SubDirs.info[p].path;
			}else if(SubDirs.info[p].last_midasi_id < midasi_id_key){
				start = p + 1;
			}else{
				end = p - 1;
			}
		}
		// ここでは end + 1 == start
		// end と start の間にある可能性あり
		if(end == -1){
			if(midasi_id_key > 0 && midasi_id_key <= SubDirs.info[start].last_midasi_id)
				return SubDirs.info[start].path;
		}else if(start == SubDirs.info.length){
			return null;
		}else{
			if (midasi_id_key > SubDirs.info[end].last_midasi_id &&
				midasi_id_key <= SubDirs.info[start].last_midasi_id)
				return SubDirs.info[start].path;
		}
		return null;
	}
	// 語義ID "A.B.C..." から見出しID "A" を取り出すメソッド   
	private String get_midasi_id (String sense_id) {
		String[] tmp = sense_id.split("\\.");
		if(tmp.length == 0){
			return null;
		}else{
			return tmp[0];
		}
	}
}

class SubDir {
	protected String path;
	protected int last_midasi_id;
	SubDir (String p,int i){
		this.path = p;
		this.last_midasi_id = i;
	}
}

// サブディレクトリ名とそれに格納されている最後の辞書の見出しIDの情報を
// 静的フィールドとして定義するためだけの抽象クラス
abstract class SubDirs {
	public static final SubDir[] info = {
		new SubDir("dict01/01", 160),
		new SubDir("dict01/02", 330),
		new SubDir("dict01/03", 500),
		new SubDir("dict01/04", 668),
		new SubDir("dict01/05", 837),
		new SubDir("dict01/06", 1006),
		new SubDir("dict01/07", 1170),
		new SubDir("dict01/08", 1340),
		new SubDir("dict01/09", 1502),
		new SubDir("dict01/10", 1652),
		new SubDir("dict02/01", 1830),
		new SubDir("dict02/02", 2012),
		new SubDir("dict02/03", 2198),
		new SubDir("dict02/04", 2356),
		new SubDir("dict02/05", 2513),
		new SubDir("dict02/06", 2670),
		new SubDir("dict02/07", 2848),
		new SubDir("dict02/08", 3032),
		new SubDir("dict02/09", 3209),
		new SubDir("dict02/10", 3390),
		new SubDir("dict03/01", 3574),
		new SubDir("dict03/02", 3762),
		new SubDir("dict03/03", 3953),
		new SubDir("dict03/04", 4145),
		new SubDir("dict03/05", 4325),
		new SubDir("dict03/06", 4509),
		new SubDir("dict03/07", 4687),
		new SubDir("dict03/08", 4865),
		new SubDir("dict03/09", 5051),
		new SubDir("dict03/10", 5227),
		new SubDir("dict04/01", 5383),
		new SubDir("dict04/02", 5552),
		new SubDir("dict04/03", 5715),
		new SubDir("dict04/04", 5886),
		new SubDir("dict04/05", 6055),
		new SubDir("dict04/06", 6227),
		new SubDir("dict04/07", 6393),
		new SubDir("dict04/08", 6561),
		new SubDir("dict04/09", 6726),
		new SubDir("dict04/10", 6885),
		new SubDir("dict05/01", 7064),
		new SubDir("dict05/02", 7253),
		new SubDir("dict05/03", 7439),
		new SubDir("dict05/04", 7633),
		new SubDir("dict05/05", 7822),
		new SubDir("dict05/06", 8006),
		new SubDir("dict05/07", 8194),
		new SubDir("dict05/08", 8383),
		new SubDir("dict05/09", 8574),
		new SubDir("dict05/10", 8748),
		new SubDir("dict06/01", 8923),
		new SubDir("dict06/02", 9105),
		new SubDir("dict06/03", 9288),
		new SubDir("dict06/04", 9466),
		new SubDir("dict06/05", 9651),
		new SubDir("dict06/06", 9828),
		new SubDir("dict06/07", 10003),
		new SubDir("dict06/08", 10174),
		new SubDir("dict06/09", 10343),
		new SubDir("dict06/10", 10511),
		new SubDir("dict07/01", 10723),
		new SubDir("dict07/02", 10935),
		new SubDir("dict07/03", 11155),
		new SubDir("dict07/04", 11366),
		new SubDir("dict07/05", 11589),
		new SubDir("dict07/06", 11813),
		new SubDir("dict07/07", 12029),
		new SubDir("dict07/08", 12232),
		new SubDir("dict07/09", 12453),
		new SubDir("dict07/10", 12662),
		new SubDir("dict08/01", 12860),
		new SubDir("dict08/02", 13050),
		new SubDir("dict08/03", 13253),
		new SubDir("dict08/04", 13464),
		new SubDir("dict08/05", 13676),
		new SubDir("dict08/06", 13886),
		new SubDir("dict08/07", 14084),
		new SubDir("dict08/08", 14282),
		new SubDir("dict08/09", 14458),
		new SubDir("dict08/10", 14652),
		new SubDir("dict09/01", 14861),
		new SubDir("dict09/02", 15069),
		new SubDir("dict09/03", 15279),
		new SubDir("dict09/04", 15470),
		new SubDir("dict09/05", 15677),
		new SubDir("dict09/06", 15898),
		new SubDir("dict09/07", 16109),
		new SubDir("dict09/08", 16314),
		new SubDir("dict09/09", 16518),
		new SubDir("dict09/10", 16712),
		new SubDir("dict10/01", 16896),
		new SubDir("dict10/02", 17094),
		new SubDir("dict10/03", 17270),
		new SubDir("dict10/04", 17469),
		new SubDir("dict10/05", 17658),
		new SubDir("dict10/06", 17848),
		new SubDir("dict10/07", 18037),
		new SubDir("dict10/08", 18219),
		new SubDir("dict10/09", 18407),
		new SubDir("dict10/10", 18600),
		new SubDir("dict11/01", 18790),
		new SubDir("dict11/02", 18975),
		new SubDir("dict11/03", 19177),
		new SubDir("dict11/04", 19378),
		new SubDir("dict11/05", 19573),
		new SubDir("dict11/06", 19759),
		new SubDir("dict11/07", 19947),
		new SubDir("dict11/08", 20127),
		new SubDir("dict11/09", 20315),
		new SubDir("dict11/10", 20498),
		new SubDir("dict12/01", 20685),
		new SubDir("dict12/02", 20888),
		new SubDir("dict12/03", 21083),
		new SubDir("dict12/04", 21275),
		new SubDir("dict12/05", 21474),
		new SubDir("dict12/06", 21667),
		new SubDir("dict12/07", 21865),
		new SubDir("dict12/08", 22064),
		new SubDir("dict12/09", 22259),
		new SubDir("dict12/10", 22434),
		new SubDir("dict13/01", 22646),
		new SubDir("dict13/02", 22852),
		new SubDir("dict13/03", 23064),
		new SubDir("dict13/04", 23282),
		new SubDir("dict13/05", 23493),
		new SubDir("dict13/06", 23710),
		new SubDir("dict13/07", 23932),
		new SubDir("dict13/08", 24146),
		new SubDir("dict13/09", 24350),
		new SubDir("dict13/10", 24540),
		new SubDir("dict14/01", 24743),
		new SubDir("dict14/02", 24952),
		new SubDir("dict14/03", 25164),
		new SubDir("dict14/04", 25382),
		new SubDir("dict14/05", 25607),
		new SubDir("dict14/06", 25810),
		new SubDir("dict14/07", 26020),
		new SubDir("dict14/08", 26217),
		new SubDir("dict14/09", 26434),
		new SubDir("dict14/10", 26636),
		new SubDir("dict15/01", 26830),
		new SubDir("dict15/02", 27025),
		new SubDir("dict15/03", 27212),
		new SubDir("dict15/04", 27411),
		new SubDir("dict15/05", 27591),
		new SubDir("dict15/06", 27769),
		new SubDir("dict15/07", 27952),
		new SubDir("dict15/08", 28136),
		new SubDir("dict15/09", 28319),
		new SubDir("dict15/10", 28500),
		new SubDir("dict16/01", 28695),
		new SubDir("dict16/02", 28891),
		new SubDir("dict16/03", 29092),
		new SubDir("dict16/04", 29300),
		new SubDir("dict16/05", 29495),
		new SubDir("dict16/06", 29701),
		new SubDir("dict16/07", 29907),
		new SubDir("dict16/08", 30117),
		new SubDir("dict16/09", 30319),
		new SubDir("dict16/10", 30513),
		new SubDir("dict17/01", 30694),
		new SubDir("dict17/02", 30878),
		new SubDir("dict17/03", 31063),
		new SubDir("dict17/04", 31246),
		new SubDir("dict17/05", 31441),
		new SubDir("dict17/06", 31640),
		new SubDir("dict17/07", 31834),
		new SubDir("dict17/08", 32033),
		new SubDir("dict17/09", 32229),
		new SubDir("dict17/10", 32413),
		new SubDir("dict18/01", 32590),
		new SubDir("dict18/02", 32780),
		new SubDir("dict18/03", 32959),
		new SubDir("dict18/04", 33151),
		new SubDir("dict18/05", 33347),
		new SubDir("dict18/06", 33546),
		new SubDir("dict18/07", 33738),
		new SubDir("dict18/08", 33934),
		new SubDir("dict18/09", 34136),
		new SubDir("dict18/10", 34331),
		new SubDir("dict19/01", 34511),
		new SubDir("dict19/02", 34686),
		new SubDir("dict19/03", 34841),
		new SubDir("dict19/04", 35005),
		new SubDir("dict19/05", 35178),
		new SubDir("dict19/06", 35346),
		new SubDir("dict19/07", 35512),
		new SubDir("dict19/08", 35648),
		new SubDir("dict19/09", 35802),
		new SubDir("dict19/10", 35962),
		new SubDir("dict20/01", 36154),
		new SubDir("dict20/02", 36343),
		new SubDir("dict20/03", 36534),
		new SubDir("dict20/04", 36733),
		new SubDir("dict20/05", 36928),
		new SubDir("dict20/06", 37121),
		new SubDir("dict20/07", 37314),
		new SubDir("dict20/08", 37512),
		new SubDir("dict20/09", 37714),
		new SubDir("dict20/10", 37889),
		new SubDir("dict21/01", 38056),
		new SubDir("dict21/02", 38225),
		new SubDir("dict21/03", 38394),
		new SubDir("dict21/04", 38565),
		new SubDir("dict21/05", 38729),
		new SubDir("dict21/06", 38900),
		new SubDir("dict21/07", 39062),
		new SubDir("dict21/08", 39235),
		new SubDir("dict21/09", 39387),
		new SubDir("dict21/10", 39559),
		new SubDir("dict22/01", 39735),
		new SubDir("dict22/02", 39909),
		new SubDir("dict22/03", 40083),
		new SubDir("dict22/04", 40262),
		new SubDir("dict22/05", 40434),
		new SubDir("dict22/06", 40612),
		new SubDir("dict22/07", 40788),
		new SubDir("dict22/08", 40964),
		new SubDir("dict22/09", 41141),
		new SubDir("dict22/10", 41303),
		new SubDir("dict23/01", 41486),
		new SubDir("dict23/02", 41675),
		new SubDir("dict23/03", 41870),
		new SubDir("dict23/04", 42064),
		new SubDir("dict23/05", 42257),
		new SubDir("dict23/06", 42446),
		new SubDir("dict23/07", 42638),
		new SubDir("dict23/08", 42821),
		new SubDir("dict23/09", 43011),
		new SubDir("dict23/10", 43194),
		new SubDir("dict24/01", 43405),
		new SubDir("dict24/02", 43618),
		new SubDir("dict24/03", 43827),
		new SubDir("dict24/04", 44022),
		new SubDir("dict24/05", 44229),
		new SubDir("dict24/06", 44435),
		new SubDir("dict24/07", 44644),
		new SubDir("dict24/08", 44859),
		new SubDir("dict24/09", 45066),
		new SubDir("dict24/10", 45258),
		new SubDir("dict25/01", 45454),
		new SubDir("dict25/02", 45645),
		new SubDir("dict25/03", 45845),
		new SubDir("dict25/04", 46041),
		new SubDir("dict25/05", 46226),
		new SubDir("dict25/06", 46420),
		new SubDir("dict25/07", 46618),
		new SubDir("dict25/08", 46814),
		new SubDir("dict25/09", 47012),
		new SubDir("dict25/10", 47202),
		new SubDir("dict26/01", 47400),
		new SubDir("dict26/02", 47592),
		new SubDir("dict26/03", 47774),
		new SubDir("dict26/04", 47964),
		new SubDir("dict26/05", 48162),
		new SubDir("dict26/06", 48366),
		new SubDir("dict26/07", 48570),
		new SubDir("dict26/08", 48770),
		new SubDir("dict26/09", 48968),
		new SubDir("dict26/10", 49163),
		new SubDir("dict27/01", 49354),
		new SubDir("dict27/02", 49544),
		new SubDir("dict27/03", 49734),
		new SubDir("dict27/04", 49924),
		new SubDir("dict27/05", 50103),
		new SubDir("dict27/06", 50290),
		new SubDir("dict27/07", 50474),
		new SubDir("dict27/08", 50656),
		new SubDir("dict27/09", 50836),
		new SubDir("dict27/10", 51016),
		new SubDir("dict28/01", 51201),
		new SubDir("dict28/02", 51387),
		new SubDir("dict28/03", 51583),
		new SubDir("dict28/04", 51762),
		new SubDir("dict28/05", 51950),
		new SubDir("dict28/06", 52141),
		new SubDir("dict28/07", 52334),
		new SubDir("dict28/08", 52511),
		new SubDir("dict28/09", 52696),
		new SubDir("dict28/10", 52882),
		new SubDir("dict29/01", 53100),
		new SubDir("dict29/02", 53311),
		new SubDir("dict29/03", 53527),
		new SubDir("dict29/04", 53734),
		new SubDir("dict29/05", 53942),
		new SubDir("dict29/06", 54146),
		new SubDir("dict29/07", 54340),
		new SubDir("dict29/08", 54547),
		new SubDir("dict29/09", 54758),
		new SubDir("dict29/10", 54953),
		new SubDir("dict30/01", 55089),
		new SubDir("dict30/02", 55221),
		new SubDir("dict30/03", 55348),
		new SubDir("dict30/04", 55483),
		new SubDir("dict30/05", 55602),
		new SubDir("dict30/06", 55728),
		new SubDir("dict30/07", 55860),
		new SubDir("dict30/08", 55993),
		new SubDir("dict30/09", 56127),
		new SubDir("dict30/10", 56257)
		};
}