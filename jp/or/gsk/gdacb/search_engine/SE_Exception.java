package jp.or.gsk.gdacb.search_engine;

public class SE_Exception extends Exception {
	public String MsgJ = null;
	public String MsgE = null;
	private static final long serialVersionUID = 1L;
	
	/* コンスラクタの継承 */
	public SE_Exception() {
		super();
	}
	public SE_Exception(String message) {
		super(message);
	}
	public SE_Exception(String message, Throwable cause) {
		super(message, cause);
	}
	public SE_Exception(Throwable cause) {
		super(cause);
	}

	public void setMsgJ (String msg) {
		this.MsgJ = msg;
	}
	public void setMsgE (String msg) {
		this.MsgE = msg;
	}
}
