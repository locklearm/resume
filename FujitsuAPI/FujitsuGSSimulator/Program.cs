using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.ServiceModel.Description;
using System.IO;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace FujitsuGSSimulator
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("====================== GS SIMULATOR *======================\n");

            string baseAddress = "http://" + Environment.MachineName + ":8000/FujitsuGSSimulator";
            ServiceHost host = new ServiceHost(typeof(GlobalStoreService), new Uri(baseAddress));
            ServiceEndpoint endpoint = host.AddServiceEndpoint(typeof(GlobalStoreService), new WebHttpBinding(), "");
            endpoint.Behaviors.Add(new WebHttpBehavior());
            host.Open();

            //Prompt to exit
            Console.WriteLine("Press any key to exit...");
            Console.ReadKey();

            host.Close();

        }
    }

    [ServiceContract]
    public class GlobalStoreService
    {
        [OperationContract]
        [WebInvoke(Method = "POST", RequestFormat = WebMessageFormat.Xml, ResponseFormat = WebMessageFormat.Xml, UriTemplate = "")]
        //LookupItemResponse LookupItem(LookupItem li)
        public LookupItemResponse LookupItem()
        {

            //Console.WriteLine("Received LookupItem, ItemCode:" + li.ItemCode + " RegisterID:" + li.RegisterID);

            LookupItemResponse response = new LookupItemResponse();

            //response.RegisterID = li.RegisterID;
            response.RegisterID = 45;
            response.ResponseCode = 2;
            response.ResponseErrorMsg = "This is an error message.";
            //response.ItemCode = li.ItemCode;
            response.ItemCode = "cg";
            response.Description = "This is the item description.";
            response.UnitPrice = 33.24m;
            response.WeightRequired = false;
            response.QuantityRequired = false;
            response.PriceRequired = false;
            response.NotOnFile = false;
            response.AgeRestriction = 0;
            response.NotForSale = false;
            response.IsManufacturerCoupon = false;
            response.IsStoreCoupon = true;

            Console.WriteLine("Sending LookupItemResponse (I think)");

            return response;
        }
    }


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
