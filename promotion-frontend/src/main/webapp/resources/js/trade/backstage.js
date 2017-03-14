/**
 * 加载窗口事件
 */
window.onload = function() {
	document.getElementById("currentPage").innerHTML = "1";
	changeCondition(1);
};

function getProjectName() {
	var pathName = window.document.location.pathname;
	var projectName = pathName.substring(0,
			pathName.substr(1).indexOf('/') + 1);
	return projectName;
}
function changeCondition(currentPage) {
	deleteRecord();
	var tradeStyleValue = $("#tradeStyle").val();
	var tradeVarietiesValue = $("#tradeVarieties").val();
	var projectName = getProjectName();
	$.ajax({
		// 1.请求方式
		method : "post",
		// 2.参数传递
		data : {
			"tradeStyleValue" : tradeStyleValue,
			"tradeVarietiesValue" : tradeVarietiesValue,
			"pageOf" : currentPage
		},
		// 3.跳转路径
		url : projectName + "/backstage/backstagePage",
		// 4.服务器返回参数类型
		dataType : "text",
		// 接收参数的方法
		success : function(msg) {
			if (msg != "") {
				var tableNode = document.getElementById("tb");
				insertRow(tableNode, msg);
			}
		},
		error : function() {
			alert("加载失败");
		}
	});
}

/**
 * 插入多行记录
 */
function insertRow(tableNode, rowHtml) {
	var divNode = document.createElement("div"), rows;
	divNode.innerHTML = "<table>" + rowHtml + "</table>";
	rows = divNode.childNodes[0].tBodies[0].rows;
	while (rows.length > 0) {
		tableNode.tBodies[0].appendChild(rows[0]);
	}
	document.getElementById("page").innerHTML = document
			.getElementById("totalPage").value;
}

/**
 * 删除原有记录
 */
function deleteRecord() {
	var tableNode = document.getElementById("tb");
	var rows = tableNode.rows.length;
	var node = tableNode.tBodies[0].firstChild.nextElementSibling.nextElementSibling;
	for (var i = 0; i < rows - 1; i++) {
		tableNode.tBodies[0].removeChild(node);
		node = tableNode.tBodies[0].firstChild.nextElementSibling.nextElementSibling;
	}
}

function homePage() {
	var currentPage = parseInt(document.getElementById("currentPage").innerHTML);
	if (currentPage != 1) {
		document.getElementById("currentPage").innerHTML = "1";
		changeCondition(1);
	}
	return

	{
		return false;
	}
}

function previousPage() {
	var currentPage = parseInt(document.getElementById("currentPage").innerHTML);
	if (currentPage != 1) {
		currentPage--;
		document.getElementById("currentPage").innerHTML = currentPage
				+ "";
		changeCondition(currentPage);
	} else {
		return false;
	}
}

function nextPage() {
	var currentPage = parseInt(document.getElementById("currentPage").innerHTML);
	var totalPage = parseInt(document.getElementById("totalPage").value);
	if (currentPage != totalPage) {
		currentPage++;
		document.getElementById("currentPage").innerHTML = currentPage
				+ "";
		changeCondition(currentPage);
	}
}

function endPage() {
	var currentPage = parseInt(document.getElementById("currentPage").innerHTML);
	var totalPage = parseInt(document.getElementById("totalPage").value);
	document.getElementById("currentPage").innerHTML = totalPage + "";
	if (currentPage == totalPage) {
		return false;
	}
	changeCondition(totalPage);
}

/**
 * 删除记录
 * 
 * @returns {Boolean}
 */
function deleteselectedRecord() {
	if(window.confirm('您确定要选中的删除记录吗？')){
		var tableNode = document.getElementById("tb");
		var check = document.getElementsByName("check");
		var len = check.length;
		var ids = [];
		var flag=false;
		for (var i = 0; i < len; i++) {
			if (check[i].checked) {
				ids.push(parseInt(check[i].value));
				tableNode.deleteRow(i + 1);
				i--;
				len--;
				flag=true;
			}
		}
		if(!flag)
		{
			alert("没有选中任何记录");
		}
		if (ids.length == 0) {
			return false;
		}
		var projectName = getProjectName();
		$.ajax({
			url : projectName + "/backstage/deleteRecord",
			type : 'POST',
			data : JSON.stringify(ids),
			contentType : 'application/json',
			success : function(msg) {
				alert(msg);
			},
			error : function(err) {
				alert(err);
			}
		});
		return true;
	}else{
		return false;
	}
}

/**
 * 保存修改的记录
 */
function saveRecord() {
	if(window.confirm('你确定要保存修改的内容吗？')){
		var records = [];
		var tradeIdNode = document.getElementsByName("check");
		var tradeFlagNode = document.getElementsByName("tradeFlag");
		var tradeRemakesNode = document.getElementsByName("tradeRemakes");
		var tradeTypeNode = document.getElementsByName("tradeType");
		var tradeCnameNode = document.getElementsByName("tradeCname");
		var tradeVarietiesNode = document.getElementsByName("tradeVarieties");
		var tradePriceNode = document.getElementsByName("tradePrice");
		var tradeAmountNode = document.getElementsByName("tradeAmount");
		var tradeMarketValueNode = document.getElementsByName("tradeMarketValue");
		var tradeUnitNode = document.getElementsByName("tradeUnit");
		var tradePnameNode = document.getElementsByName("tradePname");
		var tradeTelphoneNode = document.getElementsByName("tradeTelphone");
		var tradeTimeNode = document.getElementsByName("tradeTime");
		for (var i = 0; i < tradeIdNode.length; i++) {
			records[i] = {
				"tradeId" : tradeIdNode[i].value,
				"tradeFlag" : tradeFlagNode[i].value,
				"tradeRemakes" : tradeRemakesNode[i].value,
				"tradeType":tradeTypeNode[i].value,
				"tradeCname":tradeCnameNode[i].value,
				"tradeVarieties":tradeVarietiesNode[i].value,
				"tradePrice":tradePriceNode[i].value,
				"tradeAmount":tradeAmountNode[i].value,
				"tradeUnit":tradeUnitNode[i].value,
				"tradePname":tradePnameNode[i].value,
				"tradeTelphone":tradeTelphoneNode[i].value,
				"tradeTime":tradeTimeNode[i].value
			};
		}
		if (tradeIdNode.length == 0) {
			return false;
		}
		var projectName = getProjectName();
		$.ajax({
			url : projectName + "/backstage/savaRecord",
			type : 'POST',
			data : JSON.stringify(records),
			contentType : 'application/json',
			success : function(msg) {
				alert(msg);
				var currentPage = parseInt(document.getElementById("currentPage").innerHTML);
				changeCondition(currentPage);
			},
			error : function(err) {
				alert(err);
			}
		});
                return true;
             }else{
                return false;
            }
}

function jump() {
	if (document.getElementById("jump").value == "") {
		return false;
	}
	var tradeIdNode = document.getElementsByName("check");
	var currentPage = parseInt(document.getElementById("currentPage").innerHTML);
	if (tradeIdNode.length == 0) {
		return false;
	}
	var jumpNodeValue = parseInt(document.getElementById("jump").value);
	var totalPage = parseInt(document.getElementById("totalPage").value);
	if (jumpNodeValue > totalPage) {
		alert("总共" + totalPage + "页，不能进行跳转");
		return false;
	}
	if (jumpNodeValue == totalPage && currentPage == totalPage) {
		return false;
	}
	if (jumpNodeValue < 1) {
		alert("请输入正确的页数");
		return false;
	}
	if (jumpNodeValue == 1 && totalPage == 1) {
		return false;
	}
	document.getElementById("currentPage").innerHTML = jumpNodeValue + "";
	changeCondition(jumpNodeValue);
}

