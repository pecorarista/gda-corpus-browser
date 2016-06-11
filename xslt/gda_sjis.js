// This is automatically generate file
/*
 * GDA�R�[�p�X�ɕt�^����Ă���^�O�����u���E�Y����
 */

/* ���ϐ� */

// id�����������\������ۂ̔w�i�F
//var highlight_id_background_color = '#FFCC33'; // �I�����W
var highlight_id_background_color = '#FFFF00';
// eq�����������\������ۂ̔w�i�F
var highlight_eq_background_color = '#FFFF00';
// �u�֌W�v���X�g�̃|�b�v�A�b�v���j���[�̔w�i�F
var relation_popupmenu_bg_color = '#E6E6E6';
// �u�֌W�v���X�g�̃|�b�v�A�b�v���j���[��I�������Ƃ��̔w�i�F
var relation_popupmenu_highlight_bg_color = '#FF99FF';
// �u�֌W�v���X�g���̒����̏��̐F
var relation_popupmenu_deictic_fg_color = '#009900';

// ������\�����Ă���Ƃ��A���̗v�f
var current_obj_underline = null;
// ���Q�Ƃ������\�����Ă���Ƃ��A���̑ΏۂƂȂ��Ă���ID
var current_id_coreference = null;
// ���Q�Ƃ������\�����Ă���Ƃ��A
// �}�E�X�N���b�N�ɂ���Ă��̕\���𐶂��������I�u�W�F�N�g
var current_source_obj_coreference = null;
// �|�b�v�A�b�v���j���[��\�����Ă���Ƃ��A
// �}�E�X�N���b�N�ɂ���Ă��̕\���𐶂��������I�u�W�F�N�g
var current_source_obj_floating_popup_menu = null;
// �֌W(�֌W���ƎQ�Ɛ�)�������\�����Ă���Ƃ��A���̎Q�Ɛ��ID
var current_id_relation = null;
// �����̊֌W�������\�����Ă���Ƃ��A������\�����Ă���I�u�W�F�N�g
var current_obj_deictic = null;

// �Ō�Ƀ}�E�X���N���b�N���ꂽ����
var last_click_time = 0;


/*
 * �N���b�N�����v�f�ɉ����������֐�
 */
function show_underline(obj){
    obj.style.textDecoration = "underline";
    current_obj_underline = obj;
}
/*
 * ��������������֐�
 */
function hide_underline(){
    if(current_obj_underline == null) return;

    current_obj_underline.style.textDecoration = '';
    current_obj_underline = null;
}

/*
 * �|�b�v�A�b�v���j���[���쐬/�\������
 */
function show_floating_popup_menu(obj,eve,menu){
    var i, objX, objY, top, left, relX, relY, obj_style, font_size;
    var menu_width, menu_height, wrap_lower_flag, wrap_upper_flag;

    var popup = document.getElementById('floating_popup_menu');

    // �I�u�W�F�N�g�̈ʒu�𓾂�
    objX = getLeft(obj);
    objY = getTop(obj);
    // �}�E�X�̈ʒu�𓾂�
    getMouseXY(eve);
    // �E�B���h�E�̓����̈�̃T�C�Y�𓾂�
    getScreenSize();
    // �X�N���[���̃I�t�Z�b�g�𓾂�
    getScrollPosition();
    // �I�u�W�F�N�g�̃X�^�C���𓾂�
    obj_style = obj.currentStyle || document.defaultView.getComputedStyle(obj,'');
    // �t�H���g�T�C�Y�𓾂�
    font_size = (obj_style == null) ?
		 obj.offsetHeight : parseInt(obj_style.fontSize);

    // ���j���[��\������
    moveObject(popup,0,0);	// �T�C�Y�𓾂邽�߈�x(0,0)�̈ʒu�ŕ\������
    popup.innerHTML = menu;
    popup.style.visibility = 'visible';

    // ���j���[�̈ʒu��ύX����

    //// ���j���[�̃T�C�Y�𓾂�
    menu_width = popup.offsetWidth;
    menu_height = popup.offsetHeight;

    //// left �����߂�
    if(objX > mouseX){
	// 2�s�ɂ܂�����v�f�̉��̍s���N���b�N�����Ƃ�
	wrap_lower_flag = 1;	
	left = mouseX;
    }else{
	wrap_lower_flag = 0;
	left = objX;
    }
    relX = left - scrollOffsetX;
    if( relX + menu_width + 20 > windowInnerWidth ){
	// body�v�f�̗]�����l�����A�����]���ɍ��Ɋ񂹂�
	left = windowInnerWidth - menu_width - 20;
	if(left < 0) left = 0;
    }
    //// top �����߂�
    if(wrap_lower_flag){
	top = mouseY + font_size + 1;
    }else{
	if(obj.offsetHeight > font_size * 2){
	    // 2�s�ɂ܂�����v�f�̏�̍s���N���b�N�����Ƃ�
	    wrap_upper_flag = 1;
	    top = mouseY + font_size + 1;
	}else{
	    wrap_upper_flag = 0;
	    top = objY + obj.offsetHeight + 3;
	}
    }
    relY = top - scrollOffsetY;
    if( relY + menu_height + 20 > windowInnerHeight ){
	// +20 ��body�v�f�̗]�����l����������
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
 *  �|�b�v�A�b�v���j���[������
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
 * �S�Ẵf�R���[�V���������Z�b�g����
 * id, eq, �֌W�̃}�[�J�[��ŌĂяo��
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
 * class�����̒l����A
 * ����GDA�t�@�C���ɂ����� eq(��������eq.*)�����̒l�����o���֐�
 *   ����=HTML�v�f
 *   �ߒl=eq�����̒l  ��������  null
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
 * �}�E�X���A�����ăN���b�N���ꂽ�����`�F�b�N����
 * onmouseup�C�x���g�Ɋ֐������蓖�Ă�ꂽspan�v�f������q�ɂȂ��Ă��āA
 * �֐���2��Ăяo���ꂽ�Ƃ����܂�
 *   �ߒl: �A���N���b�N�Ȃ� true�A����ȊO�� false
 */
function check_duplicate_mouse_click(){
    var now = new Date();
    var t = now.getTime();
    if(t - last_click_time < 500){	// �P�ʂ̓~���b
	return true;
    }
    last_click_time = t;
    return false;
}

/* �C�x���g(�}�E�X�̈ʒu��{�^���̎��)���擾����ɂ́A
 * �ȉ��̂悤�Ɋ֐����o�C���h����K�v������
 * HTML �̒��� <span onmouseup="func()"> �̂悤�ɏ����Ă�
 * �C�x���g�͎擾�ł��Ȃ�
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
 * �`�ԑf���(mph,sem)�������u���E�Y����
 */

/*
 * �P���Ń}�E�X���N���b�N�����Ƃ��̃A�N�V����
 * ��1�����̓C�x���g
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

/* ����GDA�t�@�C���̑����ƒl��\�킷�����񂩂�A
 * mph, sem �����̒l�����o���֐�
 * �ߒl�� ( mph�����̒l, sem�����̒l ) �Ƃ������X�g
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
 * �`�ԑf����\������|�b�v�A�b�v���j���[���쐬����
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
		menu += '<td class="attr">mph(�i��)</td>';
		menu += '<td class="value">'+mor_info[0]+'</td></tr>';
	    }
	    if(mor_info[1] != ''){
		menu += '<tr onmouseup="hide_floating_popup_menu()">';
		menu += '<td class="attr">mph(���p�`)</td>';
		menu += '<td class="value">'+mor_info[1]+'</td></tr>';
	    }
	    if(mor_info[2] != ''){
		menu += '<tr onmouseup="hide_floating_popup_menu()">';
		menu += '<td class="attr">mph(��{�`)</td>';
		menu += '<td class="value">'+mor_info[2]+'</td></tr>';
	    }
	    if(mor_info[3] != ''){
		menu += '<tr onmouseup="hide_floating_popup_menu()">';
		menu += '<td class="attr">mph(�ǂ�)</td>';
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
 * ���Q�Ƃ̏����u���E�Y����
 */

/*
 * id�܂���eq�����̃}�[�J��Ń}�E�X���N���b�N�����Ƃ��̃A�N�V����
 * ��1������HTML�v�f
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
 * ���Q�Ɗ֌W�ɂ���v�f�������\������֐�
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
    // �v�f��1�ȏ゠��Ƃ��A�w�i�F��ς���
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
 * ���Q�Ɗ֌W�ɂ���v�f�������\�����������֐�
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
    // �w�i�F������
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
 * �Ή���̂Ȃ�eq����,id�����̃}�[�J�[���\���ɂ���
 */
function hide_invalid_markers() {
    var i, j, j2, id, flag, span_objs, eq_objs, id_objs;
    var eq_val, eq_val2, eq_val_list;

    span_objs = document.getElementsByTagName('span');
    //eq_objs = new Array();	// ����GDA�t�@�C����eq���������^�O
    eq_objs = new Array();	// eq�����̃}�[�J�[ (��)
    id_objs = new Array();	// id�����̃}�[�J�[ (��)
    for(i=0 ; i < span_objs.length ; i++){
	//if( get_eq_value(span_objs[i]) != null ){
	if(span_objs[i].className == "marker_eq"){
	    eq_objs.push(span_objs[i]);
	//}else if(span_objs[i].getAttribute("class") == "marker_id"){
	}else if(span_objs[i].className == "marker_id"){
	    id_objs.push(span_objs[i]);
	}	    
    }

    // ������id�����̃}�[�J�[������
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
    // ������eq�����̃}�[�J�[������
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

    /* �Â��o�[�W����
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
 * ���Ԃ́u�֌W�v���u���E�Y����
 */

/*
 * �֌W��\�킷�}�[�J��Ń}�E�X���N���b�N�����Ƃ��̃A�N�V����
 * ��1�����̓C�x���g
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

	// �|�b�v�A�b�v���j���[��\������
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
 * �֌W�������\������֐�
 * ��1����=�|�b�v�A�b�v���j���[�̍��ڂ̃I�u�W�F�N�g
 * ��2����=�u�֌W�v�̎Q�Ɛ�I�u�W�F�N�g��ID
 * ��3����=�����\�����邩(1)����(0)��
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
	    // �����F��w�i�Ɠ����ɂ��ĉB��
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
 * �����̊֌W��\������֐�
 *   ��1����=�����̏��
 *   ��2����=�����̏���\��������I�u�W�F�N�g(div�v�f)
 *   ��3����=�֌W�̎�ނ�\�����Ă���I�u�W�F�N�g(td�v�f)
 */
function show_deictic(deictic,d_obj,r_obj) {
    var content, r_objX, r_objY, r_objWidth, d_objWidth, relX;

    // �����e�[�u�����쐬
    content = '<table class="relation"><tr>';
    content += '<td class="deictic_arrow"> = </td>';
    content += '<td class="deictic">' + deictic + '</td>';
    content += '</tr></table>';

    // �֌W��\�����Ă���I�u�W�F�N�g�̈ʒu�𓾂�
    r_objX = getLeft(r_obj);
    r_objY = getTop(r_obj);
    // �E�B���h�E�̓����̈�̃T�C�Y�𓾂�
    getScreenSize();
    // �X�N���[���̃I�t�Z�b�g�𓾂�
    getScrollPosition();

    // �����e�[�u����\������
    moveObject(d_obj,0,0);	// �T�C�Y�𓾂邽�߈�x(0,0)�̈ʒu�ŕ\������
    d_obj.innerHTML = content;
    d_obj.style.visibility = 'visible';

    // �����e�[�u���̈ʒu�����߁A�\������

    r_objWidth = r_obj.offsetWidth;
    d_objWidth = d_obj.offsetWidth;

    relX = r_objX - scrollOffsetX;
    if( relX + r_objWidth + d_objWidth > windowInnerWidth ){
	// �����̏����E�ɕ\��
	content = '<table class="relation"><tr>';
	content += '<td class="deictic">' + deictic + '</td>';
	content += '<td class="deictic_arrow"> = </td>';
	content += '</tr></table>';
	d_obj.innerHTML = content;

	moveObject(d_obj, r_objX - d_objWidth - 1, r_objY);
    }else{
	// �����̏������ɕ\��
	moveObject(d_obj, r_objX + r_objWidth + 1, r_objY);
    }
}

/*
 * ����GDA�t�@�C���̑����ƒl��\�킷�����񂩂�A
 * �֌W��\�킷�����Ƃ��̒l�����o���֐�
 * �ߒl�� ( ����1, �l1, ����2, �l2, ... ) �Ƃ������X�g
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
 * �u�֌W�v�̃��X�g��\������|�b�v�A�b�v���j���[���쐬����
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
	    menu += ' �� ' + deictic;
	    menu += '</td>';
	}
	*/
	menu += '</tr>';
    }
    menu += '</table>';

    return menu;
}

/* 
 * �u�֌W�v�̎Q�Ɛ悪�����̂Ƃ��A���e��������镶�����Ԃ��֐�
 */
function deictic_string (str) {
    if(str == 'p0'){
	return "p0(��ʐl��)";
    }else if(str == 'p1'){
	return "p1(��l�̒P��)";
    }else if(str == 'p1p'){
	return "p1p(��l�̕���)";
    }else if(str == 'p1i'){
	return "p1i(��b�҂��܂ވ�l�̕���)";
    }else if(str == 'p1x'){
	return "p1x(��b�҂��܂܂Ȃ���l�̕���)";
    }else if(str == 'p2'){
	return "p2(��l�̒P��)";
    }else if(str == 'p2p'){
	return "p2p(��l�̕���)";
    }else if(str == 'nil'){
	return "nil(�w�����s��)";
    }else if(str == 'top'){
	return "top(�k�b�S��)";
    }else if(str == 'self'){
	return "self(���g)";
    }else if(str == 'fwd'){
	return "fwd(�O��)";
    }else if(str == 'bwd'){
	return "bwd(���)";
    }else if(str == 'mcn'){
	return "mcn(minimal container noun)";
    }else if(str == 'mention'){
	return "mention";
    }else{
	return null;
    }
}
// �t���[�e�B���O���j���[��\������
// ���: http://www.b-s-c.co.jp/~moritake/oboegaki/h_js_smp076.html

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
/* ���[�e�B���e�B�֐�
 * �u���E�U�Ɉˑ����鏈���͂��̃t�@�C���ŏ���
 */

// �N���b�N�����Ƃ��̃}�E�X�̈ʒu (body��̍��W)
var mouseX, mouseY;
// �N���b�N�����Ƃ��̃}�E�X�̈ʒu (�N���b�N�����v�f��̍��W)
var mouseOffsetX, mouseOffsetY;
// �E�B���h�E�̓����̈�̑傫��
var windowInnerWidth, windowInnerHeight;
// �E�B���h�E�̊O���̑傫��
var windowOuterWidth, windowOuterHeight;
// �X�N���[�����A�E�B���h�E�̍�����̓_�̃y�[�W�S�̂ɂ�����ʒu
var scrollOffsetX, scrollOffsetY;

/*
 * �E�B���h�E�̑傫���𓾂�
 * windowInnerWidth, windowInnerHeight �Ɍ��ʂ�ۑ�
function getInnerWindowSize(){
    getInnerWindowWidth();
    getInnerWindowHeight();
}
 */
/*
 * �E�B���h�E�̕��𓾂�
 * windowInnerWidth �Ɍ��ʂ�ۑ�
function getInnerWindowWidth(){
    //if(window.innerWidth){	// Mozilla, Opera, NN4
    //	windowInnerWidth = window.innerWidth;
    //}else if(document.documentElement &&
    //	     document.documentElement.clientWidth){ // �ȉ� IE
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
 * �E�B���h�E�̕��𓾂�
 * windowInnerHeight �Ɍ��ʂ�ۑ�
function getInnerWindowHeight(){
    //if(window.innerHeight){	 // Mozilla, Opera, NN4
    //	windowInnerHeight = window.innerHeight;
    //}else if(document.documentElement && 
    //    	     document.documentElement.clientHeight){ // �ȉ� IE
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
 * �E�B���h�E�̕��ƍ����𓾂�
 *   windowInnerWidth�AwindowInnerHeight �Ɍ��ʂ�ۑ�
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
 * �E�B���h�E�̊O�g�̕��ƍ����𓾂� (���j���[�o�[�Ȃǂ��܂�)
 *   windowOuterWidth�AwindowOuterHeight �Ɍ��ʂ�ۑ�
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
 * �X�N���[���̈ʒu(���݂̕\����ʂ̍�����̈ʒu�̃y�[�W�S�̂ɂ�������W)
 *   scrollOffsetX, scrollOffsetY �Ɍ��ʂ�ۑ�
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
 * �N���b�N�����Ƃ��̃}�E�X�̈ʒu�𓾂�
 * mouseX, mouseY, mouseOffsetX, mouseOffsetY �Ɍ��ʂ�ۑ�
 */
//window.document.onmousedown = getMouseXY;
function getMouseXY(evt) {
    if (window.createPopup){	// IE�̏���
	mouseX = event.x + document.body.scrollLeft;
	mouseY = event.y + document.body.scrollTop;
    }else{
	mouseX = evt.pageX;
	mouseY = evt.pageY;
    };
    if (document.all) {		// IE�܂���Opera�̂Ƃ��̏���
	mouseOffsetX = event.offsetX;
	mouseOffsetY = event.offsetY;
    }else{
	mouseOffsetX = evt.layerX;
	mouseOffsetY = evt.layerY;
    }
}

/****************************************************************
* �@�@�\�F �I�u�W�F�N�g�̍��ʒu���擾
* ���@���F �I�u�W�F�N�g
* �߂�l�F ������̃s�N�Z����
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
* �@�@�\�F �I�u�W�F�N�g�̏�ʒu���擾
* ���@���F �I�u�W�F�N�g
* �߂�l�F �ォ��̃s�N�Z����
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
 * �C�x���g�����������e�̃I�u�W�F�N�g�𓾂�
 */
function getTargetObj (eve){
    if(eve.target) return eve.target;
    if(eve.srcElement) return eve.srcElement;	// for IE
    return null;
}

/*
 * HTML�v�f���w��̍��W�Ɉړ�����
 * HTML�v�f�� style="position: absolute;" �̎w������Ă��邱��
 */
function moveObject(obj,x,y){
    if ( bwType == 1 ) {	// defined in floating_menu.js
	obj.style.posLeft = x;
	obj.style.posTop = y;
    } else if ( bwType == 2 ) {
	// Firefox ��XML�𒼐ڕ\������Ƃ��� px �����Ȃ��Ɠ��삵�Ȃ�
	obj.style.left = x + 'px';
	obj.style.top = y + 'px';
    } else {
	obj.moveTo( x, y );
    }
}


/*
 * class�����̒l�𓾂�
 */
function get_class_value (obj){
    var v;
    // IE�ł͋@�\���Ȃ�
    //v = obj.getAttribute("class");
    v = obj.className;

    if(v == null || v == "") return null;
    return v;
}

/*
 * class�����Ɏw��̕����񂪊܂܂�邩���`�F�b�N����
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

/* ��2�����̃��X�g�̒��ɑ�1�������܂܂�Ă邩��Ԃ��֐� */
function member(key,list){
    var i;

    for(i=0 ; i < list.length ; i++){
	if(list[i] == key) return true;
    }
    return false;
}

/* ���X�g��2�����Ɏ��A���ʂ̗v�f���܂܂�Ă邩��Ԃ��֐� */
function has_intersection(list1,list2){
    var i,j;

    for(i=0 ; i < list1.length ; i++){
	for(j=0 ; j < list2.length ; j++){
	    if(list1[i] == list2[j]) return true;
	}
    }
    return false;
}
