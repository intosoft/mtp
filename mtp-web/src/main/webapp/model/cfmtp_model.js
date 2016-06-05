function MTPModel(stompClient, applicationModel) {
	var self = this;

	self.availableCurrenciesPairs = ko.observableArray();
	self.chosenCurrenciesPairs = ko.observableArray();
	self.tradeMessagesSubscriptions = new Array();

	self.loadTradeMessages = function(tradeMessagesDTO) {
		var foundInChosenCurrenciesPairs = false;
		for (var i = 0; i < self.chosenCurrenciesPairs().length; i++) {
			var chosenCurrenciesPairs = self.chosenCurrenciesPairs()[i];

			if (tradeMessagesDTO.currencyFrom == chosenCurrenciesPairs[0]
					&& tradeMessagesDTO.currencyTo == chosenCurrenciesPairs[1]) {
				foundInChosenCurrenciesPairs = true;
			}
		}

		if (!foundInChosenCurrenciesPairs) {
			return;
		}

		var tradeMessageTransformed = new Array();

		var tradeMessages = tradeMessagesDTO.tradeMessages;

		for (var i = 0; i < tradeMessages.length; i++) {
			var tradeMessage = tradeMessages[i];
			tradeMessageTransformed[i] = [ tradeMessage.timePlaced, tradeMessage.rate ];
		}

		var chart = $('#container').highcharts();

		if (chart === undefined) {
			$('#container').highcharts(
					'StockChart',
					{
						legend : { enabled : true },
						plotOptions : { series : { animation : { duration : 2000, } } },

						rangeSelector : {
							buttons : [ { type : 'minute', count : 1, text : '1min' },
									{ type : 'hour', count : 1, text : '1h' },
									{ type : 'day', count : 1, text : '1d' },
									{ type : 'week', count : 1, text : '1w' },
									{ type : 'month', count : 1, text : '1m' },
									{ type : 'year', count : 1, text : '1y' }, { type : 'all', text : 'All' } ],
							selected : 1 },

						title : { text : 'AAPL Stock Price' },

					});
		}
		chart = $('#container').highcharts();

		var series = self.findChartSeries(chart, tradeMessagesDTO.currencyFrom, tradeMessagesDTO.currencyTo);

		if (tradeMessagesDTO.messageType == 'UPDATE') {
			if (series != null) {
				for (var i = 0; i < tradeMessageTransformed.length; i++) {
					var tradeMessageTransformed = tradeMessageTransformed[i];
					series.addPoint([ tradeMessageTransformed[0], tradeMessageTransformed[1] ], true, true);
				}
			}
		} else {
			applicationModel.pushNotification("Chart data loaded for market: " + tradeMessagesDTO.currencyFrom + " to "
					+ tradeMessagesDTO.currencyTo);
			if (series == null) {
				chart.addSeries({ id : tradeMessagesDTO.currencyFrom + " -> " + tradeMessagesDTO.currencyTo,
					name : tradeMessagesDTO.currencyFrom + " -> " + tradeMessagesDTO.currencyTo,
					data : tradeMessageTransformed, tooltip : { valueDecimals : 2 } });
			}
		}
	};

	self.loadCurrenciesPairs = function(currenciesPairs) {
		for (var i = 0; i < currenciesPairs.length; i++) {
			var currencyPair = currenciesPairs[i];

			var indexOf = self.arrayFirstIndexOf(self.availableCurrenciesPairs(), function(item) {
				return item[0] === currencyPair[0] && item[1] === currencyPair[1];
			});

			if (indexOf < 0) {
				self.availableCurrenciesPairs().push(currencyPair);
			}
		}

		self.availableCurrenciesPairs.sort();
	};

	self.findChartSeries = function(chart, currencyFrom, currencyTo) {
		for (var c = 0; c < chart.series.length; c++) {
			var chartSeries = chart.series[c];

			if (chartSeries.name == currencyFrom + " -> " + currencyTo) {
				return chartSeries;
			}
		}
		return null;
	}

	self.onCurrencyPairsChange = function(object, event) {
		for (var i = 0; i < self.availableCurrenciesPairs().length; i++) {
			var available = self.availableCurrenciesPairs()[i];

			var found = false;

			for (var j = 0; j < self.chosenCurrenciesPairs().length; j++) {
				var chosenCurrencyPair = self.chosenCurrenciesPairs()[j];

				if (chosenCurrencyPair[0] == available[0] && chosenCurrencyPair[1] == available[1]) {
					found = true;
					break;
				}
			}

			if (found) {
				var foundInSubscriptions = false;
				for (var z = 0; z < self.tradeMessagesSubscriptions.length; z++) {
					var subscription = self.tradeMessagesSubscriptions[z];

					if (available[0] == subscription[0] && available[1] == subscription[1]) {
						foundInSubscriptions = true;
						break;
					}
				}

				if (!foundInSubscriptions) {
					self.tradeMessagesSubscriptions[self.tradeMessagesSubscriptions.length] = [
							available[0],
							available[1],
							stompClient.subscribe("/topic/trade.messages." + available[0] + "." + available[1],
									function(message) {
										self.loadTradeMessages(JSON.parse(message.body));
									}) ];

					stompClient.send('/app/trade.messages', {}, JSON.stringify({ 'currencyFrom' : available[0],
						'currencyTo' : available[1] }));
				}
			} else {
				for (var t = 0; t < self.tradeMessagesSubscriptions.length; t++) {
					var subscription = self.tradeMessagesSubscriptions[t];

					if (available[0] == subscription[0] && available[1] == subscription[1]) {
						subscription[2].unsubscribe();
						self.tradeMessagesSubscriptions.splice(t, 1);
					}
				}

				var chart = $('#container').highcharts();

				if (chart !== undefined) {
					var series = self.findChartSeries(chart, available[0], available[1]);
					if (series != null) {
						series.remove();
					}
				}
			}
		}

	};

	self.arrayFirstIndexOf = function(array, predicate, predicateOwner) {
		for (var i = 0, j = array.length; i < j; i++) {
			if (predicate.call(predicateOwner, array[i])) {
				return i;
			}
		}
		return -1;
	}

};