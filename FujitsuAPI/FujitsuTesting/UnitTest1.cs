using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using FujitsuRESTAPI;

namespace FujitsuTesting
{
    [TestClass]
    public class UnitTest1
    {
        [TestMethod]
        public void TestMethod1()
        {
            LookupItem li = new LookupItem();
            li.ItemCode = "alphaCode";
            li.RegisterID = 0;

            Assert.AreEqual("alphaCode", li.ItemCode);
            Assert.AreEqual(0, li.RegisterID);
        }
    }
}
