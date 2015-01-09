using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.Threading.Tasks;

namespace FujitsuRESTAPI
{

    /**
     * Here is the main interface for the API!!!  Yay sauce!!
     */
    [ServiceContract]
    public class FujitsuRestAPIService
    {

        [OperationContract]
        [WebInvoke(Method = "POST", RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json, UriTemplate = "")]
        APIResponse processRequest(APIRequest r)
        {


            //TODO This is where we do the security check
            
            


            //Now we figure out what type of request we are working with
            //and what the response should be
            Object responseObject = null;
            
            
            //If this is a price check we are dealing with
            if (r.RequestType.Equals(PriceCheck.FUNCTION_NAME))
            {
                PriceCheckRequest pcr = (PriceCheckRequest)r.RequestObject;

                PriceCheck pc = new PriceCheck();
                responseObject = pc.getResponse(pcr);
            }



            //Now we prepare and return the response object
            APIResponse apr = new APIResponse();
            apr.ResponseType = PriceCheck.FUNCTION_NAME;
            apr.ResponseObject = responseObject;

            return apr;

        }

    }

    /**
     * This class will manage the price check function of our API
     */
    public class PriceCheck
    {
        public const string FUNCTION_NAME = "price_check";

        public const string EXTRA_ITEM_DESCRIPTION = "extra_item_description";

        public PriceCheckResponse getResponse(PriceCheckRequest pcr)
        {

            //For debugging
            //Console.WriteLine("\nReceived a PriceCheckRequest with ItemID:" + pcr.ItemID + " SecurityToken:" + pcr.SecurityToken);

            //Set up the lookup item that we are going ot ssend to the Fujitsu GlobalStore
            LookupItem li = this.prepareLookupItem(pcr.ItemID);

            //Get that response from GlobalStore
            LookupItemResponse lir = FujitsuGSSimulator.GetLookupItemResponse(li);

            //Set up the response object that we want to send to the user
            PriceCheckResponse pcResponse = new PriceCheckResponse();
            pcResponse.ItemPrice = lir.UnitPrice;
            
            //Here is where we add the "extras"
            if (pcr.RequestExtras.ContainsKey(PriceCheck.EXTRA_ITEM_DESCRIPTION))
            {
                pcResponse.Extras.Add(PriceCheck.EXTRA_ITEM_DESCRIPTION, lir.Description);
            }

            //Send the response
            return pcResponse;

        }

        private LookupItem prepareLookupItem(string itemCode)
        {
            //Set up the lookup item that we are going ot ssend to the Fujitsu GlobalStore
            LookupItem li = new LookupItem();
            li.ItemCode = itemCode;
            li.RegisterID = 0;  //Since we don't want to reserve a register.

            return li;
        }

    }
}
