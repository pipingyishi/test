var scriptNode=document.createElement("script");
scriptNode.setAttribute("type", "text/javascript");
var projectName=getProjectName();
scriptNode.setAttribute("src", projectName+"/resources/js/common/common.js");
document.body.appendChild(scriptNode);

/**
 * 求购和出售之间相互转换
 * @param state
 * @returns {Number}
 */
function switchStatus(state) {
	var sellUnderlineNode = document.getElementById("sell-underline");
	var buyUnderlineNode = document.getElementById("buy-underline");
	if (sellUnderlineNode == null || buyUnderlineNode == null) {
		return 0;
	}
	var sellNode = document.getElementById("sell");
	var buyNode = document.getElementById("buy");
	if (sellNode == null || buyNode == null) {
		return 0;
	}
	setFontColor(sellNode, buyNode);
	var status = "";
	var sellUnderlineColor,buyUnderlineColor;
	if (state == "sell") {
		if(isMobileDevice()){//pc端
			sellUnderlineColor="border-top: solid 2px #ff8941;width: 100px;float: left;margin-left: 19%;";
			buyUnderlineColor="border-top: solid 2px #ffffff;width: 100px;float: right;margin-right: 16%;";
		}else{
			sellUnderlineColor="border-top: solid 2px #ff8941; width: 23%;position: absolute; left: 15.5%;";
			buyUnderlineColor="border-top: solid 2px #ffffff;width: 23%;position: absolute;right: 13%;";
		}
		setUnderlineColor(sellUnderlineNode,buyUnderlineNode,sellUnderlineColor,buyUnderlineColor);
		status = "sell";
		document.getElementById("type").value = "出售";
	} else {
		if(isMobileDevice()){
			sellUnderlineColor="border-top: solid 2px #ffffff;width: 100px;float: left;margin-left: 19%;";
			buyUnderlineColor="border-top: solid 2px #ff8941;width: 100px;float: right;margin-right: 16%;";
		}else{
			sellUnderlineColor="border-top: solid 2px #ffffff; width: 23%;position: absolute; left: 15.5%;";
			buyUnderlineColor="border-top: solid 2px #ff8941;width: 23%;position: absolute;right: 13%;";
		}
		setUnderlineColor(sellUnderlineNode,buyUnderlineNode,sellUnderlineColor,buyUnderlineColor);
		status = "buy";
		document.getElementById("type").value = "求购";
	}
	deleteRecord("productTable");
	post(status);
}


function setFontColor(sellNode, buyNode) {
	sellNode.setAttribute("style", "text-decoration:none;color:#000;outline:none;");
	buyNode.setAttribute("style", "text-decoration:none;color:#000;outline:none;");
}


/**
 * 加载窗口事件
 */
window.onload = function() {
	if(!isMobileDevice()){
		document.getElementById("qqImg").setAttribute("style", "display:none");
		document.getElementById("weiXinImg").setAttribute("style", "display:none");
	}
	if(document.getElementById("merchant")!=null){//表示该用户是普通用户
		document.getElementById("stockInfo").setAttribute("style", "position: absolute;top: 40%;height: 49.5%;margin-top: 3%;overflow: auto;width: 100%;background: #e9e9e9;");
	}
	var url = window.location.href;
	if (url.indexOf("#buy") > -1) {
		status = "buy";
	} else {
		status = "sell";
	}
	switchStatus(status);
};

/**
 * 使用ajax get请求
 */ 
function post(type) {
	var userId = document.getElementById("userId").value;
	var projectName = getProjectName();
	var req = createXMLHTTPRequest();
	if (req) {
		req.open("get", projectName + "/trust/stock/" + type+ "/" + userId, true);
		req.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=gbk;");
		req.send(null);
		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					var content = req.responseText;// 将数据拆分，回来的数据已经是是tr字符串
					if (content.indexOf("tr")>-1) {
						var tableNode = document.getElementById("productTable");
						insertRow(tableNode, content);
					}
				} else {
				}
			}
		}
	}
}


/**
 * 插入多行记录
 */
function insertRow(tableNode, rowHtml) {
	commonInsertRow(tableNode, rowHtml);
	if(!isMobileDevice()){
		setProductNameWidth(window.screen.width);
	}
}

/**
 * 86:能够设置的有限长度(这里其实是86%,86只是方便计算)
 * 4：提示文字
 * 12：各个所的文字大小
 * 16：设备分辨率下的文字的大小
 * 8:一个英文字符占8px
 * 60:设置各个所的样式宽度为60px；
 * @param screenWidth
 */
function setProductNameWidth(screenWidth){
	var enableLength=86;
	var titleFontSize=12;
	var tipFontCount=4;
	var normalFontSize=16
	var halfAngleFontSize=8;
	var percent=100;
	var titleWidth=60;
	var tableNode = document.getElementById("productTable");
	var trNodes = tableNode.getElementsByTagName("tr");
	for(var i=0;i<trNodes.length;i++){
		if(i%3==0){//3代表藏品名和价格每3次出现一次
			var divNode=trNodes[i].getElementsByTagName("div");
			var productName=divNode[1].innerHTML;
			var titleNode=divNode[0].getElementsByTagName("div")[1].getElementsByTagName("button")[0];
			var title=titleNode.innerHTML;
			var price=divNode[3].innerHTML;
			var length=enableLength-(title.length*titleFontSize+tipFontCount*normalFontSize+(price.length-tipFontCount)*halfAngleFontSize)/screenWidth*percent;//可以分给他的宽度
			var productLength=productName.replace(/(^\s*)|(\s*$)/g, "").length*normalFontSize/screenWidth*percent;
			if(productLength>length){
				divNode[1].style.width=length+"%";
			}else{
				var  titleEnableLength=productLength+(tipFontCount*normalFontSize+((price.length-tipFontCount)*halfAngleFontSize))/screenWidth*percent;
				var  titleRealLength=title.length*titleFontSize/screenWidth*percent;
				if(titleRealLength<titleEnableLength){
					if(titleEnableLength/percent*screenWidth>60&&titleRealLength/percent*screenWidth<59){
						titleNode.style.width=titleWidth+"px";
					}
				}
			}
		}
	}
}

/**
 * 跳转到藏品详细信息页面 
 * @param productId
 */
function getStockInfo(productId) {
	var projectName = getProjectName();
	window.location.href = projectName + "/trust/product/" + productId;
}

function getProjectName() {
	var pathName = window.document.location.pathname;
	var projectName = pathName.substring(0,pathName.substr(1).indexOf('/') + 1);
	return projectName;
}

function openApp(app) {
	if(isWeiXinBrower()||(isIphoneDevice()&&isQQInnerBrower())){
		if(isQQInnerBrower()){
			setPrompt("qqInnerBrower");
		}else{
			setPrompt("weiXinInnerBrower");
		}
	}else{
		var contendType,contentId;
		if(app=="detail"){
			contentId=document.getElementById("userId").value;
			contendType=720;
		}else{
			contentId=document.getElementById("productId").value;
			contendType=151;
		} 
		var url="youbiquan://"+contendType+"/"+contentId;
		jump(url);
	}
}



