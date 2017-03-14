var scriptNode=document.createElement("script");
scriptNode.setAttribute("type", "text/javascript");
var projectName=getProjectName();
scriptNode.setAttribute("src", projectName+"/resources/js/common/common.js");
document.body.appendChild(scriptNode);

/**
 * 加载窗口事件
 */
window.onload = function() {
	if(!isMobileDevice()){
		document.getElementById("qqImg").setAttribute("style", "display:none");
		document.getElementById("weiXinImg").setAttribute("style", "display:none");
	}
	var status;
	var url = window.location.href;
	if (url.indexOf("#buy") > -1) {
		status = "buy";
	} else {
		status = "sell";
	}
	switchStatus(status);
};

/**
 * 93:能够设置的有限长度
 * 5：提示文字
 * 15：设备分辨率在320以下（包含320）该节点的文本字体的大小为15px
 * 7.5:一个英文字符占7.5px
 * +1:表示误差
 * 
 * @param screenWidth
 */
function setProductNameWidth(screenWidth){
	var tableNode = document.getElementById("goodsTable");
	var trNodes = tableNode.getElementsByTagName("tr");
	for(var i=0;i<trNodes.length;i++){
		if(i%4==0){
			var productNameNode=trNodes[i].getElementsByTagName("span")[0];
			var priceNode=trNodes[i].getElementsByTagName("span")[1];
			var productName=productNameNode.innerHTML;
			var price=priceNode.innerHTML;
			var priceStyle=(((price.length-5)*7.5+5*15)/320*100+1)+"%";
			var productNameStyle=(93-((price.length-5)*7.5+5*15)/320*100)+"%";
			productNameNode.style.width=productNameStyle;
			priceNode.style.width=priceStyle;
		}
	}
}


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
	var sellUnderlineColor,buyUnderlineColor;
	if (state == "sell") {
		if(isMobileDevice()){//pc端
			sellUnderlineColor="border-top: solid 2px #ff8941;width: 100px;margin-left: 100px;float: left;";
			buyUnderlineColor="border-top: solid 2px #ffffff;width: 100px;float: left;margin-left: 200px;";
		}else{
			sellUnderlineColor="border-top: solid 2px #ff8941;width: 23%;position: absolute;left: 13.5%;";
			buyUnderlineColor="border-top: solid 2px #ffffff;width: 23%;position: absolute;right: 13%;";
		}
		setUnderlineColor(sellUnderlineNode,buyUnderlineNode,sellUnderlineColor,buyUnderlineColor);
		document.getElementById("type").value = "出售";
	} else {
		if(isMobileDevice()){
			sellUnderlineColor="border-top: solid 2px #ffffff;width: 100px;margin-left: 100px;float: left;";
			buyUnderlineColor="border-top: solid 2px #ff8941;width: 100px;float: left;margin-left: 200px;";
		}else{
			sellUnderlineColor="border-top: solid 2px #ffffff;width: 23%;position: absolute;left: 13.5%;";
			buyUnderlineColor="border-top: solid 2px #ff8941;width: 23%;position: absolute;right: 13%;"
		}
		setUnderlineColor(sellUnderlineNode,buyUnderlineNode,sellUnderlineColor,buyUnderlineColor);
		document.getElementById("type").value = "求购";
	}
	deleteRecord("goodsTable");
	post(state);
}


/**
* 使用ajax get请求
*/ 
function post(type) {
	var userId = document.getElementById("userId").value;
	var projectName = getProjectName();
	var req = createXMLHTTPRequest();
	if (req) {
		req.open("get", projectName + "/goods/product/" + type+ "/" + userId, true);
		req.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=gbk;");
		req.send(null);
		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					var content = req.responseText;// 将数据拆分，回来的数据已经是是tr字符串
					if (content.indexOf("tr")>-1) {
						if(!isMobileDevice()){
							setGrayLine(getProductCount(content));
						}
						var tableNode = document.getElementById("goodsTable");
						insertRow(tableNode, content);
					}else{
						if(!isMobileDevice()){
							setGrayLine(0);
						}
					}
				} else {
					alert("加载失败");
				}
			}
		}
	}
}

function getProductCount(str){
	var trStr = "<tr";
	var regex = new RegExp(trStr, 'g'); // 使用g表示整个字符串都要匹配
	var result = str.match(regex);
	var count = !result ? 0 : result.length;
	return  (count+1)/4;
}

function setGrayLine(productCount){
	var divNode=document.getElementById("grayLine");
	if(productCount!=1){
		divNode.setAttribute("style","border-bottom:solid 1px #e9e9e9;position:fixed;bottom:8%;width:100%;");
	}else{
		divNode.setAttribute("style","height: 10%;background: #f7f7f7;width: 100%;position: fixed;bottom: 8%;");
	}
}


function getProjectName() {
	var pathName = window.document.location.pathname;
	var projectName = pathName.substring(0,pathName.substr(1).indexOf('/') + 1);
	return projectName;
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


function openApp(app){
	if(isWeiXinBrower()||(isIphoneDevice()&&isQQInnerBrower())){
		if(isQQInnerBrower()){
		    setPrompt("qqInnerBrower");
		}else{
		    setPrompt("weiXinInnerBrower");
		}
	}else{
		var contendType,contentId;
		if(app=="goods"){
			contentId=document.getElementById("userId").value;
			contendType=721;//现货商主页
		}else{
			contentId=document.getElementById("productId").value;
			contendType=722;//现货主页
		}
		var url="youbiquan://"+contendType+"/"+contentId;
		jump(url);
	}
}

/**
 * 跳转到藏品详细信息页面 
 * @param productId
 */
function getMerchantPrice(productId) {
	var projectName = getProjectName();
	window.location.href = projectName + "/goods/product/" + productId;
}
