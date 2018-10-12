	sigmaGraph = new sigma({
	    graph: {
	        nodes: [],
	        edges: []
	    },
	    renderer: {
	        container: document.getElementById('graph-container'),
	        type: 'canvas'
	    },
	    settings: {
	        doubleClickEnabled: false,
	        minEdgeSize: 0.1,
	        maxEdgeSize: 4,
	        enableEdgeHovering: true,
	        edgeHoverColor: 'edge',
	        defaultEdgeHoverColor: '#000',
	        edgeHoverSizeRatio: 1,
	        edgeHoverExtremities: true,
	    }
	});
	showGraph = false;

	lastBlockNum = 0;
	var Web3 = require('web3');
	var web3 = new Web3();
	web3.setProvider(new web3.providers.HttpProvider('http://127.0.0.1:8545'));

	stop = 1;
	randomGeneratedGraph = undefined;

	function readBalance() {
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
	}

	function sendTransaction(sender, receiver, amount, callback) {
	    web3.personal.unlockAccount(sender, "feal1234")

	    tx = web3.eth.sendTransaction({
	        from: sender,
	        to: receiver,
	        value: web3.toWei(amount, "ether")
	    });
	    //console.log(tx);
	    callback(tx);
	}

	function allocateAmount() {
	    console.log("allocateAmount");
	    amount = $("#valueToBalance").val();
	    for (var i = choosenNodeFrom; i < choosenNodeTo; i++) {
	        console.log("allocate:" + web3.eth.accounts[i] + " " + amount)
	        sendTransaction(web3.eth.accounts[0], web3.eth.accounts[i], amount, callback);

	        function callback(tx) {}
	    }
	}

	function allocateAllAccountAmount() {
	    //console.log(web3.eth.accounts);
	    lenAccs = web3.eth.accounts.length;
	    //console.log(lenAccs);
	    totalAmount = new web3.BigNumber(0);
	    for (var i in web3.eth.accounts) {
	        acc = web3.eth.accounts[i];
	        //console.log(acc + " - "+web3.eth.getBalance(acc));
	        //console.log(web3.fromWei(web3.eth.getBalance(acc), "ether").toString())
	        totalAmount = totalAmount.plus(new web3.BigNumber(web3.eth.getBalance(acc)));
	    }
	    totalFloat = parseFloat(web3.fromWei(totalAmount.toString(), "ether").toString());
	    eachAccAmount = totalFloat / lenAccs;
	    console.log("Total:" + totalFloat);
	    console.log("Each account will have :" + eachAccAmount);
	    amounts = [];
	    for (var i in web3.eth.accounts) {
	        amounts[i] = parseFloat(web3.fromWei(web3.eth.getBalance(web3.eth.accounts[i]), "ether").toString());
	    }
	    for (var i in web3.eth.accounts) {
	        for (var j in web3.eth.accounts) {
	            if (i == j) {
	                continue;
	            }
	            accIFAmount = amounts[i]; //parseFloat(web3.fromWei(web3.eth.getBalance(web3.eth.accounts[i]), "ether").toString());
	            if (accIFAmount < eachAccAmount) {
	                continue;
	            }
	            accJFAmount = amounts[j]; //parseFloat(web3.fromWei(web3.eth.getBalance(web3.eth.accounts[j]), "ether").toString());
	            amount = eachAccAmount - accJFAmount;
	            if (amount <= 0) {
	                continue;
	            }
	            if (amount > accIFAmount) {
	                continue;
	            }
	            console.log("Send: from :" + i + " to " + j + " with : " + amount);
	            sendTransaction(web3.eth.accounts[i], web3.eth.accounts[j], amount, callback);

	            function callback() {

	            }
	            amounts[i] -= amount;
	            amounts[j] += amount;
	        }
	    }
	}

	function createNewAccount() {
	    curr = web3.eth.accounts.length;
	    nnum = $("#numNewAcc").val();
	    for (i = curr; i < nnum; i++) {
	        web3.personal.newAccount("feal1234", createAccCallback);

	        function createAccCallback() {
	            console.log("Create account done");
	            //console.log(web3.eth.accounts);
	            $("#numNewAcc").val(web3.eth.accounts.length);
	        }
	    }
	}

	function readBlocks() {

	    //web3.setProvider(new web3.providers.HttpProvider());

	    //web3.setProvider(new web3.providers.HttpProvider('http://127.0.0.1:8545')); //8545 Mainnet

	    console.log(web3.fromWei(web3.eth.getBalance(web3.eth.coinbase), "ether").toString())

	    console.log(web3.eth);
	    console.log(web3.eth.accounts);
	    //console.log(web3.eth.getAccounts());
	    console.log(web3);
	    //console.log(web3.personal.newAccount());

	    console.log('Retreiving all transactions');

	    let blockNum = web3.eth.blockNumber;
	    console.log("Max blockNum: " + blockNum);

	    var deferred = $.Deferred();

	    var nextBlock = function() {
	        if (lastBlockNum < blockNum && !stop) {
	            // Do something
	            console.log("Block number:" + lastBlockNum + " " + !stop)
	            const block = web3.eth.getBlock(lastBlockNum);
	            console.log(block);
	            currTransactions = block.transactions;
	            for (tx in currTransactions) {
	                window.alert(">>" + lastBlockNum);
	                stop = true;
	                addTransaction(tx, currTransactions)
	            }
	            lastBlockNum++;
	            //End do sthg

	            setTimeout(nextBlock, 0); //: No timeout
	        } else {
	            deferred.resolve(lastBlockNum);
	        }
	    }
	    nextBlock();
	}

	function addTransaction(tx, currTransactions) {
	    curTx = web3.eth.getTransaction(currTransactions[tx]);
	    //curTx = transactions[tx];
	    console.log(curTx)
	    console.log("------------" + tx)
	    //console.log(JSON.parse(JSON.stringify(curTx)))
	    if (!curTx.from || !curTx.to || curTx.value == 0) {
	        console.log("Reject Transaction");
	        return;
	    }
	    console.log(curTx.from)
	    console.log(curTx.to)
	    console.log(new web3.BigNumber(curTx.value).toString())
	    addEdge(curTx.blockNumber + "_" + curTx.transactionIndex, curTx.from, curTx.to, new web3.BigNumber(curTx.value).toString())
	}

	function addNodeGraph(label, value, posX, posY) {
	    try {
	        sigmaGraph.graph.addNode({
	            id: label,
	            label: label,
	            x: posX,
	            y: posY,
	            size: value,
	            color: '#666'
	        });
	    } catch (e) {
	        return;
	    }
	}

	function addEdgeGraph(txId, fromNode, toNode, weight, color) {
	    //try {
	    sigmaGraph.graph.addEdge({
	        id: 'e' + txId,
	        source: fromNode,
	        target: toNode,
	        size: weight,
	        type: 'arrow',
	        color: color,
	        hover_color: '#000',
	        originalColor: color
	    });
	    //}catch(e) {
	    //	return;
	    //}
	    //sigmaGraph.refresh();
	}

	function addEdge(txId, fromNode, toNode, weight) {
	    fnsuccesscallback = function(rs) {
	        console.log("Send done")
	    }
	    $.ajax({
	        url: "http://localhost:8080/RestTransactions/webresources/transactions/" + txId + "/" + fromNode + "/" + toNode + "/" + weight + "/" + lastBlockNum,
	        crossDomain: true,
	        type: "GET",
	        contentType: "application/json; charset=utf-8",
	        async: false,
	        dataType: "jsonp",
	        complete: fnsuccesscallback
	    });

	    if (showGraph == true) {
	        try {
	            balanceTo = web3.eth.getBalance(fromNode);
	            balanceFrom = web3.eth.getBalance(toNode);
	        } catch (e) {
	            return;
	        }
	        addNodeGraph(fromNode, balanceTo)
	        addNodeGraph(toNode, balanceFrom)
	        addEdgeGraph(txId, fromNode, toNode, weight)
	        sigma.plugins.relativeSize(sigmaGraph, 10)
	        sigmaGraph.refresh();
	    }
	}

	function getRandomInt(min, max) {
	    return Math.floor(Math.random() * (max - min + 1)) + min;
	}

	function generateGraph(choosenNode, p) {
	    sigmaGraph.graph = {
	        nodes: [],
	        edges: []
	    };
	    sigmaGraph.graph.clear();
	    sigmaGraph.refresh();

	    n = choosenNode.length;
	    var i, j;
	    for (i = 0; i < n; i++) {
	        //graph.nodes.push({ label: 'node '+i });
	        posX = i % numOfRow;
	        posY = Math.floor(i / numOfRow);
	        addNodeGraph(choosenNode[i], 1, posX, posY);
	    }
	    console.log(sigmaGraph.graph.nodes())
	    for (i = 0; i < n; i++) {
	        for (j = 0; j < i; j++) {
	            if (sigmaGraph.graph.nodes()[i].x == sigmaGraph.graph.nodes()[j].x) {
	                continue;
	            }
	            m = getRandomInt(0, 1000);
	            if (m < p) {
	                color = (sigmaGraph.graph.nodes()[i].x > sigmaGraph.graph.nodes()[j].x) ? '#0F08F3' : '#FB070B';
	                addEdgeGraph(choosenNode[i] + "-" + choosenNode[j], choosenNode[i], choosenNode[j], 1, color);
	            }
	        }
	    }

	    /*randomGeneratedGraph = {
	    	nodes : sigmaGraph.graph.nodes(),
	    	edges : sigmaGraph.graph.edges()
	    }
	    //console.log(randomGeneratedGraph);
	    */

	    sigma.plugins.relativeSize(sigmaGraph, 10)
	    sigmaGraph.refresh();
	}

	function folowRandomGraph() {
	    amounts = [];
	    for (var i in choosenNode) {
	        amounts[choosenNode[i]] = parseFloat(web3.fromWei(web3.eth.getBalance(choosenNode[i]), "ether").toString());
	    }
	    console.log(amounts)
	    for (e in sigmaGraph.graph.edges()) {
	        i = sigmaGraph.graph.edges()[e].source;
	        j = sigmaGraph.graph.edges()[e].target;
	        //console.log(s + " "+ t);

	        if (i == j) {
	            continue;
	        }
	        accIFAmount = amounts[i]; //parseFloat(web3.fromWei(web3.eth.getBalance(web3.eth.accounts[i]), "ether").toString());
	        accJFAmount = amounts[j]; //parseFloat(web3.fromWei(web3.eth.getBalance(web3.eth.accounts[j]), "ether").toString());
	        amount = parseInt(accIFAmount * getRandomInt(10, 30) * 0.01);

	        if (amount <= 0) {
	            continue;
	        }

	        console.log("Send: from :" + i + " to " + j + " with : " + amount);
	        sendTransaction(i, j, amount, callback);

	        sigmaGraph.graph.edges()[e].amount = amount;

	        function callback(tx) {
	            sigmaGraph.graph.edges()[e].txId = tx;
	        }
	        amounts[i] -= amount;
	        amounts[j] += amount;

	    }
	    sigmaGraph.refresh();
	}

	function checkExecutingTx() {
	    console.log("checkExecutingTx:"+sigmaGraph.graph.edges().length);
	    //logText = "";
	    //logText += i + " > " + acc + " - " + parseFloat(web3.fromWei(web3.eth.getBalance(acc), "ether").toString()).toFixed(2) + "\n";
	    //$("#logText").val(logText);
	    /*
	    source:"0xfd741bb71e554f966164c7adbc49c291bc20dc14"
	    target:"0x963399c38248b0d33dd696bbd333084dbc61faab"
	    txId:"0x2bdf34614da026e94e05d1346a084fe1dc86df85700a2c148f04480935e9adf7"
	    */
	    logText = "";

	    for (e in sigmaGraph.graph.edges()) {
	        //console.log(web3.eth.getTransaction(sigmaGraph.graph.edges()[e].txId).blockNumber);
	        edge = sigmaGraph.graph.edges()[e];
	        edge.color = "#848484";
	        if (edge.txId) {
	            blockNumber = web3.eth.getTransaction(edge.txId).blockNumber;
	            if (blockNumber != null && !edge.sent) {
	                edge.color = edge.originalColor;
					edge.sent = true;
					
	                logText += "tx(" + edge.txId + "," + edge.source + "," + edge.target + "," + edge.amount + ")\n";
	                
	                $.ajax({
	    	            url: "addTransactions",
	    	            type: "POST",
	    	            dataType: "json",
	    	            data: {
	    	                id : edge.txId,
	    	                from :edge.source ,
	    	                to : edge.target,
	    	                value : edge.amount,
	    	                blocknum :  blockNumber
	    	            }
	    	        }).done(function(response) {
	    	        	console.log(response)
	    	        }).fail(
	    	                function(xhr, status, errorThrown) {
	    	                	window.alert("I cannot load the user from server try to reload.");
	    	                    console.log("Error: " + errorThrown);
	    	                    console.log("Status: " + status);
	    	                    console.dir(xhr);
	    	        });
	                
	            }
	        }
	    }
	    console.log(logText);

	    $("#logText").val(logText);

	    sigmaGraph.refresh();
	}

	function gatherEth() {
	    for (var i in web3.eth.accounts) {
	        if (i == 0) {
	            continue;
	        }
	        acc = web3.eth.accounts[i];
	        amount = web3.fromWei(web3.eth.getBalance(acc), "ether").minus(0.1).toString();
	        if (amount <= 0) {
	            continue;
	        }
	        console.log("gather:" + web3.eth.accounts[i] + " - " + web3.eth.accounts[0] + " - " + amount);
	        sendTransaction(web3.eth.accounts[i], web3.eth.accounts[0], amount, callback);

	        function callback(e) {}
	    }
	}
	function resetDLVFile() {
		console.log("resetDLVFile");
		$.ajax({
	            url: "resetDLVTransactionFile",
	            type: "POST",
	            dataType: "json"
	        }).done(function(response) {
	        	console.log(response)
	        }).fail(
	                function(xhr, status, errorThrown) {
	                    window.alert("I cannot load the user from server try to reload.");
	                    console.log("Error: " + errorThrown);
	                    console.log("Status: " + status);
	                    console.dir(xhr);
	        });
	}
	function executeDLVTx() {
		console.log("executeDLVTransaction");
		 $.ajax({
	            url: "executeDLVTransaction",
	            type: "POST",
	            dataType: "json"
	        }).done(function(response) {
	        	console.log(response)
	        	$("#logText").val(response.result);
	        }).fail(
	                function(xhr, status, errorThrown) {
	                	window.alert("I cannot load the user from server try to reload.");
	                    console.log("Error: " + errorThrown);
	                    console.log("Status: " + status);
	                    console.dir(xhr);
	        });
	}
	sigmaGraph.bind('clickNode', function(e) {
	    console.log(e.data.node);
	    console.log(e.data.node.label + " balance:" + e.data.node.size);
	});
	sigmaGraph.bind('clickEdge', function(e) {
	    console.log(e.data.edge);
	    //console.log("From:"+e.data.edge.source+" To :"+e.data.edge.target+ " eth :"+e.data.edge.size)
	});

	lastBlockNum = 180000; //46168 is min
	choosenNode = [];
	choosenNodeFrom = undefined;
	choosenNodeTo = undefined;
	//readBlocks();
	$(document).ready(function() {
	    $("#numNewAcc").val(web3.eth.accounts.length);
	    $("#btnStopSync").click(function(e) {
	        stop = !stop;
	        readBlocks();
	        //readBalance();
	    });
	    $("#btnBalance").click(function(e) {
	        readBalance();
	    })
	    $("#btnCreateAccount").click(function(e) {
	        createNewAccount();
	    })
	    $("#btnSendTx").click(function(e) {
	        sendTransaction();
	    })
	    $("#btnGenerateGraph").click(function(e) {
	        choosenNode = [];
	        numOfRow = $("#numOfRow").val();
	        choosenNodeFrom = $("#choosenNodeFrom").val();
	        choosenNodeTo = $("#choosenNodeTo").val();
	        probability = $("#probability").val();
	        for (var i = choosenNodeFrom; i < choosenNodeTo; i++) {
	            choosenNode[i - choosenNodeFrom] = web3.eth.accounts[i];
	        }
	        console.log(choosenNode);
	        console.log(choosenNode.length);
	        generateGraph(choosenNode, probability);
	    })
	    $("#btnAllocateAmount").click(function(e) {
	        allocateAmount();
	    })
	    $("#btnFollowGraph").click(function(e) {
	        folowRandomGraph();
	    })
	    $("#btnGatherEth").click(function(e) {
	        gatherEth();
	    })
	    $("#btnCheckExecuteTx").click(function(e) {
	        checkExecutingTx();
	    })
	     $("#btnResetDLVFile").click(function(e) {
	    	 resetDLVFile();
	    })
	     $("#btnExecuteDLVFile").click(function(e) {
	        executeDLVTx();
	    })
	    $("#btnTest").click(function(e) {
	        contractAddress = "0x27be9b343dcDa329F01Ebb5f994015EB674189c9";
	        var abiContract = [ { "constant": true, "inputs": [], "name": "name", "outputs": [ { "name": "", "type": "string", "value": "XBRL1.2" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "uint256" } ], "name": "companies", "outputs": [ { "name": "companyAddress", "type": "address" }, { "name": "companyName", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "validated", "type": "string" } ], "name": "updateValidateReport", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": false, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" }, { "name": "arcIndex", "type": "uint256" }, { "name": "conceptFrom", "type": "string" }, { "name": "conceptTo", "type": "string" }, { "name": "weight", "type": "string" }, { "name": "calLinkBase", "type": "string" } ], "name": "updateArcByIndex", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "ownCompany", "outputs": [ { "name": "rs", "type": "uint256", "value": "0" }, { "name": "companyIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [], "name": "standard", "outputs": [ { "name": "", "type": "string", "value": "Token 0.1" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyAddress", "type": "address" } ], "name": "getCompany", "outputs": [ { "name": "cpAddress", "type": "address", "value": "0x0000000000000000000000000000000000000000" }, { "name": "companyName", "type": "string", "value": "" }, { "name": "reportSize", "type": "uint256", "value": "0" }, { "name": "companyIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "concept", "type": "string" }, { "name": "context", "type": "string" }, { "name": "value", "type": "string" }, { "name": "unit", "type": "string" }, { "name": "factgroup", "type": "string" } ], "name": "addFact", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" } ], "name": "getReportByIndex", "outputs": [ { "name": "rpId", "type": "string" }, { "name": "rpIndex", "type": "uint256" }, { "name": "date", "type": "string" }, { "name": "validated", "type": "string" }, { "name": "factSize", "type": "uint256" }, { "name": "arcSize", "type": "uint256" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "date", "type": "string" } ], "name": "addReport", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "owner", "outputs": [ { "name": "", "type": "address", "value": "0x44d112ae0e3b1cdcffc8738764bed38f1417ab13" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [], "name": "symbol", "outputs": [ { "name": "", "type": "string", "value": "XBRL" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "address" } ], "name": "frozenAccount", "outputs": [ { "name": "", "type": "bool", "value": false } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" }, { "name": "arcIndex", "type": "uint256" } ], "name": "getArcByIndex", "outputs": [ { "name": "conceptFrom", "type": "string" }, { "name": "conceptTo", "type": "string" }, { "name": "weight", "type": "string" }, { "name": "calLinkBase", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "companyName", "type": "string" } ], "name": "registerNewCompany", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "reportId", "type": "string" } ], "name": "ownReport", "outputs": [ { "name": "rs", "type": "uint256", "value": "0" }, { "name": "companyIndex", "type": "uint256", "value": "0" }, { "name": "reportIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "conceptFrom", "type": "string" }, { "name": "conceptTo", "type": "string" }, { "name": "weight", "type": "string" }, { "name": "calLinkBase", "type": "string" } ], "name": "addArc", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": false, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" }, { "name": "factIndex", "type": "uint256" }, { "name": "concept", "type": "string" }, { "name": "context", "type": "string" }, { "name": "value", "type": "string" }, { "name": "unit", "type": "string" }, { "name": "factgroup", "type": "string" } ], "name": "updateFactByIndex", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" }, { "name": "factIndex", "type": "uint256" } ], "name": "getFactByIndex", "outputs": [ { "name": "concept", "type": "string" }, { "name": "context", "type": "string" }, { "name": "value", "type": "string" }, { "name": "unit", "type": "string" }, { "name": "factgroup", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyAddress", "type": "address" }, { "name": "reportId", "type": "string" } ], "name": "getReport", "outputs": [ { "name": "rpId", "type": "string", "value": "" }, { "name": "reportIndex", "type": "uint256", "value": "0" }, { "name": "date", "type": "string", "value": "" }, { "name": "validated", "type": "string", "value": "" }, { "name": "factSize", "type": "uint256", "value": "0" }, { "name": "arcSize", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "newOwner", "type": "address" } ], "name": "transferOwnership", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "inputs": [ { "name": "tokenName", "type": "string", "index": 0, "typeShort": "string", "bits": "", "displayName": "token Name", "template": "elements_input_string", "value": "XBRL1.2" }, { "name": "tokenSymbol", "type": "string", "index": 1, "typeShort": "string", "bits": "", "displayName": "token Symbol", "template": "elements_input_string", "value": "XBRL" } ], "payable": false, "stateMutability": "nonpayable", "type": "constructor" }, { "anonymous": false, "inputs": [ { "indexed": false, "name": "target", "type": "address" }, { "indexed": false, "name": "frozen", "type": "bool" } ], "name": "FrozenAccount", "type": "event" } ];

	        myContract = web3.eth.contract(abiContract).at(contractAddress);
	        console.log("Test")
	        text = "a";
	        text = text.toUpperCase();
	        rs = [];
	        stop = false;
	        loop = function(index, callback) {
		        myContract.companies(index, function (error, result) {
		            if (!error) {
		            	console.log(index);
		                console.log(result);
		                if (result[1].toUpperCase().includes(text)) {
		                	rs.push({address : result[0], name : result[1]});
		                }
		                if (rs.length >= 5) {
		                	callback()
		                } else {
		                	loop(index+1, callback);
		                }
		            } else {
		                console.error(error);
		                callback();
		            }
		            
		        })
	        };
	        callback = function(){
	        	console.log("Finish")
	        	console.log(rs);
	        }
	        loop(0, callback);
	    })

	})
	//46168
	/*
	geth --datadir "F:\LocalNode\ETH"  --bootnodes "enode://651acaef26675933dadade4dea470e488a98a2fb46ddbfd8bd9ea041482a64f09fee1b70608641b6b3f79f717892aaa65b66a1881c1e0b2787c56f3c4f6be2b0@160.97.62.236:30301,enode://2586d68d2aa4ea654edd675cd3ab49e2b84c48708f7203b6575b58330b58c2007d0b9b1e6bff32e394ae567660bcaba3fd348f2375d54bae4e4082fb7a2bb861@160.97.62.236:30302" --mine --minerthreads=1 --rpc --rpccorsdomain "*" --rpcapi="db,eth,net,web3,personal,web3"
	./geth --datadir "/media/lvtan/New Volume1/LocalNode/ETH"  --bootnodes "enode://651acaef26675933dadade4dea470e488a98a2fb46ddbfd8bd9ea041482a64f09fee1b70608641b6b3f79f717892aaa65b66a1881c1e0b2787c56f3c4f6be2b0@160.97.62.236:30301,enode://2586d68d2aa4ea654edd675cd3ab49e2b84c48708f7203b6575b58330b58c2007d0b9b1e6bff32e394ae567660bcaba3fd348f2375d54bae4e4082fb7a2bb861@160.97.62.236:30302" --mine --minerthreads=1 --rpc --rpccorsdomain "*" --rpcapi="db,eth,net,web3,personal,web3"
	*/