var projectName=getProjectName();
var exchange=true;
var noticeType=true;
var preScrollTop = 0;
var count=1;
var pageNum=0;
var loadOver=false;
var scrollTops=false;
var preScrollTop=0;

window.onload = function() {
	scrollTops=true;
	var divNode=document.getElementById("exchangeTab");
	var spanNodes=divNode.getElementsByTagName("span");
	var len=spanNodes.length;
	var cols=4;
	for(var i=0;i<len;i++){
		if(i%cols==0){
			spanNodes[i].style.marginLeft="2%";
		}
	}
	var divNodes=document.getElementsByName("detailNoticeList");
	divNodes[0].style.marginTop="111px";
	setExchangeLogoVertical(divNodes);
	document.getElementsByTagName('body')[0].scrollTop=0;
	var url = window.location.href;
	if(url.indexOf("#")>=0){
		var tabItem=getTabItem(url);
		operateTab(tabItem);
		setTabItemStyle(url);
		document.body.scrollTop=preScrollTop;
	}
}

function getTabItem(url){
	var tabItem,spanNode;
	var presentExchangeId=document.getElementById("presentExchangeId");
	var presentNoticeType=document.getElementById("presentNoticeType");
	if(url.indexOf("exchange")>=0){
		tabItem={
				"id":url.split("#")[1],
				"preSelfId":presentExchangeId,
				"type":url.split("#")[2],
				"tabImage":document.getElementsByTagName("img")[1],
				"preOtherId":presentNoticeType
		};
		document.getElementById("noticeType").getElementsByTagName("span")[1].innerHTML=document.getElementById("noticeTypeId-"+tabItem.preOtherId.value).innerHTML;
		spanNode=document.getElementById("exchangeTab").getElementsByTagName("span")[0];
	}else{
		tabItem={
				"id":url.split("#")[1],
				"preSelfId":presentNoticeType,
				"type":url.split("#")[2],
				"tabImage":document.getElementsByTagName("img")[2],
				"preOtherId":presentExchangeId
		};
		document.getElementById("exchange").getElementsByTagName("span")[1].innerHTML=document.getElementById("exchangeId-"+tabItem.preOtherId.value).innerHTML;
		spanNode=document.getElementById("noticeTypeTab").getElementsByTagName("span")[0];
	}
	setPreTabItemStyle(spanNode);
	return tabItem;
}

function setPreTabItemStyle(spanNode){
	spanNode.style.backgroundColor="#fff";
	spanNode.style.borderColor="#e9e9e9";
	spanNode.style.color="#000";
}

function setPresentTabItemStyle(spanNode){
	spanNode.style.backgroundColor="#ff8941";
	spanNode.style.borderColor="#ff8941";
	spanNode.style.color="#fff";
}

function setTabItemStyle(url){
	if(url.indexOf("exchange")>=0){
		var noticeTypeId=document.getElementById("presentNoticeType").value;
		spanNode=document.getElementById("noticeTypeTab").getElementsByTagName("span")[0];
		setPreTabItemStyle(spanNode);
		spanNode=document.getElementById("noticeTypeId-"+noticeTypeId);
		setPresentTabItemStyle(spanNode);
	}else{
		var exchangeId=document.getElementById("presentExchangeId").value;
		spanNode=document.getElementById("exchangeTab").getElementsByTagName("span")[0];
		setPreTabItemStyle(spanNode);
		spanNode=document.getElementById("exchangeId-"+exchangeId);
		setPresentTabItemStyle(spanNode);
	}
}

function setExchangeLogoVertical(divNodes){
	for(var j=0,len=divNodes.length;j<len;j++){
		var noticeListInfoNodes=divNodes[j].getElementsByTagName("div");
		var offsetHeight=noticeListInfoNodes[1].offsetHeight;
		noticeListInfoNodes[0].style.height=offsetHeight+"px";
		noticeListInfoNodes[0].style.lineHeight=(offsetHeight-1)+"px";
	}
}

function getNode(){
	var exchangeDivNode=document.getElementById("exchange");
	var exchangeTabDivNode=document.getElementById("exchangeTab");
	var noticeTypeDivNode=document.getElementById("noticeType");
	var noticeTypeTabDivNode=document.getElementById("noticeTypeTab");
	var node={
			"exchangeDivNode":exchangeDivNode,
			"exchangeTabDivNode":exchangeTabDivNode,
			"noticeTypeDivNode":noticeTypeDivNode,
			"noticeTypeTabDivNode":noticeTypeTabDivNode	
	}
	return node;
}

function exchangeSelect(){
	var node=getNode();
	var tab={
			"divNode1":node.exchangeDivNode,
			"tabDivNode1":node.exchangeTabDivNode,
			"divNode2":node.noticeTypeDivNode,
			"tabDivNode2":node.noticeTypeTabDivNode,
			"type":"exchange",
		        "upDown":exchange
	};
	tabSelect(tab);
}

function noticeTypeSelect(){
	var node=getNode();
	var tab={
			"divNode1":node.noticeTypeDivNode,
			"tabDivNode1":node.noticeTypeTabDivNode,
			"divNode2":node.exchangeDivNode,
			"tabDivNode2":node.exchangeTabDivNode,
			"type":"noticeType",
			"upDown":noticeType
	};
	tabSelect(tab);
}

function tabSelect(tab){
	var imgNode=tab.divNode1.getElementsByTagName("img")[0];
	var divNode=tab.tabDivNode1;
	var bodyNode=document.getElementsByTagName('body')[0];
	var noticeListDivNode=document.getElementById("noticeList");
	var flag=tab.type.indexOf("exchange")>=0?true:false;
	if(tab.upDown){//菜单展开
		if(count==1){
			preScrollTop=bodyNode.scrollTop;
		}
		count++;
		bodyNode.scrollTop=0;
		imgNode.setAttribute("src",projectName+"/resources/image/up-arrow.png");
		divNode.style.display="block";
		noticeListDivNode.style.display="none";
		if(flag){
			exchange=false;
		}else{
			noticeType=false;
		}
	}else{//菜单合拢
		count=1;
		imgNode.setAttribute("src",projectName+"/resources/image/down-arrow.png");
		divNode.style.display="none";
		noticeListDivNode.style.display="block";
		bodyNode.scrollTop = preScrollTop;
		if(flag){
			exchange=true;
		}else{
			noticeType=true;
		}
	}
	imgNode=tab.divNode2.getElementsByTagName("img")[0];
	imgNode.setAttribute("src",projectName+"/resources/image/down-arrow.png");
	divNode=tab.tabDivNode2
	divNode.style.display="none";
	if(flag){
		noticeType=true;
	}else{
		exchange=true;
	}
}


/**
* 使用ajax get请求
*/ 
function getNoticeList(url) {
	var req = createXMLHTTPRequest();
	if (req) {
		req.open("get", url, true);
		req.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=gbk;");
		req.send(null);
		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					var content = req.responseText;//获得服务器返回的内容
					if(pageNum!=0){
						var loadImgNode=document.getElementById("loading");
						if(loadImgNode!=null){
							document.body.removeChild(loadImgNode);
						}
					}
					if(content.indexOf("div")>=0){//表示内容已经读取完
						addContent(content);
					}else{
						loadOver=true;
						if((pageNum!=0)&&(document.getElementById("loadOver")==null)){
							var divNode=document.createElement("div");
							divNode.setAttribute("style","text-align: center;font-size: 13px;height: 30px;line-height: 30px;background-color: #e9e9e9;");
							divNode.setAttribute("id","loadOver");
							divNode.innerHTML="没有更多了...";
							document.body.appendChild(divNode);
						}
					}
				} else {
					if(pageNum!=0){
						document.body.removeChild(document.getElementById("loading"));
					}
					alert("加载失败");
				}
			}
		}
	}
}


function addContent(content){
	var divNode=document.getElementById("noticeList");
	if(pageNum==0){
		document.body.removeChild(divNode);
		divNode=document.createElement("div");
		divNode.setAttribute("id", "noticeList");
		divNode.innerHTML=content;
		divNode.getElementsByTagName("div")[0].style.marginTop="111px";
		document.body.appendChild(divNode);
	}else{
		var newDivNode=document.createElement("div")
		newDivNode.innerHTML=content;
		divNode.appendChild(newDivNode);
	}
	var divNodes=document.getElementsByName("detailNoticeList");
	setExchangeLogoVertical(divNodes);
}


function exchangeTab(exchangeId){
	var tabItem={
			"id":exchangeId,
			"preSelfId":document.getElementById("presentExchangeId"),
			"type":"exchange",
			"tabImage":document.getElementsByTagName("img")[1],
			"preOtherId":document.getElementById("presentNoticeType")
	}
	operateTab(tabItem);
}

function noticeTypeTab(type){
	var tabItem={
			"id":type,
			"preSelfId":document.getElementById("presentNoticeType"),
			"type":"noticeType",
			"tabImage":document.getElementsByTagName("img")[2],
			"preOtherId":document.getElementById("presentExchangeId")
	}
	operateTab(tabItem);
}


function operateTab(tabItem){
	window.location.href="#"+tabItem.id+"#"+tabItem.type;
	loadOver=false;
	var loadOverDivNode=document.getElementById("loadOver");
	if(loadOverDivNode!=null){
		document.body.removeChild(loadOverDivNode);
	}
	var preSelfId=tabItem.preSelfId.value;
	var spanNode=document.getElementById(tabItem.type+"Id"+"-"+preSelfId);
	tabItem.preSelfId.value=tabItem.id;
	spanNode.style.backgroundColor="#fff";
	spanNode.style.borderColor="#e9e9e9";
	spanNode.style.color="#000";
	spanNode=document.getElementById(tabItem.type+"Id"+"-"+(tabItem.id));
	spanNode.style.backgroundColor="#ff8941";
	spanNode.style.borderColor="#ff8941";
	spanNode.style.color="#fff";
	tabItem.tabImage.setAttribute("src",projectName+"/resources/image/down-arrow.png");
	document.getElementById(tabItem.type+"Tab").style.display="none";
	document.getElementById(tabItem.type).getElementsByTagName("span")[1].innerHTML=spanNode.innerHTML;
	pageNum=0;
	var type=0,exchangeId=0;
	if(tabItem.type.indexOf("exchange")>=0){
		type=tabItem.preOtherId.value;
		exchangeId=tabItem.id;
		exchange=true;
	}else{
		exchangeId=tabItem.preOtherId.value;
		type=tabItem.id;
		noticeType=true;
	}
	var url=projectName + "/notice/pull/"  + exchangeId+"/"+type+"/"+pageNum;
	getNoticeList(url);
}


function getProjectName() {
	var pathName = window.document.location.pathname;
	var projectName = pathName.substring(0,pathName.substr(1).indexOf('/') + 1);
	return projectName;
}


/**
 * 滚动条滚动的时候出发该方法
 */
window.onscroll=function(){
	if(scrollTops){
		document.body.scrollTop=0;
		scrollTops=false;
	}
	// 滚动条在Y轴上的滚动距离
	var scrollTop=getScrollTop();
	// 浏览器视口的高度
	var windowHeight=getWindowHeight();
	// 文档的总高度
	var scrollHeight=getScrollHeight();
	if((scrollTop+windowHeight== scrollHeight)&&(scrollTop!=0)&&(!loadOver)){
		if(document.getElementById("loading")==null){
			addLoadPicture();
		}
		pageNum++;
		var exchangeId=document.getElementById("presentExchangeId").value;
		var type=document.getElementById("presentNoticeType").value;
		var url=projectName + "/notice/pull/"  + exchangeId+"/"+type+"/"+pageNum;
		getNoticeList(url);
	}
}

function addLoadPicture(){
	var imgNode=document.createElement("img");
	imgNode.setAttribute("id", "loading");
	var src= getProjectName()+"/resources/image/load.gif";
	imgNode.setAttribute("src", src);
	imgNode.setAttribute("style","width: 100px;margin: 0 auto;display: block;");
	document.body.appendChild(imgNode);
}

function showNoticeInfo(noticeId){
	preScrollTop=document.body.scrollTop;
	window.location.href = projectName + "/notice/" + noticeId;
}

