// 滚动条在Y轴上的滚动距离
function getScrollTop() {

	var scrollTop = 0, bodyScrollTop = 0, documentScrollTop = 0;

	if (document.body) {

		bodyScrollTop = document.body.scrollTop;
	}
	if (document.documentElement) {

		documentScrollTop = document.documentElement.scrollTop;
	}
	scrollTop = (bodyScrollTop - documentScrollTop > 0) ? bodyScrollTop: documentScrollTop;

	return scrollTop;
}
// 文档的总高度
function getScrollHeight() {

	var scrollHeight = 0, bodyScrollHeight = 0, documentScrollHeight = 0;

	if (document.body) {

		bodyScrollHeight = document.body.scrollHeight;
	}
	if (document.documentElement) {

		documentScrollHeight = document.documentElement.scrollHeight;
	}
	scrollHeight = (bodyScrollHeight - documentScrollHeight > 0) ? bodyScrollHeight: documentScrollHeight;

	return scrollHeight;
}
// 浏览器视口的高度
function getWindowHeight() {
	var windowHeight = 0;

	if (document.compatMode == "CSS1Compat") {

		windowHeight = document.documentElement.clientHeight;
	} else {

		windowHeight = document.body.clientHeight;
	}
	return windowHeight;
}

function isMobileDevice(){
	if(navigator.userAgent.match(/(Android|windows ce|windows mobile|midp)/i)||isIphoneDevice()){
		  return false;  //移动端
	}else{
		return true  //pc端
	}
}

function isIphoneDevice(){
	if(navigator.userAgent.match(/(iPhone|iPod|ipad|ios)/i)){
		return true;
	}else{
		return false;
	}
}


function setUnderlineColor(sellUnderlineNode,buyUnderlineNode,sellUnderlineColor,buyUnderlineColor){
	sellUnderlineNode.setAttribute("style",sellUnderlineColor);
	buyUnderlineNode.setAttribute("style",buyUnderlineColor);
}

/**
* 删除原有记录
*/
function deleteRecord(id) {
	var tableNode = document.getElementById(id);
	var rows = tableNode.rows.length;
	if (rows == 0) {
		return 0;
	}
	var trNodes = tableNode.getElementsByTagName("tr");
	while (trNodes.length > 0) {
		tableNode.tBodies[0].removeChild(trNodes[0]);
	}
}

/**
*创建XMLHttpRequest对象
 */
function createXMLHTTPRequest() {
	// 需要针对IE和其他类型的浏览器建立这个对象的不同方式写不同的代码
	var xmlHttpRequest;
	if (window.XMLHttpRequest) {
		// 针对FireFox，Mozillar，Opera，Safari，IE7，IE8
		xmlHttpRequest = new XMLHttpRequest();
		// 针对某些特定版本的mozillar浏览器的BUG进行修正
		if (xmlHttpRequest.overrideMimeType) {
			xmlHttpRequest.overrideMimeType("text/xml");
		}
	} else if (window.ActiveXObject) {
		// 针对IE6，IE5.5，IE5
		// 两个可以用于创建XMLHTTPRequest对象的控件名称，保存在一个js的数组中
		// 排在前面的版本较新
		var activexName = [ "MSXML2.XMLHTTP", "Microsoft.XMLHTTP" ];
		for (var i = 0; i < activexName.length; i++) {
			try {
				// 取出一个控件名进行创建，如果创建成功就终止循环
				// 如果创建失败，回抛出异常，然后可以继续循环，继续尝试创建
				xmlHttpRequest = new ActiveXObject(activexName[i]);
				if (xmlHttpRequest) {
					break;
				}
			} catch (e) {
				alert(e);
			}
		}
	}
	return xmlHttpRequest;
}



/**
* 插入多行记录
*/
function commonInsertRow(tableNode, rowHtml) {
	if (rowHtml == "") {
		return 0;
	}
	var divNode = document.createElement("div"), row;
	divNode.innerHTML = "<table><tbody>" + rowHtml + "</tbody></table>";
	row = divNode.childNodes[0].tBodies[0].rows;
	while (row.length > 0) {
		tableNode.tBodies[0].appendChild(row[0]);
	}
}

function setPrompt(brower){
	var height=document.body.scrollHeight+"px";
	var styleValue="width:100%;height:"+height+";position:absolute;top:0px;background: white;display:block;";
	if(brower=="qqInnerBrower"){
		document.getElementById("qqImg").setAttribute("style", styleValue);
	}else{
		document.getElementById("weiXinImg").setAttribute("style", styleValue);
	}
	//滚动条回到顶端
	document.getElementsByTagName('body')[0].scrollTop = 0;
}


function isWeiXinBrower(){
	var ua = window.navigator.userAgent.toLowerCase();
	    if(ua.match(/MicroMessenger/i) == 'micromessenger'){
		    return true;
	    }else{
		    return false;
	    }
}

function isQQInnerBrower(){
	if (navigator.userAgent.match(/QQ\//i) == "QQ/") {
		return true;
	} else {
		return false;
	}
}


/**
 * 通过iframe的方式试图打开APP，如果能正常打开，会直接切换到APP，
 * 否则直接跳转到下载页
 */
function jump(url){
	var timeout, t = 400, hasApp = true, jumpTimeOut = 3000;
        setTimeout(function () {
            if (hasApp) {
            } else {
                window.location.href = "http://a.app.qq.com/o/simple.jsp?pkgname=cc.quanhai.youbiquan&amp;g_f=991653";
            }
        }, jumpTimeOut);
        var startTime = Date.now();
        window.location.href =url;
        timeout = setTimeout(function () {
            var endTime = Date.now();
            if (!startTime || endTime - startTime < t + 30) {
                hasApp = false;
            }
        }, t);
}

