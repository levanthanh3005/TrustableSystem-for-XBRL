<!doctype>
<html>

<head>

<link rel="stylesheet" type="text/css" href="resources/css/index.css">

<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/bignumber.min.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/web3.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/ethereumjs-tx.js"></script>

<script src="resources/validationHash.js"></script>
</head>
<body>

	<input type="hidden" id="userAddress" value="${ userAddress}" />
	<input type="hidden" id="userPass" value="${ userPass}"/>
	
	<a href="index">
		<div class="button">Come back home</div>
	</a>
	</br>

	<div id="newCompany" class="button">
		<input type="text" id="companyName" />
		<div id="btnSubmitCompany" class="button">Submit company name</div>
	</div>
	</br>

	<div id="showCompany" class="showForm"></div>
	</br>

	<form class="fileform">
		<input type="file" name="file" class="file" style="display: none" />
		<div class="button">
			<div id="btnChooseFile" class="button">Choose your file</div>
		</div>
	</form>
	<div id="showFile" class="showForm"></div>
	</br>
	<div id="btnValidate" class="button">Translating => I-DLV Validate</div>
	</br>
	<div id="showValidation" class="showForm"></div>
	</br>
	<div id="btnPushToBlockchain" class="button">
		Push to blockchain
		</br> Your reportId: </br> 
		<input type="text" id="reportId" placeholder="Your report Id" />
		<div class="button" id="btnPushReport">Push report</div>
	</div>
	</br>
	<div id="showPushedReport" class="showForm"></div>
	</br>
	<div id="showPushedResult" class="showForm"></div>
	</br>
	<div id="showPushedValidation" class="showForm"></div>
	</br>

	<a href="index" id="back">
		<div class="button">Come back home</div>
	</a>
	</br>
</body>
</html>

