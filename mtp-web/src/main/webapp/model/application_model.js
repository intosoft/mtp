function ApplicationModel(stompClient) {
	var self = this;

	self.username = ko.observable();
	self.mtp = ko.observable(new MTPModel(stompClient, self));
	self.notifications = ko.observableArray();

	self.connect = function() {
		stompClient.connect({}, function(frame) {
			console.log('Connected ' + frame);
			self.username(frame.headers['user-name']);

			stompClient.subscribe("/topic/currencies", function(message) {
				self.mtp().loadCurrenciesPairs(JSON.parse(message.body));
			});

			stompClient.subscribe("/user/queue/errors", function(message) {
				self.pushNotification("Error " + message.body);
			});
		}, function(error) {
			console.log("STOMP protocol error " + error);
		});
	}

	self.disableDebug = function(stompClient) {
		stompClient.debug = null;
	}

	self.pushNotification = function(text) {
		self.notifications.push({
			notification : text
		});
		if (self.notifications().length > 5) {
			self.notifications.shift();
		}
	}
}