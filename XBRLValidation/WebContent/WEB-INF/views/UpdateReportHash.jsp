<!doctype>
<html>

<head>
<link rel="stylesheet" type="text/css" href="resources/css/index.css">

<link rel="stylesheet" type="text/css"
	href="resources/css/updateReport.css">

<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/bignumber.min.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/web3.js"></script>
<script type="text/javascript"
	src="resources/jsTransactions/dist/ethereumjs-tx.js"></script>

<script src="resources/updateReportHash.js"></script>

</head>
<body>
	<!-- The popup modal -->
	<div id="popupModel" class="modal">

		<!-- Modal content -->
		<div class="modal-content">
			<p class="messages">Waiting for updating</p>
		</div>

	</div>

	<input type="hidden" id="userAddress" value="${userAddress}" />
	<input type="hidden" id="userPass" value="${userPass}" />
	<a href="index">
		<div class="button">Come back home</div>
	</a>
	</br>
	<div class="button dropdown">
		<input type="text" id="companyName" placeholder="Or find company by name" />
		<div id="dropdownCompany" class="dropdown-content">
    		<div class="dropdownSubLink">Link 1</div>
  		</div>
	</div>
	</br>
	<div id="showCompany" class="showForm"></div>
	</br>

	<div class="bigFrame">
			<div class="title">Your reports</div>
			<div class="showLsReports">
				<table style="width: 100%" class="table">
					<tr>
						<th>ReportId</th>
						<th>Date</th>
						<th>Validated</th>
						<th>Hash</th>
					</tr>
					<tbody class="lsReports">
						<!-- 
						<tr>
							<td>Jill</td>
							<td>Smith</td>
							<td>50</td>
							<td>50</td>
						</tr>
						-->
					</tbody>
				</table>
			</div>
	</div>
	</br></br>
</body>
</html>

