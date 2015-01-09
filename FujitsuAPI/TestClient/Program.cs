using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace FujistuTestClient
{
    class Program
    {
        static void Main(string[] args)
        {
            //Get the address we are sending the POST Request to
            string baseAddress = "http://localhost:8000/FujitsuRestAPI/";
            //Set up the contents of the message
            //string outMessage = "{\"SecurityToken\":\"a1b2c3d4e5f6g7\",\"ItemID\":\"75\"}";
            string outMessage = "{\"RequestObject\":{\"__type\":\"PriceCheckRequest:#FujitsuRESTAPI\",\"ItemID\":\"8itemID\",\"RequestExtras\":[{\"Key\":\"extra_item_description\",\"Value\":null}]},\"RequestType\":\"price_check\",\"SecurityToken\":\"a1b2c34e5f6g7\"}";
            byte[] outMesssageBytes = Encoding.UTF8.GetBytes(outMessage);

            //Set up to send the request
            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(baseAddress);
            request.Method = "POST";
            request.ContentType = "text/json";
            Stream requestStream = request.GetRequestStream();

            //Send the message
            requestStream.Write(outMesssageBytes, 0, outMessage.Length);
            requestStream.Close();

            //Log it
            Console.WriteLine("====================== TEST CLIENT *======================\n");
            Console.WriteLine("Request Sent To: " + baseAddress + "\n");
            Console.WriteLine("********************** begin request body **********************");
            Console.WriteLine(outMessage);
            Console.WriteLine("**********************  end request body  **********************\n");

            //Now we collect the response
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream responseStream = response.GetResponseStream();
            byte[] buffer = new byte[10000];
            String responseContents = "";
            int bytesRead;
            do
            {
                bytesRead = 0;
                bytesRead = responseStream.Read(buffer, 0, buffer.Length);
                responseContents += Encoding.UTF8.GetString(buffer, 0, bytesRead);

            }
            while (bytesRead > 0);
            responseStream.Close();

            //Log it
            Console.WriteLine("********************** begin response contents **********************");
            Console.WriteLine(responseContents);
            Console.WriteLine("**********************  end response contents  **********************\n");

            //Prompt to exit
            Console.WriteLine("Press any key to exit...");
            Console.ReadKey();
        }
    }
}
