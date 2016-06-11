// This is automatically generate file
/*
 * GDAコーパスに付与されているタグ情報をブラウズする
 */

/* 大域変数 */

// id属性を強調表示する際の背景色
//var highlight_id_background_color = '#FFCC33'; // オレンジ
var highlight_id_background_color = '#FFFF00';
// eq属性を強調表示する際の背景色
var highlight_eq_background_color = '#FFFF00';
// 「関係」リストのポップアップメニューの背景色
var relation_popupmenu_bg_color = '#E6E6E6';
// 「関係」リストのポップアップメニューを選択したときの背景色
var relation_popupmenu_highlight_bg_color = '#FF99FF';
// 「関係」リスト内の直示の情報の色
var relation_popupmenu_deictic_fg_color = '#009900';

// 下線を表示しているとき、その要素
var current_obj_underline = null;
// 共参照を強調表示しているとき、その対象となっているID
var current_id_coreference = null;
// 共参照を強調表示しているとき、
// マウスクリックによってその表示を生じさせたオブジェクト
var current_source_obj_coreference = null;
// ポップアップメニューを表示しているとき、
// マウスクリックによってその表示を生じさせたオブジェクト
var current_source_obj_floating_popup_menu = null;
// 関係(関係名と参照先)を強調表示しているとき、その参照先のID
var current_id_relation = null;
// 直示の関係を強調表示しているとき、直示を表示しているオブジェクト
var current_obj_deictic = null;

// 最後にマウスがクリックされた時刻
var last_click_time = 0;


/*
 * クリックした要素に下線を引く関数
 */
function show_underline(obj){
    obj.style.textDecoration = "underline";
    current_obj_underline = obj;
}
/*
 * 下線を消去する関数
 */
function hide_underline(){
    if(current_obj_underline == null) return;

    current_obj_underline.style.textDecoration = '';
    current_obj_underline = null;
}

/*
 * ポップアップメニューを作成/表示する
 */
function show_floating_popup_menu(obj,eve,menu){
    var i, objX, objY, top, left, relX, relY, obj_style, font_size;
    var menu_width, menu_height, wrap_lower_flag, wrap_upper_flag;

    var popup = document.getElementById('floating_popup_menu');

    // オブジェクトの位置を得る
    objX = getLeft(obj);
    objY = getTop(obj);
    // マウスの位置を得る
    getMouseXY(eve);
    // ウィンドウの内部領域のサイズを得る
    getScreenSize();
    // スクロールのオフセットを得る
    getScrollPosition();
    // オブジェクトのスタイルを得る
    obj_style = obj.currentStyle || document.defaultView.getComputedStyle(obj,'');
    // フォントサイズを得る
    font_size = (obj_style == null) ?
		 obj.offsetHeight : parseInt(obj_style.fontSize);

    // メニューを表示する
    moveObject(popup,0,0);	// サイズを得るため一度(0,0)の位置で表示する
    popup.innerHTML = menu;
    popup.style.visibility = 'visible';

    // メニューの位置を変更する

    //// メニューのサイズを得る
    menu_width = popup.offsetWidth;
    menu_height = popup.offsetHeight;

    //// left を決める
    if(objX > mouseX){
	// 2行にまたがる要素の下の行をクリックしたとき
	wrap_lower_flag = 1;	
	left = mouseX;
    }else{
	wrap_lower_flag = 0;
	left = objX;
    }
    relX = left - scrollOffsetX;
    if( relX + menu_width + 20 > windowInnerWidth ){
	// body要素の余白を考慮し、少し余分に左に寄せる
	left = windowInnerWidth - menu_width - 20;
	if(left < 0) left = 0;
    }
    //// top を決める
    if(wrap_lower_flag){
	top = mouseY + font_size + 1;
    }else{
	if(obj.offsetHeight > font_size * 2){
	    // 2行にまたがる要素の上の行をクリックしたとき
	    wrap_upper_flag = 1;
	    top = mouseY + font_size + 1;
	}else{
	    wrap_upper_flag = 0;
	    top = objY + obj.offsetHeight + 3;
	}
    }
    relY = top - scrollOffsetY;
    if( relY + menu_height + 20 > windowInnerHeight ){
	// +20 はbody要素の余白を考慮したもの
	if(wrap_lower_flag){
	    top = mouseY - menu_height - font_size - 4;
	}else{
	    top = objY - menu_height - 4;
	}
    }
    moveObject(popup,left,top);

    current_source_obj_floating_popup_menu = obj;
}
/*
 *  ポップアップメニューを消す
 */
function hide_floating_popup_menu(){
    if(current_source_obj_floating_popup_menu == null) return;

    hide_underline();
    if(current_id_relation != null){
	var elm = document.getElementById(current_id_relation);
	if(elm != null) elm.style.backgroundColor = '';
	current_id_relation = null;
    }
    if(current_obj_deictic != null){
	current_obj_deictic.style.visibility = 'hidden';
	current_obj_deictic = null;
    }
    document.getElementById('floating_popup_menu').style.visibility = 'hidden';
    current_source_obj_floating_popup_menu = null;
}

/*
 * 全てのデコレーションをリセットする
 * id, eq, 関係のマーカー上で呼び出す
 */
function reset_all_decoration (obj) {
    if(current_obj_underline != obj.parentNode){
	hide_underline();
    }
    if(current_source_obj_coreference != obj.parentNode){
	unhighlight_coreference();
    }
    if(current_source_obj_floating_popup_menu != obj){
	hide_floating_popup_menu();
    }
}

/*
 * class属性の値から、
 * 元のGDAファイルにおける eq(もしくはeq.*)属性の値を取り出す関数
 *   引数=HTML要素
 *   戻値=eq属性の値  もしくは  null
 */
function get_eq_value (obj) {
    var i, v, pair_array, pair, gda_attr, gda_value;

    if(obj == null) return;

    v = get_class_value(obj);
    if(v == null) return null;

    pair_array = v.split("||");
    for(i=0 ; i < pair_array.length ; i++){
	if(pair_array[i] == '') break;
	pair = pair_array[i].split(',');
	gda_attr = pair.shift();
	gda_value = pair.join(',');
	if(gda_attr == "eq" || gda_attr.substr(0,3) == "eq."){
	    return gda_value;
	}
    }
    return null;
}

/*
 * マウスが連続してクリックされたかをチェックする
 * onmouseupイベントに関数が割り当てられたspan要素が入れ子になっていて、
 * 関数が2回呼び出されたときも含む
 *   戻値: 連続クリックなら true、それ以外は false
 */
function check_duplicate_mouse_click(){
    var now = new Date();
    var t = now.getTime();
    if(t - last_click_time < 500){	// 単位はミリ秒
	return true;
    }
    last_click_time = t;
    return false;
}

/* イベント(マウスの位置やボタンの種類)を取得するには、
 * 以下のように関数をバインドする必要がある
 * HTML の中で <span onmouseup="func()"> のように書いても
 * イベントは取得できない
function add_mouse_handler () {
    var i, span_objs;

    span_objs = document.getElementsByTagName('span');
    for(i=0 ; i < span_objs.length ; i++){
	//if(span_objs[i].getAttribute("class").indexOf("eq," + id) != -1){
	//eq_objs.push(span_objs[i]);
	//}else if(span_objs[i].getAttribute("id") == id){
	//  id_objs.push(span_objs[i]);
	//}	    
	if(span_objs[i].className == "marker_eq"){
	    span_objs[i].onmouseup = ck;
	}
    }
}
*/
/*
 * 形態素情報(mph,sem)属性をブラウズする
 */

/*
 * 単語上でマウスをクリックしたときのアクション
 * 第1引数はイベント
 */
//function ck1(obj) {
function ck1(eve) {
    var i, class_value, mph_sem, new_morph_menu;

    var obj = getTargetObj(eve);
    if(obj == null) return;

    if( check_duplicate_mouse_click() ) return;

    reset_all_decoration(obj);

    if(current_source_obj_floating_popup_menu == null){
	class_value = get_class_value(obj);
	if(class_value == 'target_word'){
	    class_value = get_class_value(obj.parentNode);
	}
	mph_sem = get_mph_sem_value(class_value);

	if(mph_sem == null) return;

	new_morph_menu = gen_morph_menu_content(mph_sem[0],mph_sem[1]);
	if(new_morph_menu == null) return;

	show_floating_popup_menu(obj,eve,new_morph_menu);
	show_underline(obj);
    }else{
	hide_floating_popup_menu();
	hide_underline();
    }
}

/* 元のGDAファイルの属性と値を表わす文字列から、
 * mph, sem 属性の値を取り出す関数
 * 戻値は ( mph属性の値, sem属性の値 ) というリスト
 */
function get_mph_sem_value (str) {
    var i, pair_array, pair, gda_attr, gda_value;

    if(str == null){  return null  };

    var result = new Array(2);
    result[0] = '';
    result[1] = '';

    pair_array = str.split("||");
    for(i=0 ; i < pair_array.length ; i++){
	if(pair_array[i] == ''){  break;  };
	pair = pair_array[i].split(',');
	gda_attr = pair.shift();
	gda_value = pair.join(',');
	if(gda_attr == "mph"){
	    result[0] = gda_value;
	}else if(gda_attr == "sem"){
	    result[1] = gda_value;
	}
    }
    return result;
}

/*
 * 形態素情報を表示するポップアップメニューを作成する
 */
function gen_morph_menu_content (mph,sem){
    var i, menu, rel, referent;

    if(mph == '' && sem == ''){
	return null;
    }

    menu = '<table class="morph">';
    menu += '<tr><td colspan="2" class="floating_popup_menu_header"></td></tr>';
    if(mph != ''){
	if(mph.indexOf(';') == -1){
	    menu += '<tr onmouseup="hide_floating_popup_menu()">';
	    menu += '<td class="attr">mph</td>';
	    menu += '<td class="value">'+mph+'</td></tr>';
	}else{
	    var mor_info = mph.split(';');
	    if(mor_info[0] == 'ipa' || mor_info[0] == 'chasen'){
		mor_info.shift();
	    }
	    if(mor_info[0] != ''){
		menu += '<tr onmouseup="hide_floating_popup_menu()">';
		menu += '<td class="attr">mph(品詞)</td>';
		menu += '<td class="value">'+mor_info[0]+'</td></tr>';
	    }
	    if(mor_info[1] != ''){
		menu += '<tr onmouseup="hide_floating_popup_menu()">';
		menu += '<td class="attr">mph(活用形)</td>';
		menu += '<td class="value">'+mor_info[1]+'</td></tr>';
	    }
	    if(mor_info[2] != ''){
		menu += '<tr onmouseup="hide_floating_popup_menu()">';
		menu += '<td class="attr">mph(基本形)</td>';
		menu += '<td class="value">'+mor_info[2]+'</td></tr>';
	    }
	    if(mor_info[3] != ''){
		menu += '<tr onmouseup="hide_floating_popup_menu()">';
		menu += '<td class="attr">mph(読み)</td>';
		menu += '<td class="value">'+mor_info[3]+'</td></tr>';
	    }
	}
    }
    if(sem != ''){
	menu += '<tr onmouseup="hide_floating_popup_menu()">';
	menu += '<td class="attr">sem</td>';
	menu += '<td class="value">'+sem+'</td></tr>';
    }
    menu += '</table>';

    return menu;
}
/*
 * 共参照の情報をブラウズする
 */

/*
 * idまたはeq属性のマーカ上でマウスをクリックしたときのアクション
 * 第1引数はHTML要素
 */
function ck2(obj) {
//function ck2(eve) {
    var i, id, eq_id, class_value;

    //var obj = getTargetObj(eve);
    //if(obj == null) return;

    if( check_duplicate_mouse_click() ) return;

    reset_all_decoration(obj);

    if(current_obj_underline == null){
	show_underline(obj.parentNode);
    }else{
	hide_underline();
    }

    if(current_id_coreference == null){
	eq_id = get_eq_value( obj.parentNode );
	if(eq_id != null){
	    highlight_coreference(obj.parentNode,eq_id);
	    return;
	}

	id = obj.parentNode.getAttribute("id");
	if(id != null && id != ""){
	    highlight_coreference(obj.parentNode,id);
	    return;
	}
    }else{
	unhighlight_coreference();
    }
}

/*
 * 共参照関係にある要素を強調表示する関数
 */
function highlight_coreference(me,id) {
    var i, n, span_objs, eq_objs, id_objs, eq_val, target_id_list;

    target_id_list = id.split(' ');

    span_objs = document.getElementsByTagName('span');
    eq_objs = new Array();
    id_objs = new Array();
    for(i=0 ; i < span_objs.length ; i++){
	eq_val = get_eq_value(span_objs[i]);
	if( eq_val != null &&
	    has_intersection( eq_val.split(' '),target_id_list) ){
	    eq_objs.push(span_objs[i]);
	}else if( member(span_objs[i].getAttribute("id"), target_id_list) ){
	    id_objs.push(span_objs[i]);
	}	    
    }
    // 要素が1以上あるとき、背景色を変える
    n = eq_objs.length + id_objs.length;
    if(n > 1){
	for(i=0 ; i < eq_objs.length ; i++){
	    // eq_objs[i].style.color = ***foregroud color***
	    eq_objs[i].style.backgroundColor = highlight_eq_background_color;
	}
	for(i=0 ; i < id_objs.length ; i++){
	    id_objs[i].style.backgroundColor = highlight_id_background_color;
	}
	current_id_coreference = id;
	current_source_obj_coreference = me;
    }
}
/*
 * 共参照関係にある要素を強調表示を取り消す関数
 */
function unhighlight_coreference() {
    var i, n, span_objs, eq_objs, id_objs, eq_val, target_id_list;

    if(current_id_coreference == null) return;

    target_id_list = current_id_coreference.split(' ');

    span_objs = document.getElementsByTagName('span');
    eq_objs = new Array();
    id_objs = new Array();
    for(i=0 ; i < span_objs.length ; i++){
	eq_val = get_eq_value(span_objs[i]);
	if( eq_val != null &&
	    has_intersection( eq_val.split(' '),target_id_list) ){
	    eq_objs.push(span_objs[i]);
	}else if( member(span_objs[i].getAttribute("id"), target_id_list) ){
	    id_objs.push(span_objs[i]);
	}	    
    }
    // 背景色を消す
    for(i=0 ; i < eq_objs.length ; i++){
	eq_objs[i].style.backgroundColor = '';
    }
    for(i=0 ; i < id_objs.length ; i++){
	id_objs[i].style.backgroundColor = '';
    }

    current_id_coreference = null;
    current_source_obj_coreference = null;
}

/*
 * 対応先のないeq属性,id属性のマーカーを非表示にする
 */
function hide_invalid_markers() {
    var i, j, j2, id, flag, span_objs, eq_objs, id_objs;
    var eq_val, eq_val2, eq_val_list;

    span_objs = document.getElementsByTagName('span');
    //eq_objs = new Array();	// 元のGDAファイルでeq属性を持つタグ
    eq_objs = new Array();	// eq属性のマーカー (■)
    id_objs = new Array();	// id属性のマーカー (■)
    for(i=0 ; i < span_objs.length ; i++){
	//if( get_eq_value(span_objs[i]) != null ){
	if(span_objs[i].className == "marker_eq"){
	    eq_objs.push(span_objs[i]);
	//}else if(span_objs[i].getAttribute("class") == "marker_id"){
	}else if(span_objs[i].className == "marker_id"){
	    id_objs.push(span_objs[i]);
	}	    
    }

    // 無効なid属性のマーカーを消す
    for(i=0 ; i < id_objs.length ; i++){
	id = id_objs[i].parentNode.getAttribute("id");
	if(id == null || id == "") continue;

	flag = false;
	for(j=0 ; j < eq_objs.length ; j++){
	    eq_val = get_eq_value(eq_objs[j].parentNode);
	    if( eq_val != null && member(id,eq_val.split(' ')) ){
		flag = true;
		break;
	    }
	}
	if(! flag){
	    //id_objs[i].style.visibility = 'hidden';
	    id_objs[i].innerHTML = "";
	}
    }
    // 無効なeq属性のマーカーを消す
    for(j=0 ; j < eq_objs.length ; j++){
	eq_val = get_eq_value(eq_objs[j].parentNode);
	if(eq_val == null) continue;
	eq_val_list = eq_val.split(' ');

	flag = false;
	for(i=0 ; i < id_objs.length ; i++){
	    id = id_objs[i].parentNode.getAttribute("id");
	    if(id == null || id == "") continue;
	    if( member(id,eq_val_list) ){
		flag = true;
		break;
	    }
	}
	if(! flag){
	    for(j2=0 ; j2 < eq_objs.length ; j2++){
		if(j == j2) continue;
		eq_val2 = get_eq_value(eq_objs[j2].parentNode);
		if( eq_val2 != null &&
		    has_intersection(eq_val_list,eq_val2.split(' ')) ){
		    flag = true;
		    break;
		}
	    }
	}
	if(! flag){
	    //eq_objs[j].style.visibility = 'hidden';
	    eq_objs[j].innerHTML = "";
	}
    }

    /* 古いバージョン
    for(i=0 ; i < id_objs.length ; i++){
	id = id_objs[i].parentNode.getAttribute("id");
	if(id == null || id == ""){
	    //id_objs[i].style.visibility = 'hidden';
	    //id_objs[i].innerHTML = "";
	}else{
	    flag = false;
	    for(j=0 ; j < eq_objs.length ; j++){
		if( get_eq_value(eq_objs[j]) == id ){
		    flag = true;
		    break;
		}
	    }
	    if(! flag){
		//id_objs[i].style.visibility = 'hidden';
		id_objs[i].innerHTML = "";
	    }
	}
    }
    */
}


/*
 * 文間の「関係」をブラウズする
 */

/*
 * 関係を表わすマーカ上でマウスをクリックしたときのアクション
 * 第1引数はイベント
 */
//function ck3(obj) {
function ck3(eve) {
    var class_value, rel_list, new_menu;

    var obj = getTargetObj(eve);
    if(obj == null) return;

    if( check_duplicate_mouse_click() ) return;

    reset_all_decoration(obj);

    if(current_source_obj_floating_popup_menu == null){
	class_value = get_class_value( obj.parentNode );
	rel_list = get_rel_list(class_value);

	// ポップアップメニューを表示する
	if(rel_list != null && rel_list.length > 0){
	    new_menu = gen_relation_menu_content(rel_list);
	    show_floating_popup_menu(obj,eve,new_menu);
	    show_underline(obj.parentNode);
	}
    }else{
	hide_floating_popup_menu();
	hide_underline();
    }
}

/*
 * 関係を強調表示する関数
 * 第1引数=ポップアップメニューの項目のオブジェクト
 * 第2引数=「関係」の参照先オブジェクトのID
 * 第3引数=強調表示するか(1)か否(0)か
 */
function highlight_relation(obj,id,sw) {
    var i, elm, text, deictic = null;
    if(id.substr(0,2) == "D:"){
	elm = null;
	deictic = id.substr(2);
    //if(id == null){
    //	elm = null;
    }else{
	elm = document.getElementById(id);
    }
    if(sw == 1){
	obj.style.backgroundColor = relation_popupmenu_highlight_bg_color;
	/*
	if(obj.lastChild != null && obj.lastChild.className == 'deictic'){
	    obj.lastChild.style.color = relation_popupmenu_deictic_fg_color;
	}
	*/
	/*
	if(obj.nextSibling != null && obj.nextSibling.className == 'deictic'){
	    obj.nextSibling.style.visibility = 'visible';
	    current_obj_deictic = obj.nextSibling;
	}
	*/
	if(elm != null){
	    elm.style.backgroundColor = relation_popupmenu_highlight_bg_color;
	    current_id_relation = id;
	}
	if(deictic != null){
	    current_obj_deictic = document.getElementById('deictic_area');
	    show_deictic(deictic,current_obj_deictic,obj);
	}
	    
    }else{
	obj.style.backgroundColor = relation_popupmenu_bg_color;
	/*
	if(obj.lastChild != null && obj.lastChild.className == 'deictic'){
	    // 文字色を背景と同じにして隠す
	    obj.lastChild.style.color = relation_popupmenu_bg_color;
	}
	*/
	/*
	if(obj.nextSibling != null && obj.nextSibling.className == 'deictic'){
	    obj.nextSibling.style.visibility = 'hidden';
	}
	*/
	if(elm != null){
	    elm.style.backgroundColor = '';
	}
	if(deictic != null){
	    if(current_obj_deictic != null){
		current_obj_deictic.style.visibility = 'hidden';
	    }
	}
	current_id_relation = null;
	current_obj_deictic = null;
    }
}
/*
 * 直示の関係を表示する関数
 *   第1引数=直示の情報
 *   第2引数=直示の情報を表示させるオブジェクト(div要素)
 *   第3引数=関係の種類を表示しているオブジェクト(td要素)
 */
function show_deictic(deictic,d_obj,r_obj) {
    var content, r_objX, r_objY, r_objWidth, d_objWidth, relX;

    // 直示テーブルを作成
    content = '<table class="relation"><tr>';
    content += '<td class="deictic_arrow"> = </td>';
    content += '<td class="deictic">' + deictic + '</td>';
    content += '</tr></table>';

    // 関係を表示しているオブジェクトの位置を得る
    r_objX = getLeft(r_obj);
    r_objY = getTop(r_obj);
    // ウィンドウの内部領域のサイズを得る
    getScreenSize();
    // スクロールのオフセットを得る
    getScrollPosition();

    // 直示テーブルを表示する
    moveObject(d_obj,0,0);	// サイズを得るため一度(0,0)の位置で表示する
    d_obj.innerHTML = content;
    d_obj.style.visibility = 'visible';

    // 直示テーブルの位置を決め、表示する

    r_objWidth = r_obj.offsetWidth;
    d_objWidth = d_obj.offsetWidth;

    relX = r_objX - scrollOffsetX;
    if( relX + r_objWidth + d_objWidth > windowInnerWidth ){
	// 直示の情報を右に表示
	content = '<table class="relation"><tr>';
	content += '<td class="deictic">' + deictic + '</td>';
	content += '<td class="deictic_arrow"> = </td>';
	content += '</tr></table>';
	d_obj.innerHTML = content;

	moveObject(d_obj, r_objX - d_objWidth - 1, r_objY);
    }else{
	// 直示の情報を左に表示
	moveObject(d_obj, r_objX + r_objWidth + 1, r_objY);
    }
}

/*
 * 元のGDAファイルの属性と値を表わす文字列から、
 * 関係を表わす属性とその値を取り出す関数
 * 戻値は ( 属性1, 値1, 属性2, 値2, ... ) というリスト
 */
function get_rel_list (str) {
    var i, pair_array, pair, gda_attr, gda_value;

    if(str == null){  return null  };

    var result = new Array();

    pair_array = str.split("||");
    for(i=0 ; i < pair_array.length ; i++){
	if(pair_array[i] == ''){  break;  };
	pair = pair_array[i].split(',');
	gda_attr = pair.shift();
	gda_value = pair.join(',');
	if(gda_attr != 'id' && gda_attr != 'mph' && gda_attr != 'sem' &&
	   gda_attr.substr(0,2) != 'eq'){
	    result.push(gda_attr);
	    result.push(gda_value);
	}
    }
    return result;
}

/*
 * 「関係」のリストを表示するポップアップメニューを作成する
 */
function gen_relation_menu_content (rel_list){
    var i, menu, rel, referent, deictic, ref_id;

    menu = '<table class="relation">';
    menu += '<tr><td class="floating_popup_menu_header"></td></tr>';
    for(i=0 ; i < rel_list.length ; i += 2){
	rel = rel_list[i];
	referent = rel_list[i+1];
	deictic = deictic_string(referent);
	if(deictic == null){
	    ref_id = '\'' + referent + '\'';
	}else{
	    //ref_id = 'null';
	    ref_id = '\'D:' + deictic + '\'';
	}

	menu += '<tr><td class="popupmenu_relation_item"';
	menu += ' style="background-color:'+relation_popupmenu_bg_color+';"';
	menu += ' onmouseover="highlight_relation(this,'+ref_id+',1)"';
	menu += ' onmouseout="highlight_relation(this,'+ref_id+',0)"';
	menu += ' onmouseup="hide_floating_popup_menu();hide_underline()"';
	menu += '>';
	menu += '<span class="attr">'+rel+'</span>';
	menu += '</td>';
	/*
	if(deictic != null){
	    menu += '<td class="deictic">';
	    //menu += '<span class="deictic" style="color:'+relation_popupmenu_bg_color+';">';
	    //menu += ' -&gt; ' + deictic + '</span>';
	    menu += ' → ' + deictic;
	    menu += '</td>';
	}
	*/
	menu += '</tr>';
    }
    menu += '</table>';

    return menu;
}

/* 
 * 「関係」の参照先が直示のとき、内容を説明する文字列を返す関数
 */
function deictic_string (str) {
    if(str == 'p0'){
	return "p0(一般人称)";
    }else if(str == 'p1'){
	return "p1(一人称単数)";
    }else if(str == 'p1p'){
	return "p1p(一人称複数)";
    }else if(str == 'p1i'){
	return "p1i(受話者を含む一人称複数)";
    }else if(str == 'p1x'){
	return "p1x(受話者を含まない一人称複数)";
    }else if(str == 'p2'){
	return "p2(二人称単数)";
    }else if(str == 'p2p'){
	return "p2p(二人称複数)";
    }else if(str == 'nil'){
	return "nil(指示物不在)";
    }else if(str == 'top'){
	return "top(談話全体)";
    }else if(str == 'self'){
	return "self(自身)";
    }else if(str == 'fwd'){
	return "fwd(前方)";
    }else if(str == 'bwd'){
	return "bwd(後方)";
    }else if(str == 'mcn'){
	return "mcn(minimal container noun)";
    }else if(str == 'mention'){
	return "mention";
    }else{
	return null;
    }
}
// フローティングメニューを表示する
// 情報源: http://www.b-s-c.co.jp/~moritake/oboegaki/h_js_smp076.html

var bwType = null;
var trgObj = null;
var downFlag = false;
var overFlag = false;
var objPosX;
var objPosY;
var mousePosX;
var mousePosY;

function floating_menu_moveObj() {
    var posX = mousePosX - objPosX;
    var posY = mousePosY - objPosY;
    moveObject(trgObj,posX,posY);
    /*
    if ( bwType == 1 ) {
	trgObj.style.posLeft = posX;
	trgObj.style.posTop = posY;
    } else if ( bwType == 2 ) {
	trgObj.style.left = posX + 'px';
	trgObj.style.top = posY + 'px';
    } else {
	trgObj.moveTo( posX, posY );
    }
    */
}

//function mouseOver(e) {
//    overFlag = true;
//}

//function mouseOut(e) {
//    overFlag = false;
//}

function floating_menu_mouseDown(e) {
    /*
    var mouse_x, mouse_y, obj_x, obj_y;
    if ( bwType == 1 ) {
	mouse_x = document.body.scrollLeft + event.clientX;
	mouse_y = document.body.scrollTop + event.clientY;
    } else {
	mouse_x = e.pageX;
	mouse_y = e.pageY;
    }
    obj_x = GetLeft(trgObj);
    obj_y = GetTop(trgObj);
    if(mouse_x >= obj_x && mouse_x <= obj_x + trgObj.offsetWidth &&
       mouse_y >= obj_y && mouse_y <= obj_y + trgObj.offsetHeight){
	overFlag = true;
    }else{
	overFlag = false;
    }
    */

    //if ( overFlag ) {
	if ( bwType == 1 ) {
	    objPosX = event.offsetX;
	    objPosY = event.offsetY;
	} else {
	    objPosX = e.layerX;
	    objPosY = e.layerY;
	}
	downFlag = true;
    //}
}

function floating_menu_mouseMove(e) {
    if ( downFlag ) {
	if ( bwType == 1 ) {
	    mousePosX = document.body.scrollLeft + event.clientX;
	    mousePosY = document.body.scrollTop + event.clientY;
	} else {
	    mousePosX = e.pageX;
	    mousePosY = e.pageY;
	}
	floating_menu_moveObj();
	return false;
    }
}

function floating_menu_mouseUp( e ) {
    if ( downFlag ) {
	downFlag = false;
    }
}

function bwCheck(){
    if ( document.all ) {	// IE, Opera, Safari, Crome
	return (1);
    } else if ( document.getElementById ) {	// Firefox
	return (2);
    } else if ( document.layers ) {	// Other(?)
	return (3);
    } else {
	return (null);
    }
}

function formLoad( id ){
    bwType = bwCheck();
    switch ( bwType ) {
    case 1:
	trgObj = document.all( id );
	break;
    case 2:
	trgObj = document.getElementById( id );
	break;
    case 3:
	trgObj = document.layers[ id ];
	document.captureEvents(Event.MOUSEDOWN | Event.MOUSEMOVE | Event.MOUSEUP);
	break;
    default:
	return;
    }
    //document.onmousedown = floating_menu_mouseDown;
    //document.onmouseup = floating_menu_mouseUp;
    //document.onmousemove = floating_menu_mouseMove;
    //trgObj.onmouseover = mouseOver;
    //trgObj.onmouseout = mouseOut;
    trgObj.onmousedown = floating_menu_mouseDown;
    trgObj.onmouseup = floating_menu_mouseUp;
    trgObj.onmousemove = floating_menu_mouseMove;
}
/* ユーティリティ関数
 * ブラウザに依存する処理はこのファイルで書く
 */

// クリックしたときのマウスの位置 (body上の座標)
var mouseX, mouseY;
// クリックしたときのマウスの位置 (クリックした要素上の座標)
var mouseOffsetX, mouseOffsetY;
// ウィンドウの内部領域の大きさ
var windowInnerWidth, windowInnerHeight;
// ウィンドウの外部の大きさ
var windowOuterWidth, windowOuterHeight;
// スクロール時、ウィンドウの左上隅の点のページ全体における位置
var scrollOffsetX, scrollOffsetY;

/*
 * ウィンドウの大きさを得る
 * windowInnerWidth, windowInnerHeight に結果を保存
function getInnerWindowSize(){
    getInnerWindowWidth();
    getInnerWindowHeight();
}
 */
/*
 * ウィンドウの幅を得る
 * windowInnerWidth に結果を保存
function getInnerWindowWidth(){
    //if(window.innerWidth){	// Mozilla, Opera, NN4
    //	windowInnerWidth = window.innerWidth;
    //}else if(document.documentElement &&
    //	     document.documentElement.clientWidth){ // 以下 IE
    //	windowInnerWidth = document.documentElement.clientWidth;
    //}else if(document.body && document.body.clientWidth){
    //	windowInnerWidth = document.body.clientWidth;
    //}
    windowInnerWidth = document.documentElement.clientWidth;	// IE6
    if(windowInnerWidth == null){ 				// IE5
	windowInnerWidth = document.body.clientWidth;
    };
    if(windowInnerWidth == null){ 				// other
	windowInnerWidth = window.innerWidth;
    };
}
 */
/*
 * ウィンドウの幅を得る
 * windowInnerHeight に結果を保存
function getInnerWindowHeight(){
    //if(window.innerHeight){	 // Mozilla, Opera, NN4
    //	windowInnerHeight = window.innerHeight;
    //}else if(document.documentElement && 
    //    	     document.documentElement.clientHeight){ // 以下 IE
    //	windowInnerHeight = document.documentElement.clientHeight;
    //}else if (document.body && document.body.clientHeight){
    //	windowInnerHeight = document.body.clientHeight;
    //}
    //windowInnerHeight = document.documentElement.clientHeight;
    //if(windowInnerHeight == null){
    //windowInnerHeight = document.body.clientHeight;
    //};
    //if(windowInnerHeight == null){
    //windowInnerHeight = window.innerHeight;
    //};
    windowInnerHeight = document.documentElement.clientHeight||document.body.clientHeight||document.body.scrollHeight;
}
 */


var isWin9X = (navigator.appVersion.toLowerCase().indexOf('windows 98')+1);
var isIE = (navigator.appName.toLowerCase().indexOf('internet explorer')+1?1:0);
var isOpera = (navigator.userAgent.toLowerCase().indexOf('opera')+1?1:0);
if (isOpera) isIE = false;
var isSafari = (navigator.appVersion.toLowerCase().indexOf('safari')+1?1:0);

/*
 * ウィンドウの幅と高さを得る
 *   windowInnerWidth、windowInnerHeight に結果を保存
 */
function getScreenSize() {
    if (!isSafari && !isOpera) {
	windowInnerWidth = document.documentElement.clientWidth || document.body.clientWidth || document.body.scrollWidth;
	windowInnerHeight = document.documentElement.clientHeight || document.body.clientHeight || document.body.scrollHeight;
    } else {
	windowInnerWidth = window.innerWidth;
	windowInnerHeight = window.innerHeight;
    }
}

/*
 * ウィンドウの外枠の幅と高さを得る (メニューバーなども含む)
 *   windowOuterWidth、windowOuterHeight に結果を保存
 */
function getOuterWindowSize(){
    if(window.outerWidth){
	windowOuterWidth = window.outerWidth;
    }
    if(document.documentElement.clientWidth){
	windowOuterWidth = document.documentElement.clientWidth + 24;
    }
    if(document.body.clientWidth){
	windowOuterWidth = document.body.clientWidth + 24;
    }

    if(window.outerHeight){
	windowOuterHeight = window.outerHeight;
    }
    if(document.documentElement.clientHeight){
	windowOuterWidth = document.documentElement.clientHeight + 24;
    }
    if(document.body.clientHeight){
	windowOuterWidth = document.body.clientHeight + 24;
    }
}

/*
 * スクロールの位置(現在の表示画面の左上隅の位置のページ全体における座標)
 *   scrollOffsetX, scrollOffsetY に結果を保存
 */
function getScrollPosition() {
    scrollOffsetX = document.documentElement.scrollLeft || document.body.scrollLeft;
    scrollOffsetY = document.documentElement.scrollTop || document.body.scrollTop;
}

/*
function getScrollOffset() {
    if(window.pageXOffset){ scrollOffsetX = window.pageXOffset; };
    if(document.body.scrollLeft){ scrollOffsetX = document.body.scrollLeft; };
    scrollOffsetX = 0;

    if(window.pageYOffset){ scrollOffsetY = window.pageYOffset; };
    if(document.body.scrollTop){ scrollOffsetY = document.body.scrollTop; };
    scrollOffsetY = 0;
}
*/

/*
 * クリックしたときのマウスの位置を得る
 * mouseX, mouseY, mouseOffsetX, mouseOffsetY に結果を保存
 */
//window.document.onmousedown = getMouseXY;
function getMouseXY(evt) {
    if (window.createPopup){	// IEの処理
	mouseX = event.x + document.body.scrollLeft;
	mouseY = event.y + document.body.scrollTop;
    }else{
	mouseX = evt.pageX;
	mouseY = evt.pageY;
    };
    if (document.all) {		// IEまたはOperaのときの処理
	mouseOffsetX = event.offsetX;
	mouseOffsetY = event.offsetY;
    }else{
	mouseOffsetX = evt.layerX;
	mouseOffsetY = evt.layerY;
    }
}

/****************************************************************
* 機　能： オブジェクトの左位置を取得
* 引　数： オブジェクト
* 戻り値： 左からのピクセル数
****************************************************************/
function getLeft(oj){
    var px = 0;
    while(oj){
	px += oj.offsetLeft;
        oj = oj.offsetParent;
    }
    return px;
}
/****************************************************************
* 機　能： オブジェクトの上位置を取得
* 引　数： オブジェクト
* 戻り値： 上からのピクセル数
****************************************************************/
function getTop(oj){
    var px = 0;
    while(oj){
        px += oj.offsetTop;
        oj = oj.offsetParent;
    }
    return px;
}
/*
 * イベントが発生した親のオブジェクトを得る
 */
function getTargetObj (eve){
    if(eve.target) return eve.target;
    if(eve.srcElement) return eve.srcElement;	// for IE
    return null;
}

/*
 * HTML要素を指定の座標に移動する
 * HTML要素は style="position: absolute;" の指定をしていること
 */
function moveObject(obj,x,y){
    if ( bwType == 1 ) {	// defined in floating_menu.js
	obj.style.posLeft = x;
	obj.style.posTop = y;
    } else if ( bwType == 2 ) {
	// Firefox でXMLを直接表示するときは px をつけないと動作しない
	obj.style.left = x + 'px';
	obj.style.top = y + 'px';
    } else {
	obj.moveTo( x, y );
    }
}


/*
 * class属性の値を得る
 */
function get_class_value (obj){
    var v;
    // IEでは機能しない
    //v = obj.getAttribute("class");
    v = obj.className;

    if(v == null || v == "") return null;
    return v;
}

/*
 * class属性に指定の文字列が含まれるかをチェックする
 */
function class_contains (obj,key){
    var v = get_class_value(obj);
    if(v == null) return false;

    if(v.indexOf(key) == -1){
        return false;
    }else{
        return true;
    }
}

/* 第2引数のリストの中に第1引数が含まれてるかを返す関数 */
function member(key,list){
    var i;

    for(i=0 ; i < list.length ; i++){
	if(list[i] == key) return true;
    }
    return false;
}

/* リストを2つ引数に取り、共通の要素が含まれてるかを返す関数 */
function has_intersection(list1,list2){
    var i,j;

    for(i=0 ; i < list1.length ; i++){
	for(j=0 ; j < list2.length ; j++){
	    if(list1[i] == list2[j]) return true;
	}
    }
    return false;
}
