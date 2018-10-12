$(document).ready(function() {
    var web3;
    
    initWeb3();
    
    $("#divAccountInfo").hide();
    
    syncAccount();
    
    setInterval(function(){
    	syncAccount(); 
    }, 1500);
    
    currentAccount = null;
    
    ///////////web3 areas
    function initWeb3(){
    	var Web3 = require('web3');
    	web3 = new Web3();
    	web3 = new Web3(new Web3.providers.HttpProvider("http://127.0.0.1:8545"));
    }
       
    function readBalance(acc) {
    	return web3.fromWei(web3.eth.getBalance(acc), "ether").toString();
	}
    
    function readTotalBalance() {
	    logText = "";
	    totalAmount = new web3.BigNumber(0);
	    for (var i in web3.eth.accounts) {
	        acc = web3.eth.accounts[i];
	        //			console.log(acc + " - "+web3.eth.getBalance(acc));
	        logText += i + " > " + acc + " - " + parseFloat(web3.fromWei(web3.eth.getBalance(acc), "ether").toString()).toFixed(2) + "\n";
	        $("#logText").val(logText);
	        console.log(acc + " - " + web3.fromWei(web3.eth.getBalance(acc), "ether").toString())
	        totalAmount = totalAmount.plus(new web3.BigNumber(web3.eth.getBalance(acc)));
	    }
	    console.log("Total:" + web3.fromWei(totalAmount.toString(), "ether").toString());
	    return web3.fromWei(totalAmount.toString(), "ether").toString();
	}

    function syncAccount() {
    	containter = $("#listOfAccount .grid-container");
    	//containter.html("");
    	
	    //add new account:
	    if( $('#addNewAccount').length == 0) {
    		console.log("not exist");
	        gridHtml = "<div class=\"grid-item\">\r\n" + 
	        "	<div class=\"accountName\" id=\"addNewAccount\">New Account</div>\r\n" + 
	        "</div>";
	        containter.append(gridHtml);
	    }
    	
	    totalAmount = new web3.BigNumber(0);
	    for (var i in web3.eth.accounts) {
	        acc = web3.eth.accounts[i];
	        balance = readBalance(acc);
	        totalAmount = totalAmount.plus(new web3.BigNumber(balance));
	        
	    	if( $('.'+acc).length ) {
	    		$('.'+acc+' #accountName'+acc).html("Account "+i);
	    		$('.'+acc+' #accountNo'+acc+'Balance').html(balance+"");
	    		$('.balanceInfor #accountNo'+acc+'Balance').html(balance+"");
	    		
	    	} else {
	    		console.log("not exist");
	    		gridHtml = "<div class=\"grid-item "+acc+"\">\r\n" + 
		        "	<div class=\"accountName\" id=\"accountNo"+acc+"\">Account "+i+"</div>\r\n" + 
		        "	<div class=\"balanceInfor\">\r\n" + 
		        "		<div class=\"accountBalance\" id=\"accountNo"+acc+"Balance\">"+balance+"</div>\r\n" + 
		        "		<div class=\"accountUnit\">ether</div>\r\n" + 
		        "	</div>\r\n" + 
		        "	<div class=\"accountAddress\">"+acc+"</div>\r\n" + 
		        "</div>";
		        containter.append(gridHtml);
	    	}
	    }
//	    console.log(totalAmount.toString())
	    $("#totalBalance").html(totalAmount+"");
    }
    
	function sendTransaction(sender, receiver, amount, pass, callback) {
	    web3.personal.unlockAccount(sender, pass)

	    tx = web3.eth.sendTransaction({
	        from: sender,
	        to: receiver,
	        value: web3.toWei(amount, "ether")
	    });
	    //console.log(tx);
	    waitForTx(tx,callback);
	}
	
	 function waitForTx(txid, callback) {
    	var myTime = setInterval(function(){ 
    		if(checkTx(txid)){
    			clearInterval(myTime);	
    			callback();
    		}
    	}, 1000);
	}
	    
    function checkTx(txid) {
    	console.log(web3.eth.getTransaction(txid));
    	if(web3.eth.getTransaction(txid).blockNumber){
    		return true;
    	}
    	return false;
    }
    
	function createNewAccount(pass) {
        web3.personal.newAccount(pass, createAccCallback);

        function createAccCallback() {
            console.log("Create account done");
            $("#newPassword").val("");
        }
	}
	
    $('body').on('click', '#listOfAccount .grid-container .grid-item', function() {
    	$("#divAccountInfo").show();    	
    	console.log("Choose grid item");
    	console.log($(this)[0].childNodes[1].innerHTML);
    	if ($(this)[0].childNodes[1].innerHTML == "New Account") {
    		console.log("Create a new account");
    		html = 
    			"<div class=\"divPassword\">\r\n" + 
    			"	Enter your password:"+ 
    			"	</br>"+
    			"	<input type='password' id='newPassword' placeholder='Your password' />"+
    			"</div>\r\n"+
    			"</br>"+
    			"<div class='button' id='btnPassword'>Ok</div>";
    		$("#divAccountInfo .showForm").html(html);
    	} else {
    		console.log("Choose account");
    		console.log($(this)[0].childNodes[5].innerHTML);
    		currentAccount = $(this)[0].childNodes[5].innerHTML;
    		html = 
    		"	<div id='divSender'>" +
    		"		<h3> Address </h3>\r\n" +
    		"		<div>"+currentAccount+"</div>\r\n" +
	        "		<div class=\"balanceInfor\">\r\n" + 
	        "			<h3> Balance in Ethereum </h3>\r\n" +
	        "			<div class=\"accountFullBalance\" id=\"accountNo"+currentAccount+"Balance\">"+readBalance(currentAccount)+"</div>\r\n" + 
	        "		</div>" +
	        "		</br></br>" +
	        "		You can send Eth to " +
	        "		</br>" +
	        "		<input type='text' id='ethReceiver' placeholder='Your receiver address' />" +
	        "		</br>" +
	        "		With amount:" +
	        "		</br>" +
	        "		<input type='text' id='ethAmount' placeholder='Your amount' />" +
	        "		</br>" +
	        "		Your password:" +
	        "		</br>" +
	        "		<input type='password' id='passToSend' placeholder='Your password' />" +
	        "		</br></br>" +
	        "		<div class='button' id='btnSendEth'>Send</div>" +
	        "	</div>\r\n";
    		
    		$("#divAccountInfo .showForm").html(html);
    	}
    });
    $('body').on('click', '#btnPassword', function() {
    	console.log($("#newPassword").val());
    	pass = $("#newPassword").val();
    	if (pass == null || pass.length == 0) {
    		console.log("No password");
    		window.alert("Please enter your password");
    		return;
    	};
    	createNewAccount(pass);
    })
    
    $('body').on('click', '#btnSendEth', function() {
    	console.log("send ether from:"+currentAccount);
    	receiverAddr = $("#ethReceiver").val();
    	amount = parseFloat($("#ethAmount").val());

    	if (web3.isAddress(receiverAddr) == false) {
    		window.alert("Your receiver address is not valid");
    		return;
    	}
    	if (amount == 0) {
    		window.alert("The amount must be greater than 0");
    		return;
    	}
    	currentBalance = parseFloat(readBalance(currentAccount));
    	if (amount > currentBalance) {
    		window.alert("The amount must be smaller than your current amount");
    		return;
    	};
    	
    	pass = $("#passToSend").val();
    	if (pass == null || pass.length ==0) {
    		window.alert("Please enter your password");
    		return;
    	}
    	$("#btnSendEth").html("Sending...");
    	console.log("make transaction");
    	
    	sendTransaction(currentAccount, receiverAddr, amount, pass, function(){
    		$("#btnSendEth").html("Send");
    		$("#passToSend").val("");
    	});
    })
})