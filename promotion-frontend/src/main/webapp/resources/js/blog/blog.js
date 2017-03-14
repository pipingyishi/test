var scriptNode=document.createElement("script");
scriptNode.setAttribute("type", "text/javascript");
var projectName=getProjectName();
scriptNode.setAttribute("src", projectName+"/resources/js/common/common.js");
document.body.appendChild(scriptNode);

var startClientPointX,endClientPointX;
var pageNum=0;
var presentType=0;
var loadOver=false;

window.onload = function() {
	var slideNode=document.getElementById("slide");
	slideNode.addEventListener('touchstart',touchStart, false);
	slideNode.addEventListener('touchmove',touchEnd, false);
	//加载初始页
	var type=0;
	var url = window.location.href;
	if(url.indexOf("#")>=0){
		type=url.split("#")[1];
	}
	clickItem(type);
}

function touchStart(event){
	var event = event || window.event;
	var touch = event.touches[0];
	startClientPointX = touch.clientX;
}

function touchEnd(event){
	var event = event || window.event;
	var touch =event.changedTouches[0];
	endClientPointX= touch.clientX;
	var gapX=endClientPointX-startClientPointX;
	var slideDivNode=document.getElementById("slide");
	var slideDivStyle=slideDivNode.currentStyle || document.defaultView.getComputedStyle(slideDivNode,"");
	var marginLeftStyle=slideDivStyle.marginLeft;
	var marginLeftStyleValue=parseInt(marginLeftStyle.split("px")[0]);
	var screenWidth=window.screen.width;
	var initialDistance=15;
	var totalLength=560;
	if(gapX<0){//往左移动
		gapX+=marginLeftStyleValue;
		gapEnd=screenWidth-initialDistance-totalLength;
		if(gapX<gapEnd){//不可再往左移动
		   gapX=gapEnd;
		}
	}else{ //往右移动
		if(marginLeftStyleValue<initialDistance){//可以移动
			gapX+=marginLeftStyleValue;
			if(gapX>=initialDistance){
				gapX=initialDistance;  
			}
		}else{//不可再往右移动
			gapX=initialDistance;  
		}
	}
	var style="white-space: nowrap;position:relative;margin-left:"+gapX+"px;";
	setTimeout(function(){
		document.getElementById("slide").setAttribute("style",style); 
	},300);
}


/**
* 使用ajax get请求
*/ 
function getBlogList(url) {
	var req = createXMLHTTPRequest();
	if (req) {
		req.open("get", url, true);
		req.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=gbk;");
		req.send(null);
		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					if(pageNum!=1){
						var loadNode=document.getElementById("loading");
						if(loadNode!=null){
							document.body.removeChild(loadNode);
						}
					}
					var content = req.responseText;//获得服务器返回的内容
					if(content.indexOf("div")>=0){//表示内容已经读取完
						addContent(content);
					}else{
						loadOver=true;
						if((pageNum!=1)&&(document.getElementById("loadOver")==null)){
							var divNode=document.createElement("div");
							divNode.setAttribute("style","text-align: center;font-size: 13px;height: 30px;line-height: 30px;background-color: #e9e9e9;");
							divNode.setAttribute("id","loadOver");
							divNode.innerHTML="没有更多了...";
							document.body.appendChild(divNode);
						}
					}
				} else {
					alert("加载失败");
				}
			}
		}
	}
}

function getProjectName() {
	var pathName = window.document.location.pathname;
	var projectName = pathName.substring(0,pathName.substr(1).indexOf('/') + 1);
	return projectName;
}

function clickItem(type){
	loadOver=false;
	var loadOverDivNode=document.getElementById("loadOver");
	if(loadOverDivNode!=null){
		document.body.removeChild(loadOverDivNode);
	}
	window.location.href="#"+type;
	presentType=type;
	setUnderLine(type);
	pageNum=1;
	var projectName = getProjectName();
	var url=projectName + "/blog/pull/" + type+ "/" + pageNum;
	//删除之前，看是不是该列表，如果是，就不要删除，直接往后添加，否则删除
	deleteBlogList();
	getBlogList(url);
}

function setUnderLine(type){
	var id="type-"+type;
	var aNode=document.getElementById(id);
	var index=0;
	var itemNodes=document.getElementsByName("item");
	var underLineNodes=document.getElementsByName("underLine");
	for(var i=0,len=itemNodes.length;i<len;i++){
		if(aNode==itemNodes[i]){
			itemNodes[i].style.color="#ff8941";
			index=i;
		}else{
			itemNodes[i].style.color="#000";
		}
	}
	for(var i=0,len=underLineNodes.length;i<len;i++){
		if(i==index){
			underLineNodes[index].style.display="block";
		}else{
			underLineNodes[i].style.display="none";
		}
	}
}

function deleteBlogList(){
	var divNodes=document.getElementsByName("list");
	while(divNodes.length>0){
		document.body.removeChild(divNodes[0]);
	}
}


function addContent(content){
//	if(pageNum!=1){
//		var loadNode=document.getElementById("loading");
//		if(loadNode!=null){
//			document.body.removeChild(loadNode);
//		}
//	}
	var divNode=document.createElement("div");
	divNode.setAttribute("name", "list");
	if(pageNum==1){
		divNode.setAttribute("style", "margin-top:106px;");
	}
	divNode.innerHTML=content;
	document.body.appendChild(divNode);
}
/**
 * 滚动条滚动的时候出发该方法
 */
window.onscroll=function(){
	// 滚动条在Y轴上的滚动距离
	var scrollTop=getScrollTop();
	// 浏览器视口的高度
	var windowHeight=getWindowHeight();
	// 文档的总高度
	var scrollHeight=getScrollHeight();
	if((scrollTop+windowHeight== scrollHeight)&&(scrollTop!=0)&&(!loadOver)){
		pageNum++;
		if(document.getElementById("loading")==null){
			addLoadPicture();
		}
		var projectName = getProjectName();
		var url=projectName + "/blog/pull/" + presentType+ "/" + pageNum;
		getBlogList(url);
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

function showBlogInfo(blogId){
	var projectName = getProjectName();
	window.location.href = projectName + "/blog/" + blogId;
}

