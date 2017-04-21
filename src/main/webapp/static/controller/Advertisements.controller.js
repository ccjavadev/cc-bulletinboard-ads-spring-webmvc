sap.ui.define([
    "sap/demo/bulletinboard/controller/BaseController",
], function(BaseController) {
	"use strict";

	return BaseController.extend("sap.demo.bulletinboard.controller.Advertisements", {

//		onInit : function() {
//		},
	
		onCreateAd : function() {
			this.getRouter().navTo("createAd");
		},

		onSelectAd : function(oEvent) {
			var oAdTile = oEvent.getSource();
			var iAdId = oAdTile.getBindingContext().getObject().id;

			this.getRouter().navTo("ad", { adId : iAdId });
		},

	})
});
