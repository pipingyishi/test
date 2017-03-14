var status=0;
function styleChoice(btnStyle) {
	var tradeStyle = document.getElementById("tradeStyle");
	var stockNode = document.getElementById("stock-value");
	var electronicNode = document.getElementById("electronic-value");
	if (btnStyle == "stock") {
		// 改变按钮的值
		tradeStyle.value = "现货盘";
		// 改变按钮颜色
		stockNode
				.setAttribute("style",
						"background-color:#ff8941;color:#ffffff;float:left;margin-left:5%");
		electronicNode
				.setAttribute(
						"style",
						"background-color:#ffffff;color:#ff8941;border-style:solid;border-color:#ff8941;float:right;margin-right:5%;border-radius: 10px");
	} else {
		// 改变按钮颜色
		tradeStyle.value = "电子盘";
		electronicNode
				.setAttribute("style",
						"background-color:#ff8941;color:#ffffff;float:right;margin-right:5%");
		stockNode
				.setAttribute(
						"style",
						"background-color:#ffffff;color:#ff8941;border-style:solid;border-color:#ff8941;float:left;margin-left:5%;border-radius: 10px");
	}
}

function resetInfo(strId) {
	if (strId == "tradeCname") {
		if (document.getElementById("tradePrice").value == "") {
			document.getElementById("tradePrice").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePrice").value = "请输入";
		}
		if (document.getElementById("tradeAmount").value == "") {
			document.getElementById("tradeAmount").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeAmount").value = "请输入";
		}
		if (document.getElementById("tradeUnit").value == "") {
			document.getElementById("tradeUnit").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeUnit").value = "包/捆等";
		}
		if (document.getElementById("tradePname").value == "") {
			document.getElementById("tradePname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePname").value = "请输入";
		}
		if (document.getElementById("tradeTelphone").value == "") {
			document.getElementById("tradeTelphone").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeTelphone").value = "请输入";
		}
	} else if (strId == "tradePrice") {
		if (document.getElementById("tradeCname").value == "") {
			document.getElementById("tradeCname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeCname").value = "请输入";
		}
		if (document.getElementById("tradeAmount").value == "") {
			document.getElementById("tradeAmount").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeAmount").value = "请输入";
		}
		if (document.getElementById("tradeUnit").value == "") {
			document.getElementById("tradeUnit").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeUnit").value = "包/捆等";
		}
		if (document.getElementById("tradePname").value == "") {
			document.getElementById("tradePname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePname").value = "请输入";
		}
		if (document.getElementById("tradeTelphone").value == "") {
			document.getElementById("tradeTelphone").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeTelphone").value = "请输入";
		}
	} else if (strId == "tradeAmount") {
		if (document.getElementById("tradeCname").value == "") {
			document.getElementById("tradeCname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeCname").value = "请输入";
		}
		if (document.getElementById("tradePrice").value == "") {
			document.getElementById("tradePrice").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePrice").value = "请输入";
		}
		if (document.getElementById("tradeUnit").value == "") {
			document.getElementById("tradeUnit").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeUnit").value = "包/捆等";
		}
		if (document.getElementById("tradePname").value == "") {
			document.getElementById("tradePname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePname").value = "请输入";
		}
		if (document.getElementById("tradeTelphone").value == "") {
			document.getElementById("tradeTelphone").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeTelphone").value = "请输入";
		}
	} else if (strId == "tradeUnit") {
		if (document.getElementById("tradeCname").value == "") {
			document.getElementById("tradeCname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeCname").value = "请输入";
		}
		if (document.getElementById("tradePrice").value == "") {
			document.getElementById("tradePrice").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePrice").value = "请输入";
		}
		if (document.getElementById("tradeAmount").value == "") {
			document.getElementById("tradeAmount").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeAmount").value = "请输入";
		}
		if (document.getElementById("tradePname").value == "") {
			document.getElementById("tradePname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePname").value = "请输入";
		}
		if (document.getElementById("tradeTelphone").value == "") {
			document.getElementById("tradeTelphone").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeTelphone").value = "请输入";
		}
	} else if (strId == "tradePname") {
		if (document.getElementById("tradeCname").value == "") {
			document.getElementById("tradeCname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeCname").value = "请输入";
		}
		if (document.getElementById("tradePrice").value == "") {
			document.getElementById("tradePrice").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePrice").value = "请输入";
		}
		if (document.getElementById("tradeAmount").value == "") {
			document.getElementById("tradeAmount").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeAmount").value = "请输入";
		}
		if (document.getElementById("tradeUnit").value == "") {
			document.getElementById("tradeUnit").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeUnit").value = "包/捆等";
		}
		if (document.getElementById("tradeTelphone").value == "") {
			document.getElementById("tradeTelphone").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeTelphone").value = "请输入";
		}
	} else {
		if (document.getElementById("tradeCname").value == "") {
			document.getElementById("tradeCname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeCname").value = "请输入";
		}
		if (document.getElementById("tradePrice").value == "") {
			document.getElementById("tradePrice").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePrice").value = "请输入";
		}
		if (document.getElementById("tradeAmount").value == "") {
			document.getElementById("tradeAmount").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeAmount").value = "请输入";
		}
		if (document.getElementById("tradeUnit").value == "") {
			document.getElementById("tradeUnit").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradeUnit").value = "包/捆等";
		}
		if (document.getElementById("tradePname").value == "") {
			document.getElementById("tradePname").setAttribute(
					"style", "color:#cccccc");
			document.getElementById("tradePname").value = "请输入";
		}
	}
}

function tradeCnameDelete() {
	var cnameNode = document.getElementById("tradeCname");
	cnameNode.value = "";
	cnameNode.setAttribute("style", "color:#000000");
	resetInfo("tradeCname");
}

function marketValue() {
	var tradeAmount = document.getElementById("tradeAmount").value;
	for (var i = 0; i < tradeAmount.length; i++) {
		if (!(tradeAmount.charAt(i) >= '0' && tradeAmount.charAt(i) <= '9')) {
			document.getElementById("marketValue").value = "";
			return false;
		}
	}
	return true;
}

function EnptyTradePrice() {
	var tradePrice = document.getElementById("tradePrice").value;
	if (tradePrice.charAt(0) == '.'
			|| tradePrice.charAt(tradePrice.length - 1) == '.') {
		document.getElementById("marketValue").value = "";
		return false;
	}
	if (isNaN(tradePrice)&&(tradePrice!="请输入")) {
		document.getElementById("marketValue").value = "";
		return false;
	}
	return true;
}

function isEnptyTradePrice() {
	var tradePrice = document.getElementById("tradePrice").value;
	if (tradePrice == "请输入" || tradePrice == "") {
		marketValueNode = document.getElementById("marketValue");
		marketValueNode.value = "";
		status=1;
		return false;
	}
	if (!EnptyTradePrice()) {
		alert("请输入正确的价格");
		status=1;
		return false;
	}
	if (marketValue()) {
		var tradeAmount = document.getElementById("tradeAmount").value;
		marketValueNode = document.getElementById("marketValue");
		var money = tradeAmount * tradePrice / 10000;
		marketValueNode.value = Math.round(money * 100) / 100 + "万元";
	}
	status=0;
	return true;
}

function tradePriceDelete() {
	var priceNode = document.getElementById("tradePrice");
	priceNode.setAttribute("style", "color:#000000");
	if (priceNode.value == "请输入") {
		priceNode.value = "";
	}
	for (var i = 0; i < tradeAmount.length; i++) {
		if (!(tradeAmount.charAt(i) >= '0' && tradeAmount.charAt(i) <= '9')) {
			document.getElementById("marketValue").value = "";
			break;
		}
	}
	resetInfo("tradePrice");
}

function EnptyTradeAmount() {
	var tradeAmount = document.getElementById("tradeAmount").value;
	if(tradeAmount!="请输入"){
		for (var i = 0; i < tradeAmount.length; i++) {
			if (!(tradeAmount.charAt(i) >= '0' && tradeAmount.charAt(i) <= '9')) {
				document.getElementById("marketValue").value = "";
				return false;
			}
		}
	}
	return true;
}

function isEnptyTradeAmount() {
	var tradeAmount = document.getElementById("tradeAmount").value;
	if (tradeAmount == "请输入" || tradeAmount == "") {
		marketValueNode = document.getElementById("marketValue");
		marketValueNode.value = "";
		status=1;
		return false;
	}
	if (!EnptyTradeAmount()) {
		alert("请输入正确的数量");
		status=1;
		return false;
	}
	var tradePrice = document.getElementById("tradePrice").value;
	for (var i = 0; i < tradePrice.length; i++) {
		if (!(tradePrice.charAt(i) >= '0' && tradePrice.charAt(i) <= '9'||tradePrice.charAt(i)=='.')) {
			document.getElementById("marketValue").value = "";
			status=1;
			return false ;
		}
	}
	marketValueNode = document.getElementById("marketValue");
	var money = tradeAmount * tradePrice / 10000;
	marketValueNode.value = Math.round(money * 100) / 100 + "万元";
	status=0;
	return true;
}

function tradeAmountDelete() {
	var amountNode = document.getElementById("tradeAmount");
	amountNode.setAttribute("style", "color:#000000");
	if (amountNode.value == "请输入") {
		amountNode.value = "";
	}
	for (var i = 0; i < tradeAmount.length; i++) {
		if (!(tradeAmount.charAt(i) >= '0' && tradeAmount.charAt(i) <= '9')) {
			document.getElementById("marketValue").value = "";
			break;
		}
	}
	resetInfo("tradeAmount");
}

function tradeUnitDelete() {
	var unitNode = document.getElementById("tradeUnit");
	unitNode.setAttribute("style", "color:#000000");
	unitNode.value = "";
	resetInfo("tradeUnit");
}

function tradePnameDelete() {
	var pnameNode = document.getElementById("tradePname");
	pnameNode.setAttribute("style", "color:#000000");
	pnameNode.value = "";
	resetInfo("tradePname");
}

function EnptyTradeTelphone() {
	var tradeTelphone = document.getElementById("tradeTelphone").value;
	if(tradeTelphone!="请输入"){
		for (var i = 0; i < tradeTelphone.length; i++) {
			if (!(tradeTelphone.charAt(i) >= '0' && tradeTelphone.charAt(i) <= '9')) {
				return false;
			}
		}
	}
	return true;
}

function isEnptyTradeTelphone() {
	if (!EnptyTradeTelphone()) {
		alert("请输入正确的电话号码");
		status=1;
		return false;
	}
	status=0;
	return true;
}

function tradeTelphoneDelete() {
	var telphoneNode = document.getElementById("tradeTelphone");
	telphoneNode.setAttribute("style", "color:#000000");
	telphoneNode.value = "";
	resetInfo("tradeTelphone");
}

function informationEnpty() {
	var cnameNode = document.getElementById("tradeCname").value;
	var priceNode = document.getElementById("tradePrice").value;
	var amountNode = document.getElementById("tradeAmount").value;
	var unitNode = document.getElementById("tradeUnit").value;
	var telphoneNode = document.getElementById("tradeTelphone").value;
	if (cnameNode == "请输入" && priceNode == "请输入" && amountNode == "请输入"
			&& unitNode == "包/捆等" && telphoneNode == "请输入") {
		return false;
	}
	return true;
}

function isDigitPrompt(){
	var priceValue = document.getElementById("tradePrice").value;
	var amountValue = document.getElementById("tradeAmount").value;
	if((priceValue=="请输入"&&amountValue=="请输入")||(/^[\d|\.]*$/.test(priceValue)&&/^[\d]+$/.test(amountValue))){
		return true;
	}
	return false;
}

function EnptyTradeUtil(){
	var unitValue = document.getElementById("tradeUnit").value;
	if(unitValue.match(/\d+/g)&&(unitValue!="包/捆等")){
		return false;
	}
	return true;
}

function isEnptyTradeUtil(){
	if(!EnptyTradeUtil()){
		alert("请输入正确的单位");
		status=1;
		return false;
	}
	status=0;
	return true;
}


function getPrompt(promptInfo,detail){
	if(promptInfo=="请输入正确的"){
		promptInfo+=detail;
	}else{
		promptInfo+="、"+detail;
	}
	return promptInfo;
}

function testTradeImformation(){
	if (!informationEnpty()) {
		alert("*为必填");
		return false;
	}
	if(status==1){
		status=0;
		return false;
	}
	var cnameValue = document.getElementById("tradeCname").value;
	var priceValue = document.getElementById("tradePrice").value;
	var amountValue = document.getElementById("tradeAmount").value;
	var unitValue = document.getElementById("tradeUnit").value;
	var telphoneValue = document.getElementById("tradeTelphone").value;
	var promptInfo="请输入正确的";
	if(!EnptyTradePrice()){
		promptInfo+="价格";
	}
	if(!EnptyTradeAmount()){
		promptInfo=getPrompt(promptInfo,"数量");
	}
	if(!EnptyTradeUtil()){
		promptInfo=getPrompt(promptInfo,"单位");
	}
	if(!EnptyTradeTelphone()){
		promptInfo=getPrompt(promptInfo,"电话号码");
	}
	if(promptInfo!="请输入正确的"){
		alert(promptInfo);
		return false;
	}
	if(cnameValue=="请输入"||priceValue=="请输入"||amountValue=="请输入"||unitValue=="包/捆等"||telphoneValue=="请输入"||
	   cnameValue==""||priceValue==""||amountValue==""||unitValue==""||telphoneValue==""){
		alert("*必填");
		return false;
	}
}
