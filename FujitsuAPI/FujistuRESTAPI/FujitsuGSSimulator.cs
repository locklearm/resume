using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FujitsuRESTAPI
{

    /*
     * This class will simulate request response pairs from GlobalStore 
     */
    public static class FujitsuGSSimulator
    {
        internal static LookupItemResponse GetLookupItemResponse(LookupItem li)
        {

            LookupItemResponse response = new LookupItemResponse();

            response.RegisterID = li.RegisterID;
            response.ResponseCode = 2;
            response.ResponseErrorMsg = "This is an error message.";
            response.ItemCode = li.ItemCode;
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

            return response;
        }
    }
}
