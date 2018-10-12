<!doctype>
<html>

<head>

<link rel="stylesheet" type="text/css" href="resources/css/index.css">
<link rel="stylesheet" type="text/css" href="resources/css/Validation.css">
<link rel="stylesheet" type="text/css" href="resources/css/ValidationMany.css">


<script	src="resources/jquery.min.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/bignumber.min.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/web3.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/ethereumjs-tx.js"></script>

<script src="resources/validationManyFiles.js"></script>
</head>
<body>

	<input type="hidden" id="userAddress" value="${ userAddress}" />
	<input type="hidden" id="userPass" value="${ userPass}" />

	<!-- The Modal -->
	<div id="showModal" class="modal">

		<!-- Modal content -->
		<div class="modal-content">
			<span class="close" id="modelClose">&times;</span>
			<div id="modelContent">Some text in the Modal..</div>
		</div>

	</div>

	<a href="index">
		<div class="button">Come back home</div>
	</a>
	</br>

	<form class="fileform" enctype="multipart/form-data">
		<input type="file" name="file" class="file" multiple="multiple"
			style="display: none" />
		<div class="button">
			<div id="btnChooseFile" class="button">Choose your file</div>
		</div>
	</form>
	<div id="showFile" class="showForm"></div>
	</br>

	<div class="showLsReports" id="lsXBRLFiles">
		<table style="width: 100%" class="table">
			<tr>
				<th>ReportId</th>
				<th>Cal-Validated</th>
				<th>MaxCalThreshold</th>
				<th>DebtRateAccept</th>
				<th>RateDifference</th>
				<th>Show More</th>
			</tr>
			<tbody class="lsReports">
				
			</tbody>
		</table>
	</div>
	</br>
	
	<div id="showReport" class="showForm"></div>
	</br>
	
	<div id="btnPushToBlockchain" class="button">
		Push to blockchain
		</br> Your reportId: </br> 
		<input type="text" id="reportId" placeholder="Your report Id" />
		<div class="button" id="btnPushReport">Push report</div>
		</br>
	</div>
	<div id="showPushedReport" class="showForm">
	</div>
	</br>
	<div id="showPushedResult" class="showForm"></div>
	</br>

	<a href="index" id="back">
		<div class="button">Come back home</div>
	</a>
	</br>
</body>
</html>

