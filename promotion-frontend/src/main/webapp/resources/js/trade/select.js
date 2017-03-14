var pageOf = 0;
var bottom = false;
var first = false;
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

function scroll() {
	if (bottom) {
		return false;
	}
	document.getElementById("div").style.display = "";
	loadPage();
}
/**
 * 滚动滚定条的时候触发该方法
 */
window.onscroll = function() {
	var u = navigator.userAgent;
	var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); // ios终端
	if (isiOS) {
		if (getScrollHeight() - (getScrollTop() + getWindowHeight()) <= 200) {
			scroll();
		}
	} else {
		if (!first) {
			if (getScrollHeight()- (getScrollTop() + getWindowHeight()) <50) {
				scroll();
				first = true;
			}
		} else {
			if (getScrollHeight()- (getScrollTop() + getWindowHeight()) == 0) {
				scroll();
			}
		}
	}
}

/*
 * 创建XMLHttpRequest对象
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

// 使用post请求
function post(tradeType, pageOf) {
	var req = createXMLHTTPRequest();
	if (req) {
		req.open("post", "/promotion-frontend/trade/page?pageOf="+ pageOf + "&tradeType=" + tradeType, true);
		req.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=gbk;");
		req.send(null);
		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					var content = req.responseText;
					if (content != " ") {
						var tableNode = document
								.getElementById("tb");
						insertRow(tableNode, content);
					}
				} else if (req.status == 404) {
					alert("加载失败");
				} else {
					alert("加载失败");
				}
			}
		}
	}
}

/**
 * 插入多行记录
 */
function insertRow(tableNode, rowHtml) {
	document.getElementById("div").style.display = "none";
	if (rowHtml.length == 3) {
		bottom = true;
		return false;
	}
	var divNode = document.createElement("div"), row;
	divNode.innerHTML = "<table>" + rowHtml + "</table>";
	row = divNode.childNodes[0].tBodies[0].rows;
	while (row.length > 0) {
		tableNode.tBodies[0].appendChild(row[0]);
	}
}

/**
 * 获取交易类型
 */
function tradeStyle(pageOf) {
	var tradeType = "";
	var stockNode = document.getElementById("stock");
	var attribute = "backgroundColor";
	var backgroundColor = stockNode.currentStyle ? stockNode.currentStyle[attribute]: document.defaultView.getComputedStyle(stockNode,false)[attribute];
	if (backgroundColor != "rgb(255, 255, 255)") {
		tradeType = "现货盘";
	} else {
		tradeType = "电子盘";
	}
	post(tradeType, pageOf);
}

/**
 * 加载一页
 */
function loadPage() {
	pageOf++;
	tradeStyle(pageOf);
}

/**
 * 加载窗口事件
 */
window.onload = function() {
	var loadPage = 0;
	tradeStyle(loadPage);
};

/**
 * 删除原有记录
 */
function deleteRecord() {
	var tableNode = document.getElementById("tb");
	var rows = tableNode.rows.length;
	var headerRows = 1;
	var node = tableNode.tBodies[0].firstChild.nextElementSibling
			|| tableNode.tBodies[0].firstChild.nextSibling;
	node = node.nextElementSibling || node.nextSibling;
	for (var i = 0; i < rows - headerRows; i++) {
		tableNode.tBodies[0].removeChild(node);
		node = tableNode.tBodies[0].firstChild.nextElementSibling
				|| tableNode.tBodies[0].firstChild.nextSibling;
		node = node.nextElementSibling || node.nextSibling;
	}
}

/**
 * 切换到现货盘
 */
function showOnclickStock() {
	bottom = false;
	first = false;
	pageOf = 0;
	var tradeType = "现货盘";
	document.getElementById("stock").setAttribute("style","background-color:#ff8941;color:#FFFFFF");
	document.getElementById("electronic").setAttribute("style","background-color:#FFFFFF;color:#ff8941;border-style:solid;border-color:#ff8941");
	deleteRecord();
	post(tradeType, pageOf);
}

/**
 * 切换到电子盘
 */
function showOnclickElectronic() {
	bottom = false;
	first = false;
	pageOf = 0;
	var tradeType = "电子盘";
	document.getElementById("electronic").setAttribute("style","background-color:#ff8941;color:#FFFFFF");
	document.getElementById("stock").setAttribute("style","background-color:#FFFFFF;color:#ff8941;border-style:solid;border-color:#ff8941");
	deleteRecord();
	post(tradeType, pageOf);
}
