using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace FujitsuRESTAPI
{

    [KnownType(typeof(PriceCheckRequest))]
    [DataContract]
    public class APIRequest
    {
        string securityToken;
        string requestType;
        Object requestObject;

        [DataMember]
        public string SecurityToken
        {
            get { return securityToken; }
            set { securityToken = value; }
        }

        [DataMember]
        public string RequestType
        {
            get { return requestType; }
            set { requestType = value; }
        }

        [DataMember]
        public Object RequestObject
        {
            get { return requestObject; }
            set { requestObject = value; }
        }

    }

    [KnownType(typeof(PriceCheckResponse))]
    [DataContract]
    public class APIResponse
    {
        string securityToken;
        string responseType;
        string errorMessage = null;
        Object responseObject;

        [DataMember]
        public string SecurityToken
        {
            get { return securityToken; }
            set { securityToken = value; }
        }

        [DataMember]
        public string ResponseType
        {
            get { return responseType; }
            set { responseType = value; }
        }

        [DataMember]
        public string ErrorMessage
        {
            get { return errorMessage; }
            set { errorMessage = value; }
        }

        [DataMember]
        public Object ResponseObject
        {
            get { return responseObject; }
            set { responseObject = value; }
        }
    }

    /*
     * This is the object that the request from the client will be deserialized to (by WCF)
     */
    [DataContract]
    public class PriceCheckRequest
    {
        string itemID;
        Dictionary<string, Object> requestExtras = new Dictionary<string, Object>();

        [DataMember]
        public string ItemID
        {
            get { return itemID; }
            set { itemID = value; }
        }

        [DataMember]
        public Dictionary<string, Object> RequestExtras
        {
            get { return requestExtras; }
            set { requestExtras = value; }
        }

    }

    /*
     * This is the object that will be used to build the response to a price check request.  It will
     * then be serialized and sent to the client.
     */
    [DataContract]
    public class PriceCheckResponse
    {
        //string itemDescription;
        decimal itemPrice;
        Dictionary<string, Object> extras = new Dictionary<string, Object>();

        //[DataMember]
        //public string ItemDescription
        //{
        //    get { return itemDescription; }
        //    set { itemDescription = value; }
        //}

        [DataMember]
        public decimal ItemPrice
        {
            get { return itemPrice; }
            set { itemPrice = value; }
        }

        [DataMember]
        public Dictionary<string, Object> Extras
        {
            get { return extras; }
            set { extras = value; }
        }

    }

    /*
     * This object was modeled using the object with the same name in the XSD file that was provided.
     */
    [DataContract]
    public class LookupItem
    {

        ushort registerID;
        string itemCode;  //Note, in the "real" version, this is a Identifier type

        [DataMember]
        public ushort RegisterID
        {
            get { return registerID; }
            set { registerID = value; }
        }

        [DataMember]
        public string ItemCode
        {
            get { return itemCode; }
            set { itemCode = value; }
        }

    }

    /*
     * This object was modeled using the object with the same name in the XSD file that was provided.
     */
    [DataContract]
    public class LookupItemResponse
    {

        ushort registerID;
        int responseCode;  //Note, in the "real" version, this is a ResponseCodes type
        string responseErrorMsg;
        string itemCode; //Note, in the "real" version, this is a Identifier type
        string description;
        decimal unitPrice;  //Note, in the "real" version, this is a nonNegativeCurrency type
        bool weightRequired;
        bool quantityRequired;
        bool priceRequired;
        bool notOnFile;
        int ageRestriction;
        bool notForSale;
        bool isManufacturerCoupon;
        bool isStoreCoupon;

        [DataMember]
        public ushort RegisterID
        {
            get { return registerID; }
            set { registerID = value; }
        }

        [DataMember]
        public int ResponseCode
        {
            get { return responseCode; }
            set { responseCode = value; }
        }

        [DataMember]
        public string ResponseErrorMsg
        {
            get { return responseErrorMsg; }
            set { responseErrorMsg = value; }
        }

        [DataMember]
        public string ItemCode
        {
            get { return itemCode; }
            set { itemCode = value; }
        }

        [DataMember]
        public string Description
        {
            get { return description; }
            set { description = value; }
        }

        [DataMember]
        public decimal UnitPrice
        {
            get { return unitPrice; }
            set { unitPrice = value; }
        }

        [DataMember]
        public bool WeightRequired
        {
            get { return weightRequired; }
            set { weightRequired = value; }
        }

        [DataMember]
        public bool QuantityRequired
        {
            get { return quantityRequired; }
            set { quantityRequired = value; }
        }

        [DataMember]
        public bool PriceRequired
        {
            get { return priceRequired; }
            set { priceRequired = value; }
        }

        [DataMember]
        public bool NotOnFile
        {
            get { return notOnFile; }
            set { notOnFile = value; }
        }

        [DataMember]
        public int AgeRestriction
        {
            get { return ageRestriction; }
            set { ageRestriction = value; }
        }

        [DataMember]
        public bool NotForSale
        {
            get { return notForSale; }
            set { notForSale = value; }
        }

        [DataMember]
        public bool IsManufacturerCoupon
        {
            get { return isManufacturerCoupon; }
            set { isManufacturerCoupon = value; }
        }

        [DataMember]
        public bool IsStoreCoupon
        {
            get { return isStoreCoupon; }
            set { isStoreCoupon = value; }
        }

    }
}
