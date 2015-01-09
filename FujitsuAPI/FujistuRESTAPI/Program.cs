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
using System.Xml;
using System.Runtime.Serialization.Json;

namespace FujitsuRESTAPI
{

    class Program
    {
        /**
         * This simply hosts the Service that is described in FujistuRestAPIService.
         */
        static void Main(string[] args)
        {
            Console.WriteLine("====================== REST API *======================\n");

            //TESTING, PLEASE REMOVE

            /*APIRequest apr = new APIRequest();
            apr.SecurityToken = "a1b2c34e5f6g7";
            apr.RequestType = PriceCheck.FUNCTION_NAME;
            PriceCheckRequest pcr = new PriceCheckRequest();
            pcr.ItemID = "8itemID";
            pcr.RequestExtras.Add(PriceCheck.EXTRA_ITEM_DESCRIPTION, null);
            apr.RequestObject = (Object)pcr;

            MemoryStream stream1 = new MemoryStream();
            DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(APIRequest));
            ser.WriteObject(stream1, apr);
            stream1.Position = 0;
            StreamReader sr = new StreamReader(stream1);
            Console.Write("JSON form of Person object: ");
            Console.WriteLine(sr.ReadToEnd());*/

            //END TESTING

            string baseAddress = "http://" + Environment.MachineName + ":8000/FujitsuRestAPI";
            ServiceHost host = new ServiceHost(typeof(FujitsuRestAPIService), new Uri(baseAddress));
            ServiceEndpoint endpoint = host.AddServiceEndpoint(typeof(FujitsuRestAPIService), new WebHttpBinding(), "");
            endpoint.Behaviors.Add(new WebHttpBehavior());
            host.Open();
            
            Console.WriteLine("Press any key to exit...");
            Console.ReadKey();

            host.Close();
        }
    }
}
