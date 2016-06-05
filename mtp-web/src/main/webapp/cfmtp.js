$(document)
	.ready(
		function() {
		    var socket = new SockJS('/mtp-web/mtpws');
		    var stompClient = Stomp.over(socket);
		    
		    var appModel = new ApplicationModel(stompClient);
		    ko.applyBindings(appModel);

		    appModel.disableDebug(stompClient);
		    appModel.connect();
		    appModel
			    .pushNotification("Loading currencies.");
		});