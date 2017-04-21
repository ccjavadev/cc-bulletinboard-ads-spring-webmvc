sap.ui.define([
	"sap/demo/bulletinboard/controller/BaseController",
	"sap/demo/bulletinboard/model/formatter",
	"sap/m/MessageToast"
], function(BaseController, formatter, MessageToast) {
	"use strict";

	return BaseController.extend("sap.demo.bulletinboard.controller.AdDetails", {

		formatter: formatter,

		onInit : function () {
			var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			oRouter.getRoute("ad").attachPatternMatched(this._onAdMatched, this);
		},

		_onAdMatched : function (oEvent) {
			var iAdId = oEvent.getParameter("arguments").adId;

			var aAllAds = this.getModel().getData();
			var iAdIndex = this._getAdIndexInModel(iAdId, aAllAds);

			this.getView().bindElement({
				path: "/" + iAdIndex
			});
		},
	
		onDelete : function() {
			var iAdId = this.getView().getBindingContext().getProperty("id");

			$.ajax({
				method : "DELETE",
				url: this.getMainServiceURL() + "/" + iAdId,
				// Pseudo-authentication to qualify as "premium user" who is
				// allowed to create new advertisements:
				headers : { "User-Id" : this.getUserId() }
			})
			.done(this._onAdDeleted.bind(this))
			.fail( function(oJqXHR, sTextStatus, sErrorThrown) {
					MessageToast.show("Failed to delete the ad.");
					jQuery.sap.log.error("Failed to delete ad " + iAdId + ".", sErrorThrown);
	
				}
			);
		},

		/**
		 * Locate ad in array by its ID
		 * @param iAdId: ID of the ad to locate
		 * @param aAllAds: array of all ads
		 */
		_getAdIndexInModel : function(iAdId, aAllAds) {
			var iAdIndex;
			for (var i = 0; i < aAllAds.length; i++) {
			    if (aAllAds[i].id == iAdId) {
			    	iAdIndex = i;
			    	break;
			    } 
			}

			return iAdIndex;
		},

		_onAdDeleted : function(oResponseFromServer, sTextStatus, oJqXHR) {
			// Remove deleted ad from main model.
			var oAllAdsModel = this.getModel();
			var aAllAds = oAllAdsModel.getData();

			var iAdId = this.getView().getBindingContext().getProperty("id");
			var iAdIndex = this._getAdIndexInModel(iAdId, aAllAds);

			aAllAds.splice(iAdIndex, 1);
			oAllAdsModel.setData(aAllAds, true);

			var sAdTitle = this.getView().getBindingContext().getProperty("title");
			MessageToast.show("Ad '" + sAdTitle + "' has been deleted.");

			// Go back to list of ads.
			this.getRouter().navTo("main");
		}

	})
});