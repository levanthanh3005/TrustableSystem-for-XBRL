$(document).ready(function() {
    $("#showFile").hide();
    $("#btnValidate").show();
    $("#showValidation").hide();
    $(".fileform").show();
    $(".showLsReports").hide();
    $("#showReport").hide();
    $("#btnPushToBlockchain").hide();
    $("#showPushedReport").hide();
    $("#showPushedResult").hide();

    $("#back").show();
    
    
	var reportId = undefined;
    fileToValidate = undefined;
    queryResult = undefined;
       
   
    $('body').on('click', '#btnChooseFile', function() {
//    	reportId = $("#reportId").val();
//    	if (!reportId) {
//    		alert("Enter your report Id, please");
//    	} else {
//    		var date = new Date()+"";
//    		tx = addReport(reportId,date);
//    		$("#showFile").show();
//    		$("#showFile").html("Publishing your report Id");
    		var fileForm = $(".file"); 
//    		waitForTx(tx, function(){
    			fileForm.trigger('click');
    			
//    			balance = parseFloat(web3.fromWei(web3.eth.getBalance(sender), "ether").toString());
//    			$("#showFile").html("Published your report Id, costed :"+(currentBalance - balance)+" ETH");
//    			currentBalance = balance;
//    		})
//    	}
    });
    $('body').on('change', '.file', function() {
        console.log("Change");
        
        $("#showFile").hide();
        $("#btnValidate").hide();
        $("#showValidation").hide();
        $("#btnPushToBlockchain").hide();
        
        console.log($('.file')[0].files);

        filedata = new FormData($(".fileform")[0]);

        formData = new FormData();
        for (file in $('.file')[0].files) {
        	formData.append("file", $('.file')[0].files[file]);
        }
        
        console.log(formData);
        console.log(filedata.files);
        uploadFile(formData);
//        $("#showFile").show();
//        $("#showFile").html("Ready for " + $('.file')[0].files[0].name);
    });
    
    
    function stringToJson(data) {
    	jsonStr= "["+data.replace(/'/g, '"')+"]";
        //				console.log(err);
        //				var obj = JSON.stringify(JSON.parse(err));
        //				console.log(obj);

        regeStr = '', // A EMPTY STRING TO EVENTUALLY HOLD THE FORMATTED STRINGIFIED OBJECT
            f = {
                brace: 0
            }; // AN OBJECT FOR TRACKING INCREMENTS/DECREMENTS,
        // IN PARTICULAR CURLY BRACES (OTHER PROPERTIES COULD BE ADDED)

        regeStr = jsonStr.replace(/({|}[,]*|[^{}:]+:[^{}:,]*[,{]*)/g, function(m, p1) {
            var rtnFn = function() {
                    return '<div style="text-indent: ' + (f['brace'] * 20) + 'px;">' + p1 + '</div>';
                },
                rtnStr = 0;
            if (p1.lastIndexOf('{') === (p1.length - 1)) {
                rtnStr = rtnFn();
                f['brace'] += 1;
            } else if (p1.indexOf('}') === 0) {
                f['brace'] -= 1;
                rtnStr = rtnFn();
            } else {
                rtnStr = rtnFn();
            }
            return rtnStr;
        });
        return regeStr;
    }
    
    function uploadFile(formData) {
        console.log("uploadFile");
        $.ajax({
            type: "POST",
            url: "uploadManyFile",
            data: formData,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000
        }).done(function(response) {
            console.log(response);
            fileToValidate = response.filepaths;
//            fileToValidate = response.filepaths;
//            console.log(fileToValidate);
            validateFile();
        }).fail(function(xhr, status, errorThrown) {
            console.log("Error: " + errorThrown);
            console.log("Status: " + status);
            console.dir(xhr);
        });
    }
    
    $('body').on('click', '.readReportBtn', function() {
    	console.log($(this).attr("class"));
    	companyId = $(this).attr("class").split(" ")[1];
    	console.log(companyId);
    	console.log(queryResult[companyId]);
    	
        htmlError = "";
        if (queryResult[companyId].error) {
            htmlError = "<div class='errorDiv'>" + stringToJson(queryResult[companyId].error) + "</div>";
        }
        htmlDebtAndInterestInfo = ""
        
        if (queryResult[companyId].debtAndInterestInfo) {
        	htmlDebtAndInterestInfo = stringToJson(queryResult[companyId].debtAndInterestInfo);
        }
    	
    	htmlText = "<div>" +
        "<div class='validatedDiv'>Calculation Validated: " + queryResult[companyId].validated + "</div>" +
        "<div>Maximum calculated threshold:" + queryResult[companyId].maxCalculatedThreshold + "</div>" +
        "<div id='calEval' >"+htmlError +"</div>" +
        
        "<div>Difference between rates of debt and interest:" + queryResult[companyId].debtDiff + "</div>" +
        "<div class='showMore'>" +
        	(queryResult[companyId].rateRs == false ? "Alert:In 2 contexts the difference between 2 rates is too high, more than 0.5" : "") +
        "</div>" +        
        "<div id='debtAndInterestInfoEval' >"+htmlDebtAndInterestInfo +"</div>" +
        "</div>";
	    $("#showReport").show();
	    $("#showReport").html($.parseHTML(htmlText));
    
    	
    });
    
    function validateFile() {
        console.log("validateFile");
        $("#btnValidate").show();
        $.ajax({
            type: "POST",
            url: "validateManyFiles",
            data: {
                filePath: fileToValidate
            }
        }).done(function(response) {
            console.log(response);
            $("#showFile").show();
            
            var count = 0;
            queryResult = response.queryResult;
            $(".lsReports").html("");
            
            for (var k in response.queryResult) {
            	console.log(k);
            	count++;    
            	obj = response.queryResult[k]
	            $(".lsReports").append(
	        			"<tr><td>"+k+"</td>" +
	        			"<td>"+obj["validated"]+"</td>" +
	        			"<td>"+obj["maxCalculatedThreshold"]+"</td>" +
	        			"<td>"+obj["rateRs"]+"</td>"+
	        			"<td>"+obj["debtDiff"]+"</td>"+
	        			"<td><div class='readReportBtn "+k+"'>Read Report</div></td></tr>");
            }
            htmlText = "<div>" +
            		"<div>Time To Load : "+response.timeToLoad+"</div>" +
            		"<div>Time To Evaluate : "+response.timeToValidate+"</div>" +
            		"<div>Number of files : "+count+"</div>" +
            		"</div>";
            	
            $("#showFile").html($.parseHTML(htmlText));
            $(".showLsReports").show();
            $("#btnPushToBlockchain").show();
            
        }).fail(function(xhr, status, errorThrown) {
            console.log("Error: " + errorThrown);
            console.log("Status: " + status);
            console.dir(xhr);
        });
    }
    
})