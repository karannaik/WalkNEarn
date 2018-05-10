package com.androiders.walknearn.model;

// Class specifies the details of the coupons
public class CouponItemDetails {

    private String OfferName;
    private int OfferId;
    private String OfferValue;

    public CouponItemDetails(String name,int id,String value){
        this.OfferName = name;
        this.OfferId = id;
        this.OfferValue = value;
    }


    public String getOfferName() {
        return OfferName;
    }

    public void setOfferName(String offerName) {
        OfferName = offerName;
    }

    public int getOfferId() {
        return OfferId;
    }

    public void setOfferId(int offerId) {
        OfferId = offerId;
    }

    public String getOfferValue() {
        return OfferValue;
    }

    public void setOfferValue(String offerValue) {
        OfferValue = offerValue;
    }
}
