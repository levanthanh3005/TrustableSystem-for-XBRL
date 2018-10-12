<!doctype>
<html>

<head>
<script type="text/javascript" src="resources/jsTransactions/dist/bignumber.min.js"></script>
<script type="text/javascript" src="resources/jsTransactions/dist/web3-light.js"></script>
<script type="text/javascript" src="resources/jsTransactions/dist/ethereumjs-tx.js"></script>
<!-- START SIGMA IMPORTS -->
<script src="resources/jsTransactions/sigmaSrc/sigma.core.js"></script>
<script src="resources/jsTransactions/sigmaSrc/conrad.js"></script>
<script src="resources/jsTransactions/sigmaSrc/utils/sigma.utils.js"></script>
<script src="resources/jsTransactions/sigmaSrc/utils/sigma.polyfills.js"></script>
<script src="resources/jsTransactions/sigmaSrc/sigma.settings.js"></script>
<script src="resources/jsTransactions/sigmaSrc/classes/sigma.classes.dispatcher.js"></script>
<script src="resources/jsTransactions/sigmaSrc/classes/sigma.classes.configurable.js"></script>
<script src="resources/jsTransactions/sigmaSrc/classes/sigma.classes.graph.js"></script>
<script src="resources/jsTransactions/sigmaSrc/classes/sigma.classes.camera.js"></script>
<script src="resources/jsTransactions/sigmaSrc/classes/sigma.classes.quad.js"></script>
<script src="resources/jsTransactions/sigmaSrc/classes/sigma.classes.edgequad.js"></script>
<script src="resources/jsTransactions/sigmaSrc/captors/sigma.captors.mouse.js"></script>
<script src="resources/jsTransactions/sigmaSrc/captors/sigma.captors.touch.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/sigma.renderers.canvas.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/sigma.renderers.webgl.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/sigma.renderers.svg.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/sigma.renderers.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/webgl/sigma.webgl.nodes.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/webgl/sigma.webgl.nodes.fast.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/webgl/sigma.webgl.edges.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/webgl/sigma.webgl.edges.fast.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/webgl/sigma.webgl.edges.arrow.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.labels.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.hovers.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.nodes.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.edges.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.edges.curve.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.edges.arrow.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.edges.curvedArrow.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.edgehovers.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.edgehovers.curve.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.edgehovers.arrow.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.edgehovers.curvedArrow.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/canvas/sigma.canvas.extremities.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/svg/sigma.svg.utils.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/svg/sigma.svg.nodes.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/svg/sigma.svg.edges.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/svg/sigma.svg.edges.curve.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/svg/sigma.svg.labels.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/renderers/svg/sigma.svg.hovers.def.js"></script>
<script src="resources/jsTransactions/sigmaSrc/middlewares/sigma.middlewares.rescale.js"></script>
<script src="resources/jsTransactions/sigmaSrc/middlewares/sigma.middlewares.copy.js"></script>
<script src="resources/jsTransactions/sigmaSrc/misc/sigma.misc.animation.js"></script>
<script src="resources/jsTransactions/sigmaSrc/misc/sigma.misc.bindEvents.js"></script>
<script src="resources/jsTransactions/sigmaSrc/misc/sigma.misc.bindDOMEvents.js"></script>
<script src="resources/jsTransactions/sigmaSrc/misc/sigma.misc.drawHovers.js"></script>
<script src="resources/jsTransactions/sigmaPlugins/sigma.plugins.relativeSize/sigma.plugins.relativeSize.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

<!-- END SIGMA IMPORTS -->
</head>
<body>
	<button id="btnStopSync">Start/Stop</button>
	<button id="btnBalance">Check balance</button>
	<button id="btnSendTx">Send Transaction</button>
	</br></br>
	<input id="numOfRow" type="number" placeholder="numOfRow" value = "10"/>
	<input id="probability" type="number" placeholder="probability" value = "20"/>
	</br></br>
	<button id="btnGenerateGraph">Generate Graph</button>
	<button id="btnFollowGraph">Follow Graph</button>
	<button id="btnCheckExecuteTx">Check executing tx</button>
	</br></br>
	<button id="btnCreateAccount">Create Account</button>
	<input id="numNewAcc" type="number"/>
	<button id="btnGatherEth">Gather All Eth</button>
	</br></br>
	Choose account:
	<input id="choosenNodeFrom" type="number" placeholder="From" value = "1"/>
	<input id="choosenNodeTo" type="number" placeholder="To" value = "121"/>
	</br></br>
	<button id="btnAllocateAmount">Allocate Amount</button>
	<input id="valueToBalance" type="number" placeholder="To" value = "10"/>
	</br></br>
	<button id="btnResetDLVFile">Reset DLV File</button>
	<button id="btnExecuteDLVFile">Execute DLV File</button>
	</br></br>
	<button id="btnTest">Test</button>
	</br></br>
	<textarea id="logText" style="width: 450px;height: 300;"></textarea>

	<div id="container">
		<style>
		#graph-container {
			    top: 0;
				bottom: 0;
				left: 470;
				right: 0;
				position: absolute;
				border: 1px solid;
		}
		</style>
		<div id="graph-container"></div>
	</div>
	<script src="resources/jsTransactions/transactions.js"></script>
</body>
</html>

