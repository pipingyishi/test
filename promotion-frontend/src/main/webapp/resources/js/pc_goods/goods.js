var totalWidth=1200,itemFontSize=16,itemHalfAngleFontSize=8,underlineItemGap=8,itemGap=30;
var homePagePromptWidth=166,searchGoodsMarginLeft=663;

window.onload=function(){
	var spanNodes=document.getElementById("category").getElementsByTagName("span");
	setItemColor(spanNodes);
	spanNodes=document.getElementById("section").getElementsByTagName("span");
	setItemColor(spanNodes);
	buildSection();
	var projectName = getProjectName();
	var url=projectName+"/goods/option?dataItemId=0&dataOptionId=0&dataOptionValueId=0&type=section&page=0";
	ajaxGet(url,"section");
	var leftRightMargin=(document.body.clientWidth-totalWidth)/2+1;
	document.getElementsByTagName("section")[0].style.paddingLeft=leftRightMargin+"px";
	document.getElementById("searchGoods").style.left=leftRightMargin+homePagePromptWidth+searchGoodsMarginLeft+"px";
}

//当浏览器窗口大小改变时，设置显示内容的高度  
window.onresize=function(){  
	var leftRightMargin=(document.body.clientWidth-totalWidth)/2+1;
	document.getElementsByTagName("section")[0].style.paddingLeft=leftRightMargin+"px";
	document.getElementById("searchGoods").style.left=leftRightMargin+homePagePromptWidth+searchGoodsMarginLeft+"px";
}  

function OnFocus(obj){
	if(obj.value=="搜索藏品"){
		obj.value="";
	}else{
		if(obj.value.length!=0){
			document.getElementById("searchGoods").style.display="block";
		}
	}
}

function OnBlur(obj){
	obj.value="搜索藏品";
	setTimeout(function(){
		if(obj.value.length==0){
			obj.value="搜索藏品";
		}
		document.getElementById("searchGoods").style.display="none";
	},500);
}


function OnKeyup(obj){
	var queue;
	clearTimeout(queue);
	queue=setTimeout(function(){
		var projectName = getProjectName();
		var searchKey=obj.value;
		if(searchKey.length!=0){
			var url=projectName+"/goods/goodsSearch?searchKey="+searchKey;
			ajaxGet(url,"goodsSearch");
		}else{
			document.getElementById("searchGoods").style.display="none";
		}
	},500);
}


function selectSearchGoods(obj){
	var spanNode=obj.getElementsByTagName("span")[0];
	var inputNode=document.getElementById("goodsInput");
	inputNode.value=spanNode.innerHTML;
	document.getElementById("searchGoods").style.display="none";
	var productId=spanNode.getAttribute("data-product");
	inputNode.setAttribute("productid",productId);
}


function searchGoods(){
	var inputNode=document.getElementById("goodsInput");
	var productId=inputNode.getAttribute("productid");
	var projectName = getProjectName();
	var url=projectName+"/goods/productSearch?productId="+productId;
	ajaxGet(url,"productSearch");
}


function section(obj){
	typeSelect(obj,"section");
}


function category(obj){
	typeSelect(obj,"category");
}

function typeSelect(obj,type){
	var spanNodes=document.getElementById(type).getElementsByTagName("span");
	for(var i=0,length=spanNodes.length;i<length;i++){
		spanNodes[i].style.border="0px";
	}
	obj.style.borderBottom="solid 2px #ff9b21";
	var dataItemId,dataOptionId,dataOptionValueId;
	if(type=="section"){
		dataItemId=document.getElementById("save").getAttribute("itemId");
		dataOptionId=obj.getAttribute("data-optionid");
		dataOptionValueId=obj.getAttribute("data-optionvalueid");
	}else{
		dataItemId=obj.getAttribute("data-itemid");
		dataOptionId=0;
		dataOptionValueId=0;
	}
	var page=0;
	savePresentOption(dataItemId,dataOptionId,dataOptionValueId,type);
	if(document.getElementById("list")!=null){
		removeRecorde(type);
	}else{
		removePrompt();
	}
	var projectName = getProjectName();
	var url=projectName+"/goods/option?dataItemId=" +dataItemId+"&dataOptionId="+ dataOptionId+ "&dataOptionValueId=" + dataOptionValueId+"&type="+type+"&page="+page;
	ajaxGet(url,type);
}


function countEnglishCharacter(str){
		var count=0;
		for(var i=0;i<str.length;i++){
			if(str.charCodeAt(i)>255){//是否为汉字
				count++;
			}
		}
		return str.length-count;
}

function buildSection(){
	var spanNodes=document.getElementById("section").getElementsByTagName("span");
	var lineLength=0;
	var sectionItemLength=1118;
	var count=0;
	var sectionItemStr="";
	for(var i=1,length=spanNodes.length;i<length;i++){
		var sectionItem=spanNodes[i].innerHTML.replace(/(^\s*)|(\s*$)/g, "");
		var EnglishCharactarCount=countEnglishCharacter(sectionItem);
		lineLength+=(EnglishCharactarCount*itemHalfAngleFontSize+(sectionItem.length-EnglishCharactarCount)*itemFontSize+underlineItemGap+itemGap);
		if(lineLength>sectionItemLength){
			sectionItemStr+=";";
			count++;
			lineLength=0;
			i--;
		}else{
			sectionItemStr+=(sectionItem+",");
		}
	}
	if(count!=0){
		var sectionItemAllRow=sectionItemStr.split(";");
		var divItemNode=document.createElement("div");
		var spanNode=document.createElement("span");
		var spanCount=0;
		for(var j=0;j<=count;j++){
			var sectionItemRow=sectionItemAllRow[j];
			var divNode=document.createElement("div");
			var sectionItemCol=sectionItemRow.split(",");
			for(var k=0,length=sectionItemCol.length-1;k<length;k++){
				spanCount++;
				var spanNode=createSectionItem(sectionItemCol[k],k,spanNodes,spanCount);
				divNode.appendChild(spanNode);
				divNode.setAttribute("style","height: 56px;line-height:56px;border-bottom: solid 1px #ddd;width:"+sectionItemLength+"px;");
			}
			divItemNode.appendChild(divNode);
		}
		divItemNode.style.float="left";
		divItemNode.style.marginLeft=itemGap+"px";
		var divRootNode=document.getElementById("goods");
		var node;
		if(divRootNode.firstChild.nodeType==1){
			node=divRootNode.firstChild;
		}else{
			node=divRootNode.firstElementChild;
		}
		node.removeChild(document.getElementById("section"));
		var divSectionNode=document.createElement("div");
		var itemRowHeight=56;
		var sectionHeight=(count+1)*itemRowHeight;
		divSectionNode.setAttribute("style","float:left;font-size: 16px;margin-left:20px;color:#ff9b21;margin-top:20px;");
		divSectionNode.innerHTML="版块";
		var divNode=document.createElement("div");
		divNode.setAttribute("id","section");
		divNode.setAttribute("style","font-size: 0px;height:"+(sectionHeight+1)+"px;");
		divNode.appendChild(divSectionNode);
		divNode.appendChild(divItemNode);
		node.appendChild(divNode);
	}else{
		var divNode=document.getElementById("section");
		var spanNodes=divNode.getElementsByTagName("div")[1].getElementsByTagName("span");
		for(var i=0,length=spanNodes.length;i<length;i++){
			var offsetWidth=spanNodes[i].offsetWidth+underlineItemGap;
			spanNodes[i].style.width=offsetWidth;
			spanNodes[i].style.textAlign="center";
		}
		
	}
}

function createSectionItem(sectionContend,position,spanNodes,spanCount){
	var spanNode=document.createElement("span");
	spanNode.innerHTML=sectionContend;
	if(position!=0){
		spanNode.style.marginLeft=itemGap+"px";
	}
	spanNode.style.display="inline-block";
	spanNode.style.fontSize=itemFontSize+"px";
	spanNode.style.height="55px";
	if(spanCount==1){
		spanNode.style.borderBottom="solid 2px #ff8941";
	}
	spanNode.style.cursor="pointer";
	var EnglishCharactarCount=countEnglishCharacter(sectionContend);
	spanNode.style.width=(itemHalfAngleFontSize*EnglishCharactarCount+(sectionContend.length-EnglishCharactarCount)*itemFontSize+underlineItemGap)+"px";
	spanNode.style.textAlign="center";
	spanNode.setAttribute("onclick","section(this)");
	spanNode.setAttribute("data-optionid",spanNodes[spanCount].getAttribute("data-optionid"));
	spanNode.setAttribute("data-optionvalueid",spanNodes[spanCount].getAttribute("data-optionvalueid"));
	return spanNode;
}

function savePresentOption(dataItemId,dataOptionId,dataOptionValueId,type){
	var divNode=document.getElementById("save");
	divNode.setAttribute("itemId",dataItemId);
	divNode.setAttribute("optionId",dataOptionId);
	divNode.setAttribute("optionValueId",dataOptionValueId);
	divNode.setAttribute("type",type);
}


function setItemColor(spanNodes){
	var i=0;
	var type=spanNodes[0].innerHTML.replace(/(^\s*)|(\s*$)/g, "");
	var length=spanNodes.length;
	if(type!="全部"){
		i=1;
	}
	var widen=false;
	if(spanNodes[0].offsetWidth>type.length*itemFontSize){
		widen=true;
	}
	for(;i<length;i++){
		var offsetWidth=spanNodes[i].offsetWidth;
		if(!widen){
			spanNodes[i].style.width=offsetWidth+underlineItemGap+"px";
			spanNodes[i].style.textAlign="center";
		}
	}
}


function getProjectName() {
	var pathName = window.document.location.pathname;
	var projectName = pathName.substring(0,pathName.substr(1).indexOf('/') + 1);
	return projectName;
}

function ajaxGet(url,type){
	var req = createXMLHTTPRequest();
	if (req) {
		req.open("get", url, true);
		req.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=gbk;");
		req.send(null);
		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					var content = req.responseText;
					if(type=="goodsSearch"){
						if(content.indexOf("li")>-1){
							goodsSearch(content);
						}else{
						}
					}else if(type=="productSearch"){
						if(content.indexOf("div")>-1){
							productSearch(content);
						}else{
							if(document.getElementById("list")!=null){
								removeListRecord();
								addSearchPrompt()
							}
						}
					}else{
						if (content.indexOf("div")>-1) {
							optionExchange(content,type);
						}else{
							addSearchPrompt();
						}
					}
				} else {
					alert("加载失败");
				}
			}
		}
	}
}


function goodsSearch(content){
	var searchGoodsNode=document.getElementById("searchGoods");
	var divNode=document.getElementById("mCSB_1");
	if(divNode==null){
		searchGoodsNode.innerHTML=content;
		$("#searchGoods").mCustomScrollbar();
		divNode=document.getElementById("mCSB_1_container");
		if(divNode!=null){
			divNode.setAttribute("class","");
		}
		divNode=document.getElementById("mCSB_1_dragger_vertical");
		if(divNode!=null){
			divNode.getElementsByTagName("div")[0].style.background="#ff9b21";
		}
		document.getElementById("mCSB_1_scrollbar_vertical").style.width="2px";
	}else{
		divNode=document.getElementById("mCSB_1_container");
		var aNodes=divNode.getElementsByTagName("a");
		while(aNodes.length>0){
			divNode.removeChild(aNodes[0]);
		}
		divNode.innerHTML=content;
	}
	divNode=document.getElementById("mCSB_1_container");
	var aNode=document.createElement("a");
	aNode.setAttribute("style","display:block;width:200px;height:26px;");
	divNode.appendChild(aNode);
	var aNodes=divNode.getElementsByTagName("a");
	var marginRight=5,marginLeft=5,liTotalWidth=220,productCateGap=10,fontSize=14;
	for(var i=0,length=aNodes.length-1;i<length;i++){
		var spanNodes=aNodes[i].getElementsByTagName("span");
		var cateWidth=spanNodes[1].innerHTML.length*fontSize;
		var productWidth=liTotalWidth-cateWidth-marginRight-marginLeft-productCateGap;
		spanNodes[0].style.width=productWidth+"px";
	}
	searchGoodsNode.style.display="block";
}


function setUserStyle(){
	var listHeight=200;
	var userImageHeight=82;
	var divNodes=document.getElementsByName("user");
	for(var i=0,length=divNodes.length;i<length;i++){
		var offsetHeight=divNodes[i].offsetHeight;
		divNodes[i].style.marginTop=(listHeight-offsetHeight)/2+"px";
		var imageNode;
		if(divNodes[i].previousSibling.nodeType==1){
			imageNode=divNodes[i].previousSibling;
		}else{
			imageNode=divNodes[i].previousElementSibling;
		}
		imageNode.style.marginTop=(listHeight-userImageHeight)/2+"px";
	}
	
}

/**
 * status:区别当前翻页列表是否为可滚动的
 */
function isTurnPage(liNodes,operatePage,status){
	var i,length;
	if(!status){
		i=1;
		length=liNodes.length-1;
	}else{
		i=2;
		length=liNodes.length-3;
	}
	var currentPage;
	for(;i<length;i++){
		var currentPageNode=liNodes[i].getElementsByTagName("span")[0];
		if(currentPageNode.style.background=="rgb(255, 155, 33)"){
			currentPage=currentPageNode.innerHTML;
			break;
		}
	}
	if(currentPage==operatePage){
		return false;
	}
	return true;
}


function flipPage(obj){
	var page=obj.innerHTML;
	var ulNode=document.getElementById("pageList");
	var liNodes=ulNode.getElementsByTagName("li");
	var firstFlipBtnContend=liNodes[0].getElementsByTagName("span")[0].innerHTML;
	if(firstFlipBtnContend!="首页"){
		if(! isTurnPage(liNodes,page,false)){
			return false;
		}
		page=unableCreateBtn(obj,page,liNodes);
	}else{
		if(!isTurnPage(liNodes,page,true)){
			return false;
		}
		var totalPage=document.getElementById("totalPage").innerHTML;
		if(page=="首页"||page=="上一页"){
			var spanNode=liNodes[2].getElementsByTagName("span")[0];
			var currentFirstPage=spanNode.innerHTML;
			var attribute = "backgroundColor";
			var backgroundColor = spanNode.currentStyle ? spanNode.currentStyle[attribute]: document.defaultView.getComputedStyle(spanNode,false)[attribute];
			if(backgroundColor=="rgb(255, 155, 33)"&&currentFirstPage==1){
				return false;
			}else{//当前页不是第一页
				var operatePage=obj.getAttribute("data-page");
				operatePage=Number(operatePage);
				if(operatePage==1){//点击的是首页
					createPageBtn(operatePage,"首页");
					page=operatePage;
				}else{//点击的是上一页
					createPageBtn(operatePage,"上一页");
					page=operatePage;
				}
			}
		}else if(page=="下一页"||page=="尾页"){
			var prevNextPage,previousNextNodeColor;
			for(var i=2,length=liNodes.length;i<length;i++){
				var anyPage=liNodes[i].getElementsByTagName("span")[0].innerHTML;
				if(anyPage=="下一页"){//获得可能的最后一页
					var previousSiblingNode
					if(liNodes[i].previousSibling.nodeType==1){
						previousSiblingNode=liNodes[i].previousSibling
					}else{
						previousSiblingNode=liNodes[i].previousElementSibling
					}
					previousNextNodeColor=previousSiblingNode.getElementsByTagName("span")[0].style.background;
					prevNextPage=previousSiblingNode.getElementsByTagName("span")[0].innerHTML;
					break;
				}
			}
			if(prevNextPage==totalPage&&previousNextNodeColor=="rgb(255, 155, 33)"){//判断当前页是否是最后一页
				return false;
			}else{//当前页不是最后一页
				var operatePage=obj.getAttribute("data-page");
				operatePage=Number(operatePage);
				createPageBtn(operatePage);
				page=operatePage;
			}
		}else{//两头可进退
			if(page=="确定"){
				var jumpPage=document.getElementById("jumpPage").value;
				if(jumpPage>totalPage){
					alert("总共"+totalPage+"页,不能跳转");
					return false;
				}
				for(var i=2,length=liNodes.length-3;i<length;i++){
					var currentPageNode=liNodes[i].getElementsByTagName("span")[0];
					if(currentPageNode.style.background=="rgb(255, 155, 33)"){
						var currentPage=currentPageNode.innerHTML;
						if(currentPage==jumpPage){
							return false;
						}
					}
				}
				createPageBtn(Number( jumpPage),page);
				page=jumpPage;
			}else{
				createPageBtn(Number(page));
			}
		}
		setFlipBtnStatus(page,totalPage);
	}
	if(page==0){
		return false;
	}
	document.getElementById("turnPage").style.display="none";
	removeRecorde("flip");
	var divNode=document.getElementById("save");
	var itemId=divNode.getAttribute("itemId");
	var optionId=divNode.getAttribute("optionId");
	var optionValueId=divNode.getAttribute("optionValueId");
	var projectName = getProjectName();
	page=Number(page)-1;
	var url=projectName+"/goods/option?dataItemId=" +itemId+"&dataOptionId="+ optionId+ "&dataOptionValueId=" + optionValueId+"&type=section&page="+page;
	ajaxGet(url,"flip");
}


function setFlipBtnStatus(page,totalPage){
	var ulNode=document.getElementById("pageList");
	var liNodes=ulNode.getElementsByTagName("li");
	page=Number(page);
	var unableBtnColor="#dadada";
	var ableBtnColor="#000";
	totalPage=Number(totalPage);
	var homePageNode=liNodes[0].getElementsByTagName("span")[0];
	var previousPageNode=liNodes[1].getElementsByTagName("span")[0];
	if(page==1){
		liNodes[0].style.color=unableBtnColor;
		liNodes[1].style.color=unableBtnColor;
		homePageNode.setAttribute("data-page","0");
		previousPageNode.setAttribute("data-page","0");
	}else{
		liNodes[0].style.color=ableBtnColor;
		liNodes[1].style.color=ableBtnColor;
		homePageNode.setAttribute("data-page","1");
		previousPageNode.setAttribute("data-page",""+(page-1));
	}
	var nextPageNode=document.getElementById("nextPage");
	var endPageNode=document.getElementById("endPage");
	var nextPageSpanNode,endPageSpanNode;
	if(nextPageNode.firstChild.nodeType==1){
		nextPageSpanNode=nextPageNode.firstChild;
		endPageSpanNode=endPageNode.firstChild;
	}else{
		nextPageSpanNode=nextPageNode.firstElementChild;
		endPageSpanNode=endPageNode.firstElementChild;
	}
	if(page==totalPage){
		
		nextPageNode.getElementsByTagName("span")[0].style.color=unableBtnColor;
		endPageNode.getElementsByTagName("span")[0].style.color=unableBtnColor;
		
		nextPageSpanNode.setAttribute("data-page","0");
		endPageSpanNode.setAttribute("data-page","0");
	}else{
		
		nextPageNode.style.color=ableBtnColor;
		endPageNode.style.color=ableBtnColor;
		nextPageSpanNode.setAttribute("data-page",""+(page+1));
		endPageSpanNode.setAttribute("data-page",""+totalPage);
	}
}


function removeRecorde(type){
	//删除翻页按钮
	var turnPageNode=document.getElementById("turnPage");
	var rootDivNode=document.getElementById("goods");
	if(type!="flip"&&turnPageNode!=null){
		rootDivNode.removeChild(turnPageNode);
	}
	//删除版块列表
	if(type=="category"){
		var divNode=rootDivNode.getElementsByTagName("div")[0];
		var section=document.getElementById("section");
		if(section!=null){
			divNode.removeChild(section);
		}
	}
	//删除藏品列表
	var list=document.getElementById("list");
	if(list!=null){
		rootDivNode.removeChild(list);
	}
}

function optionExchange(html,type) {
	var divFatherNode=document.getElementById("goods");
	if(html.indexOf("<br />")>-1){
		var splitHtml=html.split("<br />");
		var divNode=divFatherNode.getElementsByTagName("div")[0];
		if(type!="flip"){//查看当前是否只是翻页
			if(html.indexOf("版块")>0){
				var newDivNode=document.createElement("div");
				newDivNode.setAttribute("id","section");
				newDivNode.innerHTML=splitHtml[1];
				divNode.appendChild(newDivNode);//插入新的板块分类
			}
		}
		updateList(divFatherNode,splitHtml[0],type);
		if(type!="flip"){
			if(html.indexOf("</ul>")>0){
				var filpDivNode=document.createElement("div");
				filpDivNode.setAttribute("style","text-align: center;background: #fff;padding-top: 20px;padding-bottom: 20px;");	
				filpDivNode.setAttribute("id","turnPage");
				if(splitHtml.length==2){
					filpDivNode.innerHTML=splitHtml[1];
				}else{
					filpDivNode.innerHTML=splitHtml[2];
				}
				divFatherNode.appendChild(filpDivNode);
			}
		}
		
		if(type=="category"){//设置板块的样式
			buildSection();
			spanNodes=document.getElementById("section").getElementsByTagName("span");
			setItemColor(spanNodes);
		}
	}else{
		updateList(divFatherNode,html);
	}
	setUserStyle();
	background();
	var turnPageNode=document.getElementById("turnPage");
	if(turnPageNode!=null){
		var turnPageFirstBtn=turnPageNode.getElementsByTagName("span")[0].innerHTML;
		if(turnPageFirstBtn!="首页"){
			if(type!="flip"){
				var ulNode=document.getElementById("pageList");
				var liNodes=ulNode.getElementsByTagName("li");
				var previousPageNode=liNodes[0].getElementsByTagName("span")[0];
				previousPageNode.style.color="#ddd";
				previousPageNode.style.border="1px solid #ddd;";
				var homePageNode=liNodes[1].getElementsByTagName("span")[0];
				homePageNode.setAttribute("style","padding: 6px 12px;border: 1px solid #ff9b21;background: #ff9b21;color: #fff;");
			}
		}else{
			if(type!="flip"){
				setFlipBtnStatus(1,document.getElementById("flip").getAttribute("data-totalpage"));
			}
		}
	}
	var divNode=document.getElementById("list");
	var firstChild;
	if(divNode.firstChild.nodeType==1){
		firstChild=divNode.firstChild;
	}else{
		firstChild=divNode.firstElementChild;
	}
	firstChild.style.marginTop="30px"
	divNode=document.getElementById("turnPage");
	if(divNode!=null){
		divNode.style.display="block";
	}
}

function updateList(divFatherNode,html,type){
	var newDivNode=document.createElement("div");
	newDivNode.setAttribute("id","list");
	newDivNode.setAttribute("style","margin-bottom:30px;");
	newDivNode.innerHTML=html;
	if(type=="flip"){
		divFatherNode.insertBefore(newDivNode,document.getElementById("turnPage"));
	}else{
		divFatherNode.appendChild(newDivNode);
	}
}


function background(){//这个位置还要修改
	var divNodes=document.getElementsByName("background");
	for(var i=0,length=divNodes.length;i<length;i++){
		if(divNodes[i].getElementsByTagName("p").length!=0){
			divNodes[i].style.background="url(/promotion-frontend/resources/image/deal.png) no-repeat right bottom";
			divNodes[i].style.backgroundSize="126px";
		}else{
			var pNode=divNodes[i].parentNode;
			var inputNodeValue=pNode.getElementsByTagName("input")[0].value;
			if(inputNodeValue=="已过期"){
				divNodes[i].style.background="url(/promotion-frontend/resources/image/out-date.png) no-repeat right bottom";
				divNodes[i].style.backgroundSize="126px";
			}
		}
	}
}

//获得当前操作的节点
function operateNode(operatePage,status){
	var ulNode=document.getElementById("pageList");
	var liNodes=ulNode.getElementsByTagName("li");
	var i,length;
	if(!status){
		i=1;
		length=liNodes.length-1;
	}else{
		i=2;
		length=liNodes.length-3;
	}
	for(;i<length;i++){
		var traversalPage=liNodes[i].getElementsByTagName("span")[0].innerHTML;
		if(Number(traversalPage)==operatePage){
			return liNodes[i];
		}
	}
	return null;
}

function createPageBtn(operatePage,prev){
	var pages=7;
	var  totalPage=document.getElementById("totalPage").innerHTML;
	totalPage=Number(totalPage);
	var ulNode=document.getElementById("pageList");
	var liNodes=ulNode.getElementsByTagName("li");
	if(totalPage<=pages){//翻页数不会改变
		currentPageColor(operateNode(operatePage,true),true);
	}else{
		var endPage=false;
		for(var i=2,length=liNodes.length;i<length;i++){//  查看当前页是否是最后一页
			var anyPage=liNodes[i].getElementsByTagName("span")[0].innerHTML;
			if(anyPage=="下一页"){//获得可能的最后一页
				var previousSiblingNode
				if(liNodes[i].previousSibling.nodeType==1){
					previousSiblingNode=liNodes[i].previousSibling
				}else{
					previousSiblingNode=liNodes[i].previousElementSibling
				}
				var prevNextPage=previousSiblingNode.getElementsByTagName("span")[0].innerHTML;
				if(Number(prevNextPage)==totalPage){
					endPage=true;
				}
				break;
			}
		}
		if((operatePage>=1&&(operatePage-parseInt(pages/2)<=1)&&operatePage<parseInt(pages/2)+1)||((operatePage+parseInt(pages/2))>totalPage)&&endPage){//左边不用换，拿取数据
			if((prev=="首页")||(prev=="确定"&&operatePage==1)){
				for(var i=2;i<(7+2);i++){
					liNodes[i].getElementsByTagName("span")[0].innerHTML=i-1;
				}
			}
			currentPageColor(operateNode(operatePage,true),true);
		}else{
			var count=2;
			if((operatePage+parseInt(pages/2)>totalPage)||(prev=="确定"&&operatePage==totalPage)){
				for(var j=(totalPage-pages+1);j<=totalPage;j++){
					liNodes[count].getElementsByTagName("span")[0].innerHTML=j;
					count++;
				}
				currentPageColor(operateNode(operatePage,true),true);
			}else{
				for(var i=operatePage-parseInt(pages/2);i<operatePage;i++){
					liNodes[count].getElementsByTagName("span")[0].innerHTML=i;
					count++;
				}
				liNodes[count].getElementsByTagName("span")[0].innerHTML=operatePage;
				count++;
				for(var i=operatePage+1;i<=operatePage+parseInt(pages/2);i++){
					liNodes[count].getElementsByTagName("span")[0].innerHTML=i;
					count++;
				}
				currentPageColor(operateNode(operatePage,true),true);
			}
		}
	}
}


function unableCreateBtn(obj,operatePage,liNodes){
	var page, previousPage,nextPage;
	var previousPageSpan=liNodes[0].getElementsByTagName("span")[0];
	var nextPageSpan=liNodes[liNodes.length-1].getElementsByTagName("span")[0];
	var totalPage=document.getElementById("flip").getAttribute("data-totalpage");
	if(operatePage=="上一页"){
		liNodes[liNodes.length-1].getElementsByTagName("span")[0].style.color="#000";
		if(liNodes[1].getElementsByTagName("span")[0].style.background=="rgb(255, 155, 33)"){
			return 0;
		}else{
			page=Number(obj.getAttribute("data-page"));
			if(page!=1){
				previousPage=page-1;
				nextPageSpan.setAttribute("data-page",""+page);
			}else{
				previousPage=1;
				nextPageSpan.setAttribute("data-page",""+(page+1));
			}
			liNodes[0].getElementsByTagName("span")[0].setAttribute("data-page",""+previousPage);
			currentPageColor(operateNode(page,false),false);
		}
	}else if(operatePage=="下一页"){
		liNodes[0].getElementsByTagName("span")[0].style.color="#000";
		if(liNodes[liNodes.length-2].getElementsByTagName("span")[0].style.background=="rgb(255, 155, 33)"){
			return 0;
		}else{
			page=Number(obj.getAttribute("data-page"));
			if(page!=totalPage){
				nextPage=page+1;
				previousPageSpan.setAttribute("data-page",""+page);
			}else{
				nextPage=totalPage;
				previousPageSpan.setAttribute("data-page",""+(page-1));
			}
			liNodes[liNodes.length-1].getElementsByTagName("span")[0].setAttribute("data-page",""+nextPage);
			currentPageColor(operateNode(page,false),false);
		}
	}else{
		currentPageColor(operateNode(operatePage,false),false);
		page=Number(operatePage);
		if(operatePage!=1){
			previousPageSpan.style.color="#000";
			previousPageSpan.setAttribute("data-page",""+(page-1));
		}else{
			previousPageSpan.setAttribute("data-page",""+page);
		}
		if(operatePage!=totalPage){
			nextPageSpan.style.color="#000";
			nextPageSpan.setAttribute("data-page",""+(page+1));
		}else{
			nextPageSpan.setAttribute("data-page",""+page);
		}
	}
	if(page==1||page==totalPage){
		var i;
		if(page==1){
			i=0;
		}else{
			i=liNodes.length-1;
		}
		var previousPageNode=liNodes[i].getElementsByTagName("span")[0];
		previousPageNode.style.color="#ddd";
		previousPageNode.style.border="1px solid #ddd;";
	}
	return page;
}


/*
 * status:区别当前翻页列表是否为可滚动的
 */
function currentPageColor(currentPageNode,status){
	var ulNode=document.getElementById("pageList");
	var liNodes=ulNode.getElementsByTagName("li");
	var i,length;
	if(!status){
		i=1;
		length=liNodes.length-1;
	}else{
		i=2;
		length=liNodes.length-3;
	}
	for(;i<length;i++){
		var spanNode=liNodes[i].getElementsByTagName("span")[0];
		spanNode.style.background="#fafafa";
		spanNode.style.borderColor="#ddd";
		spanNode.style.color="#000";
	}
	var currentPageSpanNode;
	if(status){
		if(currentPageNode==null){
			currentPageSpanNode=liNodes[2].getElementsByTagName("span")[0];
		}else{
			currentPageSpanNode=currentPageNode.getElementsByTagName("span")[0];
		}
	}else{
        if(currentPageNode==null){
        	currentPageSpanNode=liNodes[liNodes.length-2].getElementsByTagName("span")[0];
        }else{
        	currentPageSpanNode=currentPageNode.getElementsByTagName("span")[0];
        }
	}
	currentPageSpanNode.style.background="#ff9b21";
	currentPageSpanNode.style.borderColor="#ff9b21";
	currentPageSpanNode.style.color="#fff";
}


function productSearch(content){
	
	var divNode=document.getElementById("goods");
	if(document.getElementById("list")!=null){
		removeListRecord();
	}else{
		removePrompt();
	}
	var newDivNode=document.createElement("div");
	newDivNode.setAttribute("id","list");
	newDivNode.setAttribute("style","margin-bottom: 30px;");
	newDivNode.innerHTML=content;
	divNode.appendChild(newDivNode);
	setUserStyle();
	document.getElementById("list").getElementsByTagName("div")[0].style.marginTop="30px";
}

function removeListRecord(){
	var divNode=document.getElementById("goods");
	var childNode=document.getElementById("list");
	if(childNode.nextSibling!=null){
		if(childNode.nextSibling.nodeType==1){
			divNode.removeChild(childNode.nextSibling);
		}else{
			divNode.removeChild(childNode.nextElementSibling);
		}
	}
	divNode.removeChild(childNode);
}

function removePrompt(){
	var divNode=document.getElementById("goods");
	var searchPromptNode=document.getElementById("searchPrompt");
	if(searchPromptNode!=null){
		divNode.removeChild(document.getElementById("searchPrompt"));
	}
}


function addSearchPrompt(){
	var divNode=document.createElement("div");
	divNode.setAttribute("style","width:1200px;height:500px;background: #fff;margin-top: 30px;clear: both;");
	divNode.setAttribute("id","searchPrompt");
	var childDivNode=document.createElement("div");
	childDivNode.setAttribute("style","margin-left: 450px;padding-top: 173px;");
	var imgNode=document.createElement("img");
	var projectName = getProjectName();
	imgNode.setAttribute("src",projectName+"/resources/image/face.png");
	imgNode.setAttribute("style","float: left;");
	childDivNode.appendChild(imgNode);
	var spanNode=document.createElement("span");
	spanNode.setAttribute("style","float: left;margin-top: 30px;margin-left: 15px;");
	spanNode.innerHTML="未搜到您想要的结果!";
	childDivNode.appendChild(spanNode);
	divNode.appendChild(childDivNode);
	document.getElementById("goods").appendChild(divNode);
}
